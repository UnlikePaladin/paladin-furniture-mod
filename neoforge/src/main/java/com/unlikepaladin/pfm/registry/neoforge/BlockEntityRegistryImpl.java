package com.unlikepaladin.pfm.registry.neoforge;

import com.unlikepaladin.pfm.PaladinFurnitureMod;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;

import java.util.*;

public class BlockEntityRegistryImpl {
    public static Map<Identifier, BlockEntityType<?>> blockEntityTypes = new LinkedHashMap<>();

    public static <T extends BlockEntity>BlockEntityType<T> registerBlockEntity(String blockEntityId, Block[] block, BlockEntityType.BlockEntityFactory<T> factory) {
        BlockEntityType<T> blockEntityType = new BlockEntityType<>(factory, Set.of(block));
        blockEntityTypes.put(Identifier.of(PaladinFurnitureMod.MOD_ID, blockEntityId), blockEntityType);
        return blockEntityType;
    }
}
