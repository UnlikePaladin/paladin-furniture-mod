package com.unlikepaladin.pfm.mixin;

import net.minecraft.client.render.item.model.CompositeItemModel;
import net.minecraft.client.render.item.model.ItemModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(CompositeItemModel.class)
public interface CompositeItemModelAccessor {
    @Accessor("models")
    List<ItemModel> getItemModels();
}
