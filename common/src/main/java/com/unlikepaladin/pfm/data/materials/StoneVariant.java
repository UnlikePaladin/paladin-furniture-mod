package com.unlikepaladin.pfm.data.materials;

import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.blocks.models.ModelHelper;
import com.unlikepaladin.pfm.registry.BlockItemRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.ItemConvertible;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class StoneVariant extends VariantBase<StoneVariant> {
    private final Block polishedBlock;
    private final Block rawBlock;
    private final Material vanillaMaterial;

    StoneVariant(Identifier identifier, Block polishedBlock, Block rawBlock) {
        super(identifier);
        this.polishedBlock = polishedBlock;
        this.rawBlock = rawBlock;
        this.vanillaMaterial = polishedBlock.getDefaultState().getMaterial();
    }

    @Override
    public String asString() {
        String postfix = this.isVanilla() ? "" : "_"+this.getNamespace();
        return this.identifier.getPath()+postfix;
    }

    @Environment(EnvType.CLIENT)
    @Override
    public Identifier getTexture(BlockType type) {
        if (type == BlockType.SECONDARY)
            return ModelHelper.getTextureId(rawBlock);
        return ModelHelper.getTextureId(polishedBlock);
    }

    @Override
    public String getPath() {
        return this.identifier.getPath();
    }

    @Override
    public Block getBaseBlock() {
        return this.polishedBlock;
    }

    @Override
    public Block getSecondaryBlock() {
        return rawBlock;
    }

    public Block getRawBlock() {
        return rawBlock;
    }

    public String toString() {
            return this.identifier.toString();
        }

    public boolean isNetherWood() {
        return this.identifier.getPath().contains("warped") || this.identifier.getPath().contains("crimson");
    }

    public Material getVanillaMaterial() {
        return rawBlock.getDefaultState().getMaterial();
    }

    @Override
    public Material getBaseMaterial() {
        return rawBlock.getDefaultState().getMaterial();
    }

    @Override
    public Material getSecondaryMaterial() {
        return polishedBlock.getDefaultState().getMaterial();
    }

    @Override
    public boolean isVanilla() {
        return identifier.getNamespace().equals("") || identifier.getNamespace().equals("minecraft");
    }

    @Override
    public StoneVariant getVariantType() {
        return StoneVariant.this;
    }

    @Override
    public void initializeChildrenBlocks() {
        this.addChild("slab", this.findRelatedEntry("slab", Registry.BLOCK));
        this.addChild("stairs", this.findRelatedEntry("stairs", Registry.BLOCK));
        this.addChild("wall", this.findRelatedEntry("fence", Registry.BLOCK));
    }

    @Override
    public @Nullable ItemConvertible getItemForRecipe(String key, Class<? extends Block> blockClass) {
        ItemConvertible itemConvertible = super.getItemForRecipe(key, blockClass);
        if ((identifier.getPath().equals("calcite") || identifier.getPath().equals("netherite")) && (key.equals("base") || key.equals("secondary")) && blockClass.getSimpleName().contains("Kitchen")) {
            if (itemConvertible == getBaseBlock())
                return getSecondaryBlock();
            else
                return getBaseBlock();
        }
        return itemConvertible;
    }

    @Override
    public void initializeChildrenItems() {
    }

    public boolean hasStripped() {
        Object child = this.getChild("stripped_log");
        return child != null && child != this.getBaseBlock();
    }
    @Override
    public Block mainChild() {
        return this.polishedBlock;
    }

    public static class Finder implements SetFinder<StoneVariant> {

        private final Map<String, Identifier> childNames = new HashMap<>();
        private final Supplier<Block> polishedFinder;
        private final Supplier<Block> rawFinder;
        private final Identifier id;

        public Finder(Identifier id, Supplier<Block> polished, Supplier<Block> raw) {
            this.id = id;
            this.polishedFinder = polished;
            this.rawFinder = raw;
        }

        public static Finder simple(String modId, String stoneTypeName, String polishedName, String rawName) {
            return simple(new Identifier(modId, stoneTypeName), new Identifier(modId, polishedName), new Identifier(modId, rawName));
        }

        public static Finder simple(Identifier stoneTypeName, Identifier polishedName, Identifier rawName) {
            return new Finder(stoneTypeName,
                    () -> Registry.BLOCK.get(polishedName),
                    () -> Registry.BLOCK.get(rawName));
        }

        public void addChild(String childType, String childName) {
            addChild(childType, new Identifier(id.getNamespace(), childName));
        }

        public void addChild(String childType, Identifier childName) {
            this.childNames.put(childType, childName);
        }

        public Optional<StoneVariant> get() {
            if (BlockItemRegistry.isModLoaded(id.getNamespace())) {
                try {
                    Block plank = polishedFinder.get();
                    Block log = rawFinder.get();
                    Block d = Registry.BLOCK.get(new Identifier("minecraft","air"));
                    if (plank != d && log != d && plank != null && log != null) {
                        StoneVariant w = new StoneVariant(id, plank, log);
                        for (Map.Entry<String, Identifier> entry : childNames.entrySet()){
                            Object child = Registry.BLOCK.getOrEmpty(entry.getValue()).isPresent() ? Registry.BLOCK.get(entry.getValue()) : Registry.ITEM.get(entry.getValue());
                            w.addChild(entry.getKey(), child);
                        }
                        return Optional.of(w);
                    }
                } catch (Exception ignored) {
                }
                PaladinFurnitureMod.GENERAL_LOGGER.warn("Failed to find custom stone type {}", id);
            }
            return Optional.empty();
        }
    }
}