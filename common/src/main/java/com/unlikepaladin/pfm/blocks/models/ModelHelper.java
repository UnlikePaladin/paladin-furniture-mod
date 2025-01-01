package com.unlikepaladin.pfm.blocks.models;

import com.mojang.datafixers.util.Pair;
import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.blocks.DyeableFurnitureBlock;
import com.unlikepaladin.pfm.data.materials.*;
import com.unlikepaladin.pfm.runtime.PFMDataGenerator;
import com.unlikepaladin.pfm.runtime.PFMRuntimeResources;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.data.client.TextureMap;
import net.minecraft.registry.Registries;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.Direction;
import net.minecraft.registry.Registry;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.util.math.random.Random;

public class ModelHelper {
    public static List<Sprite> OAK_SPRITES_PLANKS_TO_REPLACE = null;
    public static List<Sprite> getOakPlankLogSprites() {
        if (OAK_SPRITES_PLANKS_TO_REPLACE == null) {
            SpriteIdentifier planksId = new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, Identifier.of("minecraft:block/oak_planks"));
            SpriteIdentifier logId = new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, Identifier.of("minecraft:block/oak_log"));
            OAK_SPRITES_PLANKS_TO_REPLACE = Arrays.asList(planksId.getSprite(), logId.getSprite());
        }
        return OAK_SPRITES_PLANKS_TO_REPLACE;
    }
    public static List<Sprite> OAK_SPRITES_BED_TO_REPLACE = null;
    public static List<Sprite> getOakBedSprites() {
        if (OAK_SPRITES_BED_TO_REPLACE == null) {
            SpriteIdentifier planksId = new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, Identifier.of("minecraft:block/oak_planks"));
            SpriteIdentifier bedId = TexturedRenderLayers.BED_TEXTURES[DyeColor.RED.getId()];
            OAK_SPRITES_BED_TO_REPLACE = Arrays.asList(planksId.getSprite(), bedId.getSprite());
        }
        return OAK_SPRITES_BED_TO_REPLACE;
    }
    public static List<Sprite> OAK_SPRITES_LOG_TOP_TO_REPLACE = null;
    public static List<Sprite> getOakLogLogTopSprites() {
        if (OAK_SPRITES_LOG_TOP_TO_REPLACE == null) {
            SpriteIdentifier logId = new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, Identifier.of("minecraft:block/oak_log"));
            SpriteIdentifier logTopId = new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, Identifier.of("minecraft:block/oak_log_top"));
            OAK_SPRITES_LOG_TOP_TO_REPLACE = Arrays.asList(logId.getSprite(), logTopId.getSprite());
        }
        return OAK_SPRITES_LOG_TOP_TO_REPLACE;
    }
    public static boolean containsIdentifier(Identifier[] modelIds, Identifier comparison) {
        AtomicBoolean contains = new AtomicBoolean(false);
        Arrays.stream(modelIds).forEach(identifier -> {
            if (comparison.getPath().equals(identifier.getPath())){
                contains.set(true);
            }
        });
        return contains.get();
    }

    public static BlockType getBlockType(Identifier identifier) {
        if (identifier.getPath().contains("stripped_")) {
            return BlockType.STRIPPED_LOG;
        }
        for (WoodVariant variant : WoodVariantRegistry.getVariants()) {
            if (identifier.getPath().contains(variant.getPath())) {
                return BlockType.PLANKS;
            }
        }
        return BlockType.BLOCK;
    }

    public static VariantBase<?> getVariant(Identifier identifier) {
        VariantBase<?> var = getExtraCounterType(identifier);
        if (var == null) {
            var = getStoneType(identifier);
        }
        if (var == null) {
            var = getWoodType(identifier);
        }
        return var;
    }

    @Nullable
    public static ExtraCounterVariant getExtraCounterType(Identifier identifier) {
        for (ExtraCounterVariant variant:
                ExtraCounterVariant.values()) {
            if (identifier.getPath().contains(variant.getPath()) && getBlockType(identifier) == BlockType.BLOCK) {
                return variant;
            }
        }
        return null;
    }

    @Nullable
    public static StoneVariant getStoneType(Identifier identifier) {
        for (StoneVariant variant : StoneVariantRegistry.getVariants()) {
            if (identifier.getPath().contains(variant.getPath()) && getBlockType(identifier) == BlockType.BLOCK) {
                return variant;
            }
        }
        return null;
    }
    public static WoodVariant getWoodType(Identifier identifier){
        WoodVariant selectedVariant = null;
        for (WoodVariant woodVariant : WoodVariantRegistry.getVariants())
            if (identifier.getPath().contains(woodVariant.identifier.getPath())) {
                if (identifier.getPath().contains("dark") && !woodVariant.identifier.getPath().contains("dark") || (!identifier.getPath().contains(woodVariant.getNamespace()) && !woodVariant.isVanilla()))
                    continue;
                selectedVariant = woodVariant;
        }
        return selectedVariant != null ? selectedVariant : WoodVariantRegistry.OAK;
    }

    public static DyeColor getColor(Identifier identifier) {
        if (Registries.BLOCK.get(identifier) instanceof DyeableFurnitureBlock block) {
            return block.getPFMColor();
        }
        for (DyeColor color : DyeColor.values()) {
            if (identifier.getPath().contains(color.getName())){
                if (!identifier.getPath().contains("light") && color.getName().contains("light"))  {
                    continue;
                } else if (identifier.getPath().contains("light") && !color.getName().contains("light"))  {
                    continue;
                }
                return color;
            }
        }
        return DyeColor.RED;
    }

    public static Identifier getVanillaConcreteColor(Identifier identifier) {
        DyeColor color = getColor(identifier);
        if (!identifier.getPath().contains(color.getName()))
            return Identifier.of("minecraft", "block/white_concrete");
        return Identifier.of("minecraft", "block/"+ color.getName() + "_concrete");
    }

    public static Block getWoolColor(String string) {
        Block block = Registries.BLOCK.get(Identifier.of("minecraft", string+"_wool"));
        if (block != Blocks.AIR) {
            return block;
        }
        return Blocks.WHITE_WOOL;
    }

    public static Identifier getTextureId(Block block) {
        return getTextureId(block, "");
    }
    public static final Map<Pair<String, String>, Pair<Identifier, Integer>> blockToTextureMap = new HashMap<>();
    public static Identifier getTextureId(Block block, String postfix) {
        if (postfix.isEmpty())
            postfix = null;
        Pair<String, String> pair = new Pair<>(block.toString(), postfix);
        if (blockToTextureMap.containsKey(pair) && (blockToTextureMap.get(pair).getFirst() != MissingSprite.getMissingSpriteId() || blockToTextureMap.get(pair).getSecond() > 3)) {
            return blockToTextureMap.get(pair).getFirst();
        }
        int attemptNum = 1;
        if (blockToTextureMap.containsKey(pair)) {
            attemptNum += blockToTextureMap.get(pair).getSecond();
        }
        if (postfix == null)
            postfix = "";

        Identifier id;
        if (postfix.isEmpty() && !PFMDataGenerator.areAssetsRunning()) {
            BakedModel model = MinecraftClient.getInstance().getBakedModelManager().getBlockModels().getModel(block.getDefaultState());
            if (model != null) {
                List<BakedQuad> quadList = model.getQuads(block.getDefaultState(), Direction.NORTH, Random.create(42L));
                if (!quadList.isEmpty()) {
                    id = quadList.get(0).getSprite().getContents().getId();
                    if (id != null && id != MissingSprite.getMissingSpriteId()) {
                        blockToTextureMap.put(pair, new Pair<>(id, attemptNum));
                        return id;
                    }
                }
            }
        } else if (postfix.equals("_top") && !PFMDataGenerator.areAssetsRunning()) {
            BakedModel model = MinecraftClient.getInstance().getBakedModelManager().getBlockModels().getModel(block.getDefaultState());
            if (model != null) {
                List<BakedQuad> quadList = model.getQuads(block.getDefaultState(), Direction.UP, Random.create(42L));
                if (!quadList.isEmpty()) {
                    id = quadList.get(0).getSprite().getContents().getId();
                    if (id != null && id != MissingSprite.getMissingSpriteId()) {
                        blockToTextureMap.put(pair, new Pair<>(id, attemptNum));
                        return id;
                    }
                }
                quadList = model.getQuads(block.getDefaultState(), Direction.DOWN, Random.create(42L));
                if (!quadList.isEmpty()) {
                    id = quadList.get(0).getSprite().getContents().getId();
                    if (id != null && id != MissingSprite.getMissingSpriteId()) {
                        blockToTextureMap.put(pair, new Pair<>(id, attemptNum));
                        return id;
                    }
                }
            }
        }

        if (idExists(TextureMap.getSubId(block, postfix), ResourceType.CLIENT_RESOURCES, IdLocation.TEXTURES)){
            id = TextureMap.getSubId(block, postfix);
        }
        else if(idExists(getLogId(block, postfix), ResourceType.CLIENT_RESOURCES, IdLocation.TEXTURES)) {
            id = getLogId(block, postfix);
        }
        else if (idExists(TextureMap.getId(block), ResourceType.CLIENT_RESOURCES, IdLocation.TEXTURES)) {
            id = TextureMap.getId(block);
        }
        else if (idExists(TextureMap.getSubId(block, "_side"), ResourceType.CLIENT_RESOURCES, IdLocation.TEXTURES)) {
            id = TextureMap.getSubId(block, "_side");
        }
        else if (idExists(TextureMap.getSubId(block, "_side_1"), ResourceType.CLIENT_RESOURCES, IdLocation.TEXTURES)) {
            id = TextureMap.getSubId(block, "_side_1");
        }
        else if (idExists(TextureMap.getSubId(block, "_bottom"), ResourceType.CLIENT_RESOURCES, IdLocation.TEXTURES)){
            id = TextureMap.getSubId(block, "_bottom");
        }
        else if (idExists(TextureMap.getSubId(block, "_top"), ResourceType.CLIENT_RESOURCES, IdLocation.TEXTURES)){
            id = TextureMap.getSubId(block, "_top");
        }
        else if (idExists(TextureMap.getSubId(block, "_middle"), ResourceType.CLIENT_RESOURCES, IdLocation.TEXTURES)){
            id = TextureMap.getSubId(block, "_middle");
        }
        else if(idExists(getPlankId(block), ResourceType.CLIENT_RESOURCES, IdLocation.TEXTURES)) {
            id = getPlankId(block);
        }
        else if(idExists(getLogId(block, "_side"), ResourceType.CLIENT_RESOURCES, IdLocation.TEXTURES)) {
            id = getLogId(block, "_side");
        }
        else if(idExists(getLogId(block, "_side_1"), ResourceType.CLIENT_RESOURCES, IdLocation.TEXTURES)) {
            id = getLogId(block, "_side_1");
        }
        else if(idExists(getLogId(block, "_top"), ResourceType.CLIENT_RESOURCES, IdLocation.TEXTURES)) {
            id = getLogId(block, "_top");
        }
        else if(idExists(getLogId(block, "_middle"), ResourceType.CLIENT_RESOURCES, IdLocation.TEXTURES)) {
            id = getLogId(block, "_middle");
        }
        else if(idExists(getLogId(block, "_bottom"), ResourceType.CLIENT_RESOURCES, IdLocation.TEXTURES)) {
            id = getLogId(block, "_bottom");
        }
        else if (Registries.BLOCK.getId(block).getNamespace().equals("quark")) {
            id = TextureMap.getSubId(block, postfix);
        } else {
            PaladinFurnitureMod.GENERAL_LOGGER.warn("Couldn't find texture for, {}, this is attempt {} at finding it", block, attemptNum);
            id = MissingSprite.getMissingSpriteId();
        }
        blockToTextureMap.put(pair, new Pair<>(id, attemptNum));
        return id;
    }

    // For compatibility with Twilight Forest's Planks
    public static Identifier getPlankId(Block block) {
        Identifier identifier = Registries.BLOCK.getId(block);
        String namespace = identifier.getNamespace();
        String path = identifier.getPath().replace("luphie_", "");
        if (path.contains("planks")) {
            path = path.replace("_planks", "").replace("plank_", "");
            Identifier id = Identifier.of(namespace, "block/" + path +"/planks");
            if (idExists(id, ResourceType.CLIENT_RESOURCES, IdLocation.TEXTURES))
                return id;

            path = "planks_" + path;
            if (namespace.contains("pixelmon") && path.contains("ultra")) {
                path = path.replace("ultra_", "").replace("_ultra", "");
                path = "ultra_space/" + path;
            }
            if (namespace.equals("blue_skies")) {
                path = "wood/" + path;
            }
            id = Identifier.of(namespace, "block/" + path);
            path = path.replace("mining", "mine").replace("sorting", "sort").replace("transformation", "trans").replace("dark", "darkwood").replace("alpha_", "alpha_oak_").replace("flowering_pink", "flowerypink").replace("flowering_purple", "floweringpurple");
            Identifier id2 = Identifier.of(namespace, "block/wood/" + path);
            Identifier id3 = Identifier.of(namespace, "block/" + path.replace("planks_", "") + "planks");
            Identifier id4 = Identifier.of(namespace, "block/" + path.replace("planks_", "") + "_planks");
            Identifier id5 = Identifier.of(namespace, "block/" + path.replace("planks_", "") + "plankstext");
            Identifier id6 = Identifier.of(namespace, "block/" + path.replace("planks_", "") + "plankretext");
            Identifier id7 = Identifier.of(namespace, "block/" + path.replace("planks_", "") + "_planks0");
            Identifier id8 = Identifier.of(namespace, "block/" + path.replace("planks_", "") + "_planks1");

            if (idExists(id, ResourceType.CLIENT_RESOURCES, IdLocation.TEXTURES))
                return id;
            else if (idExists(id2, ResourceType.CLIENT_RESOURCES, IdLocation.TEXTURES))
                return id2;
            else if (idExists(id3, ResourceType.CLIENT_RESOURCES, IdLocation.TEXTURES))
                return id3;
            else if (idExists(id4, ResourceType.CLIENT_RESOURCES, IdLocation.TEXTURES))
                return id4;
            else if (idExists(id5, ResourceType.CLIENT_RESOURCES, IdLocation.TEXTURES))
                return id5;
            else if (idExists(id6, ResourceType.CLIENT_RESOURCES, IdLocation.TEXTURES))
                return id6;
            else if (idExists(id7, ResourceType.CLIENT_RESOURCES, IdLocation.TEXTURES))
                return id7;
            else if (idExists(id8, ResourceType.CLIENT_RESOURCES, IdLocation.TEXTURES))
                return id8;
            else
                return Identifier.of(namespace, "block/wood/" + path+ "_0");
        }
        else
            return Identifier.of(namespace, "block/" + path);
    }

    public static Identifier getLogId(Block block, String postFix) {
        Identifier identifier = Registries.BLOCK.getId(block);
        String namespace = identifier.getNamespace();
        String path = identifier.getPath().replace("luphie_", "");
        if (namespace.contains("luphieclutteredmod") && path.contains("flowering_log")) {
            path = path.replace("flowering_log", "flowering_yellow_log");
        }
        if (namespace.contains("pixelmon") && path.contains("ultra")) {
            path = path.replace("ultra_", "").replace("_ultra", "");
            path = "ultra_space/" + path;
        }
        if (namespace.equals("blue_skies")) {
            path = "wood/" + path;
        }
        if (namespace.equals("byg") && path.contains("pedu"))
            path = path.replace("pedu", "log");
        if (path.contains("log") || path.contains("stem")) {
            if (!path.contains("_log")) {
                path = path.replace("log", "_log");
            }
            Identifier id = Identifier.of(namespace, "block/" + path);
            if (idExists(id, ResourceType.CLIENT_RESOURCES, IdLocation.TEXTURES)) {
                return id;
            }
            path = path.replace("stem", "log").replace("log", "bark");
            path += postFix;
            if (idExists(id, ResourceType.CLIENT_RESOURCES, IdLocation.TEXTURES)) {
                return id;
            }

            path = path.replace("stripped", "striped");
            id = Identifier.of(namespace, "block/" + path);
            if (idExists(id, ResourceType.CLIENT_RESOURCES, IdLocation.TEXTURES)) {
                return id;
            }
            path = path.replace("striped", "stripped");
            path = path.replace("bark", "log");
            id = Identifier.of(namespace, "block/" + path);
            if (idExists(id, ResourceType.CLIENT_RESOURCES, IdLocation.TEXTURES)) {
                return id;
            }
            path = path.replace("stripped", "striped");
            id = Identifier.of(namespace, "block/" + path);
            if (idExists(id, ResourceType.CLIENT_RESOURCES, IdLocation.TEXTURES)) {
                return id;
            }

            path = path.contains("striped") ? "stripped_"+path.replace("_striped", "") : path;
            id = Identifier.of(namespace, "block/" + path);
            if (idExists(id, ResourceType.CLIENT_RESOURCES, IdLocation.TEXTURES)) {
                return id;
            }
            path = path.replace("stripped", "striped");
            id = Identifier.of(namespace, "block/" + path);
            if (idExists(id, ResourceType.CLIENT_RESOURCES, IdLocation.TEXTURES)) {
                return id;
            }
            String loc = identifier.getPath().contains("stripped") || identifier.getPath().contains("striped") ? "stripped_log" : "log";
            path = path.replace("striped_", "").replace(postFix, "").replace("_log", "");

            id = Identifier.of(namespace, "block/" + path+ "/" + loc + postFix);
            if (idExists(id, ResourceType.CLIENT_RESOURCES, IdLocation.TEXTURES)) {
                return id;
            }
            id = Identifier.of(namespace, "block/" + path+ "/" + loc.replace("log", "stem") + postFix);
            if (idExists(id, ResourceType.CLIENT_RESOURCES, IdLocation.TEXTURES)) {
                return id;
            }
            id = Identifier.of(namespace, "block/" + path+ "/" + loc + "/" + postFix.replace("_", ""));
            if (idExists(id, ResourceType.CLIENT_RESOURCES, IdLocation.TEXTURES)) {
                return id;
            }
            id = Identifier.of(namespace, "block/" + path+ "/" + loc.replace("log", "stem") + "/" + postFix.replace("_", ""));
            if (idExists(id, ResourceType.CLIENT_RESOURCES, IdLocation.TEXTURES)) {
                return id;
            }
            id = Identifier.of(namespace, "block/" + path+ "/" + loc);
            if (idExists(id, ResourceType.CLIENT_RESOURCES, IdLocation.TEXTURES)) {
                return id;
            }
            id = Identifier.of(namespace, "block/" + path+ "/" + loc.replace("log", "stem"));
            if (idExists(id, ResourceType.CLIENT_RESOURCES, IdLocation.TEXTURES)) {
                return id;
            }
            id = Identifier.of(namespace, "block/stripped_" + path+ "_log");
            if (idExists(id, ResourceType.CLIENT_RESOURCES, IdLocation.TEXTURES)) {
                return id;
            }
            id = Identifier.of(namespace, "block/stripped_" + path+ "_stem");
            if (idExists(id, ResourceType.CLIENT_RESOURCES, IdLocation.TEXTURES)) {
                return id;
            }
            id = Identifier.of(namespace, "block/" + path+ "_log_stripped");
            if (idExists(id, ResourceType.CLIENT_RESOURCES, IdLocation.TEXTURES)) {
                return id;
            }
            id = Identifier.of(namespace, "block/" + path+ "_stem_stripped");
            if (idExists(id, ResourceType.CLIENT_RESOURCES, IdLocation.TEXTURES)) {
                return id;
            }
        } else if (path.contains("reed")) {
            path = path.replace("nether_", "").replace("reed", "reeds");
            Identifier id = Identifier.of(namespace, "block/" + path);
            if (idExists(id, ResourceType.CLIENT_RESOURCES, IdLocation.TEXTURES)){
                return id;
            }
            path += postFix;
            id = Identifier.of(namespace, "block/" + path);
            if (idExists(id, ResourceType.CLIENT_RESOURCES, IdLocation.TEXTURES)) {
                return id;
            }
            id = Identifier.of(namespace, "block/" + path.replace("planks", "roof"));
            if (idExists(id, ResourceType.CLIENT_RESOURCES, IdLocation.TEXTURES)) {
                return id;
            }
        }
        if (path.contains("alpha_") && namespace.contains("regions")) {
            path = !path.contains("alpha_oak") ? path.replace("alpha", "alpha_oak") : path;
            Identifier id = Identifier.of(namespace, "block/" + path);
            if (idExists(id, ResourceType.CLIENT_RESOURCES, IdLocation.TEXTURES)){
                return id;
            }
            path += postFix;
            id = Identifier.of(namespace, "block/" + path);
            if (idExists(id, ResourceType.CLIENT_RESOURCES, IdLocation.TEXTURES)) {
                return id;
            }
            id = Identifier.of(namespace, "block/alpha_oak_log" + postFix);
            if (idExists(id, ResourceType.CLIENT_RESOURCES, IdLocation.TEXTURES)) {
                return id;
            }
            id = Identifier.of(namespace, "block/alpha_oak_log");
            if (idExists(id, ResourceType.CLIENT_RESOURCES, IdLocation.TEXTURES)) {
                return id;
            }
        }
        return Identifier.of(namespace, "block/" + path);
    }

    private static final HashMap<Identifier, Boolean> idCacheMap = new HashMap<>();
    public static boolean idExists(Identifier id, ResourceType resourceType, IdLocation idLocation) {
        if (idCacheMap.containsKey(id)) {
            return idCacheMap.get(id);
        }
        Identifier id2 = Identifier.of(id.getNamespace(), idLocation.asString() + "/" + id.getPath() + idLocation.getFileType());
        AtomicBoolean exists = new AtomicBoolean(false);
        for (ResourcePack rp : PFMRuntimeResources.RESOURCE_PACK_LIST) {
            if (exists.get())
                break;

            rp.findResources(resourceType, id2.getNamespace(), id2.getPath(), (identifier, supplier) -> {
                try {
                    supplier.get().read();
                    supplier.get().close();
                    exists.set(true);
                } catch (IOException e) {
                    exists.set(false);
                }
            });
        }
        idCacheMap.put(id, exists.get());
        return exists.get();
    }

    public enum IdLocation implements StringIdentifiable {
        TEXTURES("textures", ".png"),
        MODELS("models"),
        BLOCKSTATES("blockstates"),
        RECIPES("recipes"),
        TAGS("tags"),
        LOOT_TABLES("loot_tables"),
        STRUCTURES("structures"),
        ADVANCEMENTS("advancements");
        private final String name;
        private final String fileType;
        IdLocation(String name, String fileType) {
            this.name = name;
            this.fileType = fileType;
        }

        IdLocation(String name) {
            this.name = name;
            this.fileType = ".json";
        }

        @Override
        public String asString() {
            return name;
        }

        public String getFileType() {
            return fileType;
        }
    }
}
