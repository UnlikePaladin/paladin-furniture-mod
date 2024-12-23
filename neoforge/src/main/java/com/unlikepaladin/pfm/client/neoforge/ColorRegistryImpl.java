package com.unlikepaladin.pfm.client.neoforge;

import net.minecraft.block.Block;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.Item;

import java.util.HashMap;
import java.util.Map;

public class ColorRegistryImpl {
    public static final Map<Block, BlockColorProvider> BLOCK_COLOR_PROVIDER_MAP = new HashMap<>();
    public static final Map<Block, RenderLayer> BLOCK_RENDER_LAYER_MAP = new HashMap<>();

    public static BlockColors blockColors;

    public static void registerBlockColor(Block block, BlockColorProvider blockColorProvider) {
        BLOCK_COLOR_PROVIDER_MAP.put(block, blockColorProvider);
    }

    public static void registerBlockToRenderLayer(Block block, RenderLayer renderLayer) {
        BLOCK_RENDER_LAYER_MAP.put(block, renderLayer);
    }

    public static BlockColorProvider getBlockColor(Block block) {
        if (BLOCK_COLOR_PROVIDER_MAP.containsKey(block)) {
            return BLOCK_COLOR_PROVIDER_MAP.get(block);
        }
        return ((BlockColorsExtension) blockColors).getColorMap().get(block);
    }
}
