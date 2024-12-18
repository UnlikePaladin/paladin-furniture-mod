package com.unlikepaladin.pfm.blocks;

import com.unlikepaladin.pfm.data.FurnitureBlock;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LadderBlock;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class SimpleBunkLadderBlock extends LadderBlock {
    public static final BooleanProperty UP = Properties.UP;
    private static final List<FurnitureBlock> SIMPLE_BUNK_LADDER = new ArrayList<>();
    public SimpleBunkLadderBlock(Settings settings) {
        super(settings);
        this.setDefaultState(((this.stateManager.getDefaultState()).with(FACING, Direction.NORTH)).with(WATERLOGGED, false).with(UP, true));
        SIMPLE_BUNK_LADDER.add(new FurnitureBlock(this, "simple_bunk_ladder"));
    }

    public static Stream<FurnitureBlock> streamSimpleBunkLadder() {
        return SIMPLE_BUNK_LADDER.stream();
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState blockState;
        if (!ctx.canReplaceExisting() && (blockState = ctx.getWorld().getBlockState(ctx.getBlockPos().offset(ctx.getSide().getOpposite()))).isOf(this) && blockState.get(FACING) == ctx.getSide()) {
            return null;
        }
        blockState = this.getDefaultState();
        World worldView = ctx.getWorld();
        BlockPos blockPos = ctx.getBlockPos();
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        boolean up = ctx.getWorld().getBlockState(blockPos.up()).getBlock() instanceof SimpleBunkLadderBlock;
        for (Direction direction : ctx.getPlacementDirections()) {
            if (!direction.getAxis().isHorizontal() || !(blockState = blockState.with(FACING, direction.getOpposite())).canPlaceAt(worldView, blockPos)) continue;
            return blockState.with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER).with(UP, up);
        }
        return null;
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if (direction.getAxis().isVertical() && canPlaceAt(state, world, pos)) {
            boolean up = world.getBlockState(pos.up()).getBlock() instanceof SimpleBunkLadderBlock;
            return state.with(UP, up);
        }
        return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    private boolean canPlaceOn(BlockView world, BlockPos pos, Direction side) {
        BlockState blockState = world.getBlockState(pos);
        return blockState.isSideSolidFullSquare(world, pos, side) || blockState.getBlock() instanceof BedBlock;
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        Direction direction = state.get(FACING);
        return this.canPlaceOn(world, pos.offset(direction.getOpposite()), direction);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED, UP);
    }


}
