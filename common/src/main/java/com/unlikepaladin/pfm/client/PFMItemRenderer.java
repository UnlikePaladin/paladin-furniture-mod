package com.unlikepaladin.pfm.client;

import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.blocks.SimpleBedBlock;
import com.unlikepaladin.pfm.blocks.blockentities.PFMBedBlockEntity;
import com.unlikepaladin.pfm.blocks.models.basicLamp.UnbakedBasicLampModel;
import com.unlikepaladin.pfm.blocks.models.bed.UnbakedBedModel;
import com.unlikepaladin.pfm.data.materials.WoodVariant;
import com.unlikepaladin.pfm.data.materials.WoodVariantRegistry;
import com.unlikepaladin.pfm.entity.render.PFMBedBlockEntityRenderer;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.*;

public class PFMItemRenderer {
    private final PFMBedBlockEntity renderBed;
    private final BlockEntityRenderDispatcher blockEntityRenderDispatcher;
    public PFMItemRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
        this.blockEntityRenderDispatcher = blockEntityRenderDispatcher;
        if (PaladinFurnitureMod.furnitureEntryMap.get(SimpleBedBlock.class) != null ) {
            renderBed = new PFMBedBlockEntity(BlockPos.ORIGIN, PaladinFurnitureMod.furnitureEntryMap.get(SimpleBedBlock.class).getVariantToBlockMapList().get(WoodVariantRegistry.OAK).iterator().next().getDefaultState());
        } else {
            renderBed = null;
        }
    }

    public static Map<Boolean, BakedModel> bedModel = new HashMap<>();
    public BakedModel getBedModel(boolean classic) {
        if (bedModel.containsKey(classic) && bedModel.get(classic) != null) {
            return bedModel.get(classic);
        }
        bedModel.put(classic, classic ? UnbakedBedModel.inventoryModels.getRight() : UnbakedBedModel.inventoryModels.getLeft());
        return bedModel.get(classic);
    }

    public void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        boolean leftHanded = MinecraftClient.getInstance().player != null && MinecraftClient.getInstance().player.getMainArm() == Arm.LEFT && mode.isFirstPerson();

        if (stack.getItem() instanceof BlockItem && ((BlockItem) stack.getItem()).getBlock() instanceof SimpleBedBlock) {
            matrices.push();

            Block block = ((BlockItem) stack.getItem()).getBlock();
            BakedModel bedModel = getBedModel(stack.getItem().getTranslationKey().contains("classic"));
            bedModel.getTransformation().getTransformation(mode).apply(leftHanded, matrices);
            matrices.translate(-.5, -.5, -.5); // Replicate ItemRenderer's translation

            this.renderBed.setPFMColor(((SimpleBedBlock)block).getColor());
            BlockEntityRenderer<PFMBedBlockEntity> blockEntityRenderer = blockEntityRenderDispatcher.get(renderBed);
            blockEntityRenderer.render(renderBed, 1.0f, matrices, vertexConsumers, light, overlay);
            matrices.pop();
        }
    }
}
