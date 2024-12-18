package com.unlikepaladin.pfm.blocks.models.fridge;


import com.mojang.datafixers.util.Pair;
import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.blocks.models.ModelHelper;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.*;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

@Environment(EnvType.CLIENT)
public class UnbakedFridgeModel implements UnbakedModel {
    public static final List<String> FRIDGE_MODEL_PARTS_BASE = new ArrayList<>() {
        {
            add("block/white_fridge/fridge_single");
            add("block/white_fridge/fridge_top");
            add("block/white_fridge/fridge_middle");
            add("block/white_fridge/fridge_bottom");
            add("block/white_fridge/fridge");
            add("block/white_fridge/fridge_middle_freezer");
            add("block/white_fridge/fridge_single_open");
            add("block/white_fridge/fridge_top_open");
            add("block/white_fridge/fridge_middle_open");
            add("block/white_fridge/fridge_bottom_open");
            add("block/white_fridge/fridge_open");
            add("block/white_fridge/fridge_middle_freezer_open");
        }
    };

    public static final List<Identifier> ALL_MODEL_IDS = new ArrayList<>() {
        {
            for (String part : FRIDGE_MODEL_PARTS_BASE) {
                add(Identifier.of(PaladinFurnitureMod.MOD_ID, part));
            }
            for (String part : FRIDGE_MODEL_PARTS_BASE) {
                add(Identifier.of(PaladinFurnitureMod.MOD_ID, part.replaceAll("white", "gray")));
            }
        }
    };

    private static final Identifier PARENT = Identifier.of("block/block");
    private final SpriteIdentifier frameTex;

    public Collection<SpriteIdentifier> getTextureDependencies(Function<Identifier, UnbakedModel> unbakedModelGetter, Set<Pair<String, String>> unresolvedTextureReferences) {
        return List.of(frameTex);
    }

    public static final List<Identifier> FRIDGE_MODEL_IDS = new ArrayList<>() { {
        add(Identifier.of(PaladinFurnitureMod.MOD_ID, "block/white_fridge"));
        add(Identifier.of(PaladinFurnitureMod.MOD_ID, "block/gray_fridge"));
    }};
    private final Identifier id;
    public UnbakedFridgeModel(Identifier id) {
        this.id = id;
        this.frameTex = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, ModelHelper.getVanillaConcreteColor(this.id));
    }
    @Nullable
    @Override
    public BakedModel bake(Baker loader, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings rotationContainer) {
        Map<String,BakedModel> bakedModels = new LinkedHashMap<>();
        for (String modelPart : FRIDGE_MODEL_PARTS_BASE) {
            if (this.id.getPath().contains("gray"))
                modelPart = modelPart.replaceAll("white", "gray");
            bakedModels.put(modelPart, loader.bake(Identifier.of(PaladinFurnitureMod.MOD_ID, modelPart), rotationContainer));
        }
        return getBakedModel(textureGetter.apply(frameTex), rotationContainer, bakedModels, bakedModels.keySet().stream().toList());
    }

    @ExpectPlatform
    public static BakedModel getBakedModel(Sprite frame, ModelBakeSettings settings, Map<String,BakedModel> bakedModels, List<String> MODEL_PARTS) {
        throw new RuntimeException("Method wasn't replaced correctly");
    }

    @Override
    public void resolve(Resolver resolver) {
        for (Identifier c : ALL_MODEL_IDS)
            resolver.resolve(c);
    }
}
