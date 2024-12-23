package com.unlikepaladin.pfm.client.model;

import com.unlikepaladin.pfm.data.materials.VariantBase;
import net.minecraft.block.BlockState;

public interface PFMBakedModelSetPropertiesExtension {
    void setBlockStateProperty(BlockState state);
    BlockState getBlockStateProperty();
    void setVariant(VariantBase<?> variant);
    VariantBase<?> getVariant();
}
