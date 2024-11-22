package com.unlikepaladin.pfm.client.forge;

import net.minecraft.block.Block;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;

public class ColorRegistryImpl {
    public static final Map<Block, BlockColorProvider> BLOCK_COLOR_PROVIDER_MAP = new HashMap<>();
    public static final Map<Block, RenderLayer> BLOCK_RENDER_LAYER_MAP = new HashMap<>();
    public static final Map<Item, ItemColorProvider> ITEM_COLOR_PROVIDER_MAP = new HashMap<>();

    public static BlockColors blockColors;
    public static ItemColors itemColors;

    public static void registerBlockColor(Block block, BlockColorProvider blockColorProvider) {
        BLOCK_COLOR_PROVIDER_MAP.put(block, blockColorProvider);
    }

    public static void registerBlockToRenderLayer(Block block, RenderLayer renderLayer) {
        BLOCK_RENDER_LAYER_MAP.put(block, renderLayer);
    }

    public static void registerItemColor(Item item, ItemColorProvider colorProvider) {
        ITEM_COLOR_PROVIDER_MAP.put(item, colorProvider);
    }

    public static BlockColorProvider getBlockColor(Block block) {
        if (BLOCK_COLOR_PROVIDER_MAP.containsKey(block)) {
            return BLOCK_COLOR_PROVIDER_MAP.get(block);
        }
        return ((BlockColorsExtension) blockColors).getColorMap().get(block.getRegistryEntry());
    }

    public static ItemColorProvider getItemColor(Item item) {
        if (ITEM_COLOR_PROVIDER_MAP.containsKey(item)) {
            return ITEM_COLOR_PROVIDER_MAP.get(item);
        }
        return ((ItemColorsExtension) itemColors).getColorMap().get(item.getRegistryEntry());
    }
}
