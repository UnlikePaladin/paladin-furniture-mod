package com.unlikepaladin.pfm.recipes.neoforge;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.inventory.Inventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.input.RecipeInput;
import org.jetbrains.annotations.Nullable;

public class FurnitureSerializerNeoForge <J extends Recipe<I>, T extends RecipeSerializer<J>, I extends RecipeInput> implements RecipeSerializer<J> {
    public FurnitureSerializerNeoForge(T recipeSerializer) {
        this.serializer = recipeSerializer;
    }

    T serializer;
    @Override
    public MapCodec<J> codec() {
        return serializer.codec();
    }

    @Override
    public PacketCodec<RegistryByteBuf, J> packetCodec() {
        return serializer.packetCodec();
    }
}

