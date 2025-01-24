package com.unlikepaladin.pfm.compat.rei;

import com.google.common.collect.Lists;
import com.unlikepaladin.pfm.registry.PaladinFurnitureModBlocksItems;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Slot;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.DisplayMerger;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class FurnitureCategory implements DisplayCategory<FurnitureDisplay> {
    public static final EntryStack<ItemStack> ICON = EntryStacks.of(PaladinFurnitureModBlocksItems.WORKING_TABLE);
    public static final Text TITLE = Text.translatable("rei.pfm.furniture");

    public CategoryIdentifier<? extends FurnitureDisplay> getCategoryIdentifier() {
        return FurnitureDisplay.IDENTIFIER;
    }

    @Override
    public Renderer getIcon() {
        return ICON;
    }

    @Override
    public Text getTitle() {
        return TITLE;
    }

    @Override
    public List<Widget> setupDisplay(FurnitureDisplay display, Rectangle bounds) {
        Map<EntryStack<?>, Integer> stackToSlotIndex = new HashMap<>();
        Point startPoint = new Point(bounds.getCenterX() - 58, bounds.getCenterY() - 27);
        List<Widget> widgets = Lists.newArrayList();
        widgets.add(Widgets.createRecipeBase(bounds));
        widgets.add(Widgets.createArrow(new Point(startPoint.x + 60, startPoint.y + 18)));
        widgets.add(Widgets.createResultSlotBackground(new Point(startPoint.x + 95, startPoint.y + 19)));
        List<EntryIngredient> input = display.getInputEntries();

        Slot output = Widgets.createSlot(new Point(startPoint.x + 95, startPoint.y + 19)).disableBackground().markOutput();
        int recipeIndex = 0;
        for (EntryIngredient ingredient : display.getOutputEntries()) {
            output.entries(ingredient);
            stackToSlotIndex.put(ingredient.get(0), recipeIndex);
            recipeIndex++;
        }

        widgets.add(output);

        List<Slot> slots = Lists.newArrayList();
        int innerRecipeSize = display.itemsPerInnerRecipe();
        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 3; ++x) {
                Slot inputSlot = Widgets.createSlot(new Point(startPoint.x + 1 + x * 18, startPoint.y + 1 + y * 18)).markInput();
                slots.add(inputSlot);
            }
        }

        for (int i = 0; i < innerRecipeSize; i++) {
            int finalSlotIndex = i;
            // many thanks to shedaniel for helping me figure this out :)
            widgets.add(Widgets.createDrawableWidget((graphics, matrixStack, mouseX, mouseY, delta) -> {
                Integer currentRecipe = stackToSlotIndex.get(output.getCurrentEntry());
                if (currentRecipe != null && input.size() > finalSlotIndex+(currentRecipe*innerRecipeSize)) {
                    slots.get(finalSlotIndex).clearEntries().entries(input.get(finalSlotIndex+(currentRecipe*innerRecipeSize)));
                }
            }));
        }
        widgets.addAll(slots);
        return widgets;
    }

    @Nullable
    public DisplayMerger<FurnitureDisplay> getDisplayMerger() {
        return DisplayCategory.getContentMerger();
    }
}
