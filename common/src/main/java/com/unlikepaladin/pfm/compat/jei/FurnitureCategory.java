package com.unlikepaladin.pfm.compat.jei;
/*
import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.recipes.FurnitureRecipe;
import com.unlikepaladin.pfm.registry.PaladinFurnitureModBlocksItems;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class FurnitureCategory implements IRecipeCategory<FurnitureRecipe> {
    private final IDrawable BACKGROUND;
    public static final Identifier TEXTURE_GUI_VANILLA = Identifier.of("pfm:textures/gui/gui_jei.png");
    public final IDrawable ICON;
    public static final Text TITLE = Text.translatable("rei.pfm.furniture");
    private final ICraftingGridHelper craftingGridHelper;
    private static final int craftOutputSlot = 9;
    private static final int craftInputSlot1 = 0;

    public FurnitureCategory(IGuiHelper guiHelper) {
        ICON = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(PaladinFurnitureModBlocksItems.WORKING_TABLE));
        this.BACKGROUND = guiHelper.createDrawable(TEXTURE_GUI_VANILLA, 0, 60, 116, 54);
        craftingGridHelper = guiHelper.createCraftingGridHelper();
    }
    public static final Identifier IDENTIFIER = Identifier.of(PaladinFurnitureMod.MOD_ID, "crafting");

    @Override
    public RecipeType<FurnitureRecipe> getRecipeType() {
        return PaladinFurnitureModJEI.FURNITURE_RECIPE;
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
    public void setRecipe(IRecipeLayoutBuilder builder, FurnitureRecipe recipe, IFocusGroup focuses) {
        List<Ingredient> ingredientsList = recipe.getIngredients();
        HashMap<Item, Integer> containedItems = new LinkedHashMap<>();
        for (Ingredient ingredient : ingredientsList) {
            for (RegistryEntry<Item> itemRegistryEntry : ingredient.getMatchingItems().toList()) {
                if (!containedItems.containsKey(itemRegistryEntry.value())) {
                    containedItems.put(itemRegistryEntry.value(), 1);
                } else {
                    containedItems.put(itemRegistryEntry.value(), containedItems.get(itemRegistryEntry.value()) + 1);
                }
            }
        }
        List<Ingredient> finalList = new ArrayList<>();
        for (Map.Entry<Item, Integer> entry: containedItems.entrySet()) {
            finalList.add(Ingredient.ofItem(entry.getKey()));
        }
        finalList.sort(Comparator.comparing(o -> o.getMatchingItems().toList().get(0).toString()));
        List<List<ItemStack>> inputLists = new ArrayList<>();
        for (Ingredient input : finalList) {
            List<RegistryEntry<Item>> stacks = input.getMatchingItems().toList();
          //  List<ItemStack> expandedInput = List.of(stacks);
         //   inputLists.add(expandedInput);
        }
       //ItemStack resultItem = recipe.getResult(MinecraftClient.getInstance().world.getRegistryManager());

        int width = getSize(inputLists.size());
        int height = width;
      //  craftingGridHelper.createAndSetOutputs(builder, VanillaTypes.ITEM_STACK, List.of(resultItem));
        craftingGridHelper.createAndSetInputs(builder, VanillaTypes.ITEM_STACK, inputLists, width, height);
    }

    private static int getSize(int total) {
        if (total > 4) {
            return 3;
        } else if (total > 1) {
            return 2;
        } else {
            return 1;
        }
    }
}
*/