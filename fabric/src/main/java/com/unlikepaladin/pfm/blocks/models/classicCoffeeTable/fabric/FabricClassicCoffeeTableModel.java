package com.unlikepaladin.pfm.blocks.models.classicCoffeeTable.fabric;

import com.unlikepaladin.pfm.blocks.ClassicCoffeeTableBlock;
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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class FabricClassicCoffeeTableModel extends PFMFabricBakedModel {
    public FabricClassicCoffeeTableModel(ModelBakeSettings settings, List<BakedModel> modelParts) {
        super(settings, modelParts);
    }
    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    @Override
    public void emitBlockQuads(QuadEmitter context, BlockRenderView world, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, Predicate<@Nullable Direction> cullTest) {
        if (state.getBlock() instanceof ClassicCoffeeTableBlock) {
            ClassicCoffeeTableBlock block = (ClassicCoffeeTableBlock) state.getBlock();
            boolean north = block.canConnect(world.getBlockState(pos.north()));
            boolean east = block.canConnect(world.getBlockState(pos.east()));
            boolean west = block.canConnect(world.getBlockState(pos.west()));
            boolean south = block.canConnect(world.getBlockState(pos.south()));
            pushTextureTransform(context, getSpriteList(state).get(0));
            getTemplateBakedModels().get(0).emitBlockQuads(context, world, state, pos, randomSupplier, cullTest);
            context.popTransform();

            pushTextureTransform(context, getSpriteList(state).get(1));
            if (!north && !east) {
                getTemplateBakedModels().get(1).emitBlockQuads(context, world, state, pos, randomSupplier, cullTest);
            }
            if (!north && !west) {
                getTemplateBakedModels().get(2).emitBlockQuads(context, world, state, pos, randomSupplier, cullTest);
            }
            if (!south && !west) {
                getTemplateBakedModels().get(3).emitBlockQuads(context, world, state, pos, randomSupplier, cullTest);
            }
            if (!south && !east) {
                getTemplateBakedModels().get(4).emitBlockQuads(context, world, state, pos, randomSupplier, cullTest);
            }
            context.popTransform();
        }
    }

    @Override
    public void emitItemQuads(QuadEmitter context, Supplier<Random> randomSupplier) {
        pushTextureTransform(context, getSpriteList(blockState).get(0));
        getTemplateBakedModels().get(0).emitItemQuads(context, randomSupplier);
        context.popTransform();

        pushTextureTransform(context, getSpriteList(blockState).get(1));
        // legs
        getTemplateBakedModels().get(1).emitItemQuads(context, randomSupplier);
        getTemplateBakedModels().get(2).emitItemQuads(context, randomSupplier);
        getTemplateBakedModels().get(3).emitItemQuads(context, randomSupplier);
        getTemplateBakedModels().get(4).emitItemQuads(context, randomSupplier);
        context.popTransform();
    }

    @Override
    public Sprite pfm$getParticle(BlockState state) {
        return getSpriteList(state).get(0);
    }
}