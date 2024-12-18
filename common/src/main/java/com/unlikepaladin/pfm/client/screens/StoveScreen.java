package com.unlikepaladin.pfm.client.screens;

import com.unlikepaladin.pfm.menus.StoveScreenHandler;
import net.minecraft.client.gui.screen.ingame.AbstractFurnaceScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.recipebook.RecipeBookType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeBookCategories;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

public class StoveScreen extends AbstractFurnaceScreen<StoveScreenHandler> {
    // You can replace the background with whatever you like, just remember there will always be the recipe book button
    private static final Identifier BACKGROUND = Identifier.of("textures/gui/container/smoker.png");
    private static final Identifier LIT_PROGRESS_TEXTURE = Identifier.of("container/smoker/lit_progress");
    private static final Identifier BURN_PROGRESS_TEXTURE = Identifier.of("container/smoker/burn_progress");
    private static final Text TOGGLE_SMOKABLE_TEXT = Text.translatable("gui.recipebook.toggleRecipes.smokable");
    private static final List<RecipeBookWidget.Tab> TABS = List.of(
            new RecipeBookWidget.Tab(RecipeBookType.SMOKER), new RecipeBookWidget.Tab(Items.PORKCHOP, RecipeBookCategories.SMOKER_FOOD)
    );

    public StoveScreen(StoveScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title, TOGGLE_SMOKABLE_TEXT, BACKGROUND, LIT_PROGRESS_TEXTURE, BURN_PROGRESS_TEXTURE, TABS);
    }
}
