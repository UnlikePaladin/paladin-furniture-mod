package com.unlikepaladin.pfm.compat.rei.forge;

import com.unlikepaladin.pfm.compat.rei.FreezingCategory;
import com.unlikepaladin.pfm.compat.rei.FreezingDisplay;
import com.unlikepaladin.pfm.compat.rei.FurnitureCategory;
import com.unlikepaladin.pfm.compat.rei.FurnitureDisplay;
import com.unlikepaladin.pfm.recipes.FreezingRecipe;
import com.unlikepaladin.pfm.recipes.SimpleFurnitureRecipe;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.forge.REIPlugin;
import net.minecraftforge.api.distmarker.Dist;

@REIPlugin(Dist.CLIENT)
public class PaladinFurnitureModREIPlugin implements REIClientPlugin {
    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new FurnitureCategory());
        registry.addWorkstations(FurnitureDisplay.IDENTIFIER, FurnitureCategory.ICON);
        registry.add(new FreezingCategory());
        registry.addWorkstations(FreezingDisplay.IDENTIFIER, FreezingCategory.WORKSTATIONS);
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        registry.registerFiller(SimpleFurnitureRecipe.class, FurnitureDisplay::new);
        registry.registerFiller(FreezingRecipe.class, FreezingDisplay::new);
    }

}
