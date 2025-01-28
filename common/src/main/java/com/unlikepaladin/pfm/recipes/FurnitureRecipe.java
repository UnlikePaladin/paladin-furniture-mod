package com.unlikepaladin.pfm.recipes;

import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.registry.RecipeTypes;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.book.RecipeBookCategories;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public interface FurnitureRecipe extends Recipe<FurnitureRecipe.FurnitureRecipeInput> {
    @Override
    default RecipeType<?> getType() {
        return RecipeTypes.FURNITURE_RECIPE;
    }

    List<CraftableFurnitureRecipe> getInnerRecipes();

    String outputClass();

    default List<CraftableFurnitureRecipe> getAvailableOutputs(FurnitureRecipe.FurnitureRecipeInput inventory, RegistryWrapper.WrapperLookup registryManager) {
        return getInnerRecipes();
    }

    default CraftableFurnitureRecipe getInnerRecipeFromOutput(ItemStack stack) {
        return getInnerRecipes().get(0);
    }

    static int getSlotWithStackIgnoreNBT(PlayerInventory inventory, ItemStack stack) {
        for(int i = 0; i < inventory.main.size(); ++i) {
            if (!inventory.main.get(i).isEmpty() && stack.isOf(inventory.main.get(i).getItem())) {
                return i;
            }
        }
        return -1;
    }

    default int getMaxInnerRecipeSize() {
        return getIngredients().size();
    }

    default int getOutputCount(RegistryWrapper.WrapperLookup registryManager) {
        return getResult(registryManager).getCount();
    }

    default List<? extends CraftableFurnitureRecipe> getInnerRecipesForVariant(Identifier identifier) {
        return Collections.singletonList(getInnerRecipes().get(0));
    }

    default String getName(RegistryWrapper.WrapperLookup registryManager) {
        return getResult(registryManager).getName().getString();
    }

    interface CraftableFurnitureRecipe extends Comparable<CraftableFurnitureRecipe> {
        List<Ingredient> getIngredients();
        ItemStack getResult(RegistryWrapper.WrapperLookup registryManager);
        ItemStack craft(FurnitureRecipe.FurnitureRecipeInput inventory, RegistryWrapper.WrapperLookup registryManager);
        boolean matches(FurnitureRecipe.FurnitureRecipeInput playerInventory, World world);
        FurnitureRecipe parent();
        ItemStack getRecipeOuput();

        @Override
        default int compareTo(@NotNull FurnitureRecipe.CraftableFurnitureRecipe o) {
            return getRecipeOuput().toString().compareTo(o.getRecipeOuput().toString());
        }

        default ItemStack craftAndRemoveItems(FurnitureRecipe.FurnitureRecipeInput input, RegistryWrapper.WrapperLookup registryManager) {
            ItemStack output = getResult(registryManager).copy();
            List<Ingredient> ingredients = getIngredients();
            PlayerInventory playerInventory = input.playerInventory();
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
    public static record FurnitureRecipeInput(PlayerInventory playerInventory) implements RecipeInput {

        @Override
        public ItemStack getStackInSlot(int slot) {
            return playerInventory.getStack(slot);
        }

        @Override
        public int size() {
            return playerInventory.size();
        }

        @Override
        public boolean isEmpty() {
            return playerInventory.isEmpty();
        }
    }
}
