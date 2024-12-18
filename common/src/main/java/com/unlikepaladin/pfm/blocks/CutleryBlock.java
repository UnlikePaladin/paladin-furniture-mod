package com.unlikepaladin.pfm.blocks;

import com.mojang.serialization.MapCodec;
import com.unlikepaladin.pfm.data.FurnitureBlock;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.state.StateManager;
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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static com.unlikepaladin.pfm.blocks.ClassicStoolBlock.rotateShape;

public class CutleryBlock extends HorizontalFacingBlock {
    private static final List<FurnitureBlock> CUTLERY = new ArrayList<>();
    public static final MapCodec<CutleryBlock> CODEC = createCodec(CutleryBlock::new);

    public CutleryBlock(Settings settings) {
        super(settings);
        setDefaultState(this.getStateManager().getDefaultState().with(FACING, Direction.NORTH));
        CUTLERY.add(new FurnitureBlock(this, "cutlery"));
    }

    @Override
    protected MapCodec<? extends HorizontalFacingBlock> getCodec() {
        return CODEC;
    }

    public static Stream<FurnitureBlock> streamCutlery() {
        return CUTLERY.stream();
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState()
                .with(FACING, ctx.getHorizontalPlayerFacing());
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if (!state.canPlaceAt(world, pos)) {
            return Blocks.AIR.getDefaultState();
        }
        return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    private static final VoxelShape FACING_NORTH = VoxelShapes.union(createCuboidShape(3, 0, 0,11, 0.5, 15.5));
    private static final VoxelShape FACING_SOUTH = rotateShape(Direction.NORTH, Direction.SOUTH, FACING_NORTH);
    private static final VoxelShape FACING_EAST = rotateShape(Direction.NORTH, Direction.EAST, FACING_NORTH);
    private static final VoxelShape FACING_WEST = rotateShape(Direction.NORTH, Direction.WEST, FACING_NORTH);

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Direction dir = state.get(FACING);
        switch (dir) {
            case WEST: {
                return FACING_SOUTH;
            }
            case NORTH: {
                return FACING_WEST;
            }
            case SOUTH: {
                return FACING_EAST;
            }
            default: {
                return FACING_NORTH;
            }
        }
    }

    @Override
    protected ActionResult onUseWithItem(ItemStack itemStack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        Block block = (Registries.BLOCK.get(Registries.ITEM.getId(itemStack.getItem())));
        if(block instanceof PlateBlock) {
            BlockState newState = block.getDefaultState();
            world.setBlockState(pos, newState.with(PlateBlock.CUTLERY, true).with(FACING, state.get(FACING)));
            itemStack.decrement(1);
            return ActionResult.SUCCESS;
        }
        return super.onUseWithItem(itemStack, state, world, pos, player, hand, hit);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        Direction direction = Direction.DOWN;
        return Block.sideCoversSmallSquare(world, pos.offset(direction), direction.getOpposite());
    }
}
