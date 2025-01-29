package com.unlikepaladin.pfm.recipes;

import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.registry.RecipeTypes;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface FurnitureRecipe extends Recipe<FurnitureRecipe.FurnitureRecipeInput> {
    @Override
    default RecipeType<? extends Recipe<FurnitureRecipeInput>> getType() {
        return RecipeTypes.FURNITURE_RECIPE;
    }

    List<CraftableFurnitureRecipe> getInnerRecipes(FeatureSet features);

    String outputClass();

    default List<CraftableFurnitureRecipe> getAvailableOutputs(FurnitureRecipe.FurnitureRecipeInput inventory, RegistryWrapper.WrapperLookup registryManager) {
        return getInnerRecipes(inventory.playerInventory.player.getWorld().getEnabledFeatures());
    }

    static int getSlotWithStackIgnoreNBT(PlayerInventory inventory, Item item) {
        for(int i = 0; i < inventory.main.size(); ++i) {
            if (!inventory.main.get(i).isEmpty() && inventory.main.get(i).getItem() == item) {
                return i;
            }
        }
        return -1;
    }

    default int getMaxInnerRecipeSize() {
        return getIngredientPlacement().getIngredients().size();
    }

    default int getOutputCount(RegistryWrapper.WrapperLookup registryManager) {
        return getResult(registryManager).getCount();
    }

    ItemStack getResult(RegistryWrapper.WrapperLookup registryManager);

    default List<? extends CraftableFurnitureRecipe> getInnerRecipesForVariant(World world, Identifier identifier) {
        return Collections.singletonList(getInnerRecipes(world.getEnabledFeatures()).getFirst());
    }

    default String getName(RegistryWrapper.WrapperLookup registryManager) {
        return getResult(registryManager).getName().getString();
    }

    void write(RegistryByteBuf buf);

    default boolean enabled(World world) {
        for (CraftableFurnitureRecipe recipe : getInnerRecipes(world.getEnabledFeatures())) {
            if (!recipe.isInnerEnabled(world.getEnabledFeatures()))
                return false;
        }
        return true;
    }

    default List<Ingredient> getIngredients(World world) {
        return getIngredientPlacement().getIngredients();
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

        default boolean isInnerEnabled(FeatureSet featureSet) {
            if (!this.getRecipeOuput().isItemEnabled(featureSet))
                return false;
            for (Ingredient ingredient : this.getIngredients()) {
                for (RegistryEntry<Item> item : ingredient.getMatchingItems().toList()) {
                    if (!item.value().isEnabled(featureSet))
                        return false;
                }
            }
            return true;
        }

        default ItemStack craftAndRemoveItems(FurnitureRecipe.FurnitureRecipeInput input, RegistryWrapper.WrapperLookup registryManager) {
            ItemStack output = getResult(registryManager).copy();
            List<Ingredient> ingredients = getIngredients();
            PlayerInventory playerInventory = input.playerInventory();
            Map<Item, Integer> ingredientCounts = getItemCounts();
            for (Map.Entry<Item, Integer> entry : ingredientCounts.entrySet()) {
                Item item = entry.getKey();
                Integer count = entry.getValue();

                int indexOfStack = FurnitureRecipe.getSlotWithStackIgnoreNBT(playerInventory, item);
                if (indexOfStack != -1) {
                    if (playerInventory.getStack(indexOfStack).getCount() >= count) {
                        ItemStack stack1 = playerInventory.getStack(indexOfStack);
                        stack1.decrement(count);
                        playerInventory.setStack(indexOfStack, stack1);
                        playerInventory.markDirty();
                    } else {
                        int remainingCount = count - playerInventory.getStack(indexOfStack).getCount();
                        playerInventory.setStack(indexOfStack, ItemStack.EMPTY);
                        while (remainingCount > 0) {
                            indexOfStack = FurnitureRecipe.getSlotWithStackIgnoreNBT(playerInventory, item);
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
            return output;
        }

        default Map<Item, Integer> getItemCounts() {
            Map<Item, Integer> ingredientCounts = new HashMap<>();
            for (Ingredient ingredient : this.getIngredients()) {
                for (RegistryEntry<Item> itemRegistryEntry : ingredient.getMatchingItems()) {
                    if (ingredientCounts.containsKey(itemRegistryEntry.value())) {
                        ingredientCounts.put(itemRegistryEntry.value(), ingredientCounts.get(itemRegistryEntry.value())+1);
                    } else {
                        ingredientCounts.put(itemRegistryEntry.value(), 1);
                    }
                }
            }
            return ingredientCounts;
        }

    }
    record FurnitureRecipeInput(PlayerInventory playerInventory) implements RecipeInput {

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
