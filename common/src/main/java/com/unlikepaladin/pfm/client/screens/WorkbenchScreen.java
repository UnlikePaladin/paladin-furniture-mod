package com.unlikepaladin.pfm.client.screens;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import com.unlikepaladin.pfm.menus.WorkbenchScreenHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.search.SearchManager;
import net.minecraft.client.search.SearchableContainer;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.screen.StonecutterScreenHandler;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagGroup;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

import java.util.*;
import java.util.function.Predicate;

public class WorkbenchScreen extends HandledScreen<WorkbenchScreenHandler> {
    private static final Identifier TEXTURE = new Identifier("pfm:textures/gui/container/working_table.png");
    private static final int SCROLLBAR_WIDTH = 12;
    private static final int SCROLLBAR_HEIGHT = 15;
    private static final int RECIPE_LIST_COLUMNS = 6;
    private static final int RECIPE_LIST_ROWS = 3;
    private static final int RECIPE_ENTRY_WIDTH = 16;
    private static final int RECIPE_ENTRY_HEIGHT = 18;
    private static final int SCROLLBAR_AREA_HEIGHT = 54;
    private static final int RECIPE_LIST_OFFSET_X = 20;
    private static final int RECIPE_LIST_OFFSET_Y = 30;
    private float scrollAmount;
    private boolean mouseClicked;
    private int scrollOffset;
    private boolean canCraft;
    private TextFieldWidget searchBox;

    public WorkbenchScreen(WorkbenchScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        handler.setContentsChangedListener(this::onInventoryChange);
        this.canCraft = handler.canCraft();
    }

    @Override
    protected void init() {
        super.init();
        this.searchBox = new TextFieldWidget(this.textRenderer, this.x + 20, this.y + 18, 110, this.textRenderer.fontHeight, new TranslatableText("itemGroup.search"));
        this.searchBox.setMaxLength(50);
        this.searchBox.setDrawsBackground(false);
        this.searchBox.setVisible(true);
        this.searchBox.setEditableColor(0xFFFFFF);
        this.addButton(this.searchBox);
        this.backgroundHeight = 180;
        this.backgroundWidth = 176;
        this.playerInventoryTitleY = this.backgroundHeight - 92;
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        String string = this.searchBox.getText();
        this.init(client, width, height);
        this.searchBox.setText(string);
        if (!this.searchBox.getText().isEmpty()) {
            this.search();
        }
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        String string = this.searchBox.getText();
        if (this.searchBox.charTyped(chr, modifiers)) {
            if (!Objects.equals(string, this.searchBox.getText())) {
                this.search();
            }
            return true;
        }
        return super.charTyped(chr, modifiers);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        boolean bl2 = InputUtil.fromKeyCode(keyCode, scanCode).method_30103().isPresent();
        if (bl2 && this.handleHotbarKeyPressed(keyCode, scanCode)) {
            return true;
        }
        String string = this.searchBox.getText();
        if (this.searchBox.keyPressed(keyCode, scanCode, modifiers)) {
            if (!Objects.equals(string, this.searchBox.getText())) {
                this.search();
            }
            return true;
        }
        if (this.searchBox.isFocused() && this.searchBox.isVisible() && keyCode != GLFW.GLFW_KEY_ESCAPE) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    private final Map<Identifier, Tag<Item>> searchResultTags = Maps.newTreeMap();

    private void search() {
        this.handler.getSearchableRecipes().clear();
        this.searchResultTags.clear();
        String string = this.searchBox.getText();
        if (string.isEmpty()) {
            this.handler.updateInput();
            this.handler.searching = false;
        } else {
            this.handler.updateInput();
            SearchableContainer<ItemStack> searchable;
            if (string.startsWith("#")) {
                string = string.substring(1);
                searchable = this.client.getSearchableContainer(SearchManager.ITEM_TAG);
                this.searchForTags(string);
            } else {
                searchable = this.client.getSearchableContainer(SearchManager.ITEM_TOOLTIP);
            }
            List<Item> items = new ArrayList<>();
            searchable.findAll(string.toLowerCase(Locale.ROOT)).forEach(itemStack -> items.add(itemStack.getItem()));
            this.handler.getSortedRecipes().forEach(furnitureRecipe -> {
                if (items.contains(furnitureRecipe.getOutput().getItem())) {
                    this.handler.getSearchableRecipes().add(furnitureRecipe);
                }
            });
            this.handler.searching = true;
        }
        this.scrollAmount = 0.0f;
        this.scrollOffset = 0;
    }

    private void searchForTags(String id2) {
        Predicate<Identifier> predicate;
        int i = id2.indexOf(58);
        if (i == -1) {
            predicate = id -> id.getPath().contains(id2);
        } else {
            String string = id2.substring(0, i).trim();
            String string2 = id2.substring(i + 1).trim();
            predicate = id -> id.getNamespace().contains(string) && id.getPath().contains(string2);
        }
        TagGroup<Item> tagGroup = ItemTags.getTagGroup();
        tagGroup.getTagIds().stream().filter(predicate).forEach(id -> this.searchResultTags.put(id, tagGroup.getTag(id)));
    }

    @Override
    public void tick() {
        super.tick();
        if (this.searchBox != null) {
            this.searchBox.tick();
        }
    }
    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        this.renderBackground(matrices);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.client.getTextureManager().bindTexture(TEXTURE);
        int x = this.x;
        int y = this.y;
        this.drawTexture(matrices, x, y, 0, 0, this.backgroundWidth, this.backgroundHeight);
        int k = (int)(41.0f * this.scrollAmount);
        this.drawTexture(matrices, x + 119, y + 31 + k, 176 + (this.shouldScroll() ? 0 : 12), 0, SCROLLBAR_WIDTH, SCROLLBAR_HEIGHT);
        int xOffSetForIcons = this.x + RECIPE_LIST_OFFSET_X;
        int yOffsetForIcons = this.y + RECIPE_LIST_OFFSET_Y;
        int scrollOffsetForIcons = this.scrollOffset + 18;
        this.renderRecipeBackground(matrices, mouseX, mouseY, xOffSetForIcons, yOffsetForIcons, scrollOffsetForIcons);
        this.renderRecipeIcons(xOffSetForIcons, yOffsetForIcons, scrollOffsetForIcons);
        this.searchBox.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    protected void drawMouseoverTooltip(MatrixStack matrices, int x, int y) {
        super.drawMouseoverTooltip(matrices, x, y);
        int xOffsetForTooltip = this.x + RECIPE_LIST_OFFSET_X;
        int yOffsetForTooltip = this.y + RECIPE_LIST_OFFSET_Y;
        int scrollOffsetForTooltip = this.scrollOffset + 18;
        for (int recipeIndex = this.scrollOffset; recipeIndex < scrollOffsetForTooltip && recipeIndex < this.handler.getVisibleRecipeCount(); ++recipeIndex) {
            int m = recipeIndex - this.scrollOffset;
            int n = xOffsetForTooltip + m % RECIPE_LIST_COLUMNS * RECIPE_ENTRY_WIDTH;
            int o = yOffsetForTooltip + m / RECIPE_LIST_COLUMNS * RECIPE_ENTRY_HEIGHT + 2;
            if (x < n || x >= n + RECIPE_ENTRY_WIDTH || y < o || y >= o + RECIPE_ENTRY_HEIGHT) continue;
            List<Text> tooltip = new ArrayList<>();
            int iCopy = recipeIndex;
            if (this.handler.searching) {
                iCopy = this.handler.getSortedRecipes().indexOf(this.handler.getSearchableRecipes().get(iCopy));
            }
            tooltip.add(getTooltipFromItem(this.handler.getSortedRecipes().get(iCopy).getOutput()).get(0));
            tooltip.add(new TranslatableText("container.pfm.working_table.ingredient_required").setStyle(Style.EMPTY.withItalic(true)));
            HashMap<Item, Integer> itemStackCountMap = new HashMap<>();
            for (Ingredient ingredient : this.handler.getSortedRecipes().get(iCopy).getIngredients()) {
                for (ItemStack stack : ingredient.getMatchingStacksClient()) {
                    if (!itemStackCountMap.containsKey(stack.getItem())) {
                        itemStackCountMap.put(stack.getItem(), stack.getCount());
                    } else {
                        itemStackCountMap.put(stack.getItem(), itemStackCountMap.get(stack.getItem()) + stack.getCount());
                    }
                }
            }
            itemStackCountMap.forEach((item, integer) -> {
                int itemCount = handler.getPlayerInventory().count(item);
                Style style = Style.EMPTY.withColor(Formatting.GRAY);
                if (itemCount < integer) {
                    style = style.withColor(Formatting.RED);
                }
                tooltip.add(new LiteralText(integer + " ").append(new LiteralText(getTooltipFromItem(item.getDefaultStack()).get(0).getString())).setStyle(style));
            });
            this.renderTooltip(matrices, tooltip, x, y);
        }
    }

    private void renderRecipeBackground(MatrixStack matrices, int mouseX, int mouseY, int x, int y, int scrollOffset) {
        for (int i = this.scrollOffset; i < scrollOffset && i < this.handler.getVisibleRecipeCount(); ++i) {
            int j = i - this.scrollOffset;
            int k = x + j % RECIPE_LIST_COLUMNS * RECIPE_ENTRY_WIDTH;
            int l = j / RECIPE_LIST_COLUMNS;
            int m = y + l * RECIPE_ENTRY_HEIGHT + 2;
            int v = this.backgroundHeight;
            int iCopy = i;
            if (this.handler.searching) {
                iCopy = this.handler.getSortedRecipes().indexOf(this.handler.getSearchableRecipes().get(iCopy));
            }
            if (iCopy == this.handler.getSelectedRecipe()) {
                v += 55;
            }
            else if (!this.handler.getAvailableRecipes().contains(this.handler.getSortedRecipes().get(iCopy))) {
                v += 18;
            } else if (mouseX >= k && mouseY >= m && mouseX < k + 16 && mouseY < m + 18) {
                v += 36;
            }
            this.drawTexture(matrices, k, m - 1, 0, v, 16, 18);
        }
    }

    private void renderRecipeIcons(int x, int y, int scrollOffset) {
        for (int i = this.scrollOffset; i < scrollOffset && i < this.handler.getVisibleRecipeCount(); ++i) {
            int iMinusScrollOffset = i - this.scrollOffset;
            int xOffset = x + iMinusScrollOffset % RECIPE_LIST_COLUMNS * RECIPE_ENTRY_WIDTH;
            int l = iMinusScrollOffset / RECIPE_LIST_COLUMNS;
            int yOffset = y + l * RECIPE_ENTRY_HEIGHT + 2;
            int iCopy = i;
            if (this.handler.searching) {
                iCopy = this.handler.getSortedRecipes().indexOf(this.handler.getSearchableRecipes().get(iCopy));
            }
            this.client.getItemRenderer().renderInGuiWithOverrides(this.handler.getSortedRecipes().get(iCopy).getOutput(), xOffset, yOffset);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.mouseClicked = false;
        if (this.canCraft) {
            int xOffsetForMouseClick = this.x + RECIPE_LIST_OFFSET_X;
            int yOffsetForMouseClick = this.y + RECIPE_LIST_OFFSET_Y;
            int scrollOffsetForMouseClick = this.scrollOffset + 18;
            for (int clickedRecipeId = this.scrollOffset; clickedRecipeId < scrollOffsetForMouseClick; ++clickedRecipeId) {
                int m = clickedRecipeId - this.scrollOffset;
                double d = mouseX - (double)(xOffsetForMouseClick + m % RECIPE_LIST_COLUMNS * RECIPE_ENTRY_WIDTH);
                double e = mouseY - (double)(yOffsetForMouseClick + m / RECIPE_LIST_COLUMNS * RECIPE_ENTRY_HEIGHT);
                int clickedRecipeIdCopy = clickedRecipeId;
                if (this.handler.searching) {
                    if (clickedRecipeIdCopy < this.handler.getSearchableRecipes().size())
                        clickedRecipeIdCopy = this.handler.getSortedRecipes().indexOf(this.handler.getSearchableRecipes().get(clickedRecipeIdCopy));
                    else
                        clickedRecipeIdCopy = -1;
                }
                if (!(d >= 0.0) || !(e >= 0.0) || !(d < 16.0) || !(e < 18.0) || !this.handler.onButtonClick(this.client.player, clickedRecipeIdCopy)) continue;
                this.client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1.0f));
                this.client.interactionManager.clickButton(this.handler.syncId, clickedRecipeIdCopy);
                return true;
            }
            xOffsetForMouseClick = this.x + 119;
            yOffsetForMouseClick = this.y + 9;
            if (mouseX >= (double)xOffsetForMouseClick && mouseX < (double)(xOffsetForMouseClick + SCROLLBAR_WIDTH) && mouseY >= (double)yOffsetForMouseClick && mouseY < (double)(yOffsetForMouseClick + SCROLLBAR_AREA_HEIGHT)) {
                this.mouseClicked = true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (this.mouseClicked && this.shouldScroll()) {
            int i = this.y + 30;
            int j = i + 54;
            this.scrollAmount = ((float)mouseY - (float)i - 7.5f) / ((float)(j - i) - SCROLLBAR_HEIGHT);
            this.scrollAmount = MathHelper.clamp(this.scrollAmount, 0.0f, 1.0f);
            this.scrollOffset = (int)((double)(this.scrollAmount * (float)this.getMaxScroll()) + 0.5) * RECIPE_LIST_COLUMNS;
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (this.shouldScroll()) {
            int i = this.getMaxScroll();
            this.scrollAmount = (float)((double)this.scrollAmount - amount / (double)i);
            this.scrollAmount = MathHelper.clamp(this.scrollAmount, 0.0f, 1.0f);
            this.scrollOffset = (int)((double)(this.scrollAmount * (float)i) + 0.5) * RECIPE_LIST_COLUMNS;
        }
        return true;
    }

    private boolean shouldScroll() {
        return this.handler.getVisibleRecipeCount() > 18;
    }

    protected int getMaxScroll() {
        return (this.handler.getVisibleRecipeCount() + RECIPE_LIST_COLUMNS - 1) / RECIPE_LIST_COLUMNS - RECIPE_LIST_ROWS;
    }

    private void onInventoryChange() {
        this.canCraft = this.handler.canCraft();
        if (!this.canCraft) {
            this.scrollAmount = 0.0f;
            this.scrollOffset = 0;
        }
    }
}