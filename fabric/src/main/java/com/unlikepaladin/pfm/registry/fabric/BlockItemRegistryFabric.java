package com.unlikepaladin.pfm.registry.fabric;

import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.items.fabric.FurnitureGuideBookImpl;
import com.unlikepaladin.pfm.registry.BlockItemRegistry;
import com.unlikepaladin.pfm.registry.PaladinFurnitureModBlocksItems;
import com.unlikepaladin.pfm.registry.dynamic.LateBlockRegistry;
import net.minecraft.item.Item;
import net.minecraft.util.Rarity;

import java.util.ArrayList;
import java.util.LinkedHashSet;

public class BlockItemRegistryFabric {

    public static void registerBlocks(){
        //Block Registry
        BlockItemRegistry.registerCommonBlocks();
    }

    public static void registerItems() {
        PaladinFurnitureModBlocksItems.FURNITURE_BOOK = new FurnitureGuideBookImpl(new Item.Settings().rarity(Rarity.RARE).maxCount(1).registryKey(LateBlockRegistry.getItemRegistryKey("furniture_book")));
        if (!PaladinFurnitureModBlocksItems.ITEM_GROUP_LIST_MAP.containsKey(PaladinFurnitureMod.FURNITURE_GROUP)) {
            PaladinFurnitureModBlocksItems.ITEM_GROUP_LIST_MAP.put(PaladinFurnitureMod.FURNITURE_GROUP, new LinkedHashSet<>());
        }
        PaladinFurnitureModBlocksItems.ITEM_GROUP_LIST_MAP.get(PaladinFurnitureMod.FURNITURE_GROUP).add(PaladinFurnitureModBlocksItems.FURNITURE_BOOK);
        BlockItemRegistry.registerCommonItems();
    }

}
