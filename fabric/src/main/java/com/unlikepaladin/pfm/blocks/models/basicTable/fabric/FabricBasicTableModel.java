package com.unlikepaladin.pfm.blocks.models.basicTable.fabric;

import com.unlikepaladin.pfm.blocks.BasicTableBlock;
import com.unlikepaladin.pfm.blocks.models.fabric.PFMFabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.texture.Sprite;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class FabricBasicTableModel extends PFMFabricBakedModel {
    public FabricBasicTableModel(ModelBakeSettings settings, List<BakedModel> modelParts) {
        super(settings, modelParts);
    }

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    @Override
    public void emitBlockQuads(QuadEmitter context, BlockRenderView world, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, Predicate<@Nullable Direction> cullTest) {
        if (state.getBlock() instanceof BasicTableBlock) {
            Direction.Axis dir = state.get(BasicTableBlock.AXIS);
            BasicTableBlock block = (BasicTableBlock) state.getBlock();
            boolean north = block.canConnect(world, state, pos.north(), pos);
            boolean east = block.canConnect(world, state, pos.east(), pos);
            boolean west = block.canConnect(world, state, pos.west(), pos);
            boolean south = block.canConnect(world, state, pos.south(), pos);
            boolean cornerNorthWest = north && west && !block.canConnect(world, state, pos.north().west(), pos);
            boolean cornerNorthEast = north && east && !block.canConnect(world, state, pos.north().east(), pos);
            boolean cornerSouthEast = south && east && !block.canConnect(world, state, pos.south().east(), pos);
            boolean cornerSouthWest = south && west && !block.canConnect(world, state, pos.south().west(), pos);

            List<Sprite> spriteList = getSpriteList(state);
            pushTextureTransform(context, spriteList.get(0));
            getTemplateBakedModels().get(0).emitBlockQuads(context, world, state, pos, randomSupplier, cullTest);
            context.popTransform();


            pushTextureTransform(context, spriteList.get(1));
            if (!north && !south && !east && !west) {
                getTemplateBakedModels().get(8).emitBlockQuads(context, world, state, pos, randomSupplier, cullTest);
                getTemplateBakedModels().get(7).emitBlockQuads(context, world, state, pos, randomSupplier, cullTest);
            }
            if (dir == Direction.Axis.Z) {
                if (!north && !east)  {
                    getTemplateBakedModels().get(1).emitBlockQuads(context, world, state, pos, randomSupplier, cullTest);
                }
                if (!north && !west)  {
                    getTemplateBakedModels().get(2).emitBlockQuads(context, world, state, pos, randomSupplier, cullTest);
                }
                if (!south && !east)  {
                    getTemplateBakedModels().get(3).emitBlockQuads(context, world, state, pos, randomSupplier, cullTest);
                }
                if (!south && !west)  {
                    getTemplateBakedModels().get(4).emitBlockQuads(context, world, state, pos, randomSupplier, cullTest);
                }
                if (!north && south && !east && !west) {
                    getTemplateBakedModels().get(7).emitBlockQuads(context, world, state, pos, randomSupplier, cullTest);
                }
                if (north && !south && !east && !west) {
                    getTemplateBakedModels().get(8).emitBlockQuads(context, world, state, pos, randomSupplier, cullTest);
                }
                if (!north && east && !west) {
                    getTemplateBakedModels().get(5).emitBlockQuads(context, world, state, pos, randomSupplier, cullTest);
                }
                if (!south && !east && west) {
                    getTemplateBakedModels().get(10).emitBlockQuads(context, world, state, pos, randomSupplier, cullTest);
                }
                if (!south && east && !west) {
                    getTemplateBakedModels().get(9).emitBlockQuads(context, world, state, pos, randomSupplier, cullTest);
                }
                if (!north && !east && west) {
                    getTemplateBakedModels().get(6).emitBlockQuads(context, world, state, pos, randomSupplier, cullTest);
                }
                if (!north && east && west) {
                    getTemplateBakedModels().get(12).emitBlockQuads(context, world, state, pos, randomSupplier, cullTest);
                }
                if (!south && east && west) {
                    getTemplateBakedModels().get(11).emitBlockQuads(context, world, state, pos, randomSupplier, cullTest);
                }
                if (cornerNorthEast) {
                    getTemplateBakedModels().get(13).emitBlockQuads(context, world, state, pos, randomSupplier, cullTest);
                    getTemplateBakedModels().get(1).emitBlockQuads(context, world, state, pos, randomSupplier, cullTest);
                }
                if (cornerNorthWest) {
                    getTemplateBakedModels().get(14).emitBlockQuads(context, world, state, pos, randomSupplier, cullTest);
                    getTemplateBakedModels().get(2).emitBlockQuads(context, world, state, pos, randomSupplier, cullTest);
                }
                if (cornerSouthWest) {
                    getTemplateBakedModels().get(16).emitBlockQuads(context, world, state, pos, randomSupplier, cullTest);
                    getTemplateBakedModels().get(4).emitBlockQuads(context, world, state, pos, randomSupplier, cullTest);
                }
                if (cornerSouthEast) {
                    getTemplateBakedModels().get(15).emitBlockQuads(context, world, state, pos, randomSupplier, cullTest);
                    getTemplateBakedModels().get(3).emitBlockQuads(context, world, state, pos, randomSupplier, cullTest);
                }
            } else {
                if (!north && !east)  {
                    getTemplateBakedModels().get(2).emitBlockQuads(context, world, state, pos, randomSupplier, cullTest);
                }
                if (!north && !west)  {
                    getTemplateBakedModels().get(4).emitBlockQuads(context, world, state, pos, randomSupplier, cullTest);
                }
                if (!south && !east)  {
                    getTemplateBakedModels().get(1).emitBlockQuads(context, world, state, pos, randomSupplier, cullTest);
                }
                if (!south && !west)  {
                    getTemplateBakedModels().get(3).emitBlockQuads(context, world, state, pos, randomSupplier, cullTest);
                }
                if (!north && south && !west) {
                    getTemplateBakedModels().get(9).emitBlockQuads(context, world, state, pos, randomSupplier, cullTest);
                }
                if (north && !south && !west) {
                    getTemplateBakedModels().get(10).emitBlockQuads(context, world, state, pos, randomSupplier, cullTest);
                }
                if (!north && south && !east) {
                    getTemplateBakedModels().get(5).emitBlockQuads(context, world, state, pos, randomSupplier, cullTest);
                }
                if (north && !south && !east) {
                    getTemplateBakedModels().get(6).emitBlockQuads(context, world, state, pos, randomSupplier, cullTest);
                }

                if (!north && !south && !east) {
                    getTemplateBakedModels().get(7).emitBlockQuads(context, world, state, pos, randomSupplier, cullTest);
                }
                if (!north && !south && !west) {
                    getTemplateBakedModels().get(8).emitBlockQuads(context, world, state, pos, randomSupplier, cullTest);
                }

                if (north && south && !east) {
                    getTemplateBakedModels().get(12).emitBlockQuads(context, world, state, pos, randomSupplier, cullTest);
                }
                if (north && south && !west) {
                    getTemplateBakedModels().get(11).emitBlockQuads(context, world, state, pos, randomSupplier, cullTest);
                }

                if (cornerNorthEast) {
                    getTemplateBakedModels().get(14).emitBlockQuads(context, world, state, pos, randomSupplier, cullTest);
                    getTemplateBakedModels().get(2).emitBlockQuads(context, world, state, pos, randomSupplier, cullTest);
                }
                if (cornerSouthEast) {
                    getTemplateBakedModels().get(13).emitBlockQuads(context, world, state, pos, randomSupplier, cullTest);
                    getTemplateBakedModels().get(1).emitBlockQuads(context, world, state, pos, randomSupplier, cullTest);
                }
                if (cornerNorthWest) {
                    getTemplateBakedModels().get(16).emitBlockQuads(context, world, state, pos, randomSupplier, cullTest);
                    getTemplateBakedModels().get(4).emitBlockQuads(context, world, state, pos, randomSupplier, cullTest);
                }
                if (cornerSouthWest) {
                    getTemplateBakedModels().get(15).emitBlockQuads(context, world, state, pos, randomSupplier, cullTest);
                    getTemplateBakedModels().get(3).emitBlockQuads(context, world, state, pos, randomSupplier, cullTest);
                }
            }
            context.popTransform();
        }
    }

    @Override
    public void emitItemQuads(QuadEmitter context, Supplier<Random> randomSupplier) {
        if (blockState != null) {
            pushTextureTransform(context, getSpriteList(blockState).get(0));
            // base
            getTemplateBakedModels().get(0).emitItemQuads(context, randomSupplier);
            context.popTransform();

            pushTextureTransform(context, getSpriteList(blockState).get(1));
            // legs
            getTemplateBakedModels().get(1).emitItemQuads(context, randomSupplier);
            getTemplateBakedModels().get(2).emitItemQuads(context, randomSupplier);
            getTemplateBakedModels().get(3).emitItemQuads(context, randomSupplier);
            getTemplateBakedModels().get(4).emitItemQuads(context, randomSupplier);
            // in between pieces
            getTemplateBakedModels().get(8).emitItemQuads(context, randomSupplier);
            getTemplateBakedModels().get(7).emitItemQuads(context, randomSupplier);
            context.popTransform();
        }
    }

    @Override
    public Sprite pfm$getParticle(BlockState state) {
        return getSpriteList(state).get(0);
    }
}