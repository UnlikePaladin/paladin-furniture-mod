package com.unlikepaladin.pfm.blocks;

import com.mojang.serialization.MapCodec;
import com.unlikepaladin.pfm.blocks.blockentities.ShowerHandleBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.tick.ScheduledTickView;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class BasicShowerHandleBlock extends HorizontalFacingBlockWithEntity {
    public static final EnumProperty<Direction> FACING = HorizontalFacingBlock.FACING;
    public static final BooleanProperty POWERED = Properties.POWERED;
    private static final List<BasicShowerHandleBlock> SHOWER_HANDLE_BLOCKS = new ArrayList<>();
    public BasicShowerHandleBlock(Settings settings) {
        super(settings);
        this.setDefaultState(stateManager.getDefaultState().with(FACING, Direction.NORTH).with(POWERED, false));
        SHOWER_HANDLE_BLOCKS.add(this);
    }

    public static final MapCodec<BasicShowerHandleBlock> CODEC = createCodec(BasicShowerHandleBlock::new);
    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    public static Stream<BasicShowerHandleBlock> streamShowerHandleBlocks() {
        return SHOWER_HANDLE_BLOCKS.stream();
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite()).with(POWERED, false);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }
        BlockState blockState = this.toggleOpen(state, world, pos, false, false);
        float f = blockState.get(POWERED) ? 0.9f : 0.8f;
        world.playSound(null, pos, SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.BLOCKS, 0.3f, f);
        world.emitGameEvent(player, blockState.get(POWERED) ? GameEvent.BLOCK_ACTIVATE : GameEvent.BLOCK_DEACTIVATE, pos);
        return ActionResult.CONSUME;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (itemStack.contains(DataComponentTypes.BLOCK_ENTITY_DATA)) {
            NbtComponent nbtCompound = itemStack.get(DataComponentTypes.BLOCK_ENTITY_DATA);
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (nbtCompound.contains("showerHead") && blockEntity instanceof ShowerHandleBlockEntity) {
                itemStack.remove(DataComponentTypes.BLOCK_ENTITY_DATA);
            }
        }

    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if (direction.getOpposite() == state.get(FACING) && !state.canPlaceAt(world, pos)) {
            return Blocks.AIR.getDefaultState();
        }
        return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        Direction direction = state.get(FACING);
        BlockPos blockPos = pos.offset(direction.getOpposite());
        BlockState blockState = world.getBlockState(blockPos);
        return blockState.isSideSolidFullSquare(world, blockPos, direction);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
        builder.add(POWERED);
    }

    public BlockState toggleOpen(BlockState state, World world, BlockPos pos, boolean listenTo, boolean toggleTo) {
        if(listenTo) {
            state = state.with(POWERED, toggleTo);
        }
        else {
        state = state.cycle(POWERED);}
        world.setBlockState(pos, state, Block.NOTIFY_ALL);
        this.updateNeighbors(state, world, pos);
        if (world.getBlockEntity(pos) instanceof ShowerHandleBlockEntity)
            ((ShowerHandleBlockEntity)(world.getBlockEntity(pos))).setState(state.get(POWERED));
        return state;
    }

    private static final VoxelShape SHOWER_HANDLE = VoxelShapes.union(createCuboidShape(11.5, 8, 14, 12.5, 9, 15), createCuboidShape(11, 7.5, 15, 13, 9.5, 16), createCuboidShape(11.5, 6.5, 13, 12.5, 10.5, 14), createCuboidShape(10, 8, 13, 14, 9, 14), createCuboidShape(3.5, 8, 14, 4.5, 9, 15), createCuboidShape(3, 7.5, 15, 5, 9.5, 16), createCuboidShape(3.5, 6.5, 13, 4.5, 10.5, 14), createCuboidShape(2, 8, 13, 6, 9, 14));
    private static final VoxelShape SHOWER_HANDLE_SOUTH = BasicTableBlock.rotateShape(Direction.NORTH, Direction.SOUTH, SHOWER_HANDLE);
    private static final VoxelShape SHOWER_HANDLE_EAST = BasicTableBlock.rotateShape(Direction.NORTH, Direction.EAST, SHOWER_HANDLE);
    private static final VoxelShape SHOWER_HANDLE_WEST = BasicTableBlock.rotateShape(Direction.NORTH, Direction.WEST, SHOWER_HANDLE);

    private static final VoxelShape SHOWER_HANDLE_OPEN = VoxelShapes.union(createCuboidShape(11, 7.5, 15, 13, 9.5, 16), createCuboidShape(11.5, 8, 13, 12.5, 9, 15), createCuboidShape(12.5, 9, 13, 13.5, 10, 14), createCuboidShape(12.5, 7, 13, 13.5, 8, 14), createCuboidShape(10.5, 9, 13, 11.5, 10, 14), createCuboidShape(10.5, 7, 13, 11.5, 8, 14), createCuboidShape(3.5, 8, 13, 4.5, 9, 15), createCuboidShape(4.5, 9, 13,5.5, 10, 14), createCuboidShape(4.5, 7, 13,5.5, 8, 14), createCuboidShape(2.5, 9, 13,3.5, 10, 14), createCuboidShape(2.5, 7, 13, 3.5, 8, 14), createCuboidShape(3, 7.5, 15,5, 9.5, 16));
    private static final VoxelShape SHOWER_HANDLE_SOUTH_OPEN = BasicTableBlock.rotateShape(Direction.NORTH, Direction.SOUTH, SHOWER_HANDLE_OPEN);
    private static final VoxelShape SHOWER_HANDLE_EAST_OPEN = BasicTableBlock.rotateShape(Direction.NORTH, Direction.EAST, SHOWER_HANDLE_OPEN);
    private static final VoxelShape SHOWER_HANDLE_WEST_OPEN = BasicTableBlock.rotateShape(Direction.NORTH, Direction.WEST, SHOWER_HANDLE_OPEN);
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Direction facing = getDirection(state);
        boolean open = state.get(POWERED);
        switch (facing) {
            case SOUTH: {
                if(open)
                    return SHOWER_HANDLE_SOUTH_OPEN;
                else
                    return SHOWER_HANDLE_SOUTH;
            }
            case EAST: {
                if (open)
                    return SHOWER_HANDLE_EAST_OPEN;
                else
                    return SHOWER_HANDLE_EAST;
            }
            case WEST: {
                if (open)
                    return SHOWER_HANDLE_WEST_OPEN;
                else
                    return SHOWER_HANDLE_WEST;
            }
            default:  {
                if (open)
                    return SHOWER_HANDLE_OPEN;
                else
                    return SHOWER_HANDLE;
            }
        }
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    private void updateNeighbors(BlockState state, World world, BlockPos pos) {
        world.updateNeighborsAlways(pos, this);
        world.updateNeighborsAlways(pos.offset(getDirection(state).getOpposite()), this);
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (world.getBlockEntity(pos) != null) {
            ((ShowerHandleBlockEntity)(world.getBlockEntity(pos))).setState(false);
        }
        this.spawnBreakParticles(world, player, pos, state);
        if (state.isIn(BlockTags.GUARDED_BY_PIGLINS) && world instanceof ServerWorld serverWorld) {
            PiglinBrain.onGuardedBlockInteracted(serverWorld, player, false);
        }
        world.emitGameEvent(player, GameEvent.BLOCK_DESTROY, pos);
        return super.onBreak(world, pos, state, player);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ShowerHandleBlockEntity(pos, state);
    }

    protected static Direction getDirection(BlockState state) {
        return state.get(FACING);
    }
}
