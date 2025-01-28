package com.unlikepaladin.pfm.compat.patchouli;

import com.unlikepaladin.pfm.recipes.FurnitureRecipe;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

import java.util.List;

public class FurnitureRecipeProcessor implements IComponentProcessor {
    @Override
    public void setup(World level, IVariableProvider variables) {

    }

    @Override
    public IVariable process(World level, String key) {
        return null;
    }
 /*   private FurnitureRecipe recipe;
    private Identifier variant;
    private boolean isBase;
    @Override
    public void setup(World level, IVariableProvider variables) {
        String recipeId = variables.get("recipe", level.getRegistryManager()).asString();
        RecipeManager manager = level.getRecipeManager();
        Recipe<?> recipe = manager.get(Identifier.of(recipeId)).map(RecipeEntry::value).orElse(null);
        this.recipe = recipe instanceof FurnitureRecipe ? (FurnitureRecipe) recipe : null;
        this.variant = variables.has("variant") ? Identifier.tryParse(variables.get("variant", level.getRegistryManager()).asString()) : null;
    }

    @Override
    public @NotNull IVariable process(World level, String key) {
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
                    if (index >= innerRecipe.getIngredientPlacement().getIngredients().size()) {
                        ingredientsArr[i] = ItemStack.EMPTY;
                        continue;
                    }
                    Ingredient ingredient = innerRecipe.getIngredientPlacement().getIngredients().get(index);
                    List<RegistryEntry<Item>> stacks = ingredient.getMatchingItems();
                    ingredientsArr[i] = stacks.isEmpty() ? ItemStack.EMPTY : stacks.get(0).value().getDefaultStack();
                }
                return IVariable.from(ingredientsArr, level.getRegistryManager());
            } else if (key.equals("resultitem")) {
                ItemStack[] resultsArr = new ItemStack[innerRecipeList.size()];
                for (int i = 0; i < innerRecipeList.size(); i++) {
                    FurnitureRecipe.CraftableFurnitureRecipe innerRecipe = innerRecipeList.get(i);
                    resultsArr[i] = innerRecipe..getResult(level.getRegistryManager());
                }
                return IVariable.from(resultsArr, level.getRegistryManager());
            } else if (key.equals("icon")) {
                ItemStack icon = recipe.createIcon();
                return IVariable.from(icon, level.getRegistryManager());
            } else if (key.equals("text")) {
                return IVariable.wrap(recipe.getOutputCount(level.getRegistryManager()) + "x$(br)" + recipe.getName(level.getRegistryManager()));
            } else if (key.equals("icount")) {
                return IVariable.wrap(recipe.getOutputCount(level.getRegistryManager()));
            } else if (key.equals("iname")) {
                return IVariable.wrap(recipe.getName(level.getRegistryManager()));
            }
        }
        return IVariable.empty();
    }

    @Override
    public void refresh(Screen parent, int left, int top) {
        IComponentProcessor.super.refresh(parent, left, top);
    }*/
}