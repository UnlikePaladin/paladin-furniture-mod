package com.unlikepaladin.pfm.registry.dynamic.neoforge;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Lifecycle;
import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.blocks.AbstractSittableBlock;
import com.unlikepaladin.pfm.blocks.SimpleBedBlock;
import com.unlikepaladin.pfm.neoforge.PaladinFurnitureModNeoForge;
import com.unlikepaladin.pfm.registry.PaladinFurnitureModBlocksItems;
import com.unlikepaladin.pfm.registry.dynamic.LateBlockRegistry;
import com.unlikepaladin.pfm.registry.neoforge.BlockItemRegistryImpl;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.BedPart;
import net.minecraft.item.*;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.world.poi.PointOfInterestType;
import net.minecraft.world.poi.PointOfInterestTypes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Supplier;

@EventBusSubscriber(modid = "pfm", bus = EventBusSubscriber.Bus.MOD)
public class LateBlockRegistryImpl {

    public static Map<String, Block> blocks = new LinkedHashMap<>();
    public static Map<String, Supplier<Item>> items = new LinkedHashMap<>();
    public static void registerLateItem(String itemName, Supplier<Item> itemSup, Pair<String, ItemGroup> group) {
        items.put(itemName, itemSup);
        BlockItemRegistryImpl.itemNameToGroup.put(itemName, group);
    }

    public static <T extends Block> T registerLateBlock(String blockId, Supplier<T> blockSup, boolean registerItem, Pair<String, ItemGroup> group) {
        T block = blockSup.get();
        if (registerItem) {
            PaladinFurnitureModBlocksItems.BLOCKS.add(block);
            registerBlockItemPlatformSpecific(blockId, block, group);
        }
        blocks.put(blockId, block);
        return block;
    }

    public static void registerBlockItemPlatformSpecific(String itemName, Block block, Pair<String, ItemGroup> group) {
        if (AbstractSittableBlock.isWoodBased(block.getDefaultState())) {
            registerLateItem(itemName, () -> new BlockItem(block, new Item.Settings().useBlockPrefixedTranslationKey().registryKey(LateBlockRegistry.getItemRegistryKey(itemName))) {
                @Override
                public int getBurnTime(ItemStack itemStack, @Nullable RecipeType<?> recipeType, FuelRegistry fuelValues) {
                    return 300;
                }
            }, group);
        }
        registerLateItem(itemName, () -> new BlockItem(block, new Item.Settings().useBlockPrefixedTranslationKey().registryKey(LateBlockRegistry.getItemRegistryKey(itemName))), group);
    }


    public static void registerBlocks(Registry<Block> blockRegisterEvent) {
        try {
            LateBlockRegistry.registerBlocks();
        } catch (InvocationTargetException | InstantiationException |
                 IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
        blocks.forEach((blockName, block) -> Registry.register(Registries.BLOCK, Identifier.of(PaladinFurnitureMod.MOD_ID, blockName), block));
    }

    public static void registerItems(Registry<Item> itemIForgeRegistry) {
        items.forEach((itemName, itemSup) -> {
            Item item = itemSup.get();
            Registry.register(Registries.ITEM, Identifier.of(PaladinFurnitureMod.MOD_ID, itemName), item);
            if (!PaladinFurnitureModBlocksItems.ITEM_GROUP_LIST_MAP.containsKey(BlockItemRegistryImpl.itemNameToGroup.get(itemName))) {
                PaladinFurnitureModBlocksItems.ITEM_GROUP_LIST_MAP.put(BlockItemRegistryImpl.itemNameToGroup.get(itemName), new LinkedHashSet<>());
            }
            PaladinFurnitureModBlocksItems.ITEM_GROUP_LIST_MAP.get(BlockItemRegistryImpl.itemNameToGroup.get(itemName)).add(item);
        });
    }

    public static <T extends Block> T registerLateBlockClassic(String blockId, T block, boolean registerItem, Pair<String, ItemGroup> group) {
        if (registerItem) {
            PaladinFurnitureModBlocksItems.BLOCKS.add(block);
            registerBlockItemPlatformSpecific(blockId, block, group);
        }
        blocks.put(blockId, block);
        return block;
    }

    @SubscribeEvent
    public static void registerPOI(RegisterEvent event) {
        event.register(Registries.POINT_OF_INTEREST_TYPE.getKey(), pointOfInterestTypeRegisterHelper -> {
            PaladinFurnitureModNeoForge.replaceHomePOIStates();
        });
    }
}
