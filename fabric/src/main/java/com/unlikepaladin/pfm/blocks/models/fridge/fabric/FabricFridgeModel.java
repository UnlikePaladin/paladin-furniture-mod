package com.unlikepaladin.pfm.blocks.models.fridge.fabric;

import com.unlikepaladin.pfm.blocks.FreezerBlock;
import com.unlikepaladin.pfm.blocks.FridgeBlock;
import com.unlikepaladin.pfm.blocks.IronFridgeBlock;
import com.unlikepaladin.pfm.blocks.models.fabric.PFMFabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
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
import java.util.Map;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class FabricFridgeModel extends PFMFabricBakedModel {
    public FabricFridgeModel(Sprite frame, ModelBakeSettings settings, Map<String, BakedModel> bakedModels, List<String> modelParts) {
        super(settings, bakedModels.values().stream().toList());
    }

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    @Override
    public void emitBlockQuads(QuadEmitter context, BlockRenderView world, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, Predicate<@Nullable Direction> cullTest) {
        boolean bottom = state.isOf(world.getBlockState(pos.up()).getBlock());
        boolean top = state.isOf(world.getBlockState(pos.down()).getBlock());
        boolean hasFreezer = world.getBlockState(pos.up()).getBlock() instanceof FreezerBlock && !(world.getBlockState(pos.up()).getBlock() instanceof IronFridgeBlock);
        int openOffset = state.get(FridgeBlock.OPEN) ? 6 : 0;
        if (top && hasFreezer) {
            getTemplateBakedModels().get((5+openOffset)).emitBlockQuads(context, world, state, pos, randomSupplier, cullTest);
        }
        else if (top && bottom) {
            getTemplateBakedModels().get((2+openOffset)).emitBlockQuads(context, world, state, pos, randomSupplier, cullTest);
        } else if (bottom) {
            getTemplateBakedModels().get((3+openOffset)).emitBlockQuads(context, world, state, pos, randomSupplier, cullTest);
        } else if (top) {
            getTemplateBakedModels().get((1+openOffset)).emitBlockQuads(context, world, state, pos, randomSupplier, cullTest);
        } else if (hasFreezer) {
            getTemplateBakedModels().get((4+openOffset)).emitBlockQuads(context, world, state, pos, randomSupplier, cullTest);
        } else {
            getTemplateBakedModels().get((openOffset)).emitBlockQuads(context, world, state, pos, randomSupplier, cullTest);
        }
    }

    @Override
    public void emitItemQuads(QuadEmitter context, Supplier<Random> randomSupplier) {

    }

    @Override
    public Sprite pfm$getParticle(BlockState state) {
        return getParticleSprite();
    }
}
