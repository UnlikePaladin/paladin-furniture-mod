package com.unlikepaladin.pfm.mixin.forge;

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
public abstract class PFMRenderLayersForgeMixin {

    @Shadow
    public static boolean canRenderInLayer(BlockState par1, RenderLayer par2) {
        throw new AssertionError();
    }

    @Unique
    private static final Map<BlockState, Boolean> pfm$renderLayers = new HashMap<>();
    @Inject(method = "canRenderInLayer(Lnet/minecraft/block/BlockState;Lnet/minecraft/client/render/RenderLayer;)Z", at = @At("TAIL"), cancellable = true)
    private static void modifyFurnitureRenderLayer(BlockState state, RenderLayer type, CallbackInfoReturnable<Boolean> cir) {
        if (state.getBlock().getTranslationKey().contains("pfm")) {
            if (pfm$renderLayers.containsKey(state)) {
                cir.setReturnValue(pfm$renderLayers.get(state));
                return;
            }

            if (MinecraftClient.getInstance().getBakedModelManager().getBlockModels().getModel(state) instanceof AbstractBakedModel) {
                AbstractBakedModel abstractBakedModel = (AbstractBakedModel) MinecraftClient.getInstance().getBakedModelManager().getBlockModels().getModel(state);
                VariantBase<?> variant = abstractBakedModel.getVariant(state);
                if (variant != null) {
                    boolean doesBaseRender = canRenderInLayer(variant.getBaseBlock().getDefaultState(), type);
                    if (doesBaseRender || !cir.getReturnValue()) {
                        cir.setReturnValue(doesBaseRender);
                        pfm$renderLayers.put(state, doesBaseRender);
                    } else {
                        pfm$renderLayers.put(state, cir.getReturnValue());
                    }
                }
            }
            if (state.getBlock() instanceof DynamicRenderLayerInterface) {
                RenderLayer renderLayer = ((DynamicRenderLayerInterface) state.getBlock()).getCustomRenderLayer();
                if (PaladinFurnitureMod.getPFMConfig().isShaderSolidFixOn())
                    cir.setReturnValue(PaladinFurnitureModClient.areShadersOn() ? type == RenderLayer.getSolid() : type == renderLayer);
                else
                    cir.setReturnValue(type == renderLayer);
            }
        }
    }
}
