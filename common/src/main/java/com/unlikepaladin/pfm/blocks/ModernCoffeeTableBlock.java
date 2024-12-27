package com.unlikepaladin.pfm.blocks;

import com.mojang.serialization.MapCodec;
import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.data.FurnitureBlock;
import net.minecraft.block.*;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
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

public class ModernCoffeeTableBlock extends Block {
    private final Block baseBlock;
    public static final EnumProperty<Direction.Axis> AXIS = Properties.HORIZONTAL_AXIS;
    private static final List<FurnitureBlock> WOOD_COFFEE_MODERN_TABLES = new ArrayList<>();
    private static final List<FurnitureBlock> STONE_COFEEE_MODERN_TABLES = new ArrayList<>();
    private final BlockState baseBlockState;

    public ModernCoffeeTableBlock(Settings settings) {
        super(settings.luminance((state) -> 0).emissiveLighting((blockstate, b, c) -> false));
        setDefaultState(this.getStateManager().getDefaultState().with(AXIS, Direction.Axis.X));
        this.baseBlockState = this.getDefaultState();
        this.baseBlock = baseBlockState.getBlock();
        if(AbstractSittableBlock.isWoodBased(this.getDefaultState()) && this.getClass().isAssignableFrom(ModernCoffeeTableBlock.class)){
            WOOD_COFFEE_MODERN_TABLES.add(new FurnitureBlock(this, "coffee_table_modern"));
        }
        else if (this.getClass().isAssignableFrom(ModernCoffeeTableBlock.class)){
            STONE_COFEEE_MODERN_TABLES.add(new FurnitureBlock(this, "coffee_table_modern"));
        }
    }

    public static Stream<FurnitureBlock> streamWoodModernCoffeeTables() {
        return WOOD_COFFEE_MODERN_TABLES.stream();
    }
    public static Stream<FurnitureBlock> streamStoneModernCoffeeTables() {
        return STONE_COFEEE_MODERN_TABLES.stream();
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(AXIS);
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!state.isOf(state.getBlock())) {
            this.baseBlockState.neighborUpdate(world, pos, Blocks.AIR, pos, false);
            this.baseBlock.onBlockAdded(this.baseBlockState, world, pos, oldState, false);
        }
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(AXIS, ctx.getHorizontalPlayerFacing().rotateYClockwise().getAxis());
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    boolean canConnect(BlockState blockState)
    {
        return PaladinFurnitureMod.getPFMConfig().doTablesOfDifferentMaterialsConnect() ? blockState.getBlock() instanceof ModernCoffeeTableBlock : blockState.getBlock() == this;
    }

    public boolean isTable(BlockView world, BlockPos pos, Direction.Axis direction, int i)
    {
        BlockState state = world.getBlockState(pos.offset(direction, i));
        if(canConnect(state))
        {
            Direction.Axis sourceDirection = state.get(AXIS);
            return sourceDirection.equals(direction);
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

    final static VoxelShape MODERN_COFFEE_TABLE = VoxelShapes.union(createCuboidShape(0, 8, 0, 16, 10, 16), createCuboidShape(12, 0, 12, 14, 8, 14), createCuboidShape(12, 0, 2, 14, 8, 4), createCuboidShape(13, 2, 7,15, 8, 9), createCuboidShape(1, 2, 7, 3, 8, 9),createCuboidShape(2, 0, 2,4, 8, 4),createCuboidShape(2, 0, 4, 4, 2, 12), createCuboidShape(3, 2, 7,13, 4, 9),createCuboidShape(12, 0, 4,14, 2, 12), createCuboidShape(2, 0, 12,4, 8, 14));
    final static VoxelShape MODERN_COFFEE_TABLE_MIDDLE = VoxelShapes.union(createCuboidShape(0, 8, 0, 16, 10, 16),createCuboidShape(0, 2, 7,16, 4, 9 ));
    final static VoxelShape MODERN_COFFEE_TABLE_ONE = VoxelShapes.union(createCuboidShape(0, 8, 0, 16, 10, 16), createCuboidShape(13, 2, 7, 15, 8, 9), createCuboidShape(12, 0, 12,14, 8, 14), createCuboidShape(12, 0, 4,14, 2, 12 ), createCuboidShape(0, 2, 7,13, 4, 9), createCuboidShape(12, 0, 2,14, 8, 4 ));
    final static VoxelShape MODERN_COFFEE_TABLE_ONE_SOUTH = rotateShape(Direction.NORTH, Direction.SOUTH, MODERN_COFFEE_TABLE_ONE);
    final static VoxelShape MODERN_COFFEE_TABLE_ONE_WEST = rotateShape(Direction.NORTH, Direction.WEST, MODERN_COFFEE_TABLE_ONE);
    final static VoxelShape MODERN_COFFEE_TABLE_ONE_EAST = rotateShape(Direction.NORTH, Direction.EAST, MODERN_COFFEE_TABLE_ONE);
    final static VoxelShape MODERN_COFFEE_TABLE_MIDDLE_EAST = rotateShape(Direction.NORTH, Direction.EAST, MODERN_COFFEE_TABLE_MIDDLE);
    final static VoxelShape MODERN_COFFEE_TABLE_EAST = rotateShape(Direction.NORTH, Direction.EAST, MODERN_COFFEE_TABLE);

    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
        Direction.Axis dir = state.get(AXIS);
        boolean dirNorthOrSouth = dir.equals(Direction.Axis.X);
        boolean dirWestOrEast = dir.equals(Direction.Axis.Z);
        boolean left = isTable(view, pos, dir, -1);
        boolean right = isTable(view, pos, dir, 1);

        if (left && right) {
            if (dirNorthOrSouth) {
                return MODERN_COFFEE_TABLE_MIDDLE;
            } else {
                return MODERN_COFFEE_TABLE_MIDDLE_EAST;
            }
        }
        else if (right) {
            if (dirNorthOrSouth) {
                return MODERN_COFFEE_TABLE_ONE_SOUTH;
            } else {
                return MODERN_COFFEE_TABLE_ONE_WEST;
            }
        }
        else if (left) {
            if (dirNorthOrSouth) {
                return MODERN_COFFEE_TABLE_ONE;
            } else {
                return MODERN_COFFEE_TABLE_ONE_EAST;
            }
        }
        else {
            if (dirWestOrEast) {
                return MODERN_COFFEE_TABLE_EAST;}
            else {
                return MODERN_COFFEE_TABLE;
            }
        }
    }

    @Override
    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return false;
    }

    @Override
    protected MapCodec<? extends Block> getCodec() {
        return CODEC;
    }
}


