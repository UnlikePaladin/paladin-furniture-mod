package com.unlikepaladin.pfm.blocks;

import com.unlikepaladin.pfm.data.FurnitureBlock;
import com.unlikepaladin.pfm.data.PFMTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public class BasicChairBlock extends AbstractSittableBlock {
    private static final List<FurnitureBlock> WOOD_BASIC_CHAIRS = new ArrayList<>();
    private static final List<FurnitureBlock> STONE_BASIC_CHAIRS = new ArrayList<>();

    public static final BooleanProperty TUCKED = BooleanProperty.of("tucked");
    public BasicChairBlock(Settings settings) {
        super(settings);
        setDefaultState(this.getStateManager().getDefaultState().with(Properties.HORIZONTAL_FACING, Direction.NORTH).with(TUCKED, false));
        if(isWoodBased(this.getDefaultState()) && this.getClass().isAssignableFrom(BasicChairBlock.class)){
            WOOD_BASIC_CHAIRS.add(new FurnitureBlock(this, "chair"));
        }
        else if (this.getClass().isAssignableFrom(BasicChairBlock.class)){
            STONE_BASIC_CHAIRS.add(new FurnitureBlock(this, "chair"));
        }
    }

    public static Stream<FurnitureBlock> streamWoodBasicChairs() {
        return WOOD_BASIC_CHAIRS.stream();
    }
    public static Stream<FurnitureBlock> streamStoneBasicChairs() {
        return STONE_BASIC_CHAIRS.stream();
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        super.appendProperties(stateManager);
        stateManager.add(TUCKED);
    }

    protected static final VoxelShape FACE_WEST = VoxelShapes.union(createCuboidShape(1, 0, 2 ,3.5 ,8 ,4.5), createCuboidShape(1, 0, 11, 3.5, 8, 13.5), createCuboidShape(11, 0, 2, 13.5, 8, 4.5), createCuboidShape(11, 0, 11, 13.5, 8, 13.5), createCuboidShape(0.32, 8,1.6, 14.3, 10.49, 14.6 ), createCuboidShape(0.32, 8, 1.6, 2.65, 24.49,14.6 ));
    protected static final VoxelShape FACE_EAST = VoxelShapes.union(createCuboidShape(2.5, 0, 2.5 ,5 ,8 ,5), createCuboidShape(2.5, 0, 11.5, 5, 8, 14), createCuboidShape(12.5, 0, 2.5, 15, 8, 5), createCuboidShape(12.5, 0, 11.5, 15, 8, 14), createCuboidShape(1.65, 8,1.4, 15.66, 10.49, 14.4 ), createCuboidShape(13.33, 8, 1.4, 15.66, 24.49,14.4 ) );
    protected static final VoxelShape FACE_NORTH = VoxelShapes.union(createCuboidShape(2.5, 0, 1 ,5 ,8 ,3.5), createCuboidShape(2.5, 0, 11, 5, 8, 13.5), createCuboidShape(11.5, 0, 1, 14, 8, 3.5), createCuboidShape(11.5, 0, 11, 14, 8, 13.5), createCuboidShape(1.39, 8,0.32, 14.4, 10.49, 14.32 ), createCuboidShape(1.39, 8, 0.32, 14.4, 24.49,2.65 ));
    protected static final VoxelShape FACE_SOUTH = VoxelShapes.union(createCuboidShape(2, 0, 2.5 ,4.5 ,8 ,5), createCuboidShape(2, 0, 12.5, 4.5, 8, 15), createCuboidShape(11, 0, 2.5, 13.5, 8, 5), createCuboidShape(11, 0, 12.5, 13.5, 8, 15), createCuboidShape(1.61, 8,1.65, 14.66, 10.49, 15.67 ), createCuboidShape(1.61, 8, 13.4, 14.66, 24.49,15.67 ) );
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

    /** Method to tuck the Chair's Voxel Shapes */
    public static VoxelShape tuckShape(Direction from, VoxelShape shape) {
        VoxelShape[] buffer = new VoxelShape[]{shape, VoxelShapes.empty()};

        switch (from) {
            case NORTH -> { buffer[0].forEachBox((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] = VoxelShapes.union(buffer[1], VoxelShapes.cuboid(minX, minY, minZ + 0.5, maxX, maxY, maxZ + 0.5)));
                buffer[0] = buffer[1];
                buffer[1] = VoxelShapes.empty();}
            case SOUTH -> { buffer[0].forEachBox((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] = VoxelShapes.union(buffer[1], VoxelShapes.cuboid(minX, minY, minZ - 0.5, maxX, maxY, maxZ - 0.5)));
                buffer[0] = buffer[1];
                buffer[1] = VoxelShapes.empty();}
            case WEST -> { buffer[0].forEachBox((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] = VoxelShapes.union(buffer[1], VoxelShapes.cuboid(minX + 0.5, minY, minZ, maxX + 0.5, maxY, maxZ)));
                buffer[0] = buffer[1];
                buffer[1] = VoxelShapes.empty();}
            default -> { buffer[0].forEachBox((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] = VoxelShapes.union(buffer[1], VoxelShapes.cuboid(minX - 0.5, minY, minZ, maxX - 0.5, maxY, maxZ)));
                buffer[0] = buffer[1];
                buffer[1] = VoxelShapes.empty();}
        }
        return buffer[0];
    }
    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (!canTuck(world.getBlockState(pos.offset(state.get(FACING).getOpposite()))) && state.get(TUCKED)){
            return state.with(TUCKED, false);
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    public boolean canTuck(BlockState state) {
        return state.isIn(PFMTags.TUCKABLE_BLOCKS);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (player.isSneaking() && this.canTuck(world.getBlockState(pos.offset(state.get(FACING).getOpposite())))) {
            if (state.get(TUCKED)) {
                world.setBlockState(pos, state.with(TUCKED, false));
            }
            else {
                world.setBlockState(pos, state.with(TUCKED, true));
            }
            return ActionResult.SUCCESS;
        }
        return super.onUse(state, world, pos, player, hit);
    }

    @Override
    public Function<Settings, AbstractSittableBlock> getChairConstructor() {
        return BasicChairBlock::new;
    }
}

