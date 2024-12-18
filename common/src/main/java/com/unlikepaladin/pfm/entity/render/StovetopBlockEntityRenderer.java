package com.unlikepaladin.pfm.entity.render;

import com.unlikepaladin.pfm.blocks.KitchenStovetopBlock;
import com.unlikepaladin.pfm.blocks.blockentities.StovetopBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;

@Environment(value= EnvType.CLIENT)
public class StovetopBlockEntityRenderer<T extends StovetopBlockEntity>
        implements BlockEntityRenderer<T> {
    private static final float SCALE = 0.375f;
    private final ItemRenderer itemRenderer;

    public StovetopBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        itemRenderer = ctx.getItemRenderer();
    }

    @Override
    public void render(StovetopBlockEntity stovetopBlockEntity, float f, MatrixStack matrices, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
        Direction direction = stovetopBlockEntity.getCachedState().get(KitchenStovetopBlock.FACING);
        DefaultedList<ItemStack> itemList = stovetopBlockEntity.getItemsBeingCooked();
        int k = (int)stovetopBlockEntity.getPos().asLong();
        for (int l = 0; l < itemList.size(); ++l) {
            ItemStack itemStack = itemList.get(l);
            if (itemStack == ItemStack.EMPTY) continue;
            matrices.push();
            Direction direction2 = Direction.fromHorizontal((l + direction.getHorizontal()) % 4);
            float g = -direction2.asRotation();
            int rot = 45;
            switch(direction) {
                case NORTH:
                    matrices.translate(0.5, 0.08, 0.45);
                    break;
                case SOUTH:
                    matrices.translate(0.5, 0.08, 0.55);
                    break;
                case WEST:
                    matrices.translate(0.45, 0.08, 0.5);
                    break;
                case EAST:
                    matrices.translate(0.55, 0.08, 0.5);
            }
            rot = 180;
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(g));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rot));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0f));
            matrices.translate(-0.1625, -0.1625, 0.0);
            matrices.scale(0.355f, 0.355f, 0.355f);
            this.itemRenderer.renderItem(itemStack, ModelTransformationMode.FIXED, i, j, matrices, vertexConsumerProvider,stovetopBlockEntity.getWorld(), k + l);
            matrices.pop();
        }
    }
}

