package com.unlikepaladin.pfm.blocks.models.kitchenCabinet.fabric;

import com.unlikepaladin.pfm.blocks.KitchenCabinetBlock;
import com.unlikepaladin.pfm.blocks.KitchenWallDrawerBlock;
import com.unlikepaladin.pfm.blocks.models.ModelHelper;
import com.unlikepaladin.pfm.blocks.models.fabric.PFMFabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.texture.Sprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;

import java.util.List;

import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class FabricKitchenCabinetModel extends PFMFabricBakedModel {
    public FabricKitchenCabinetModel(ModelBakeSettings settings, List<BakedModel> modelParts) {
        super(settings, modelParts);
    }
    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    @Override
    public void emitBlockQuads(QuadEmitter context, BlockRenderView world, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, Predicate<@Nullable Direction> cullTest) {
        if (state.getBlock() instanceof KitchenCabinetBlock) {
            KitchenCabinetBlock block = (KitchenCabinetBlock) state.getBlock();
            Direction direction = state.get(KitchenCabinetBlock.FACING);
            BlockState neighborStateOpposite = world.getBlockState(pos.offset(direction.getOpposite()));
            int openOffset = state.get(KitchenWallDrawerBlock.OPEN) ? 5 : 0;
            List<Sprite> spriteList = getSpriteList(state);
            pushTextureTransform(context, ModelHelper.getOakPlankLogSprites(), spriteList);

            Direction direction3 = null;
            Direction direction2;
            boolean innerCorner = block.isCabinet(neighborStateOpposite) && (direction3 = neighborStateOpposite.get(KitchenCabinetBlock.FACING)).getAxis() != state.get(KitchenCabinetBlock.FACING).getAxis() && block.isDifferentOrientation(state, world, pos, direction3);
            BlockState blockState = world.getBlockState(pos.offset(direction));
            if (block.isCabinet(blockState) && (direction2 = blockState.get(KitchenCabinetBlock.FACING)).getAxis() != state.get(KitchenCabinetBlock.FACING).getAxis() && block.isDifferentOrientation(state, world, pos, direction2.getOpposite())) {
                if (direction2 == direction.rotateYCounterclockwise()) {
                    getTemplateBakedModels().get((3 + openOffset)).emitBlockQuads(context, world, state, pos, randomSupplier, cullTest);
                }
                else {
                    getTemplateBakedModels().get((4 + openOffset)).emitBlockQuads(context, world, state, pos, randomSupplier, cullTest);
                }
            }
            else if (innerCorner) {
                if (direction3 == direction.rotateYCounterclockwise()) {
                    getTemplateBakedModels().get((2 + openOffset)).emitBlockQuads(context, world, state, pos, randomSupplier, cullTest);
                } else {
                    getTemplateBakedModels().get((1 + openOffset)).emitBlockQuads(context, world, state, pos, randomSupplier, cullTest);
                }
            } else {
                getTemplateBakedModels().get((openOffset)).emitBlockQuads(context, world, state, pos, randomSupplier, cullTest);
            }
            context.popTransform();
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