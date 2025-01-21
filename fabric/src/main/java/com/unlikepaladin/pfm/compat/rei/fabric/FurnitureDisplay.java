package com.unlikepaladin.pfm.compat.rei.fabric;

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


import com.google.common.collect.Lists;
import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.recipes.FurnitureRecipe;
import com.unlikepaladin.pfm.runtime.data.PFMRecipeProvider;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.TransferRecipeDisplay;
import me.shedaniel.rei.server.ContainerInfo;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class FurnitureDisplay implements TransferRecipeDisplay {
    protected FurnitureRecipe recipe;
    public static final Identifier IDENTIFIER = new Identifier(PaladinFurnitureMod.MOD_ID, "furniture");
    private int itemsPerInnerRecipe;
    public FurnitureDisplay(FurnitureRecipe recipe) {
        this.recipe = recipe;
        this.itemsPerInnerRecipe = recipe.getIngredients().size();
    }

    private final List<List<EntryStack>> inputs = new ArrayList<>();

    @Override
    public @NotNull List<List<EntryStack>> getInputEntries() {
        if (!inputs.isEmpty()) return inputs;

        List<Ingredient> inputEntries = new ArrayList<>();
        this.itemsPerInnerRecipe = recipe.getMaxInnerRecipeSize();
        for (FurnitureRecipe.CraftableFurnitureRecipe innerRecipe: recipe.getInnerRecipes()) {
            List<Ingredient> ingredients = innerRecipe.getIngredients();
            HashMap<Item, Integer> containedItems = new HashMap<>();
            for (Ingredient ingredient : ingredients) {
                for (ItemStack stack : PFMRecipeProvider.pfm$getMatchingStacks(ingredient)) {
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
            finalList.sort(Comparator.comparing(o -> PFMRecipeProvider.pfm$getMatchingStacks(o)[0].getItem().toString()));

            if (finalList.size() != itemsPerInnerRecipe) {
                while (finalList.size() != itemsPerInnerRecipe) {
                    finalList.add(Ingredient.EMPTY);
                }
            }
            inputEntries.addAll(finalList);
        }
        for (Ingredient ingredient : inputEntries) {
            if (!ingredient.isEmpty())
                inputs.add(EntryStack.ofIngredient(ingredient));
            else
                inputs.add(Collections.singletonList(EntryStack.empty()));
        }
        return inputs;
    }

    public int itemsPerInnerRecipe() {
        return itemsPerInnerRecipe;
    }

    private final List<List<EntryStack>> outputs = new ArrayList<>();
    @Override
    public @NotNull List<List<EntryStack>> getResultingEntries() {
        if (outputs.isEmpty())
            outputs.addAll(recipe.getInnerRecipes().stream().map(FurnitureRecipe.CraftableFurnitureRecipe::getOutput).map(itemStack -> EntryStack.ofItemStacks(Collections.singletonList(itemStack))).collect(Collectors.toList()));
        return outputs;
    }


    @Override
    public @NotNull Identifier getRecipeCategory() {
        return IDENTIFIER;
    }

    @Override
    public int getWidth() {
        return 3;
    }

    @Override
    public int getHeight() {
        return 3;
    }

    @Override
    public List<List<EntryStack>> getOrganisedInputEntries(ContainerInfo<ScreenHandler> containerInfo, ScreenHandler container) {
        List<List<EntryStack>> list = Lists.newArrayListWithCapacity(containerInfo.getCraftingWidth(container) * containerInfo.getCraftingHeight(container));
        for (int i = 0; i < containerInfo.getCraftingWidth(container) * containerInfo.getCraftingHeight(container); i++) {
            list.add(Collections.emptyList());
        }
        List<List<EntryStack>> inputs = getInputEntries();
        for (int i = 0; i < inputs.size(); i++) {
            List<EntryStack> stacks = inputs.get(i);
            list.set(FurnitureCategory.getSlotWithSize(this, i, containerInfo.getCraftingWidth(container)), stacks);
        }
        return list;
    }
}