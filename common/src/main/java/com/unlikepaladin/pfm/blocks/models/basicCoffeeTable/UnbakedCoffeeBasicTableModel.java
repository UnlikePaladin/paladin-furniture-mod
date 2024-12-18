package com.unlikepaladin.pfm.blocks.models.basicCoffeeTable;

import com.mojang.datafixers.util.Pair;
import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.data.materials.StoneVariant;
import com.unlikepaladin.pfm.data.materials.StoneVariantRegistry;
import com.unlikepaladin.pfm.data.materials.WoodVariant;
import com.unlikepaladin.pfm.data.materials.WoodVariantRegistry;
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
public class UnbakedCoffeeBasicTableModel implements UnbakedModel {
    public static final Identifier[] BASIC_MODEL_PARTS_BASE = {
            Identifier.of(PaladinFurnitureMod.MOD_ID, "block/coffee_table_basic/coffee_table_basic_base"),
            Identifier.of(PaladinFurnitureMod.MOD_ID, "block/coffee_table_basic/coffee_table_basic_north_east"),
            Identifier.of(PaladinFurnitureMod.MOD_ID, "block/coffee_table_basic/coffee_table_basic_north_west"),
            Identifier.of(PaladinFurnitureMod.MOD_ID, "block/coffee_table_basic/coffee_table_basic_south_east"),
            Identifier.of(PaladinFurnitureMod.MOD_ID, "block/coffee_table_basic/coffee_table_basic_south_west"),
            Identifier.of(PaladinFurnitureMod.MOD_ID, "block/coffee_table_basic/coffee_table_basic_north_south_east_top"),
            Identifier.of(PaladinFurnitureMod.MOD_ID, "block/coffee_table_basic/coffee_table_basic_north_south_west_top"),
            Identifier.of(PaladinFurnitureMod.MOD_ID, "block/coffee_table_basic/coffee_table_basic_east_west_north"),
            Identifier.of(PaladinFurnitureMod.MOD_ID, "block/coffee_table_basic/coffee_table_basic_east_west_south"),
            Identifier.of(PaladinFurnitureMod.MOD_ID, "block/coffee_table_basic/coffee_table_basic_north_south_east_bottom"),
            Identifier.of(PaladinFurnitureMod.MOD_ID, "block/coffee_table_basic/coffee_table_basic_north_south_west_bottom"),
            Identifier.of(PaladinFurnitureMod.MOD_ID, "block/coffee_table_basic/coffee_table_basic_north_south_east"),
            Identifier.of(PaladinFurnitureMod.MOD_ID, "block/coffee_table_basic/coffee_table_basic_north_south_west"),
            Identifier.of(PaladinFurnitureMod.MOD_ID, "block/coffee_table_basic/coffee_table_basic_north_east_corner"),
            Identifier.of(PaladinFurnitureMod.MOD_ID, "block/coffee_table_basic/coffee_table_basic_north_west_corner"),
            Identifier.of(PaladinFurnitureMod.MOD_ID, "block/coffee_table_basic/coffee_table_basic_south_east_corner"),
            Identifier.of(PaladinFurnitureMod.MOD_ID, "block/coffee_table_basic/coffee_table_basic_south_west_corner")
    };
    private static final Identifier PARENT = Identifier.of("block/block");
    public static final Identifier TABLE_MODEL_ID = Identifier.of(PaladinFurnitureMod.MOD_ID, "block/coffee_table_basic");
    public static final List<Identifier> MODEL_IDS = new ArrayList<>() {
        {
            for(WoodVariant variant : WoodVariantRegistry.getVariants()){
                add(Identifier.of(PaladinFurnitureMod.MOD_ID, "item/" + variant.asString() + "_coffee_table_basic"));
                if (variant.hasStripped())
                    add(Identifier.of(PaladinFurnitureMod.MOD_ID, "item/stripped_" + variant.asString() + "_coffee_table_basic"));
            }
            for(StoneVariant variant : StoneVariantRegistry.getVariants()){
                add(Identifier.of(PaladinFurnitureMod.MOD_ID, "item/" + variant.asString() + "_coffee_table_basic"));
            }
            add(TABLE_MODEL_ID);
        }
    };

    @Nullable
    @Override
    public BakedModel bake(Baker loader, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings rotationContainer) {
        if (PFMRuntimeResources.modelCacheMap.containsKey(TABLE_MODEL_ID) && PFMRuntimeResources.modelCacheMap.get(TABLE_MODEL_ID).getCachedModelParts().containsKey(rotationContainer))
            return getBakedModel(TABLE_MODEL_ID, rotationContainer, PFMRuntimeResources.modelCacheMap.get(TABLE_MODEL_ID).getCachedModelParts().get(rotationContainer));

        if (!PFMRuntimeResources.modelCacheMap.containsKey(TABLE_MODEL_ID))
            PFMRuntimeResources.modelCacheMap.put(TABLE_MODEL_ID, new PFMBakedModelContainer());

        List<BakedModel> bakedModelList = new ArrayList<>();
        for (Identifier modelPart : BASIC_MODEL_PARTS_BASE) {
            bakedModelList.add(loader.bake(modelPart, rotationContainer));
        }

        PFMRuntimeResources.modelCacheMap.get(TABLE_MODEL_ID).getCachedModelParts().put(rotationContainer, bakedModelList);
        return getBakedModel(TABLE_MODEL_ID, rotationContainer, bakedModelList);
    }

    @ExpectPlatform
    public static BakedModel getBakedModel(Identifier modelId, ModelBakeSettings settings, List<BakedModel> modelParts) {
        throw new RuntimeException("Method wasn't replaced correctly");
    }

    @Override
    public void resolve(Resolver resolver) {
        for (Identifier c : BASIC_MODEL_PARTS_BASE)
            resolver.resolve(c);
    }
}
