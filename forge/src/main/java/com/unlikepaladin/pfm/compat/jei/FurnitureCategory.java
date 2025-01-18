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

    @Override
    public void setIngredients(FurnitureRecipe recipe, IIngredients ingredients) {
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
            List<Ingredient> finalList = new ArrayList<>();
            for (Map.Entry<Item, Integer> entry: containedItems.entrySet()) {
                finalList.add(Ingredient.ofStacks(new ItemStack(entry.getKey(), entry.getValue())));
            }
        finalList.sort(Comparator.comparing(o -> o.getMatchingStacks()[0].getItem().toString()));

        ingredients.setInputIngredients(finalList);
        ingredients.setOutput(VanillaTypes.ITEM, recipe.getOutput());
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, FurnitureRecipe recipe, IIngredients ingredients) {
        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
        guiItemStacks.init(craftOutputSlot, false, 94, 18);
        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 3; ++x) {
                int index = craftInputSlot1 + x + (y * 3);
                guiItemStacks.init(index, true, x * 18, y * 18);
            }
        }

        List<List<ItemStack>> inputs = ingredients.getInputs(VanillaTypes.ITEM);
        List<List<ItemStack>> outputs = ingredients.getOutputs(VanillaTypes.ITEM);
        craftingGridHelper.setInputs(guiItemStacks, inputs);
        recipeLayout.setShapeless();

        guiItemStacks.set(craftOutputSlot, outputs.get(0));
    }

}
