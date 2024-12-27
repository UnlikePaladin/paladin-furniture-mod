package com.unlikepaladin.pfm.blocks;

import com.mojang.serialization.MapCodec;
import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.data.FurnitureBlock;
import net.minecraft.block.*;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
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
public class LogTableBlock extends HorizontalFacingBlock {
    private final Block baseBlock;
    public static final EnumProperty<Direction> FACING = Properties.HORIZONTAL_FACING;

    private final BlockState baseBlockState;
    private static final List<FurnitureBlock> WOOD_LOG_TABLES = new ArrayList<>();
    private static final List<FurnitureBlock> STONE_NATURAL_TABLES = new ArrayList<>();
    public static final MapCodec<LogTableBlock> CODEC = createCodec(LogTableBlock::new);
    public LogTableBlock(Settings settings) {
        super(settings.luminance((state) -> 0).emissiveLighting((blockstate, b, c) -> false));
        setDefaultState(this.getStateManager().getDefaultState().with(FACING, Direction.NORTH));
        this.baseBlockState = this.getDefaultState();
        this.baseBlock = baseBlockState.getBlock();
        if(AbstractSittableBlock.isWoodBased(this.getDefaultState()) && this.getClass().isAssignableFrom(LogTableBlock.class)){
            WOOD_LOG_TABLES.add(new FurnitureBlock(this, "table_"));
        }
        else if (this.getClass().isAssignableFrom(LogTableBlock.class)){
            STONE_NATURAL_TABLES.add(new FurnitureBlock(this, "natural_table"));
        }
    }

    @Override
    protected MapCodec<? extends HorizontalFacingBlock> getCodec() {
        return CODEC;
    }

    public static Stream<FurnitureBlock> streamWoodLogTables() {
        return WOOD_LOG_TABLES.stream();
    }
    public static Stream<FurnitureBlock> streamStoneNaturalTables() {
        return STONE_NATURAL_TABLES.stream();
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(FACING);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return super.getFluidState(state);
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing());
    }


    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
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

    public boolean canConnect(BlockState blockState)
    {
        return PaladinFurnitureMod.getPFMConfig().doTablesOfDifferentMaterialsConnect() ? blockState.getBlock() instanceof LogTableBlock : blockState.getBlock() == this;
    }

    @Override
    public boolean isShapeFullCube(BlockState state, BlockView world, BlockPos pos) {
        return false;
    }

    /**
     * Method to rotate VoxelShapes from this random Forge Forums thread: https://forums.minecraftforge.net/topic/74979-1144-rotate-voxel-shapes/
     */
    public static VoxelShape rotateShape(Direction from, Direction to, VoxelShape shape) {
        VoxelShape[] buffer = new VoxelShape[]{shape, VoxelShapes.empty()};

        int times = (to.getHorizontalQuarterTurns() - from.getHorizontalQuarterTurns() + 4) % 4;
        for (int i = 0; i < times; i++) {
            buffer[0].forEachBox((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] = VoxelShapes.union(buffer[1], VoxelShapes.cuboid(1 - maxZ, minY, minX, 1 - minZ, maxY, maxX)));
            buffer[0] = buffer[1];
            buffer[1] = VoxelShapes.empty();
        }

        return buffer[0];
    }

    final static VoxelShape LOG_TABLE = VoxelShapes.union(createCuboidShape(0, 14, 0, 16, 16, 16), createCuboidShape(2, 0, 5, 4.5, 14, 11), createCuboidShape(11.5, 0, 5, 14, 14, 11));
    final static VoxelShape LOG_TABLE_MIDDLE = VoxelShapes.union(createCuboidShape(0, 14, 0, 16, 16, 16));
    final static VoxelShape LOG_TABLE_ONE = VoxelShapes.union(createCuboidShape(0, 14, 0, 16, 16, 16), createCuboidShape(6, 0, 5, 8.5, 14, 11));
    final static VoxelShape LOG_TABLE_ONE_WEST = rotateShape(Direction.NORTH, Direction.WEST, LOG_TABLE_ONE);
    final static VoxelShape LOG_TABLE_ONE_SOUTH = rotateShape(Direction.NORTH, Direction.SOUTH, LOG_TABLE_ONE);
    final static VoxelShape LOG_TABLE_ONE_EAST = rotateShape(Direction.NORTH, Direction.EAST, LOG_TABLE_ONE);
    final static VoxelShape LOG_TABLE_EAST = rotateShape(Direction.NORTH, Direction.EAST, LOG_TABLE);
    // Cursed I know
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
        Direction dir = state.get(FACING);
        boolean left = isTable(view, pos, dir.rotateYCounterclockwise(), dir);
        boolean right = isTable(view, pos, dir.rotateYClockwise(), dir);
        boolean dirNorthOrSouth = dir.equals(Direction.NORTH) || dir.equals(Direction.SOUTH);
        boolean dirWestOrEast = dir.equals(Direction.WEST) || dir.equals(Direction.EAST);
        if (left && right) {
            return LOG_TABLE_MIDDLE;
        }
        else if (left) {
            if (dirNorthOrSouth) {
                return LOG_TABLE_ONE;}
            else if (dirWestOrEast) {
                return LOG_TABLE_ONE_WEST;}
            else {
                return LOG_TABLE;
            }
        }
        else if (right) {
            if (dirNorthOrSouth) {
                return LOG_TABLE_ONE_SOUTH;}
            else if (dirWestOrEast) {
                return LOG_TABLE_ONE_EAST;}
            else {
                return LOG_TABLE;
            }
        }
        else {
            if (dirWestOrEast) {
                return LOG_TABLE_EAST;}
            else {
                return LOG_TABLE;
            }
        }
    }

    @Override
    public boolean canPathfindThrough(BlockState state, NavigationType type) {
        return false;
    }
}


