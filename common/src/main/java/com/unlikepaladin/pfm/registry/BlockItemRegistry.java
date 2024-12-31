package com.unlikepaladin.pfm.registry;

import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.blocks.MirrorBlock;
import com.unlikepaladin.pfm.items.LightSwitchItem;
import com.unlikepaladin.pfm.items.ShowerHandleItem;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.util.Pair;

import java.util.function.Supplier;

public class BlockItemRegistry {
    public static void registerCommonBlocks() {
        registerFurniture("working_table", PaladinFurnitureModBlocksItems.WORKING_TABLE, true);

        registerBlock("oak_herringbone_planks", PaladinFurnitureModBlocksItems.OAK_HERRINGBONE_PLANKS, true);
        registerBlock("spruce_herringbone_planks", PaladinFurnitureModBlocksItems.SPRUCE_HERRINGBONE_PLANKS, true);
        registerBlock("birch_herringbone_planks", PaladinFurnitureModBlocksItems.BIRCH_HERRINGBONE_PLANKS, true);
        registerBlock("jungle_herringbone_planks", PaladinFurnitureModBlocksItems.JUNGLE_HERRINGBONE_PLANKS, true);
        registerBlock("acacia_herringbone_planks", PaladinFurnitureModBlocksItems.ACACIA_HERRINGBONE_PLANKS, true);
        registerBlock("dark_oak_herringbone_planks", PaladinFurnitureModBlocksItems.DARK_OAK_HERRINGBONE_PLANKS, true);
        registerBlock("crimson_herringbone_planks", PaladinFurnitureModBlocksItems.CRIMSON_HERRINGBONE_PLANKS, true);
        registerBlock("warped_herringbone_planks", PaladinFurnitureModBlocksItems.WARPED_HERRINGBONE_PLANKS, true);
        registerBlock("mangrove_herringbone_planks", PaladinFurnitureModBlocksItems.MANGROVE_HERRINGBONE_PLANKS, true);
        registerBlock("bamboo_herringbone_planks", PaladinFurnitureModBlocksItems.BAMBOO_HERRINGBONE_PLANKS, true);
        registerBlock("cherry_herringbone_planks", PaladinFurnitureModBlocksItems.CHERRY_HERRINGBONE_PLANKS, true);
        registerBlock("pale_oak_herringbone_planks", PaladinFurnitureModBlocksItems.PALE_OAK_HERRINGBONE_PLANKS, true);

        registerBlock("raw_concrete", PaladinFurnitureModBlocksItems.RAW_CONCRETE, true);
        registerBlock("raw_concrete_powder", PaladinFurnitureModBlocksItems.RAW_CONCRETE_POWDER, true);
        registerBlock("iron_chain", PaladinFurnitureModBlocksItems.IRON_CHAIN, true);
        registerBlock("leather_block", PaladinFurnitureModBlocksItems.LEATHER_BLOCK, true);
    }

    public static void registerBlock(String blockName, Block block, BlockItem item, Pair<String, ItemGroup> group) {
        PaladinFurnitureModBlocksItems.BLOCKS.add(block);
        registerBlockPlatformSpecific(blockName, block, false);
        registerItemPlatformSpecific(blockName, ()-> item, group);
    }

    public static void registerFurniture(String blockName, Block block, boolean registerItem) {
        if (registerItem) {
            PaladinFurnitureModBlocksItems.BLOCKS.add(block);
            registerBlockItemPlatformSpecific(blockName, block, PaladinFurnitureMod.FURNITURE_GROUP);
        }
        registerBlockPlatformSpecific(blockName, block, false);
    }

    public static void registerFurniture(String blockName, Block block, int count) {
        PaladinFurnitureModBlocksItems.BLOCKS.add(block);
        registerBlockPlatformSpecific(blockName, block, false);
        registerItemPlatformSpecific(blockName, () -> new BlockItem(block, new Item.Settings().maxCount(count)), PaladinFurnitureMod.FURNITURE_GROUP);
    }

    public static void registerBlock(String blockName, Block block, boolean registerItem) {
        if (registerItem) {
            PaladinFurnitureModBlocksItems.BLOCKS.add(block);
            registerBlockItemPlatformSpecific(blockName, block, new Pair<>("building_blocks", Registries.ITEM_GROUP.get(ItemGroups.BUILDING_BLOCKS)));
        }
        registerBlockPlatformSpecific(blockName, block, false);
    }

    @ExpectPlatform
    public static void registerBlockItemPlatformSpecific(String itemName, Block block, Pair<String, ItemGroup> group) {
        throw new UnsupportedOperationException();
    }

    @ExpectPlatform
    public static void registerBlockPlatformSpecific(String blockName, Block block, boolean registerItem) {
        throw new UnsupportedOperationException();
    }

    @ExpectPlatform
    public static void registerItemPlatformSpecific(String itemName, Supplier<Item> itemSupplier, Pair<String, ItemGroup> group) {
        throw new UnsupportedOperationException();
    }

    @ExpectPlatform
    public static boolean isModLoaded(String modId) {
        throw new UnsupportedOperationException();
    }

    public static void registerCommonItems() {
        //Dye Kits
        registerItemPlatformSpecific("furniture_book", ()-> PaladinFurnitureModBlocksItems.FURNITURE_BOOK, PaladinFurnitureMod.FURNITURE_GROUP);
        registerItemPlatformSpecific("dye_kit_red", ()-> PaladinFurnitureModBlocksItems.DYE_KIT_RED, PaladinFurnitureMod.DYE_KITS);
        registerItemPlatformSpecific("dye_kit_orange", ()-> PaladinFurnitureModBlocksItems.DYE_KIT_ORANGE, PaladinFurnitureMod.DYE_KITS);
        registerItemPlatformSpecific("dye_kit_yellow", ()-> PaladinFurnitureModBlocksItems.DYE_KIT_YELLOW, PaladinFurnitureMod.DYE_KITS);
        registerItemPlatformSpecific("dye_kit_lime", ()-> PaladinFurnitureModBlocksItems.DYE_KIT_LIME, PaladinFurnitureMod.DYE_KITS);
        registerItemPlatformSpecific("dye_kit_green", ()-> PaladinFurnitureModBlocksItems.DYE_KIT_GREEN, PaladinFurnitureMod.DYE_KITS);
        registerItemPlatformSpecific("dye_kit_cyan", ()-> PaladinFurnitureModBlocksItems.DYE_KIT_CYAN, PaladinFurnitureMod.DYE_KITS);
        registerItemPlatformSpecific("dye_kit_light_blue", ()-> PaladinFurnitureModBlocksItems.DYE_KIT_LIGHT_BLUE, PaladinFurnitureMod.DYE_KITS);
        registerItemPlatformSpecific("dye_kit_blue", ()-> PaladinFurnitureModBlocksItems.DYE_KIT_BLUE, PaladinFurnitureMod.DYE_KITS);
        registerItemPlatformSpecific("dye_kit_purple", ()-> PaladinFurnitureModBlocksItems.DYE_KIT_PURPLE, PaladinFurnitureMod.DYE_KITS);
        registerItemPlatformSpecific("dye_kit_magenta", ()-> PaladinFurnitureModBlocksItems.DYE_KIT_MAGENTA, PaladinFurnitureMod.DYE_KITS);
        registerItemPlatformSpecific("dye_kit_pink", ()-> PaladinFurnitureModBlocksItems.DYE_KIT_PINK, PaladinFurnitureMod.DYE_KITS);
        registerItemPlatformSpecific("dye_kit_brown", ()-> PaladinFurnitureModBlocksItems.DYE_KIT_BROWN, PaladinFurnitureMod.DYE_KITS);
        registerItemPlatformSpecific("dye_kit_black", ()-> PaladinFurnitureModBlocksItems.DYE_KIT_BLACK, PaladinFurnitureMod.DYE_KITS);
        registerItemPlatformSpecific("dye_kit_gray", ()-> PaladinFurnitureModBlocksItems.DYE_KIT_GRAY, PaladinFurnitureMod.DYE_KITS);
        registerItemPlatformSpecific("dye_kit_light_gray", ()-> PaladinFurnitureModBlocksItems.DYE_KIT_LIGHT_GRAY, PaladinFurnitureMod.DYE_KITS);
        registerItemPlatformSpecific("dye_kit_white", ()-> PaladinFurnitureModBlocksItems.DYE_KIT_WHITE, PaladinFurnitureMod.DYE_KITS);
    }
}
