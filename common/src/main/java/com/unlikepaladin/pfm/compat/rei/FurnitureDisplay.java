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
import com.unlikepaladin.pfm.recipes.SimpleFurnitureRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;

import java.util.*;

public class FurnitureDisplay implements Display {
    protected FurnitureRecipe recipe;
    public static final CategoryIdentifier<FurnitureDisplay> IDENTIFIER = CategoryIdentifier.of(new Identifier(PaladinFurnitureMod.MOD_ID, "furniture"));
    public List<EntryIngredient> input;
    public List<EntryIngredient> output;
    public FurnitureDisplay(FurnitureRecipe recipe) {
        this.recipe = recipe;
        output = Collections.singletonList(EntryIngredients.of(recipe.getOutput()));
    }

    @Override
    public List<EntryIngredient> getInputEntries() {
        List<Ingredient> ingredients = recipe.getIngredients();
        HashMap<Item, Integer> containedItems = new HashMap<>();
        for (Ingredient ingredient : ingredients) {
            for (ItemStack stack : ingredient.getMatchingStacks()) {
                if (!containedItems.containsKey(stack.getItem())) {
                    containedItems.put(stack.getItem(), 1);
                } else {
                    containedItems.put(stack.getItem(), containedItems.get(stack.getItem()) + 1);
                }
            }
        }
        List<Ingredient> finalList = new ArrayList<>();
        for (Map.Entry<Item, Integer> entry: containedItems.entrySet()) {
            finalList.add(Ingredient.ofStacks(new ItemStack(entry.getKey(), entry.getValue())));
        }
        finalList.sort(Comparator.comparing(o -> o.getMatchingStacks()[0].getItem().toString()));
        return EntryIngredients.ofIngredients(finalList);
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return output;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return IDENTIFIER;
    }

}