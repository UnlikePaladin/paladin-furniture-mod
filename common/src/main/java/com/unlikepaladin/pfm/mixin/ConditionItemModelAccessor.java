package com.unlikepaladin.pfm.mixin;

import net.minecraft.client.render.item.model.ConditionItemModel;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.item.property.bool.BooleanProperty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ConditionItemModel.class)
public interface ConditionItemModelAccessor {
    @Accessor
    ItemModel getOnTrue();

    @Accessor
    ItemModel getOnFalse();

    @Accessor
    BooleanProperty getProperty();
}
