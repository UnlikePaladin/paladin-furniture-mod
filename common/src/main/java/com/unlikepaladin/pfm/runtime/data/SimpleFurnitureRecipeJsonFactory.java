package com.unlikepaladin.pfm.runtime.data;


import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import com.unlikepaladin.pfm.registry.RecipeTypes;
import net.minecraft.advancement.*;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.data.server.recipe.CraftingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SimpleFurnitureRecipeJsonFactory implements CraftingRecipeJsonBuilder {
    private final Item output;
    private final int outputCount;
    private final List<Ingredient> inputs = Lists.newArrayList();
    private final Map<String, AdvancementCriterion<?>> criteria = new LinkedHashMap<>();
    private boolean showNotification = true;
    @Nullable
    private NbtElement nbtElement;
    @Nullable
    private String group;

    public SimpleFurnitureRecipeJsonFactory(ItemConvertible output, int outputCount) {
        this.output = output.asItem();
        this.outputCount = outputCount;
    }

    public SimpleFurnitureRecipeJsonFactory(ItemConvertible output, int outputCount, @Nullable NbtElement nbtElement) {
        this.output = output.asItem();
        this.outputCount = outputCount;
        this.nbtElement = nbtElement;
    }

    public static SimpleFurnitureRecipeJsonFactory create(ItemConvertible output, int count, NbtElement nbtElement) {
        return new SimpleFurnitureRecipeJsonFactory(output, count, nbtElement);
    }

    public static SimpleFurnitureRecipeJsonFactory create(ItemConvertible output, NbtElement nbtElement) {
        return new SimpleFurnitureRecipeJsonFactory(output, 1, nbtElement);
    }

    public static SimpleFurnitureRecipeJsonFactory create(ItemConvertible output) {
        return new SimpleFurnitureRecipeJsonFactory(output, 1);
    }
    public static SimpleFurnitureRecipeJsonFactory create(ItemConvertible output, int count) {
        return new SimpleFurnitureRecipeJsonFactory(output, count);
    }
    public SimpleFurnitureRecipeJsonFactory input(TagKey<Item> tag) {
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

    public FurnitureRecipeJsonFactory showNotification(boolean showNotification) {
        this.showNotification = showNotification;
        return this;
    }

    @Override
    public Item getOutputItem() {
        return this.output;
    }

    @Override
    public void offerTo(RecipeExporter exporter, Identifier recipeId) {
        Advancement.Builder advancement$builder = exporter.getAdvancementBuilder().criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId)).rewards(AdvancementRewards.Builder.recipe(recipeId)).criteriaMerger(AdvancementRequirements.CriterionMerger.OR);
        this.criteria.forEach(advancement$builder::criterion);
        exporter.accept(new SimpleFurnitureRecipeJsonProvider(recipeId, this.output, this.nbtElement, this.outputCount, this.group == null ? "" : this.group, this.inputs, advancement$builder.build(recipeId.withPrefixedPath("recipes/furniture/")), this.showNotification));
    }

    private void validate(Identifier recipeId) {
        if (this.criteria.isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + recipeId);
        }
    }

    public static class SimpleFurnitureRecipeJsonProvider
            implements RecipeJsonProvider {
        private final Identifier recipeId;
        private final Item output;
        private final int count;
        private final String group;
        private final List<Ingredient> inputs;
        private final AdvancementEntry advancement;
        private final boolean showNotification;
        @Nullable
        private final NbtElement nbtElement;

        public SimpleFurnitureRecipeJsonProvider(Identifier recipeId, Item output, @Nullable NbtElement nbtElement, int outputCount, String group, List<Ingredient> inputs, AdvancementEntry entry, boolean showNotification) {
            this.recipeId = recipeId;
            this.output = output;
            this.count = outputCount;
            this.group = group;
            this.inputs = inputs;
            this.nbtElement = nbtElement;
            this.advancement = entry;
            this.showNotification = showNotification;
        }

        @Override
        public void serialize(JsonObject json) {
            if (!this.group.isEmpty()) {
                json.addProperty("group", this.group);
            }
            JsonArray jsonArray = new JsonArray();
            for (Ingredient ingredient : this.inputs) {
                jsonArray.add(ingredient.toJson(true));
            }
            json.add("ingredients", jsonArray);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("item", Registries.ITEM.getId(this.output).toString());
            if (this.count > 1) {
                jsonObject.addProperty("count", this.count);
            }
            json.addProperty("show_notification", this.showNotification);
            if (nbtElement != null) {
                JsonElement object = NbtOps.INSTANCE.convertTo(JsonOps.INSTANCE, this.nbtElement);
                jsonObject.add("tag", object);
            }
            json.add("result", jsonObject);
        }

        @Nullable
        @Override
        public AdvancementEntry advancement() {
            return this.advancement;
        }

        @Override
        public RecipeSerializer<?> serializer() {
            return RecipeTypes.SIMPLE_FURNITURE_SERIALIZER;
        }

        @Override
        public Identifier id() {
            return this.recipeId;
        }
    }
}


