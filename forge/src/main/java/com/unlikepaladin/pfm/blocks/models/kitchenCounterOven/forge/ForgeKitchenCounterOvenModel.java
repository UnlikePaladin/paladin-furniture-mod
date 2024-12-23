package com.unlikepaladin.pfm.blocks.models.kitchenCounterOven.forge;

import com.unlikepaladin.pfm.blocks.KitchenCounterOvenBlock;
import com.unlikepaladin.pfm.blocks.models.ModelHelper;
import com.unlikepaladin.pfm.blocks.models.forge.ModelBitSetProperty;
import com.unlikepaladin.pfm.blocks.models.forge.PFMForgeBakedModel;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import net.minecraft.util.math.random.Random;

public class ForgeKitchenCounterOvenModel extends PFMForgeBakedModel {
    public ForgeKitchenCounterOvenModel(ModelBakeSettings settings, List<BakedModel> modelParts) {
        super(settings, modelParts);
    }

    @NotNull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull Random rand, @NotNull ModelData extraData, RenderLayer renderLayer) {
        if (state != null && state.getBlock() instanceof KitchenCounterOvenBlock && extraData.get(CONNECTIONS) != null && extraData.get(CONNECTIONS).connections != null) {
            List<BakedQuad> quads = new ArrayList<>();
            BitSet data = extraData.get(CONNECTIONS).connections;
            int openOffset = state.get(KitchenCounterOvenBlock.OPEN) ? 2 : 0;
            List<Sprite> spriteList = getSpriteList(state);
            if (data.get(0) || data.get(1)) {
                quads.addAll(getTemplateBakedModels().get(1 + openOffset).getQuads(state, side, rand, extraData, renderLayer));
            } else {
                quads.addAll(getTemplateBakedModels().get(openOffset).getQuads(state, side, rand, extraData, renderLayer));
            }
            return getQuadsWithTexture(quads, ModelHelper.getOakPlankLogSprites(), spriteList);
        }
        return Collections.emptyList();
    }

    public static ModelProperty<ModelBitSetProperty> CONNECTIONS = new ModelProperty<>();
    @NotNull
    @Override
    public ModelData getModelData(@NotNull BlockRenderView world, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ModelData tileData) {
        if (state.getBlock() instanceof KitchenCounterOvenBlock) {
            ModelData.Builder builder = ModelData.builder();

            ModelData data = builder.build();
            data = super.getModelData(world, pos, state, data);

            BitSet set = new BitSet();
            set.set(0, KitchenCounterOvenBlock.connectsVertical(world.getBlockState(pos.up()).getBlock()));
            set.set(1, KitchenCounterOvenBlock.connectsVertical(world.getBlockState(pos.down()).getBlock()));
            data = data.derive().with(CONNECTIONS, new ModelBitSetProperty(set)).build();
            return data;
        }
        return tileData;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable Direction face, Random random) {
        List<Sprite> spriteList = getSpriteList(blockState);
        return getQuadsWithTexture(getTemplateBakedModels().get(0).getQuads(null, face, random), ModelHelper.getOakPlankLogSprites(), spriteList);
    }
}
