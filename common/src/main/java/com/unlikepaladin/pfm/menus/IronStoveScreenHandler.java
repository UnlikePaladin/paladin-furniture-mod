package com.unlikepaladin.pfm.menus;

import com.unlikepaladin.pfm.registry.ScreenHandlerIDs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipePropertySet;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.recipe.book.RecipeBookType;
import net.minecraft.screen.AbstractFurnaceScreenHandler;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandlerType;

public class IronStoveScreenHandler extends AbstractFurnaceScreenHandler {
    private final Inventory inventory;
    public IronStoveScreenHandler(int syncId, PlayerInventory playerInventory) {
        super(ScreenHandlerIDs.IRON_STOVE_SCREEN_HANDLER, RecipeType.SMOKING, RecipePropertySet.SMOKER_INPUT, RecipeBookType.SMOKER, syncId, playerInventory);
        this.inventory = new SimpleInventory(3);
    }

    public IronStoveScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate) {
        super(ScreenHandlerIDs.IRON_STOVE_SCREEN_HANDLER, RecipeType.SMOKING, RecipePropertySet.SMOKER_INPUT, RecipeBookType.SMOKER, syncId, playerInventory, inventory, propertyDelegate);
        this.inventory = inventory;
        inventory.onOpen(playerInventory.player);
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        this.inventory.onClose(player);
    }
}