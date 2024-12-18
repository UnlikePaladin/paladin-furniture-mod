package com.unlikepaladin.pfm.compat.cookingforblockheads.fabric.menu;

import com.mojang.blaze3d.systems.RenderSystem;
import com.unlikepaladin.pfm.compat.cookingforblockheads.fabric.StoveBlockEntityBalm;
import net.blay09.mods.balm.api.energy.EnergyStorage;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class StoveScreenBalm extends HandledScreen<StoveScreenHandlerBalm> {
    private static final Identifier texture = Identifier.of("cookingforblockheads", "textures/gui/oven.png");

    public StoveScreenBalm(StoveScreenHandlerBalm container, PlayerInventory playerInventory, Text displayName) {
        super(container, playerInventory, displayName);
        this.backgroundWidth += 22;
        this.backgroundHeight = 193;
        this.titleX += 22;
        this.playerInventoryTitleX += 22;
        this.playerInventoryTitleY = this.backgroundHeight - 94;
    }

    public void render(DrawContext context, int mouseX, int mouseY, float partialTicks) {
        super.render(context, mouseX, mouseY, partialTicks);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
        StoveBlockEntityBalm tileEntity = this.handler.getTileEntity();
        if (tileEntity.hasPowerUpgrade() && mouseX >= this.x + this.backgroundWidth - 25 && mouseY >= this.y + 22 && mouseX < this.x + this.backgroundWidth - 25 + 35 + 18 && mouseY < this.y + 22 + 72) {
            EnergyStorage energyStorage = tileEntity.getEnergyStorage();
            context.drawTooltip(MinecraftClient.getInstance().textRenderer, Text.translatable("tooltip.cookingforblockheads:energy_stored", new Object[]{energyStorage.getEnergy(), energyStorage.getCapacity()}), mouseX, mouseY);
        }

    }

    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        super.drawForeground(context, mouseX, mouseY);
        StoveBlockEntityBalm tileEntity = this.handler.getTileEntity();

        for(int i = 0; i < 9; ++i) {
            Slot slot = this.handler.slots.get(i + 7);
            if (slot.hasStack()) {
                ItemStack itemStack = tileEntity.getSmeltingResult(slot.getStack());
                if (!itemStack.isEmpty()) {
                }
            }
        }

    }

    protected void drawBackground(DrawContext drawContext, float partialTicks, int mouseX, int mouseY) {
        drawContext.drawTexture(RenderLayer::getGuiTextured, texture, this.x + 22, this.y, 0, 0, this.backgroundWidth - 22, this.backgroundHeight, this.backgroundWidth - 22, this.backgroundHeight);
        drawContext.drawTexture(RenderLayer::getGuiTextured, texture, this.x, this.y + 10, 176, 30, 25, 87, 25, 87);
        StoveBlockEntityBalm tileEntity = this.handler.getTileEntity();
        int offsetX = tileEntity.hasPowerUpgrade() ? -5 : 0;
        drawContext.drawTexture(RenderLayer::getGuiTextured, texture, this.x + 22 + 61 + offsetX, this.y + 18, 176, 117, 76, 76, 76, 76);
        drawContext.drawTexture(RenderLayer::getGuiTextured, texture, this.x + 22 + 38 + offsetX, this.y + 43, 205, 84, 18, 33, 18, 33);
        if (tileEntity.isBurning()) {
            int burnTime = (int)(12.0F * tileEntity.getBurnTimeProgress());
            drawContext.drawTexture(RenderLayer::getGuiTextured, texture, this.x + 22 + 40 + offsetX, this.y + 43 + 12 - burnTime, 176, 12 - burnTime, 14, burnTime + 1, 12 - burnTime, 14, burnTime + 1);
        }

        if (tileEntity.hasPowerUpgrade()) {
            drawContext.drawTexture(RenderLayer::getGuiTextured, texture, this.x + this.backgroundWidth - 25, this.y + 22, 205, 0, 18, 72, 18, 72);
            EnergyStorage energyStorage = tileEntity.getEnergyStorage();
            float energyPercentage = (float)energyStorage.getEnergy() / (float)energyStorage.getCapacity();
            drawContext.drawTexture(RenderLayer::getGuiTextured, texture, this.x + this.backgroundWidth - 25 + 1, this.y + 22 + 1 + 70 - (int)(energyPercentage * 70.0F), 223, 0, 16, (int)(energyPercentage * 70.0F), 16, (int)(energyPercentage * 70.0F));
        }

    }
}