package com.unlikepaladin.pfm.blocks;

import com.unlikepaladin.pfm.blocks.blockentities.GenericStorageBlockEntity3x3;
import com.unlikepaladin.pfm.data.FurnitureBlock;
import com.unlikepaladin.pfm.registry.Statistics;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class KitchenWallDrawerSmallBlock extends KitchenWallDrawerBlock {
    private static final List<FurnitureBlock> WOOD_SMALL_WALL_DRAWERS = new ArrayList<>();
    private static final List<FurnitureBlock> STONE_SMALL_WALL_DRAWERS = new ArrayList<>();
    public KitchenWallDrawerSmallBlock(Settings settings) {
        super(settings);
        setDefaultState(this.getStateManager().getDefaultState().with(FACING, Direction.NORTH).with(OPEN, false));
        if(AbstractSittableBlock.isWoodBased(this.getDefaultState()) && this.getClass().isAssignableFrom(KitchenWallDrawerSmallBlock.class)){
            WOOD_SMALL_WALL_DRAWERS.add(new FurnitureBlock(this, "kitchen_wall_small_drawer"));
        }
        else if (this.getClass().isAssignableFrom(KitchenWallDrawerSmallBlock.class)){
            STONE_SMALL_WALL_DRAWERS.add(new FurnitureBlock(this, "kitchen_wall_small_drawer"));
        }
    }

    public static Stream<FurnitureBlock> streamWoodWallSmallDrawers() {
        return WOOD_SMALL_WALL_DRAWERS.stream();
    }
    public static Stream<FurnitureBlock> streamStoneWallSmallDrawers() {
        return STONE_SMALL_WALL_DRAWERS.stream();
    }

    protected static final VoxelShape SMALL_DRAWER = VoxelShapes.union(createCuboidShape(0,6,0,16, 16, 13),createCuboidShape(9, 9, 14,10, 12, 15),createCuboidShape(6, 9, 14,7, 12, 15),createCuboidShape(1, 7, 13,15, 15, 14));
    protected static final VoxelShape SMALL_DRAWER_SOUTH = rotateShape(Direction.NORTH, Direction.SOUTH, SMALL_DRAWER);
    protected static final VoxelShape SMALL_DRAWER_EAST = rotateShape(Direction.NORTH, Direction.EAST, SMALL_DRAWER);
    protected static final VoxelShape SMALL_DRAWER_WEST = rotateShape(Direction.NORTH, Direction.WEST, SMALL_DRAWER);

    protected static final VoxelShape SMALL_DRAWER_OPEN = VoxelShapes.union(createCuboidShape(0,6,0,16, 16, 13),createCuboidShape(0, 7, 13,1, 15, 20),createCuboidShape(-1, 9, 18,0, 12, 19),createCuboidShape(15, 7, 13,16, 15, 20),createCuboidShape(16, 9, 18,17, 12, 19));
    protected static final VoxelShape SMALL_DRAWER_OPEN_SOUTH = rotateShape(Direction.NORTH, Direction.SOUTH, SMALL_DRAWER_OPEN);
    protected static final VoxelShape SMALL_DRAWER_OPEN_EAST = rotateShape(Direction.NORTH, Direction.EAST, SMALL_DRAWER_OPEN);
    protected static final VoxelShape SMALL_DRAWER_OPEN_WEST = rotateShape(Direction.NORTH, Direction.WEST, SMALL_DRAWER_OPEN);

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
        boolean open = state.get(OPEN);
        Direction direction = state.get(FACING);
        if (open) {
            return switch (direction) {
                case NORTH -> SMALL_DRAWER_OPEN;
                case SOUTH -> SMALL_DRAWER_OPEN_SOUTH;
                case WEST -> SMALL_DRAWER_OPEN_WEST;
                default -> SMALL_DRAWER_OPEN_EAST;
            };
        }
        return switch (direction) {
            case NORTH -> SMALL_DRAWER;
            case SOUTH -> SMALL_DRAWER_SOUTH;
            case WEST -> SMALL_DRAWER_WEST;
            default -> SMALL_DRAWER_EAST;
        };
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(FACING);
        stateManager.add(OPEN);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing());
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return GenericStorageBlockEntity3x3.getFactory().create(pos, state);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (world instanceof ServerWorld && blockEntity instanceof GenericStorageBlockEntity3x3) {
            player.openHandledScreen((GenericStorageBlockEntity3x3)blockEntity);
            player.incrementStat(Statistics.DRAWER_SEARCHED);
            PiglinBrain.onGuardedBlockInteracted((ServerWorld) world, player, true);
        }
        return ActionResult.CONSUME;
    }

}
