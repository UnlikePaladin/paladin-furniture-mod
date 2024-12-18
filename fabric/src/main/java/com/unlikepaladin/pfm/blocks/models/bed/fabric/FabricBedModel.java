package com.unlikepaladin.pfm.blocks.models.bed.fabric;

import com.unlikepaladin.pfm.blocks.ClassicBedBlock;
import com.unlikepaladin.pfm.blocks.SimpleBedBlock;
import com.unlikepaladin.pfm.blocks.models.AbstractBakedModel;
import com.unlikepaladin.pfm.blocks.models.ModelHelper;
import com.unlikepaladin.pfm.blocks.models.bed.BedInterface;
import com.unlikepaladin.pfm.blocks.models.fabric.PFMFabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.BedPart;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.texture.Sprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;

import java.util.List;
import java.util.Map;
import net.minecraft.util.math.random.Random;
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
    public void emitBlockQuads(BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context) {
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
                ((FabricBakedModel) getTemplateBakedModels().get((classicOffset+3))).emitBlockQuads(blockView, state, pos, randomSupplier, context);
                if (!right){
                    ((FabricBakedModel) getTemplateBakedModels().get((classicOffset+6))).emitBlockQuads(blockView, state, pos, randomSupplier, context);
                }
                if (!left){
                    ((FabricBakedModel) getTemplateBakedModels().get((classicOffset+7))).emitBlockQuads(blockView, state, pos, randomSupplier, context);
                }
                if (bunk && !(state.getBlock() instanceof ClassicBedBlock)){
                    ((FabricBakedModel) getTemplateBakedModels().get((classicOffset+10))).emitBlockQuads(blockView, state, pos, randomSupplier, context);
                }
            } else {
                ((FabricBakedModel) getTemplateBakedModels().get((classicOffset+2))).emitBlockQuads(blockView, state, pos, randomSupplier, context);
                if (!right){
                    ((FabricBakedModel) getTemplateBakedModels().get((classicOffset+4))).emitBlockQuads(blockView, state, pos, randomSupplier, context);
                }
                if (!left){
                    ((FabricBakedModel) getTemplateBakedModels().get((classicOffset+5))).emitBlockQuads(blockView, state, pos, randomSupplier, context);
                }
                if (!right && bunk){
                    ((FabricBakedModel) getTemplateBakedModels().get((classicOffset+8))).emitBlockQuads(blockView, state, pos, randomSupplier, context);
                }
                if (!left && bunk){
                    ((FabricBakedModel) getTemplateBakedModels().get((classicOffset+9))).emitBlockQuads(blockView, state, pos, randomSupplier, context);
                }
            }
            context.popTransform();
        }
    }


    @Override
    public void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context) {
        List<Sprite> spriteList = getSpriteList(stack);
        pushTextureTransform(context, ModelHelper.getOakBedSprites(), spriteList);
        int classicOffset = stack.getItem().getTranslationKey().contains("classic") ? 12 : 0;
        ((FabricBakedModel) getTemplateBakedModels().get((classicOffset+11))).emitItemQuads(stack, randomSupplier, context);
        context.popTransform();

    }

    @Override
    public Sprite pfm$getParticle(BlockState state) {
        return getSpriteList(state).get(0);
    }
}
