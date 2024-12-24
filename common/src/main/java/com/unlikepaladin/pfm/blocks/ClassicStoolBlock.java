package com.unlikepaladin.pfm.blocks;

import com.unlikepaladin.pfm.data.FurnitureBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class ClassicStoolBlock extends BasicChairBlock {
    private static final List<FurnitureBlock> WOOD_CLASSIC_STOOLS = new ArrayList<>();
    private static final List<FurnitureBlock> STONE_CLASSIC_STOOLS = new ArrayList<>();
    public ClassicStoolBlock(Settings settings) {
        super(settings);
        setDefaultState(this.getStateManager().getDefaultState().with(Properties.HORIZONTAL_FACING, Direction.NORTH).with(TUCKED, false));
        if(isWoodBased(this.getDefaultState()) && this.getClass().isAssignableFrom(ClassicStoolBlock.class)){
            WOOD_CLASSIC_STOOLS.add(new FurnitureBlock(this, "classic_stool"));
        }
        else if (this.getClass().isAssignableFrom(ClassicStoolBlock.class)){
            STONE_CLASSIC_STOOLS.add(new FurnitureBlock(this, "classic_stool"));
        }
    }

    public static Stream<FurnitureBlock> streamWoodClassicStools() {
        return WOOD_CLASSIC_STOOLS.stream();
    }
    public static Stream<FurnitureBlock> streamStoneClassicStools() {
        return STONE_CLASSIC_STOOLS.stream();
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return super.getPlacementState(ctx);
    }

    protected static final VoxelShape CLASSIC_STOOL_SOUTH = VoxelShapes.union(createCuboidShape(3.625, 0, 3.5,5.625, 10, 5.5), createCuboidShape(10.625, 0, 3.5, 12.625, 10, 5.5), createCuboidShape(10.625, 0, 10.5, 12.625, 20, 12.5), createCuboidShape(3.625, 10, 3.5, 12.625, 12, 10.5), createCuboidShape(5.625, 10, 10.5, 10.625, 12, 12.5), createCuboidShape(5.625, 15, 11, 10.625, 19.5, 12),createCuboidShape(3.625, 0, 10.5,5.625, 20, 12.5));
    protected static final VoxelShape CLASSIC_STOOL = rotateShape(Direction.NORTH, Direction.SOUTH, CLASSIC_STOOL_SOUTH);
    protected static final VoxelShape CLASSIC_STOOL_EAST = rotateShape(Direction.NORTH, Direction.WEST, CLASSIC_STOOL_SOUTH);
    protected static final VoxelShape CLASSIC_STOOL_WEST = rotateShape(Direction.NORTH, Direction.EAST, CLASSIC_STOOL_SOUTH);
    protected static final VoxelShape FACE_SOUTH_TUCKED = tuckShape(Direction.SOUTH, CLASSIC_STOOL_SOUTH);
    protected static final VoxelShape FACE_NORTH_TUCKED = tuckShape(Direction.NORTH, CLASSIC_STOOL);
    protected static final VoxelShape FACE_WEST_TUCKED = tuckShape(Direction.WEST, CLASSIC_STOOL_WEST);
    protected static final VoxelShape FACE_EAST_TUCKED = tuckShape(Direction.EAST, CLASSIC_STOOL_EAST);

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
            case WEST -> CLASSIC_STOOL_WEST;
            case NORTH -> CLASSIC_STOOL;
            case SOUTH -> CLASSIC_STOOL_SOUTH;
            default -> CLASSIC_STOOL_EAST;
        };
    }


}

