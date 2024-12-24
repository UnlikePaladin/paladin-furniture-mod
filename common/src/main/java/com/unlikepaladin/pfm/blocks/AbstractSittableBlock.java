package com.unlikepaladin.pfm.blocks;

import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.entity.ChairEntity;
import com.unlikepaladin.pfm.registry.Entities;
import com.unlikepaladin.pfm.registry.Statistics;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractSittableBlock extends HorizontalFacingBlock {
    private final BlockState baseBlockState;
    private final Block baseBlock;

    public AbstractSittableBlock(Settings settings) {
        super(settings);
        this.baseBlockState = this.getDefaultState();
        this.baseBlock = baseBlockState.getBlock();
        setDefaultState(this.getStateManager().getDefaultState().with(Properties.HORIZONTAL_FACING, Direction.NORTH));

        this.height = 0.36f;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(Properties.HORIZONTAL_FACING);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction facing = PaladinFurnitureMod.getPFMConfig().doChairsFacePlayer() ? ctx.getPlayerFacing() : ctx.getPlayerFacing().getOpposite();
            return this.getDefaultState().with(Properties.HORIZONTAL_FACING, facing);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return super.getFluidState(state);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (state.contains(Properties.WATERLOGGED)) {
            if (state.get(Properties.WATERLOGGED))
                world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    public float height;
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.CONSUME;
        }

        if (player.isSpectator() || player.isSneaking()) {
            return ActionResult.FAIL;
        }

        List<ChairEntity> active = world.getEntitiesByClass(ChairEntity.class, new Box(pos), Entity::hasPassengers);
        if (active != null && !active.isEmpty()) {
            List<Entity> hasPassenger = new ArrayList<>();
            active.forEach(chairEntity -> hasPassenger.add(chairEntity.getPrimaryPassenger()));
            if (hasPassenger.stream().anyMatch(entity -> entity instanceof PlayerEntity)) {
                return ActionResult.FAIL;
            }
            else if (!active.isEmpty()) {
                hasPassenger.forEach(Entity::stopRiding);
                return ActionResult.SUCCESS;
            }
        }

        if (sitEntity(world, pos, state, player) == ActionResult.SUCCESS) {
            if (!(state.getBlock() instanceof BasicToiletBlock))
                player.incrementStat(Statistics.CHAIR_USED);
            return ActionResult.SUCCESS;
        }
        return ActionResult.CONSUME;
    }


    public ActionResult sitEntity(World world, BlockPos pos, BlockState state, Entity entityToSit) {
        double px;
        double pz;
        if (state.getBlock() instanceof BasicChairBlock) {
            Direction direction = state.get(FACING);
            if (state.get(BasicChairBlock.TUCKED)) {
                switch (direction) {
                    case EAST : {
                        px = pos.getX() + 0.1;
                        pz = pos.getZ() + 0.5;
                        break;
                    }
                    case WEST : {
                        px = pos.getX() + 0.9;
                        pz = pos.getZ() + 0.5;
                        break;
                    }
                    case SOUTH : {
                        px = pos.getX() + 0.5;
                        pz = pos.getZ() + 0.1;
                        break;
                    }
                    default : {
                        px = pos.getX() + 0.5;
                        pz = pos.getZ() + 0.9;
                        break;
                    }
                }
            }
            else {
                px =  pos.getX() + 0.5;
                pz = pos.getZ() + 0.5;
            }
        }
        else {
            px =  pos.getX() + 0.5;
            pz = pos.getZ() + 0.5;
        }
        double py = pos.getY() + this.height;
        float yaw = state.get(FACING).getOpposite().asRotation();
        ChairEntity chairEntity = Entities.CHAIR.create(world);
        chairEntity.refreshPositionAndAngles(px, py, pz, yaw, 0);
        chairEntity.setNoGravity(true);
        chairEntity.setSilent(true);
        chairEntity.setInvisible(false);
        chairEntity.setInvulnerable(true);
        chairEntity.setAiDisabled(true);
        if (world.spawnEntity(chairEntity)) {
            entityToSit.startRiding(chairEntity, true);
            entityToSit.setHeadYaw(yaw);
            chairEntity.setBodyYaw(yaw);
            chairEntity.setHeadYaw(yaw);
            return ActionResult.SUCCESS;
        }
        return ActionResult.CONSUME;
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        super.onEntityCollision(state, world, pos, entity);
        List<ChairEntity> active = world.getEntitiesByClass(ChairEntity.class, new Box(pos), Entity::hasPassengers);
        if (active == null || !active.isEmpty())
            return;

        if (entity instanceof PlayerEntity || entity instanceof IronGolemEntity || entity instanceof AbstractMinecartEntity || entity.hasVehicle() || !(entity instanceof LivingEntity) || entity instanceof ChairEntity) {
            return;
        }
        if (!PaladinFurnitureMod.getPFMConfig().doMobsSitOnChairs())
            return;

        sitEntity(world, pos, state, entity);
    }

    public int getFlammability(BlockState state, BlockView world, BlockPos pos, Direction face) {
        if (state.getMaterial() == Material.WOOD || state.getMaterial() == Material.WOOL) {
            return 20;
        }
        return 0;
    }

    @Override
    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return false;
    }
}

