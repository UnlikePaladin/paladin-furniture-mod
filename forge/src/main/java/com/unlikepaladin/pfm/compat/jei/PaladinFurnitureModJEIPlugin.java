package com.unlikepaladin.pfm.compat.jei;

import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.recipes.FreezingRecipe;
import com.unlikepaladin.pfm.recipes.FurnitureRecipe;
import com.unlikepaladin.pfm.registry.PaladinFurnitureModBlocksItems;
import com.unlikepaladin.pfm.registry.RecipeTypes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.Objects;

@JeiPlugin
public class PaladinFurnitureModJEIPlugin implements IModPlugin {
    public void PaladinFurnitureModJEIPlugin(){

    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        MinecraftClient mc = MinecraftClient.getInstance();
        ClientWorld world = Objects.requireNonNull(mc.world);

        Collection<FreezingRecipe> freezingRecipes = world.getRecipeManager().listAllOfType(RecipeTypes.FREEZING_RECIPE);
        registration.addRecipes(freezingRecipes, FreezingCategory.IDENTIFIER);

        Collection<FurnitureRecipe> simpleFurnitureRecipes = world.getRecipeManager().listAllOfType(RecipeTypes.FURNITURE_RECIPE);
        registration.addRecipes(simpleFurnitureRecipes, FurnitureCategory.IDENTIFIER);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new FurnitureCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new FreezingCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration)
    {
        registration.addRecipeCatalyst(new ItemStack(PaladinFurnitureModBlocksItems.WHITE_FREEZER), FreezingCategory.IDENTIFIER);
        registration.addRecipeCatalyst(new ItemStack(PaladinFurnitureModBlocksItems.IRON_FREEZER), FreezingCategory.IDENTIFIER);
        registration.addRecipeCatalyst(new ItemStack(PaladinFurnitureModBlocksItems.GRAY_FREEZER), FreezingCategory.IDENTIFIER);
        registration.addRecipeCatalyst(new ItemStack(PaladinFurnitureModBlocksItems.WORKING_TABLE), FurnitureCategory.IDENTIFIER);
    }

    @Override
    public Identifier getPluginUid() {
        return new Identifier(PaladinFurnitureMod.MOD_ID, "jei_plugin");
    }
}
