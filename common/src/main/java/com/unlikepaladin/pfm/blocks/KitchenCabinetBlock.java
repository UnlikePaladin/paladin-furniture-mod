package com.unlikepaladin.pfm.blocks;

import com.mojang.serialization.MapCodec;
import com.unlikepaladin.pfm.blocks.blockentities.GenericStorageBlockEntity9x3;
import com.unlikepaladin.pfm.data.FurnitureBlock;
import com.unlikepaladin.pfm.registry.Statistics;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.state.State;
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

import static com.unlikepaladin.pfm.blocks.KitchenCounterBlock.rotateShape;

public class KitchenCabinetBlock extends HorizontalFacingBlock implements BlockEntityProvider {
    private final BlockState baseBlockState;
    private final Block baseBlock;
    private static final List<FurnitureBlock> WOOD_CABINETS = new ArrayList<>();
    private static final List<FurnitureBlock> STONE_CABINETS = new ArrayList<>();

    public KitchenCabinetBlock(Settings settings) {
        super(settings.luminance((state) -> 0).emissiveLighting((blockstate, b, c) -> false));
        setDefaultState(this.getStateManager().getDefaultState().with(Properties.HORIZONTAL_FACING, Direction.NORTH).with(OPEN, false));
        this.baseBlockState = this.getDefaultState();
        this.baseBlock = baseBlockState.getBlock();
        if(AbstractSittableBlock.isWoodBased(this.getDefaultState()) && this.getClass().isAssignableFrom(KitchenCabinetBlock.class)){
            WOOD_CABINETS.add(new FurnitureBlock(this, "kitchen_cabinet"));
        }
        else if (this.getClass().isAssignableFrom(KitchenCabinetBlock.class)){
            STONE_CABINETS.add(new FurnitureBlock(this, "kitchen_cabinet"));
        }
    }

    public static final MapCodec<KitchenCabinetBlock> CODEC = createCodec(KitchenCabinetBlock::new);
    @Override
    protected MapCodec<? extends HorizontalFacingBlock> getCodec() {
        return CODEC;
    }

    public static Stream<FurnitureBlock> streamWoodCabinets() {
        return WOOD_CABINETS.stream();
    }
    public static Stream<FurnitureBlock> streamStoneCabinets() {
        return STONE_CABINETS.stream();
    }

    public static final BooleanProperty OPEN = Properties.OPEN;

    protected static final VoxelShape STRAIGHT = VoxelShapes.union(createCuboidShape(0, 0, 0,16, 16, 8), createCuboidShape(0, 1, 8,16, 16, 9), createCuboidShape(6, 3, 9,7, 7, 10), createCuboidShape(9, 3, 9,10, 7, 10));
    protected static final VoxelShape INNER_CORNER = VoxelShapes.union(createCuboidShape(0, 0, 0,8, 16, 8),createCuboidShape(1, 3, 9,2, 7, 10), createCuboidShape(0, 1, 8,8, 16, 9),createCuboidShape(7, 1, 9,8, 16, 16),createCuboidShape(8, 0, 0,16, 16, 16),createCuboidShape(6, 3, 13,7, 7, 14));
    protected static final VoxelShape OUTER_CORNER = VoxelShapes.union(createCuboidShape(0, 0, 0,8, 16, 8),createCuboidShape(6, 3, 9,7, 7, 10),createCuboidShape(0, 1, 8,8, 16, 9),createCuboidShape(8, 1, 0,9, 16, 8),createCuboidShape(9, 3, 6,10, 7, 7));

    protected static final VoxelShape STRAIGHT_OPEN = VoxelShapes.union(createCuboidShape(0, 0, 0,16, 16, 8), createCuboidShape(16, 3, 14,17, 7, 15), createCuboidShape(15, 1, 8,16, 16, 16), createCuboidShape(-1, 3, 14,0, 7, 15),createCuboidShape(0, 1, 8,1, 16, 16));
    protected static final VoxelShape INNER_CORNER_OPEN = VoxelShapes.union(createCuboidShape(7, 1, 9,8, 16, 16),createCuboidShape(8, 0, 8,16, 16, 16), createCuboidShape(6, 3, 13,7, 7, 14),createCuboidShape(0, 0, 0,16, 16, 8));
    protected static final VoxelShape OUTER_CORNER_OPEN = VoxelShapes.union(createCuboidShape(0, 0, 0,8, 16, 8),createCuboidShape(0, 1, 8,1, 16, 16),createCuboidShape(-1, 3, 14,0, 7, 15),createCuboidShape(8, 1, 0,9, 16, 8),createCuboidShape(9, 3, 6,10, 7, 7));

    protected static final VoxelShape STRAIGHT_SOUTH = rotateShape(Direction.NORTH, Direction.SOUTH, STRAIGHT);
    protected static final VoxelShape STRAIGHT_EAST = rotateShape(Direction.NORTH, Direction.EAST, STRAIGHT);
    protected static final VoxelShape STRAIGHT_WEST = rotateShape(Direction.NORTH, Direction.WEST, STRAIGHT);
    protected static final VoxelShape STRAIGHT_OPEN_SOUTH = rotateShape(Direction.NORTH, Direction.SOUTH, STRAIGHT_OPEN);
    protected static final VoxelShape STRAIGHT_OPEN_EAST = rotateShape(Direction.NORTH, Direction.EAST, STRAIGHT_OPEN);
    protected static final VoxelShape STRAIGHT_OPEN_WEST = rotateShape(Direction.NORTH, Direction.WEST, STRAIGHT_OPEN);

    protected static final VoxelShape INNER_CORNER_SOUTH = rotateShape(Direction.NORTH, Direction.SOUTH, INNER_CORNER);
    protected static final VoxelShape INNER_CORNER_EAST = rotateShape(Direction.NORTH, Direction.EAST, INNER_CORNER);
    protected static final VoxelShape INNER_CORNER_WEST = rotateShape(Direction.NORTH, Direction.WEST, INNER_CORNER);
    protected static final VoxelShape INNER_CORNER_OPEN_SOUTH = rotateShape(Direction.NORTH, Direction.SOUTH, INNER_CORNER_OPEN);
    protected static final VoxelShape INNER_CORNER_OPEN_EAST = rotateShape(Direction.NORTH, Direction.EAST, INNER_CORNER_OPEN);
    protected static final VoxelShape INNER_CORNER_OPEN_WEST = rotateShape(Direction.NORTH, Direction.WEST, INNER_CORNER_OPEN);

    protected static final VoxelShape OUTER_CORNER_SOUTH = rotateShape(Direction.NORTH, Direction.SOUTH, OUTER_CORNER);
    protected static final VoxelShape OUTER_CORNER_EAST = rotateShape(Direction.NORTH, Direction.EAST, OUTER_CORNER);
    protected static final VoxelShape OUTER_CORNER_WEST = rotateShape(Direction.NORTH, Direction.WEST, OUTER_CORNER);
    protected static final VoxelShape OUTER_CORNER_OPEN_SOUTH = rotateShape(Direction.NORTH, Direction.SOUTH, OUTER_CORNER_OPEN);
    protected static final VoxelShape OUTER_CORNER_OPEN_EAST = rotateShape(Direction.NORTH, Direction.EAST, OUTER_CORNER_OPEN);
    protected static final VoxelShape OUTER_CORNER_OPEN_WEST = rotateShape(Direction.NORTH, Direction.WEST, OUTER_CORNER_OPEN);

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(Properties.HORIZONTAL_FACING);
        stateManager.add(OPEN);
    }
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
        Direction direction = state.get(KitchenCounterBlock.FACING);
        BlockState neighborStateFacing = view.getBlockState(pos.offset(direction));
        BlockState neighborStateOpposite = view.getBlockState(pos.offset(direction.getOpposite()));
        boolean open = state.get(OPEN);
        if (isCabinet(neighborStateFacing) && neighborStateFacing.getProperties().contains(Properties.HORIZONTAL_FACING)) {
            Direction direction2 = neighborStateFacing.get(Properties.HORIZONTAL_FACING);
            if (direction2.getAxis() != state.get(Properties.HORIZONTAL_FACING).getAxis() && isDifferentOrientation(state, view, pos, direction2.getOpposite())) {
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
        else if (isCabinet(neighborStateOpposite) && neighborStateOpposite.getProperties().contains(Properties.HORIZONTAL_FACING)) {
            Direction direction3;
            if (neighborStateOpposite.getBlock() instanceof AbstractFurnaceBlock) {
                direction3 = neighborStateOpposite.get(Properties.HORIZONTAL_FACING).getOpposite();
            }
            else {
                direction3 = neighborStateOpposite.get(Properties.HORIZONTAL_FACING);
            }
            if (direction3.getAxis() != state.get(Properties.HORIZONTAL_FACING).getAxis() && isDifferentOrientation(state, view, pos, direction3)) {
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
        else {
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
    public boolean isCabinet(BlockState state) {
        return state.getBlock() instanceof KitchenCabinetBlock;
    }

    public boolean isDifferentOrientation(BlockState state, BlockView world, BlockPos pos, Direction dir) {
        BlockState blockState = world.getBlockState(pos.offset(dir));
        return !this.isCabinet(blockState);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }
    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!state.isOf(state.getBlock())) {
            this.baseBlockState.neighborUpdate(world, pos, Blocks.AIR, pos, false);
            this.baseBlock.onBlockAdded(this.baseBlockState, world, pos, oldState, false);
        }
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing());
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof GenericStorageBlockEntity9x3) {
            player.openHandledScreen((GenericStorageBlockEntity9x3)blockEntity);
            player.incrementStat(Statistics.CABINET_SEARCHED);
            PiglinBrain.onGuardedBlockInteracted(player, true);
        }
        return ActionResult.CONSUME;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public boolean isShapeFullCube(BlockState state, BlockView world, BlockPos pos) {
        return false;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        BlockEntity blockEntity;
        if (itemStack.hasCustomName() && (blockEntity = world.getBlockEntity(pos)) instanceof GenericStorageBlockEntity9x3) {
            ((GenericStorageBlockEntity9x3)blockEntity).setCustomName(itemStack.getName());
        }
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

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos));
    }

    @Override
    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return false;
    }
}
