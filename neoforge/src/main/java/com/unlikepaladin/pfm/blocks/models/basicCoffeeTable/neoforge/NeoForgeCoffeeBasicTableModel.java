package com.unlikepaladin.pfm.blocks.models.basicCoffeeTable.neoforge;

import com.unlikepaladin.pfm.blocks.BasicCoffeeTableBlock;
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
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;

public class NeoForgeCoffeeBasicTableModel extends PFMNeoForgeBakedModel {
    public NeoForgeCoffeeBasicTableModel(ModelBakeSettings settings, List<BakedModel> modelParts) {
        super(settings, modelParts);
    }

    public static ModelProperty<ModelBitSetProperty> CONNECTIONS = new ModelProperty<>();
    @NotNull
    @Override
    public ModelData getModelData(@NotNull BlockRenderView world, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ModelData tileData) {
        if (state.getBlock() instanceof BasicCoffeeTableBlock) {
            ModelData.Builder builder = ModelData.builder();

            ModelData data = builder.build();
            data = super.getModelData(world, pos, state, data);

            BasicCoffeeTableBlock block = (BasicCoffeeTableBlock) state.getBlock();
            boolean north = block.canConnect(world, state, pos.north(), pos);
            boolean east = block.canConnect(world, state, pos.east(), pos);
            boolean west = block.canConnect(world, state, pos.west(), pos);
            boolean south = block.canConnect(world, state, pos.south(), pos);
            boolean cornerNorthWest = north && west && !block.canConnect(world, state, pos.north().west(), pos);
            boolean cornerNorthEast = north && east && !block.canConnect(world, state, pos.north().east(), pos);
            boolean cornerSouthEast = south && east && !block.canConnect(world, state, pos.south().east(), pos);
            boolean cornerSouthWest = south && west && !block.canConnect(world, state, pos.south().west(), pos);
            BitSet set = new BitSet();
            set.set(0, north);
            set.set(1, east);
            set.set(2, west);
            set.set(3, south);
            set.set(4, cornerNorthWest);
            set.set(5, cornerNorthEast);
            set.set(6, cornerSouthEast);
            set.set(7, cornerSouthWest);
            data = data.derive().with(CONNECTIONS, new ModelBitSetProperty(set)).build();
            return data;
        }
        return tileData;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull Random rand, @NotNull ModelData extraData, RenderLayer renderType) {
        if (state != null && state.getBlock() instanceof BasicCoffeeTableBlock && extraData.get(CONNECTIONS) != null && extraData.get(CONNECTIONS).connections != null) {
            List<BakedQuad> baseQuads = new ArrayList<>();
            List<BakedQuad> secondaryQuads = new ArrayList<>();

            BitSet set = extraData.get(CONNECTIONS).connections;
            boolean north = set.get(0);
            boolean east = set.get(1);
            boolean west = set.get(2);
            boolean south = set.get(3);
            boolean cornerNorthWest = set.get(4);
            boolean cornerNorthEast = set.get(5);
            boolean cornerSouthEast = set.get(6);
            boolean cornerSouthWest = set.get(7);
            Direction.Axis dir = state.get(BasicCoffeeTableBlock.AXIS);
            baseQuads.addAll(getTemplateBakedModels().get(0).getQuads(state, side, rand, extraData, renderType));
            if (!north && !south && !east && !west) {
                secondaryQuads.addAll(getTemplateBakedModels().get(8).getQuads(state, side, rand, extraData, renderType));
                secondaryQuads.addAll(getTemplateBakedModels().get(7).getQuads(state, side, rand, extraData, renderType));
            }
            if (dir == Direction.Axis.Z) {
                if (!north && !east)  {
                    secondaryQuads.addAll(getTemplateBakedModels().get(1).getQuads(state, side, rand, extraData, renderType));
                }
                if (!north && !west)  {
                    secondaryQuads.addAll(getTemplateBakedModels().get(2).getQuads(state, side, rand, extraData, renderType));
                }
                if (!south && !east)  {
                    secondaryQuads.addAll(getTemplateBakedModels().get(3).getQuads(state, side, rand, extraData, renderType));
                }
                if (!south && !west)  {
                    secondaryQuads.addAll(getTemplateBakedModels().get(4).getQuads(state, side, rand, extraData, renderType));
                }
                if (!north && south && !east && !west) {
                    secondaryQuads.addAll(getTemplateBakedModels().get(7).getQuads(state, side, rand, extraData, renderType));
                }
                if (north && !south && !east && !west) {
                    secondaryQuads.addAll(getTemplateBakedModels().get(8).getQuads(state, side, rand, extraData, renderType));
                }
                if (!north && east && !west) {
                    secondaryQuads.addAll(getTemplateBakedModels().get(5).getQuads(state, side, rand, extraData, renderType));
                }
                if (!south && !east && west) {
                    secondaryQuads.addAll(getTemplateBakedModels().get(10).getQuads(state, side, rand, extraData, renderType));
                }
                if (!south && east && !west) {
                    secondaryQuads.addAll(getTemplateBakedModels().get(9).getQuads(state, side, rand, extraData, renderType));
                }
                if (!north && !east && west) {
                    secondaryQuads.addAll(getTemplateBakedModels().get(6).getQuads(state, side, rand, extraData, renderType));
                }
                if (!north && east && west) {
                    secondaryQuads.addAll(getTemplateBakedModels().get(12).getQuads(state, side, rand, extraData, renderType));
                }
                if (!south && east && west) {
                    secondaryQuads.addAll(getTemplateBakedModels().get(11).getQuads(state, side, rand, extraData, renderType));
                }
                if (cornerNorthEast) {
                    secondaryQuads.addAll(getTemplateBakedModels().get(13).getQuads(state, side, rand, extraData, renderType));
                    secondaryQuads.addAll(getTemplateBakedModels().get(1).getQuads(state, side, rand, extraData, renderType));
                }
                if (cornerNorthWest) {
                    secondaryQuads.addAll(getTemplateBakedModels().get(14).getQuads(state, side, rand, extraData, renderType));
                    secondaryQuads.addAll(getTemplateBakedModels().get(2).getQuads(state, side, rand, extraData, renderType));
                }
                if (cornerSouthWest) {
                    secondaryQuads.addAll(getTemplateBakedModels().get(16).getQuads(state, side, rand, extraData, renderType));
                    secondaryQuads.addAll(getTemplateBakedModels().get(4).getQuads(state, side, rand, extraData, renderType));
                }
                if (cornerSouthEast) {
                    secondaryQuads.addAll(getTemplateBakedModels().get(15).getQuads(state, side, rand, extraData, renderType));
                    secondaryQuads.addAll(getTemplateBakedModels().get(3).getQuads(state, side, rand, extraData, renderType));
                }
            } else {
                if (!north && !east)  {
                    secondaryQuads.addAll(getTemplateBakedModels().get(2).getQuads(state, side, rand, extraData, renderType));
                }
                if (!north && !west)  {
                    secondaryQuads.addAll(getTemplateBakedModels().get(4).getQuads(state, side, rand, extraData, renderType));
                }
                if (!south && !east)  {
                    secondaryQuads.addAll(getTemplateBakedModels().get(1).getQuads(state, side, rand, extraData, renderType));
                }
                if (!south && !west)  {
                    secondaryQuads.addAll(getTemplateBakedModels().get(3).getQuads(state, side, rand, extraData, renderType));
                }
                if (!north && south && !west) {
                    secondaryQuads.addAll(getTemplateBakedModels().get(9).getQuads(state, side, rand, extraData, renderType));
                }
                if (north && !south && !west) {
                    secondaryQuads.addAll(getTemplateBakedModels().get(10).getQuads(state, side, rand, extraData, renderType));
                }
                if (!north && south && !east) {
                    secondaryQuads.addAll(getTemplateBakedModels().get(5).getQuads(state, side, rand, extraData, renderType));
                }
                if (north && !south && !east) {
                    secondaryQuads.addAll(getTemplateBakedModels().get(6).getQuads(state, side, rand, extraData, renderType));
                }

                if (!north && !south && !east) {
                    secondaryQuads.addAll(getTemplateBakedModels().get(7).getQuads(state, side, rand, extraData, renderType));
                }
                if (!north && !south && !west) {
                    secondaryQuads.addAll(getTemplateBakedModels().get(8).getQuads(state, side, rand, extraData, renderType));
                }

                if (north && south && !east) {
                    secondaryQuads.addAll(getTemplateBakedModels().get(12).getQuads(state, side, rand, extraData, renderType));
                }
                if (north && south && !west) {
                    secondaryQuads.addAll(getTemplateBakedModels().get(11).getQuads(state, side, rand, extraData, renderType));
                }

                if (cornerNorthEast) {
                    secondaryQuads.addAll(getTemplateBakedModels().get(14).getQuads(state, side, rand, extraData, renderType));
                    secondaryQuads.addAll(getTemplateBakedModels().get(2).getQuads(state, side, rand, extraData, renderType));
                }
                if (cornerSouthEast) {
                    secondaryQuads.addAll(getTemplateBakedModels().get(13).getQuads(state, side, rand, extraData, renderType));
                    secondaryQuads.addAll(getTemplateBakedModels().get(1).getQuads(state, side, rand, extraData, renderType));
                }
                if (cornerNorthWest) {
                    secondaryQuads.addAll(getTemplateBakedModels().get(16).getQuads(state, side, rand, extraData, renderType));
                    secondaryQuads.addAll(getTemplateBakedModels().get(4).getQuads(state, side, rand, extraData, renderType));
                }
                if (cornerSouthWest) {
                    secondaryQuads.addAll(getTemplateBakedModels().get(15).getQuads(state, side, rand, extraData, renderType));
                    secondaryQuads.addAll(getTemplateBakedModels().get(3).getQuads(state, side, rand, extraData, renderType));
                }
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

        List<BakedQuad> secondaryQuads = new ArrayList<>();
        // legs
        secondaryQuads.addAll(getTemplateBakedModels().get(1).getQuads(null, face, random));
        secondaryQuads.addAll(getTemplateBakedModels().get(2).getQuads(null, face, random));
        secondaryQuads.addAll(getTemplateBakedModels().get(3).getQuads(null, face, random));
        secondaryQuads.addAll(getTemplateBakedModels().get(4).getQuads(null, face, random));
        // in between pieces
        secondaryQuads.addAll(getTemplateBakedModels().get(8).getQuads(null, face, random));
        secondaryQuads.addAll(getTemplateBakedModels().get(7).getQuads(null, face, random));

        List<Sprite> spriteList = getSpriteList(blockState);
        List<BakedQuad> quads = getQuadsWithTexture(baseQuads, new SpriteData(spriteList.get(0)));
        quads.addAll(getQuadsWithTexture(secondaryQuads, new SpriteData(spriteList.get(1))));
        return quads;
    }
}