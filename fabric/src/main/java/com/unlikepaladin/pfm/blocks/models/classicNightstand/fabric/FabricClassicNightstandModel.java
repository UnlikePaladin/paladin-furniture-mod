package com.unlikepaladin.pfm.blocks.models.classicNightstand.fabric;

import com.unlikepaladin.pfm.blocks.ClassicNightstandBlock;
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

public class FabricClassicNightstandModel extends PFMFabricBakedModel {
    public FabricClassicNightstandModel(ModelBakeSettings settings, List<BakedModel> modelParts) {
        super(settings, modelParts);
    }

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    @Override
    public void emitBlockQuads(QuadEmitter context, BlockRenderView world, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, Predicate<@Nullable Direction> cullTest) {
        if (state.getBlock() instanceof ClassicNightstandBlock) {
            ClassicNightstandBlock block = (ClassicNightstandBlock) state.getBlock();
            Direction dir = state.get(ClassicNightstandBlock.FACING);
            boolean left = block.isStand(world, pos, dir.rotateYCounterclockwise(), dir);
            boolean right = block.isStand(world, pos, dir.rotateYClockwise(), dir);
            int openIndexOffset = state.get(ClassicNightstandBlock.OPEN) ? 4 : 0;
            List<Sprite> spriteList = getSpriteList(state);
            pushTextureTransform(context, ModelHelper.getOakPlankLogSprites(), spriteList);
            if (left && right) {
                getTemplateBakedModels().get((openIndexOffset)).emitBlockQuads(context, world, state, pos, randomSupplier, cullTest);
            } else if (!left && right) {
                getTemplateBakedModels().get((1+openIndexOffset)).emitBlockQuads(context, world, state, pos, randomSupplier, cullTest);
            } else if (left) {
                getTemplateBakedModels().get((2+openIndexOffset)).emitBlockQuads(context, world, state, pos, randomSupplier, cullTest);
            } else {
                getTemplateBakedModels().get((3+openIndexOffset)).emitBlockQuads(context, world, state, pos, randomSupplier, cullTest);
            }
            context.popTransform();
        }
    }

    @Override
    public void emitItemQuads(QuadEmitter context, Supplier<Random> randomSupplier) {
        if (blockState == null) return;

        List<Sprite> spriteList = getSpriteList(blockState);
        pushTextureTransform(context, ModelHelper.getOakPlankLogSprites(), spriteList);
        getTemplateBakedModels().get((3)).emitItemQuads(context, randomSupplier);
        context.popTransform();
    }

    @Override
    public Sprite pfm$getParticle(BlockState state) {
        return getSpriteList(state).get(0);
    }
}
