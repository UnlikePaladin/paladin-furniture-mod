package com.unlikepaladin.pfm.blocks;

import com.unlikepaladin.pfm.blocks.blockentities.FridgeBlockEntity;
import com.unlikepaladin.pfm.registry.Statistics;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
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

import java.util.function.Supplier;

import static com.unlikepaladin.pfm.blocks.KitchenDrawerBlock.rotateShape;

public class XboxFridgeBlock extends FridgeBlock
{
    public XboxFridgeBlock(Settings settings, Supplier<FreezerBlock> freezer) {
        super(settings, freezer);
    }
    public static final EnumProperty<DoubleBlockHalf> HALF = Properties.DOUBLE_BLOCK_HALF;

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(Properties.HORIZONTAL_FACING);
        stateManager.add(OPEN);
        stateManager.add(HALF);
    }
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (world instanceof ServerWorld serverWorld && blockEntity instanceof FridgeBlockEntity) {
            player.openHandledScreen((FridgeBlockEntity)blockEntity);
            player.incrementStat(Statistics.FRIDGE_OPENED);
            PiglinBrain.onGuardedBlockInteracted(serverWorld, player, true);
        }
        return ActionResult.CONSUME;
    }
    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        world.setBlockState(pos.up(), this.getDefaultState().with(FACING, placer.getHorizontalFacing()).with(HALF, DoubleBlockHalf.UPPER).with(OPEN,false), NOTIFY_ALL);
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient && player.isCreative()) {
            this.onBreakInCreative(world, pos, state, player);
        }
        return super.onBreak(world, pos, state, player);
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        DoubleBlockHalf doubleBlockHalf = state.get(HALF);
        if (direction.getAxis() == Direction.Axis.Y && doubleBlockHalf == DoubleBlockHalf.LOWER == (direction == Direction.UP)) {
            if (neighborState.isOf(this) && neighborState.get(HALF) != doubleBlockHalf) {
                return state.with(FACING, neighborState.get(FACING)).with(OPEN, neighborState.get(OPEN));
            }
        }
        return state;
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockPos blockPos = pos.down();
        BlockState blockState = world.getBlockState(blockPos);
        if (state.get(HALF) == DoubleBlockHalf.LOWER) {
            return blockState.isSideSolidFullSquare(world, blockPos, Direction.UP);
        }
        return blockState.isOf(this);
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockPos blockPos = ctx.getBlockPos();
        World world = ctx.getWorld();
        if (blockPos.getY() < world.getTopYInclusive() - 1 && world.getBlockState(blockPos.up()).canReplace(ctx)) {
            return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing()).with(OPEN, false).with(HALF, DoubleBlockHalf.LOWER);
        }
        return null;
    }

    @Override
    public void onBroken(WorldAccess world, BlockPos pos, BlockState state) {
        BlockPos blockPos;
        BlockState blockState = world.getBlockState(blockPos = pos.down());
        if (blockState.isOf(state.getBlock())) {
            BlockState blockState2 = blockState.contains(Properties.WATERLOGGED) && blockState.get(Properties.WATERLOGGED) ? Blocks.WATER.getDefaultState() : Blocks.AIR.getDefaultState();
            world.setBlockState(blockPos, blockState2, NOTIFY_ALL | SKIP_DROPS);
        }
        blockState = world.getBlockState(blockPos = pos.up());
        if (blockState.isOf(state.getBlock())) {
            BlockState blockState2 = blockState.contains(Properties.WATERLOGGED) && blockState.get(Properties.WATERLOGGED) ? Blocks.WATER.getDefaultState() : Blocks.AIR.getDefaultState();
            world.setBlockState(blockPos, blockState2, NOTIFY_ALL | SKIP_DROPS);
        }
        super.onBroken(world, pos, state);
    }

    protected static final VoxelShape XBOX_FRIDGE = VoxelShapes.union(createCuboidShape(0.5, 1, 3,15.5, 32, 16),createCuboidShape(1, 0, 2.84,15, 1, 15.84),createCuboidShape(0.51, 1, 1.91,15.31, 16, 2.91));
    protected static final VoxelShape XBOX_FRIDGE_UPPER = VoxelShapes.union(createCuboidShape(0.5, -15, 3,15.5, 16, 16),createCuboidShape(1, -16, 2.84,15, -15, 15.84),createCuboidShape(0.51, 0, 1.91,15.31, 16, 2.91));
    protected static final VoxelShape XBOX_FRIDGE_OPEN = VoxelShapes.union(createCuboidShape(0.5, 1, 3,15.5, 32, 16),createCuboidShape(1, 0, 2.84,15, 1, 15.84),createCuboidShape(0.5, 16, -11.69,1.5, 32, 3.11));
    protected static final VoxelShape XBOX_FRIDGE_UPPER_OPEN = VoxelShapes.union(createCuboidShape(0.5, -15, 3,15.5, 16, 16),createCuboidShape(1, -16, 2.84,15, -15, 15.84),createCuboidShape(0.5, 0, -11.69,1.5, 16, 3.11));

    protected static final VoxelShape XBOX_FRIDGE_SOUTH = rotateShape(Direction.NORTH, Direction.SOUTH, XBOX_FRIDGE);
    protected static final VoxelShape XBOX_FRIDGE_UPPER_SOUTH = rotateShape(Direction.NORTH, Direction.SOUTH, XBOX_FRIDGE_UPPER);
    protected static final VoxelShape XBOX_FRIDGE_OPEN_SOUTH = rotateShape(Direction.NORTH, Direction.SOUTH, XBOX_FRIDGE_OPEN);
    protected static final VoxelShape XBOX_FRIDGE_UPPER_OPEN_SOUTH = rotateShape(Direction.NORTH, Direction.SOUTH, XBOX_FRIDGE_UPPER_OPEN);
    protected static final VoxelShape XBOX_FRIDGE_EAST = rotateShape(Direction.NORTH, Direction.EAST, XBOX_FRIDGE);
    protected static final VoxelShape XBOX_FRIDGE_UPPER_EAST = rotateShape(Direction.NORTH, Direction.EAST, XBOX_FRIDGE_UPPER);
    protected static final VoxelShape XBOX_FRIDGE_OPEN_EAST = rotateShape(Direction.NORTH, Direction.EAST, XBOX_FRIDGE_OPEN);
    protected static final VoxelShape XBOX_FRIDGE_UPPER_OPEN_EAST = rotateShape(Direction.NORTH, Direction.EAST, XBOX_FRIDGE_UPPER_OPEN);
    protected static final VoxelShape XBOX_FRIDGE_WEST = rotateShape(Direction.NORTH, Direction.WEST, XBOX_FRIDGE);
    protected static final VoxelShape XBOX_FRIDGE_UPPER_WEST = rotateShape(Direction.NORTH, Direction.WEST, XBOX_FRIDGE_UPPER);
    protected static final VoxelShape XBOX_FRIDGE_OPEN_WEST = rotateShape(Direction.NORTH, Direction.WEST, XBOX_FRIDGE_OPEN);
    protected static final VoxelShape XBOX_FRIDGE_UPPER_OPEN_WEST = rotateShape(Direction.NORTH, Direction.WEST, XBOX_FRIDGE_UPPER_OPEN);

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Direction dir = state.get(FACING);
        Boolean open = state.get(OPEN);
        Enum<DoubleBlockHalf> half = state.get(HALF);

        switch (dir) {
            case NORTH:
                if (half == DoubleBlockHalf.UPPER){
                    if (open) {
                    return XBOX_FRIDGE_UPPER_OPEN_SOUTH;}
                    else {
                        return XBOX_FRIDGE_UPPER_SOUTH;}
                    }
                else {
                    if (open)
                    return XBOX_FRIDGE_OPEN_SOUTH;
                    else
                        return XBOX_FRIDGE_SOUTH;
                }

            case SOUTH:
                if (half == DoubleBlockHalf.UPPER){
                    if (open) {
                        return XBOX_FRIDGE_UPPER_OPEN;}
                    else {
                        return XBOX_FRIDGE_UPPER;}
                }
                else {
                    if (open)
                        return XBOX_FRIDGE_OPEN;
                    else
                        return XBOX_FRIDGE;
                }

            case EAST:
                if (half == DoubleBlockHalf.UPPER){
                    if (open) {
                        return XBOX_FRIDGE_UPPER_OPEN_WEST;}
                    else {
                        return XBOX_FRIDGE_UPPER_WEST;}
                }
                else {
                    if (open)
                        return XBOX_FRIDGE_OPEN_WEST;
                    else
                        return XBOX_FRIDGE_WEST;
                }
            default:
                if (half == DoubleBlockHalf.UPPER){
                    if (open) {
                        return XBOX_FRIDGE_UPPER_OPEN_EAST;}
                    else {
                        return XBOX_FRIDGE_UPPER_EAST;}
                }
                else {
                    if (open)
                        return XBOX_FRIDGE_OPEN_EAST;
                    else
                        return XBOX_FRIDGE_EAST;
            }
        }
    }
}
