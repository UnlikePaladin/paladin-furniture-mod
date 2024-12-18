package com.unlikepaladin.pfm.compat.emi;

import com.mojang.blaze3d.systems.RenderSystem;
import com.unlikepaladin.pfm.recipes.FreezingRecipe;
import com.unlikepaladin.pfm.recipes.FurnitureRecipe;
import com.unlikepaladin.pfm.registry.PaladinFurnitureModBlocksItems;
import com.unlikepaladin.pfm.registry.RecipeTypes;
import com.unlikepaladin.pfm.registry.ScreenHandlerIDs;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiRenderable;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.util.Identifier;

@EmiEntrypoint
public class PaladinFurnitureModEMIPlugin implements EmiPlugin {
    @Override
    public void register(EmiRegistry registry) {

    }
    /*
    protected static EmiRecipeCategory FURNITURE;
    protected static EmiRecipeCategory FREEZER;

    public static EmiIngredient WORKBENCH_ICON = EmiStack.of(PaladinFurnitureModBlocksItems.WORKING_TABLE);
    public static Identifier WORKBENCH_ID = Identifier.of("pfm:furniture");
    public static EmiIngredient FREEZER_ICON = EmiStack.of(PaladinFurnitureModBlocksItems.WHITE_FREEZER);
    public static Identifier FREEZER_ID = Identifier.of("pfm:freezer");
    @Override
    public void register(EmiRegistry registry) {
        FURNITURE  = new EmiRecipeCategory(WORKBENCH_ID, WORKBENCH_ICON, simplifiedRenderer(240, 240));
        FREEZER  = new EmiRecipeCategory(FREEZER_ID, FREEZER_ICON, simplifiedRenderer(240, 240));
        registry.addCategory(FURNITURE);
        registry.addWorkstation(FURNITURE, WORKBENCH_ICON);
        registry.addCategory(FREEZER);
        registry.addWorkstation(FREEZER, FREEZER_ICON);
        registry.addWorkstation(FREEZER, EmiStack.of(PaladinFurnitureModBlocksItems.GRAY_FREEZER));
        registry.addWorkstation(FREEZER, EmiStack.of(PaladinFurnitureModBlocksItems.IRON_FREEZER));
        registry.addRecipeHandler(ScreenHandlerIDs.WORKBENCH_SCREEN_HANDLER, new FurnitureRecipeHandler());
        registry.addRecipeHandler(ScreenHandlerIDs.FREEZER_SCREEN_HANDLER, new FreezerRecipeHandler(FREEZER));

        for (RecipeEntry<FurnitureRecipe> recipe : registry.getRecipeManager().listAllOfType(RecipeTypes.FURNITURE_RECIPE)) {
            registry.addRecipe(new EmiFurnitureRecipe(recipe));
        }
        for (RecipeEntry<FreezingRecipe> recipe : registry.getRecipeManager().listAllOfType(RecipeTypes.FREEZING_RECIPE)) {
            registry.addRecipe(new EmiFreezingRecipe(recipe));
        }
    }

    private static EmiRenderable simplifiedRenderer(int u, int v) {
        return (context, x, y, delta) -> {
            RenderSystem.setShader(GameRenderer::getPositionTexProgram);
            context.drawTexture(Identifier.of("emi", "textures/gui/widgets.png"), x, y, u, v, 16, 16, 256, 256);
        };
    }*/
}
