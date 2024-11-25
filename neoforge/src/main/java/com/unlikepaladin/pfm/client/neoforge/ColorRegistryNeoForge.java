package com.unlikepaladin.pfm.client.neoforge;

import com.unlikepaladin.pfm.client.ColorRegistry;
import net.minecraft.client.render.RenderLayers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

public class ColorRegistryNeoForge {
    public static void registerBlockColors(RegisterColorHandlersEvent.Block event){
        ColorRegistryImpl.blockColors = event.getBlockColors();
        ColorRegistry.registerBlockColors();
        ColorRegistryImpl.BLOCK_COLOR_PROVIDER_MAP.forEach((block, blockColorProvider) -> event.getBlockColors().registerColorProvider(blockColorProvider, block));
    }

    public static void registerItemColors(RegisterColorHandlersEvent.Item event){
        ColorRegistryImpl.itemColors = event.getItemColors();
        ColorRegistry.registerItemColors();
        ColorRegistryImpl.ITEM_COLOR_PROVIDER_MAP.forEach((item, colorProvider) -> event.getItemColors().register(colorProvider, item));
    }

    public static void registerBlockRenderLayers() {
        ColorRegistry.registerBlockRenderLayers();
        ColorRegistryImpl.BLOCK_RENDER_LAYER_MAP.forEach(RenderLayers::setRenderLayer);
    }
}
