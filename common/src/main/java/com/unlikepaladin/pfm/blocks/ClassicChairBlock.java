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

public class ClassicChairBlock extends BasicChairBlock {
    private static final List<FurnitureBlock> WOOD_CLASSIC_CHAIRS = new ArrayList<>();
    private static final List<FurnitureBlock> STONE_CLASSIC_CHAIRS = new ArrayList<>();
    public ClassicChairBlock(Settings settings) {
        super(settings);
        setDefaultState(this.getStateManager().getDefaultState().with(Properties.HORIZONTAL_FACING, Direction.NORTH).with(TUCKED, false));
        if(isWoodBased(this.getDefaultState()) && this.getClass().isAssignableFrom(ClassicChairBlock.class)){
            WOOD_CLASSIC_CHAIRS.add(new FurnitureBlock(this.asBlock(), "chair_classic"));
        }
        else if (this.getClass().isAssignableFrom(ClassicChairBlock.class)){
            STONE_CLASSIC_CHAIRS.add(new FurnitureBlock(this.asBlock(), "chair_classic"));
        }
    }

    public static Stream<FurnitureBlock> streamWoodClassicChairs() {
        return WOOD_CLASSIC_CHAIRS.stream();
    }
    public static Stream<FurnitureBlock> streamStoneClassicChairs() {
        return STONE_CLASSIC_CHAIRS.stream();
    }

    protected static final VoxelShape FACE_WEST = VoxelShapes.union(createCuboidShape(2, 9, 2.05,4, 24, 13.95), createCuboidShape(2, 7.85, 4.05, 4, 9, 11.9), createCuboidShape(3.85, 8.75, 1.99, 14.95, 10.25, 13.96), createCuboidShape(3.8, 7.86, 1.97, 15, 9.26, 13.97), createCuboidShape(2, 0, 11.9, 4, 9, 13.9), createCuboidShape(12, 0, 11.9, 14, 9, 13.9),createCuboidShape(2, 0, 2.05,4, 9, 4.05),createCuboidShape(12, 0, 2.05,14, 9, 4.05));
    protected static final VoxelShape FACE_EAST = rotateShape(Direction.WEST, Direction.EAST, FACE_WEST);
    protected static final VoxelShape FACE_NORTH = rotateShape(Direction.WEST, Direction.NORTH, FACE_WEST);
    protected static final VoxelShape FACE_SOUTH = rotateShape(Direction.WEST, Direction.SOUTH, FACE_WEST);
    protected static final VoxelShape FACE_NORTH_TUCKED = tuckShape(Direction.NORTH, FACE_NORTH);
    protected static final VoxelShape FACE_SOUTH_TUCKED = tuckShape(Direction.SOUTH, FACE_SOUTH);
    protected static final VoxelShape FACE_EAST_TUCKED = tuckShape(Direction.EAST, FACE_EAST);
    protected static final VoxelShape FACE_WEST_TUCKED = tuckShape(Direction.WEST, FACE_WEST);
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
        return switch (dir) {
            case WEST -> FACE_WEST;
            case NORTH -> FACE_NORTH;
            case SOUTH -> FACE_SOUTH;
            default -> FACE_EAST;
        };
    }


}

