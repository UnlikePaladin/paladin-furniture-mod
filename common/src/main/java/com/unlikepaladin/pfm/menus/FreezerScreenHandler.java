package com.unlikepaladin.pfm.menus;

import com.unlikepaladin.pfm.registry.ScreenHandlerIDs;
import com.unlikepaladin.pfm.menus.AbstractFreezerScreenHandler;
import com.unlikepaladin.pfm.registry.RecipeTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.recipe.book.RecipeBookType;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.server.world.ServerWorld;

public class FreezerScreenHandler extends AbstractFreezerScreenHandler {
    public FreezerScreenHandler(int syncId, PlayerInventory playerInventory) {
        super(ScreenHandlerIDs.FREEZER_SCREEN_HANDLER, RecipeTypes.FREEZING_RECIPE, RecipeBookType.FURNACE, syncId, playerInventory);
    }

    public FreezerScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate) {
        super(ScreenHandlerIDs.FREEZER_SCREEN_HANDLER, RecipeTypes.FREEZING_RECIPE, RecipeBookType.FURNACE, syncId, playerInventory, inventory, propertyDelegate);
    }
}

