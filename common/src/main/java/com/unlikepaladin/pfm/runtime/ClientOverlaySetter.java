package com.unlikepaladin.pfm.runtime;

import com.mojang.blaze3d.systems.RenderSystem;
import com.unlikepaladin.pfm.client.screens.overlay.PFMGeneratingOverlay;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Util;
import org.joml.Matrix4fStack;

public class ClientOverlaySetter {
    public static void setOverlayToPFMOverlay(PFMResourceProgress resourceProgress) {
        MinecraftClient client = MinecraftClient.getInstance();
        PFMGeneratingOverlay overlay = new PFMGeneratingOverlay(client.getOverlay(), resourceProgress, client, true);
        client.setOverlay(overlay);
    }

    public static void updateScreen() {
        MinecraftClient client = MinecraftClient.getInstance();

        Matrix4fStack matrixStack = RenderSystem.getModelViewStack();

        matrixStack.pushMatrix();
        RenderSystem.applyModelViewMatrix();
        client.getFramebuffer().beginWrite(true);
        long i = Util.getMeasuringTimeNano();
        client.gameRenderer.render(client.getRenderTickCounter(), shouldTick(client));
        client.getFramebuffer().endWrite();
        matrixStack.popMatrix();

        matrixStack.pushMatrix();
        RenderSystem.applyModelViewMatrix();
        client.getFramebuffer().draw(client.getWindow().getFramebufferWidth(), client.getWindow().getFramebufferHeight());
        matrixStack.popMatrix();

        RenderSystem.applyModelViewMatrix();
        client.getWindow().swapBuffers();
        ((RenderTickCounter.Dynamic)client.getRenderTickCounter()).tick(client.isPaused());
        ((RenderTickCounter.Dynamic)client.getRenderTickCounter()).setTickFrozen(!shouldTick(client));
    }


    private static boolean shouldTick(MinecraftClient client) {
        return client.world == null || client.world.getTickManager().shouldTick();
    }
}
