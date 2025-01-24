package com.unlikepaladin.pfm.compat.emi;

import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.recipes.FurnitureRecipe;
import com.unlikepaladin.pfm.registry.ScreenHandlerIDs;
import dev.emi.emi.api.EmiApi;
import dev.emi.emi.api.recipe.EmiCraftingRecipe;
import dev.emi.emi.api.recipe.EmiPatternCraftingRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.GeneratedSlotWidget;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.Identifier;

import java.util.*;
import java.util.stream.Collectors;

public class EmiFurnitureRecipe extends EmiPatternCraftingRecipe {

    private final FurnitureRecipe recipe;
    public EmiFurnitureRecipe(FurnitureRecipe recipe) {
        super(padIngredients(recipe), EmiStack.EMPTY,
                recipe.getId());
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
                ItemStack remainder = recipe.getRemainder(playerInventory).get(i);
                if (!remainder.isEmpty()) {
                    stack.setRemainder(EmiStack.of(remainder));
                }
            }
        }
        this.recipe = recipe;
    }

    @Override
    public List<EmiStack> getOutputs() {
        return getOutputEntries(this.recipe);
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return PaladinFurnitureModEMIPlugin.FURNITURE;
    }

    @Override
    public Identifier getId() {
        return super.getId();
    }

    private static final Map<FurnitureRecipe, List<EmiStack>> outputs = new HashMap<>();
    public static List<EmiStack> getOutputEntries(FurnitureRecipe recipe) {
        if (!outputs.containsKey(recipe))
            outputs.put(recipe, recipe.getInnerRecipes().stream().map(FurnitureRecipe.CraftableFurnitureRecipe::getOutput).map(EmiStack::of).collect(Collectors.toList()));
        return outputs.get(recipe);
    }

    static Map<ItemStack, List<ItemStack>> itemStackListMap = new HashMap<>();
    public static List<ItemStack> collectIngredientsFromRecipe(FurnitureRecipe.CraftableFurnitureRecipe recipe) {
        if (itemStackListMap.containsKey(recipe.getOutput())) return itemStackListMap.get(recipe.getOutput());

        List<Ingredient> ingredients = recipe.getIngredients();
        HashMap<Item, Integer> containedItems = new HashMap<>();
        for (Ingredient ingredient : ingredients) {
            for (ItemStack stack : ingredient.getMatchingStacks()) {
                if (!containedItems.containsKey(stack.getItem())) {
                    containedItems.put(stack.getItem(), stack.getCount());
                } else {
                    containedItems.put(stack.getItem(), containedItems.get(stack.getItem()) + stack.getCount());
                }
            }
        }
        List<ItemStack> listOfList = new ArrayList<>();
        for (Map.Entry<Item, Integer> entry: containedItems.entrySet()) {
            listOfList.add(new ItemStack(entry.getKey(), entry.getValue()));
        }
        if (listOfList.size() != recipe.parent().getMaxInnerRecipeSize()) {
            while (listOfList.size() != recipe.parent().getMaxInnerRecipeSize()) {
                // this is sadly necessary
                listOfList.add(ItemStack.EMPTY);
            }
        }

        itemStackListMap.put(recipe.getOutput(), listOfList);
        return listOfList;
    }

    // Required so that ingredients show up in usages correctly
    private static List<EmiIngredient> padIngredients(FurnitureRecipe recipe) {
        List<List<ItemStack>> ingredients = new ArrayList<>(recipe.getMaxInnerRecipeSize());
        for (FurnitureRecipe.CraftableFurnitureRecipe innerRecipe: recipe.getInnerRecipes()) {
            List<ItemStack> finalList = collectIngredientsFromRecipe(innerRecipe);
            for (int i = 0; i < finalList.size(); i++) {
                ItemStack stack = finalList.get(i);
                if (ingredients.size() <= i) {
                    ingredients.add(new ArrayList<>());
                }
                ingredients.get(i).add(stack);
            }
        }
        List<EmiIngredient> finalList = new ArrayList<>();
        for (List<ItemStack> ingredientList : ingredients) {
            finalList.add(EmiIngredient.of(Ingredient.ofStacks(ingredientList.stream())));
        }
        return finalList;
    }

    @Override
    public SlotWidget getInputWidget(int slot, int x, int y) {
        return new GeneratedSlotWidget((r -> {
            int selectedRecipe = r.nextInt(recipe.getInnerRecipes().size());
            List<ItemStack> ingredients = collectIngredientsFromRecipe(recipe.getInnerRecipes().get(selectedRecipe));
            if (ingredients.size() > slot) {
                return EmiIngredient.of(Ingredient.ofStacks(ingredients.get(slot)), ingredients.get(slot).getCount());
            } else {
                return EmiStack.EMPTY;
            }
        }), unique, x, y);
    }

    @Override
    public SlotWidget getOutputWidget(int x, int y) {
        return new GeneratedSlotWidget((r -> {
            int selectedRecipe = r.nextInt(recipe.getInnerRecipes().size());
            return EmiIngredient.of(Ingredient.ofStacks(recipe.getInnerRecipes().get(selectedRecipe).getOutput()), recipe.getOutputCount());
        }), unique, x, y);
    }
}