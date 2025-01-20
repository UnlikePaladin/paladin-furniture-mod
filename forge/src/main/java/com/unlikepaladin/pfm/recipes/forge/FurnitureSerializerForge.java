package com.unlikepaladin.pfm.recipes.forge;

import com.google.gson.JsonObject;
import com.unlikepaladin.pfm.recipes.SimpleFurnitureRecipe;
import net.minecraft.inventory.Inventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraftforge.registries.ForgeRegistryEntry;
import org.jetbrains.annotations.Nullable;

public class FurnitureSerializerForge <J extends Recipe<I>, T extends RecipeSerializer<J>, I extends Inventory> extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<J> {
    public FurnitureSerializerForge(T recipeSerializer) {
        this.serializer = recipeSerializer;
    }

    T serializer;
    @Override
    public J read(Identifier id, JsonObject json) {
        return serializer.read(id, json);
    }

    @Nullable
    @Override
    public J read(Identifier id, PacketByteBuf buf) {
        return serializer.read(id, buf);
    }

    @Override
    public void write(PacketByteBuf buf, J recipe) {
        serializer.write(buf, recipe);
    }
}

