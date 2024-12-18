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
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class BasicCoffeeTableBlock extends Block {
    private final Block baseBlock;
    public static final EnumProperty<Direction.Axis> AXIS = Properties.HORIZONTAL_AXIS;
    private final BlockState baseBlockState;
    private static final List<FurnitureBlock> WOOD_BASIC_TABLES_COFFEE = new ArrayList<>();
    private static final List<FurnitureBlock> STONE_BASIC_TABLES_COFFEE = new ArrayList<>();
    public BasicCoffeeTableBlock(Settings settings) {
        super(settings);
        setDefaultState(this.getStateManager().getDefaultState().with(AXIS, Direction.Axis.X));
        this.baseBlockState = this.getDefaultState();
        this.baseBlock = baseBlockState.getBlock();
        if(AbstractSittableBlock.isWoodBased(baseBlockState) && this.getClass().isAssignableFrom(BasicCoffeeTableBlock.class)){
            WOOD_BASIC_TABLES_COFFEE.add(new FurnitureBlock(this, "coffee_table_basic"));
        }
        else if (this.getClass().isAssignableFrom(BasicCoffeeTableBlock.class)){
            STONE_BASIC_TABLES_COFFEE.add(new FurnitureBlock(this, "coffee_table_basic"));
        }
    }

    public static Stream<FurnitureBlock> streamWoodBasicTables() {
        return WOOD_BASIC_TABLES_COFFEE.stream();
    }
    public static Stream<FurnitureBlock> streamStoneBasicTables() {
        return STONE_BASIC_TABLES_COFFEE.stream();
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(AXIS);
    }

    @Override
    public boolean isShapeFullCube(BlockState state, BlockView world, BlockPos pos) {
        return false;
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction.Axis facing = ctx.getHorizontalPlayerFacing().getAxis();
        return this.getDefaultState().with(AXIS, facing);
    }
    @Override
    public FluidState getFluidState(BlockState state) {
        return super.getFluidState(state);
    }

    public boolean canConnect(BlockState blockState)
    {
        return PaladinFurnitureMod.getPFMConfig().doTablesOfDifferentMaterialsConnect() ? blockState.getBlock() instanceof BasicCoffeeTableBlock : blockState.getBlock() == this;
    }

    public int getFlammability(BlockState state, BlockView world, BlockPos pos, Direction face) {
        if (AbstractSittableBlock.isWoodBased(state)) {
            return 20;
        }
        return 0;
    }

    /** Method to rotate VoxelShapes from this random Forge Forums thread: https://forums.minecraftforge.net/topic/74979-1144-rotate-voxel-shapes/ */
    public static VoxelShape rotateShape(Direction from, Direction to, VoxelShape shape) {
        VoxelShape[] buffer = new VoxelShape[]{ shape, VoxelShapes.empty() };
        int times = (to.getHorizontal() - from.getHorizontal() + 4) % 4;
        for (int i = 0; i < times; i++) {
            buffer[0].forEachBox((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] = VoxelShapes.union(buffer[1], VoxelShapes.cuboid(1-maxZ, minY, minX, 1-minZ, maxY, maxX)));
            buffer[0] = buffer[1];
            buffer[1] = VoxelShapes.empty();
        }
        return buffer[0];
    }

    final static VoxelShape TABLE_BASIC_BASE = createCuboidShape(0, 8, 0, 16, 10, 16);
    final static VoxelShape TABLE_BASIC_NORTH_EAST_LEG = createCuboidShape(12, 0, 2, 14, 8, 4);
    final static VoxelShape TABLE_BASIC_SOUTH_WEST_LEG = createCuboidShape(2, 0, 12,4, 8, 14);
    final static VoxelShape TABLE_BASIC_NORTH_WEST_LEG = createCuboidShape(2, 0, 2,4, 8, 4);
    final static VoxelShape TABLE_BASIC_SOUTH_EAST_LEG = createCuboidShape(12, 0, 12,14, 8, 14);
    final static VoxelShape TABLE_BASIC_EAST_WEST_NORTH = createCuboidShape(4, 0, 2,12, 2, 4);
    final static VoxelShape TABLE_BASIC_EAST_WEST_SOUTH = createCuboidShape(4, 0, 12,12, 2, 14);
    final static VoxelShape TABLE_BASIC_SOUTH_EAST_TOP = createCuboidShape(4, 0, 2,16, 2, 4);
    final static VoxelShape TABLE_BASIC_SOUTH_EAST_BOTTOM = createCuboidShape(4, 0, 12,16, 2, 14);
    final static VoxelShape TABLE_BASIC_SOUTH_WEST_BOTTOM = createCuboidShape(0, 0, 12,12, 2, 14);
    final static VoxelShape TABLE_BASIC_SOUTH_WEST_TOP = createCuboidShape(0, 0, 2,12, 2, 4);
    final static VoxelShape TABLE_BASIC_NORTH_SOUTH_WEST = createCuboidShape(0, 0, 2,16, 2, 4);
    final static VoxelShape TABLE_BASIC_NORTH_SOUTH_EAST = createCuboidShape(0, 0, 12, 16, 2, 14);
    final static VoxelShape TABLE_BASIC_NORTH_EAST_CORNER = createCuboidShape(14, 0, 2, 16, 2, 4);
    final static VoxelShape TABLE_BASIC_SOUTH_EAST_CORNER = createCuboidShape(14, 0, 12, 16, 2, 14);
    final static VoxelShape TABLE_BASIC_NORTH_WEST_CORNER = createCuboidShape(0, 0, 2, 2, 2, 4);
    final static VoxelShape TABLE_BASIC_SOUTH_WEST_CORNER = createCuboidShape(0, 0, 12, 2, 2, 14);
    final static Map<String, VoxelShape> VOXEL_SHAPES = new HashMap<>();

    public boolean canConnect(BlockView world, BlockState state, BlockPos neighborPos, BlockPos pos){
        BlockState neighborState = world.getBlockState(neighborPos);
        if (neighborState.contains(AXIS)) {
            return neighborState.get(AXIS) == state.get(AXIS) && canConnect(neighborState);
        }
        return false;
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Direction.Axis dir = state.get(BasicCoffeeTableBlock.AXIS);

        Boolean north = canConnect(world, state, pos.north(), pos);
        boolean east = canConnect(world, state, pos.east(), pos);
        boolean west = canConnect(world, state, pos.west(), pos);
        boolean south = canConnect(world, state, pos.south(), pos);
        boolean cornerNorthWest = north && west && !canConnect(world, state, pos.north().west(), pos);
        boolean cornerNorthEast = north && east && !canConnect(world, state, pos.north().east(), pos);
        boolean cornerSouthEast = south && east && !canConnect(world, state, pos.south().east(), pos);
        boolean cornerSouthWest = south && west && !canConnect(world, state, pos.south().west(), pos);

        String key = north.toString()+ east + west + south + cornerNorthWest + cornerNorthEast + cornerSouthEast + cornerSouthWest + dir.asString();
        if (!VOXEL_SHAPES.containsKey(key)) {
            generateVoxelShape(key, north, east, west, south, cornerNorthWest, cornerNorthEast, cornerSouthEast, cornerSouthWest, dir);
        }
        return VOXEL_SHAPES.get(key);
    }

    private static void generateVoxelShape(String key, Boolean north, Boolean east, Boolean west, Boolean south, Boolean cornerNorthWest, Boolean cornerNorthEast, Boolean cornerSouthEast, Boolean cornerSouthWest, Direction.Axis axis) {
        VoxelShape newVoxelShape = TABLE_BASIC_BASE;
        if (!north && !south && !east && !west) {
            newVoxelShape = VoxelShapes.union(newVoxelShape, TABLE_BASIC_EAST_WEST_NORTH, TABLE_BASIC_EAST_WEST_SOUTH);
        }
        if (!north && !east) {
            newVoxelShape = VoxelShapes.union(newVoxelShape, getShapeForAxis(axis, TABLE_BASIC_NORTH_EAST_LEG, TABLE_BASIC_NORTH_WEST_LEG));
        }
        if (!north && !west)  {
            newVoxelShape = VoxelShapes.union(newVoxelShape, getShapeForAxis(axis, TABLE_BASIC_NORTH_WEST_LEG, TABLE_BASIC_SOUTH_WEST_LEG));
        }
        if (!south && !east)  {
            newVoxelShape = VoxelShapes.union(newVoxelShape, getShapeForAxis(axis, TABLE_BASIC_SOUTH_EAST_LEG, TABLE_BASIC_NORTH_EAST_LEG));
        }
        if (!south && !west)  {
            newVoxelShape = VoxelShapes.union(newVoxelShape, getShapeForAxis(axis, TABLE_BASIC_SOUTH_WEST_LEG, TABLE_BASIC_SOUTH_EAST_LEG));
        }
        if (cornerNorthEast) {
            newVoxelShape = VoxelShapes.union(newVoxelShape, getShapeForAxis(axis, VoxelShapes.union(TABLE_BASIC_NORTH_EAST_LEG, TABLE_BASIC_NORTH_EAST_CORNER), VoxelShapes.union(TABLE_BASIC_NORTH_WEST_LEG, TABLE_BASIC_NORTH_WEST_CORNER)));
        }
        if (cornerNorthWest) {
            newVoxelShape = VoxelShapes.union(newVoxelShape, getShapeForAxis(axis, VoxelShapes.union(TABLE_BASIC_NORTH_WEST_LEG, TABLE_BASIC_NORTH_WEST_CORNER), VoxelShapes.union(TABLE_BASIC_SOUTH_WEST_LEG, TABLE_BASIC_SOUTH_WEST_CORNER)));
        }
        if (cornerSouthWest) {
            newVoxelShape = VoxelShapes.union(newVoxelShape, getShapeForAxis(axis, VoxelShapes.union(TABLE_BASIC_SOUTH_WEST_LEG, TABLE_BASIC_SOUTH_WEST_CORNER), VoxelShapes.union(TABLE_BASIC_SOUTH_EAST_LEG, TABLE_BASIC_SOUTH_EAST_CORNER)));
        }
        if (cornerSouthEast) {
            newVoxelShape = VoxelShapes.union(newVoxelShape, getShapeForAxis(axis, VoxelShapes.union(TABLE_BASIC_SOUTH_EAST_LEG, TABLE_BASIC_SOUTH_EAST_CORNER), VoxelShapes.union(TABLE_BASIC_NORTH_EAST_LEG, TABLE_BASIC_NORTH_EAST_CORNER)));
        }

        if (axis == Direction.Axis.Z) {
            if (!north && south && !east && !west) {
                newVoxelShape = VoxelShapes.union(newVoxelShape, TABLE_BASIC_EAST_WEST_NORTH);
            }
            if (north && !south && !east && !west) {
                newVoxelShape = VoxelShapes.union(newVoxelShape, TABLE_BASIC_EAST_WEST_SOUTH);
            }
            if (!north && east && !west) {
                newVoxelShape = VoxelShapes.union(newVoxelShape, TABLE_BASIC_SOUTH_EAST_TOP);
            }
            if (!south && !east && west) {
                newVoxelShape = VoxelShapes.union(newVoxelShape, TABLE_BASIC_SOUTH_WEST_BOTTOM);
            }
            if (!south && east && !west) {
                newVoxelShape = VoxelShapes.union(newVoxelShape, TABLE_BASIC_SOUTH_EAST_BOTTOM);
            }
            if (!north && !east && west) {
                newVoxelShape = VoxelShapes.union(newVoxelShape, TABLE_BASIC_SOUTH_WEST_TOP);
            }
            if (!north && east && west) {
                newVoxelShape = VoxelShapes.union(newVoxelShape, TABLE_BASIC_NORTH_SOUTH_WEST);
            }
            if (!south && east && west) {
                newVoxelShape = VoxelShapes.union(newVoxelShape, TABLE_BASIC_NORTH_SOUTH_EAST);
            }
        }
        else {
            if (!north && south && !west) {
                newVoxelShape = VoxelShapes.union(newVoxelShape, TABLE_BASIC_SOUTH_EAST_BOTTOM);
            }
            if (north && !south && !west) {
                newVoxelShape = VoxelShapes.union(newVoxelShape, TABLE_BASIC_SOUTH_WEST_BOTTOM);
            }
            if (!north && south && !east) {
                newVoxelShape = VoxelShapes.union(newVoxelShape, TABLE_BASIC_SOUTH_EAST_TOP);
            }
            if (north && !south && !east) {
                newVoxelShape = VoxelShapes.union(newVoxelShape, TABLE_BASIC_SOUTH_WEST_TOP);
            }
            if (north && !south && !east) {
                newVoxelShape = VoxelShapes.union(newVoxelShape, TABLE_BASIC_SOUTH_WEST_TOP);
            }
            if (!north && !south && !east) {
                newVoxelShape = VoxelShapes.union(newVoxelShape, TABLE_BASIC_EAST_WEST_NORTH);
            }
            if (!north && !south && !west) {
                newVoxelShape = VoxelShapes.union(newVoxelShape, TABLE_BASIC_EAST_WEST_SOUTH);
            }
            if (north && south && !east) {
                newVoxelShape = VoxelShapes.union(newVoxelShape, TABLE_BASIC_NORTH_SOUTH_WEST);
            }
            if (north && south && !west) {
                newVoxelShape = VoxelShapes.union(newVoxelShape, TABLE_BASIC_NORTH_SOUTH_EAST);
            }
            newVoxelShape = rotateShape(Direction.NORTH, Direction.EAST, newVoxelShape);
        }
        VOXEL_SHAPES.put(key, newVoxelShape);
    }

    private static VoxelShape getShapeForAxis(Direction.Axis axis, VoxelShape a, VoxelShape b) {
        if (axis == Direction.Axis.Z) {
            return a;
        } else if (axis == Direction.Axis.X) {
            return b;
        }
        return VoxelShapes.empty();
    }

    @Override
    public boolean canPathfindThrough(BlockState state, NavigationType type) {
        return false;
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        switch (rotation) {
            case COUNTERCLOCKWISE_90, CLOCKWISE_90 -> {
                switch (state.get(AXIS)) {
                    case X -> {
                        return state.with(AXIS, Direction.Axis.Z);
                    }
                    case Z -> {
                        return state.with(AXIS, Direction.Axis.X);
                    }
                }
                return state;
            }
        }
        return state;
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(Direction.get(Direction.AxisDirection.NEGATIVE, state.get(AXIS))));
    }

    @Override
    protected MapCodec<? extends Block> getCodec() {
        return CODEC;
    }
}


