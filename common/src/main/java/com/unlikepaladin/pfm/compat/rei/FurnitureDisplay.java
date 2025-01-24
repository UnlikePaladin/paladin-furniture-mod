package com.unlikepaladin.pfm.compat.rei;

/*
 * This file is licensed under the MIT License, part of Roughly Enough Items.
 * Copyright (c) 2018, 2019, 2020, 2021, 2022 shedaniel
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.recipes.FurnitureRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;

import java.util.*;

public class FurnitureDisplay implements Display {
    protected FurnitureRecipe recipe;
    public static final CategoryIdentifier<FurnitureDisplay> IDENTIFIER = CategoryIdentifier.of(new Identifier(PaladinFurnitureMod.MOD_ID, "furniture"));
    private int itemsPerInnerRecipe;
    public FurnitureDisplay(FurnitureRecipe recipe) {
        this.recipe = recipe;
        this.itemsPerInnerRecipe = recipe.getIngredients().size(MinecraftClient.getInstance().world.getRegistryManager());
    }

    private final List<EntryIngredient> inputs = new ArrayList<>();

    @Override
    public List<EntryIngredient> getInputEntries() {
        if (!inputs.isEmpty()) return inputs;

        List<Ingredient> inputEntries = new ArrayList<>();
        this.itemsPerInnerRecipe = recipe.getMaxInnerRecipeSize();
        for (FurnitureRecipe.CraftableFurnitureRecipe innerRecipe: recipe.getInnerRecipes()) {
            List<Ingredient> ingredients = innerRecipe.getIngredients();
            HashMap<Item, Integer> containedItems = new HashMap<>();
            for (Ingredient ingredient : ingredients) {
                for (ItemStack stack : ingredient.getMatchingStacks()) {
                    if (!containedItems.containsKey(stack.getItem())) {
                        containedItems.put(stack.getItem(), stack.getCount());
                    } else {
                        containedItems.put(stack.getItem(), containedItems.get(stack.getItem()) + stack.getCount());
                    }
                }
            }
            List<Ingredient> finalList = new ArrayList<>();
            for (Map.Entry<Item, Integer> entry: containedItems.entrySet()) {
                finalList.add(Ingredient.ofStacks(new ItemStack(entry.getKey(), entry.getValue())));
            }
            finalList.sort(Comparator.comparing(o -> o.getMatchingStacks()[0].getItem().toString()));
            if (finalList.size() != itemsPerInnerRecipe) {
                while (finalList.size() != itemsPerInnerRecipe) {
                    finalList.add(Ingredient.EMPTY);
                }
            }
            inputEntries.addAll(finalList);
        }
        for (Ingredient ingredient : inputEntries) {
            if (!ingredient.isEmpty())
                inputs.add(EntryIngredients.ofIngredient(ingredient));
            else
                inputs.add(EntryIngredient.empty());
        }
        return inputs;
    }

    public int itemsPerInnerRecipe() {
        return itemsPerInnerRecipe;
    }

    private final List<EntryIngredient> outputs = new ArrayList<>();
    @Override
    public List<EntryIngredient> getOutputEntries() {
        if (outputs.isEmpty())
            outputs.addAll(recipe.getInnerRecipes().stream().map(FurnitureRecipe.CraftableFurnitureRecipe::getOutput).map(EntryIngredients::of).toList());
        return outputs;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public Display provideInternalDisplay() {
        return Display.super.provideInternalDisplay();
    }
}