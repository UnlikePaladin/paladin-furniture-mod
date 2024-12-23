package com.unlikepaladin.pfm.data.materials;

import com.unlikepaladin.pfm.blocks.models.ModelHelper;
import com.unlikepaladin.pfm.mixin.PFMFeatureFlagFactory;
import com.unlikepaladin.pfm.registry.PaladinFurnitureModBlocksItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.resource.featuretoggle.FeatureFlag;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExtraCounterVariant extends VariantBase<ExtraCounterVariant> {
    public static ExtraCounterVariant DARK_CONCRETE = new ExtraCounterVariant(Blocks.GRAY_CONCRETE, Blocks.WHITE_CONCRETE, "dark_concrete");
    public static ExtraCounterVariant CONCRETE = new ExtraCounterVariant(Blocks.WHITE_CONCRETE, null, "concrete");
    public static ExtraCounterVariant SMOOTH_STONE = new ExtraCounterVariant(Blocks.WHITE_CONCRETE, Blocks.SMOOTH_STONE,"smooth_stone");
    public static ExtraCounterVariant DEEPSLATE_TILE = new ExtraCounterVariant(Blocks.QUARTZ_BLOCK, Blocks.DEEPSLATE_TILES,"deepslate_tile");
    private final String name;
    private final Block baseBlock;
    static final List<ExtraCounterVariant> DEFAULT_VARIANTS = new ArrayList<>();

    private final Block secondaryBlock;

    public static List<ExtraCounterVariant> values() {
        if (DEFAULT_VARIANTS.isEmpty()) {
            DEFAULT_VARIANTS.add(DARK_CONCRETE);
            DEFAULT_VARIANTS.add(CONCRETE);
            DEFAULT_VARIANTS.add(SMOOTH_STONE);
            DEFAULT_VARIANTS.add(DEEPSLATE_TILE);
        }
        return DEFAULT_VARIANTS;
    }

    ExtraCounterVariant(Identifier identifier, Block baseBlock, Block secondaryBlock) {
        super(identifier);
        this.name = identifier.getPath();
        this.baseBlock = baseBlock;
        this.secondaryBlock = secondaryBlock;
    }
    ExtraCounterVariant(Block baseBlock, Block secondaryBlock, String name) {
        this(Identifier.of("", name), baseBlock, secondaryBlock);
    }

    public static Optional<ExtraCounterVariant> getOptionalVariant(Identifier name) {
        return ExtraCounterVariant.DEFAULT_VARIANTS.stream().filter(extraCounterVariant -> extraCounterVariant.identifier.equals(name)).findFirst();
    }

    @Override
    public String asString() {
        return name;
    }

    @Override
    public Block getBaseBlock() {
        return baseBlock;
    }

    @Override
    public Block getSecondaryBlock() {
        if (secondaryBlock == null)
            return PaladinFurnitureModBlocksItems.RAW_CONCRETE;

        return secondaryBlock;
    }

    @Override
    public boolean isNetherWood() {
        return false;
    }

    @Override
    public ExtraCounterVariant getVariantType() {
        return this;
    }

    @Override
    public boolean isVanilla() {
        return identifier.getNamespace().equals("") || identifier.getNamespace().equals("minecraft");
    }

    @Override
    public void initializeChildrenBlocks() {

    }

    @Override
    public void initializeChildrenItems() {

    }

    @Override
    public List<FeatureFlag> getFeatureList() {
        FeatureFlag flag = PFMFeatureFlagFactory.newFlag(getBaseBlock().getRequiredFeatures().universe, 0);
        flag.mask = getBaseBlock().getRequiredFeatures().featuresMask;
        return List.of(flag);
    }

    @Override
    public Block mainChild() {
        return baseBlock;
    }

    @Environment(EnvType.CLIENT)
    @Override
    public Identifier getTexture(BlockType type) {
        if (type == BlockType.SECONDARY)
            return ModelHelper.getTextureId(getSecondaryBlock());
        return ModelHelper.getTextureId(baseBlock);
    }

    @Override
    public String getPath() {
        return this.identifier.getPath();
    }
}