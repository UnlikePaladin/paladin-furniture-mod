package com.unlikepaladin.pfm.blocks.models.ladder.neoforge;

import com.unlikepaladin.pfm.blocks.SimpleBunkLadderBlock;
import com.unlikepaladin.pfm.blocks.models.neoforge.PFMNeoForgeBakedModel;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class NeoForgeLadderModel extends PFMNeoForgeBakedModel {

    public NeoForgeLadderModel(ModelBakeSettings settings, List<BakedModel> templateBakedModels) {
        super(settings, templateBakedModels);
    }

    @NotNull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull Random rand, @NotNull ModelData extraData, RenderLayer layer) {
        if (state != null && state.getBlock() instanceof SimpleBunkLadderBlock) {
            int offset = state.get(SimpleBunkLadderBlock.UP) ? 1 : 0;
            Sprite sprite = getSpriteList(state).get(0);
            return getQuadsWithTexture(getTemplateBakedModels().get(offset).getQuads(state, side, rand, extraData, layer), new SpriteData(sprite));
        }
        return Collections.emptyList();
    }

    @Override
    public @NotNull ModelData getModelData(@NotNull BlockRenderView world, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ModelData tileData) {
        return super.getModelData(world, pos, state, ModelData.builder().build());
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable Direction face, Random random) {
        Sprite sprite = getSpriteList(blockState).get(0);
        return getQuadsWithTexture(getTemplateBakedModels().get(0).getQuads(null, face, random), new SpriteData(sprite));
    }
}
