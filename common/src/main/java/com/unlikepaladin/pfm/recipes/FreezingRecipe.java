package com.unlikepaladin.pfm.recipes;

import com.unlikepaladin.pfm.registry.PaladinFurnitureModBlocksItems;
import com.unlikepaladin.pfm.registry.RecipeTypes;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.book.CookingRecipeCategory;
import net.minecraft.recipe.book.RecipeBookCategories;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.util.Identifier;

public class FreezingRecipe extends AbstractCookingRecipe {

    public FreezingRecipe(String group, CookingRecipeCategory category, Ingredient input, ItemStack output, float experience, int cookTime) {
        super(group, category, input, output, experience, cookTime);
    }

    @Override
    public RecipeSerializer<? extends AbstractCookingRecipe> getSerializer() {
        return RecipeTypes.FREEZING_RECIPE_SERIALIZER;
    }

    @Override
    public RecipeType<? extends AbstractCookingRecipe> getType() {
        return RecipeTypes.FREEZING_RECIPE;
    }

    @Override
    public RecipeBookCategory getRecipeBookCategory() {
        return RecipeBookCategories.FURNACE_MISC;
    }

    @Override
    protected Item getCookerItem() {
        return PaladinFurnitureModBlocksItems.WHITE_FREEZER.asItem();
    }

    @Override
    public boolean isIgnoredInRecipeBook() {
        return true;
    }
}
