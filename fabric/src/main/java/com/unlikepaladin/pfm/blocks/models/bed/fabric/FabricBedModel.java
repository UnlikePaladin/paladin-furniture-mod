package com.unlikepaladin.pfm.blocks.models.bed.fabric;

import com.unlikepaladin.pfm.blocks.ClassicBedBlock;
import com.unlikepaladin.pfm.blocks.SimpleBedBlock;
import com.unlikepaladin.pfm.blocks.models.ModelHelper;
import com.unlikepaladin.pfm.blocks.models.bed.BedInterface;
import com.unlikepaladin.pfm.blocks.models.fabric.PFMFabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.BedPart;
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

public class FabricBedModel extends PFMFabricBakedModel implements BedInterface {
    public FabricBedModel(ModelBakeSettings settings, List<BakedModel> modelParts) {
        super(settings, modelParts);
    }

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    @Override
    public void emitBlockQuads(QuadEmitter context, BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, Predicate<@Nullable Direction> cullTest) {
        if (state.getBlock() instanceof SimpleBedBlock) {
            Direction dir = state.get(BedBlock.FACING);
            boolean isClassic = state.getBlock().getTranslationKey().contains("classic");
            boolean left = isBed(blockView, pos, dir.rotateYCounterclockwise(), dir, state, isClassic);
            boolean right = isBed(blockView, pos, dir.rotateYClockwise(), dir, state, isClassic);
            boolean bunk = isBed(blockView, pos, Direction.DOWN, dir, state, isClassic);
            int classicOffset = isClassic ? 12 : 0;
            BedPart part = state.get(BedBlock.PART);
            List<Sprite> spriteList = getSpriteList(state);
            pushTextureTransform(context, ModelHelper.getOakBedSprites(), spriteList);
            if (part == BedPart.HEAD) {
                getTemplateBakedModels().get((classicOffset+3)).emitBlockQuads(context, blockView, state, pos, randomSupplier, cullTest);
                if (!right){
                    getTemplateBakedModels().get((classicOffset+6)).emitBlockQuads(context, blockView, state, pos, randomSupplier, cullTest);
                }
                if (!left){
                    getTemplateBakedModels().get((classicOffset+7)).emitBlockQuads(context, blockView, state, pos, randomSupplier, cullTest);
                }
                if (bunk && !(state.getBlock() instanceof ClassicBedBlock)){
                    getTemplateBakedModels().get((classicOffset+10)).emitBlockQuads(context, blockView, state, pos, randomSupplier, cullTest);
                }
            } else {
                getTemplateBakedModels().get((classicOffset+2)).emitBlockQuads(context, blockView, state, pos, randomSupplier, cullTest);
                if (!right){
                    getTemplateBakedModels().get((classicOffset+4)).emitBlockQuads(context, blockView, state, pos, randomSupplier, cullTest);
                }
                if (!left){
                    getTemplateBakedModels().get((classicOffset+5)).emitBlockQuads(context, blockView, state, pos, randomSupplier, cullTest);
                }
                if (!right && bunk){
                    getTemplateBakedModels().get((classicOffset+8)).emitBlockQuads(context, blockView, state, pos, randomSupplier, cullTest);
                }
                if (!left && bunk){
                    getTemplateBakedModels().get((classicOffset+9)).emitBlockQuads(context, blockView, state, pos, randomSupplier, cullTest);
                }
            }
            context.popTransform();
        }
    }

    @Override
    public void emitItemQuads(QuadEmitter context, Supplier<Random> randomSupplier) {
        if (blockState == null) return;

        List<Sprite> spriteList = getSpriteList(blockState);
        pushTextureTransform(context, ModelHelper.getOakBedSprites(), spriteList);
        int classicOffset = blockState.getBlock().getTranslationKey().contains("classic") ? 12 : 0;
        getTemplateBakedModels().get((classicOffset+11)).emitItemQuads(context, randomSupplier);
        context.popTransform();

    }

    @Override
    public Sprite pfm$getParticle(BlockState state) {
        return getSpriteList(state).get(0);
    }
}
