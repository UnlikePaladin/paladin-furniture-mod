package com.unlikepaladin.pfm.runtime.data;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.registry.RecipeTypes;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.CriterionMerger;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.block.Block;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;

public class DynamicFurnitureRecipeJsonFactory {
    private final Advancement.Builder builder = Advancement.Builder.create();
    private List<Ingredient> vanillaIngredients = Lists.newArrayList();

    private final String outputClass;
    private final int outputCount;
    private String group;
    private Map<String, Integer> variantChildren = new HashMap<>();
    private final List<Identifier> supportedVariants;
    @Nullable
    private final NbtElement nbtElement;

    public DynamicFurnitureRecipeJsonFactory(Class<? extends Block> output, int outputCount, List<Identifier> supportedVariants, Map<String, Integer> variantChildren) {
        this.outputClass = output.getSimpleName();
        this.outputCount = outputCount;
        this.nbtElement = null;
        this.supportedVariants = supportedVariants;
        this.variantChildren = variantChildren;
    }

    public DynamicFurnitureRecipeJsonFactory(Class<? extends Block> output, int outputCount, List<Identifier> supportedVariants, Map<String, Integer> variantChildren, @Nullable NbtElement nbtElement) {
        this.outputClass = output.getSimpleName();
        this.outputCount = outputCount;
        this.supportedVariants = supportedVariants;
        this.variantChildren = variantChildren;
        this.nbtElement = nbtElement;
    }

    public DynamicFurnitureRecipeJsonFactory(Class<? extends Block> output, int outputCount, List<Identifier> supportedVariants, Map<String, Integer> variantChildren, List<Ingredient> inputs) {
        this.outputClass = output.getSimpleName();
        this.outputCount = outputCount;
        this.nbtElement = null;
        this.supportedVariants = supportedVariants;
        this.vanillaIngredients = inputs;
        this.variantChildren = variantChildren;
    }

    public DynamicFurnitureRecipeJsonFactory(Class<? extends Block> output, int outputCount, List<Identifier> supportedVariants, Map<String, Integer> variantChildren, List<Ingredient> inputs, @Nullable NbtElement nbtElement) {
        this.outputClass = output.getSimpleName();
        this.outputCount = outputCount;
        this.supportedVariants = supportedVariants;
        this.vanillaIngredients = inputs;
        this.variantChildren = variantChildren;
        this.nbtElement = nbtElement;
    }

    public DynamicFurnitureRecipeJsonFactory(Class<? extends Block> output, int outputCount, List<Identifier> supportedVariants) {
        this.outputClass = output.getSimpleName();
        this.outputCount = outputCount;
        this.supportedVariants = supportedVariants;
        this.nbtElement = null;
    }

    public DynamicFurnitureRecipeJsonFactory(Class<? extends Block> output, int outputCount, List<Identifier> supportedVariants, @Nullable NbtElement nbtElement) {
        this.outputClass = output.getSimpleName();
        this.outputCount = outputCount;
        this.supportedVariants = supportedVariants;
        this.nbtElement = nbtElement;
    }


    public static DynamicFurnitureRecipeJsonFactory create(Class<? extends Block> output, int outputCount, List<Identifier> supportedVariants, Map<String, Integer> variantChildren, List<Ingredient> inputs, @Nullable NbtElement nbtElement) {
        return new DynamicFurnitureRecipeJsonFactory(output, outputCount, supportedVariants, variantChildren, inputs, nbtElement);
    }

    public static DynamicFurnitureRecipeJsonFactory create(Class<? extends Block> output, int outputCount, List<Identifier> supportedVariants, Map<String, Integer> variantChildren, List<Ingredient> inputs) {
        return new DynamicFurnitureRecipeJsonFactory(output, outputCount, supportedVariants, variantChildren, inputs);
    }

    public static DynamicFurnitureRecipeJsonFactory create(Class<? extends Block> output, int outputCount, List<Identifier> supportedVariants, Map<String, Integer> variantChildren) {
        return new DynamicFurnitureRecipeJsonFactory(output, outputCount, supportedVariants, variantChildren);
    }

    public static DynamicFurnitureRecipeJsonFactory create(Class<? extends Block> output, int outputCount, List<Identifier> supportedVariants, Map<String, Integer> variantChildren, @Nullable NbtElement nbtElement) {
        return new DynamicFurnitureRecipeJsonFactory(output, outputCount, supportedVariants, variantChildren, nbtElement);
    }
    public static DynamicFurnitureRecipeJsonFactory create(Class<? extends Block> output, int outputCount, List<Identifier> supportedVariants) {
        return new DynamicFurnitureRecipeJsonFactory(output, outputCount, supportedVariants);
    }

    public static DynamicFurnitureRecipeJsonFactory create(Class<? extends Block> output, int outputCount, List<Identifier> supportedVariants, @Nullable  NbtElement nbtElement) {
        return new DynamicFurnitureRecipeJsonFactory(output, outputCount, supportedVariants, nbtElement);
    }

    public DynamicFurnitureRecipeJsonFactory criterion(String string, CriterionConditions criterionConditions) {
        this.builder.criterion(string, criterionConditions);
        return this;
    }


    public DynamicFurnitureRecipeJsonFactory group(@Nullable String group) {
        this.group = group;
        return this;
    }

    public String getOutputClass() {
        return outputClass;
    }

    public DynamicFurnitureRecipeJsonFactory vanillaInput(TagKey<Item> tag) {
        return this.vanillaInput(Ingredient.fromTag(tag));
    }

    public DynamicFurnitureRecipeJsonFactory vanillaInput(Ingredient ingredient) {
        return this.vanillaInput(ingredient, 1);
    }

    public DynamicFurnitureRecipeJsonFactory vanillaInput(ItemConvertible itemProvider) {
        return this.vanillaInput(itemProvider, 1);
    }

    public DynamicFurnitureRecipeJsonFactory vanillaInput(ItemConvertible itemProvider, int size) {
        for (int i = 0; i < size; ++i) {
            this.vanillaInput(Ingredient.ofItems(itemProvider));
        }
        return this;
    }

    public DynamicFurnitureRecipeJsonFactory vanillaInput(Ingredient ingredient, int size) {
        for (int i = 0; i < size; ++i) {
            this.vanillaIngredients.add(ingredient);
        }
        return this;
    }

    public DynamicFurnitureRecipeJsonFactory childInput(String ingredient, int size) {
        variantChildren.put(ingredient, size);
        return this;
    }
    public DynamicFurnitureRecipeJsonFactory childInput(String ingredient) {
        return childInput(ingredient, 1);
    }

    public void offerTo(Consumer<RecipeJsonProvider> exporter, Identifier recipeId) {
        this.builder.parent(new Identifier("recipes/root")).criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId)).rewards(AdvancementRewards.Builder.recipe(recipeId)).criteriaMerger(CriterionMerger.OR);
        exporter.accept(new DynamicFurnitureRecipeJsonProvider(recipeId, outputClass, nbtElement, outputCount, group, vanillaIngredients, supportedVariants, variantChildren, builder, new Identifier(recipeId.getNamespace(), "recipes/furniture/" + recipeId.getPath())));
    }


    public void offerTo(Consumer<RecipeJsonProvider> exporter) {
        this.offerTo(exporter, new Identifier(PaladinFurnitureMod.MOD_ID, this.getOutputClass().toLowerCase(Locale.US)));
    }

    public void offerTo(Consumer<RecipeJsonProvider> exporter, String recipePath) {
        Identifier identifier2 = new Identifier(recipePath);
        Identifier identifier = new Identifier(PaladinFurnitureMod.MOD_ID, this.getOutputClass().toLowerCase(Locale.US));
        if (identifier2.equals(identifier)) {
            throw new IllegalStateException("Recipe " + recipePath + " should remove its 'save' argument as it is equal to default one");
        }
        this.offerTo(exporter, identifier2);
    }


    public static class DynamicFurnitureRecipeJsonProvider
            implements RecipeJsonProvider {
        private final Identifier recipeId;
        private final String outputClass;
        private final int count;
        private final String group;
        private final List<Ingredient> vanillaIngredients;
        private final Map<String, Integer> variantChildren;
        private final List<Identifier> supportedVariants;
        private final Advancement.Builder builder;
        private final Identifier advancementId;
        @Nullable
        private final NbtElement nbtElement;

        public DynamicFurnitureRecipeJsonProvider(Identifier recipeId, String outputClass, @Nullable NbtElement nbtElement, int outputCount, String group, List<Ingredient> vanillaIngredients, List<Identifier> supportedVariants, Map<String, Integer> variantChildren, Advancement.Builder builder, Identifier advancementId) {
            this.recipeId = recipeId;
            this.outputClass = outputClass;
            this.count = outputCount;
            this.group = group;
            this.vanillaIngredients = vanillaIngredients;
            this.builder = builder;
            this.advancementId = advancementId;
            this.nbtElement = nbtElement;
            this.variantChildren = variantChildren;
            this.supportedVariants = supportedVariants;
        }

        @Override
        public void serialize(JsonObject json) {
            if (this.group != null && !this.group.isEmpty()) {
                json.addProperty("group", this.group);
            }
            JsonArray identifierArray = new JsonArray();
            for (Identifier identifier : this.supportedVariants) {
                identifierArray.add(identifier.toString());
            }
            json.add("supportedVariants", identifierArray);

            JsonObject ingredients = new JsonObject();

            JsonArray ingredientArray = new JsonArray();
            for (Ingredient ingredient : this.vanillaIngredients) {
                ingredientArray.add(ingredient.toJson());
            }
            ingredients.add("vanillaIngredients", ingredientArray);

            JsonObject variantChildrenObject = new JsonObject();
            for (Map.Entry<String, Integer> entry : this.variantChildren.entrySet()) {
                variantChildrenObject.addProperty(entry.getKey(), entry.getValue());
            }
            ingredients.add("variantChildren", variantChildrenObject);

            json.add("ingredients", ingredients);

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("outputClass", outputClass);
            if (this.count > 1) {
                jsonObject.addProperty("count", this.count);
            }
            if (nbtElement != null) {
                JsonElement object = NbtOps.INSTANCE.convertTo(JsonOps.INSTANCE, this.nbtElement);
                jsonObject.add("tag", object);
            }
            json.add("result", jsonObject);

        }

        @Override
        public RecipeSerializer<?> getSerializer() {
            return RecipeTypes.DYNAMIC_FURNITURE_SERIALIZER;
        }

        @Override
        public Identifier getRecipeId() {
            return this.recipeId;
        }

        @Override
        @Nullable
        public JsonObject toAdvancementJson() {
            return this.builder.toJson();
        }

        @Override
        @Nullable
        public Identifier getAdvancementId() {
            return this.advancementId;
        }
    }
}
