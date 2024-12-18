package com.unlikepaladin.pfm.registry.neoforge;

import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.blocks.blockentities.neoforge.FreezerBlockEntityImpl;
import com.unlikepaladin.pfm.compat.cookingforblockheads.neoforge.PFMCookingForBlockHeadsCompat;
import com.unlikepaladin.pfm.items.neoforge.FurnitureGuideBookImpl;
import com.unlikepaladin.pfm.registry.BlockEntities;
import com.unlikepaladin.pfm.registry.BlockItemRegistry;
import com.unlikepaladin.pfm.registry.PaladinFurnitureModBlocksItems;
import com.unlikepaladin.pfm.registry.dynamic.LateBlockRegistry;
import net.minecraft.block.Blocks;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.wrapper.ForwardingItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.*;

@EventBusSubscriber(modid = "pfm", bus = EventBusSubscriber.Bus.MOD)
public class BlockItemRegistryNeoForge {
    @SubscribeEvent
    public static void registerBlocks(RegisterEvent event) {
        event.register(Registries.BLOCK.getKey(), blockRegisterHelper -> {
            BlockItemRegistry.registerCommonBlocks();
            BlockItemRegistryImpl.blocks.forEach((blockName, block) -> blockRegisterHelper.register(Identifier.of(PaladinFurnitureMod.MOD_ID, blockName), block));
        });
    }

    @SubscribeEvent
    public static void registerItems(RegisterEvent event) {
        event.register(Registries.ITEM.getKey(), itemRegisterHelper -> {
            if (!PaladinFurnitureModBlocksItems.ITEM_GROUP_LIST_MAP.containsKey(PaladinFurnitureMod.FURNITURE_GROUP)) {
                PaladinFurnitureModBlocksItems.ITEM_GROUP_LIST_MAP.put(PaladinFurnitureMod.FURNITURE_GROUP, new LinkedHashSet<>());
            }
            PaladinFurnitureModBlocksItems.FURNITURE_BOOK = new FurnitureGuideBookImpl(new Item.Settings().rarity(Rarity.RARE).maxCount(1).registryKey(LateBlockRegistry.getItemRegistryKey("furniture_book")));
            PaladinFurnitureModBlocksItems.ITEM_GROUP_LIST_MAP.get(PaladinFurnitureMod.FURNITURE_GROUP).add(PaladinFurnitureModBlocksItems.FURNITURE_BOOK);
            BlockItemRegistry.registerCommonItems();
            BlockItemRegistryImpl.items.forEach((itemName, itemSupplier) -> {
                Item item = itemSupplier.get();
                if (!PaladinFurnitureModBlocksItems.ITEM_GROUP_LIST_MAP.containsKey(BlockItemRegistryImpl.itemNameToGroup.get(itemName))) {
                    PaladinFurnitureModBlocksItems.ITEM_GROUP_LIST_MAP.put(BlockItemRegistryImpl.itemNameToGroup.get(itemName), new LinkedHashSet<>());
                }
                PaladinFurnitureModBlocksItems.ITEM_GROUP_LIST_MAP.get(BlockItemRegistryImpl.itemNameToGroup.get(itemName)).add(item);
                itemRegisterHelper.register(Identifier.of(PaladinFurnitureMod.MOD_ID, itemName), item);
            });
        });
    }

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntities.FREEZER_BLOCK_ENTITY, (freezerBlockEntity, side) -> {
            if (side == null) {
                return new ForwardingItemHandler(() -> new InvWrapper(freezerBlockEntity));
            } else {
                return new ForwardingItemHandler(() -> new SidedInvWrapper(freezerBlockEntity, side));
            }
        });

        if (ModList.get().isLoaded("cookingforblockheads")) {
            PFMCookingForBlockHeadsCompat.registerCapabilities(event);
        }
    }
}
