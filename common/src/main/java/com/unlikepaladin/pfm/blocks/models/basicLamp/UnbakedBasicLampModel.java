package com.unlikepaladin.pfm.blocks.models.basicLamp;

import com.mojang.datafixers.util.Pair;
import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.data.materials.*;
import com.unlikepaladin.pfm.runtime.PFMBakedModelContainer;
import com.unlikepaladin.pfm.runtime.PFMRuntimeResources;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.client.render.model.*;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class UnbakedBasicLampModel implements UnbakedModel {

    public UnbakedBasicLampModel() {
    }

    public static final List<Identifier> LAMP_MODEL_IDS = new ArrayList<>() {
        {
            add(Identifier.of(PaladinFurnitureMod.MOD_ID, "block/basic_lamp"));
            add(Identifier.of(PaladinFurnitureMod.MOD_ID, "item/basic_lamp"));
        }
    };

    public static Identifier getItemModelId() {
        return PaladinFurnitureMod.getLoader() == PaladinFurnitureMod.Loader.FORGE ? Identifier.of("builtin/entity") : LAMP_MODEL_IDS.get(1);
    }

    public static final List<String> MODEL_PARTS_BASE = new ArrayList<>() {{
       add("block/basic_lamp/basic_lamp_bottom");
       add("block/basic_lamp/basic_lamp_middle");
       add("block/basic_lamp/basic_lamp_single");
        add("block/basic_lamp/basic_lamp_top");
    }};

    public static final List<String> STATIC_PARTS = new ArrayList<>() {{
        add("block/basic_lamp/basic_lamp_shade");
        add("block/basic_lamp/basic_lamp_light_bulb_off");
        add("block/basic_lamp/basic_lamp_light_bulb_on");
    }};
    private static final Identifier PARENT = Identifier.of("block/block");
    public static final List<Identifier> ALL_MODEL_IDS = new ArrayList<>() {
        {
            for (String part : MODEL_PARTS_BASE) {
                add(Identifier.of(PaladinFurnitureMod.MOD_ID, part));
            }
            for (String part : STATIC_PARTS) {
                add(Identifier.of(PaladinFurnitureMod.MOD_ID, part));
            }
        }
    };


    Map<WoodVariant, SpriteIdentifier> textureMap = new HashMap<>();

    public Collection<SpriteIdentifier> getTextureDependencies(Function<Identifier, UnbakedModel> unbakedModelGetter, Set<Pair<String, String>> unresolvedTextureReferences) {
        return Collections.emptyList();
    }

    @Nullable
    @Override
    public BakedModel bake(Baker loader, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings rotationContainer) {
        if (PFMRuntimeResources.modelCacheMap.containsKey(LAMP_MODEL_IDS.get(0)) && PFMRuntimeResources.modelCacheMap.get(LAMP_MODEL_IDS.get(0)).getCachedModelParts().containsKey(rotationContainer))
            return getBakedModel(LAMP_MODEL_IDS.get(0), rotationContainer, PFMRuntimeResources.modelCacheMap.get(LAMP_MODEL_IDS.get(0)).getCachedModelParts().get(rotationContainer));

        if (!PFMRuntimeResources.modelCacheMap.containsKey(LAMP_MODEL_IDS.get(0)))
            PFMRuntimeResources.modelCacheMap.put(LAMP_MODEL_IDS.get(0), new PFMBakedModelContainer());

        List<BakedModel> bakedModelList = new ArrayList<>();
        for (Identifier modelPart : ALL_MODEL_IDS) {
            bakedModelList.add(loader.bake(modelPart, rotationContainer));
        }

        PFMRuntimeResources.modelCacheMap.get(LAMP_MODEL_IDS.get(0)).getCachedModelParts().put(rotationContainer, bakedModelList);
        return getBakedModel(LAMP_MODEL_IDS.get(0), rotationContainer, bakedModelList);
    }

    @ExpectPlatform
    public static BakedModel getBakedModel(Identifier modelId, ModelBakeSettings settings, List<BakedModel> modelParts) {
        throw new RuntimeException("Method wasn't replaced correctly");
    }

    @Override
    public void resolve(Resolver resolver) {
        for (Identifier c : ALL_MODEL_IDS)
            resolver.resolve(c);
    }
}
