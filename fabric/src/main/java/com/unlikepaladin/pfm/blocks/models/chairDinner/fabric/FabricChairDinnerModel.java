package com.unlikepaladin.pfm.blocks.models.chairDinner.fabric;

import com.unlikepaladin.pfm.blocks.DinnerChairBlock;
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

public class FabricChairDinnerModel extends PFMFabricBakedModel {
    public FabricChairDinnerModel(ModelBakeSettings settings, List<BakedModel> bakedModels) {
        super(settings, bakedModels);
    }

    @Override
    public Sprite pfm$getParticle(BlockState state) {
        return getSpriteList(state).get(0);
    }

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    @Override
    public void emitBlockQuads(QuadEmitter emitter, BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, Predicate<@Nullable Direction> cullTest) {

        if (state.getBlock() instanceof DinnerChairBlock) {
            List<Sprite> spriteList = getSpriteList(state);
            pushTextureTransform(emitter, ModelHelper.getOakPlankLogSprites(), spriteList);
            int tucked = state.get(DinnerChairBlock.TUCKED) ? 1 : 0;

            getTemplateBakedModels().get(tucked).emitBlockQuads(emitter, blockView, state, pos, randomSupplier, cullTest);
            emitter.popTransform();
        }
    }

    @Override
    public void emitItemQuads(QuadEmitter context, Supplier<Random> randomSupplier) {
        if (blockState == null) return;

        List<Sprite> spriteList = getSpriteList(blockState);
        pushTextureTransform(context, ModelHelper.getOakPlankLogSprites(), spriteList);
        getTemplateBakedModels().get(0).emitItemQuads(context, randomSupplier);
        context.popTransform();
    }
}
