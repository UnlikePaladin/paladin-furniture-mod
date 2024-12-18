package com.unlikepaladin.pfm.blocks;

import com.unlikepaladin.pfm.blocks.blockentities.PFMBedBlockEntity;
import com.unlikepaladin.pfm.client.PFMBuiltinItemRendererExtension;
import com.unlikepaladin.pfm.data.FurnitureBlock;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.BedPart;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.*;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.tick.ScheduledTickView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static com.unlikepaladin.pfm.blocks.LogTableBlock.rotateShape;

public class SimpleBedBlock extends BedBlock implements DyeableFurnitureBlock, PFMBuiltinItemRendererExtension {
    private static final List<FurnitureBlock> SIMPLE_BEDS = new ArrayList<>();
    private final DyeColor color;
    public SimpleBedBlock(DyeColor color, Settings settings) {
        super(color, settings);
        setDefaultState(this.getStateManager().getDefaultState().with(FACING, Direction.NORTH).with(PART, BedPart.FOOT).with(OCCUPIED, false));
        if(this.getClass().isAssignableFrom(SimpleBedBlock.class)){
            String bedColor = color.getName();
            SIMPLE_BEDS.add(new FurnitureBlock(this, bedColor+"_simple_bed"));
        }
        this.color = color;
    }

    public static Stream<FurnitureBlock> streamSimpleBeds() {
        return SIMPLE_BEDS.stream();
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState blockState = this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing());
        Direction direction = ctx.getHorizontalPlayerFacing();
        BlockPos blockPos = ctx.getBlockPos();
        BlockPos blockPos2 = blockPos.offset(direction);
        if (ctx.getWorld().getBlockState(blockPos2).canReplace(ctx)) {
            return blockState;
        }
        return null;
    }

    private static Direction getDirectionTowardsOtherPart(BedPart part, Direction direction) {
        return part == BedPart.FOOT ? direction : direction.getOpposite();
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.CONSUME;
        }
        if (state.get(PART) != BedPart.HEAD && !((state = world.getBlockState(pos = pos.offset(state.get(FACING)))).getBlock() instanceof SimpleBedBlock)) {
            return ActionResult.CONSUME;
        }
        if (!BedBlock.isBedWorking(world)) {
            world.removeBlock(pos, false);
            BlockPos blockPos = pos.offset(state.get(FACING).getOpposite());
            if (world.getBlockState(blockPos).isOf(this)) {
                world.removeBlock(blockPos, false);
            }
            world.createExplosion(null, world.getDamageSources().badRespawnPoint(pos.toCenterPos()), null, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, 5.0f, true, World.ExplosionSourceType.BLOCK);
            return ActionResult.SUCCESS;
        }
        if (state.get(OCCUPIED)) {
            if (!this.isFree(world, pos)) {
                player.sendMessage(Text.translatable("block.minecraft.bed.occupied"), true);
            }
            return ActionResult.SUCCESS;
        }
        player.trySleep(pos).ifLeft(reason -> {
            if (reason != null) {
                player.sendMessage(reason.getMessage(), true);
            }
        });
        return ActionResult.SUCCESS;
    }

    private boolean isFree(World world, BlockPos pos) {
        List<VillagerEntity> list = world.getEntitiesByClass(VillagerEntity.class, new Box(pos), LivingEntity::isSleeping);
        if (list.isEmpty()) {
            return false;
        }
        list.get(0).wakeUp();
        return true;
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if (direction == getDirectionTowardsOtherPart(state.get(PART), state.get(FACING))) {
            if (neighborState.getBlock() instanceof SimpleBedBlock && neighborState.get(PART) != state.get(PART)) {
                return state.with(OCCUPIED, neighborState.get(OCCUPIED));
            }
            return Blocks.AIR.getDefaultState();
        }
        return state;
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        BlockPos blockPos;
        BlockState blockState;
        BedPart bedPart;
        if (!world.isClient && player.isCreative() && (bedPart = state.get(PART)) == BedPart.FOOT && (blockState = world.getBlockState(blockPos = pos.offset(getDirectionTowardsOtherPart(bedPart, state.get(FACING))))).isOf(this) && blockState.get(PART) == BedPart.HEAD) {
            world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL | Block.SKIP_DROPS);
            world.syncWorldEvent(player, WorldEvents.BLOCK_BROKEN, blockPos, Block.getRawIdFromState(blockState));
        }
        this.spawnBreakParticles(world, player, pos, state);
        if (world instanceof ServerWorld serverWorld && state.isIn(BlockTags.GUARDED_BY_PIGLINS)) {
            PiglinBrain.onGuardedBlockInteracted(serverWorld, player, false);
        }
        world.emitGameEvent(player, GameEvent.BLOCK_DESTROY, pos);
        return super.onBreak(world, pos, state, player);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        super.appendProperties(stateManager);
    }

    public int getFlammability(BlockState state, BlockView world, BlockPos pos, Direction face) {
        if (AbstractSittableBlock.isWoodBased(state)) {
            return 20;
        }
        return 0;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return super.getFluidState(state);
    }

    static final VoxelShape HEAD = VoxelShapes.union(createCuboidShape(0, 9, 0,16, 14, 3),createCuboidShape(0, 0, 0,16, 9, 16));
    static final VoxelShape HEAD_SOUTH = rotateShape(Direction.NORTH, Direction.SOUTH, HEAD);
    static final VoxelShape HEAD_EAST = rotateShape(Direction.NORTH, Direction.EAST, HEAD);
    static final VoxelShape HEAD_WEST = rotateShape(Direction.NORTH, Direction.WEST, HEAD);

    static final VoxelShape FOOT_EAST = VoxelShapes.union(createCuboidShape(0, 9, 0,3, 10, 16),createCuboidShape(0, 0, 0,16, 9, 16));
    static final VoxelShape FOOT_SOUTH = rotateShape(Direction.EAST, Direction.SOUTH, FOOT_EAST);
    static final VoxelShape FOOT_WEST = rotateShape(Direction.EAST, Direction.WEST, FOOT_EAST);
    static final VoxelShape FOOT_NORTH = rotateShape(Direction.EAST, Direction.NORTH, FOOT_EAST);

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return getBedShape(state.get(FACING), state.get(PART), HEAD, FOOT_NORTH, HEAD_EAST, FOOT_EAST, HEAD_WEST, FOOT_WEST, HEAD_SOUTH, FOOT_SOUTH);
    }

    static VoxelShape getBedShape(Direction direction, BedPart bedPart2, VoxelShape head, VoxelShape footNorth, VoxelShape headEast, VoxelShape footEast, VoxelShape headWest, VoxelShape footWest, VoxelShape headSouth, VoxelShape footSouth) {
        switch (direction){
            case NORTH -> {
                if(bedPart2 == BedPart.HEAD){
                    return head;
                }
                return footNorth;
            }
            case EAST -> {
                if(bedPart2 == BedPart.HEAD){
                    return headEast;
                }
                return footEast;
            }
            case WEST -> {
                if(bedPart2 == BedPart.HEAD){
                    return headWest;
                }
                return footWest;
            }
            default -> {
                if(bedPart2 == BedPart.HEAD){
                    return headSouth;
                }
                return footSouth;
            }
        }
    }

    @Override
    public boolean canPathfindThrough(BlockState state, NavigationType type) {
        return false;
    }

    @Override
    public DyeColor getPFMColor() {
        return color;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PFMBedBlockEntity(pos, state, this.color);
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }
}
