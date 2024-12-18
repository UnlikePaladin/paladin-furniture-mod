package com.unlikepaladin.pfm.compat.patchouli;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

public class FreezingRecipeProcessor implements IComponentProcessor {
    @Override
    public void setup(World level, IVariableProvider variables) {

    }

    @Override
    public IVariable process(World level, String key) {
        return null;
    }
   /* private Recipe<?> recipe;
    @Override
    public void setup(World level, IVariableProvider variables) {
        String recipeId = variables.get("recipe", level.getRegistryManager()).asString();
        RecipeManager manager = level.getRecipeManager();
        recipe = manager.get(Identifier.of(recipeId)).map(RecipeEntry::value).orElse(null);
    }

    @Override
    public @NotNull IVariable process(World level, String key) {
        if (recipe != null) {
            switch (key) {
                case "ingredient":
                    Ingredient ingredient = recipe.getIngredients().get(0);
                    ItemStack[] stacks = ingredient.getMatchingStacks();
                    ItemStack stack = stacks.length == 0 ? ItemStack.EMPTY : stacks[0];

                    return IVariable.from(stack, level.getRegistryManager());
                case "output":
                    ItemStack result = recipe.getResult(level.getRegistryManager());
                    return IVariable.from(result, level.getRegistryManager());
                case "icon":
                    ItemStack icon = recipe.createIcon();
                    return IVariable.from(icon, level.getRegistryManager());
                case "text":
                    ItemStack out = recipe.getResult(level.getRegistryManager());
                    return IVariable.wrap(out.getCount() + "x$(br)" + out.getName());
                case "icount":
                    return IVariable.wrap(recipe.getResult(level.getRegistryManager()).getCount());
                case "iname":
                    return IVariable.wrap(recipe.getResult(level.getRegistryManager()).getName().getString());
            }
        }
        return IVariable.empty();
    }

    @Override
    public void refresh(Screen parent, int left, int top) {
        IComponentProcessor.super.refresh(parent, left, top);
    }

    @Override
    public boolean allowRender(String group) {
        return recipe != null;
    }*/
}
