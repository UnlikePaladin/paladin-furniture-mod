package com.unlikepaladin.pfm.blocks.models.classicStool;

import com.mojang.datafixers.util.Pair;
import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.data.materials.StoneVariant;
import com.unlikepaladin.pfm.data.materials.StoneVariantRegistry;
import com.unlikepaladin.pfm.data.materials.WoodVariant;
import com.unlikepaladin.pfm.data.materials.WoodVariantRegistry;
import com.unlikepaladin.pfm.runtime.PFMBakedModelContainer;
import com.unlikepaladin.pfm.runtime.PFMRuntimeResources;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.client.render.model.*;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class UnbakedClassicStoolModel implements UnbakedModel {
    public static final Identifier[] CLASSIC_STOOL_PARTS_BASE = new Identifier[] {
            Identifier.of(PaladinFurnitureMod.MOD_ID, "block/classic_stool/classic_stool"),
            Identifier.of(PaladinFurnitureMod.MOD_ID, "block/classic_stool/classic_stool_tucked")
    };

    public static final Identifier STOOL_MODEL_ID = Identifier.of(PaladinFurnitureMod.MOD_ID, "block/classic_stool");
    public static final List<Identifier> CLASSIC_STOOL_MODEL_IDS = new ArrayList<>() {
        {
            for(WoodVariant variant : WoodVariantRegistry.getVariants()){
                add(Identifier.of(PaladinFurnitureMod.MOD_ID, "item/" + variant.asString() + "_classic_stool"));
                if (variant.hasStripped())
                    add(Identifier.of(PaladinFurnitureMod.MOD_ID, "item/stripped_" + variant.asString() + "_classic_stool"));
            }
            for(StoneVariant variant : StoneVariantRegistry.getVariants()){
                add(Identifier.of(PaladinFurnitureMod.MOD_ID, "item/" + variant.asString() + "_classic_stool"));
            }
            add(STOOL_MODEL_ID);
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
    public BakedModel bake(Baker loader, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings rotationContainer) {
        if (PFMRuntimeResources.modelCacheMap.containsKey(STOOL_MODEL_ID) && PFMRuntimeResources.modelCacheMap.get(STOOL_MODEL_ID).getCachedModelParts().containsKey(rotationContainer))
            return getBakedModel(STOOL_MODEL_ID, rotationContainer, PFMRuntimeResources.modelCacheMap.get(STOOL_MODEL_ID).getCachedModelParts().get(rotationContainer));

        if (!PFMRuntimeResources.modelCacheMap.containsKey(STOOL_MODEL_ID))
            PFMRuntimeResources.modelCacheMap.put(STOOL_MODEL_ID, new PFMBakedModelContainer());

        List<BakedModel> bakedModelList = new ArrayList<>();
        for (Identifier modelPart : CLASSIC_STOOL_PARTS_BASE) {
            bakedModelList.add(loader.bake(modelPart, rotationContainer));
        }

        PFMRuntimeResources.modelCacheMap.get(STOOL_MODEL_ID).getCachedModelParts().put(rotationContainer, bakedModelList);
        return getBakedModel(STOOL_MODEL_ID, rotationContainer, bakedModelList);
    }

    @ExpectPlatform
    public static BakedModel getBakedModel(Identifier modelId, ModelBakeSettings settings, List<BakedModel> modelParts) {
        throw new RuntimeException("Method wasn't replaced correctly");
    }

    @Override
    public void resolve(Resolver resolver) {
        for (Identifier c : CLASSIC_STOOL_PARTS_BASE)
            resolver.resolve(c);
    }
}
