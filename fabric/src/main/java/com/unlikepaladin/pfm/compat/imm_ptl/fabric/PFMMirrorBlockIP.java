package com.unlikepaladin.pfm.compat.imm_ptl.fabric;
/*
import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.blocks.MirrorBlock;
import com.unlikepaladin.pfm.compat.imm_ptl.fabric.entity.PFMMirrorEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PFMMirrorBlockIP extends MirrorBlock {
    protected PFMMirrorBlockIP(Settings settings) {
        super(settings);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        if (PaladinFurnitureMod.getPFMConfig().doImmersivePortalsMirrorsSpawn()) {
            if (!world.isClient && world.getNonSpectatingEntities(PFMMirrorEntity.class, new Box(pos)).isEmpty()) {
                //PFMMirrorEntity.createMirror((ServerWorld) world, pos, state.get(FACING).getOpposite());
           }
        }
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBreak(world, pos, state, player);
        if (PaladinFurnitureMod.getPFMConfig().doImmersivePortalsMirrorsSpawn()) {
    //    List<PFMMirrorEntity> mirrorBlockEntities;
       //     if (!world.isClient && !(mirrorBlockEntities = world.getNonSpectatingEntities(PFMMirrorEntity.class, new Box(pos))).isEmpty()) {
                mirrorBlockEntities.forEach(pfmMirrorEntity -> {
                    ((PFMMirrorEntity)pfmMirrorEntity).remove(Entity.RemovalReason.KILLED);
                });
                world.updateNeighbors(pos, state.getBlock());
            }
      //  }
        return super.onBreak(world, pos, state, player);
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if (PaladinFurnitureMod.getPFMConfig().doImmersivePortalsMirrorsSpawn() && !world.isClient()) {
            List<PFMMirrorEntity> mirrorBlockEntities = new ArrayList<>();
            if (canConnect(neighborState, state)) {
                mirrorBlockEntities.addAll(world.getNonSpectatingEntities(PFMMirrorEntity.class, new Box(neighborPos)));
            }
            if (!(world.getNonSpectatingEntities(PFMMirrorEntity.class, new Box(pos)).isEmpty())) {
                mirrorBlockEntities.addAll(world.getNonSpectatingEntities(PFMMirrorEntity.class, new Box(pos)));
                mirrorBlockEntities.forEach(pfmMirrorEntity -> {
                    ((Entity)pfmMirrorEntity).remove(Entity.RemovalReason.KILLED);
                });
            }
       //     PFMMirrorEntity.createMirror((ServerWorld) world, pos, state.get(FACING).getOpposite());
          //  world.updateNeighbors(pos, state.getBlock());
        }
        return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }
}
*/