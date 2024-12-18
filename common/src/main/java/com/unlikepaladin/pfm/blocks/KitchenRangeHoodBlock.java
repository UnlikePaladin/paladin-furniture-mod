package com.unlikepaladin.pfm.blocks;

import com.mojang.serialization.MapCodec;
import com.unlikepaladin.pfm.data.FurnitureBlock;
import net.minecraft.block.*;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
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

import static com.unlikepaladin.pfm.blocks.KitchenDrawerBlock.rotateShape;

public class KitchenRangeHoodBlock extends HorizontalFacingBlock {
    public static final BooleanProperty DOWN = Properties.DOWN;
    public static final BooleanProperty DRAWER = BooleanProperty.of("drawer");
    private static final List<FurnitureBlock> OVEN_RANGE_HOOD = new ArrayList<>();
    public static final MapCodec<KitchenRangeHoodBlock> CODEC = createCodec(KitchenRangeHoodBlock::new);

    public KitchenRangeHoodBlock(Settings settings) {
        super(settings);
        setDefaultState(this.getStateManager().getDefaultState().with(FACING, Direction.NORTH).with(DOWN, false).with(DRAWER, false));
        OVEN_RANGE_HOOD.add(new FurnitureBlock(this, "oven_range_hood"));
    }

    @Override
    protected MapCodec<? extends HorizontalFacingBlock> getCodec() {
        return CODEC;
    }

    public static Stream<FurnitureBlock> streamOvenRangeHoods() {
        return OVEN_RANGE_HOOD.stream();
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(FACING);
        builder.add(DOWN);
        builder.add(DRAWER);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        boolean down = ctx.getWorld().getBlockState(ctx.getBlockPos().down()).getBlock() instanceof KitchenRangeHoodBlock;
        boolean drawer = ctx.getWorld().getBlockState(ctx.getBlockPos().up()).getBlock() instanceof KitchenWallDrawerSmallBlock;
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing()).with(DOWN, down).with(DRAWER, drawer);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return super.getFluidState(state);
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if (direction.getAxis().isVertical()) {
            boolean down = world.getBlockState(pos.down()).getBlock() instanceof KitchenRangeHoodBlock;
            boolean drawer = world.getBlockState(pos.up()).getBlock() instanceof KitchenWallDrawerSmallBlock;
            return state.with(DOWN, down).with(DRAWER, drawer);
        }
        return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    protected static final VoxelShape RANGE_HOOD = VoxelShapes.union(createCuboidShape(2, 4, 0,14, 16, 7),createCuboidShape(0, 0, 0,16, 4, 15));
    protected static final VoxelShape RANGE_HOOD_SOUTH = rotateShape(Direction.NORTH, Direction.SOUTH, RANGE_HOOD);
    protected static final VoxelShape RANGE_HOOD_EAST = rotateShape(Direction.NORTH, Direction.EAST, RANGE_HOOD);
    protected static final VoxelShape RANGE_HOOD_WEST = rotateShape(Direction.NORTH, Direction.WEST, RANGE_HOOD);

    protected static final VoxelShape RANGE_HOOD_BOTTOM_DRAWER = VoxelShapes.union(createCuboidShape(2, 0, 0,14, 28, 7));
    protected static final VoxelShape RANGE_HOOD_BOTTOM_DRAWER_SOUTH = rotateShape(Direction.NORTH, Direction.SOUTH, RANGE_HOOD_BOTTOM_DRAWER);
    protected static final VoxelShape RANGE_HOOD_BOTTOM_DRAWER_EAST = rotateShape(Direction.NORTH, Direction.EAST, RANGE_HOOD_BOTTOM_DRAWER);
    protected static final VoxelShape RANGE_HOOD_BOTTOM_DRAWER_WEST = rotateShape(Direction.NORTH, Direction.WEST, RANGE_HOOD_BOTTOM_DRAWER);

    protected static final VoxelShape RANGE_HOOD_BOTTOM = VoxelShapes.union(createCuboidShape(2, 0, 0,14, 16, 7));
    protected static final VoxelShape RANGE_HOOD_BOTTOM_SOUTH = rotateShape(Direction.NORTH, Direction.SOUTH, RANGE_HOOD_BOTTOM);
    protected static final VoxelShape RANGE_HOOD_BOTTOM_EAST = rotateShape(Direction.NORTH, Direction.EAST, RANGE_HOOD_BOTTOM);
    protected static final VoxelShape RANGE_HOOD_BOTTOM_WEST = rotateShape(Direction.NORTH, Direction.WEST, RANGE_HOOD_BOTTOM);

    protected static final VoxelShape RANGE_HOOD_DRAWER = VoxelShapes.union(createCuboidShape(2, 16, 0,14, 28, 7),createCuboidShape(0, 12, 0,16, 16, 15));
    protected static final VoxelShape RANGE_HOOD_DRAWER_SOUTH = rotateShape(Direction.NORTH, Direction.SOUTH, RANGE_HOOD_DRAWER);
    protected static final VoxelShape RANGE_HOOD_DRAWER_EAST = rotateShape(Direction.NORTH, Direction.EAST, RANGE_HOOD_DRAWER);
    protected static final VoxelShape RANGE_HOOD_DRAWER_WEST = rotateShape(Direction.NORTH, Direction.WEST, RANGE_HOOD_DRAWER);
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        boolean down = state.get(DOWN);
        boolean drawer = state.get(DRAWER);
        Direction direction = state.get(FACING);
        if (down && drawer) {
            return switch (direction) {
                case NORTH -> RANGE_HOOD_BOTTOM_DRAWER;
                case SOUTH -> RANGE_HOOD_BOTTOM_DRAWER_SOUTH;
                case EAST -> RANGE_HOOD_BOTTOM_DRAWER_EAST;
                default -> RANGE_HOOD_BOTTOM_DRAWER_WEST;
            };
        } else if (down) {
            return switch (direction) {
                case NORTH -> RANGE_HOOD_BOTTOM;
                case SOUTH -> RANGE_HOOD_BOTTOM_SOUTH;
                case EAST -> RANGE_HOOD_BOTTOM_EAST;
                default -> RANGE_HOOD_BOTTOM_WEST;
            };
        }
        else if (drawer) {
            return switch (direction) {
                case NORTH -> RANGE_HOOD_DRAWER;
                case SOUTH -> RANGE_HOOD_DRAWER_SOUTH;
                case EAST -> RANGE_HOOD_DRAWER_EAST;
                default -> RANGE_HOOD_DRAWER_WEST;
            };
        }
        return switch (direction) {
            case NORTH -> RANGE_HOOD;
            case SOUTH -> RANGE_HOOD_SOUTH;
            case EAST -> RANGE_HOOD_EAST;
            default -> RANGE_HOOD_WEST;
        };
    }

    @Override
    public boolean canPathfindThrough(BlockState state, NavigationType type) {
        return false;
    }
}
