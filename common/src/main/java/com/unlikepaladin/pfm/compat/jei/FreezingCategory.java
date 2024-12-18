package com.unlikepaladin.pfm.compat.jei;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.recipes.FreezingRecipe;
import com.unlikepaladin.pfm.registry.PaladinFurnitureModBlocksItems;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import static mezz.jei.api.recipe.RecipeIngredientRole.INPUT;
import static mezz.jei.api.recipe.RecipeIngredientRole.OUTPUT;

public class FreezingCategory implements IRecipeCategory<FreezingRecipe>  {
    public static final Text TITLE = Text.translatable("rei.pfm.freezer");
    public final IDrawable ICON;
    public static final Identifier IDENTIFIER = Identifier.of(PaladinFurnitureMod.MOD_ID, "freezing");
    private final IDrawable BACKGROUND;
    public static final Identifier FREEZE_GUI = Identifier.of(PaladinFurnitureMod.MOD_ID, "textures/gui/container/freezer.png");
    protected final IDrawableStatic staticFreezeIcon;
    protected final IDrawableAnimated animatedFreezeIcon;
    private final int regularFreezeTime;
    private final LoadingCache<Integer, IDrawableAnimated> cachedArrows;

    public FreezingCategory(IGuiHelper guiHelper) {
        ICON = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(PaladinFurnitureModBlocksItems.WHITE_FREEZER));
        this.BACKGROUND = guiHelper.createDrawable(FREEZE_GUI,55, 16, 82, 54);
        staticFreezeIcon = guiHelper.createDrawable(FREEZE_GUI, 176, 0, 12, 12);
        animatedFreezeIcon = guiHelper.createAnimatedDrawable(staticFreezeIcon, 300, IDrawableAnimated.StartDirection.TOP, true);
        this.regularFreezeTime = 100;
        this.cachedArrows = CacheBuilder.newBuilder()
                .maximumSize(25)
                .build(new CacheLoader<>() {
                    @Override
                    public IDrawableAnimated load(Integer freezeTime) {
                        return guiHelper.drawableBuilder(FREEZE_GUI, 176, 14, 24, 17)
                                .buildAnimated(freezeTime, IDrawableAnimated.StartDirection.LEFT, false);
                    }
                });
    }

    @Override
    public RecipeType<FreezingRecipe> getRecipeType() {
        return PaladinFurnitureModJEI.FREEZING_RECIPE;
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

    protected static final int inputSlot = 0;
    protected static final int fuelSlot = 1;
    protected static final int outputSlot = 2;

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, FreezingRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(INPUT, 1, 1)
                .addIngredients(recipe.ingredient());

        builder.addSlot(OUTPUT, 61, 19)
                .addItemStack(recipe.result());
    }

    protected IDrawableAnimated getArrow(FreezingRecipe recipe) {
        int freezeTime = recipe.getCookingTime();
        if (freezeTime <= 0) {
            freezeTime = regularFreezeTime;
        }
        return this.cachedArrows.getUnchecked(freezeTime);
    }

    protected void drawFreezeTime(FreezingRecipe recipe, DrawContext context, int y) {
        int freezeTime = recipe.getCookingTime();
        if (freezeTime > 0) {
            int freezeTimeSeconds = freezeTime / 20;
            Text timeString = Text.of(freezeTimeSeconds + "s");
            MinecraftClient minecraft = MinecraftClient.getInstance();
            TextRenderer fontRenderer = minecraft.textRenderer;
            int stringWidth = fontRenderer.getWidth(timeString);
            context.drawText(fontRenderer, timeString, BACKGROUND.getWidth() - stringWidth, y, 0xFF808080, true);
        }
    }

    protected void drawExperience(FreezingRecipe recipe, DrawContext context, int y) {
        float experience = recipe.getExperience();
        if (experience > 0) {
            Text experienceString = Text.of(experience + " XP");
            MinecraftClient minecraft = MinecraftClient.getInstance();
            TextRenderer fontRenderer = minecraft.textRenderer;
            int stringWidth = fontRenderer.getWidth(experienceString);
            context.drawText(fontRenderer, experienceString, BACKGROUND.getWidth() - stringWidth, y, 0xFF808080, true);
        }
    }

    @Override
    public void draw(FreezingRecipe recipe, IRecipeSlotsView recipeSlotsView, DrawContext context, double mouseX, double mouseY) {
        animatedFreezeIcon.draw(context, 1, 20);

        IDrawableAnimated arrow = getArrow(recipe);
        arrow.draw(context, 24, 18);

        drawExperience(recipe, context, 0);
        drawFreezeTime(recipe, context, 45);
    }
}