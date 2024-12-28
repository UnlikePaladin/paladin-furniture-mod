package com.unlikepaladin.pfm.blocks.blockentities.fabric;

import com.unlikepaladin.pfm.blocks.blockentities.SinkBlockEntity;
import net.minecraft.block.entity.BlockEntityType;

import java.util.function.Supplier;

public class SinkBlockEntityImpl {
    public static Supplier<? extends SinkBlockEntity> getFactory() {
        return SinkBlockEntity::new;
    }
}
