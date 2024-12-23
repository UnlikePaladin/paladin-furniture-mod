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

import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.recipes.FurnitureRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.display.DisplaySerializer;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class FurnitureDisplay implements Display {
    public static final CategoryIdentifier<FurnitureDisplay> IDENTIFIER = CategoryIdentifier.of(Identifier.of(PaladinFurnitureMod.MOD_ID, "furniture"));
    public List<EntryIngredient> input;
    public List<EntryIngredient> output;
    public Optional<Identifier> location;
    public FurnitureDisplay(RecipeEntry<FurnitureRecipe> recipe) {
        this.output = Collections.singletonList(EntryIngredients.of(recipe.value().result()));
        this.location = Optional.of(recipe.id().getValue());
        List<Ingredient> ingredients = recipe.value().getIngredients();
        HashMap<Item, Integer> containedItems = new HashMap<>();
        for (Ingredient ingredient : ingredients) {
            for (RegistryEntry<Item> item : ingredient.getMatchingItems().toList()) {
                if (!containedItems.containsKey(item.value())) {
                    containedItems.put(item.value(), 1);
                } else {
                    containedItems.put(item.value(), containedItems.get(item.value()) + 1);
                }
            }
        }
        List<Ingredient> finalList = new ArrayList<>();
        for (Map.Entry<Item, Integer> entry: containedItems.entrySet()) {
            for (int i = 0; i < entry.getValue(); i++) {
                finalList.add(Ingredient.ofItem(entry.getKey()));
            }
        }
        finalList.sort(Comparator.comparing(o -> o.getMatchingItems().toList().getFirst().toString()));
        this.input = EntryIngredients.ofIngredients(finalList);
    }


    public FurnitureDisplay(List<EntryIngredient> input, List<EntryIngredient> output, Optional<Identifier> location) {
        this.input = input;
        this.output = output;
        this.location = location;
    }


    @Override
    public List<EntryIngredient> getInputEntries() {
        return input;
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return output;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public Optional<Identifier> getDisplayLocation() {
        return location;
    }

    @Override
    public @Nullable DisplaySerializer<? extends Display> getSerializer() {
        return SERIALIZER;
    }

    public static final DisplaySerializer<FurnitureDisplay> SERIALIZER = DisplaySerializer.of(
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                    EntryIngredient.codec().listOf().fieldOf("inputs").forGetter(FurnitureDisplay::getInputEntries),
                    EntryIngredient.codec().listOf().fieldOf("outputs").forGetter(FurnitureDisplay::getOutputEntries),
                    Identifier.CODEC.optionalFieldOf("location").forGetter(FurnitureDisplay::getDisplayLocation)
            ).apply(instance, FurnitureDisplay::new)),
            PacketCodec.tuple(
                    EntryIngredient.streamCodec().collect(PacketCodecs.toList()),
                    FurnitureDisplay::getInputEntries,
                    EntryIngredient.streamCodec().collect(PacketCodecs.toList()),
                    FurnitureDisplay::getOutputEntries,
                    PacketCodecs.optional(Identifier.PACKET_CODEC),
                    FurnitureDisplay::getDisplayLocation,
                    FurnitureDisplay::new
            ));
}