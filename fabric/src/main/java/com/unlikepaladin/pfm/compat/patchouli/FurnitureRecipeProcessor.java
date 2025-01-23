package com.unlikepaladin.pfm.compat.patchouli;

import com.unlikepaladin.pfm.recipes.FurnitureRecipe;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

import java.util.ArrayList;
import java.util.List;

public class FurnitureRecipeProcessor implements IComponentProcessor {
    private FurnitureRecipe recipe;
    private Identifier variant;
    private boolean isBase;
    @Override
    public void setup(IVariableProvider variables) {
        String recipeId = variables.get("recipe").asString();
        RecipeManager manager = MinecraftClient.getInstance().world.getRecipeManager();
        Recipe<?> recipe = manager.get(new Identifier(recipeId)).orElse(null);
        this.recipe = recipe instanceof FurnitureRecipe ? (FurnitureRecipe) recipe : null;
        this.variant = variables.has("variant") ? Identifier.tryParse(variables.get("variant").asString()) : null;

    }

    @Override
    public IVariable process(@NotNull String key) {
        if (recipe != null) {
            List<? extends FurnitureRecipe.CraftableFurnitureRecipe> innerRecipeList;
            if (variant != null) {
                innerRecipeList = recipe.getInnerRecipesForVariant(variant);
            } else {
                innerRecipeList = recipe.getInnerRecipes();
            }
            if (key.startsWith("item")) {
                int index = Integer.parseInt(key.substring(4)) - 1;
                ItemStack[] ingredientsArr = new ItemStack[innerRecipeList.size()];
                for (int i = 0; i < innerRecipeList.size(); i++) {
                    FurnitureRecipe.CraftableFurnitureRecipe innerRecipe = innerRecipeList.get(i);
                    if (index >= innerRecipe.getIngredients().size()) {
                        ingredientsArr[i] = ItemStack.EMPTY;
                        continue;
                    }
                    Ingredient ingredient = innerRecipe.getIngredients().get(index);
                    ItemStack[] stacks = ingredient.getMatchingStacks();
                    ingredientsArr[i] = stacks.length == 0 ? ItemStack.EMPTY : stacks[0];
                }
                return IVariable.from(ingredientsArr);
            } else if (key.equals("resultitem")) {
                ItemStack[] resultsArr = new ItemStack[innerRecipeList.size()];
                for (int i = 0; i < innerRecipeList.size(); i++) {
                    FurnitureRecipe.CraftableFurnitureRecipe innerRecipe = innerRecipeList.get(i);
                    resultsArr[i] = innerRecipe.getOutput();
                }
                return IVariable.from(resultsArr);
            } else if (key.equals("icon")) {
                ItemStack icon = recipe.createIcon();
                return IVariable.from(icon);
            } else if (key.equals("text")) {
                return IVariable.wrap(recipe.getOutputCount() + "x$(br)" + recipe.getName());
            } else if (key.equals("icount")) {
                return IVariable.wrap(recipe.getOutputCount());
            } else if (key.equals("iname")) {
                return IVariable.wrap(recipe.getName());
            }
        }
        return null;
    }

    @Override
    public void refresh(Screen parent, int left, int top) {
        IComponentProcessor.super.refresh(parent, left, top);
    }
}