package com.unlikepaladin.pfm.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.block.WireOrientation;
import net.minecraft.world.tick.ScheduledTickView;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class PendantBlock extends PowerableBlock implements DynamicRenderLayerInterface {
    public static final BooleanProperty UP = Properties.UP;
    public static final BooleanProperty DOWN = Properties.DOWN;
    public static final BooleanProperty LIT = RedstoneTorchBlock.LIT;
    private final BlockState baseBlockState;
    private final Block baseBlock;
    private static final List<PendantBlock> PENDANTS = new ArrayList<>();
    public static final MapCodec<PendantBlock> CODEC = createCodec(PendantBlock::new);

    public PendantBlock(Settings settings) {
        super(settings);
        setDefaultState(this.getStateManager().getDefaultState().with(UP, false).with(DOWN, false).with(LIT,  false).with(POWERLOCKED, false));
        this.baseBlockState = this.getDefaultState();
        this.baseBlock = baseBlockState.getBlock();
        PENDANTS.add(this);
    }

    @Override
    protected MapCodec<? extends PowerableBlock> getCodec() {
        return CODEC;
    }

    public static Stream<PendantBlock> streamPendantLights() {
        return PENDANTS.stream();
    }

    @Override
    public boolean hasSidedTransparency(BlockState state) {
        return true;
    }

    @Override
    public void setPowered(World world, BlockPos lightPos, boolean powered) {
        BlockState state = world.getBlockState(lightPos);
        world.setBlockState(lightPos, state.with(LIT, powered).with(POWERLOCKED,powered));
    }

    @Override
    public boolean isShapeFullCube(BlockState state, BlockView world, BlockPos pos) {
        return false;
    }

    public static BlockState canConnect(BlockState state, WorldView world, BlockPos pos) {
        boolean up = world.getBlockState(pos.up()).getBlock() instanceof PendantBlock;
        boolean down = world.getBlockState(pos.down()).getBlock() instanceof PendantBlock;
        if (up) {
            return state.with(UP, true).with(DOWN, down).with(LIT, (world.getBlockState(pos.up()).get(LIT)));
        }
        else {
            return state.with(UP, false).with(DOWN, down);
        }
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if (!state.canPlaceAt(world, pos)) {
            return Blocks.AIR.getDefaultState();
        }
        return direction.getAxis().isVertical() ? canConnect(state, world, pos) : super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return super.getFluidState(state);
    }

    private static final VoxelShape single = VoxelShapes.union(createCuboidShape(4, 0, 4,12, 7, 12),createCuboidShape(5, 5, 5,11, 9, 11),createCuboidShape(7.5, 9, 7.5,8.5, 15.5, 8.5),createCuboidShape(6.5, 15.5, 6.5,9.5, 16, 9.5));
    private static final VoxelShape up = VoxelShapes.union(createCuboidShape(4, 0, 4,12, 7, 12),createCuboidShape(5, 5, 5,11, 9, 11),createCuboidShape(7.5, 9, 7.5,8.5, 16, 8.5));
    private static final VoxelShape middle = createCuboidShape(7.5, 0, 7.5,8.5, 16, 8.5);
    private static final VoxelShape down = VoxelShapes.union(createCuboidShape(7.5, 0, 7.5,8.5, 15.5, 8.5),createCuboidShape(6.5, 15.5, 6.5,9.5, 16, 9.5));
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (state.get(UP) && state.get(DOWN)) {
            return middle;
        }
        else if (state.get(UP)){
            return up;
        }
        else if (state.get(DOWN)){
            return down;
        }
        return single;
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        boolean powered = ctx.getWorld().isReceivingRedstonePower(ctx.getBlockPos());
        BlockState state = this.getDefaultState().with(LIT, powered);
        return canConnect(state, ctx.getWorld(), ctx.getBlockPos());
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
        if (world.isClient) {
            return;
        }
        if(world.getBlockState(pos.up()).getBlock() instanceof PendantBlock) {
            return;
        }

        boolean bl = (state.get(LIT));
        if (bl != world.isReceivingRedstonePower(pos)) {
            if (bl) {
                world.scheduleBlockTick(pos, this, 4);
            } else {
                world.setBlockState(pos, state.cycle(LIT), Block.NOTIFY_LISTENERS);
            }
        }
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        Direction direction = Direction.UP;
        return Block.sideCoversSmallSquare(world, pos.offset(direction), direction.getOpposite()) || world.getBlockState(pos.offset(direction)).getBlock() instanceof PendantBlock;
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (state.get(LIT) && !world.isReceivingRedstonePower(pos) && !state.get(POWERLOCKED)) {
            world.setBlockState(pos, state.cycle(LIT), Block.NOTIFY_LISTENERS);
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(UP);
        builder.add(DOWN);
        builder.add(LIT);
        builder.add(POWERLOCKED);
    }

    @Override
    public boolean canPathfindThrough(BlockState state, NavigationType type) {
        return false;
    }

    @Override
    public RenderLayer getCustomRenderLayer() {
        return RenderLayer.getTranslucent();
    }
}
