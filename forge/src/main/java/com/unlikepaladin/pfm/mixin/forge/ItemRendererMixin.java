package com.unlikepaladin.pfm.mixin.forge;

import com.unlikepaladin.pfm.client.forge.PFMBakedModelGetQuadsExtension;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.util.math.Direction;
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
    public abstract void renderBakedItemQuads(MatrixStack matrices, VertexConsumer vertices, List<BakedQuad> quads, ItemStack stack, int light, int overlay);


    @Shadow
    public static VertexConsumer getItemGlintConsumer(VertexConsumerProvider vertexConsumers, RenderLayer layer, boolean solid, boolean glint) {
        throw new UnsupportedOperationException();
    }

    @Inject(at = @At("HEAD"), method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V", cancellable = true)
    private void renderPFMItem(ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel model, CallbackInfo ci) {
        if (!stack.isEmpty() && model instanceof PFMBakedModelGetQuadsExtension) {
            matrices.push();
            MatrixStack.Entry pose = matrices.peek();
            model = model.applyTransform(renderMode, matrices, leftHanded);
            matrices.translate(-0.5, -0.5, -0.5);

            BlockState state = stack.getItem() instanceof BlockItem ? ((BlockItem) stack.getItem()).getBlock().getDefaultState() : null;
            RenderLayer renderLayer = RenderLayers.getItemLayer(stack);
            VertexConsumer vertexConsumer = getItemGlintConsumer(vertexConsumers, renderLayer, true, stack.hasGlint());

            Random random = Random.create();
            long randomSeed = 42L;
            for (Direction direction : Direction.values()) {
                random.setSeed(randomSeed);
                this.renderBakedItemQuads(matrices, vertexConsumer, ((PFMBakedModelGetQuadsExtension) model).getQuadsCached(stack, state, direction, random), stack, light, overlay);
            }
            random.setSeed(randomSeed);
            this.renderBakedItemQuads(matrices, vertexConsumer, ((PFMBakedModelGetQuadsExtension)model).getQuadsCached(stack, state, null, random), stack, light, overlay);

            // Conditionally pop because of handlePerspective weirdness
            if (matrices.peek() != pose) {
                matrices.pop();
            }
            matrices.pop();
            ci.cancel();
        }
    }
}
