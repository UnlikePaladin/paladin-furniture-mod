package com.unlikepaladin.pfm.recipes.forge;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import net.minecraft.inventory.Inventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class FurnitureSerializerForge <J extends Recipe<I>, T extends RecipeSerializer<J>, I extends Inventory> implements RecipeSerializer<J> {
    public FurnitureSerializerForge(T recipeSerializer) {
        this.serializer = recipeSerializer;
    }

    T serializer;
    @Override
    public Codec<J> codec() {
        return serializer.codec();
    }

    @Override
    public @Nullable J read(PacketByteBuf buf) {
        return serializer.read(buf);
    }

    @Override
    public void write(PacketByteBuf buf, J recipe) {
        serializer.write(buf, recipe);
    }
}

