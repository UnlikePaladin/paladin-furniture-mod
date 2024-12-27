package com.unlikepaladin.pfm.blocks;

import com.mojang.serialization.MapCodec;
import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.data.FurnitureBlock;
import net.minecraft.block.*;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class KitchenCounterBlock extends HorizontalFacingBlock {
    private float height = 0.36f;
    private final Block baseBlock;
    protected FurnitureBlock counterFurnitureBlock;
    private final BlockState baseBlockState;
    private static final List<FurnitureBlock> WOOD_COUNTERS = new ArrayList<>();
    private static final List<FurnitureBlock> STONE_COUNTERS = new ArrayList<>();
    public KitchenCounterBlock(Settings settings) {
        super(settings.luminance((state) -> 0).emissiveLighting((blockstate, b, c) -> false));
        setDefaultState(this.getStateManager().getDefaultState().with(Properties.HORIZONTAL_FACING, Direction.NORTH));
        this.baseBlockState = this.getDefaultState();
        this.baseBlock = baseBlockState.getBlock();
        counterFurnitureBlock = new FurnitureBlock(this, "kitchen_counter");
        if(AbstractSittableBlock.isWoodBased(this.getDefaultState()) && this.getClass().isAssignableFrom(KitchenCounterBlock.class)){
            WOOD_COUNTERS.add(counterFurnitureBlock);
        }
        else if (this.getClass().isAssignableFrom(KitchenCounterBlock.class)){
            STONE_COUNTERS.add(counterFurnitureBlock);
        }
    }

    public static final MapCodec<KitchenCounterBlock> CODEC = createCodec(KitchenCounterBlock::new);
    @Override
    protected MapCodec<? extends HorizontalFacingBlock> getCodec() {
        return CODEC;
    }

    public static Stream<FurnitureBlock> streamWoodCounters() {
        return WOOD_COUNTERS.stream();
    }
    public static Stream<FurnitureBlock> streamStoneCounters() {
        return STONE_COUNTERS.stream();
    }

    @Override
    public boolean isShapeFullCube(BlockState state, BlockView world, BlockPos pos) {
        return false;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(Properties.HORIZONTAL_FACING);
    }

    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing());
    }

    public boolean canConnect(BlockView world, BlockPos pos, Direction direction)
    {
        BlockState state = world.getBlockState(pos.offset(direction));
        return (isCounter(state) || state.getBlock() instanceof AbstractFurnaceBlock || state.getBlock() instanceof AbstractCauldronBlock) || isCookingForBlockHeadsBlock(state.getBlock().getTranslationKey());
    }

    public static boolean isCookingForBlockHeadsBlock(String key) {
        return key.contains("cookingforblockheads") && (key.contains("cooking_table") || key.contains("oven") || key.contains("sink") || key.contains("corner") || key.contains("cabinet") || key.contains("counter"));
    }

    public boolean isDifferentOrientation(BlockState state, BlockView world, BlockPos pos, Direction dir) {
        BlockState blockState = world.getBlockState(pos.offset(dir));
        return !canConnectToCounter(blockState); //|| blockState.get(FACING) != state.get(FACING);
    }

    public boolean canConnectToCounter(BlockState state) {
        return isCounter(state) || state.getBlock() instanceof AbstractFurnaceBlock || state.getBlock() instanceof AbstractCauldronBlock || isCookingForBlockHeadsBlock(state.getBlock().getTranslationKey());
    }

    public boolean isCounter(BlockState state) {
        boolean doDifferentCountersConnect;
        if (PaladinFurnitureMod.getPFMConfig().doCountersOfDifferentMaterialsConnect()) {
            doDifferentCountersConnect = state.getBlock() instanceof KitchenCounterBlock;
        }
        else if (state.getBlock() instanceof KitchenCounterBlock){
            doDifferentCountersConnect = (this.counterFurnitureBlock.getBaseMaterial() == ((KitchenCounterBlock)state.getBlock()).counterFurnitureBlock.getBaseMaterial());
        }
        else {
            doDifferentCountersConnect = false;
        }
        return (doDifferentCountersConnect || state.getBlock() instanceof KitchenWallCounterBlock || state.getBlock() instanceof KitchenWallDrawerBlock);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @SuppressWarnings("deprecated")
    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!state.isOf(state.getBlock())) {
            oldState.neighborUpdate(world, pos, Blocks.AIR, pos, false);
        }
    }
    /**
     * Method to rotate VoxelShapes from this random Forge Forums thread: https://forums.minecraftforge.net/topic/74979-1144-rotate-voxel-shapes/
     */
    public static VoxelShape rotateShape(Direction from, Direction to, VoxelShape shape) {
        VoxelShape[] buffer = new VoxelShape[]{shape, VoxelShapes.empty()};

        int times = (to.getHorizontal() - from.getHorizontal() + 4) % 4;
        for (int i = 0; i < times; i++) {
            buffer[0].forEachBox((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] = VoxelShapes.union(buffer[1], VoxelShapes.cuboid(1 - maxZ, minY, minX, 1 - minZ, maxY, maxX)));
            buffer[0] = buffer[1];
            buffer[1] = VoxelShapes.empty();
        }
        return buffer[0];
    }

    protected static final VoxelShape STRAIGHT = VoxelShapes.union(createCuboidShape(0, 0, 0,16, 1, 12), createCuboidShape(0, 1, 0,16, 14, 13), createCuboidShape(0, 14, 0,16, 16, 16));
    protected static final VoxelShape INNER_CORNER = VoxelShapes.union(createCuboidShape(0, 14, 0,16, 16, 16),createCuboidShape(0, 1, 0,16, 14, 13),createCuboidShape(3, 1, 13,16, 14, 16));
    protected static final VoxelShape OUTER_CORNER = VoxelShapes.union(createCuboidShape(0, 14, 0,16, 16, 16),createCuboidShape(0, 1, 0,13, 14, 13),createCuboidShape(0, 0, 0,12, 1, 12));
    protected static final VoxelShape LEFT_EDGE = VoxelShapes.union(createCuboidShape(2, 0, 0,16, 1, 12), createCuboidShape(2, 1, 0,16, 14, 13), createCuboidShape(0, 0, 0,2, 14, 16),createCuboidShape(0, 14, 0,16, 16, 16));
    protected static final VoxelShape RIGHT_EDGE = VoxelShapes.union(createCuboidShape(0, 0, 0,14, 1, 12), createCuboidShape(0, 1, 0,14, 14, 13), createCuboidShape(14, 0, 0,16, 14, 16),createCuboidShape(0, 14, 0,16, 16, 16));
    protected static final VoxelShape MIDDLE = VoxelShapes.union(createCuboidShape(0, 0, 0, 16, 16, 13));
    protected static final VoxelShape INNER_MIDDLE = VoxelShapes.union(createCuboidShape(0, 0, 0, 16, 16, 13), createCuboidShape(3, 0, 13,16, 16, 16));
    protected static final VoxelShape OUTER_MIDDLE = VoxelShapes.union(createCuboidShape(0, 0, 0,13, 16, 13));

    protected static final VoxelShape MIDDLE_SOUTH = rotateShape(Direction.NORTH, Direction.SOUTH, MIDDLE);
    protected static final VoxelShape MIDDLE_EAST = rotateShape(Direction.NORTH, Direction.EAST, MIDDLE);
    protected static final VoxelShape MIDDLE_WEST = rotateShape(Direction.NORTH, Direction.WEST, MIDDLE);

    protected static final VoxelShape INNER_MIDDLE_SOUTH = rotateShape(Direction.NORTH, Direction.SOUTH, INNER_MIDDLE);
    protected static final VoxelShape INNER_MIDDLE_EAST = rotateShape(Direction.NORTH, Direction.EAST, INNER_MIDDLE);
    protected static final VoxelShape INNER_MIDDLE_WEST = rotateShape(Direction.NORTH, Direction.WEST, INNER_MIDDLE);

    protected static final VoxelShape OUTER_MIDDLE_SOUTH = rotateShape(Direction.NORTH, Direction.SOUTH, OUTER_MIDDLE);
    protected static final VoxelShape OUTER_MIDDLE_EAST = rotateShape(Direction.NORTH, Direction.EAST, OUTER_MIDDLE);
    protected static final VoxelShape OUTER_MIDDLE_WEST = rotateShape(Direction.NORTH, Direction.WEST, OUTER_MIDDLE);


    protected static final VoxelShape STRAIGHT_SOUTH = rotateShape(Direction.NORTH, Direction.SOUTH, STRAIGHT);
    protected static final VoxelShape STRAIGHT_EAST = rotateShape(Direction.NORTH, Direction.EAST, STRAIGHT);
    protected static final VoxelShape STRAIGHT_WEST = rotateShape(Direction.NORTH, Direction.WEST, STRAIGHT);

    protected static final VoxelShape INNER_CORNER_SOUTH = rotateShape(Direction.NORTH, Direction.SOUTH, INNER_CORNER);
    protected static final VoxelShape INNER_CORNER_EAST = rotateShape(Direction.NORTH, Direction.EAST, INNER_CORNER);
    protected static final VoxelShape INNER_CORNER_WEST = rotateShape(Direction.NORTH, Direction.WEST, INNER_CORNER);

    protected static final VoxelShape OUTER_CORNER_SOUTH =  rotateShape(Direction.NORTH, Direction.SOUTH, OUTER_CORNER);
    protected static final VoxelShape OUTER_CORNER_EAST = rotateShape(Direction.NORTH, Direction.EAST, OUTER_CORNER);
    protected static final VoxelShape OUTER_CORNER_WEST = rotateShape(Direction.NORTH, Direction.WEST, OUTER_CORNER);

    protected static final VoxelShape LEFT_EDGE_SOUTH =  rotateShape(Direction.NORTH, Direction.SOUTH, LEFT_EDGE);
    protected static final VoxelShape LEFT_EDGE_EAST = rotateShape(Direction.NORTH, Direction.EAST, LEFT_EDGE);
    protected static final VoxelShape LEFT_EDGE_WEST = rotateShape(Direction.NORTH, Direction.WEST, LEFT_EDGE);

    protected static final VoxelShape RIGHT_EDGE_SOUTH =  rotateShape(Direction.NORTH, Direction.SOUTH, RIGHT_EDGE);
    protected static final VoxelShape RIGHT_EDGE_EAST = rotateShape(Direction.NORTH, Direction.EAST, RIGHT_EDGE);
    protected static final VoxelShape RIGHT_EDGE_WEST = rotateShape(Direction.NORTH, Direction.WEST, RIGHT_EDGE);
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Direction direction = state.get(KitchenCounterBlock.FACING);
        boolean right = canConnect(world, pos, state.get(KitchenCounterBlock.FACING).rotateYCounterclockwise());
        boolean left = canConnect(world, pos, state.get(KitchenCounterBlock.FACING).rotateYClockwise());
        BlockState neighborStateFacing = world.getBlockState(pos.offset(direction));
        BlockState neighborStateOpposite = world.getBlockState(pos.offset(direction.getOpposite()));
        if (canConnectToCounter(neighborStateFacing) && neighborStateFacing.getProperties().contains(Properties.HORIZONTAL_FACING)) {
            Direction direction2 = neighborStateFacing.get(Properties.HORIZONTAL_FACING);
            if (direction2.getAxis() != state.get(Properties.HORIZONTAL_FACING).getAxis() && isDifferentOrientation(state, world, pos, direction2.getOpposite())) {
                if (direction2 == direction.rotateYCounterclockwise()) {
                    switch (direction) {
                        case NORTH: return OUTER_CORNER;
                        case SOUTH: return OUTER_CORNER_SOUTH;
                        case EAST: return OUTER_CORNER_EAST;
                        default: return OUTER_CORNER_WEST;
                    }
                }
                else {
                    switch (direction) {
                        case NORTH: return OUTER_CORNER_EAST;
                        case SOUTH: return OUTER_CORNER_WEST;
                        case EAST: return OUTER_CORNER_SOUTH;
                        default: return OUTER_CORNER;
                    }
                }
            } else {
                return getMiddleShape(direction, left, right);
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
                return getMiddleShape(direction, left, right);
            }
        }
        else {
            return getMiddleShape(direction, left, right);
        }
    }

    private VoxelShape getMiddleShape(Direction direction, boolean left, boolean right) {
        if (left && right) {
            switch (direction) {
                case NORTH: return STRAIGHT;
                case SOUTH: return STRAIGHT_SOUTH;
                case EAST: return STRAIGHT_EAST;
                default: return STRAIGHT_WEST;
            }
        } else if (left) {
            switch (direction) {
                case NORTH: return LEFT_EDGE;
                case SOUTH: return LEFT_EDGE_SOUTH;
                case EAST: return LEFT_EDGE_EAST;
                default: return LEFT_EDGE_WEST;
            }
        } else if (right) {
            switch (direction) {
                case NORTH: return RIGHT_EDGE;
                case SOUTH: return RIGHT_EDGE_SOUTH;
                case EAST: return RIGHT_EDGE_EAST;
                default: return RIGHT_EDGE_WEST;
            }
        } else {
            switch (direction) {
                case NORTH: return STRAIGHT;
                case SOUTH: return STRAIGHT_SOUTH;
                case EAST: return STRAIGHT_EAST;
                default: return STRAIGHT_WEST;
            }
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
}