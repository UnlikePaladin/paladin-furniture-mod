package com.unlikepaladin.pfm.blocks;

import com.unlikepaladin.pfm.data.FurnitureBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class SimpleStoolBlock extends BasicChairBlock {
    private static final List<FurnitureBlock> WOOD_SIMPLE_STOOLS = new ArrayList<>();
    private static final List<FurnitureBlock> STONE_SIMPLE_STOOLS = new ArrayList<>();
    public SimpleStoolBlock(Settings settings) {
        super(settings);
        setDefaultState(this.getStateManager().getDefaultState().with(Properties.HORIZONTAL_FACING, Direction.NORTH).with(TUCKED, false));
        if(isWoodBased(this.getDefaultState()) && this.getClass().isAssignableFrom(SimpleStoolBlock.class)){
            WOOD_SIMPLE_STOOLS.add(new FurnitureBlock(this, "simple_stool"));
        }
        else if (this.getClass().isAssignableFrom(SimpleStoolBlock.class)){
            STONE_SIMPLE_STOOLS.add(new FurnitureBlock(this, "simple_stool"));
        }
    }

    public static Stream<FurnitureBlock> streamWoodSimpleStools() {
        return WOOD_SIMPLE_STOOLS.stream();
    }
    public static Stream<FurnitureBlock> streamStoneSimpleStools() {
        return STONE_SIMPLE_STOOLS.stream();
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

    @Override
    public boolean canTuck(BlockState state) {
        return (super.canTuck(state) || state.getBlock() instanceof KitchenCounterBlock);
    }

    protected static VoxelShape SIMPLE_STOOL = VoxelShapes.union(createCuboidShape(3.5, 0, 3.5,5.5, 10, 5.5),createCuboidShape(10.5, 0, 3.5,12.5, 10, 5.5),createCuboidShape(10.5, 0, 10.5,12.5, 10, 12.5),createCuboidShape(3.5, 10, 3.5,12.5, 12, 12.5),createCuboidShape(3.5, 0, 10.5,5.5, 10, 12.5));
    protected static final VoxelShape FACE_NORTH_TUCKED = tuckShape(Direction.NORTH, SIMPLE_STOOL);
    protected static final VoxelShape FACE_SOUTH_TUCKED = tuckShape(Direction.SOUTH, SIMPLE_STOOL);
    protected static final VoxelShape FACE_EAST_TUCKED = tuckShape(Direction.EAST, SIMPLE_STOOL);
    protected static final VoxelShape FACE_WEST_TUCKED = tuckShape(Direction.WEST, SIMPLE_STOOL);
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
        Direction dir = state.get(FACING);
        if (state.get(TUCKED)) {
            return switch (dir) {
                case WEST -> FACE_WEST_TUCKED;
                case NORTH -> FACE_NORTH_TUCKED;
                case SOUTH -> FACE_SOUTH_TUCKED;
                default -> FACE_EAST_TUCKED;
            };
        }
        return SIMPLE_STOOL;
    }



}

