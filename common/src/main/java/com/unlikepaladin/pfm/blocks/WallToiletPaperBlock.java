package com.unlikepaladin.pfm.blocks;

import com.mojang.serialization.MapCodec;
import com.unlikepaladin.pfm.data.FurnitureBlock;
import net.minecraft.block.*;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static com.unlikepaladin.pfm.blocks.SimpleStoolBlock.rotateShape;

public class WallToiletPaperBlock extends HorizontalFacingBlock {
    protected static final BooleanProperty WALL = BooleanProperty.of("wall");
    private static final List<FurnitureBlock> TOILET_PAPER = new ArrayList<>();
    public static final MapCodec<WallToiletPaperBlock> CODEC = createCodec(WallToiletPaperBlock::new);
    public WallToiletPaperBlock(Settings settings) {
        super(settings);
        this.setDefaultState(getDefaultState().with(FACING, Direction.NORTH).with(WALL, false));
        TOILET_PAPER.add(new FurnitureBlock(this, "toilet_paper"));

    }

    @Override
    protected MapCodec<? extends HorizontalFacingBlock> getCodec() {
        return CODEC;
    }

    public static Stream<FurnitureBlock> streamToiletPaperBlocks() {
        return TOILET_PAPER.stream();
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(WALL, FACING);
        super.appendProperties(builder);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing());
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        Direction direction = state.get(FACING);
        BlockPos blockPos = pos.offset(direction);
        BlockState blockState = world.getBlockState(blockPos);
        return blockState.isSideSolidFullSquare(world, blockPos, direction);
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if (direction == state.get(FACING) && !state.canPlaceAt(world, pos)) {
            return Blocks.AIR.getDefaultState();
        }
        return state;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (player.isSneaking()) {
            world.setBlockState(pos, state.cycle(WALL));
            return ActionResult.SUCCESS;
        }
        return super.onUse(state, world, pos, player, hit);
    }

    public static final VoxelShape WALL_PAPER_SOUTH = VoxelShapes.union(createCuboidShape(11.5, 12, 12,12.5, 13, 16), createCuboidShape(3.5, 12, 11.5,4.5, 13, 16), createCuboidShape(4.5, 10.5, 10.5,11.5, 14.5, 14.5));
    public static final VoxelShape WALL_PAPER_NORTH = rotateShape(Direction.SOUTH, Direction.NORTH, WALL_PAPER_SOUTH);
    public static final VoxelShape WALL_PAPER_EAST = rotateShape(Direction.SOUTH, Direction.EAST, WALL_PAPER_SOUTH);
    public static final VoxelShape WALL_PAPER_WEST = rotateShape(Direction.SOUTH, Direction.WEST, WALL_PAPER_SOUTH);

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Direction direction = state.get(FACING);
        switch (direction) {
            case EAST: {
                return WALL_PAPER_EAST;
            }
            case WEST: {
                return WALL_PAPER_WEST;
            }
            case SOUTH: {
                return WALL_PAPER_SOUTH;
            }
            default:
            case NORTH: {
                return WALL_PAPER_NORTH;
            }
        }
    }

    @Override
    public boolean canPathfindThrough(BlockState state, NavigationType type) {
        return false;
    }
}
