package com.unlikepaladin.pfm.compat.rei;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.recipes.FreezingRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.display.DisplaySerializer;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class FreezingDisplay implements Display {
    public static final CategoryIdentifier<FreezingDisplay> IDENTIFIER = CategoryIdentifier.of(Identifier.of(PaladinFurnitureMod.MOD_ID, "freezing"));
    public static final DisplaySerializer<FreezingDisplay> SERIALIZER = DisplaySerializer.of(
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                    EntryIngredient.codec().listOf().fieldOf("inputs").forGetter(FreezingDisplay::getInputEntries),
                    EntryIngredient.codec().listOf().fieldOf("outputs").forGetter(FreezingDisplay::getOutputEntries),
                    Identifier.CODEC.optionalFieldOf("location").forGetter(FreezingDisplay::getDisplayLocation),
                    Codec.INT.fieldOf("cookTime").forGetter(d -> d.cookTime),
                    Codec.FLOAT.fieldOf("xp").forGetter(d -> d.xp)
            ).apply(instance, FreezingDisplay::new)),
            PacketCodec.tuple(
                    EntryIngredient.streamCodec().collect(PacketCodecs.toList()),
                    FreezingDisplay::getInputEntries,
                    EntryIngredient.streamCodec().collect(PacketCodecs.toList()),
                    FreezingDisplay::getOutputEntries,
                    PacketCodecs.optional(Identifier.PACKET_CODEC),
                    FreezingDisplay::getDisplayLocation,
                    PacketCodecs.INTEGER,
                    d -> d.cookTime,
                    PacketCodecs.FLOAT,
                    d -> d.xp,
                    FreezingDisplay::new
            ));

    public List<EntryIngredient> input;
    public List<EntryIngredient> output;
    public int cookTime;
    private final float xp;
    public Optional<Identifier> location;

    public FreezingDisplay(RecipeEntry<FreezingRecipe> recipe) {
        input = Collections.singletonList(EntryIngredients.ofIngredient(recipe.value().ingredient()));
        output = Collections.singletonList(EntryIngredients.of(recipe.value().result()));
        cookTime = recipe.value().getCookingTime();
        xp = recipe.value().getExperience();
        location = Optional.of(recipe.id().getValue());
    }

    public FreezingDisplay(List<EntryIngredient> inputs, List<EntryIngredient> outputs, Optional<Identifier> location, int cookTime, float xp) {
        this.input = inputs;
        this.output = outputs;
        this.cookTime = cookTime;
        this.xp = xp;
        this.location = location;
    }

    public FreezingDisplay(FreezingRecipe freezingRecipe) {
        this.input = Collections.singletonList(EntryIngredients.ofIngredient(freezingRecipe.ingredient()));
        this.output = Collections.singletonList(EntryIngredients.of(freezingRecipe.result()));
        this.cookTime = freezingRecipe.getCookingTime();
        this.xp = freezingRecipe.getExperience();
        this.location = Optional.empty();
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


    public float getXp() {
        return xp;
    }

    @Override
    public Optional<Identifier> getDisplayLocation() {
        return location;
    }

    @Override
    public @Nullable DisplaySerializer<? extends Display> getSerializer() {
        return SERIALIZER;
    }
}
