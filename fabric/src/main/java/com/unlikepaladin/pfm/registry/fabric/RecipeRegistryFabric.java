package com.unlikepaladin.pfm.registry.fabric;

import com.unlikepaladin.pfm.recipes.DynamicFurnitureRecipe;
import com.unlikepaladin.pfm.recipes.FreezingRecipe;
import com.unlikepaladin.pfm.recipes.FurnitureRecipe;
import com.unlikepaladin.pfm.recipes.SimpleFurnitureRecipe;
import com.unlikepaladin.pfm.registry.RecipeTypes;
import net.minecraft.recipe.CookingRecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.registry.Registry;

public class RecipeRegistryFabric {
    public static void registerRecipes() {
        RecipeTypes.FREEZING_RECIPE_SERIALIZER = Registry.register(Registry.RECIPE_SERIALIZER, RecipeTypes.FREEZING_ID, new CookingRecipeSerializer<>(FreezingRecipe::new, 200));
        RecipeTypes.FREEZING_RECIPE = Registry.register(Registry.RECIPE_TYPE, RecipeTypes.FREEZING_ID,  new RecipeType<FreezingRecipe>() {
            @Override
            public String toString() {return RecipeTypes.FREEZING_ID.getPath();}
        });

        RecipeTypes.SIMPLE_FURNITURE_SERIALIZER =Registry.register(Registry.RECIPE_SERIALIZER, RecipeTypes.SIMPLE_FURNITURE_ID, new SimpleFurnitureRecipe.Serializer());
        RecipeTypes.DYNAMIC_FURNITURE_SERIALIZER = Registry.register(Registry.RECIPE_SERIALIZER, RecipeTypes.DYNAMIC_FURNITURE_ID, new DynamicFurnitureRecipe.Serializer());

        RecipeTypes.FURNITURE_RECIPE = Registry.register(Registry.RECIPE_TYPE, RecipeTypes.FURNITURE_ID,  new RecipeType<FurnitureRecipe>() {
            @Override
            public String toString() {return RecipeTypes.FURNITURE_ID.getPath();}
        });
    }
}
