package com.unlikepaladin.pfm.compat.cookingforblockheads.fabric.menu;

import com.unlikepaladin.pfm.blocks.blockentities.StovePacket;
import com.unlikepaladin.pfm.compat.cookingforblockheads.fabric.StoveBlockEntityBalm;
import com.unlikepaladin.pfm.compat.cookingforblockheads.fabric.menu.slot.StoveResultSlot;
import com.unlikepaladin.pfm.menus.AbstractMicrowaveScreenHandler;
import com.unlikepaladin.pfm.menus.StoveScreenHandler;
import com.unlikepaladin.pfm.registry.ScreenHandlerIDs;
import net.blay09.mods.cookingforblockheads.block.entity.OvenBlockEntity;
import net.blay09.mods.cookingforblockheads.menu.IContainerWithDoor;
import net.blay09.mods.cookingforblockheads.menu.OvenMenu;
import net.blay09.mods.cookingforblockheads.menu.slot.SlotOven;
import net.blay09.mods.cookingforblockheads.menu.slot.SlotOvenFuel;
import net.blay09.mods.cookingforblockheads.menu.slot.SlotOvenTool;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;

public class StoveScreenHandlerBalm extends ScreenHandler implements IContainerWithDoor {
    private final StoveBlockEntityBalm tileEntity;

    public StoveScreenHandlerBalm(int windowId, PlayerInventory playerInventory, StoveBlockEntityBalm oven) {
        super(ScreenHandlerIDs.STOVE_SCREEN_HANDLER, windowId);
        this.tileEntity = oven;
        oven.onOpen(playerInventory.player);
        Inventory container = oven.getContainer();
        int offsetX = oven.hasPowerUpgrade() ? -5 : 0;

        int i;
        for(i = 0; i < 3; ++i) {
            this.addSlot(new Slot(container, i, 84 + i * 18 + offsetX, 19));
        }

        this.addSlot(new SlotOvenFuel(this, container, 3, 61 + offsetX, 59));

        for(i = 0; i < 3; ++i) {
            this.addSlot(new StoveResultSlot(playerInventory.player, oven, container, i + 4, 142 + offsetX, 41 + i * 18));
        }

        int j;
        for(i = 0; i < 3; ++i) {
            for(j = 0; j < 3; ++j) {
                this.addSlot(new SlotOven(container, 7 + j + i * 3, 84 + j * 18 + offsetX, 41 + i * 18));
            }
        }

        for(i = 0; i < 4; ++i) {
            this.addSlot(new SlotOvenTool(container, 16 + i, 8, 19 + i * 18, i));
        }

        for(i = 0; i < 3; ++i) {
            for(j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 30 + j * 18, 111 + i * 18));
            }
        }

        for(i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 30 + i * 18, 169));
        }

        this.addProperties(oven.getContainerData());
    }

    public StoveBlockEntityBalm getTileEntity() {
        return this.tileEntity;
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        tileEntity.onClose(player);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slotIndex) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);
        if (slot.hasStack()) {
            ItemStack slotStack = slot.getStack();
            itemStack = slotStack.copy();
            if (slotIndex >= 7 && slotIndex < 20) {
                if (!this.insertItem(slotStack, 20, 56, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (slotIndex >= 4 && slotIndex <= 6) {
                if (!this.insertItem(slotStack, 20, 56, false)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickTransfer(slotStack, itemStack);
            } else if (slotIndex >= 20) {
                ItemStack smeltingResult = this.tileEntity.getSmeltingResult(slotStack);
                if (StoveBlockEntityBalm.isItemFuel(tileEntity.getWorld(), slotStack)) {
                    if (!this.insertItem(slotStack, 3, 4, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!smeltingResult.isEmpty()) {
                    if (!this.insertItem(slotStack, 0, 3, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (slotIndex >= 20 && slotIndex < 47) {
                    if (!this.insertItem(slotStack, 47, 56, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (slotIndex >= 47 && slotIndex < 56 && !this.insertItem(slotStack, 20, 47, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(slotStack, 20, 47, false)) {
                return ItemStack.EMPTY;
            }

            if (slotStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }

            if (slotStack.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTakeItem(player, slotStack);
        }

        return itemStack;
    }

    public boolean canUse(PlayerEntity player) {
        return true;
    }

    public boolean isTileEntity(BlockEntity blockEntity) {
        return this.tileEntity == blockEntity;
    }

    public boolean isFuel(ItemStack itemStack) {
        return OvenBlockEntity.isItemFuel(this.tileEntity.getWorld(), itemStack);
    }

    public static class SlotOvenFuel extends Slot {
        private final StoveScreenHandlerBalm menu;

        public SlotOvenFuel(StoveScreenHandlerBalm menu, Inventory container, int i, int x, int y) {
            super(container, i, x, y);
            this.menu = menu;
        }

        public boolean canInsert(ItemStack itemStack) {
            return this.menu.isFuel(itemStack);
        }
    }
}