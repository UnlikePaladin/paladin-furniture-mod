package com.unlikepaladin.pfm.compat.rei.fabric;

import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.compat.rei.FreezingDisplay;
import com.unlikepaladin.pfm.compat.rei.FurnitureDisplay;
import com.unlikepaladin.pfm.recipes.FreezingRecipe;
import com.unlikepaladin.pfm.recipes.FurnitureRecipe;
import com.unlikepaladin.pfm.registry.RecipeTypes;
import me.shedaniel.rei.api.common.display.DisplaySerializerRegistry;
import me.shedaniel.rei.api.common.plugins.REICommonPlugin;
import me.shedaniel.rei.api.common.registry.display.ServerDisplayRegistry;
import me.shedaniel.rei.plugin.client.displays.ClientsidedCraftingDisplay;
import net.minecraft.util.Identifier;

public class PaladinFurnitureModREIPlugin implements REICommonPlugin {
    @Override
    public void registerDisplays(ServerDisplayRegistry registry) {
        registry.beginRecipeFiller(FurnitureRecipe.class).filterType(RecipeTypes.FURNITURE_RECIPE).fill(FurnitureDisplay::new);
        registry.beginRecipeFiller(FreezingRecipe.class).filterType(RecipeTypes.FREEZING_RECIPE).fill(FreezingDisplay::new);
    }

    @Override
    public void registerDisplaySerializer(DisplaySerializerRegistry registry) {
        registry.register(RecipeTypes.FURNITURE_ID, FurnitureDisplay.SERIALIZER);
        registry.register(RecipeTypes.FREEZING_ID, FreezingDisplay.SERIALIZER);
    }
}
