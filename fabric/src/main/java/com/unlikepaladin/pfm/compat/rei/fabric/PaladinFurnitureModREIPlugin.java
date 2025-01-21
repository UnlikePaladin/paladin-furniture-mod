package com.unlikepaladin.pfm.compat.rei.fabric;

import com.unlikepaladin.pfm.client.screens.FreezerScreen;
import com.unlikepaladin.pfm.client.screens.WorkbenchScreen;
import com.unlikepaladin.pfm.recipes.FreezingRecipe;
import com.unlikepaladin.pfm.recipes.FurnitureRecipe;
import com.unlikepaladin.pfm.registry.PaladinFurnitureModBlocksItems;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.RecipeHelper;
import me.shedaniel.rei.api.plugins.REIPluginV0;
import net.minecraft.util.Identifier;


public class PaladinFurnitureModREIPlugin implements REIPluginV0 {

    @Override
    public void registerPluginCategories(RecipeHelper recipeHelper) {
        recipeHelper.registerCategories(
                new FurnitureCategory(),
                new FreezingCategory()
        );
    }

    @Override
    public void registerRecipeDisplays(RecipeHelper recipeHelper) {
        recipeHelper.registerRecipes(FurnitureDisplay.IDENTIFIER, FurnitureRecipe.class , FurnitureDisplay::new);
        recipeHelper.registerRecipes(FreezingDisplay.IDENTIFIER, FreezingRecipe.class, FreezingDisplay::new);
    }

    @Override
    public void registerOthers(RecipeHelper recipeHelper) {
        recipeHelper.registerWorkingStations(FurnitureDisplay.IDENTIFIER, EntryStack.create(PaladinFurnitureModBlocksItems.WORKING_TABLE));
        recipeHelper.registerWorkingStations(FreezingDisplay.IDENTIFIER, EntryStack.create(PaladinFurnitureModBlocksItems.WHITE_FRIDGE));
        recipeHelper.registerContainerClickArea(new Rectangle(88, 32, 28, 23), WorkbenchScreen.class, FurnitureDisplay.IDENTIFIER);
        recipeHelper.registerContainerClickArea(new Rectangle(78, 32, 28, 23), FreezerScreen.class, FreezingDisplay.IDENTIFIER);
    }

    @Override
    public Identifier getPluginIdentifier() {
        return new Identifier("pfm:pfm");
    }
}