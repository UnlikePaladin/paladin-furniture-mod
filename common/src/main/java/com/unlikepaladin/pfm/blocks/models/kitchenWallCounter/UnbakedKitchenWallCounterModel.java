package com.unlikepaladin.pfm.blocks.models.kitchenWallCounter;

import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.data.materials.*;
import com.unlikepaladin.pfm.runtime.PFMBakedModelContainer;
import com.unlikepaladin.pfm.runtime.PFMRuntimeResources;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.*;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Environment(EnvType.CLIENT)
public class UnbakedKitchenWallCounterModel implements UnbakedModel {
    public static final Identifier[] COUNTER_MODEL_PARTS_BASE = new Identifier[] {
            Identifier.of(PaladinFurnitureMod.MOD_ID, "block/kitchen_counter/kitchen_counter_middle"),
            Identifier.of(PaladinFurnitureMod.MOD_ID, "block/kitchen_counter/kitchen_counter_middle_inner_corner_left"),
            Identifier.of(PaladinFurnitureMod.MOD_ID, "block/kitchen_counter/kitchen_counter_middle_inner_corner_right"),
            Identifier.of(PaladinFurnitureMod.MOD_ID, "block/kitchen_counter/kitchen_counter_middle_outer_corner_left"),
            Identifier.of(PaladinFurnitureMod.MOD_ID, "block/kitchen_counter/kitchen_counter_middle_outer_corner_right")
    };

    private static final Identifier PARENT = Identifier.of("block/block");
    public static final Identifier COUNTER_MODEL_ID = Identifier.of(PaladinFurnitureMod.MOD_ID, "block/kitchen_wall_counter");
    public static final List<Identifier> COUNTER_MODEL_IDS = new ArrayList<>() {
        {
            for(WoodVariant variant : WoodVariantRegistry.getVariants()){
                add(Identifier.of(PaladinFurnitureMod.MOD_ID, "item/" + variant.asString() + "_kitchen_wall_counter"));
                if (variant.hasStripped())
                    add(Identifier.of(PaladinFurnitureMod.MOD_ID, "item/stripped_" + variant.asString() + "_kitchen_wall_counter"));
            }
            for(StoneVariant variant : StoneVariantRegistry.getVariants()){
                if (variant.identifier.getPath().equals("quartz"))
                    continue;
                add(Identifier.of(PaladinFurnitureMod.MOD_ID, "item/" + variant.asString() + "_kitchen_wall_counter"));
            }
            for(ExtraCounterVariant variant : ExtraCounterVariant.values()){
                add(Identifier.of(PaladinFurnitureMod.MOD_ID, "item/" + variant.asString() + "_kitchen_wall_counter"));
            }
            add(COUNTER_MODEL_ID);
        }
    };

    public Collection<SpriteIdentifier> getTextureDependencies(Function<Identifier, UnbakedModel> unbakedModelGetter, Set<com.mojang.datafixers.util.Pair<String, String>> unresolvedTextureReferences) {
        return Collections.emptyList();
    }

    @Nullable
    @Override
    public BakedModel bake(ModelTextures textures, Baker loader, ModelBakeSettings rotationContainer, boolean ambientOcclusion, boolean isSideLit, ModelTransformation transformation){
        if (PFMRuntimeResources.modelCacheMap.containsKey(COUNTER_MODEL_ID) && PFMRuntimeResources.modelCacheMap.get(COUNTER_MODEL_ID).getCachedModelParts().containsKey(rotationContainer))
            return getBakedModel(COUNTER_MODEL_ID, rotationContainer, PFMRuntimeResources.modelCacheMap.get(COUNTER_MODEL_ID).getCachedModelParts().get(rotationContainer));

        if (!PFMRuntimeResources.modelCacheMap.containsKey(COUNTER_MODEL_ID))
            PFMRuntimeResources.modelCacheMap.put(COUNTER_MODEL_ID, new PFMBakedModelContainer());

        List<BakedModel> bakedModelList = new ArrayList<>();
        for (Identifier modelPart : COUNTER_MODEL_PARTS_BASE) {
            bakedModelList.add(loader.bake(modelPart, rotationContainer));
        }

        PFMRuntimeResources.modelCacheMap.get(COUNTER_MODEL_ID).getCachedModelParts().put(rotationContainer, bakedModelList);
        return getBakedModel(COUNTER_MODEL_ID, rotationContainer, bakedModelList);
    }

    @ExpectPlatform
    public static BakedModel getBakedModel(Identifier modelId, ModelBakeSettings settings, List<BakedModel> modelParts) {
        throw new RuntimeException("Method wasn't replaced correctly");
    }

    @Override
    public void resolve(Resolver resolver) {
        for (Identifier c : COUNTER_MODEL_PARTS_BASE)
            resolver.resolve(c);
    }
}