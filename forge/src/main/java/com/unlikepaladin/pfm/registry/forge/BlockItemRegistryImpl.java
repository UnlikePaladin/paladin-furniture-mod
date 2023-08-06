package com.unlikepaladin.pfm.registry.forge;

import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.registry.PaladinFurnitureModBlocksItems;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.*;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraftforge.fml.ModList;
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
            registerBlockItemPlatformSpecific(blockId, block, new Pair<>("building_blocks", ItemGroups.BUILDING_BLOCKS));
        }
        blocks.put(blockId, block);
    }

    public static void registerBlockItemPlatformSpecific(String itemName, Block block, Pair<String, ItemGroup> group) {
        if (block.getDefaultState().getMaterial() == Material.WOOD || block.getDefaultState().getMaterial() == Material.WOOL) {
            registerItemPlatformSpecific(itemName, () -> new BlockItem(block, new Item.Settings()) {
                @Override
                public int getBurnTime(ItemStack itemStack, @Nullable RecipeType<?> recipeType) {
                    return 300;
                }
            }, group);
        }
        registerItemPlatformSpecific(itemName, () -> new BlockItem(block, new Item.Settings()), group);
    }

    public static boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }
}
