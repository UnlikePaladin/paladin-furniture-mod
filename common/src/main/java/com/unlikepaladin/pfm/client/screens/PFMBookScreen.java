package com.unlikepaladin.pfm.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class PFMBookScreen extends Screen {
    public PFMBookScreen(Text title) {
        super(title);
    }

    public static final Identifier BOOK_TEXTURE = Identifier.of("pfm", "textures/gui/book_orange.png");
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        //RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, BOOK_TEXTURE);

        super.render(context, mouseX, mouseY, delta);
    }



}
