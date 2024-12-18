package com.unlikepaladin.pfm.registry.neoforge;

import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.blocks.AbstractSittableBlock;
import com.unlikepaladin.pfm.registry.PaladinFurnitureModBlocksItems;
import com.unlikepaladin.pfm.registry.dynamic.LateBlockRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.*;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.neoforged.fml.ModList;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;

public class BlockItemRegistryImpl {
    public static Map<String, Supplier<Item>> items = new LinkedHashMap<>();
    public static Map<String, Block> blocks = new HashMap<>();
    public static Map<String, Pair<String, ItemGroup>> itemNameToGroup = new HashMap<>();

    public static void registerItemPlatformSpecific(String itemId, Supplier<Item> item, Pair<String, ItemGroup> group) {
        items.put(itemId,item);
        itemNameToGroup.put(itemId, group);
    }

    public static void registerBlockPlatformSpecific(String blockId, Block block, boolean registerItem) {
        if (registerItem) {
            PaladinFurnitureModBlocksItems.BLOCKS.add(block);
            registerBlockItemPlatformSpecific(blockId, block, new Pair<>("building_blocks", Registries.ITEM_GROUP.get(ItemGroups.BUILDING_BLOCKS)));
        }
        blocks.put(blockId, block);
    }

    public static void registerBlockItemPlatformSpecific(String itemName, Block block, Pair<String, ItemGroup> group) {
        if (AbstractSittableBlock.isWoodBased(block.getDefaultState())) {
            registerItemPlatformSpecific(itemName, () -> new BlockItem(block, new Item.Settings().useBlockPrefixedTranslationKey().registryKey(LateBlockRegistry.getItemRegistryKey(itemName))) {
                @Override
                public int getBurnTime(ItemStack itemStack, @Nullable RecipeType<?> recipeType, FuelRegistry fuelValues) {
                    return 300;
                }
            }, group);
        }
        registerItemPlatformSpecific(itemName, () -> new BlockItem(block, new Item.Settings().useBlockPrefixedTranslationKey().registryKey(LateBlockRegistry.getItemRegistryKey(itemName))), group);
    }

    public static boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }
}
