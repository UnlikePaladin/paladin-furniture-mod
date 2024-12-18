package com.unlikepaladin.pfm.menus;

import com.unlikepaladin.pfm.blocks.blockentities.MicrowaveBlockEntity;
import com.unlikepaladin.pfm.registry.ScreenHandlerIDs;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.recipe.book.RecipeBookType;
import net.minecraft.screen.PropertyDelegate;

public class MicrowaveScreenHandler
        extends AbstractMicrowaveScreenHandler {
    public MicrowaveScreenHandler(int syncId, PlayerInventory playerInventory, MicrowaveData buf) {
        super(ScreenHandlerIDs.MICROWAVE_SCREEN_HANDLER, RecipeType.SMOKING, RecipeBookType.SMOKER, syncId, playerInventory, buf);
    }

    public MicrowaveScreenHandler(MicrowaveBlockEntity microwaveBlockEntity, int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate) {
        super(microwaveBlockEntity, ScreenHandlerIDs.MICROWAVE_SCREEN_HANDLER, RecipeType.SMOKING, RecipeBookType.SMOKER, syncId, playerInventory, inventory, propertyDelegate);
    }
}

