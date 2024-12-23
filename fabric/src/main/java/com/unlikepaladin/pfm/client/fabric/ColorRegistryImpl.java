package com.unlikepaladin.pfm.client.fabric;

import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.Item;

public class ColorRegistryImpl {
    public static void registerBlockColor(Block block, BlockColorProvider blockColorProvider) {
        ColorProviderRegistry.BLOCK.register(blockColorProvider, block);
    }

    public static void registerBlockToRenderLayer(Block block, RenderLayer renderLayer) {
        BlockRenderLayerMap.INSTANCE.putBlock(block, renderLayer);
    }

    public static BlockColorProvider getBlockColor(Block block) {
        return ColorProviderRegistry.BLOCK.get(block);
    }

}
