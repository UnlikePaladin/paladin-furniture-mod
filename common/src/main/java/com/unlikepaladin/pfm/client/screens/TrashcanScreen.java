package com.unlikepaladin.pfm.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.unlikepaladin.pfm.blocks.blockentities.MicrowaveBlockEntity;
import com.unlikepaladin.pfm.blocks.blockentities.TrashcanBlockEntity;
import com.unlikepaladin.pfm.menus.AbstractMicrowaveScreenHandler;
import com.unlikepaladin.pfm.menus.TrashcanScreenHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class TrashcanScreen extends HandledScreen<TrashcanScreenHandler> {
    private static final Identifier background = Identifier.of("textures/gui/container/dispenser.png");
    private TrashcanBlockEntity trashcanBlockEntity;
    private boolean narrow;
    private ButtonWidget startButton;

    public TrashcanScreen(TrashcanScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }
    private static final Text startButtonText = Text.translatable("gui.pfm.trashcan.clear_button");

    @Override
    public void init() {
        super.init();
        this.trashcanBlockEntity = handler.trashcanBlockEntity;
        this.narrow = this.width < 379;
        this.titleX = (this.backgroundWidth - this.textRenderer.getWidth(this.title)) / 2;
        this.startButton = this.addDrawableChild(new ButtonWidget.Builder( startButtonText, button -> {
            TrashcanScreenHandler.clear(trashcanBlockEntity);
        }).position(this.x + 8, this.y + 40).size(40, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (this.narrow) {
            this.renderBackground(context, mouseX, mouseY, delta);
        } else {
            super.render(context, mouseX, mouseY, delta);
        }
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int i = this.x;
        int j = this.y;
        context.drawTexture(RenderLayer::getGuiTextured, background, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight, 256, 256);
    }

}
