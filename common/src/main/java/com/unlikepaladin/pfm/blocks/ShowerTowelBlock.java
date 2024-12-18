package com.unlikepaladin.pfm.blocks;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.unlikepaladin.pfm.data.FurnitureBlock;
import net.minecraft.block.*;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static com.unlikepaladin.pfm.blocks.DinnerTableBlock.rotateShape;

public class ShowerTowelBlock extends HorizontalFacingBlock implements DyeableFurnitureBlock {
    private static final List<FurnitureBlock> SHOWER_TOWELS = new ArrayList<>();
    private final DyeColor color;
    public static final MapCodec<ShowerTowelBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(DyeColor.CODEC.fieldOf("color").forGetter(towel -> towel.color), createSettingsCodec()).apply(instance, ShowerTowelBlock::new));;;

    public ShowerTowelBlock(DyeColor color, Settings settings) {
        super(settings);
        this.color = color;
        if (this.getClass().isAssignableFrom(ShowerTowelBlock.class)) {
            String towelColor = color.getName();
            SHOWER_TOWELS.add(new FurnitureBlock(this, towelColor+ "_shower_towel"));
        }
    }

    public static Stream<FurnitureBlock> streamShowerTowels() {
        return SHOWER_TOWELS.stream();
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    public DyeColor getPFMColor() {
        return this.color;
    }
    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        Direction direction = state.get(FACING);
        BlockPos blockPos = pos.offset(direction.getOpposite());
        BlockState blockState = world.getBlockState(blockPos);
        return blockState.isSideSolidFullSquare(world, blockPos, direction);
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if (direction.getOpposite() == state.get(FACING) && !state.canPlaceAt(world, pos)) {
            return Blocks.AIR.getDefaultState();
        }
        return state;
    }

    private static final VoxelShape TOWEL_SOUTH = VoxelShapes.union(createCuboidShape(1, 1, 2,15, 13, 5), createCuboidShape(1, 0, 2,15, 1, 3), createCuboidShape(0, 11, 0,1, 12, 4), createCuboidShape(15, 11, 0,16, 12, 4));
    private static final VoxelShape TOWEL_NORTH = rotateShape(Direction.SOUTH, Direction.NORTH, TOWEL_SOUTH);
    private static final VoxelShape TOWEL_EAST = rotateShape(Direction.SOUTH, Direction.EAST, TOWEL_SOUTH);
    private static final VoxelShape TOWEL_WEST = rotateShape(Direction.SOUTH, Direction.WEST, TOWEL_SOUTH);
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
       Direction direction = state.get(FACING);
       switch (direction) {
           case NORTH: return TOWEL_NORTH;
           case SOUTH: return TOWEL_SOUTH;
           case EAST: return TOWEL_EAST;
           default: return  TOWEL_WEST;
       }
    }

    @Override
    public boolean canPathfindThrough(BlockState state, NavigationType type) {
        return false;
    }

    public int getFlammability(BlockState state, BlockView world, BlockPos pos, Direction face) {
        if (AbstractSittableBlock.isWoodBased(state)) {
            return 20;
        }
        return 0;
    }

    @Override
    protected MapCodec<? extends HorizontalFacingBlock> getCodec() {
        return CODEC;
    }
}
