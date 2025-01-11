package com.unlikepaladin.pfm.blocks.models.kitchenCounter.fabric;

import com.unlikepaladin.pfm.blocks.KitchenCounterBlock;
import com.unlikepaladin.pfm.blocks.models.ModelHelper;
import com.unlikepaladin.pfm.blocks.models.fabric.PFMFabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.texture.Sprite;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;

import java.util.List;

import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class FabricKitchenCounterModel extends PFMFabricBakedModel {
    public FabricKitchenCounterModel(ModelBakeSettings settings, List<BakedModel> modelParts) {
        super(settings, modelParts);
    }
    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    @Override
    public void emitBlockQuads(QuadEmitter context, BlockRenderView world, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, Predicate<@Nullable Direction> cullTest) {
        if (state.getBlock() instanceof KitchenCounterBlock) {
            KitchenCounterBlock block = (KitchenCounterBlock) state.getBlock();
            Direction direction = state.get(KitchenCounterBlock.FACING);
            boolean right = block.canConnect(world, pos, direction.rotateYCounterclockwise());
            boolean left = block.canConnect(world, pos, direction.rotateYClockwise());
            BlockState neighborStateFacing = world.getBlockState(pos.offset(direction));
            BlockState neighborStateOpposite = world.getBlockState(pos.offset(direction.getOpposite()));
            List<Sprite> spriteList = getSpriteList(state);
            pushTextureTransform(context, ModelHelper.getOakPlankLogSprites(), spriteList);
            if (block.canConnectToCounter(neighborStateFacing) && neighborStateFacing.contains(Properties.HORIZONTAL_FACING)) {
                Direction direction2 = neighborStateFacing.get(Properties.HORIZONTAL_FACING);
                if (direction2.getAxis() != state.get(Properties.HORIZONTAL_FACING).getAxis() && block.isDifferentOrientation(state, world, pos, direction2.getOpposite())) {
                    if (direction2 == direction.rotateYCounterclockwise()) {
                        getTemplateBakedModels().get((5)).emitBlockQuads(context, world, state, pos, randomSupplier, cullTest);
                    }
                    else {
                        getTemplateBakedModels().get((6)).emitBlockQuads(context, world, state, pos, randomSupplier, cullTest);
                    }
                } else {
                    middleCounter(world, state, pos, randomSupplier, context, cullTest, left, right);
                }
            }
            else if (block.canConnectToCounter(neighborStateOpposite) && neighborStateOpposite.contains(Properties.HORIZONTAL_FACING)) {
                Direction direction3;
                if (neighborStateOpposite.getBlock() instanceof AbstractFurnaceBlock) {
                    direction3 = neighborStateOpposite.get(Properties.HORIZONTAL_FACING).getOpposite();
                }
                else {
                    direction3 = neighborStateOpposite.get(Properties.HORIZONTAL_FACING);
                }
                if (direction3.getAxis() != state.get(Properties.HORIZONTAL_FACING).getAxis() && block.isDifferentOrientation(state, world, pos, direction3)) {
                    if (direction3 == direction.rotateYCounterclockwise()) {
                        getTemplateBakedModels().get((4)).emitBlockQuads(context, world, state, pos, randomSupplier, cullTest);
                    } else {
                        getTemplateBakedModels().get((3)).emitBlockQuads(context, world, state, pos, randomSupplier, cullTest);
                    }
                } else {
                    middleCounter(world, state, pos, randomSupplier, context, cullTest, left, right);
                }
            }
            else {
                middleCounter(world, state, pos, randomSupplier, context, cullTest, left, right);
            }
            context.popTransform();
        }
    }

    private void middleCounter(BlockRenderView world, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, QuadEmitter context, Predicate<@Nullable Direction> cullTest, boolean left, boolean right) {
        if (left && right) {
            getTemplateBakedModels().get((0)).emitBlockQuads(context, world, state, pos, randomSupplier, cullTest);
        } else if (left) {
            getTemplateBakedModels().get((1)).emitBlockQuads(context, world, state, pos, randomSupplier, cullTest);
        } else if (right) {
            getTemplateBakedModels().get((2)).emitBlockQuads(context, world, state, pos, randomSupplier, cullTest);
        } else {
            getTemplateBakedModels().get((0)).emitBlockQuads(context, world, state, pos, randomSupplier, cullTest);
        }
    }

    @Override
    public void emitItemQuads(QuadEmitter context, Supplier<Random> randomSupplier) {
        if (blockState == null) return;

        List<Sprite> spriteList = getSpriteList(blockState);
        pushTextureTransform(context, ModelHelper.getOakPlankLogSprites(), spriteList);
        getTemplateBakedModels().get((0)).emitItemQuads(context, randomSupplier);
        context.popTransform();
    }

    @Override
    public Sprite pfm$getParticle(BlockState state) {
        return getSpriteList(state).get(0);
    }
}