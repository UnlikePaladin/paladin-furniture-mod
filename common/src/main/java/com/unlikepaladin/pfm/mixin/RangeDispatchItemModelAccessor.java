package com.unlikepaladin.pfm.mixin;

import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.item.model.RangeDispatchItemModel;
import net.minecraft.client.render.item.property.numeric.NumericProperty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(RangeDispatchItemModel.class)
public interface RangeDispatchItemModelAccessor {
    @Accessor
    NumericProperty getProperty();

    @Accessor
    ItemModel[] getModels();

    @Accessor
    ItemModel getFallback();

    @Accessor
    float[] getThresholds();

    @Accessor
    float getScale();

    @Invoker("getIndex")
    static int getIndex(float[] thresholds, float value) {
        throw new AssertionError();
    }
}
