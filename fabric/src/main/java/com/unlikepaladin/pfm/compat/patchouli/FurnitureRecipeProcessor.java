package com.unlikepaladin.pfm.compat.patchouli;

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
 /*   private Recipe<?> recipe;
    @Override
    public void setup(World level, IVariableProvider variables) {
        String recipeId = variables.get("recipe", level.getRegistryManager()).asString();
        RecipeManager manager = level.getRecipeManager();
        recipe = manager.get(Identifier.of(recipeId)).map(RecipeEntry::value).orElse(null);
    }

    @Override
    public @NotNull IVariable process(World level, String key) {
        if (recipe != null) {
            if (key.startsWith("item")) {
                int index = Integer.parseInt(key.substring(4)) - 1;
                if (index >= recipe.getIngredientPlacement().getIngredients().size()) {
                    return IVariable.from(ItemStack.EMPTY, level.getRegistryManager());
                }
                Ingredient ingredient = recipe.getIngredientPlacement().getIngredients().get(index);
                List<RegistryEntry<Item>> stacks = ingredient.getMatchingItems();
                ItemStack stack = stacks.isEmpty() ? ItemStack.EMPTY : stacks.get(0).value().getDefaultStack();
                return IVariable.from(stack, level.getRegistryManager());
            } else if (key.equals("resultitem")) {
                ItemStack result = recipe..getResult(level.getRegistryManager());
                return IVariable.from(result, level.getRegistryManager());
            } else if (key.equals("icon")) {
                ItemStack icon = recipe.createIcon();
                return IVariable.from(icon, level.getRegistryManager());
            } else if (key.equals("text")) {
                ItemStack out = recipe.getResult(level.getRegistryManager());
                return IVariable.wrap(out.getCount() + "x$(br)" + out.getName());
            } else if (key.equals("icount")) {
                return IVariable.wrap(recipe.getResult(level.getRegistryManager()).getCount());
            } else if (key.equals("iname")) {
                return IVariable.wrap(recipe.getResult(level.getRegistryManager()).getName().getString());
            }
        }
        return IVariable.empty();
    }

    @Override
    public void refresh(Screen parent, int left, int top) {
        IComponentProcessor.super.refresh(parent, left, top);
    }*/
}