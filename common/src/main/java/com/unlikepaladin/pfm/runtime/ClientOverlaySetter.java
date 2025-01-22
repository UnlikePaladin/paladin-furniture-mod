package com.unlikepaladin.pfm.runtime;

import com.mojang.blaze3d.systems.RenderSystem;
import com.unlikepaladin.pfm.client.screens.overlay.PFMGeneratingOverlay;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Util;

public class ClientOverlaySetter {
    public static void setOverlayToPFMOverlay(PFMResourceProgress resourceProgress) {
        MinecraftClient client = MinecraftClient.getInstance();
        PFMGeneratingOverlay overlay = new PFMGeneratingOverlay(client.getOverlay(), resourceProgress, client, true);
        client.setOverlay(overlay);
    }

    public static void updateScreen() {
        MinecraftClient client = MinecraftClient.getInstance();

        MatrixStack matrixStack = RenderSystem.getModelViewStack();

        matrixStack.push();
        RenderSystem.applyModelViewMatrix();
        client.getFramebuffer().beginWrite(true);
        long i = Util.getMeasuringTimeNano();
        client.gameRenderer.render(1, i, false);
        client.getFramebuffer().endWrite();
        matrixStack.pop();

        matrixStack.push();
        RenderSystem.applyModelViewMatrix();
        client.getFramebuffer().draw(client.getWindow().getFramebufferWidth(), client.getWindow().getFramebufferHeight());
        matrixStack.pop();

        RenderSystem.applyModelViewMatrix();
        client.getWindow().swapBuffers();
    }

}
