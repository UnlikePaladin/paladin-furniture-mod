package com.unlikepaladin.pfm.runtime.data;


import com.unlikepaladin.pfm.recipes.SimpleFurnitureRecipe;
import net.minecraft.advancement.*;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.data.recipe.CraftingRecipeJsonBuilder;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

public class SimpleFurnitureRecipeJsonFactory implements CraftingRecipeJsonBuilder {
    private final ItemStack stack;
    private final DefaultedList<Ingredient> inputs = DefaultedList.of();
    private final Map<String, AdvancementCriterion<?>> criteria = new LinkedHashMap<>();
    private boolean showNotification = true;
    @Nullable
    private String group;

    public SimpleFurnitureRecipeJsonFactory(ItemConvertible output, int outputCount) {
        this.stack = new ItemStack(output, outputCount);
    }

    public SimpleFurnitureRecipeJsonFactory(ItemConvertible output, int outputCount, @NotNull NbtCompound nbtElement) {
        this.stack = new ItemStack(output, outputCount);
        this.stack.set(DataComponentTypes.BLOCK_ENTITY_DATA, NbtComponent.of(nbtElement));
    }

    public SimpleFurnitureRecipeJsonFactory(ItemStack stack) {
        this.stack = stack;
    }

    public static SimpleFurnitureRecipeJsonFactory create(ItemConvertible output, int count, NbtCompound nbtElement) {
        return new SimpleFurnitureRecipeJsonFactory(output, count, nbtElement);
    }

    public static SimpleFurnitureRecipeJsonFactory create(ItemConvertible output, NbtCompound nbtElement) {
        return new SimpleFurnitureRecipeJsonFactory(output, 1, nbtElement);
    }

    public static SimpleFurnitureRecipeJsonFactory create(ItemConvertible output) {
        return new SimpleFurnitureRecipeJsonFactory(output, 1);
    }

    public static SimpleFurnitureRecipeJsonFactory create(ItemConvertible output, int count) {
        return new SimpleFurnitureRecipeJsonFactory(output, count);
    }

    public static SimpleFurnitureRecipeJsonFactory create(ItemStack stack) {
        return new SimpleFurnitureRecipeJsonFactory(stack);
    }

    public SimpleFurnitureRecipeJsonFactory input(RegistryEntryList<Item> tag) {
        return this.input(Ingredient.fromTag(tag));
    }

    public SimpleFurnitureRecipeJsonFactory input(ItemConvertible itemProvider) {
        return this.input(itemProvider, 1);
    }

    public SimpleFurnitureRecipeJsonFactory input(ItemConvertible itemProvider, int size) {
        for (int i = 0; i < size; ++i) {
            this.input(Ingredient.ofItems(itemProvider));
        }
        return this;
    }

    public SimpleFurnitureRecipeJsonFactory input(Ingredient ingredient) {
        return this.input(ingredient, 1);
    }

    public SimpleFurnitureRecipeJsonFactory input(Ingredient ingredient, int size) {
        for (int i = 0; i < size; ++i) {
            this.inputs.add(ingredient);
        }
        return this;
    }

    @Override
    public SimpleFurnitureRecipeJsonFactory criterion(String name, AdvancementCriterion<?> criterionConditions) {
        this.criteria.put(name, criterionConditions);
        return this;
    }

    @Override
    public SimpleFurnitureRecipeJsonFactory group(@Nullable String string) {
        this.group = string;
        return this;
    }

    public SimpleFurnitureRecipeJsonFactory showNotification(boolean showNotification) {
        this.showNotification = showNotification;
        return this;
    }

    @Override
    public Item getOutputItem() {
        return this.stack.getItem();
    }

    @Override
    public void offerTo(RecipeExporter exporter, RegistryKey<Recipe<?>> recipeKey) {
        Advancement.Builder advancement$builder = exporter.getAdvancementBuilder().criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeKey)).rewards(AdvancementRewards.Builder.recipe(recipeKey)).criteriaMerger(AdvancementRequirements.CriterionMerger.OR);
        this.criteria.forEach(advancement$builder::criterion);
        exporter.accept(recipeKey, new SimpleFurnitureRecipe(this.group == null || this.group.isBlank() ? "" : this.group, stack, this.inputs), advancement$builder.build(recipeKey.getValue().withPrefixedPath("recipes/furniture/")));
    }

    public void offerTo(RecipeExporter exporter, Identifier recipeId) {
        Recipe<?> recipe =  new SimpleFurnitureRecipe(this.group == null || this.group.isBlank() ? " " : this.group, stack, this.inputs);
        RegistryKey<Recipe<?>> recipeKey = RegistryKey.of(RegistryKeys.RECIPE, recipeId);
        Advancement.Builder advancement$builder = exporter.getAdvancementBuilder().criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeKey)).rewards(AdvancementRewards.Builder.recipe(recipeKey)).criteriaMerger(AdvancementRequirements.CriterionMerger.OR);
        this.criteria.forEach(advancement$builder::criterion);
        exporter.accept(recipeKey, recipe, advancement$builder.build(recipeKey.getValue().withPrefixedPath("recipes/furniture/")));
    }

        private void validate(Identifier recipeId) {
        if (this.criteria.isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + recipeId);
        }
    }
}


