package com.unlikepaladin.pfm.recipes;

import com.google.gson.*;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.unlikepaladin.pfm.registry.PaladinFurnitureModBlocksItems;
import com.unlikepaladin.pfm.registry.RecipeTypes;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.*;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.RecipeBookCategories;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class FurnitureRecipe implements Recipe<FurnitureRecipe.FurnitureRecipeInput>, Comparable<FurnitureRecipe> {
    final String group;
    protected final ItemStack output;
    final DefaultedList<Ingredient> input;

    public FurnitureRecipe(String group, ItemStack output, DefaultedList<Ingredient> input) {
        this.group = group;
        this.output = output;
        this.input = input;
    }

    public Map<Item, Integer> getItemCounts() {
        Map<Item, Integer> ingredientCounts = new HashMap<>();
        for (Ingredient ingredient : this.getIngredients()) {
            for (RegistryEntry<Item> itemRegistryEntry : ingredient.getMatchingItems().toList()) {
                if (ingredientCounts.containsKey(itemRegistryEntry.value())) {
                    ingredientCounts.put(itemRegistryEntry.value(), ingredientCounts.get(itemRegistryEntry.value())+1);
                } else {
                    ingredientCounts.put(itemRegistryEntry.value(), 1);
                }
            }
        }
        return ingredientCounts;
    }

    @Override
    public boolean matches(FurnitureRecipeInput playerInventory, World world) {
        Map<Item, Integer> ingredientCounts = getItemCounts();

        for (Map.Entry<Item, Integer> entry : ingredientCounts.entrySet()) {
            Item item = entry.getKey();
            Integer count = entry.getValue();

            int itemCount = 0;
            ItemStack defaultStack = item.getDefaultStack();
            for (ItemStack stack1 : playerInventory.playerInventory().main) {
                if (defaultStack.isOf(stack1.getItem())) {
                    itemCount += stack1.getCount();
                }
            }
            if (itemCount < count)
                return false;
        }
        return true;
    }

    public List<Ingredient> getIngredients() {
        return input;
    }

    public static int getSlotWithStackIgnoreNBT(PlayerInventory inventory, Item item) {
        for(int i = 0; i < inventory.main.size(); ++i) {
            if (!inventory.main.get(i).isEmpty() && item == inventory.main.get(i).getItem()) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public ItemStack craft(FurnitureRecipeInput inventory, RegistryWrapper.WrapperLookup lookup) {
        if (!this.output.getComponents().isEmpty() && output.contains(DataComponentTypes.BLOCK_ENTITY_DATA) && output.get(DataComponentTypes.BLOCK_ENTITY_DATA).isEmpty()) {
            ItemStack stack = this.output.copy();
            stack.remove(DataComponentTypes.BLOCK_ENTITY_DATA);
            return stack;
        }
        return this.output.copy();
    }

    @Override
    public String getGroup() {
        return this.group;
    }



    @Override
    public RecipeSerializer<? extends Recipe<FurnitureRecipeInput>> getSerializer() {
        return RecipeTypes.FURNITURE_SERIALIZER;
    }

    @Override
    public RecipeType<? extends Recipe<FurnitureRecipeInput>> getType() {
        return RecipeTypes.FURNITURE_RECIPE;
    }

    @Override
    public IngredientPlacement getIngredientPlacement() {
        return IngredientPlacement.forMultipleSlots(input.stream().map(Optional::of).toList());
    }

    @Override
    public RecipeBookCategory getRecipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }

    @Override
    public int compareTo(@NotNull FurnitureRecipe furnitureRecipe) {
        return this.output.toString().compareTo(furnitureRecipe.output.toString());
    }

    public ItemStack result() {
        return output;
    }

    public boolean enabled(World world) {
        if (!this.output.isItemEnabled(world.getEnabledFeatures()))
            return false;
        for (Ingredient ingredient : this.getIngredients()) {
            for (RegistryEntry<Item> item : ingredient.getMatchingItems().toList()) {
                if (!item.value().isEnabled(world.getEnabledFeatures()))
                    return false;
            }
        }
        return true;
    }

    @Override
    public boolean isIgnoredInRecipeBook() {
        return true;
    }

    public static class Serializer
            implements RecipeSerializer<FurnitureRecipe> {

        public static FurnitureRecipe read(RegistryByteBuf packetByteBuf) {
            String string = packetByteBuf.readString();
            DefaultedList<Ingredient> defaultedList = packetByteBuf.readCollection(DefaultedList::ofSize, buf1 -> Ingredient.PACKET_CODEC.decode((RegistryByteBuf) buf1));
            ItemStack itemStack = ItemStack.PACKET_CODEC.decode(packetByteBuf);
            return new FurnitureRecipe(string, itemStack, defaultedList);
        }

        public static final PacketCodec<RegistryByteBuf, FurnitureRecipe> PACKET_CODEC = PacketCodec.ofStatic(
                FurnitureRecipe.Serializer::write, FurnitureRecipe.Serializer::read
        );
        @Override
        public MapCodec<FurnitureRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, FurnitureRecipe> packetCodec() {
            return PACKET_CODEC;
        }

        public static void write(RegistryByteBuf packetByteBuf, FurnitureRecipe furnitureRecipe) {
            packetByteBuf.writeString(furnitureRecipe.group);
            packetByteBuf.writeCollection(furnitureRecipe.input, (buff, ingredient) -> Ingredient.PACKET_CODEC.encode((RegistryByteBuf) buff, ingredient));
            ItemStack.PACKET_CODEC.encode(packetByteBuf, furnitureRecipe.output);
        }

        public static final MapCodec<FurnitureRecipe> CODEC = RecordCodecBuilder.mapCodec((instance) ->
                instance.group(
                        Codec.STRING.optionalFieldOf("group", "").forGetter(FurnitureRecipe::getGroup),
                        ItemStack.VALIDATED_CODEC.fieldOf("result").forGetter(recipe -> recipe.output),
                        Ingredient.CODEC.listOf().fieldOf("ingredients").flatXmap((ingredients) -> {
            DefaultedList<Ingredient> defaultedList = DefaultedList.of();
            defaultedList.addAll(ingredients);
            if (defaultedList.isEmpty()) {
                return DataResult.error(() -> "No ingredients for furniture recipe");
            } else {
                return DataResult.success(defaultedList);
            }
        }, DataResult::success).forGetter(furnitureRecipe -> furnitureRecipe.input))
                        .apply(instance, FurnitureRecipe::new));
    }
    /*
    private static final Codec<Item> CRAFTING_RESULT_ITEM = Registries.ITEM.getCodec().validate((item) -> {
        return item == Items.AIR ? DataResult.error(() -> {
            return "Crafting result must not be minecraft:air";
        }) : DataResult.success(item);
    });
    public static final Codec<ItemStack> FURNITURE_RESULT = RecordCodecBuilder.create((instance) ->
            instance.group(
            CRAFTING_RESULT_ITEM.fieldOf("item").forGetter(ItemStack::getItem),
            Codecs.POSITIVE_INT.optionalFieldOf("count", 1).forGetter(ItemStack::getCount),
            OUTPUT_TAGS.optionalFieldOf( "tag", new NbtCompound())
                    .xmap(NbtComponent::of, NbtComponent::copyNbt)
                    .xmap(nbtComponent -> ComponentMap.builder().add(DataComponentTypes.BLOCK_ENTITY_DATA, nbtComponent).build(), components -> components.getOrDefault(DataComponentTypes.BLOCK_ENTITY_DATA, NbtComponent.DEFAULT))
                    .forGetter(ItemStack::getComponents)).apply(instance, (item, integer, components) -> {
                        ItemStack stack = new ItemStack(item, integer);
                        stack.applyComponentsFrom(components);
                        return stack;
    }));*/

    public static record FurnitureRecipeInput(PlayerInventory playerInventory) implements RecipeInput {

        @Override
        public ItemStack getStackInSlot(int slot) {
            return playerInventory.getStack(slot);
        }

        @Override
        public int size() {
            return playerInventory.size();
        }

        @Override
        public boolean isEmpty() {
            return playerInventory.isEmpty();
        }
    }
}
