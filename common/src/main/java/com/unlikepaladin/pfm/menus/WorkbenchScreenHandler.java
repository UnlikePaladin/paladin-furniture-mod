package com.unlikepaladin.pfm.menus;

import com.google.common.collect.Lists;
import com.unlikepaladin.pfm.recipes.FurnitureRecipe;
import com.unlikepaladin.pfm.registry.PaladinFurnitureModBlocksItems;
import com.unlikepaladin.pfm.registry.RecipeTypes;
import com.unlikepaladin.pfm.registry.ScreenHandlerIDs;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import java.util.List;

public class WorkbenchScreenHandler extends ScreenHandler {
    private final ScreenHandlerContext context;
    private final List<FurnitureRecipe.CraftableFurnitureRecipe> availableRecipes = Lists.newArrayList();
    public static final List<FurnitureRecipe.CraftableFurnitureRecipe> ALL_RECIPES = Lists.newArrayList();
    private final List<FurnitureRecipe.CraftableFurnitureRecipe> sortedRecipes = Lists.newArrayList();
    private final List<FurnitureRecipe.CraftableFurnitureRecipe> searchableRecipes = Lists.newArrayList();

    private final Property selectedRecipe = Property.create();
    private final World world;
    final Slot outputSlot;
    final CraftingResultInventory output = new CraftingResultInventory();
    long lastTakeTime;
    final PlayerInventory playerInventory;
    Runnable contentsChangedListener;
   public boolean searching = false;

    public WorkbenchScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, ScreenHandlerContext.EMPTY);
    }
    public WorkbenchScreenHandler(int syncId, PlayerInventory playerInventory, final ScreenHandlerContext context) {
        super(ScreenHandlerIDs.WORKBENCH_SCREEN_HANDLER, syncId);
        this.context = context;
        this.world = playerInventory.player.world;
        this.playerInventory = playerInventory;
        this.contentsChangedListener = () -> {
        };
        this.outputSlot = this.addSlot(new Slot(this.output, 0, 143, 50) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return false;
            }
            @Override
            public void onTakeItem(PlayerEntity player, ItemStack stack) {
                if (WorkbenchScreenHandler.this.craft()) {
                    stack.onCraft(player.world, player, stack.getCount());
                    WorkbenchScreenHandler.this.output.unlockLastRecipe(player);
                    WorkbenchScreenHandler.this.populateResult(player);
                    context.run((world, pos) -> {
                        long l = world.getTime();
                        if (WorkbenchScreenHandler.this.lastTakeTime != l) {
                            world.playSound(null, pos, SoundEvents.UI_STONECUTTER_TAKE_RESULT, SoundCategory.BLOCKS, 1.0f, 1.0f);
                            WorkbenchScreenHandler.this.lastTakeTime = l;
                        }
                    });
                    WorkbenchScreenHandler.this.contentsChangedListener.run();
                    WorkbenchScreenHandler.this.updateInput();
                    super.onTakeItem(player, stack);
                }
            }
        });
        int i;
        for (i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 98 + i * 18));
            }
        }
        for (i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 156));
        }
        this.addProperty(this.selectedRecipe);
        if (ALL_RECIPES.isEmpty()) {
           world.getRecipeManager().listAllOfType(RecipeTypes.FURNITURE_RECIPE).forEach(recipe -> {
               ALL_RECIPES.addAll(recipe.getInnerRecipes());
           });
           ALL_RECIPES.sort(FurnitureRecipe.CraftableFurnitureRecipe::compareTo);
        }
        this.updateInput();
        selectedRecipe.set(-1);
    }

    boolean craft() {
        if (!this.availableRecipes.isEmpty() && this.isInBounds(this.availableRecipes, this.selectedRecipe.get())) {
            FurnitureRecipe.CraftableFurnitureRecipe simpleFurnitureRecipe = this.sortedRecipes.get(this.selectedRecipe.get());
            if (simpleFurnitureRecipe.matches(playerInventory, playerInventory.player.world)) {
               simpleFurnitureRecipe.craftAndRemoveItems(playerInventory);
                return true;
            }
        }
        return false;
    }

    void populateResult(PlayerEntity player) {
        if (!this.availableRecipes.isEmpty() && this.isInBounds(this.availableRecipes, this.selectedRecipe.get())) {
            FurnitureRecipe.CraftableFurnitureRecipe simpleFurnitureRecipe = this.sortedRecipes.get(this.selectedRecipe.get());
            this.outputSlot.setStack(simpleFurnitureRecipe.craft(player.getInventory()));
        } else {
            this.outputSlot.setStack(ItemStack.EMPTY);
        }
        this.sendContentUpdates();
    }

    public boolean isInBounds(List<FurnitureRecipe.CraftableFurnitureRecipe> simpleFurnitureRecipes, int id) {
        if (id >= simpleFurnitureRecipes.size() || id < 0) {
            return false;
        }
        return simpleFurnitureRecipes.contains(this.getSortedRecipes().get(id));
    }

    @Override
    public void onContentChanged(Inventory inventory) {
        this.updateInput();
        super.onContentChanged(inventory);
    }

    public PlayerInventory getPlayerInventory() {
        return playerInventory;
    }

    public int getSelectedRecipe() {
        return this.selectedRecipe.get();
    }

    public List<FurnitureRecipe.CraftableFurnitureRecipe> getAvailableRecipes() {
        return this.availableRecipes;
    }

    public List<FurnitureRecipe.CraftableFurnitureRecipe> getSortedRecipes() {
        return this.sortedRecipes;
    }

    public List<FurnitureRecipe.CraftableFurnitureRecipe> getAllRecipes() {
        return ALL_RECIPES;
    }

    public int getAvailableRecipeCount() {
        return this.availableRecipes.size();
    }

    public int getTotalRecipeCount() {
        return this.sortedRecipes.size();
    }

    public int getVisibleRecipeCount() {
        return this.searching ? searchableRecipes.size() : getTotalRecipeCount();
    }

    public void updateInput() {
        // Reset the selected recipe and clear the output slot
        if (!this.availableRecipes.isEmpty() && getSelectedRecipe() != -1) {
            if (!this.sortedRecipes.get(getSelectedRecipe()).matches(playerInventory, world)){
                this.selectedRecipe.set(-1);
                this.outputSlot.setStack(ItemStack.EMPTY);
            }
        }
        // Reset the available recipes list and add all recipes that can be crafted
        this.availableRecipes.clear();
        this.availableRecipes.addAll(ALL_RECIPES.stream().filter(newFurnitureRecipe -> newFurnitureRecipe.matches(playerInventory, world)).toList());
        // Clear the visible recipe list and add the craft-able recipes first, then add the rest, checking that it's not present already so that it's not overridden.
        this.sortedRecipes.clear();
        this.sortedRecipes.addAll(availableRecipes);
        this.sortedRecipes.addAll(ALL_RECIPES.stream().filter(furnitureRecipe -> !sortedRecipes.contains(furnitureRecipe)).toList());
    }

    public boolean canCraft() {
        return !this.availableRecipes.isEmpty();
    }

    public void setContentsChangedListener(Runnable contentsChangedListener) {
        this.contentsChangedListener = contentsChangedListener;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return canUse(this.context, player, PaladinFurnitureModBlocksItems.WORKING_TABLE);
    }

    @Override
    public boolean canInsertIntoSlot(ItemStack stack, Slot slot) {
        return slot.inventory != this.outputSlot && super.canInsertIntoSlot(stack, slot);
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasStack()) {
            ItemStack itemStack2 = slot.getStack();
            Item item = itemStack2.getItem();
            itemStack = itemStack2.copy();
            if (index == 0) {
                item.onCraft(itemStack2, player.world, player);
                if (!this.insertItem(itemStack2, 1, 37, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickTransfer(itemStack2, itemStack);
            } else if (index >= 1 && index < 28) {
                if (!this.insertItem(itemStack2, 28, 37, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= 28 && index < 37 && !this.insertItem(itemStack2, 1, 28, false)) {
                return ItemStack.EMPTY;
            }

            if (itemStack2.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            }

            slot.markDirty();
            if (itemStack2.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTakeItem(player, itemStack2);
            this.sendContentUpdates();
        }
        return itemStack;
    }


    @Override
    public boolean onButtonClick(PlayerEntity player, int id) {
        this.updateInput();
        if (this.isInBounds(this.availableRecipes, id) && canCraft()) {
            this.selectedRecipe.set(id);
            this.populateResult(player);
            return true;
        }
        return false;
    }

    public List<FurnitureRecipe.CraftableFurnitureRecipe> getSearchableRecipes() {
        return searchableRecipes;
    }
}