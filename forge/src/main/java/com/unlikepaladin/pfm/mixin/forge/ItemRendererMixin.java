package com.unlikepaladin.pfm.mixin.forge;

import com.unlikepaladin.pfm.client.model.PFMBakedModelGetQuadsExtension;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MatrixUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import net.minecraft.util.math.random.Random;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {

    @Shadow
    public static VertexConsumer getItemGlintConsumer(VertexConsumerProvider vertexConsumers, RenderLayer layer, boolean solid, boolean glint) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    private static VertexConsumer getDynamicDisplayGlintConsumer(VertexConsumerProvider provider, RenderLayer layer, MatrixStack.Entry entry) {
        return null;
    }

    @Shadow
    private static void renderBakedItemQuads(MatrixStack matrices, VertexConsumer vertexConsumer, List<BakedQuad> quads, int[] tints, int light, int overlay) {
    }

    @Inject(method = "renderItem(Lnet/minecraft/item/ModelTransformationMode;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II[ILnet/minecraft/client/render/model/BakedModel;Lnet/minecraft/client/render/RenderLayer;Lnet/minecraft/client/render/item/ItemRenderState$Glint;)V", at = @At(value = "HEAD"), cancellable = true)
    private static void renderPFMItem(ModelTransformationMode transformationMode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, int[] tints, BakedModel model, RenderLayer layer, ItemRenderState.Glint glint, CallbackInfo ci) {
        if (model instanceof PFMBakedModelGetQuadsExtension) {
            VertexConsumer vertexconsumer;
            if (glint == ItemRenderState.Glint.SPECIAL) {
                MatrixStack.Entry posestack$pose = matrices.peek().copy();
                if (transformationMode == ModelTransformationMode.GUI) {
                    MatrixUtil.scale(posestack$pose.getPositionMatrix(), 0.5F);
                } else if (transformationMode.isFirstPerson()) {
                    MatrixUtil.scale(posestack$pose.getPositionMatrix(), 0.75F);
                }

                vertexconsumer = getDynamicDisplayGlintConsumer(vertexConsumers, layer, posestack$pose);
            } else {
                vertexconsumer = getItemGlintConsumer(vertexConsumers, layer, true, glint != ItemRenderState.Glint.NONE);
            }

            Random randomsource = Random.create();
            long seed = 42L;

            for (Direction direction : Direction.values()) {
                randomsource.setSeed(seed);
                renderBakedItemQuads(matrices, vertexconsumer, ((PFMBakedModelGetQuadsExtension) model).getQuadsCached(direction, randomsource), tints, light, overlay);
            }

            randomsource.setSeed(seed);
            renderBakedItemQuads(matrices, vertexconsumer, ((PFMBakedModelGetQuadsExtension) model).getQuadsCached(null, randomsource), tints, light, overlay);
            ci.cancel();
        }
    }
}
