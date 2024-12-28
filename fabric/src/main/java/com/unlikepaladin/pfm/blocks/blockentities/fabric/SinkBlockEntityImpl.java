package com.unlikepaladin.pfm.blocks.blockentities.fabric;

import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.blocks.blockentities.SinkBlockEntity;
import com.unlikepaladin.pfm.compat.cookingforblockheads.fabric.SinkBlockEntityBalm;
import net.minecraft.block.entity.BlockEntityType;

public class SinkBlockEntityImpl {
    public static BlockEntityType.BlockEntityFactory<? extends SinkBlockEntity> getFactory() {
        return PaladinFurnitureMod.getModList().contains("cookingforblockheads") ? SinkBlockEntityBalm::new : SinkBlockEntity::new;
    }
}
