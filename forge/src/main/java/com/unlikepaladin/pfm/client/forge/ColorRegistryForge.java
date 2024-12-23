package com.unlikepaladin.pfm.client.forge;

import com.unlikepaladin.pfm.client.ColorRegistry;
import net.minecraft.client.render.RenderLayers;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;

public class ColorRegistryForge {
    public static void registerBlockColors(RegisterColorHandlersEvent.Block event){
        ColorRegistryImpl.blockColors = event.getBlockColors();
        ColorRegistry.registerBlockColors();
        ColorRegistryImpl.BLOCK_COLOR_PROVIDER_MAP.forEach((block, blockColorProvider) -> event.getBlockColors().registerColorProvider(blockColorProvider, block));
    }

    public static void registerBlockRenderLayers() {
        ColorRegistry.registerBlockRenderLayers();
        ColorRegistryImpl.BLOCK_RENDER_LAYER_MAP.forEach(RenderLayers::setRenderLayer);
    }
}
