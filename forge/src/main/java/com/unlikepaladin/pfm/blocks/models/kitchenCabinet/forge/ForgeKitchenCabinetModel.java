package com.unlikepaladin.pfm.blocks.models.kitchenCabinet.forge;

import com.unlikepaladin.pfm.blocks.KitchenCabinetBlock;
import com.unlikepaladin.pfm.blocks.KitchenWallDrawerBlock;
import com.unlikepaladin.pfm.blocks.models.ModelHelper;
import com.unlikepaladin.pfm.blocks.models.forge.ModelBitSetProperty;
import com.unlikepaladin.pfm.blocks.models.forge.PFMForgeBakedModel;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.texture.Sprite;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import net.minecraft.util.math.random.Random;

public class ForgeKitchenCabinetModel extends PFMForgeBakedModel {
    public ForgeKitchenCabinetModel(ModelBakeSettings settings, List<BakedModel> modelParts) {
        super(settings, modelParts);
    }

    public static ModelProperty<ModelBitSetProperty> CONNECTIONS = new ModelProperty<>();
    public static ModelProperty<BlockState> NEIGHBOR_OPPOSITE = new ModelProperty<>();
    public static ModelProperty<BlockState> NEIGHBOR_FACING = new ModelProperty<>();

    @NotNull
    @Override
    public ModelData getModelData(@NotNull BlockRenderView world, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ModelData tileData) {
        if (state.getBlock() instanceof KitchenCabinetBlock) {
            ModelData.Builder builder = ModelData.builder();

            ModelData data = builder.build();
            data = super.getModelData(world, pos, state, data);

            KitchenCabinetBlock block = (KitchenCabinetBlock) state.getBlock();
            Direction direction = state.get(KitchenCabinetBlock.FACING);
            BlockState neighborStateOpposite = world.getBlockState(pos.offset(direction.getOpposite()));
            Direction direction3;
            Direction direction2;
            boolean innerCorner = block.isCabinet(neighborStateOpposite) && (direction3 = neighborStateOpposite.get(KitchenCabinetBlock.FACING)).getAxis() != state.get(KitchenCabinetBlock.FACING).getAxis() && block.isDifferentOrientation(state, world, pos, direction3);
            BlockState blockState = world.getBlockState(pos.offset(direction));
            boolean isNeighborStateOppositeFacingDifferentDirection;
            if (blockState.contains(Properties.HORIZONTAL_FACING)) {
                direction2 = blockState.get(Properties.HORIZONTAL_FACING);
                isNeighborStateOppositeFacingDifferentDirection = block.isDifferentOrientation(state, world, pos, direction2.getOpposite());
            } else {
                isNeighborStateOppositeFacingDifferentDirection = false;
            }
            BitSet set = new BitSet();
            set.set(0, innerCorner);
            set.set(1, isNeighborStateOppositeFacingDifferentDirection);
            data = data.derive().with(CONNECTIONS, new ModelBitSetProperty(set)).with(NEIGHBOR_OPPOSITE, neighborStateOpposite).with(NEIGHBOR_FACING, blockState).build();
            return data;
        }
        return tileData;
    }

    @NotNull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull Random rand, @NotNull ModelData extraData, RenderLayer renderLayer) {
        if (state != null && state.getBlock() instanceof KitchenCabinetBlock && extraData.get(CONNECTIONS) != null && extraData.get(CONNECTIONS).connections != null) {
            BitSet set = extraData.get(CONNECTIONS).connections;
            KitchenCabinetBlock block = (KitchenCabinetBlock) state.getBlock();
            Direction direction = state.get(KitchenCabinetBlock.FACING);
            BlockState neighborStateOpposite = extraData.get(NEIGHBOR_OPPOSITE);
            List<Sprite> spriteList = getSpriteList(state);

            Direction direction3 = null;
            if (neighborStateOpposite.contains(Properties.HORIZONTAL_FACING)) {
                direction3 = neighborStateOpposite.get(Properties.HORIZONTAL_FACING);
            }
            Direction direction2;
            BlockState blockState = extraData.get(NEIGHBOR_FACING);
            int openOffset = state.get(KitchenWallDrawerBlock.OPEN) ? 5 : 0;
            boolean innerCorner = set.get(0);
            boolean isNeighborStateOppositeFacingDifferentDirection = set.get(1);
            if (block.isCabinet(blockState) && (direction2 = blockState.get(KitchenCabinetBlock.FACING)).getAxis() != state.get(KitchenCabinetBlock.FACING).getAxis() && isNeighborStateOppositeFacingDifferentDirection) {
                if (direction2 == direction.rotateYCounterclockwise()) {
                    return getQuadsWithTexture(getTemplateBakedModels().get(3 + openOffset).getQuads(state, side, rand, extraData, renderLayer), ModelHelper.getOakPlankLogSprites(), spriteList);
                }
                else {
                    return getQuadsWithTexture(getTemplateBakedModels().get(4 + openOffset).getQuads(state, side, rand, extraData, renderLayer), ModelHelper.getOakPlankLogSprites(), spriteList);
                }
            }
            else if (innerCorner) {
                if (direction3 == direction.rotateYCounterclockwise()) {
                    return getQuadsWithTexture(getTemplateBakedModels().get(2 + openOffset).getQuads(state, side, rand, extraData, renderLayer), ModelHelper.getOakPlankLogSprites(), spriteList);
                } else {
                    return getQuadsWithTexture(getTemplateBakedModels().get(1 + openOffset).getQuads(state, side, rand, extraData, renderLayer), ModelHelper.getOakPlankLogSprites(), spriteList);
                }
            } else {
                return getQuadsWithTexture(getTemplateBakedModels().get(openOffset).getQuads(state, side, rand, extraData, renderLayer), ModelHelper.getOakPlankLogSprites(), spriteList);
            }
        }
        return Collections.emptyList();
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable Direction face, Random random) {
        List<Sprite> spriteList = getSpriteList(blockState);
        return getQuadsWithTexture(getTemplateBakedModels().get(0).getQuads(null, face, random), ModelHelper.getOakPlankLogSprites(), spriteList);
    }
}