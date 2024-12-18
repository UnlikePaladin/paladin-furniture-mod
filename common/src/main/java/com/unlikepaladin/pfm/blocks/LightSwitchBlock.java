package com.unlikepaladin.pfm.blocks;

import com.mojang.serialization.MapCodec;
import com.unlikepaladin.pfm.blocks.blockentities.LightSwitchBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
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

public class LightSwitchBlock extends HorizontalFacingBlockWithEntity {
    public static final EnumProperty<Direction> FACING = HorizontalFacingBlock.FACING;
    public static final BooleanProperty POWERED = Properties.POWERED;

    private static final List<LightSwitchBlock> LIGHT_SWITCHES = new ArrayList<>();
    public static final MapCodec<LightSwitchBlock> CODEC = createCodec(LightSwitchBlock::new);

    public LightSwitchBlock(Settings settings) {
        super(settings);
        this.setDefaultState(stateManager.getDefaultState().with(FACING, Direction.NORTH).with(POWERED, false));
        LIGHT_SWITCHES.add(this);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    public static Stream<LightSwitchBlock> streamlightSwitches() {
        return LIGHT_SWITCHES.stream();
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
        BlockState blockState = this.togglePower(state, world, pos, false, false);
        float f = blockState.get(POWERED) ? 0.9f : 0.8f;
        world.playSound(null, pos, SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.BLOCKS, 0.3f, f);
        world.emitGameEvent(player, blockState.get(POWERED) ? GameEvent.BLOCK_ACTIVATE : GameEvent.BLOCK_DEACTIVATE, pos);
        return ActionResult.CONSUME;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (itemStack.contains(DataComponentTypes.BLOCK_ENTITY_DATA)) {
            NbtCompound nbtCompound = itemStack.getOrDefault(DataComponentTypes.BLOCK_ENTITY_DATA, NbtComponent.DEFAULT).getNbt();
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (nbtCompound.contains("lights") && blockEntity instanceof LightSwitchBlockEntity) {
                nbtCompound.remove("lights");
                itemStack.set(DataComponentTypes.BLOCK_ENTITY_DATA, NbtComponent.of(nbtCompound));
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

    @Override
    public FluidState getFluidState(BlockState state) {
        return super.getFluidState(state);
    }

    public BlockState togglePower(BlockState state, World world, BlockPos pos, boolean listenTo, boolean toggleTo) {
        if(listenTo) {
            state = state.with(POWERED, toggleTo);
        }
        else {
        state = state.cycle(POWERED);}
        world.setBlockState(pos, state, Block.NOTIFY_ALL);
        this.updateNeighbors(state, world, pos);
        if (world.getBlockEntity(pos) instanceof LightSwitchBlockEntity)
            ((LightSwitchBlockEntity)world.getBlockEntity(pos)).setState(state.get(POWERED));
        return state;
    }

    private static final VoxelShape lightSwitch = VoxelShapes.union(createCuboidShape(5, 3, 15,11, 11, 16));
    private static final VoxelShape lightSwitchSouth = BasicTableBlock.rotateShape(Direction.NORTH, Direction.SOUTH, lightSwitch);
    private static final VoxelShape lightSwitchEast = BasicTableBlock.rotateShape(Direction.NORTH, Direction.EAST, lightSwitch);
    private static final VoxelShape lightSwitchWest = BasicTableBlock.rotateShape(Direction.NORTH, Direction.WEST, lightSwitch);
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Direction facing = getDirection(state);
        switch (facing) {
            case SOUTH -> {
                return lightSwitchSouth;
            }
            case EAST -> {
                return lightSwitchEast;
            }
            case WEST -> {
                return lightSwitchWest;
            }
            default ->  {
                return lightSwitch;
            }
        }
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public boolean emitsRedstonePower(BlockState state) {
        return true;
    }
    @Override
    public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        if (state.get(POWERED) && getDirection(state) == direction) {
            return 15;
        }
        return 0;
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return state.get(POWERED) ? 15 : 0;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.get(POWERED)) {
            this.updateNeighbors(state, world, pos);
        }

        if (state.isOf(newState.getBlock())) {
            return;
        }
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof LightSwitchBlockEntity lightSwitchBlockEntity) {
            lightSwitchBlockEntity.markRemoved();
        }
    }

    private void updateNeighbors(BlockState state, World world, BlockPos pos) {
        world.updateNeighborsAlways(pos, this);
        world.updateNeighborsAlways(pos.offset(getDirection(state).getOpposite()), this);
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (world.getBlockEntity(pos) != null) {
            this.togglePower(state, world, pos, true, false);
        }
       return super.onBreak(world, pos, state, player);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new LightSwitchBlockEntity(pos, state);
    }


    protected static Direction getDirection(BlockState state) {
        return state.get(FACING);
    }

}
