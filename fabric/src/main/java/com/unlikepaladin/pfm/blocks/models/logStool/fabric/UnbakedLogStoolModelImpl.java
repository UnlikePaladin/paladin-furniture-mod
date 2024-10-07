package com.unlikepaladin.pfm.blocks.models.logStool.fabric;

import com.unlikepaladin.pfm.runtime.PFMRuntimeResources;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.util.Identifier;

import java.util.List;

public class UnbakedLogStoolModelImpl {
    public static BakedModel getBakedModel(Identifier modelId, ModelBakeSettings settings, List<BakedModel> modelParts) {
        if (PFMRuntimeResources.modelCacheMap.get(modelId).getBakedModels().containsKey(settings))
            return PFMRuntimeResources.modelCacheMap.get(modelId).getBakedModels().get(settings);

        BakedModel model = new FabricLogStoolModel(settings, modelParts);
        PFMRuntimeResources.modelCacheMap.get(modelId).getBakedModels().put(settings,model);
        return model;
    }
}
