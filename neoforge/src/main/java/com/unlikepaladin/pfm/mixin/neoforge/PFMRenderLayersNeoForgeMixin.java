package com.unlikepaladin.pfm.mixin.neoforge;

import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.blocks.DynamicRenderLayerInterface;
import com.unlikepaladin.pfm.blocks.models.AbstractBakedModel;
import com.unlikepaladin.pfm.client.PaladinFurnitureModClient;
import com.unlikepaladin.pfm.data.materials.VariantBase;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Debug(export = true, print = true)
@Mixin(RenderLayers.class)
public class PFMRenderLayersNeoForgeMixin {

    @Shadow
    @Deprecated(
            forRemoval = true,
            since = "1.19"
    )
    public static ChunkRenderTypeSet getRenderLayers(BlockState state) {
        throw new AssertionError();
    }

    @Unique
    private static final Map<BlockState, Object> pfm$renderLayers = new ConcurrentHashMap<>();
    @Inject(method = "getRenderLayers", at = @At("TAIL"), cancellable = true)
    private static void modifyFurnitureRenderLayer(BlockState state, CallbackInfoReturnable<ChunkRenderTypeSet> cir) {
        if (state.getBlock().getTranslationKey().contains("pfm")) {
            if (!cir.getReturnValue().getClass().isAssignableFrom(ChunkRenderTypeSet.class)) {
                return;
            }

            if (pfm$renderLayers.containsKey(state)) {
                Object previous = pfm$renderLayers.get(state);
                if (previous instanceof Collection<?>) {
                    cir.setReturnValue(ChunkRenderTypeSet.of((Collection<RenderLayer>) previous));
                } else {
                    cir.setReturnValue(ChunkRenderTypeSet.of((RenderLayer) previous));
                }
                return;
            }

            if (MinecraftClient.getInstance().getBakedModelManager().getBlockModels().getModel(state) instanceof AbstractBakedModel abstractBakedModel) {
                VariantBase<?> variant = abstractBakedModel.getVariant(state);
                if (variant != null) {
                    ChunkRenderTypeSet baseRenderTypes = getRenderLayers(variant.getBaseBlock().getDefaultState());
                    ChunkRenderTypeSet currentRenderTypes = cir.getReturnValue();

                    // Combine the render types using union
                    ChunkRenderTypeSet combinedRenderTypes = ChunkRenderTypeSet.union(baseRenderTypes, currentRenderTypes);

                    // Prioritize cutout and translucent over solid
                    if (combinedRenderTypes.contains(RenderLayer.getCutout()) || combinedRenderTypes.contains(RenderLayer.getTranslucent()) || combinedRenderTypes.contains(RenderLayer.getCutoutMipped())) {
                        // Remove solid if higher-priority layers are present
                        combinedRenderTypes = ChunkRenderTypeSet.intersection(combinedRenderTypes,
                                ChunkRenderTypeSet.of(RenderLayer.getCutout(), RenderLayer.getTranslucent(), RenderLayer.getCutoutMipped())
                        );
                    }

                    // Update cir with the prioritized set
                    cir.setReturnValue(combinedRenderTypes);
                    pfm$renderLayers.put(state, List.copyOf(combinedRenderTypes.asList()));
                    return;
                }
            }
            if (state.getBlock() instanceof DynamicRenderLayerInterface) {
                RenderLayer renderLayer = ((DynamicRenderLayerInterface) state.getBlock()).getCustomRenderLayer();
                if (PaladinFurnitureMod.getPFMConfig().isShaderSolidFixOn())
                    cir.setReturnValue(PaladinFurnitureModClient.areShadersOn() ? ChunkRenderTypeSet.of(RenderLayer.getSolid()) : ChunkRenderTypeSet.of(renderLayer));
                else
                    cir.setReturnValue(ChunkRenderTypeSet.of(renderLayer));
            }
        }
    }
}
