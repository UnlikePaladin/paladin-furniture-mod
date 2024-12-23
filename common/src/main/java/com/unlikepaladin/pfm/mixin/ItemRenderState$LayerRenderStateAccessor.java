package com.unlikepaladin.pfm.mixin;

import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.model.BakedModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemRenderState.LayerRenderState.class)
public interface ItemRenderState$LayerRenderStateAccessor {
    @Accessor
    BakedModel getModel();

    @Accessor
    int[] getTints();
}
