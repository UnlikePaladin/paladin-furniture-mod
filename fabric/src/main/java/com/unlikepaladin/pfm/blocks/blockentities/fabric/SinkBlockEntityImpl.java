package com.unlikepaladin.pfm.blocks.blockentities.fabric;

import com.unlikepaladin.pfm.blocks.blockentities.SinkBlockEntity;
import net.minecraft.block.entity.BlockEntityType;

public class SinkBlockEntityImpl {
    public static BlockEntityType.BlockEntityFactory<? extends SinkBlockEntity> getFactory() {
        return SinkBlockEntity::new;
    }
}
