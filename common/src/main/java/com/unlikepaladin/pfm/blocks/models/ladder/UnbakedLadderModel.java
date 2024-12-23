package com.unlikepaladin.pfm.blocks.models.ladder;

import com.mojang.datafixers.util.Pair;
import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.data.materials.*;
import com.unlikepaladin.pfm.runtime.PFMBakedModelContainer;
import com.unlikepaladin.pfm.runtime.PFMRuntimeResources;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.client.render.model.*;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class UnbakedLadderModel implements UnbakedModel {
    public static final Identifier[] LADDER_PARTS_BASE = new Identifier[] {
            Identifier.of(PaladinFurnitureMod.MOD_ID, "block/simple_bunk_ladder/simple_ladder"),
            Identifier.of(PaladinFurnitureMod.MOD_ID, "block/simple_bunk_ladder/simple_ladder_top")
    };

    public static final Identifier LADDER_MODEL_ID = Identifier.of(PaladinFurnitureMod.MOD_ID, "block/simple_bunk_ladder");
    public static final List<Identifier> LADDER_MODEL_IDS = new ArrayList<>() {
        {
            for(WoodVariant variant : WoodVariantRegistry.getVariants()){
                add(Identifier.of(PaladinFurnitureMod.MOD_ID, "item/" + variant.asString() + "_simple_bunk_ladder"));
                if (variant.hasStripped())
                    add(Identifier.of(PaladinFurnitureMod.MOD_ID, "item/stripped_" + variant.asString() + "_simple_bunk_ladder"));
            }
            add(LADDER_MODEL_ID);
        }
    };

    private static final Identifier PARENT = Identifier.of("block/block");
    public Collection<Identifier> getModelDependencies() {
        return List.of(PARENT);
    }

    public Collection<SpriteIdentifier> getTextureDependencies(Function<Identifier, UnbakedModel> unbakedModelGetter, Set<Pair<String, String>> unresolvedTextureReferences) {
        return Collections.emptyList();
    }

    @Nullable
    @Override
    public BakedModel bake(ModelTextures textures, Baker loader, ModelBakeSettings rotationContainer, boolean ambientOcclusion, boolean isSideLit, ModelTransformation transformation){
        if (PFMRuntimeResources.modelCacheMap.containsKey(LADDER_MODEL_ID) && PFMRuntimeResources.modelCacheMap.get(LADDER_MODEL_ID).getCachedModelParts().containsKey(rotationContainer))
            return getBakedModel(LADDER_MODEL_ID, rotationContainer, PFMRuntimeResources.modelCacheMap.get(LADDER_MODEL_ID).getCachedModelParts().get(rotationContainer));

        if (!PFMRuntimeResources.modelCacheMap.containsKey(LADDER_MODEL_ID))
            PFMRuntimeResources.modelCacheMap.put(LADDER_MODEL_ID, new PFMBakedModelContainer());

        List<BakedModel> bakedModelList = new ArrayList<>();
        for (Identifier modelPart : LADDER_PARTS_BASE) {
            bakedModelList.add(loader.bake(modelPart, rotationContainer));
        }

        PFMRuntimeResources.modelCacheMap.get(LADDER_MODEL_ID).getCachedModelParts().put(rotationContainer, bakedModelList);
        return getBakedModel(LADDER_MODEL_ID, rotationContainer, bakedModelList);
    }

    @ExpectPlatform
    public static BakedModel getBakedModel(Identifier modelId, ModelBakeSettings settings, List<BakedModel> modelParts) {
        throw new RuntimeException("Method wasn't replaced correctly");
    }

    @Override
    public void resolve(Resolver resolver) {
        for (Identifier c : LADDER_PARTS_BASE)
            resolver.resolve(c);
    }
}
