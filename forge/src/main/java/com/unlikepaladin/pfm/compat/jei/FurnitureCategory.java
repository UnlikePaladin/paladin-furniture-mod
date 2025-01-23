package com.unlikepaladin.pfm.compat.jei;

import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.recipes.FurnitureRecipe;
import com.unlikepaladin.pfm.registry.PaladinFurnitureModBlocksItems;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class FurnitureCategory implements IRecipeCategory<FurnitureRecipe> {
    private final IDrawable BACKGROUND;
    public static final Identifier TEXTURE_GUI_VANILLA = new Identifier("pfm:textures/gui/gui_jei.png");
    public final IDrawable ICON;
    public static final TranslatableText TITLE = new TranslatableText("rei.pfm.furniture");
    private final ICraftingGridHelper craftingGridHelper;
    private static final int craftOutputSlot = 9;
    private static final int craftInputSlot1 = 0;
    private int itemsPerInnerRecipe;

    public FurnitureCategory(IGuiHelper guiHelper) {
        ICON = guiHelper.createDrawableIngredient(new ItemStack(PaladinFurnitureModBlocksItems.WORKING_TABLE));
        this.BACKGROUND = guiHelper.createDrawable(TEXTURE_GUI_VANILLA, 0, 60, 116, 54);
        craftingGridHelper = guiHelper.createCraftingGridHelper(craftInputSlot1);
    }
    public static final Identifier IDENTIFIER = new Identifier(PaladinFurnitureMod.MOD_ID, "furniture");

    @Override
    public @NotNull Identifier getUid() {
        return IDENTIFIER;
    }

    @Override
    public @NotNull Class<FurnitureRecipe> getRecipeClass() {
        return FurnitureRecipe.class;
    }

    @Override
    public Text getTitle() {
        return TITLE;
    }

    @Override
    public IDrawable getBackground() {
        return BACKGROUND;
    }

    @Override
    public IDrawable getIcon() {
        return ICON;
    }

    Map<FurnitureRecipe, List<ItemStack>> inputCache = new HashMap<>();
    @Override
    public void setIngredients(FurnitureRecipe recipe, IIngredients iingredients) {
        this.itemsPerInnerRecipe = recipe.getMaxInnerRecipeSize();
        if (!inputCache.containsKey(recipe)) {
            List<ItemStack> inputEntries = new ArrayList<>();

            // needed for registration apparently or something
            for (FurnitureRecipe.CraftableFurnitureRecipe innerRecipe: recipe.getInnerRecipes()) {
                List<List<ItemStack>> finalList = collectIngredientsFromRecipe(innerRecipe);
                finalList.forEach(inputEntries::addAll);
            }
            inputCache.put(recipe, inputEntries);
        }

        iingredients.setInputs(VanillaTypes.ITEM, inputCache.get(recipe));
        iingredients.setOutputs(VanillaTypes.ITEM, getOutputEntries(recipe));
    }

    Map<ItemStack, List<List<ItemStack>>> itemStackListMap = new HashMap<>();
    public List<List<ItemStack>> collectIngredientsFromRecipe(FurnitureRecipe.CraftableFurnitureRecipe recipe) {
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
        List<List<ItemStack>> listOfList = new ArrayList<>();
        for (Map.Entry<Item, Integer> entry: containedItems.entrySet()) {
            listOfList.add(List.of(new ItemStack(entry.getKey(), entry.getValue())));
        }
        if (listOfList.size() != itemsPerInnerRecipe) {
            while (listOfList.size() != itemsPerInnerRecipe) {
                // this is sadly necessary
                listOfList.add(List.of());
            }
        }

        itemStackListMap.put(recipe.getOutput(), listOfList);
        return listOfList;
    }

    private final Map<FurnitureRecipe, List<ItemStack>> outputs = new HashMap<>();
    public List<ItemStack> getOutputEntries(FurnitureRecipe recipe) {
        if (!outputs.containsKey(recipe))
            outputs.put(recipe, recipe.getInnerRecipes().stream().map(FurnitureRecipe.CraftableFurnitureRecipe::getOutput).toList());
        return outputs.get(recipe);
    }

    Map<ItemStack, ItemStack> focusToOutput= new HashMap<>();
    Map<FurnitureRecipe, List<List<ItemStack>>> cachedInput = new HashMap<>();
    Map<FurnitureRecipe, List<ItemStack>> cachedOutput = new HashMap<>();
    @Override
    public void setRecipe(IRecipeLayout recipeLayout, @NotNull FurnitureRecipe recipe, @NotNull IIngredients ingredients) {
        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
        guiItemStacks.init(craftOutputSlot, false, 94, 18);
        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 3; ++x) {
                int index = craftInputSlot1 + x + (y * 3);
                guiItemStacks.init(index, true, x * 18, y * 18);
            }
        }
        IFocus<ItemStack> focused = recipeLayout.getFocus(VanillaTypes.ITEM);
        List<List<ItemStack>> inputs;
        List<ItemStack> output;
        if (focused != null && focused.getMode() == IFocus.Mode.OUTPUT) {
            // cache focus
            if (!focusToOutput.containsKey(focused.getValue())) {
                boolean broke = false;
                for (List<ItemStack> listOfOutputs : ingredients.getOutputs(VanillaTypes.ITEM)) {
                    for (ItemStack stack : listOfOutputs) {
                        if (ItemStack.canCombine(stack, focused.getValue())) {
                            focusToOutput.put(focused.getValue(), stack);
                            broke = true;
                            break;
                        }
                    }
                    if (broke) break;
                }
            }
            output = List.of(focusToOutput.get(focused.getValue()));
            inputs = collectIngredientsFromRecipe(recipe.getInnerRecipeFromOutput(focusToOutput.get(focused.getValue())));
            guiItemStacks.set(craftOutputSlot, output);
            craftingGridHelper.setInputs(guiItemStacks, inputs);
        } else {
            if (!cachedOutput.containsKey(recipe)) {
                List<List<ItemStack>> outputs = ingredients.getOutputs(VanillaTypes.ITEM);
                List<ItemStack> finalOutput = new ArrayList<>();
                for (List<ItemStack> list : outputs) {
                    finalOutput.addAll(list);
                }
                cachedOutput.put(recipe, finalOutput);
            }
            guiItemStacks.set(craftOutputSlot, cachedOutput.get(recipe));

            if (!cachedInput.containsKey(recipe)) {
                List<List<ItemStack>> finalInput = new ArrayList<>(this.itemsPerInnerRecipe);
                for (int i = 0; i < itemsPerInnerRecipe; i++)
                    finalInput.add(new ArrayList<>());

                for (FurnitureRecipe.CraftableFurnitureRecipe inner : recipe.getInnerRecipes()) {
                    List<List<ItemStack>> stsk = collectIngredientsFromRecipe(inner);
                    for (int i = 0; i < stsk.size(); i++) {
                        // add to the right slot by adding to the right list
                        finalInput.get(i % this.itemsPerInnerRecipe).addAll(stsk.get(i));
                    }
                }
                cachedInput.put(recipe, finalInput);
            }

            craftingGridHelper.setInputs(guiItemStacks, cachedInput.get(recipe));
        }
        recipeLayout.setShapeless();

    }



}
