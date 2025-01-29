package com.unlikepaladin.pfm.data.materials;

import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.blocks.models.ModelHelper;
import com.unlikepaladin.pfm.mixin.PFMFeatureFlagFactory;
import com.unlikepaladin.pfm.registry.BlockItemRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSetType;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.registry.Registries;
import net.minecraft.resource.featuretoggle.FeatureFlag;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.resource.featuretoggle.FeatureUniverse;
import net.minecraft.item.ItemConvertible;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registry;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;

public class WoodVariant extends VariantBase<WoodVariant> {
    private final Block plankBlock;
    private final Block logBlock;

    WoodVariant(Identifier identifier, Block plankBlock, Block logBlock) {
        super(identifier);
        this.plankBlock = plankBlock;
        this.logBlock = logBlock;
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
        String logN = Registries.BLOCK.getId(this.logBlock).getPath();
        Identifier[] targets = {
                Identifier.of(id.getNamespace(), logN + "_" + append + post),
                Identifier.of(id.getNamespace(), logN + "_" + append + post.replace("_", "")),
                Identifier.of(id.getNamespace(), append + "_" + logN + post),
                Identifier.of(id.getNamespace(), append + "_" + logN + post.replace("_", "")),
                Identifier.of(id.getNamespace(), id.getPath() + "_" + append + post),
                Identifier.of(id.getNamespace(), id.getPath() + "_" + append + post.replace("_", "")),
                Identifier.of(id.getNamespace(), append + "_" + id.getPath() + post),
                Identifier.of(id.getNamespace(), append + "_" + id.getPath() + post.replace("_", ""))
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
                    Identifier.of(id.getNamespace(), logN + "_" + append + postNether),
                    Identifier.of(id.getNamespace(), logN + "_" + append + postNether.replace("_", "")),
                    Identifier.of(id.getNamespace(), append + "_" + logN + postNether),
                    Identifier.of(id.getNamespace(), append + "_" + logN + postNether.replace("_", "")),
                    Identifier.of(id.getNamespace(), id.getPath() + "_" + append + postNether),
                    Identifier.of(id.getNamespace(), id.getPath() + "_" + append + postNether.replace("_", "")),
                    Identifier.of(id.getNamespace(), append + "_" + id.getPath() + postNether),
                    Identifier.of(id.getNamespace(), append + "_" + id.getPath() + postNether.replace("_", ""))
            };
            for (Identifier r : nether_targets) {
                if (Registries.BLOCK.containsId(r)) {
                    found = Registries.BLOCK.get(r);
                    break;
                }
            }
        }
        for (Identifier r : targets) {
            if (Registries.BLOCK.containsId(r)) {
                found = Registries.BLOCK.get(r);
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

    @Override
    public boolean isVanilla() {
        return identifier.getNamespace().equals("") || identifier.getNamespace().equals("minecraft");
    }

    @Override
    public WoodVariant getVariantType() {
        return WoodVariant.this;
    }

    @Override
    public List<FeatureFlag> getFeatureList() {
        FeatureFlag flag = PFMFeatureFlagFactory.newFlag(getBaseBlock().getRequiredFeatures().universe, 0);
        flag.mask = getBaseBlock().getRequiredFeatures().featuresMask;
        return List.of(flag);
    }

    @Override
    public void initializeChildrenBlocks() {
        this.addChild("planks", this.plankBlock);
        this.addChild("log", this.logBlock);
        this.addChild("leaves", this.findRelatedEntry("leaves", Registries.BLOCK));
        this.addChild("stripped_log", this.findLogRelatedBlock("stripped", "log"));
        this.addChild("stripped_wood", this.findLogRelatedBlock("stripped", "wood"));
        this.addChild("wood", this.findRelatedEntry("wood", Registries.BLOCK));
        this.addChild("slab", this.findRelatedEntry("slab", Registries.BLOCK));
        this.addChild("stairs", this.findRelatedEntry("stairs", Registries.BLOCK));
        this.addChild("fence", this.findRelatedEntry("fence", Registries.BLOCK));
        this.addChild("fence_gate", this.findRelatedEntry("fence_gate", Registries.BLOCK));
        this.addChild("door", this.findRelatedEntry("door", Registries.BLOCK));
        this.addChild("trapdoor", this.findRelatedEntry("trapdoor", Registries.BLOCK));
        this.addChild("button", this.findRelatedEntry("button", Registries.BLOCK));
        this.addChild("pressure_plate", this.findRelatedEntry("pressure_plate", Registries.BLOCK));
    }

    @Override
    public void initializeChildrenItems() {
        this.addChild("boat", this.findRelatedEntry("boat", Registries.ITEM));
        this.addChild("sign", this.findRelatedEntry("sign", Registries.ITEM));
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
            return simple(Identifier.of(modId, woodTypeName), Identifier.of(modId, planksName), Identifier.of(modId, logName));
        }

        public static Finder simple(Identifier woodTypeName, Identifier planksName, Identifier logName) {
            return new Finder(woodTypeName,
                    () -> Registries.BLOCK.get(planksName),
                    () -> Registries.BLOCK.get(logName));
        }

        public void addChild(String childType, String childName) {
            addChild(childType, Identifier.of(id.getNamespace(), childName));
        }

        public void addChild(String childType, Identifier childName) {
            this.childNames.put(childType, childName);
        }

        public Optional<WoodVariant> get() {
            if (BlockItemRegistry.isModLoaded(id.getNamespace())) {
                try {
                    Block plank = planksFinder.get();
                    Block log = logFinder.get();
                    Block d = Registries.BLOCK.get(Identifier.of("minecraft","air"));
                    if (plank != d && log != d && plank != null && log != null) {
                        WoodVariant w = new WoodVariant(id, plank, log);
                        for (Map.Entry<String, Identifier> entry : childNames.entrySet()){
                            Object child = Registries.BLOCK.getOptionalValue(entry.getValue()).isPresent() ? Registries.BLOCK.get(entry.getValue()) : Registries.ITEM.get(entry.getValue());
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