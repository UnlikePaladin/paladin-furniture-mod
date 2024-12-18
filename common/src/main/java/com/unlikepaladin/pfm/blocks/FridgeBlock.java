package com.unlikepaladin.pfm.blocks;

import com.mojang.serialization.MapCodec;
import com.unlikepaladin.pfm.blocks.blockentities.FridgeBlockEntity;
import com.unlikepaladin.pfm.data.FurnitureBlock;
import com.unlikepaladin.pfm.registry.Statistics;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.*;
import net.minecraft.world.tick.ScheduledTickView;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.unlikepaladin.pfm.blocks.KitchenDrawerBlock.rotateShape;

public class FridgeBlock extends HorizontalFacingBlockWithEntity {
    public static final BooleanProperty OPEN = Properties.OPEN;
    private final Block baseBlock;
    private final BlockState baseBlockState;
    private static final List<FurnitureBlock> FRIDGES = new ArrayList<>();
    private final Supplier<FreezerBlock> freezer;
    public FridgeBlock(Settings settings, Supplier<FreezerBlock> freezer) {
        super(settings);
        setDefaultState(this.getStateManager().getDefaultState().with(Properties.HORIZONTAL_FACING, Direction.NORTH).with(OPEN, false));
        this.baseBlockState = this.getDefaultState();
        this.baseBlock = baseBlockState.getBlock();
        FRIDGES.add(new FurnitureBlock(this, "fridge"));
        this.freezer = freezer;
    }

    public static Stream<FurnitureBlock> streamFridges() {
        return FRIDGES.stream();
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(Properties.HORIZONTAL_FACING);
        stateManager.add(OPEN);
    }

    // Todo: Also implement this codec properly
    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return null;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return super.canPlaceAt(state, world, pos);
    }

    @Override
    protected boolean isTransparent(BlockState state) {
        return true;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.isOf(newState.getBlock())) {
            return;
        }
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof Inventory) {
            ItemScatterer.spawn(world, pos, (Inventory) blockEntity);
            world.updateComparators(pos, this);
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        return direction.getAxis().isVertical() && neighborState.getBlock() == this ? neighborState.get(FACING) == state.get(FACING) ? state.with(OPEN, neighborState.get(OPEN)) : state : state;
    }

    @Override
    public boolean isShapeFullCube(BlockState state, BlockView world, BlockPos pos) {
        return false;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (world instanceof ServerWorld serverWorld && blockEntity instanceof FridgeBlockEntity) {
            player.openHandledScreen((FridgeBlockEntity)blockEntity);
            player.incrementStat(Statistics.FRIDGE_OPENED);
            PiglinBrain.onGuardedBlockInteracted(serverWorld, player, true);
        }
        return ActionResult.CONSUME;
    }

    public void onBreakInCreative(World world, BlockPos pos, BlockState state, PlayerEntity player) {
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing());
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient && player.isCreative()) {
            this.onBreakInCreative(world, pos, state, player);
        }
        return super.onBreak(world, pos, state, player);
    }

    @Override
    public boolean isSideInvisible(BlockState state, BlockState stateFrom, Direction direction) {
        if (stateFrom.isOf(this)) {
            return true;
        }
        return super.isSideInvisible(state, stateFrom, direction);
    }

    @Override
    public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
        return 1.0f;
    }

    protected static final Map<Direction, VoxelShape> FRIDGE_SINGLE = new HashMap<>() {{put(Direction.NORTH, VoxelShapes.union(createCuboidShape(12, 3, -0.5,13, 14, 1), createCuboidShape(1, 1, 1,15, 16, 2), createCuboidShape(1, 0, 2,15, 16, 16)));}};
    protected static final Map<Direction, VoxelShape> FRIDGE_TOP = new HashMap<>() {{put(Direction.NORTH, VoxelShapes.union(createCuboidShape(12, 0, -0.5, 13, 15, 1), createCuboidShape(1, 0, 1, 15, 16, 16)));}};
    protected static final Map<Direction, VoxelShape> FRIDGE_MIDDLE = new HashMap<>() {{put(Direction.NORTH, VoxelShapes.union(createCuboidShape(12, 0, -0.5, 13, 16, 1), createCuboidShape(1, 0, 1,15, 16, 16)));}};
    protected static final Map<Direction, VoxelShape> FRIDGE_BOTTOM = new HashMap<>() {{put(Direction.NORTH, VoxelShapes.union(createCuboidShape(12, 4, -0.5,13, 16, 1), createCuboidShape(1, 1, 1,15, 16, 2), createCuboidShape(1, 0, 2,15, 16, 16)));}};
    protected static final Map<Direction, VoxelShape> FRIDGE = new HashMap<>() {{put(Direction.NORTH, VoxelShapes.union(createCuboidShape(12, 4, -0.5, 13, 20, 1), createCuboidShape(1, 1, 1,15, 21, 2),createCuboidShape(1, 0, 2,15, 32, 16)));}};
    protected static final Map<Direction, VoxelShape> FRIDGE_MIDDLE_FREEZER = new HashMap<>() {{put(Direction.NORTH, VoxelShapes.union(createCuboidShape(12, 0, -0.5,13, 20, 1),createCuboidShape(1, 0, 1,15, 21, 16),createCuboidShape(1, 21, 2,15, 32, 16)));}};

    protected static final Map<Direction, VoxelShape> FRIDGE_SINGLE_OPEN = new HashMap<>() {{put(Direction.NORTH, VoxelShapes.union(createCuboidShape(-0.5, 3, -10,1, 14, -9), createCuboidShape(1, 1, -12,3, 16, 2),createCuboidShape(1, 0, 2,15, 16, 16)));}};
    protected static final Map<Direction, VoxelShape> FRIDGE_TOP_OPEN = new HashMap<>() {{put(Direction.NORTH, VoxelShapes.union(createCuboidShape(-0.5, 0, -10,1, 15, -9), createCuboidShape(1, 0, -12,3, 16, 2),createCuboidShape(1, 0, 2,15, 16, 16)));}};
    protected static final Map<Direction, VoxelShape> FRIDGE_MIDDLE_OPEN = new HashMap<>() {{put(Direction.NORTH, VoxelShapes.union(createCuboidShape(-0.5, 0, -10,1, 16, -9),createCuboidShape(1, 0, -12,3, 16, 2),createCuboidShape(1, 0, 2,15, 16, 16)));}};
    protected static final Map<Direction, VoxelShape> FRIDGE_BOTTOM_OPEN = new HashMap<>() {{put(Direction.NORTH, VoxelShapes.union(createCuboidShape(-0.5, 4, -10,1, 16, -9),createCuboidShape(1, 1, -12,3, 16, 2),createCuboidShape(1, 0, 3,15, 16, 16),createCuboidShape(1, 1, 2,15, 16, 3)));}};
    protected static final Map<Direction, VoxelShape> FRIDGE_OPEN = new HashMap<>() {{put(Direction.NORTH, VoxelShapes.union(createCuboidShape(-0.5, 4, -10,1, 20, -9),createCuboidShape(1, 1, -12,3, 20, 2),createCuboidShape(1, 20, 1,15, 21, 2),createCuboidShape(1, 0, 2,15, 32, 16)));}};
    protected static final Map<Direction, VoxelShape> FRIDGE_MIDDLE_FREEZER_OPEN = new HashMap<>() {{put(Direction.NORTH, VoxelShapes.union(createCuboidShape(-0.5, 0, -10,1, 20, -9),createCuboidShape(1, 0, -12,3, 20, 2),createCuboidShape(1, 0, 2,15, 32, 16),createCuboidShape(1, 20, 1,15, 21, 2)));}};


    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Direction dir = state.get(FACING).getOpposite();
        boolean open = state.get(OPEN);
        boolean bottom = state.isOf(world.getBlockState(pos.up()).getBlock());
        boolean top = state.isOf(world.getBlockState(pos.down()).getBlock());
        boolean hasFreezer = world.getBlockState(pos.up()).getBlock() instanceof FreezerBlock && !(world.getBlockState(pos.up()).getBlock() instanceof IronFridgeBlock);

        if (top && hasFreezer) {
            if (open) {
                if (!FRIDGE_MIDDLE_FREEZER_OPEN.containsKey(dir))
                    FRIDGE_MIDDLE_FREEZER_OPEN.put(dir, rotateShape(Direction.NORTH, dir, FRIDGE_MIDDLE_FREEZER_OPEN.get(Direction.NORTH)));
                return FRIDGE_MIDDLE_FREEZER_OPEN.get(dir);
            }
                if (!FRIDGE_MIDDLE_FREEZER.containsKey(dir))
                    FRIDGE_MIDDLE_FREEZER.put(dir, rotateShape(Direction.NORTH, dir, FRIDGE_MIDDLE_FREEZER.get(Direction.NORTH)));
                return FRIDGE_MIDDLE_FREEZER.get(dir);
        }
        else if (top && bottom) {
            if (open) {
                if (!FRIDGE_MIDDLE_OPEN.containsKey(dir))
                    FRIDGE_MIDDLE_OPEN.put(dir, rotateShape(Direction.NORTH, dir, FRIDGE_MIDDLE_OPEN.get(Direction.NORTH)));
                return FRIDGE_MIDDLE_OPEN.get(dir);
            }
                if (!FRIDGE_MIDDLE.containsKey(dir))
                    FRIDGE_MIDDLE.put(dir, rotateShape(Direction.NORTH, dir, FRIDGE_MIDDLE.get(Direction.NORTH)));
                return FRIDGE_MIDDLE.get(dir);
        }
        else if (bottom) {
            if (open) {
                if (!FRIDGE_BOTTOM_OPEN.containsKey(dir))
                    FRIDGE_BOTTOM_OPEN.put(dir, rotateShape(Direction.NORTH, dir, FRIDGE_BOTTOM_OPEN.get(Direction.NORTH)));
                return FRIDGE_BOTTOM_OPEN.get(dir);
            }
                if (!FRIDGE_BOTTOM.containsKey(dir))
                    FRIDGE_BOTTOM.put(dir, rotateShape(Direction.NORTH, dir, FRIDGE_BOTTOM.get(Direction.NORTH)));
                return FRIDGE_BOTTOM.get(dir);
        }
        else if (top) {
            if (open) {
                if (!FRIDGE_TOP_OPEN.containsKey(dir))
                    FRIDGE_TOP_OPEN.put(dir, rotateShape(Direction.NORTH, dir, FRIDGE_TOP_OPEN.get(Direction.NORTH)));
                return FRIDGE_TOP_OPEN.get(dir);
            }
                if (!FRIDGE_TOP.containsKey(dir))
                    FRIDGE_TOP.put(dir, rotateShape(Direction.NORTH, dir, FRIDGE_TOP.get(Direction.NORTH)));
                return FRIDGE_TOP.get(dir);
        }
        else if (hasFreezer) {
            if (open) {
                if (!FRIDGE_OPEN.containsKey(dir))
                    FRIDGE_OPEN.put(dir, rotateShape(Direction.NORTH, dir, FRIDGE_OPEN.get(Direction.NORTH)));
                return FRIDGE_OPEN.get(dir);
            }
                if (!FRIDGE.containsKey(dir))
                    FRIDGE.put(dir, rotateShape(Direction.NORTH, dir, FRIDGE.get(Direction.NORTH)));
                return FRIDGE.get(dir);
        }
        else {
            if (open) {
                if (!FRIDGE_SINGLE_OPEN.containsKey(dir))
                    FRIDGE_SINGLE_OPEN.put(dir, rotateShape(Direction.NORTH, dir, FRIDGE_SINGLE_OPEN.get(Direction.NORTH)));
                return FRIDGE_SINGLE_OPEN.get(dir);
            }
                if (!FRIDGE_SINGLE.containsKey(dir))
                    FRIDGE_SINGLE.put(dir, rotateShape(Direction.NORTH, dir, FRIDGE_SINGLE.get(Direction.NORTH)));
                return FRIDGE_SINGLE.get(dir);
        }
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return FridgeBlockEntity.getFactory().create(pos, state);
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos));
    }

    @Override
    public boolean canPathfindThrough(BlockState state, NavigationType type) {
        return false;
    }
}
