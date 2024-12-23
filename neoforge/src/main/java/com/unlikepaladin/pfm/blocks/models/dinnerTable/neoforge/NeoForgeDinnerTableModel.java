package com.unlikepaladin.pfm.blocks.models.dinnerTable.neoforge;

import com.unlikepaladin.pfm.blocks.DinnerTableBlock;
import com.unlikepaladin.pfm.blocks.models.neoforge.ModelBitSetProperty;
import com.unlikepaladin.pfm.blocks.models.neoforge.PFMNeoForgeBakedModel;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import net.minecraft.util.math.random.Random;

public class NeoForgeDinnerTableModel extends PFMNeoForgeBakedModel {
    public NeoForgeDinnerTableModel(ModelBakeSettings settings, List<BakedModel> modelList) {
        super(settings, modelList);
    }

    public static ModelProperty<ModelBitSetProperty> CONNECTIONS = new ModelProperty<>();

    @NotNull
    @Override
    public ModelData getModelData(@NotNull BlockRenderView world, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ModelData tileData) {
        if (state.getBlock() instanceof DinnerTableBlock) {
            ModelData.Builder builder = ModelData.builder();

            ModelData data = builder.build();
            data = super.getModelData(world, pos, state, data);

            DinnerTableBlock block = (DinnerTableBlock) state.getBlock();
            Direction dir = state.get(DinnerTableBlock.FACING);
            boolean left = block.isTable(world, pos, dir.rotateYCounterclockwise(), dir);
            boolean right = block.isTable(world, pos, dir.rotateYClockwise(), dir);
            BitSet set = new BitSet();
            set.set(0, left);
            set.set(1, right);
            data = data.derive().with(CONNECTIONS, new ModelBitSetProperty(set)).build();
            return data;
        }
        return tileData;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull Random rand, @NotNull ModelData extraData, RenderLayer renderType) {
        if (state != null && state.getBlock() instanceof DinnerTableBlock && extraData.get(CONNECTIONS) != null && extraData.get(CONNECTIONS).connections != null) {
            List<BakedQuad> baseQuads = new ArrayList<>();
            List<BakedQuad> secondaryQuads = new ArrayList<>();

            BitSet set = extraData.get(CONNECTIONS).connections;
            boolean left = set.get(0);
            boolean right = set.get(1);
            Direction dir = state.get(DinnerTableBlock.FACING);
            baseQuads.addAll(getTemplateBakedModels().get(0).getQuads(state, side, rand, extraData, renderType));
            if (!left) {
                int index = dir == Direction.NORTH || dir == Direction.WEST ? 1 : 2;
                secondaryQuads.addAll(getTemplateBakedModels().get(index).getQuads(state, side, rand, extraData, renderType));
            }
            if (!right) {
                int index = dir == Direction.NORTH || dir == Direction.WEST ? 2 : 1;
                secondaryQuads.addAll(getTemplateBakedModels().get(index).getQuads(state, side, rand, extraData, renderType));
            }
            if (!right && !left) {
                secondaryQuads.addAll(getTemplateBakedModels().get(3).getQuads(state, side, rand, extraData, renderType));
            }
            List<Sprite> spriteList = getSpriteList(state);
            List<BakedQuad> quads = getQuadsWithTexture(baseQuads, new SpriteData(spriteList.get(0)));
            quads.addAll(getQuadsWithTexture(secondaryQuads, new SpriteData(spriteList.get(1))));
            return quads;
        }
        return Collections.emptyList();
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable Direction face, Random random) {
        // base
        List<BakedQuad> baseQuads = new ArrayList<>(getTemplateBakedModels().get(0).getQuads(null, face, random));
        // legs
        List<BakedQuad> secondaryQuads = new ArrayList<>(getTemplateBakedModels().get(3).getQuads(null, face, random));

        List<Sprite> spriteList = getSpriteList(blockState);
        List<BakedQuad> quads = getQuadsWithTexture(baseQuads, new SpriteData(spriteList.get(0)));
        quads.addAll(getQuadsWithTexture(secondaryQuads, new SpriteData(spriteList.get(1))));
        return quads;
    }
}