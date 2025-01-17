package com.unlikepaladin.pfm.recipes;

import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.registry.RecipeTypes;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface FurnitureRecipe extends Recipe<PlayerInventory> {
    @Override
    default RecipeType<?> getType() {
        return RecipeTypes.FURNITURE_RECIPE;
    }

    List<CraftableFurnitureRecipe> getOutputs();

    String outputClass();

    default List<CraftableFurnitureRecipe> getAvailableOutputs(PlayerInventory inventory) {
        return getOutputs();
    }

    static int getSlotWithStackIgnoreNBT(PlayerInventory inventory, ItemStack stack) {
        for(int i = 0; i < inventory.main.size(); ++i) {
            if (!inventory.main.get(i).isEmpty() && stack.isOf(inventory.main.get(i).getItem())) {
                return i;
            }
        }
        return -1;
    }

    interface CraftableFurnitureRecipe extends Comparable<CraftableFurnitureRecipe> {
        List<Ingredient> getIngredients();
        ItemStack getOutput();
        ItemStack craft(PlayerInventory inventory);
        boolean matches(PlayerInventory playerInventory, World world);
        @Override
        default int compareTo(@NotNull FurnitureRecipe.CraftableFurnitureRecipe o) {
            return getOutput().toString().compareTo(o.getOutput().toString());
        }

        default ItemStack craftAndRemoveItems(PlayerInventory playerInventory) {
            ItemStack output = getOutput().copy();
            List<Ingredient> ingredients = getIngredients();
            for (Ingredient ingredient : ingredients) {
                for (ItemStack stack : ingredient.getMatchingStacks()) {
                    int indexOfStack = FurnitureRecipe.getSlotWithStackIgnoreNBT(playerInventory, stack);
                    int count = stack.getCount();
                    if (indexOfStack != -1) {
                        if (playerInventory.getStack(indexOfStack).getCount() >= stack.getCount()) {
                            ItemStack stack1 = playerInventory.getStack(indexOfStack);
                            stack1.decrement(stack.getCount());
                            playerInventory.setStack(indexOfStack, stack1);
                            playerInventory.markDirty();
                            break;
                        } else {
                            int remainingCount = count - playerInventory.getStack(indexOfStack).getCount();
                            playerInventory.setStack(indexOfStack, ItemStack.EMPTY);

                            while (remainingCount > 0) {
                                indexOfStack = FurnitureRecipe.getSlotWithStackIgnoreNBT(playerInventory, stack);
                                if (indexOfStack != -1) {
                                    ItemStack stack1 = playerInventory.getStack(indexOfStack);
                                    if (stack1.getCount() >= remainingCount) {
                                        stack1.decrement(remainingCount);
                                        playerInventory.setStack(indexOfStack, stack1);
                                        break;
                                    } else {
                                        int stackSize = stack1.getCount();
                                        remainingCount = Math.max(remainingCount-stackSize, 0);
                                        playerInventory.setStack(indexOfStack, ItemStack.EMPTY);
                                    }
                                } else {
                                    PaladinFurnitureMod.GENERAL_LOGGER.warn("Unable to craft recipe, this should never happen");
                                    return ItemStack.EMPTY;
                                }
                            }
                            playerInventory.markDirty();
                        }
                    }
                }
            }
            return output;
        }
    }
}
