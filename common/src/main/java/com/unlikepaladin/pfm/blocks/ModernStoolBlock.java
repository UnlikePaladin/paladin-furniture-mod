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

public class ModernStoolBlock extends BasicChairBlock {
    private static final List<FurnitureBlock> WOOD_MODERN_STOOLS = new ArrayList<>();
    private static final List<FurnitureBlock> STONE_MODERN_STOOLS = new ArrayList<>();
    public ModernStoolBlock(Settings settings) {
        super(settings);
        setDefaultState(this.getStateManager().getDefaultState().with(Properties.HORIZONTAL_FACING, Direction.NORTH).with(TUCKED, false));
        if(isWoodBased(this.getDefaultState()) && this.getClass().isAssignableFrom(ModernStoolBlock.class)){
            WOOD_MODERN_STOOLS.add(new FurnitureBlock(this, "modern_stool"));
        }
        else if (this.getClass().isAssignableFrom(ModernStoolBlock.class)){
            STONE_MODERN_STOOLS.add(new FurnitureBlock(this, "modern_stool"));
        }
    }

    public static Stream<FurnitureBlock> streamWoodModernStools() {
        return WOOD_MODERN_STOOLS.stream();
    }
    public static Stream<FurnitureBlock> streamStoneModernStools() {
        return STONE_MODERN_STOOLS.stream();
    }

    protected static final VoxelShape MODERN_STOOL_SOUTH = VoxelShapes.union(createCuboidShape(7.125, 1, 7 ,9.125, 10, 9), createCuboidShape(5.125, 0, 5, 11.125, 1, 11), createCuboidShape(4.625, 10, 4.5, 11.625, 12, 11.5), createCuboidShape(4.625, 12, 9.5, 11.625, 15, 11.5));
    protected static final VoxelShape MODERN_STOOL = rotateShape(Direction.NORTH, Direction.SOUTH, MODERN_STOOL_SOUTH);
    protected static final VoxelShape MODERN_STOOL_WEST = rotateShape(Direction.NORTH, Direction.EAST, MODERN_STOOL_SOUTH);
    protected static final VoxelShape MODERN_STOOL_EAST = rotateShape(Direction.NORTH, Direction.WEST, MODERN_STOOL_SOUTH);
    protected static final VoxelShape FACE_NORTH_TUCKED = tuckShape(Direction.NORTH, MODERN_STOOL);
    protected static final VoxelShape FACE_SOUTH_TUCKED = tuckShape(Direction.SOUTH, MODERN_STOOL_SOUTH);
    protected static final VoxelShape FACE_EAST_TUCKED = tuckShape(Direction.EAST, MODERN_STOOL_EAST);
    protected static final VoxelShape FACE_WEST_TUCKED = tuckShape(Direction.WEST, MODERN_STOOL_WEST);

    @Override
    public boolean canTuck(BlockState state) {
        return (super.canTuck(state) || state.getBlock() instanceof KitchenCounterBlock);
    }

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
            case WEST -> MODERN_STOOL_WEST;
            case NORTH -> MODERN_STOOL;
            case SOUTH -> MODERN_STOOL_SOUTH;
            default -> MODERN_STOOL_EAST;
        };
    }

}

