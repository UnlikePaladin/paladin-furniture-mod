package com.unlikepaladin.pfm.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.unlikepaladin.pfm.registry.RecipeTypes;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.RecipeBookCategories;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.*;

public class SimpleFurnitureRecipe implements FurnitureRecipe, FurnitureRecipe.CraftableFurnitureRecipe {
    final String group;
    final ItemStack output;
    final DefaultedList<Ingredient> input;

    public SimpleFurnitureRecipe(String group, ItemStack output, List<Ingredient> input) {
        this.group = group;
        this.output = output;
        this.input = DefaultedList.ofSize(input.size());
        this.input.addAll(input);
    }

    @Override
    public DefaultedList<Ingredient> getIngredients() {
        return this.input;
    }

    @Override
    public boolean matches(FurnitureRecipe.FurnitureRecipeInput input, World world) {
        Map<Item, Integer> ingredientCounts = getItemCounts();
        for (Map.Entry<Item, Integer> entry : ingredientCounts.entrySet()) {
            Item item = entry.getKey();
            Integer count = entry.getValue();

            int itemCount = 0;
            ItemStack defaultStack = item.getDefaultStack();
            for (ItemStack stack1 : input.playerInventory().main) {
                if (defaultStack.isOf(stack1.getItem())) {
                    itemCount += stack1.getCount();
                }
            }
            if (itemCount < count)
                return false;
        }
        return true;
    }

    @Override
    public FurnitureRecipe parent() {
        return this;
    }

    @Override
    public ItemStack getRecipeOuput() {
        return output;
    }

    @Override
    public ItemStack craft(FurnitureRecipe.FurnitureRecipeInput playerInventory, RegistryWrapper.WrapperLookup registryManager) {
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
    public ItemStack getResult(RegistryWrapper.WrapperLookup registryManager) {
        return this.output;
    }

    @Override
    public void write(RegistryByteBuf buf) {
        Serializer.write(buf, this);
    }

    @Override
    public RecipeSerializer<? extends Recipe<FurnitureRecipeInput>> getSerializer() {
        return RecipeTypes.SIMPLE_FURNITURE_SERIALIZER;
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
    public String outputClass() {
        return this.output.getItem().getClass().getSimpleName();
    }

    @Override
    public boolean isIgnoredInRecipeBook() {
        return true;
    }

    @Override
    public List<CraftableFurnitureRecipe> getInnerRecipes() {
        return Collections.singletonList(this);
    }

    public static class Serializer
            implements RecipeSerializer<SimpleFurnitureRecipe> {

        private static final MapCodec<SimpleFurnitureRecipe> CODEC = RecordCodecBuilder.mapCodec((instance) ->
                instance.group(
                                Codec.STRING.optionalFieldOf("group", "").forGetter(SimpleFurnitureRecipe::getGroup),
                                ItemStack.VALIDATED_CODEC.fieldOf("result").forGetter(recipe -> recipe.output),
                                Ingredient.CODEC.listOf().fieldOf("ingredients").flatXmap((ingredients) -> {
                                    DefaultedList<Ingredient> defaultedList = DefaultedList.of();
                                    defaultedList.addAll(ingredients);
                                    if (defaultedList.isEmpty()) {
                                        return DataResult.error(() -> "No ingredients for furniture recipe");
                                    } else {
                                        return DataResult.success(defaultedList);
                                    }
                                }, DataResult::success).forGetter(SimpleFurnitureRecipe::getIngredients))
                        .apply(instance, SimpleFurnitureRecipe::new));

        @Override
        public MapCodec<SimpleFurnitureRecipe> codec() {
            return CODEC;
        }

        public static final PacketCodec<RegistryByteBuf, SimpleFurnitureRecipe> PACKET_CODEC = PacketCodec.ofStatic(
                SimpleFurnitureRecipe.Serializer::write, SimpleFurnitureRecipe.Serializer::read
        );
        @Override
        public PacketCodec<RegistryByteBuf, SimpleFurnitureRecipe> packetCodec() {
            return PACKET_CODEC;
        }

        public static SimpleFurnitureRecipe read(RegistryByteBuf packetByteBuf) {
            String string = packetByteBuf.readString();
            DefaultedList<Ingredient> defaultedList = packetByteBuf.readCollection(DefaultedList::ofSize, buf1 -> Ingredient.PACKET_CODEC.decode((RegistryByteBuf) buf1));
            ItemStack itemStack = ItemStack.PACKET_CODEC.decode(packetByteBuf);
            return new SimpleFurnitureRecipe(string, itemStack, defaultedList);
        }

        public static void write(RegistryByteBuf packetByteBuf, SimpleFurnitureRecipe simpleFurnitureRecipe) {
            packetByteBuf.writeString(simpleFurnitureRecipe.group);
            packetByteBuf.writeCollection(simpleFurnitureRecipe.input, (buff, ingredient) -> Ingredient.PACKET_CODEC.encode((RegistryByteBuf) buff, ingredient));
            ItemStack.PACKET_CODEC.encode(packetByteBuf, simpleFurnitureRecipe.output);
        }
    }
}
