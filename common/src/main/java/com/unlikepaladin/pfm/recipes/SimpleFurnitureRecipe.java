package com.unlikepaladin.pfm.recipes;

import com.google.gson.*;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.registry.PaladinFurnitureModBlocksItems;
import com.unlikepaladin.pfm.registry.RecipeTypes;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.*;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.registry.Registry;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.World;

import java.util.*;

public class SimpleFurnitureRecipe implements FurnitureRecipe, FurnitureRecipe.CraftableFurnitureRecipe {
    final String group;
    final ItemStack output;
    final DefaultedList<Ingredient> input;

    public SimpleFurnitureRecipe(String group, ItemStack output, List<Ingredient> input) {
        this.group = group;
        this.output = output;
        this.input = DefaultedList.copyOf(Ingredient.EMPTY, input.toArray(Ingredient[]::new));
    }

    @Override
    public DefaultedList<Ingredient> getIngredients() {
        return this.input;
    }

    @Override
    public boolean matches(FurnitureRecipe.FurnitureRecipeInput input, World world) {
        List<Ingredient> ingredients = this.getIngredients();
        BitSet hasIngredients = new BitSet(ingredients.size());
        PlayerInventory playerInventory = input.playerInventory();
        for (int i = 0; i < ingredients.size(); i++) {
            Ingredient ingredient = ingredients.get(i);
            for (ItemStack stack : ingredient.getMatchingStacks()) {
                if (playerInventory.count(stack.getItem()) >= stack.getCount()) {
                    hasIngredients.set(i);
                    break;
                }
            }
        }
        return hasIngredients.cardinality() == ingredients.size();
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
    public boolean fits(int width, int height) {
        return true;
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
    public ItemStack createIcon() {
        return PaladinFurnitureModBlocksItems.WORKING_TABLE.asItem().getDefaultStack();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeTypes.SIMPLE_FURNITURE_SERIALIZER;
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
                                Ingredient.DISALLOW_EMPTY_CODEC.listOf().fieldOf("ingredients").flatXmap((ingredients) -> {
                                    Ingredient[] ingredients2 = ingredients.stream().filter((ingredient) -> {
                                        return !ingredient.isEmpty();
                                    }).toArray(Ingredient[]::new);
                                    if (ingredients2.length == 0) {
                                        return DataResult.error(() -> "No ingredients for furniture recipe");
                                    } else {
                                        return DataResult.success(DefaultedList.copyOf(Ingredient.EMPTY, ingredients2));
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
            int i = packetByteBuf.readVarInt();
            DefaultedList<Ingredient> defaultedList = DefaultedList.ofSize(i, Ingredient.EMPTY);
            for (int j = 0; j < defaultedList.size(); ++j) {
                defaultedList.set(j, Ingredient.PACKET_CODEC.decode(packetByteBuf));
            }
            ItemStack itemStack = ItemStack.PACKET_CODEC.decode(packetByteBuf);
            return new SimpleFurnitureRecipe(string, itemStack, defaultedList);
        }

        public static void write(RegistryByteBuf packetByteBuf, SimpleFurnitureRecipe simpleFurnitureRecipe) {
            packetByteBuf.writeString(simpleFurnitureRecipe.group);
            packetByteBuf.writeVarInt(simpleFurnitureRecipe.input.size());
            for (Ingredient ingredient : simpleFurnitureRecipe.input) {
                Ingredient.PACKET_CODEC.encode(packetByteBuf, ingredient);
            }
            ItemStack.PACKET_CODEC.encode(packetByteBuf, simpleFurnitureRecipe.output);
        }
    }
}
