package com.unlikepaladin.pfm.entity.render;

import com.unlikepaladin.pfm.blocks.InnerTrashcanBlock;
import com.unlikepaladin.pfm.blocks.TrashcanBlock;
import com.unlikepaladin.pfm.blocks.blockentities.TrashcanBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;

public class TrashcanBlockEntityRenderer<T extends TrashcanBlockEntity> implements BlockEntityRenderer<T> {
    public ItemStack itemStack;
    private final ItemRenderer itemRenderer;
    public TrashcanBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.itemRenderer = ctx.getItemRenderer();
    }

    @Override
    public void render(T trashcanBlockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumerProvider, int light, int overlay) {
        if (!(trashcanBlockEntity.getCachedState().getBlock() instanceof TrashcanBlock)) {
            for (int i = 0; i < 9; i++)
            {
                Direction direction = trashcanBlockEntity.getCachedState().get(InnerTrashcanBlock.FACING);
                matrices.push();
                Direction direction2 = Direction.fromHorizontal((direction.getHorizontal()) % 4);
                float g = -direction2.asRotation();
                itemStack = trashcanBlockEntity.getStack(i);
                switch (i) {
                    case 0: {
                        matrices.translate(0.5, 0.08, 0.3);
                        break;
                    }
                    case 1: {
                        matrices.translate(0.52, 0.2, 0.33);
                        break;
                    }
                    case 2: {
                        matrices.translate(0.5, 0.33, 0.3);
                        break;
                    }
                    case 3: {
                        matrices.translate(0.51, 0.31, 0.29);
                        break;
                    }
                    case 4: {
                        matrices.translate(0.48, 0.14, 0.31);
                        break;
                    }
                    case 5: {
                        matrices.translate(0.47, 0.44, 0.32);
                        break;
                    }
                    case 6: {
                        matrices.translate(0.47, 0.16, 0.28);
                        break;
                    }
                    case 7: {
                        matrices.translate(0.52, 0.43, 0.28);
                        break;
                    }
                    case 8: {
                        matrices.translate(0.49, 0.53, 0.31);
                        break;
                    }
                }
                if (!(itemStack.getItem() instanceof BlockItem)) {
                    matrices.translate(0.0, 0.0, 0.1);
                } else if (Registries.ITEM.getId(itemStack.getItem()).getNamespace().contains("pfm")) {
                    matrices.translate(0.0, 0.0, 0.15);
                }
                int rot = 90;
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(rot));
                int lightAbove = WorldRenderer.getLightmapCoordinates(trashcanBlockEntity.getWorld(), trashcanBlockEntity.getPos().up());
                matrices.scale(0.8f, 0.8f, 0.8f);
                this.itemRenderer.renderItem(itemStack, ModelTransformationMode.GROUND, lightAbove, overlay, matrices, vertexConsumerProvider, trashcanBlockEntity.getWorld(), (int) (trashcanBlockEntity.getPos().asLong()+ i));
                matrices.pop();
            }
        }
    }
}
