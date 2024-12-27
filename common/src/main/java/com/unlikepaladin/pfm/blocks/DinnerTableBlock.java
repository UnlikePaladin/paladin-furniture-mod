package com.unlikepaladin.pfm.blocks;

import com.mojang.serialization.MapCodec;
import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.data.FurnitureBlock;
import net.minecraft.block.*;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class DinnerTableBlock extends HorizontalFacingBlock  {

    private final Block baseBlock;
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    private final BlockState baseBlockState;

    private static final List<FurnitureBlock> WOOD_DINNER_TABLES = new ArrayList<>();
    private static final List<FurnitureBlock> STONE_DINNER_TABLES = new ArrayList<>();
    public static final MapCodec<DinnerTableBlock> CODEC = createCodec(DinnerTableBlock::new);

    public DinnerTableBlock(Settings settings) {
        super(settings.luminance((state) -> 0).emissiveLighting((blockstate, b, c) -> false));
        setDefaultState(this.getStateManager().getDefaultState().with(FACING, Direction.NORTH));
        this.baseBlockState = this.getDefaultState();
        this.baseBlock = baseBlockState.getBlock();
        if(AbstractSittableBlock.isWoodBased(this.getDefaultState()) && this.getClass().isAssignableFrom(DinnerTableBlock.class)){
            WOOD_DINNER_TABLES.add(new FurnitureBlock(this, "table_dinner"));
        }
        else if (this.getClass().isAssignableFrom(DinnerTableBlock.class)){
            STONE_DINNER_TABLES.add(new FurnitureBlock(this, "table_dinner"));
        }
    }

    @Override
    protected MapCodec<? extends HorizontalFacingBlock> getCodec() {
        return CODEC;
    }

    public static Stream<FurnitureBlock> streamWoodDinnerTables() {
        return WOOD_DINNER_TABLES.stream();
    }
    public static Stream<FurnitureBlock> streamStoneDinnerTables() {
        return STONE_DINNER_TABLES.stream();
    }

    @Override
    public boolean isShapeFullCube(BlockState state, BlockView world, BlockPos pos) {
        return false;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(FACING);
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!state.isOf(state.getBlock())) {
            oldState.neighborUpdate(world, pos, Blocks.AIR, pos, false);
        }
    }


    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing());
    }
    @Override
    public FluidState getFluidState(BlockState state) {
        return super.getFluidState(state);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    boolean canConnect(BlockState blockState)
    {
        return PaladinFurnitureMod.getPFMConfig().doTablesOfDifferentMaterialsConnect() ? blockState.getBlock() instanceof DinnerTableBlock : blockState.getBlock() == this;
    }

    public boolean isTable(BlockView world, BlockPos pos, Direction direction, Direction tableDirection)
    {
        BlockState state = world.getBlockState(pos.offset(direction));
        if(canConnect(state))
        {
            Direction sourceDirection = state.get(FACING);
            return sourceDirection.equals(tableDirection);
        }
        return false;
    }

    public int getFlammability(BlockState state, BlockView world, BlockPos pos, Direction face) {
        if (AbstractSittableBlock.isWoodBased(state)) {
            return 20;
        }
        return 0;
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

    final static VoxelShape dinner_table = VoxelShapes.union(createCuboidShape(0, 14, 0, 16, 16, 16), createCuboidShape(0.1, 0, 2, 15.8, 14, 4.05), createCuboidShape(0.1, 0, 11.9, 15.8, 14, 13.95));
    final static VoxelShape dinner_table_middle = VoxelShapes.union(createCuboidShape(0, 14, 0, 16, 16, 16));
    final static VoxelShape dinner_table_one_east = VoxelShapes.union(createCuboidShape(0, 14, 0, 16, 16, 16), createCuboidShape(0.1, 0, 2, 15.8, 14, 4.05));
    final static VoxelShape dinner_table_one_south = rotateShape(Direction.NORTH, Direction.WEST, dinner_table_one_east);
    final static VoxelShape dinner_table_one = rotateShape(Direction.NORTH, Direction.EAST, dinner_table_one_east);
    final static VoxelShape dinner_table_one_west = rotateShape(Direction.NORTH, Direction.SOUTH, dinner_table_one_east);
    final static VoxelShape dinner_table_east = rotateShape(Direction.NORTH, Direction.EAST, dinner_table);

    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
        Direction dir = state.get(FACING);
        boolean dirNorthOrSouth = dir.equals(Direction.NORTH) || dir.equals(Direction.SOUTH);
        boolean dirWestOrEast = dir.equals(Direction.WEST) || dir.equals(Direction.EAST);
        boolean left = isTable(view, pos, dir.rotateYCounterclockwise(), dir);
        boolean right = isTable(view, pos, dir.rotateYClockwise(), dir);

        if (left && right) {
            return dinner_table_middle;
        }
        else if (left) {
            if (dirNorthOrSouth) {
                return dinner_table_one;}
            else if (dirWestOrEast) {
                return dinner_table_one_east;}
            else {
                return dinner_table;
            }
        }
        else if (right) {
            if (dirNorthOrSouth) {
                return dinner_table_one_south;}
            else if (dirWestOrEast) {
                return dinner_table_one_west;}
            else {
                return dinner_table;
            }
        }
        else {
            if (dirWestOrEast) {
                return dinner_table;}
            else {
                return dinner_table_east;
            }
        }
    }
}


