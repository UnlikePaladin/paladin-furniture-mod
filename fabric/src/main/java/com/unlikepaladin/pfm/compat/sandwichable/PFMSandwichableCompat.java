package com.unlikepaladin.pfm.compat.sandwichable;

import com.unlikepaladin.pfm.blocks.blockentities.PFMToasterBlockEntity;
import io.github.foundationgames.sandwichable.Sandwichable;
import io.github.foundationgames.sandwichable.blocks.BlocksRegistry;
import io.github.foundationgames.sandwichable.items.ItemsRegistry;
import io.github.foundationgames.sandwichable.recipe.ToastingRecipe;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CampfireCookingRecipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;

public class PFMSandwichableCompat {

    public static void toastSandwich(PFMToasterBlockEntity pfmToasterBlockEntity) {
       /* World world = pfmToasterBlockEntity.getWorld();
        List<ItemStack> items = pfmToasterBlockEntity.getItems();
        for (int i = 0; i < 2; i++) {
            SimpleInventory inv = new SimpleInventory(pfmToasterBlockEntity.getItems().get(i));
            // had to disable this as it errored, until sandwichable is updated
         //   Optional<RecipeEntry<ToastingRecipe>> match = world.getRecipeManager().getFirstMatch(ToastingRecipe.Type.INSTANCE, new SingleStackRecipeInput(inv.getStack(0)), world);
            Optional<RecipeEntry<CampfireCookingRecipe>> match = world.getRecipeManager().getFirstMatch(RecipeType.CAMPFIRE_COOKING, new SingleStackRecipeInput(items.get(i)), world);

            boolean changed = false;
            if(match.isPresent()) {
                pfmToasterBlockEntity.setCurrentItem(i, match.get().value().getResult(world.getRegistryManager()).copy());
                changed = true;
            } else {
                if(items.get(i).contains(DataComponentTypes.FOOD)) {
                    Item item = items.get(i).isIn(Sandwichable.SMALL_FOODS) ? ItemsRegistry.BURNT_MORSEL : ItemsRegistry.BURNT_FOOD;
                    items.set(i, new ItemStack(item, 1));
                    changed = true;
                }
            }

            if (!world.isClient() && changed) {
                ItemStack advStack = items.get(i);
                pfmToasterBlockEntity.getLastUser().ifPresent(player -> {
                    if (player instanceof ServerPlayerEntity) {
                        Sandwichable.TOAST_ITEM.trigger((ServerPlayerEntity) player, advStack);
                    }
                });
            }
        }*/
    }

    public static boolean isMetal(ItemStack stack) {
        return stack.isIn(Sandwichable.METAL_ITEMS);
    }

    public static boolean isSandwich(ItemStack stack) {
        return stack.getItem().equals(BlocksRegistry.SANDWICH.asItem());
    }
}
