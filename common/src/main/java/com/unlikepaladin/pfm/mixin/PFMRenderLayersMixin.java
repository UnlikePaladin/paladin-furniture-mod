package com.unlikepaladin.pfm.mixin;

import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.blocks.DynamicRenderLayerInterface;
import com.unlikepaladin.pfm.blocks.models.AbstractBakedModel;
import com.unlikepaladin.pfm.client.PaladinFurnitureModClient;
import com.unlikepaladin.pfm.data.materials.VariantBase;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;

@Mixin(RenderLayers.class)
public abstract class PFMRenderLayersMixin {
    @Shadow
    @Deprecated
    public static RenderLayer getBlockLayer(BlockState state) {
        throw new AssertionError();
    }

    @Unique
    private static final Map<BlockState, RenderLayer> pfm$renderLayers = new HashMap<>();
    @Inject(method = "getBlockLayer", at = @At("HEAD"), cancellable = true)
    private static void modifyFurnitureRenderLayer(BlockState state, CallbackInfoReturnable<RenderLayer> cir) {
        if (state.getBlock().getTranslationKey().contains("pfm")) {
            if (pfm$renderLayers.containsKey(state)) {
                cir.setReturnValue(pfm$renderLayers.get(state));
                return;
            }
            if (MinecraftClient.getInstance().getBakedModelManager().getBlockModels().getModel(state) instanceof AbstractBakedModel) {
                AbstractBakedModel abstractBakedModel = (AbstractBakedModel) MinecraftClient.getInstance().getBakedModelManager().getBlockModels().getModel(state);
                VariantBase<?> variant = abstractBakedModel.getVariant(state);
                if (variant != null) {
                    RenderLayer parentLayer = getBlockLayer(variant.getBaseBlock().getDefaultState());
                    cir.setReturnValue(parentLayer);
                    pfm$renderLayers.put(state, parentLayer);
                    return;
                }
            }
            if (state.getBlock() instanceof DynamicRenderLayerInterface) {
                RenderLayer renderLayer = ((DynamicRenderLayerInterface) state.getBlock()).getCustomRenderLayer();
                    if (PaladinFurnitureMod.getPFMConfig().isShaderSolidFixOn())
                        cir.setReturnValue(PaladinFurnitureModClient.areShadersOn() ? RenderLayer.getSolid() : renderLayer);
                    else
                        cir.setReturnValue(renderLayer);
            }
        }
    }
}
