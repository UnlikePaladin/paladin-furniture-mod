package com.unlikepaladin.pfm.mixin;

import net.minecraft.client.render.item.model.BasicItemModel;
import net.minecraft.client.render.item.tint.TintSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
@Mixin(BasicItemModel.class)
public interface BasicItemModelAccessor {
    @Accessor("tints")
    List<TintSource> getTints();
}
