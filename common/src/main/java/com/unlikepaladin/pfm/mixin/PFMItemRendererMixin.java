package com.unlikepaladin.pfm.mixin;

import com.unlikepaladin.pfm.client.PFMBuiltinItemRendererExtension;
import com.unlikepaladin.pfm.client.PFMItemRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ItemRenderer.class, priority = 999)
public class PFMItemRendererMixin {
    @Unique
    private final PFMItemRenderer pfm$itemRenderer = new PFMItemRenderer(MinecraftClient.getInstance().getBlockEntityRenderDispatcher(), MinecraftClient.getInstance().getEntityModelLoader());

    @Inject(at = @At("HEAD"), method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V", cancellable = true)
    private void renderWithItemRenderer(ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel model, CallbackInfo ci) {
        if (stack.getItem() instanceof PFMBuiltinItemRendererExtension || stack.getItem() instanceof BlockItem && ((BlockItem) stack.getItem()).getBlock() instanceof PFMBuiltinItemRendererExtension) {
            pfm$itemRenderer.render(stack, renderMode, matrices, vertexConsumers, light, overlay);
        }
    }
}
