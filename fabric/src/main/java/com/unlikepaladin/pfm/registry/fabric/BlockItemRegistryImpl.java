package com.unlikepaladin.pfm.registry.fabric;

import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.blocks.AbstractSittableBlock;
import com.unlikepaladin.pfm.registry.PaladinFurnitureModBlocksItems;
import com.unlikepaladin.pfm.registry.dynamic.LateBlockRegistry;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.fabricmc.fabric.api.registry.FuelRegistryEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registry;
import net.minecraft.util.Pair;

import java.util.LinkedHashSet;
import java.util.function.Supplier;

public class BlockItemRegistryImpl {

    public static void registerItemPlatformSpecific(String itemName, Supplier<Item> itemSupplier, Pair<String, ItemGroup> group) {
        Item item = itemSupplier.get();
        Registry.register(Registries.ITEM, Identifier.of(PaladinFurnitureMod.MOD_ID, itemName), item);
        if (!PaladinFurnitureModBlocksItems.ITEM_GROUP_LIST_MAP.containsKey(group)) {
            PaladinFurnitureModBlocksItems.ITEM_GROUP_LIST_MAP.put(group, new LinkedHashSet<>());
        }
        PaladinFurnitureModBlocksItems.ITEM_GROUP_LIST_MAP.get(group).add(item);
        ItemGroupEvents.modifyEntriesEvent(Registries.ITEM_GROUP.getKey(group.getRight()).get()).register(entries -> entries.add(item));
    }

    public static void registerBlockItemPlatformSpecific(String itemName, Block block, Pair<String, ItemGroup> group) {
        PaladinFurnitureModBlocksItems.BLOCKS.add(block);
        registerItemPlatformSpecific(itemName, () -> new BlockItem(block, new Item.Settings().registryKey(LateBlockRegistry.getItemRegistryKey(itemName)).useBlockPrefixedTranslationKey()), group);
        if (AbstractSittableBlock.isWoodBased(block.getDefaultState())) {
            FlammableBlockRegistry.getDefaultInstance().add(block, 20, 5);
            FuelRegistryEvents.BUILD.register((builder, context) -> {
                builder.add(block, 300);
            });
        }
    }

    public static void registerBlockPlatformSpecific(String blockName, Block block, boolean registerItem) {
        if (registerItem) {
            PaladinFurnitureModBlocksItems.BLOCKS.add(block);
            registerBlockItemPlatformSpecific(blockName, block, new Pair<>("building_blocks", Registries.ITEM_GROUP.get(ItemGroups.BUILDING_BLOCKS)));
        }
        Registry.register(Registries.BLOCK, Identifier.of(PaladinFurnitureMod.MOD_ID, blockName),  block);
    }

    public static boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }
}
