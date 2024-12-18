package com.unlikepaladin.pfm.compat.emi;

import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.recipes.FurnitureRecipe;
import com.unlikepaladin.pfm.registry.ScreenHandlerIDs;
import dev.emi.emi.api.recipe.EmiCraftingRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.Identifier;

import java.util.*;
/*
public class EmiFurnitureRecipe extends EmiCraftingRecipe {

    public EmiFurnitureRecipe(RecipeEntry<FurnitureRecipe> entry) {
        super(padIngredients(entry.value()), EmiStack.of(entry.value().getResult(MinecraftClient.getInstance().world.getRegistryManager())),
                entry.id(), false);
        for (int i = 0; i < input.size(); i++) {
            PlayerInventory playerInventory;
            if (PaladinFurnitureMod.isClient) {
                playerInventory = MinecraftClient.getInstance().player.getInventory();
            } else {
                playerInventory = new PlayerInventory(null);
            }
            CraftingInventory inv = new CraftingInventory(new ScreenHandler(ScreenHandlerIDs.WORKBENCH_SCREEN_HANDLER, -1) {

                @Override
                public boolean canUse(PlayerEntity player) {
                    return false;
                }

                @Override
                public ItemStack quickMove(PlayerEntity player, int index) {
                    return null;
                }
            }, 3, 3);
            for (int j = 0; j < input.size(); j++) {
                if (j == i) {
                    continue;
                }
                if (!input.get(j).isEmpty()) {
                    inv.setStack(j, input.get(j).getEmiStacks().get(0).getItemStack().copy());
                }
            }
            List<EmiStack> stacks = input.get(i).getEmiStacks();
            for (EmiStack stack : stacks) {
                inv.setStack(i, stack.getItemStack().copy());
                ItemStack remainder = entry.value().getRemainder(new FurnitureRecipe.FurnitureRecipeInput(playerInventory)).get(i);
                if (!remainder.isEmpty()) {
                    stack.setRemainder(EmiStack.of(remainder));
                }
            }
        }
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return PaladinFurnitureModEMIPlugin.FURNITURE;
    }

    @Override
    public Identifier getId() {
        return super.getId();
    }

    private static List<EmiIngredient> padIngredients(FurnitureRecipe recipe) {
        List<Ingredient> ingredientsList = recipe.getIngredients();
        HashMap<Item, Integer> containedItems = new LinkedHashMap<>();
        for (Ingredient ingredient : ingredientsList) {
            for (ItemStack stack : ingredient.getMatchingStacks()) {
                if (!containedItems.containsKey(stack.getItem())) {
                    containedItems.put(stack.getItem(), 1);
                } else {
                    containedItems.put(stack.getItem(), containedItems.get(stack.getItem()) + 1);
                }
            }
        }
        List<EmiIngredient> finalList = new ArrayList<>();
        for (Map.Entry<Item, Integer> entry: containedItems.entrySet()) {
            finalList.add(EmiIngredient.of(Ingredient.ofStacks(new ItemStack(entry.getKey(), entry.getValue())), entry.getValue()));
        }
        finalList.sort(Comparator.comparing(o -> o.getEmiStacks().get(0).getItemStack().getItem().toString()));
        return finalList;
    }
}*/