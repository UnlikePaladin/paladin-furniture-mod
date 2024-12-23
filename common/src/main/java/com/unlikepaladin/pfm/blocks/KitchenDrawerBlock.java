package com.unlikepaladin.pfm.blocks;

import com.unlikepaladin.pfm.blocks.blockentities.GenericStorageBlockEntity9x3;
import com.unlikepaladin.pfm.data.FurnitureBlock;
import com.unlikepaladin.pfm.registry.Statistics;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;


public class KitchenDrawerBlock extends KitchenCounterBlock implements BlockEntityProvider {
    private float height = 0.36f;
    private final Block baseBlock;
    public static final BooleanProperty OPEN = Properties.OPEN;

    private final BlockState baseBlockState;
    private static final List<FurnitureBlock> WOOD_DRAWERS = new ArrayList<>();
    private static final List<FurnitureBlock> STONE_DRAWERS = new ArrayList<>();
    public KitchenDrawerBlock(Settings settings) {
        super(settings);
        this.baseBlockState = this.getDefaultState();
        this.baseBlock = baseBlockState.getBlock();
        if (!(this.baseBlock instanceof KitchenWallDrawerSmallBlock)) {
            setDefaultState(this.getStateManager().getDefaultState().with(Properties.HORIZONTAL_FACING, Direction.NORTH).with(OPEN, false));
        }
        counterFurnitureBlock = new FurnitureBlock(this, "kitchen_drawer");
        if(AbstractSittableBlock.isWoodBased(this.getDefaultState()) && this.getClass().isAssignableFrom(KitchenDrawerBlock.class)){
            WOOD_DRAWERS.add(counterFurnitureBlock);
        }
        else if (this.getClass().isAssignableFrom(KitchenDrawerBlock.class)){
            STONE_DRAWERS.add(counterFurnitureBlock);
        }
    }

    public static Stream<FurnitureBlock> streamWoodDrawers() {
        return WOOD_DRAWERS.stream();
    }
    public static Stream<FurnitureBlock> streamStoneDrawers() {
        return STONE_DRAWERS.stream();
    }
    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.isOf(newState.getBlock())) {
            return;
        }
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof Inventory) {
            ItemScatterer.spawn(world, pos, (Inventory) blockEntity);
            world.updateComparators(pos, this);
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    public boolean isShapeFullCube(BlockState state, BlockView world, BlockPos pos) {
        return false;
    }


    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(Properties.HORIZONTAL_FACING);
        stateManager.add(OPEN);
    }
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (world instanceof ServerWorld serverWorld && blockEntity instanceof GenericStorageBlockEntity9x3) {
            player.openHandledScreen((GenericStorageBlockEntity9x3)blockEntity);
            player.incrementStat(Statistics.DRAWER_SEARCHED);
            PiglinBrain.onGuardedBlockInteracted(serverWorld, player, true);
        }
        return ActionResult.CONSUME;
    }

    public static VoxelShape rotateShape(Direction from, Direction to, VoxelShape shape) {
        return LogTableBlock.rotateShape(from, to, shape);
    }

    protected static final VoxelShape STRAIGHT = VoxelShapes.union(createCuboidShape(0, 0, 0,16, 1, 12), createCuboidShape(0, 1, 0,16, 14, 13),createCuboidShape(0, 14, 0,16, 16, 16), createCuboidShape(1, 8, 12,15, 13, 14), createCuboidShape(1, 2, 12, 15, 7, 14), createCuboidShape(6, 4, 14, 10, 5, 15), createCuboidShape(6, 10, 14, 10, 11, 15));
    protected static final VoxelShape STRAIGHT_OPEN = VoxelShapes.union(createCuboidShape(0, 0, 0,16, 1, 12), createCuboidShape(0, 1, 0,16, 14, 13), createCuboidShape(6, 10, 19, 10, 11, 20), createCuboidShape(1, 8, 13, 15, 13, 19), createCuboidShape(0, 14, 0, 16, 16, 16),createCuboidShape(1, 2, 12, 15, 7, 14), createCuboidShape(6, 4, 14, 10, 5, 15));
    protected static final VoxelShape OUTER_CORNER_OPEN = VoxelShapes.union(createCuboidShape(0, 0, 0,12, 1, 12),createCuboidShape(0, 1, 0,13, 14, 13),createCuboidShape(0, 14, 0,16, 16, 16),createCuboidShape(5, 10, 19,8, 11, 20),createCuboidShape(1, 8, 13,12, 13, 19),createCuboidShape(1, 2, 12,12, 7, 14),createCuboidShape(5, 4, 14,8, 5, 15),createCuboidShape(12, 8, 1,14, 13, 12),createCuboidShape(12, 2, 1,14, 7, 12),createCuboidShape(14, 4, 5,15, 5, 8),createCuboidShape(14, 10, 5,15, 11, 8));
    protected static final VoxelShape OUTER_CORNER = VoxelShapes.union(createCuboidShape(0, 0, 0,12, 1, 12),createCuboidShape(0, 1, 0,13, 14, 13),createCuboidShape(0, 14, 0,16, 16, 16),createCuboidShape(5, 10, 14,8, 11, 15),createCuboidShape(1, 8, 13,12, 13, 14),createCuboidShape(1, 2, 12,12, 7, 14),createCuboidShape(5, 4, 14,8, 5, 15),createCuboidShape(12, 8, 1,14, 13, 12),createCuboidShape(12, 2, 1,14, 7, 12),createCuboidShape(14, 4, 5,15, 5, 8),createCuboidShape(14, 10, 5,15, 11, 8));
    protected static final VoxelShape INNER_CORNER = VoxelShapes.union(createCuboidShape(4, 0, 0,16, 1, 16),createCuboidShape(0, 0, 0,4, 1, 11.9),createCuboidShape(3, 1, 0,16, 14, 16),createCuboidShape(0, 1, 0,3, 14, 13),createCuboidShape(0, 14, 0,16, 16, 16),createCuboidShape(1, 2, 12,3, 7, 14),createCuboidShape(1, 8, 12,3, 13, 14),createCuboidShape(2, 8, 14,13, 13, 15));
    protected static final VoxelShape RIGHT_EDGE = VoxelShapes.union(createCuboidShape(0,0,0,14, 1, 12),createCuboidShape(0,1,0,14, 14, 13),createCuboidShape(0,14,0,16, 16, 16),createCuboidShape(14,0,0,16, 14, 16),createCuboidShape(1,8,12,13, 13, 14),createCuboidShape(1, 2, 12,13, 7, 14),createCuboidShape(6, 4, 14,9,5,15),createCuboidShape(6,10,14,9, 11, 15));
    protected static final VoxelShape LEFT_EDGE = VoxelShapes.union(createCuboidShape(2,0,0,16, 1, 12),createCuboidShape(2,1,0,16, 14, 13),createCuboidShape(0,0,0,2, 14, 16),createCuboidShape(0,14,0,16, 16, 16),createCuboidShape(3,8,12,15, 13, 14),createCuboidShape(3, 2, 12,15, 7, 14),createCuboidShape(8,4,14,11, 5, 15),createCuboidShape(8, 10, 14,11, 11, 15));
    protected static final VoxelShape RIGHT_EDGE_OPEN =  VoxelShapes.union(createCuboidShape(0, 14, 0,16, 16, 16),createCuboidShape(14, 0, 0,16, 14, 16),createCuboidShape(0, 0, 0,14, 1, 12),createCuboidShape(0, 1, 0,14, 14, 13),createCuboidShape(6, 10, 19,10, 11, 20),createCuboidShape(1, 8, 13,13, 13, 19),createCuboidShape(1, 2, 12,13, 7, 14),createCuboidShape(6, 4, 14,10, 5, 15));
    protected static final VoxelShape LEFT_EDGE_OPEN = VoxelShapes.union(createCuboidShape(0, 14, 0,16, 16, 16),createCuboidShape(0, 0, 0,2, 14, 16),createCuboidShape(2, 0, 0,16, 1, 12),createCuboidShape(2, 1, 0,16, 14, 13),createCuboidShape(8, 10, 19,12, 11, 20),createCuboidShape(3, 8, 13,15, 13, 19),createCuboidShape(3, 2, 12,15, 7, 14),createCuboidShape(8, 4, 14,12, 5, 15));

    protected static final VoxelShape STRAIGHT_SOUTH = rotateShape(Direction.NORTH, Direction.SOUTH, STRAIGHT);
    protected static final VoxelShape STRAIGHT_OPEN_SOUTH = rotateShape(Direction.NORTH, Direction.SOUTH, STRAIGHT_OPEN);
    protected static final VoxelShape STRAIGHT_EAST = rotateShape(Direction.NORTH, Direction.EAST, STRAIGHT);
    protected static final VoxelShape STRAIGHT_OPEN_EAST = rotateShape(Direction.NORTH, Direction.EAST, STRAIGHT_OPEN);
    protected static final VoxelShape STRAIGHT_WEST = rotateShape(Direction.NORTH, Direction.WEST, STRAIGHT);
    protected static final VoxelShape STRAIGHT_OPEN_WEST = rotateShape(Direction.NORTH, Direction.WEST, STRAIGHT_OPEN);
    protected static final VoxelShape INNER_CORNER_WEST = rotateShape(Direction.NORTH, Direction.WEST, INNER_CORNER);
    protected static final VoxelShape INNER_CORNER_EAST = rotateShape(Direction.NORTH, Direction.EAST, INNER_CORNER);
    protected static final VoxelShape INNER_CORNER_SOUTH = rotateShape(Direction.NORTH, Direction.SOUTH, INNER_CORNER);
    protected static final VoxelShape OUTER_CORNER_SOUTH = rotateShape(Direction.NORTH, Direction.SOUTH, OUTER_CORNER);
    protected static final VoxelShape OUTER_CORNER_OPEN_SOUTH = rotateShape(Direction.NORTH, Direction.SOUTH, OUTER_CORNER_OPEN);
    protected static final VoxelShape OUTER_CORNER_EAST = rotateShape(Direction.NORTH, Direction.EAST, OUTER_CORNER);
    protected static final VoxelShape OUTER_CORNER_OPEN_EAST = rotateShape(Direction.NORTH, Direction.EAST, OUTER_CORNER_OPEN);
    protected static final VoxelShape OUTER_CORNER_WEST = rotateShape(Direction.NORTH, Direction.WEST, OUTER_CORNER);
    protected static final VoxelShape OUTER_CORNER_OPEN_WEST = rotateShape(Direction.NORTH, Direction.WEST, OUTER_CORNER_OPEN);
    protected static final VoxelShape LEFT_EDGE_OPEN_SOUTH = rotateShape(Direction.NORTH, Direction.SOUTH, LEFT_EDGE_OPEN);
    protected static final VoxelShape LEFT_EDGE_SOUTH = rotateShape(Direction.NORTH, Direction.SOUTH, LEFT_EDGE);
    protected static final VoxelShape LEFT_EDGE_OPEN_WEST = rotateShape(Direction.NORTH, Direction.WEST, LEFT_EDGE_OPEN);
    protected static final VoxelShape LEFT_EDGE_WEST =  rotateShape(Direction.NORTH, Direction.WEST, LEFT_EDGE);
    protected static final VoxelShape LEFT_EDGE_OPEN_EAST = rotateShape(Direction.NORTH, Direction.EAST, LEFT_EDGE_OPEN);
    protected static final VoxelShape LEFT_EDGE_EAST = rotateShape(Direction.NORTH, Direction.EAST, LEFT_EDGE);
    protected static final VoxelShape RIGHT_EDGE_OPEN_SOUTH = rotateShape(Direction.NORTH, Direction.SOUTH, RIGHT_EDGE_OPEN);
    protected static final VoxelShape RIGHT_EDGE_SOUTH = rotateShape(Direction.NORTH, Direction.SOUTH, RIGHT_EDGE);
    protected static final VoxelShape RIGHT_EDGE_OPEN_WEST = rotateShape(Direction.NORTH, Direction.WEST, RIGHT_EDGE_OPEN);
    protected static final VoxelShape RIGHT_EDGE_WEST =  rotateShape(Direction.NORTH, Direction.WEST, RIGHT_EDGE);
    protected static final VoxelShape RIGHT_EDGE_OPEN_EAST = rotateShape(Direction.NORTH, Direction.EAST, RIGHT_EDGE_OPEN);
    protected static final VoxelShape RIGHT_EDGE_EAST = rotateShape(Direction.NORTH, Direction.EAST, RIGHT_EDGE);

    protected static final VoxelShape MIDDLE = VoxelShapes.union(createCuboidShape(0, 0, 0,16, 16, 13),createCuboidShape(13, 2, 14,14, 6, 15),createCuboidShape(1, 1, 13,15, 15, 14));
    protected static final VoxelShape MIDDLE_OPEN = VoxelShapes.union(createCuboidShape(0, 0, 0,16, 16, 13),createCuboidShape(1, 1, 13,2, 15, 27),createCuboidShape(0, 2, 25,1, 6, 26));
    protected static final VoxelShape MIDDLE_OUTER_CORNER_OPEN = VoxelShapes.union(createCuboidShape(0, 0, 0,13, 16, 13),createCuboidShape(1, 2, 12.75,2, 15, 23.75),createCuboidShape(0, 2.5, 21.75,1, 6.5, 22.75),createCuboidShape(13, 2, 1,14, 15, 12),createCuboidShape(14, 2.5, 10,15, 6.5, 11));
    protected static final VoxelShape MIDDLE_OUTER_CORNER = VoxelShapes.union(createCuboidShape(0, 0, 0,13, 16, 13),createCuboidShape(13, 2, 1,14, 15, 12),createCuboidShape(14, 2.5, 10,15, 6.5, 11),createCuboidShape(1, 2, 12,12, 15, 14),createCuboidShape(10, 2.5, 14,11, 6.5, 15));
    protected static final VoxelShape MIDDLE_INNER_CORNER = VoxelShapes.union(createCuboidShape(3, 0, 13,16, 16, 16),createCuboidShape(0, 0, 0,16, 16, 13),createCuboidShape(2, 1, 14,3, 15, 16),createCuboidShape(0, 1, 13,3, 15, 14));
    protected static final VoxelShape MIDDLE_INNER_CORNER_WEST =  rotateShape(Direction.NORTH, Direction.WEST, MIDDLE_INNER_CORNER);
    protected static final VoxelShape MIDDLE_INNER_CORNER_EAST =  rotateShape(Direction.NORTH, Direction.EAST, MIDDLE_INNER_CORNER);
    protected static final VoxelShape MIDDLE_INNER_CORNER_SOUTH =  rotateShape(Direction.NORTH, Direction.SOUTH, MIDDLE_INNER_CORNER);
    protected static final VoxelShape MIDDLE_OUTER_CORNER_WEST =  rotateShape(Direction.NORTH, Direction.WEST, MIDDLE_OUTER_CORNER);
    protected static final VoxelShape MIDDLE_OUTER_CORNER_EAST =  rotateShape(Direction.NORTH, Direction.EAST, MIDDLE_OUTER_CORNER);
    protected static final VoxelShape MIDDLE_OUTER_CORNER_SOUTH =  rotateShape(Direction.NORTH, Direction.SOUTH, MIDDLE_OUTER_CORNER);
    protected static final VoxelShape MIDDLE_OUTER_CORNER_OPEN_WEST =  rotateShape(Direction.NORTH, Direction.WEST, MIDDLE_OUTER_CORNER_OPEN);
    protected static final VoxelShape MIDDLE_OUTER_CORNER_OPEN_EAST =  rotateShape(Direction.NORTH, Direction.EAST, MIDDLE_OUTER_CORNER_OPEN);
    protected static final VoxelShape MIDDLE_OUTER_CORNER_OPEN_SOUTH =  rotateShape(Direction.NORTH, Direction.SOUTH, MIDDLE_OUTER_CORNER_OPEN);
    protected static final VoxelShape MIDDLE_WEST = rotateShape(Direction.NORTH, Direction.WEST, MIDDLE);
    protected static final VoxelShape MIDDLE_OPEN_WEST = rotateShape(Direction.NORTH, Direction.WEST, MIDDLE_OPEN);
    protected static final VoxelShape MIDDLE_EAST = rotateShape(Direction.NORTH, Direction.EAST, MIDDLE);
    protected static final VoxelShape MIDDLE_OPEN_EAST = rotateShape(Direction.NORTH, Direction.EAST, MIDDLE_OPEN);
    protected static final VoxelShape MIDDLE_SOUTH = rotateShape(Direction.NORTH, Direction.SOUTH, MIDDLE);
    protected static final VoxelShape MIDDLE_OPEN_SOUTH = rotateShape(Direction.NORTH, Direction.SOUTH, MIDDLE_OPEN);

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Direction direction = state.get(KitchenCounterBlock.FACING);
        boolean right = canConnect(world, pos, state.get(KitchenCounterBlock.FACING).rotateYCounterclockwise());
        boolean left = canConnect(world, pos, state.get(KitchenCounterBlock.FACING).rotateYClockwise());
        BlockState neighborStateFacing = world.getBlockState(pos.offset(direction));
        BlockState neighborStateOpposite = world.getBlockState(pos.offset(direction.getOpposite()));
        boolean open = state.get(OPEN);
        if (canConnectToCounter(neighborStateFacing) && neighborStateFacing.getProperties().contains(Properties.HORIZONTAL_FACING)) {
            Direction direction2 = neighborStateFacing.get(Properties.HORIZONTAL_FACING);
            if (direction2.getAxis() != state.get(Properties.HORIZONTAL_FACING).getAxis() && isDifferentOrientation(state, world, pos, direction2.getOpposite())) {
                if (direction2 == direction.rotateYCounterclockwise()) {
                    switch (direction) {
                        case NORTH: {
                            if (open) {
                                return OUTER_CORNER_OPEN;
                            }
                            return OUTER_CORNER;
                        }
                        case SOUTH: {
                            if (open) {
                                return OUTER_CORNER_OPEN_SOUTH;
                            }
                            return OUTER_CORNER_SOUTH;
                        }
                        case EAST: {
                            if (open) {
                                return OUTER_CORNER_OPEN_EAST;
                            }
                            return OUTER_CORNER_EAST;
                        }
                        default: {
                            if (open) {
                                return OUTER_CORNER_OPEN_WEST;
                            }
                            return OUTER_CORNER_WEST;
                        }
                    }
                }
                else {
                    switch (direction) {
                        case NORTH: {
                            if (open) {
                                return OUTER_CORNER_OPEN_EAST;
                            }
                            return OUTER_CORNER_EAST;
                        }
                        case SOUTH: {
                            if (open) {
                                return OUTER_CORNER_OPEN_WEST;
                            }
                            return OUTER_CORNER_WEST;
                        }
                        case EAST: {
                            if (open) {
                                return OUTER_CORNER_OPEN_SOUTH;
                            }
                            return OUTER_CORNER_SOUTH;
                        }
                        default: {
                            if (open) {
                                return OUTER_CORNER_OPEN;
                            }
                            return OUTER_CORNER;
                        }
                    }
                }
            } else {
                switch (direction) {
                    case NORTH: {
                        if (open) {
                            return STRAIGHT_OPEN;
                        }
                        return STRAIGHT;
                    }
                    case SOUTH: {
                        if (open) {
                            return STRAIGHT_OPEN_SOUTH;
                        }
                        return STRAIGHT_SOUTH;
                    }
                    case EAST: {
                        if (open) {
                            return STRAIGHT_OPEN_EAST;
                        }
                        return STRAIGHT_EAST;
                    }
                    default: {
                        if (open) {
                            return STRAIGHT_OPEN_WEST;
                        }
                        return STRAIGHT_WEST;
                    }
                }
            }
        }
        else if (canConnectToCounter(neighborStateOpposite) && neighborStateOpposite.getProperties().contains(Properties.HORIZONTAL_FACING)) {
            Direction direction3;
            if (neighborStateOpposite.getBlock() instanceof AbstractFurnaceBlock) {
                direction3 = neighborStateOpposite.get(Properties.HORIZONTAL_FACING).getOpposite();
            }
            else {
                direction3 = neighborStateOpposite.get(Properties.HORIZONTAL_FACING);
            }
            if (direction3.getAxis() != state.get(Properties.HORIZONTAL_FACING).getAxis() && isDifferentOrientation(state, world, pos, direction3)) {
                if (direction3 == direction.rotateYCounterclockwise()) {
                    switch (direction) {
                        case NORTH: return INNER_CORNER_WEST;
                        case SOUTH: return INNER_CORNER_EAST;
                        case EAST: return INNER_CORNER;
                        default: return INNER_CORNER_SOUTH;
                    }
                } else {
                    switch (direction) {
                        case NORTH: return INNER_CORNER;
                        case SOUTH: return INNER_CORNER_SOUTH;
                        case EAST: return INNER_CORNER_EAST;
                        default: return INNER_CORNER_WEST;
                    }
                }
            } else {
                switch (direction) {
                    case NORTH: {
                        if (open) {
                            return STRAIGHT_OPEN;
                        }
                        return STRAIGHT;
                    }
                    case SOUTH: {
                        if (open) {
                            return STRAIGHT_OPEN_SOUTH;
                        }
                        return STRAIGHT_SOUTH;
                    }
                    case EAST: {
                        if (open) {
                            return STRAIGHT_OPEN_EAST;
                        }
                        return STRAIGHT_EAST;
                    }
                    default: {
                        if (open) {
                            return STRAIGHT_OPEN_WEST;
                        }
                        return STRAIGHT_WEST;
                    }
                }
            }
        }
        else if (left && right) {
            switch (direction) {
                case NORTH: {
                    if (open) {
                        return STRAIGHT_OPEN;
                    }
                    return STRAIGHT;
                }
                case SOUTH: {
                    if (open) {
                        return STRAIGHT_OPEN_SOUTH;
                    }
                    return STRAIGHT_SOUTH;
                }
                case EAST: {
                    if (open) {
                        return STRAIGHT_OPEN_EAST;
                    }
                    return STRAIGHT_EAST;
                }
                default: {
                    if (open) {
                        return STRAIGHT_OPEN_WEST;
                    }
                    return STRAIGHT_WEST;
                }
            }
        } else if (left) {
            switch (direction) {
                case NORTH: {
                    if (open)
                        return LEFT_EDGE_OPEN;
                    return LEFT_EDGE;
                }
                case SOUTH: {
                    if (open)
                        return LEFT_EDGE_OPEN_SOUTH;
                    return LEFT_EDGE_SOUTH;
                }
                case EAST: {
                    if (open)
                        return LEFT_EDGE_OPEN_EAST;
                    return LEFT_EDGE_EAST;
                }
                default: {
                    if (open)
                        return LEFT_EDGE_OPEN_WEST;
                    return LEFT_EDGE_WEST;
                }
            }
        } else if (right) {
            switch (direction) {
                case NORTH: {
                    if (open)
                        return RIGHT_EDGE_OPEN;
                    return RIGHT_EDGE;
                }
                case SOUTH: {
                    if (open)
                        return RIGHT_EDGE_OPEN_SOUTH;
                    return RIGHT_EDGE_SOUTH;
                }
                case EAST: {
                    if (open)
                        return RIGHT_EDGE_OPEN_EAST;
                    return RIGHT_EDGE_EAST;
                }
                default: {
                    if (open)
                        return RIGHT_EDGE_OPEN_WEST;
                    return RIGHT_EDGE_WEST;
                }
            }
        } else {
            switch (direction) {
                case NORTH: {
                    if (open) {
                        return STRAIGHT_OPEN;
                    }
                    return STRAIGHT;
                }
                case SOUTH: {
                    if (open) {
                        return STRAIGHT_OPEN_SOUTH;
                    }
                    return STRAIGHT_SOUTH;
                }
                case EAST: {
                    if (open) {
                        return STRAIGHT_OPEN_EAST;
                    }
                    return STRAIGHT_EAST;
                }
                default: {
                    if (open) {
                        return STRAIGHT_OPEN_WEST;
                    }
                    return STRAIGHT_WEST;
                }
            }
        }
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return GenericStorageBlockEntity9x3.getFactory().create(pos,state);
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos));
    }

}

