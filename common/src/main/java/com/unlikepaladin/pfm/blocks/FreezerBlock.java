package com.unlikepaladin.pfm.blocks;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.unlikepaladin.pfm.blocks.blockentities.FreezerBlockEntity;
import com.unlikepaladin.pfm.data.FurnitureBlock;
import com.unlikepaladin.pfm.registry.BlockEntities;
import com.unlikepaladin.pfm.registry.Statistics;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.*;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.tick.ScheduledTickView;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.unlikepaladin.pfm.blocks.KitchenDrawerBlock.rotateShape;

public class FreezerBlock extends HorizontalFacingBlockWithEntity {
    public static final BooleanProperty OPEN = Properties.OPEN;
    private Supplier<FridgeBlock> fridge;
    private static final List<FurnitureBlock> FREEZERS = new ArrayList<>();
  //  public static final MapCodec<FreezerBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(createSettingsCodec(), ).apply(instance, FreezerBlock::new));
    public FreezerBlock(Settings settings, Supplier<FridgeBlock> fridge) {
        super(settings);
        setDefaultState(this.getStateManager().getDefaultState().with(Properties.HORIZONTAL_FACING, Direction.NORTH).with(OPEN, false));
        this.fridge = fridge;
        FREEZERS.add(new FurnitureBlock(this, "freezer"));
    }

    public static Stream<FurnitureBlock> streamFreezers() {
        return FREEZERS.stream();
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
         if (!world.isClient) {
                NamedScreenHandlerFactory screenHandlerFactory = state.createScreenHandlerFactory(world, pos);
                if (screenHandlerFactory != null) {
                    player.incrementStat(Statistics.FREEZER_OPENED);
                    player.openHandledScreen(screenHandlerFactory);
                    PiglinBrain.onGuardedBlockInteracted((ServerWorld) world, player, true);
                }
            }
            return ActionResult.SUCCESS;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(Properties.HORIZONTAL_FACING);
        stateManager.add(OPEN);
    }

    // Todo: Figure out how i can implement this properly, supplier to a block is kinda weird, it is ok to return null in 1.20.4 for now
    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return null;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public boolean hasSidedTransparency(BlockState state) {
        return true;
    }

    @Override
    public ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state) {
        return super.getPickStack(world, pos, state);
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    protected void onBreakInCreative(World world, BlockPos pos, BlockState state, PlayerEntity player) {
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing());
    }

    protected static final Map<Direction, VoxelShape> FREEZER = new HashMap<>() {{put(Direction.NORTH, VoxelShapes.union(createCuboidShape(12, 5, -0.5,13, 15, 1),createCuboidShape(1, 5, 1,15, 16, 2),createCuboidShape(1, -16, 2,15, 16, 16)));}};
    protected static final Map<Direction, VoxelShape> FREEZER_SINGLE = new HashMap<>() {{put(Direction.NORTH, VoxelShapes.union(createCuboidShape(12, 4, -0.5,13, 15, 1),createCuboidShape(1, 1, 1,15, 16, 2),createCuboidShape(1, 0, 2,15, 16, 16)));}};
    protected static final Map<Direction, VoxelShape> FREEZER_OPEN = new HashMap<>() {{put(Direction.NORTH, VoxelShapes.union(createCuboidShape(-0.5, 5, -10,1, 15, -9),createCuboidShape(1, 5, -12,3, 16, 2),createCuboidShape(1, 5, 2,15, 16, 16),createCuboidShape(1, -16, 1,15, 5, 16)));}};
    protected static final Map<Direction, VoxelShape> FREEZER_SINGLE_OPEN = new HashMap<>() {{put(Direction.NORTH, VoxelShapes.union(createCuboidShape(-0.5, 4, -10,1, 15, -9),createCuboidShape(1, 1, -12,3, 16, 2),createCuboidShape(1, 0, 2,15, 16, 16)));}};

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Direction dir = state.get(FACING).getOpposite();
        Boolean open = state.get(OPEN);
        boolean hasFridge = world.getBlockState(pos.down()).getBlock() instanceof FridgeBlock && !(world.getBlockState(pos.down()).getBlock() instanceof IronFridgeBlock);
        if (hasFridge) {
            if (open) {
                if (!FREEZER_OPEN.containsKey(dir))
                    FREEZER_OPEN.put(dir, rotateShape(Direction.NORTH, dir, FREEZER_OPEN.get(Direction.NORTH)));
                return FREEZER_OPEN.get(dir);
            }
            if (!FREEZER.containsKey(dir))
                FREEZER.put(dir, rotateShape(Direction.NORTH, dir, FREEZER.get(Direction.NORTH)));
            return FREEZER.get(dir);
        } else {
            if (open) {
                if (!FREEZER_SINGLE_OPEN.containsKey(dir))
                    FREEZER_SINGLE_OPEN.put(dir, rotateShape(Direction.NORTH, dir, FREEZER_SINGLE_OPEN.get(Direction.NORTH)));
                return FREEZER_SINGLE_OPEN.get(dir);
            }
            if (!FREEZER_SINGLE.containsKey(dir))
                FREEZER_SINGLE.put(dir, rotateShape(Direction.NORTH, dir, FREEZER_SINGLE.get(Direction.NORTH)));
            return FREEZER_SINGLE.get(dir);
        }
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient && player.isCreative()) {
            this.onBreakInCreative(world, pos,state, player);
        }
        return super.onBreak(world, pos, state, player);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return super.canPlaceAt(state, world, pos);
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
    public void onBroken(WorldAccess world, BlockPos pos, BlockState state) {
       super.onBroken(world, pos, state);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return FreezerBlockEntity.getFactory().create(pos,state);
    }
    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(world, type, BlockEntities.FREEZER_BLOCK_ENTITY);
    }

    @Nullable
    protected static <T extends BlockEntity> BlockEntityTicker<T> checkType(World world, BlockEntityType<T> givenType, BlockEntityType<? extends FreezerBlockEntity> expectedType) {
        return world.isClient ? null : validateTicker(givenType, expectedType, FreezerBlockEntity::tick);
    }

    @Override
    public boolean canPathfindThrough(BlockState state, NavigationType type) {
        return false;
    }
}
