package com.unlikepaladin.pfm.client.neoforge;

import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.blocks.DyeableFurnitureBlock;
import com.unlikepaladin.pfm.client.model.PFMBedModelRenderer;
import com.unlikepaladin.pfm.client.model.PFMItemModel;
import com.unlikepaladin.pfm.registry.PaladinFurnitureModBlocksItems;
import net.minecraft.block.Block;
import net.minecraft.util.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterItemModelsEvent;
import net.neoforged.neoforge.client.event.RegisterSpecialBlockModelRendererEvent;
import net.neoforged.neoforge.client.event.RegisterSpecialModelRendererEvent;

@EventBusSubscriber(modid = "pfm", bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ItemModelRegistry {
    @SubscribeEvent
    public static void registerItemModelTypes(RegisterItemModelsEvent event) {
        event.register(Identifier.of(PaladinFurnitureMod.MOD_ID, "furniture_model"), PFMItemModel.Unbaked.CODEC);
    }

    @SubscribeEvent
    public static void registerSpecialModelRenderer(RegisterSpecialModelRendererEvent event) {
        event.register(Identifier.of(PaladinFurnitureMod.MOD_ID, "pfm_bed"), PFMBedModelRenderer.Unbaked.CODEC);
    }

    @SubscribeEvent
    public static void registerSpecialModelRenderers(RegisterSpecialBlockModelRendererEvent event) {
        for (Block block : PaladinFurnitureModBlocksItems.getBeds()) {
            if (block instanceof DyeableFurnitureBlock)
                event.register(block, new PFMBedModelRenderer.Unbaked(((DyeableFurnitureBlock) block).getPFMColor()));
        }
    }


}
