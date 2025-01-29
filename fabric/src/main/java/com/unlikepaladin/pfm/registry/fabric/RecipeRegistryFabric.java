package com.unlikepaladin.pfm.registry.fabric;

import com.unlikepaladin.pfm.recipes.DynamicFurnitureRecipe;
import com.unlikepaladin.pfm.recipes.FreezingRecipe;
import com.unlikepaladin.pfm.recipes.FurnitureRecipe;
import com.unlikepaladin.pfm.recipes.SimpleFurnitureRecipe;
import com.unlikepaladin.pfm.registry.RecipeTypes;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SingleStackRecipe;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class RecipeRegistryFabric {
    public static void registerRecipes() {
        RecipeTypes.FREEZING_RECIPE_SERIALIZER = Registry.register(Registries.RECIPE_SERIALIZER, RecipeTypes.FREEZING_ID, new AbstractCookingRecipe.Serializer<>(FreezingRecipe::new, 200));
        RecipeTypes.FREEZING_RECIPE = Registry.register(Registries.RECIPE_TYPE, RecipeTypes.FREEZING_ID,  new RecipeType<FreezingRecipe>() {
            @Override
            public String toString() {return RecipeTypes.FREEZING_ID.getPath();}
        });

        RecipeTypes.SIMPLE_FURNITURE_SERIALIZER = Registry.register(Registries.RECIPE_SERIALIZER, RecipeTypes.SIMPLE_FURNITURE_ID, new SimpleFurnitureRecipe.Serializer());
        RecipeTypes.DYNAMIC_FURNITURE_SERIALIZER = Registry.register(Registries.RECIPE_SERIALIZER, RecipeTypes.DYNAMIC_FURNITURE_ID, new DynamicFurnitureRecipe.Serializer());

        RecipeTypes.FURNITURE_RECIPE = Registry.register(Registries.RECIPE_TYPE, RecipeTypes.FURNITURE_ID,  new RecipeType<FurnitureRecipe>() {
            @Override
            public String toString() {return RecipeTypes.FURNITURE_ID.getPath();}
        });
    }
}
