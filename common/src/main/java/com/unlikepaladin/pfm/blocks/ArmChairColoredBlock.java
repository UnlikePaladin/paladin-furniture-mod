package com.unlikepaladin.pfm.blocks;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.data.FurnitureBlock;
import net.minecraft.block.*;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.State;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.DyeColor;
import net.minecraft.util.StringIdentifiable;
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

import static com.unlikepaladin.pfm.blocks.KitchenDrawerBlock.rotateShape;

public class ArmChairColoredBlock extends ArmChairBlock implements DyeableFurnitureBlock {
    public static final EnumProperty<ArmChairShape> SHAPE = EnumProperty.of("shape", ArmChairShape.class);
    private static final List<FurnitureBlock> COLORED_ARMCHAIRS = new ArrayList<>();
    private final DyeColor color;
    public static MapCodec<ArmChairColoredBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(DyeColor.CODEC.fieldOf("color").forGetter(abstractSittableBlock -> abstractSittableBlock.color), createSettingsCodec()).apply(instance, ArmChairColoredBlock::new));;

    public ArmChairColoredBlock(DyeColor color, Settings settings) {
        super(settings);
        setDefaultState(this.getStateManager().getDefaultState().with(Properties.HORIZONTAL_FACING, Direction.NORTH).with(SHAPE, ArmChairShape.STRAIGHT));
            if (this.getClass().isAssignableFrom(ArmChairColoredBlock.class)) {
                COLORED_ARMCHAIRS.add(new FurnitureBlock(this, "arm_chair"));
            }
        this.color = color;
    }

    @Override
    protected MapCodec<? extends HorizontalFacingBlock> getCodec() {
        return CODEC;
    }

    public static Stream<FurnitureBlock> streamArmChairColored() {
        return COLORED_ARMCHAIRS.stream();
    }

    public DyeColor getPFMColor() {
        return this.color;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        super.appendProperties(stateManager);
        stateManager.add(SHAPE);
    }

    protected static final VoxelShape STANDARD = VoxelShapes.union(createCuboidShape(12, 0, 12 ,14.5, 3, 14.5),createCuboidShape(12, 0, 1.5,14.5, 3, 4), createCuboidShape(1, 0, 1.5, 3.5, 3, 4), createCuboidShape(1, 0, 12, 3.5, 3, 14.5), createCuboidShape(6.6, 2, 13, 16.3, 13.71, 16), createCuboidShape(6.6, 2, 0, 16.3, 13.71, 3), createCuboidShape(0.3, 2, 3, 16.3, 10.51, 13), createCuboidShape(0.3, 10.5, 3, 5.3, 25.51, 13), createCuboidShape(0.3, 2, 13, 6.6, 25.51, 16), createCuboidShape(0.3, 2, 0, 6.6, 25.51, 3));
    protected static final VoxelShape MIDDLE = VoxelShapes.union(createCuboidShape(0, 2, 0.3,16, 9.51, 16),createCuboidShape(0, 9.5, 0.3,16, 25.51, 5.3), createCuboidShape(0, 9.5, 5.3, 16, 10.5, 16));
    protected static final VoxelShape OUTER = VoxelShapes.union(createCuboidShape(0, 2, 0,16, 10.51, 15.7),createCuboidShape(0, 10.5, 10.7,5.3, 25.51, 15.7), createCuboidShape(0.3, 2, 15.7, 5.3, 25.51, 16),createCuboidShape(5.3, 2, 15.7,16, 10.51, 16),createCuboidShape(12.5, 0, 1.7,15, 3, 4.2),createCuboidShape(1, 0, 11.7,3.5, 3, 14.2));
    protected static final VoxelShape LEFT_EDGE = VoxelShapes.union(createCuboidShape(1.5, 0, 12,4, 3, 14.5),createCuboidShape(1.5, 0, 1,4, 3, 3.5), createCuboidShape(0, 2, 6.6, 3, 13.71, 16),createCuboidShape(3, 2, 0.3,16, 10.51, 16),createCuboidShape(3, 10.5, 0.3,16, 25.51, 5.3),createCuboidShape(0, 2, 0.3,3, 25.51, 6.6));
    protected static final VoxelShape RIGHT_EDGE = VoxelShapes.union(createCuboidShape(12.5, 0, 12,15, 3, 14.5),createCuboidShape(12.5, 0, 1,15, 3, 3.5), createCuboidShape(13, 2, 6.6, 16, 13.71, 16),createCuboidShape(0, 2, 0.3,13, 10.51, 16),createCuboidShape(0, 10.5, 0.3,13, 25.51, 5.3),createCuboidShape(13, 2, 0.3,16, 25.51, 6.6));
    protected static final VoxelShape INNER = VoxelShapes.union(createCuboidShape(12.5, 0, 12,15, 3, 14.5),createCuboidShape(1, 0, 1.5,3.5, 3, 4), createCuboidShape(0.3, 2, 0.3, 16, 10.51, 16),createCuboidShape(0.3, 10.5, 5.3,5.3, 25.51, 16),createCuboidShape(0.3, 10.5, 0.3,16, 25.51, 5.3));

    protected static final VoxelShape STANDARD_SOUTH = rotateShape(Direction.WEST, Direction.SOUTH, STANDARD);
    protected static final VoxelShape STANDARD_EAST = rotateShape(Direction.WEST, Direction.EAST, STANDARD);
    protected static final VoxelShape STANDARD_NORTH = rotateShape(Direction.WEST, Direction.NORTH, STANDARD);

    protected static final VoxelShape MIDDLE_SOUTH = rotateShape(Direction.WEST, Direction.SOUTH, MIDDLE);
    protected static final VoxelShape MIDDLE_EAST = rotateShape(Direction.WEST, Direction.EAST, MIDDLE);
    protected static final VoxelShape MIDDLE_WEST = rotateShape(Direction.WEST, Direction.NORTH, MIDDLE);

    protected static final VoxelShape OUTER_SOUTH = rotateShape(Direction.WEST, Direction.SOUTH, OUTER);
    protected static final VoxelShape OUTER_EAST = rotateShape(Direction.WEST, Direction.EAST, OUTER);
    protected static final VoxelShape OUTER_WEST = rotateShape(Direction.WEST, Direction.NORTH, OUTER);

    protected static final VoxelShape LEFT_EDGE_NORTH = rotateShape(Direction.WEST, Direction.NORTH, LEFT_EDGE);
    protected static final VoxelShape LEFT_EDGE_SOUTH = rotateShape(Direction.WEST, Direction.SOUTH, LEFT_EDGE);
    protected static final VoxelShape LEFT_EDGE_EAST = rotateShape(Direction.WEST, Direction.EAST, LEFT_EDGE);

    protected static final VoxelShape RIGHT_EDGE_NORTH = rotateShape(Direction.WEST, Direction.NORTH, RIGHT_EDGE);
    protected static final VoxelShape RIGHT_EDGE_SOUTH = rotateShape(Direction.WEST, Direction.SOUTH, RIGHT_EDGE);
    protected static final VoxelShape RIGHT_EDGE_EAST = rotateShape(Direction.WEST, Direction.EAST, RIGHT_EDGE);

    protected static final VoxelShape INNER_NORTH = rotateShape(Direction.WEST, Direction.NORTH, INNER);
    protected static final VoxelShape INNER_SOUTH = rotateShape(Direction.WEST, Direction.SOUTH, INNER);
    protected static final VoxelShape INNER_EAST = rotateShape(Direction.WEST, Direction.EAST, INNER);
    @SuppressWarnings("deprecated")
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
        Direction dir = state.get(FACING);
        ArmChairShape shape = state.get(SHAPE);
        switch(shape) {
            case STRAIGHT:
                return switch (dir) {
                    case NORTH -> STANDARD_NORTH;
                    case SOUTH -> STANDARD_SOUTH;
                    case EAST -> STANDARD_EAST;
                    default -> STANDARD;
                };
            case MIDDLE:
                return switch (dir) {
                    case NORTH -> MIDDLE;
                    case SOUTH -> MIDDLE_EAST;
                    case EAST -> MIDDLE_WEST;
                    default -> MIDDLE_SOUTH;
                };
            case OUTER_LEFT:
                return switch (dir) {
                    case NORTH -> OUTER_WEST;
                    case SOUTH -> OUTER_SOUTH;
                    case EAST -> OUTER_EAST;
                    default -> OUTER;
                };
            case OUTER_RIGHT:
                return switch (dir) {
                    case NORTH -> OUTER_EAST;
                    case SOUTH -> OUTER;
                    case EAST -> OUTER_SOUTH;
                    default -> OUTER_WEST;
                };
            case LEFT_EDGE:
                return switch (dir) {
                    case NORTH -> LEFT_EDGE;
                    case SOUTH -> LEFT_EDGE_EAST;
                    case EAST -> LEFT_EDGE_NORTH;
                    default -> LEFT_EDGE_SOUTH;
                };
            case RIGHT_EDGE:
                return switch (dir) {
                    case NORTH -> RIGHT_EDGE;
                    case SOUTH -> RIGHT_EDGE_EAST;
                    case EAST -> RIGHT_EDGE_NORTH;
                    default -> RIGHT_EDGE_SOUTH;
                };
            case INNER_RIGHT:
                return switch (dir) {
                    case NORTH -> INNER_NORTH;
                    case SOUTH -> INNER_SOUTH;
                    case EAST -> INNER_EAST;
                    default -> INNER;
                };
            case INNER_LEFT:
                return switch (dir) {
                    case NORTH -> INNER;
                    case SOUTH -> INNER_EAST;
                    case EAST -> INNER_NORTH;
                    default -> INNER_SOUTH;
                };
            default:
                return STANDARD;
        }
    }


    public boolean isArmChair(BlockState state) {
        return(state.getBlock().getClass().isAssignableFrom(ArmChairColoredBlock.class) && state.getBlock() instanceof ArmChairColoredBlock);
    }


    private ArmChairShape getShape(BlockState state, BlockView world, BlockPos pos) {
        Direction direction3;
        Object direction2;
        Direction direction = state.get(FACING);
        BlockState blockState = world.getBlockState(pos.offset(direction));
        boolean right = this.canConnect(world, pos, state.get(FACING).rotateYCounterclockwise(), state.get(FACING));
        boolean left = this.canConnect(world, pos, state.get(FACING).rotateYClockwise(), state.get(FACING));
        if (this.isArmChair(blockState) && ((Direction)(direction2 = blockState.get(FACING))).getAxis() != state.get(FACING).getAxis() && this.isDifferentOrientation(state, world, pos, ((Direction)direction2).getOpposite())) {
            if (direction2 == direction.rotateYCounterclockwise()) {
                return ArmChairShape.OUTER_LEFT;
            }
            return ArmChairShape.OUTER_RIGHT;
        }
        direction2 = world.getBlockState(pos.offset(direction.getOpposite()));
        if (this.isArmChair((BlockState)direction2) && (direction3 = (Direction) ((State)direction2).get(FACING)).getAxis() != state.get(FACING).getAxis() && this.isDifferentOrientation(state, world, pos, direction3)) {
            if (direction3 == direction.rotateYCounterclockwise()) {
                return ArmChairShape.INNER_LEFT;
            }
            return ArmChairShape.INNER_RIGHT;
        }
        if (left && right) {
            return ArmChairShape.MIDDLE;
        }
        else if (left) {
            return ArmChairShape.LEFT_EDGE;
        }
        else if (right) {
            return ArmChairShape.RIGHT_EDGE;
        }
        return ArmChairShape.STRAIGHT;
    }

    public boolean canConnect(BlockView world, BlockPos pos, Direction direction, Direction tableDirection)
    {
        BlockState state = world.getBlockState(pos.offset(direction));
        return (state.getBlock().getClass().isAssignableFrom(ArmChairColoredBlock.class) && state.getBlock() instanceof ArmChairColoredBlock);
    }

    private boolean isDifferentOrientation(BlockState state, BlockView world, BlockPos pos, Direction dir) {
        BlockState blockState = world.getBlockState(pos.offset(dir));
        return !this.isArmChair(blockState) || blockState.get(FACING) != state.get(FACING);
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        return direction.getAxis().isHorizontal() ? state.with(FACING, state.get(FACING)).with(SHAPE, getShape(state, world, pos)) : super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState blockState = this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing());
        Direction facing = PaladinFurnitureMod.getPFMConfig().doChairsFacePlayer() ? ctx.getHorizontalPlayerFacing() : ctx.getHorizontalPlayerFacing().getOpposite();
        return this.getDefaultState().with(SHAPE, getShape(blockState, ctx.getWorld(), ctx.getBlockPos())).with(FACING, facing);
    }

}

enum ArmChairShape implements StringIdentifiable
{
    STRAIGHT("straight"),
    INNER_LEFT("inner_left"),
    INNER_RIGHT("inner_right"),
    OUTER_LEFT("outer_left"),
    OUTER_RIGHT("outer_right"),
    MIDDLE("middle"),
    LEFT_EDGE("left_edge"),
    RIGHT_EDGE("right_edge");


    private final String name;

    ArmChairShape(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    @Override
    public String asString() {
        return this.name;
    }
}



