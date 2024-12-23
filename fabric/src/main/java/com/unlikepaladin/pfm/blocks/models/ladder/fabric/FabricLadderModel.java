package com.unlikepaladin.pfm.blocks.models.ladder.fabric;

import com.unlikepaladin.pfm.blocks.SimpleBunkLadderBlock;
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
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class FabricLadderModel extends PFMFabricBakedModel {
    public FabricLadderModel(ModelBakeSettings settings, List<BakedModel> bakedModels) {
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
    public void emitBlockQuads(QuadEmitter context, BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, Predicate<@Nullable Direction> cullTest) {
        if (state != null && state.getBlock() instanceof SimpleBunkLadderBlock) {
            Sprite sprite = getSpriteList(state).get(0);
            pushTextureTransform(context, sprite);
            int offset = state.get(SimpleBunkLadderBlock.UP) ? 1 : 0;
            getTemplateBakedModels().get(offset).emitBlockQuads(context, blockView, state, pos, randomSupplier, cullTest);
            context.popTransform();
        }
    }

    @Override
    public void emitItemQuads(QuadEmitter context, Supplier<Random> randomSupplier) {
        Sprite sprite = getSpriteList(blockState).get(0);
        pushTextureTransform(context, sprite);
        getTemplateBakedModels().get(0).emitItemQuads(context, randomSupplier);
        context.popTransform();
    }
}
