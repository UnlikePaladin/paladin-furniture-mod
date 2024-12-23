package com.unlikepaladin.pfm.client.forge;

import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.blocks.DyeableFurnitureBlock;
import com.unlikepaladin.pfm.client.model.PFMBedModelRenderer;
import com.unlikepaladin.pfm.client.model.PFMItemModel;
import com.unlikepaladin.pfm.registry.PaladinFurnitureModBlocksItems;
import net.minecraft.block.Block;
import net.minecraft.client.render.item.model.ItemModelTypes;
import net.minecraft.client.render.item.model.special.SpecialModelTypes;
import net.minecraft.util.Identifier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.CreateSpecialBlockRendererEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class ItemModelRegistry {
    public static void registerItemModelTypes() {
        ItemModelTypes.ID_MAPPER.put(Identifier.of(PaladinFurnitureMod.MOD_ID, "furniture_model"), PFMItemModel.Unbaked.CODEC);
        SpecialModelTypes.ID_MAPPER.put(Identifier.of(PaladinFurnitureMod.MOD_ID, "pfm_bed"), PFMBedModelRenderer.Unbaked.CODEC);
    }


    public static void registerSpecialModelRenderers(CreateSpecialBlockRendererEvent event) {
        for (Block block : PaladinFurnitureModBlocksItems.getBeds()) {
            if (block instanceof DyeableFurnitureBlock)
                event.register(block, new PFMBedModelRenderer.Unbaked(((DyeableFurnitureBlock) block).getPFMColor()));
        }
    }


}
