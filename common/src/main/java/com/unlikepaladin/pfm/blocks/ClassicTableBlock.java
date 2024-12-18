package com.unlikepaladin.pfm.blocks;

import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.data.FurnitureBlock;
import net.minecraft.block.*;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class ClassicTableBlock extends Block {
    private final Block baseBlock;

    private static final List<FurnitureBlock> WOOD_CLASSIC_TABLES = new ArrayList<>();
    private static final List<FurnitureBlock> STONE_CLASSIC_TABLES = new ArrayList<>();
    private final BlockState baseBlockState;
    public ClassicTableBlock(Settings settings) {
        super(settings);
        setDefaultState(this.getStateManager().getDefaultState());
        this.baseBlockState = this.getDefaultState();
        this.baseBlock = baseBlockState.getBlock();
        if(AbstractSittableBlock.isWoodBased(this.getDefaultState()) && this.getClass().isAssignableFrom(ClassicTableBlock.class)){
            WOOD_CLASSIC_TABLES.add(new FurnitureBlock(this, "table_classic"));
        }
        else if (this.getClass().isAssignableFrom(ClassicTableBlock.class)){
            STONE_CLASSIC_TABLES.add(new FurnitureBlock(this, "table_classic"));
        }
    }

    public static Stream<FurnitureBlock> streamWoodClassicTables() {
        return WOOD_CLASSIC_TABLES.stream();
    }
    public static Stream<FurnitureBlock> streamStoneClassicTables() {
        return STONE_CLASSIC_TABLES.stream();
    }

    @Override
    public boolean isShapeFullCube(BlockState state, BlockView world, BlockPos pos) {
        return false;
    }

    public int getFlammability(BlockState state, BlockView world, BlockPos pos, Direction face) {
        if (AbstractSittableBlock.isWoodBased(state)) {
            return 20;
        }
        return 0;
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState();
    }

    public boolean canConnect(BlockState blockState) {
        return PaladinFurnitureMod.getPFMConfig().doTablesOfDifferentMaterialsConnect() ? blockState.getBlock() instanceof ClassicTableBlock : blockState.getBlock() == this;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return super.getFluidState(state);
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

    final static VoxelShape TABLE_CLASSIC_BASE = createCuboidShape(0, 14, 0, 16, 16, 16);
    final static VoxelShape TABLE_CLASSIC_NORTH_EAST_LEG = createCuboidShape(12, 0, 2, 14, 15, 4);
    final static VoxelShape TABLE_CLASSIC_NORTH_WEST_LEG = createCuboidShape(2, 0, 2, 4, 15, 4);
    final static VoxelShape TABLE_CLASSIC_SOUTH_WEST_LEG = createCuboidShape(2, 0, 12, 4, 15, 14);
    final static VoxelShape TABLE_CLASSIC_SOUTH_EAST_LEG = createCuboidShape(12, 0, 12, 14, 15, 14);

    // Cursed I know
    final static Map<String, VoxelShape> VOXEL_SHAPES = new HashMap<>();
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {

        Boolean north = canConnect(world.getBlockState(pos.north()));
        boolean east = canConnect(world.getBlockState(pos.east()));
        boolean west = canConnect(world.getBlockState(pos.west()));
        boolean south = canConnect(world.getBlockState(pos.south()));

        String key = north.toString()+ east + west + south;
        if (!VOXEL_SHAPES.containsKey(key)) {
            generateVoxelShape(key, north, east, west, south);
        }
        return VOXEL_SHAPES.get(key);
    }
    private static void generateVoxelShape(String key, Boolean north, Boolean east, Boolean west, Boolean south) {
        VoxelShape newVoxelShape = TABLE_CLASSIC_BASE;
        if (!north && !south && !east && !west) {
            newVoxelShape = VoxelShapes.union(newVoxelShape, TABLE_CLASSIC_NORTH_EAST_LEG, TABLE_CLASSIC_SOUTH_WEST_LEG, TABLE_CLASSIC_SOUTH_EAST_LEG, TABLE_CLASSIC_NORTH_WEST_LEG);
        }
        if (!north && !east) {
            newVoxelShape = VoxelShapes.union(newVoxelShape, TABLE_CLASSIC_NORTH_EAST_LEG);
        }
        if (!north && !west)  {
            newVoxelShape = VoxelShapes.union(newVoxelShape, TABLE_CLASSIC_NORTH_WEST_LEG);
        }
        if (!south && !east)  {
            newVoxelShape = VoxelShapes.union(newVoxelShape, TABLE_CLASSIC_SOUTH_EAST_LEG);
        }
        if (!south && !west)  {
            newVoxelShape = VoxelShapes.union(newVoxelShape, TABLE_CLASSIC_SOUTH_WEST_LEG);
        }
        VOXEL_SHAPES.put(key, newVoxelShape);
    }

    @Override
    public boolean canPathfindThrough(BlockState state, NavigationType type) {
        return false;
    }
}


