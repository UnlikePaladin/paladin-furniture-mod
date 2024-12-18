package com.unlikepaladin.pfm.blocks.models.kitchenWallDrawer;

import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.data.materials.*;
import com.unlikepaladin.pfm.runtime.PFMBakedModelContainer;
import com.unlikepaladin.pfm.runtime.PFMRuntimeResources;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.*;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Environment(EnvType.CLIENT)
public class UnbakedKitchenWallDrawerModel implements UnbakedModel {
    public static final Identifier[] COUNTER_MODEL_PARTS_BASE = new Identifier[] {
            Identifier.of(PaladinFurnitureMod.MOD_ID, "block/kitchen_drawer/kitchen_drawer_middle"),
            Identifier.of(PaladinFurnitureMod.MOD_ID, "block/kitchen_drawer/kitchen_drawer_middle_inner_corner_left"),
            Identifier.of(PaladinFurnitureMod.MOD_ID, "block/kitchen_drawer/kitchen_drawer_middle_inner_corner_right"),
            Identifier.of(PaladinFurnitureMod.MOD_ID, "block/kitchen_drawer/kitchen_drawer_middle_outer_corner_left"),
            Identifier.of(PaladinFurnitureMod.MOD_ID, "block/kitchen_drawer/kitchen_drawer_middle_outer_corner_right"),
            Identifier.of(PaladinFurnitureMod.MOD_ID, "block/kitchen_drawer/kitchen_drawer_middle_open"),
            Identifier.of(PaladinFurnitureMod.MOD_ID, "block/kitchen_drawer/kitchen_drawer_middle_inner_corner_left"),
            Identifier.of(PaladinFurnitureMod.MOD_ID, "block/kitchen_drawer/kitchen_drawer_middle_inner_corner_right"),
            Identifier.of(PaladinFurnitureMod.MOD_ID, "block/kitchen_drawer/kitchen_drawer_middle_outer_corner_open_left"),
            Identifier.of(PaladinFurnitureMod.MOD_ID, "block/kitchen_drawer/kitchen_drawer_middle_outer_corner_open_right")
    };


    private static final Identifier PARENT = Identifier.of("block/block");
    public static final Identifier DRAWER_MODEL_ID = Identifier.of(PaladinFurnitureMod.MOD_ID, "block/kitchen_wall_drawer");
    public static final List<Identifier> DRAWER_MODEL_IDS = new ArrayList<>() {
        {
            for(WoodVariant variant : WoodVariantRegistry.getVariants()){
                add(Identifier.of(PaladinFurnitureMod.MOD_ID, "item/" + variant.asString() + "_kitchen_wall_drawer"));
                if (variant.hasStripped())
                    add(Identifier.of(PaladinFurnitureMod.MOD_ID, "item/stripped_" + variant.asString() + "_kitchen_wall_drawer"));
            }
            for(StoneVariant variant : StoneVariantRegistry.getVariants()){
                if (variant.identifier.getPath().equals("quartz"))
                    continue;
                add(Identifier.of(PaladinFurnitureMod.MOD_ID, "item/" + variant.asString() + "_kitchen_wall_drawer"));
            }
            for(ExtraCounterVariant variant : ExtraCounterVariant.values()){
                add(Identifier.of(PaladinFurnitureMod.MOD_ID, "item/" + variant.asString() + "_kitchen_wall_drawer"));
            }
            add(DRAWER_MODEL_ID);
        }
    };

    public Collection<SpriteIdentifier> getTextureDependencies(Function<Identifier, UnbakedModel> unbakedModelGetter, Set<com.mojang.datafixers.util.Pair<String, String>> unresolvedTextureReferences) {
        return Collections.emptyList();
    }

    @Nullable
    @Override
    public BakedModel bake(Baker loader, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings rotationContainer) {
        if (PFMRuntimeResources.modelCacheMap.containsKey(DRAWER_MODEL_ID) && PFMRuntimeResources.modelCacheMap.get(DRAWER_MODEL_ID).getCachedModelParts().containsKey(rotationContainer))
            return getBakedModel(DRAWER_MODEL_ID, rotationContainer, PFMRuntimeResources.modelCacheMap.get(DRAWER_MODEL_ID).getCachedModelParts().get(rotationContainer));

        if (!PFMRuntimeResources.modelCacheMap.containsKey(DRAWER_MODEL_ID))
            PFMRuntimeResources.modelCacheMap.put(DRAWER_MODEL_ID, new PFMBakedModelContainer());

        List<BakedModel> bakedModelList = new ArrayList<>();
        for (Identifier modelPart : COUNTER_MODEL_PARTS_BASE) {
            bakedModelList.add(loader.bake(modelPart, rotationContainer));
        }

        PFMRuntimeResources.modelCacheMap.get(DRAWER_MODEL_ID).getCachedModelParts().put(rotationContainer, bakedModelList);
        return getBakedModel(DRAWER_MODEL_ID, rotationContainer, bakedModelList);
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