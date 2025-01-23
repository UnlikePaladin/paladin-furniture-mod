package com.unlikepaladin.pfm.data.materials;

import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.blocks.models.ModelHelper;
import com.unlikepaladin.pfm.registry.BlockItemRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;

public class WoodVariant extends VariantBase<WoodVariant> {
    private final Block plankBlock;
    private final Block logBlock;
    private final Material vanillaMaterial;
    @Nullable
    private final BoatEntity.Type vanillaWoodType;

    WoodVariant(Identifier identifier, Block plankBlock, Block logBlock) {
        super(identifier);
        this.plankBlock = plankBlock;
        this.logBlock = logBlock;
        this.vanillaMaterial = plankBlock.getDefaultState().getMaterial();
        this.vanillaWoodType = BoatEntity.Type.getType(identifier.getPath()) != BoatEntity.Type.OAK &&  BoatEntity.Type.getType(identifier.getPath()).getBaseBlock() == plankBlock ? BoatEntity.Type.getType(identifier.getPath()) : null;
    }

    WoodVariant(Identifier identifier, Block plankBlock, Block logBlock, BoatEntity.@Nullable Type vanillaWoodType) {
        super(identifier);
        this.plankBlock = plankBlock;
        this.logBlock = logBlock;
        this.vanillaMaterial = plankBlock.getDefaultState().getMaterial();
        this.vanillaWoodType = vanillaWoodType;
    }

    public BoatEntity.@Nullable Type getVanillaWoodType() {
        return vanillaWoodType;
    }

    @Override
    public String asString() {
        String postfix = this.isVanilla() ? "" : "_"+this.getNamespace();
        return this.identifier.getPath()+postfix;
    }

    @Environment(EnvType.CLIENT)
    @Override
    public Identifier getTexture(BlockType type) {
        if (type == BlockType.STRIPPED_LOG) {
            return ModelHelper.getTextureId((Block) this.getChild("stripped_log"));
        } else if (type == BlockType.LOG || type == BlockType.SECONDARY) {
            return ModelHelper.getTextureId(this.logBlock);
        } else if (type == BlockType.LOG_TOP) {
            return ModelHelper.getTextureId(this.logBlock, "_top");
        } else if (type == BlockType.STRIPPED_LOG_TOP) {
            return ModelHelper.getTextureId((Block) this.getChild("stripped_log"), "_top");
        }
        return ModelHelper.getTextureId(plankBlock);
    }

    @Override
    public String getPath() {
        return this.identifier.getPath();
    }

    @Nullable
    protected Block findLogRelatedBlock(String append, String postpend) {
        String post = postpend.isEmpty() ? "" : "_" + postpend;
        Identifier id = this.getIdentifier();
        String logN = Registry.BLOCK.getId(this.logBlock).getPath();
        Identifier[] targets = {
                new Identifier(id.getNamespace(), logN + "_" + append + post),
                new Identifier(id.getNamespace(), logN + "_" + append + post.replace("_", "")),
                new Identifier(id.getNamespace(), append + "_" + logN + post),
                new Identifier(id.getNamespace(), append + "_" + logN + post.replace("_", "")),
                new Identifier(id.getNamespace(), id.getPath() + "_" + append + post),
                new Identifier(id.getNamespace(), id.getPath() + "_" + append + post.replace("_", "")),
                new Identifier(id.getNamespace(), append + "_" + id.getPath() + post),
                new Identifier(id.getNamespace(), append + "_" + id.getPath() + post.replace("_", ""))
        };
        String postNether = "";
        switch (postpend) {
            case "log" -> postNether = "stem";
            case "wood" -> postNether = "hyphae";
        }
        postNether = postpend.isEmpty() ? "" : "_" + postNether;
        Block found = null;
        if (!postNether.isEmpty()) {
            Identifier[] nether_targets = {
                    new Identifier(id.getNamespace(), logN + "_" + append + postNether),
                    new Identifier(id.getNamespace(), logN + "_" + append + postNether.replace("_", "")),
                    new Identifier(id.getNamespace(), append + "_" + logN + postNether),
                    new Identifier(id.getNamespace(), append + "_" + logN + postNether.replace("_", "")),
                    new Identifier(id.getNamespace(), id.getPath() + "_" + append + postNether),
                    new Identifier(id.getNamespace(), id.getPath() + "_" + append + postNether.replace("_", "")),
                    new Identifier(id.getNamespace(), append + "_" + id.getPath() + postNether),
                    new Identifier(id.getNamespace(), append + "_" + id.getPath() + postNether.replace("_", ""))
            };
            for (Identifier r : nether_targets) {
                if (Registry.BLOCK.containsId(r)) {
                    found = Registry.BLOCK.get(r);
                    break;
                }
            }
        }
        for (Identifier r : targets) {
            if (Registry.BLOCK.containsId(r)) {
                found = Registry.BLOCK.get(r);
                break;
            }
        }
        return found;
    }

    @Override
    public Block getBaseBlock() {
        return this.plankBlock;
    }

    @Override
    public Block getSecondaryBlock() {
        return logBlock;
    }

    public Block getLogBlock() {
        return logBlock;
    }

    public String toString() {
            return this.identifier.toString();
        }

    public boolean isNetherWood() {
        return this.identifier.getPath().contains("warped") || this.identifier.getPath().contains("crimson");
    }

    public Material getVanillaMaterial() {
        return vanillaMaterial;
    }

    @Override
    public boolean isVanilla() {
        return identifier.getNamespace().equals("") || identifier.getNamespace().equals("minecraft");
    }

    @Override
    public WoodVariant getVariantType() {
        return WoodVariant.this;
    }

    @Override
    public void initializeChildrenBlocks() {
        this.addChild("planks", this.plankBlock);
        this.addChild("log", this.logBlock);
        this.addChild("leaves", this.findRelatedEntry("leaves", Registry.BLOCK));
        this.addChild("stripped_log", this.findLogRelatedBlock("stripped", "log"));
        this.addChild("stripped_wood", this.findLogRelatedBlock("stripped", "wood"));
        this.addChild("wood", this.findRelatedEntry("wood", Registry.BLOCK));
        this.addChild("slab", this.findRelatedEntry("slab", Registry.BLOCK));
        this.addChild("stairs", this.findRelatedEntry("stairs", Registry.BLOCK));
        this.addChild("fence", this.findRelatedEntry("fence", Registry.BLOCK));
        this.addChild("fence_gate", this.findRelatedEntry("fence_gate", Registry.BLOCK));
        this.addChild("door", this.findRelatedEntry("door", Registry.BLOCK));
        this.addChild("trapdoor", this.findRelatedEntry("trapdoor", Registry.BLOCK));
        this.addChild("button", this.findRelatedEntry("button", Registry.BLOCK));
        this.addChild("pressure_plate", this.findRelatedEntry("pressure_plate", Registry.BLOCK));
    }

    @Override
    public void initializeChildrenItems() {
        this.addChild("boat", this.findRelatedEntry("boat", Registry.ITEM));
        this.addChild("sign", this.findRelatedEntry("sign", Registry.ITEM));
    }

    public boolean hasStripped() {
        Object child = this.getChild("stripped_log");
        return child != null && child != this.getBaseBlock();
    }

    public @Nullable ItemConvertible getItemForRecipe(String key, Class<? extends Block> blockClass, boolean stripped) {
        if (stripped) {
            if (key.equals("base")) {
                return (ItemConvertible) getChild("stripped_log");
            } else if (key.equals("secondary"))
                return getBaseBlock();
        }
        return super.getItemForRecipe(key, blockClass);
    }

    @Override
    public Block mainChild() {
        return this.plankBlock;
    }

    public static class Finder implements VariantBase.SetFinder<WoodVariant> {

        private final Map<String, Identifier> childNames = new HashMap<>();
        private final Supplier<Block> planksFinder;
        private final Supplier<Block> logFinder;
        private final Identifier id;

        public Finder(Identifier id, Supplier<Block> planks, Supplier<Block> log) {
            this.id = id;
            this.planksFinder = planks;
            this.logFinder = log;
        }

        public static Finder simple(String modId, String woodTypeName, String planksName, String logName) {
            return simple(new Identifier(modId, woodTypeName), new Identifier(modId, planksName), new Identifier(modId, logName));
        }

        public static Finder simple(Identifier woodTypeName, Identifier planksName, Identifier logName) {
            return new Finder(woodTypeName,
                    () -> Registry.BLOCK.get(planksName),
                    () -> Registry.BLOCK.get(logName));
        }

        public void addChild(String childType, String childName) {
            addChild(childType, new Identifier(id.getNamespace(), childName));
        }

        public void addChild(String childType, Identifier childName) {
            this.childNames.put(childType, childName);
        }

        public Optional<WoodVariant> get() {
            if (BlockItemRegistry.isModLoaded(id.getNamespace())) {
                try {
                    Block plank = planksFinder.get();
                    Block log = logFinder.get();
                    Block d = Registry.BLOCK.get(new Identifier("minecraft","air"));
                    if (plank != d && log != d && plank != null && log != null) {
                        WoodVariant w = new WoodVariant(id, plank, log);
                        for (Map.Entry<String, Identifier> entry : childNames.entrySet()){
                            Object child = Registry.BLOCK.getOrEmpty(entry.getValue()).isPresent() ? Registry.BLOCK.get(entry.getValue()) : Registry.ITEM.get(entry.getValue());
                            w.addChild(entry.getKey(), child);
                        }
                        return Optional.of(w);
                    }
                } catch (Exception ignored) {
                }
                PaladinFurnitureMod.GENERAL_LOGGER.warn("Failed to find custom wood type {}", id);
            }
            return Optional.empty();
        }
    }
}