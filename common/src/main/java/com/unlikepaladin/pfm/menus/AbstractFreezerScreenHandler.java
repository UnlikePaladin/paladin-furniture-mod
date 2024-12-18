package com.unlikepaladin.pfm.menus;

import com.unlikepaladin.pfm.blocks.blockentities.FreezerBlockEntity;
import com.unlikepaladin.pfm.fabric.menus.slots.GenericOutputSlot;
import com.unlikepaladin.pfm.menus.slots.FreezerFuelSlot;
import com.unlikepaladin.pfm.registry.RecipeTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.recipe.book.RecipeBookType;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

import java.util.List;

public abstract class AbstractFreezerScreenHandler extends AbstractRecipeScreenHandler {
    private final Inventory inventory;
    private final PropertyDelegate propertyDelegate;
    protected final World world;
    private final RecipeType<? extends AbstractCookingRecipe> recipeType;
    private final RecipeBookType category;
    private final RecipePropertySet recipePropertySet;

    protected AbstractFreezerScreenHandler(ScreenHandlerType<?> type, RecipeType<? extends AbstractCookingRecipe> recipeType, RecipeBookType category, int syncId, PlayerInventory playerInventory) {
        this(type, recipeType, category, syncId, playerInventory, new SimpleInventory(3), new ArrayPropertyDelegate(4));
    }

    protected AbstractFreezerScreenHandler(ScreenHandlerType<?> type, RecipeType<? extends AbstractCookingRecipe> recipeType, RecipeBookType category, int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate) {
        super(type, syncId);
        int i;
        this.recipeType = recipeType;
        this.category = category;
        AbstractFreezerScreenHandler.checkSize(inventory, 3);
        AbstractFreezerScreenHandler.checkDataCount(propertyDelegate, 4);
        this.inventory = inventory;
        inventory.onOpen(playerInventory.player);
        this.propertyDelegate = propertyDelegate;
        this.world = playerInventory.player.getEntityWorld();
        this.recipePropertySet = this.world.getRecipeManager().getPropertySet(RecipeTypes.FREEZING_INPUT);
        this.addSlot(new Slot(inventory, 0, 56, 17));
        this.addSlot(new FreezerFuelSlot(this, inventory, 1, 56, 53));
        this.addSlot(new GenericOutputSlot(playerInventory.player, inventory, 2, 116, 35,0));
        for (i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
        this.addProperties(propertyDelegate);
    }

    @Override
    public void populateRecipeFinder(RecipeFinder finder) {
        if (this.inventory instanceof RecipeInputProvider) {
            ((RecipeInputProvider)((Object)this.inventory)).provideRecipeInputs(finder);
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    public Inventory getInventory() {
        return this.inventory;
    }


    public ItemStack quickMove(PlayerEntity player, int slot) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot2 = (Slot)this.slots.get(slot);
        if (slot2 != null && slot2.hasStack()) {
            ItemStack itemStack2 = slot2.getStack();
            itemStack = itemStack2.copy();
            if (slot == 2) {
                if (!this.insertItem(itemStack2, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }

                slot2.onQuickTransfer(itemStack2, itemStack);
            } else if (slot != 1 && slot != 0) {
                if (this.isFreezeable(itemStack2)) {
                    if (!this.insertItem(itemStack2, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (this.isFuel(itemStack2)) {
                    if (!this.insertItem(itemStack2, 1, 2, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (slot >= 3 && slot < 30) {
                    if (!this.insertItem(itemStack2, 30, 39, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (slot >= 30 && slot < 39 && !this.insertItem(itemStack2, 3, 30, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(itemStack2, 3, 39, false)) {
                return ItemStack.EMPTY;
            }

            if (itemStack2.isEmpty()) {
                slot2.setStack(ItemStack.EMPTY);
            } else {
                slot2.markDirty();
            }

            if (itemStack2.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot2.onTakeItem(player, itemStack2);
        }

        return itemStack;
    }

    protected boolean isFreezeable(ItemStack itemStack) {
        return this.recipePropertySet.canUse(itemStack);
    }

    public boolean isFuel(ItemStack itemStack) {
        return FreezerBlockEntity.canUseAsFuel(itemStack);
    }

    public int getFreezeProgress() {
        int i = this.propertyDelegate.get(2);
        int j = this.propertyDelegate.get(3);
        if (j == 0 || i == 0) {
            return 0;
        }
        return i * 24 / j;
    }

    public int getFuelProgress() {
        int i = this.propertyDelegate.get(1);
        if (i == 0) {
            i = 200;
        }
        return this.propertyDelegate.get(0) * 13 / i;
    }

    public boolean isActive() {
        return this.propertyDelegate.get(0) > 0;
    }

    @Override
    public RecipeBookType getCategory() {
        return this.category;
    }

    @Override
    public boolean canInsertIntoSlot(Slot slot) {
        return slot.id != 1;
    }

    @Override
    public boolean canInsertIntoSlot(ItemStack stack, Slot slot) {
        return slot.id != 1;
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        this.inventory.onClose(player);
    }

    public AbstractRecipeScreenHandler.PostFillAction fillInputSlots(boolean craftAll, boolean creative, RecipeEntry<?> recipe, final ServerWorld world, PlayerInventory inventory) {
        final List<Slot> list = List.of(this.getSlot(0), this.getSlot(2));
        RecipeEntry<AbstractCookingRecipe> recipeEntry = (RecipeEntry<AbstractCookingRecipe>) recipe;
        return InputSlotFiller.fill(new InputSlotFiller.Handler<>() {
            public void populateRecipeFinder(RecipeFinder finder) {
                AbstractFreezerScreenHandler.this.populateRecipeFinder(finder);
            }

            public void clear() {
                list.forEach((slot) -> {
                    slot.setStackNoCallbacks(ItemStack.EMPTY);
                });
            }

            public boolean matches(RecipeEntry<AbstractCookingRecipe> entry) {
                return entry.value().matches(new SingleStackRecipeInput(AbstractFreezerScreenHandler.this.inventory.getStack(0)), world);
            }
        }, 1, 1, List.of(this.getSlot(0)), list, inventory, recipeEntry, craftAll, creative);
    }
}

