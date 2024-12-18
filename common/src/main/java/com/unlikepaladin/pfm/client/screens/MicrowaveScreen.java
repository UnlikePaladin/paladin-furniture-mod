package com.unlikepaladin.pfm.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.blocks.blockentities.MicrowaveBlockEntity;
import com.unlikepaladin.pfm.menus.AbstractMicrowaveScreenHandler;
import com.unlikepaladin.pfm.menus.MicrowaveScreenHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.*;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.Objects;
import java.util.Optional;

public class MicrowaveScreen extends HandledScreen<MicrowaveScreenHandler> {
    private final Identifier background = Identifier.of(PaladinFurnitureMod.MOD_ID,"textures/gui/container/microwave.png");
    private boolean narrow;
    public boolean isActive;
    private MicrowaveBlockEntity microwaveBlockEntity;
    private RecipePropertySet recipePropertySet;

    private final Text startButtonText = Text.translatable("gui.pfm.microwave.start_button");
    public MicrowaveScreen(MicrowaveScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    public ButtonWidget startButton;

    @Override
    public void init() {
        super.init();
        this.microwaveBlockEntity = handler.microwaveBlockEntity;
        isActive = handler.getActive();
        this.narrow = this.width < 379;
        this.titleX = (this.backgroundWidth - this.textRenderer.getWidth(this.title)) / 2;
        this.startButton = this.addDrawableChild(new ButtonWidget.Builder(startButtonText, button -> {
            AbstractMicrowaveScreenHandler.setActive(microwaveBlockEntity,true);
        }).position(this.x + 8, this.y + 40).size( 40, 20).build());
        if (recipePropertySet == null)
            recipePropertySet = microwaveBlockEntity.getWorld().getRecipeManager().getPropertySet(RecipePropertySet.SMOKER_INPUT);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // if (this.recipeBook.isOpen() && this.narrow) {
        if (this.narrow) {
            this.renderBackground(context, mouseX, mouseY, delta);
            //this.recipeBook.render(context, mouseX, mouseY, delta);
        } else {
            //this.recipeBook.render(context, mouseX, mouseY, delta);
            super.render(context, mouseX, mouseY, delta);
            //this.recipeBook.drawGhostSlots(context, this.x, this.y, true, delta);
        }
        this.drawMouseoverTooltip(context, mouseX, mouseY);
        //this.recipeBook.drawTooltip(context, this.x, this.y, mouseX, mouseY);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int k;
        int i = this.x;
        int j = this.y;
        context.drawTexture(RenderLayer::getGuiTextured, this.background, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight, 256, 256);
        k = this.handler.getCookProgress();
        k = Math.round(k * 1.75f);
        context.drawTexture(RenderLayer::getGuiTextured, this.background, i + 147, j + 66 + -k, 176, 40 - k, 13, k +1, 256, 256);
    }

    @Override
    protected void handledScreenTick() {
        super.handledScreenTick();
        this.isActive = handler.isActive;

        if(!recipePropertySet.canUse(microwaveBlockEntity.getStack(0)) && !this.handler.isActive()) {
            this.startButton.active = false;
        }
        else {
            this.startButton.active = !this.handler.isActive();
        }
    }
}
