package com.unlikepaladin.pfm.registry;

import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.blocks.*;
import com.unlikepaladin.pfm.blocks.blockentities.*;
import com.unlikepaladin.pfm.compat.PFMModCompatibility;
import com.unlikepaladin.pfm.data.FurnitureBlock;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;

import java.util.ArrayList;
import java.util.List;

public class BlockEntityRegistry {

    public static void registerBlockEntities() {
        List<Block> storage9x3Blocks = new ArrayList<>(PaladinFurnitureModBlocksItems.furnitureEntryMap.get(KitchenDrawerBlock.class).getAllBlocks());
        storage9x3Blocks.addAll(PaladinFurnitureModBlocksItems.furnitureEntryMap.get(KitchenWallDrawerBlock.class).getAllBlocks());
        storage9x3Blocks.addAll(PaladinFurnitureModBlocksItems.furnitureEntryMap.get(KitchenCabinetBlock.class).getAllBlocks());
        storage9x3Blocks.addAll(PaladinFurnitureModBlocksItems.furnitureEntryMap.get(ClassicNightstandBlock.class).getAllBlocks());
        BlockEntities.DRAWER_BLOCK_ENTITY = registerBlockEntity("drawer_block_entity", storage9x3Blocks.toArray(Block[]::new), GenericStorageBlockEntity9x3.getFactory());
        BlockEntities.FRIDGE_BLOCK_ENTITY = registerBlockEntity("fridge_block_entity", new Block[]{PaladinFurnitureModBlocksItems.WHITE_FRIDGE, PaladinFurnitureModBlocksItems.XBOX_FRIDGE, PaladinFurnitureModBlocksItems.GRAY_FRIDGE, PaladinFurnitureModBlocksItems.IRON_FRIDGE}, FridgeBlockEntity.getFactory());
        BlockEntities.FREEZER_BLOCK_ENTITY = registerBlockEntity("freezer_block_entity", new Block[]{PaladinFurnitureModBlocksItems.WHITE_FREEZER, PaladinFurnitureModBlocksItems.GRAY_FREEZER, PaladinFurnitureModBlocksItems.IRON_FREEZER}, FreezerBlockEntity.getFactory());
        Block[] counterOvens = PaladinFurnitureModBlocksItems.furnitureEntryMap.get(KitchenCounterOvenBlock.class).getAllBlocks().toArray(Block[]::new);
        BlockEntities.KITCHEN_COUNTER_OVEN_BLOCK_ENTITY = registerBlockEntity("kitchen_counter_oven_block_entity", counterOvens, KitchenCounterOvenBlock.getFactory());
        BlockEntities.LIGHT_SWITCH_BLOCK_ENTITY = registerBlockEntity("light_switch_block_entity", new Block[]{PaladinFurnitureModBlocksItems.LIGHT_SWITCH}, LightSwitchBlockEntity::new);
        BlockEntities.STOVE_BLOCK_ENTITY = BlockEntityRegistry.registerBlockEntity("stove_block_entity", new Block[]{PaladinFurnitureModBlocksItems.WHITE_STOVE, PaladinFurnitureModBlocksItems.GRAY_STOVE, PaladinFurnitureModBlocksItems.IRON_STOVE}, StoveBlockEntity.getFactory());
        BlockEntities.STOVE_TOP_BLOCK_ENTITY = BlockEntityRegistry.registerBlockEntity("stovetop_block_entity", new Block[]{PaladinFurnitureModBlocksItems.KITCHEN_STOVETOP}, StovetopBlockEntity.getFactory());
        BlockEntities.MICROWAVE_BLOCK_ENTITY = BlockEntityRegistry.registerBlockEntity("microwave_block_entity", new Block[]{PaladinFurnitureModBlocksItems.IRON_MICROWAVE}, MicrowaveBlockEntity.getFactory());
        BlockEntities.PLATE_BLOCK_ENTITY = BlockEntityRegistry.registerBlockEntity("plate_block_entity", new Block[]{PaladinFurnitureModBlocksItems.BASIC_PLATE}, PlateBlockEntity.getFactory());
        BlockEntities.TOILET_BLOCK_ENTITY = BlockEntityRegistry.registerBlockEntity("toilet_block_entity", new Block[]{PaladinFurnitureModBlocksItems.BASIC_TOILET}, ToiletBlockEntity::new);
        BlockEntities.KITCHEN_DRAWER_SMALL_BLOCK_ENTITY = BlockEntityRegistry.registerBlockEntity("small_storage_block_entity", PaladinFurnitureModBlocksItems.furnitureEntryMap.get(KitchenWallDrawerSmallBlock.class).getAllBlocks().toArray(Block[]::new), GenericStorageBlockEntity3x3.getFactory());
        BlockEntities.TRASHCAN_BLOCK_ENTITY = BlockEntityRegistry.registerBlockEntity("trashcan_block_entity", new Block[]{PaladinFurnitureModBlocksItems.TRASHCAN, PaladinFurnitureModBlocksItems.MESH_TRASHCAN}, TrashcanBlockEntity.getFactory());
        List<Block> sinks = new ArrayList<>(PaladinFurnitureModBlocksItems.furnitureEntryMap.get(KitchenSinkBlock.class).getAllBlocks());
        sinks.addAll(PaladinFurnitureModBlocksItems.furnitureEntryMap.get(BasicSinkBlock.class).getAllBlocks());
        BlockEntities.SINK_BLOCK_ENTITY = BlockEntityRegistry.registerBlockEntity("sink_block_entity", sinks.toArray(Block[]::new), SinkBlockEntity::new);
        BlockEntities.SHOWER_HEAD_BLOCK_ENTITY = BlockEntityRegistry.registerBlockEntity("shower_head_block_entity", new Block[]{PaladinFurnitureModBlocksItems.BASIC_SHOWER_HEAD}, ShowerHeadBlockEntity.getFactory());
        BlockEntities.SHOWER_HANDLE_BLOCK_ENTITY = BlockEntityRegistry.registerBlockEntity("shower_handle_block_entity", new Block[]{PaladinFurnitureModBlocksItems.BASIC_SHOWER_HANDLE}, ShowerHandleBlockEntity::new);
        BlockEntities.BATHTUB_BLOCK_ENTITY = BlockEntityRegistry.registerBlockEntity("bathtub_block_entity", new Block[]{PaladinFurnitureModBlocksItems.BASIC_BATHTUB}, BathtubBlockEntity::new);
        BlockEntities.LAMP_BLOCK_ENTITY = BlockEntityRegistry.registerBlockEntity("lamp_block_entity", new Block[]{PaladinFurnitureModBlocksItems.BASIC_LAMP}, LampBlockEntity.getFactory());
        BlockEntities.TOASTER_BLOCK_ENTITY = BlockEntityRegistry.registerBlockEntity("iron_toaster", new Block[]{PaladinFurnitureModBlocksItems.TOASTER_BLOCK}, PFMToasterBlockEntity.getFactory());
        BlockEntities.BED_BLOCK_ENTITY = BlockEntityRegistry.registerBlockEntity("bed_block_entity", PaladinFurnitureModBlocksItems.getBeds(), PFMBedBlockEntity::new);

        PaladinFurnitureMod.pfmModCompatibilities.forEach(PFMModCompatibility::registerBlockEntityTypes);
    }

    @ExpectPlatform
    public static <T extends BlockEntity>BlockEntityType<T> registerBlockEntity(String id, Block[] block, BlockEntityType.BlockEntityFactory<T> factory) {
        throw new RuntimeException();
    }
}
