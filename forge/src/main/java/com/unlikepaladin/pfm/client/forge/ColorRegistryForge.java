package com.unlikepaladin.pfm.client.forge;

import com.unlikepaladin.pfm.client.ColorRegistry;
import net.minecraft.client.render.RenderLayers;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;

public class ColorRegistryForge {
    public static void registerBlockColors(RegisterColorHandlersEvent.Item event){
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
