package com.unlikepaladin.pfm.client.fabric;

import com.unlikepaladin.pfm.blocks.models.basicTable.UnbakedBasicTableModel;
import com.unlikepaladin.pfm.blocks.models.bed.UnbakedBedModel;
import com.unlikepaladin.pfm.blocks.models.classicTable.UnbakedClassicTableModel;
import com.unlikepaladin.pfm.blocks.models.logTable.UnbakedLogTableModel;
import net.fabricmc.fabric.api.client.model.ExtraModelProvider;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

public class PFMExtraModelProvider implements ExtraModelProvider {
    @Override
    public void provideExtraModels(ResourceManager manager, Consumer<Identifier> out) {
        UnbakedBedModel.ALL_MODEL_IDS.forEach(out::accept);
        UnbakedBasicTableModel.ALL_MODEL_IDS.forEach(out::accept);
        UnbakedClassicTableModel.ALL_MODEL_IDS.forEach(out::accept);
        UnbakedLogTableModel.ALL_MODEL_IDS.forEach(out::accept);
    }
}
