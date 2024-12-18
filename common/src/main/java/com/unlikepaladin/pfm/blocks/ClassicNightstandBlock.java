package com.unlikepaladin.pfm.blocks;

import com.mojang.serialization.MapCodec;
import com.unlikepaladin.pfm.blocks.blockentities.GenericStorageBlockEntity9x3;
import com.unlikepaladin.pfm.data.FurnitureBlock;
import com.unlikepaladin.pfm.registry.Statistics;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
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

import static com.unlikepaladin.pfm.blocks.KitchenDrawerBlock.rotateShape;

public class ClassicNightstandBlock extends HorizontalFacingBlockWithEntity {
    public static BooleanProperty OPEN = Properties.OPEN;
    private static final List<FurnitureBlock> WOOD_NIGHTSTAND = new ArrayList<>();
    private static final List<FurnitureBlock> STONE_NIGHTSTAND = new ArrayList<>();
    public static final MapCodec<ClassicNightstandBlock> CODEC = createCodec(ClassicNightstandBlock::new);

    public ClassicNightstandBlock(Settings settings) {
        super(settings);
        setDefaultState(this.getStateManager().getDefaultState().with(FACING, Direction.NORTH).with(OPEN, false));
        if(AbstractSittableBlock.isWoodBased(this.getDefaultState()) && this.getClass().isAssignableFrom(ClassicNightstandBlock.class)){
            WOOD_NIGHTSTAND.add(new FurnitureBlock(this, "classic_nightstand"));
        }
        else if (this.getClass().isAssignableFrom(ClassicNightstandBlock.class)){
            STONE_NIGHTSTAND.add(new FurnitureBlock(this, "classic_nightstand"));
        }
    }


    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    public static Stream<FurnitureBlock> streamWoodClassicNightstands() {
        return WOOD_NIGHTSTAND.stream();
    }
    public static Stream<FurnitureBlock> streamStoneClassicNightstands() {
        return STONE_NIGHTSTAND.stream();
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return super.getFluidState(state);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(OPEN);
        super.appendProperties(stateManager);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.CONSUME;
        }
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (world instanceof ServerWorld && blockEntity instanceof GenericStorageBlockEntity9x3) {
            player.openHandledScreen((GenericStorageBlockEntity9x3)blockEntity);
            player.incrementStat(Statistics.CABINET_SEARCHED);
            PiglinBrain.onGuardedBlockInteracted((ServerWorld) world, player, true);
        }
        return ActionResult.SUCCESS;
    }


    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
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

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return GenericStorageBlockEntity9x3.getFactory().create(pos, state);
    }

    public int getFlammability(BlockState state, BlockView world, BlockPos pos, Direction face) {
        if (AbstractSittableBlock.isWoodBased(state)) {
            return 20;
        }
        return 0;
    }

    public boolean isStand(BlockView world, BlockPos pos, Direction direction, Direction tableDirection)
    {
        BlockState state = world.getBlockState(pos.offset(direction));
        if(state.getBlock() == this)
        {
            Direction sourceDirection = state.get(FACING);
            return sourceDirection.equals(tableDirection);
        }
        return false;
    }

    static final VoxelShape NIGHT_STAND = VoxelShapes.union(createCuboidShape(0, 14, 0,16, 16, 16),createCuboidShape(3, 1, 1,13, 3, 2),createCuboidShape(13, 1, 1,15, 14, 2),createCuboidShape(1, 1, 2,15, 14, 15),createCuboidShape(0.5, 0, 0,3.5, 1, 16),createCuboidShape(12.5, 0, 0,15.5, 1, 16),createCuboidShape(4, 9, 1,12, 13, 2),createCuboidShape(4, 4, 1,12, 8, 2),createCuboidShape(6.5, 5.5, 0,9.5, 6.5, 1),createCuboidShape(6.5, 10.5, 0,9.5, 11.5, 1));
    static final VoxelShape NIGHT_STAND_SOUTH = rotateShape(Direction.NORTH, Direction.SOUTH, NIGHT_STAND);
    static final VoxelShape NIGHT_STAND_EAST = rotateShape(Direction.NORTH, Direction.EAST, NIGHT_STAND);
    static final VoxelShape NIGHT_STAND_WEST = rotateShape(Direction.NORTH, Direction.WEST, NIGHT_STAND);
    static final VoxelShape NIGHT_STAND_OPEN = VoxelShapes.union(createCuboidShape(0, 14, 0,16, 16, 16),createCuboidShape(3, 1, 1,13, 3, 2),createCuboidShape(1, 1, 1,3, 14, 2),createCuboidShape(13, 1, 1,15, 14, 2),createCuboidShape(1, 1, 2,15, 14, 15),createCuboidShape(0.5, 0, 0,3.5, 1, 16),createCuboidShape(12.5, 0, 0,15.5, 1, 16),createCuboidShape(4, 9, 1,12, 13, 2),createCuboidShape(4, 4, -6,12, 8, 2),createCuboidShape(6.5, 10.5, 0,9.5, 11.5, 1),createCuboidShape(6.5, 5.5, -7,9.5, 6.5, -6));
    static final VoxelShape NIGHT_STAND_OPEN_SOUTH = rotateShape(Direction.NORTH, Direction.SOUTH, NIGHT_STAND_OPEN);
    static final VoxelShape NIGHT_STAND_OPEN_EAST = rotateShape(Direction.NORTH, Direction.EAST, NIGHT_STAND_OPEN);
    static final VoxelShape NIGHT_STAND_OPEN_WEST = rotateShape(Direction.NORTH, Direction.WEST, NIGHT_STAND_OPEN);

    static final VoxelShape NIGHT_STAND_MIDDLE = VoxelShapes.union(createCuboidShape(0, 14, 0, 2, 16, 16),createCuboidShape(15, 14, 0, 16, 16, 16),createCuboidShape(1, 1, 0, 2, 3, 16),createCuboidShape(2, 1, 0, 15, 16, 16),createCuboidShape(1, 9, 2.5, 2, 13, 13.5),createCuboidShape(0, 10.5, 6.5, 1, 11.5, 9.5),createCuboidShape(0, 10.5, 6.5, 1, 11.5, 9.5));
    static final VoxelShape NIGHT_STAND_MIDDLE_SOUTH = rotateShape(Direction.NORTH, Direction.SOUTH, NIGHT_STAND_MIDDLE);
    static final VoxelShape NIGHT_STAND_MIDDLE_EAST = rotateShape(Direction.NORTH, Direction.EAST, NIGHT_STAND_MIDDLE);
    static final VoxelShape NIGHT_STAND_MIDDLE_WEST = rotateShape(Direction.NORTH, Direction.WEST, NIGHT_STAND_MIDDLE);
    static final VoxelShape NIGHT_STAND_MIDDLE_OPEN = VoxelShapes.union(createCuboidShape(0, 14, 0, 2, 16, 16),createCuboidShape(15, 14, 0, 16, 16, 16),createCuboidShape(1, 1, 0, 2, 3, 16),createCuboidShape(2, 1, 0, 15, 16, 16),createCuboidShape(1, 9, 2.5, 2, 13, 13.5),createCuboidShape(0, 10.5, 6.5, 1, 11.5, 9.5),createCuboidShape(0, 10.5, 6.5, 1, 11.5, 9.5));
    static final VoxelShape NIGHT_STAND_MIDDLE_OPEN_SOUTH = rotateShape(Direction.NORTH, Direction.SOUTH, NIGHT_STAND_MIDDLE_OPEN);
    static final VoxelShape NIGHT_STAND_MIDDLE_OPEN_EAST = rotateShape(Direction.NORTH, Direction.EAST, NIGHT_STAND_MIDDLE_OPEN);
    static final VoxelShape NIGHT_STAND_MIDDLE_OPEN_WEST = rotateShape(Direction.NORTH, Direction.WEST, NIGHT_STAND_MIDDLE_OPEN);

    static final VoxelShape NIGHT_STAND_LEFT = VoxelShapes.union(createCuboidShape(0, 14, 0, 2, 16, 16),createCuboidShape(15, 14, 1, 16, 16, 16),createCuboidShape(2, 14, 0, 16, 16, 1),createCuboidShape(1, 1, 3, 2, 3, 16),createCuboidShape(2, 1, 1, 15, 16, 16),createCuboidShape(1, 1, 1, 2, 14, 3),createCuboidShape(0, 0, 0.5, 16, 1, 3.5),createCuboidShape(1, 9, 4, 2, 13, 15),createCuboidShape(1, 4, 4, 2, 8, 15),createCuboidShape(0, 5.5, 8, 1, 6.5, 11),createCuboidShape(0, 10.5, 8, 1, 11.5, 11));
    static final VoxelShape NIGHT_STAND_LEFT_SOUTH = rotateShape(Direction.NORTH, Direction.SOUTH, NIGHT_STAND_LEFT);
    static final VoxelShape NIGHT_STAND_LEFT_EAST = rotateShape(Direction.NORTH, Direction.EAST, NIGHT_STAND_LEFT);
    static final VoxelShape NIGHT_STAND_LEFT_WEST = rotateShape(Direction.NORTH, Direction.WEST, NIGHT_STAND_LEFT);
    static final VoxelShape NIGHT_STAND_LEFT_OPEN = VoxelShapes.union(createCuboidShape(0, 14, 1, 2, 16, 16),createCuboidShape(15, 14, 1, 16, 16, 16),createCuboidShape(0, 14, 0, 16, 16, 1),createCuboidShape(1, 1, 3, 2, 3, 16),createCuboidShape(-7, 5.5, 8, -6, 6.5, 11),createCuboidShape(-6, 4, 4, 2, 8, 15),createCuboidShape(1, 1, 1, 2, 14, 3),createCuboidShape(0, 0, 0.5, 16, 1, 3.5),createCuboidShape(1, 9, 4, 2, 13, 15),createCuboidShape(0, 10.5, 8, 1, 11.5, 11),createCuboidShape(2, 1, 1, 15, 16, 16));
    static final VoxelShape NIGHT_STAND_LEFT_OPEN_SOUTH = rotateShape(Direction.NORTH, Direction.SOUTH, NIGHT_STAND_LEFT_OPEN);
    static final VoxelShape NIGHT_STAND_LEFT_OPEN_EAST = rotateShape(Direction.NORTH, Direction.EAST, NIGHT_STAND_LEFT_OPEN);
    static final VoxelShape NIGHT_STAND_LEFT_OPEN_WEST = rotateShape(Direction.NORTH, Direction.WEST, NIGHT_STAND_LEFT_OPEN);

    static final VoxelShape NIGHT_STAND_RIGHT = VoxelShapes.union(createCuboidShape(0, 14, 0, 2, 16, 15),createCuboidShape(15, 14, 0, 16, 16, 15),createCuboidShape(0, 14, 15, 16, 16, 16),createCuboidShape(1, 1, 0, 2, 3, 13),createCuboidShape(1, 1, 13, 2, 14, 15),createCuboidShape(0, 0, 12.5, 16, 1, 15.5),createCuboidShape(1, 9, 1, 2, 13, 12),createCuboidShape(0, 10.5, 5, 1, 11.5, 8),createCuboidShape(0, 5.5, 5, 1, 6.5, 8),createCuboidShape(2, 1, 0, 15, 16, 15),createCuboidShape(1, 4, 1, 2, 8, 12));
    static final VoxelShape NIGHT_STAND_RIGHT_SOUTH = rotateShape(Direction.NORTH, Direction.SOUTH, NIGHT_STAND_RIGHT);
    static final VoxelShape NIGHT_STAND_RIGHT_EAST = rotateShape(Direction.NORTH, Direction.EAST, NIGHT_STAND_RIGHT);
    static final VoxelShape NIGHT_STAND_RIGHT_WEST = rotateShape(Direction.NORTH, Direction.WEST, NIGHT_STAND_RIGHT);
    static final VoxelShape NIGHT_STAND_RIGHT_OPEN = VoxelShapes.union(createCuboidShape(0, 14, 0, 2, 16, 15),createCuboidShape(15, 14, 0, 16, 16, 15),createCuboidShape(0, 14, 15, 16, 16, 16),createCuboidShape(1, 1, 0, 2, 3, 13),createCuboidShape(1, 1, 13, 2, 14, 15),createCuboidShape(0, 0, 12.5, 16, 1, 15.5),createCuboidShape(1, 9, 1, 2, 13, 12),createCuboidShape(0, 10.5, 5, 1, 11.5, 8),createCuboidShape(-7, 5.5, 5, -6, 6.5, 8),createCuboidShape(2, 1, 0, 15, 16, 15),createCuboidShape(-6, 4, 1, 2, 8, 12));
    static final VoxelShape NIGHT_STAND_RIGHT_OPEN_SOUTH = rotateShape(Direction.NORTH, Direction.SOUTH, NIGHT_STAND_RIGHT_OPEN);
    static final VoxelShape NIGHT_STAND_RIGHT_OPEN_EAST = rotateShape(Direction.NORTH, Direction.EAST, NIGHT_STAND_RIGHT_OPEN);
    static final VoxelShape NIGHT_STAND_RIGHT_OPEN_WEST = rotateShape(Direction.NORTH, Direction.WEST, NIGHT_STAND_RIGHT_OPEN);
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Direction dir = state.get(FACING);
        boolean open = state.get(OPEN);
        boolean left = isStand(world, pos, dir.rotateYCounterclockwise(), dir);
        boolean right = isStand(world, pos, dir.rotateYClockwise(), dir);
        MiddleShape shape = left || right ? left && right ? MiddleShape.MIDDLE : left ? MiddleShape.LEFT : MiddleShape.RIGHT : MiddleShape.SINGLE;
        switch (shape) {
            case SINGLE -> {
                switch (dir) {
                    case NORTH -> {
                        if (open) {
                            return NIGHT_STAND_OPEN;
                        }
                        return NIGHT_STAND;
                    }
                    case SOUTH -> {
                        if (open) {
                            return NIGHT_STAND_OPEN_SOUTH;
                        }
                        return NIGHT_STAND_SOUTH;
                    }
                    case EAST -> {
                        if (open) {
                            return NIGHT_STAND_OPEN_EAST;
                        }
                        return NIGHT_STAND_EAST;
                    }
                    default -> {
                        if (open) {
                            return NIGHT_STAND_OPEN_WEST;
                        }
                        return NIGHT_STAND_WEST;
                    }
                }
            }
            case MIDDLE -> {
                switch (dir) {
                    case NORTH -> {
                        if (open) {
                            return NIGHT_STAND_MIDDLE_OPEN_EAST;
                        }
                        return NIGHT_STAND_MIDDLE_EAST;
                    }
                    case SOUTH -> {
                        if (open) {
                            return NIGHT_STAND_MIDDLE_OPEN_WEST;
                        }
                        return NIGHT_STAND_MIDDLE_WEST;
                    }
                    case EAST -> {
                        if (open) {
                            return NIGHT_STAND_MIDDLE_OPEN_SOUTH;
                        }
                        return NIGHT_STAND_MIDDLE_SOUTH;
                    }
                    default -> {
                        if (open) {
                            return NIGHT_STAND_MIDDLE_OPEN;
                        }
                        return NIGHT_STAND_MIDDLE;
                    }
                }
            }
            case LEFT -> {
                switch (dir) {
                    case NORTH -> {
                        if (open) {
                            return NIGHT_STAND_LEFT_OPEN_EAST;
                        }
                        return NIGHT_STAND_LEFT_EAST;
                    }
                    case SOUTH -> {
                        if (open) {
                            return NIGHT_STAND_LEFT_OPEN_WEST;
                        }
                        return NIGHT_STAND_LEFT_WEST;
                    }
                    case EAST -> {
                        if (open) {
                            return NIGHT_STAND_LEFT_OPEN_SOUTH;
                        }
                        return NIGHT_STAND_LEFT_SOUTH;
                    }
                    default -> {
                        if (open) {
                            return NIGHT_STAND_LEFT_OPEN;
                        }
                        return NIGHT_STAND_LEFT;
                    }
                }
            }
            default -> {
                switch (dir) {
                    case NORTH -> {
                        if (open) {
                            return NIGHT_STAND_RIGHT_OPEN_EAST;
                        }
                        return NIGHT_STAND_RIGHT_EAST;
                    }
                    case SOUTH -> {
                        if (open) {
                            return NIGHT_STAND_RIGHT_OPEN_WEST;
                        }
                        return NIGHT_STAND_RIGHT_WEST;
                    }
                    case EAST -> {
                        if (open) {
                            return NIGHT_STAND_RIGHT_OPEN_SOUTH;
                        }
                        return NIGHT_STAND_RIGHT_SOUTH;
                    }
                    default -> {
                        if (open) {
                            return NIGHT_STAND_RIGHT_OPEN;
                        }
                        return NIGHT_STAND_RIGHT;
                    }
                }
            }
        }

    }

    @Override
    public boolean canPathfindThrough(BlockState state, NavigationType type) {
        return false;
    }
}
