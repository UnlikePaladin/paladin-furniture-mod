package com.unlikepaladin.pfm.blocks.fabric;

import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.blocks.blockentities.StoveBlockEntity;
import com.unlikepaladin.pfm.blocks.blockentities.fabric.StoveBlockEntityImpl;
import com.unlikepaladin.pfm.compat.cookingforblockheads.fabric.PFMCookingForBlockHeadsCompat;
import com.unlikepaladin.pfm.registry.BlockEntities;
import com.unlikepaladin.pfm.registry.Statistics;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class StoveBlockImpl {
    public static BlockEntity getBlockEntity(BlockPos pos, BlockState state) {
        return PaladinFurnitureMod.getModList().contains("cookingforblockheads") ? PFMCookingForBlockHeadsCompat.getStoveBlockEntity(pos , state) : new StoveBlockEntityImpl(BlockEntities.STOVE_BLOCK_ENTITY, pos, state);
    }

    public static void openMenuScreen(World world, BlockPos pos, PlayerEntity player) {
        if (PaladinFurnitureMod.getModList().contains("cookingforblockheads")) {
            PFMCookingForBlockHeadsCompat.openMenuScreen(world, pos, player);
        } else {
            NamedScreenHandlerFactory screenHandlerFactory = world.getBlockState(pos).createScreenHandlerFactory(world, pos);
            if (screenHandlerFactory != null) {
                // With this call the server will request the client to open the appropriate Screenhandler
                player.openHandledScreen(screenHandlerFactory);
                player.incrementStat(Statistics.STOVE_OPENED);
            }
        }
    }

    public static <T extends BlockEntity> BlockEntityTicker<T> getModdedTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (PaladinFurnitureMod.getModList().contains("cookingforblockheads")) {
            return PFMCookingForBlockHeadsCompat.getStoveTicker(world, type);
        } else {
            if (world.isClient) {
                return checkType(type, BlockEntities.STOVE_BLOCK_ENTITY, StoveBlockEntity::clientTick);
            } else {
                return checkType(type, BlockEntities.STOVE_BLOCK_ENTITY, StoveBlockEntity::litServerTick);
            }
        }
    }

    @Nullable
    public static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> checkType(BlockEntityType<A> givenType, BlockEntityType<E> expectedType, BlockEntityTicker<? super E> ticker) {
        return expectedType == givenType ? (BlockEntityTicker<A>) ticker : null;
    }

    public static ActionResult onUseCookingForBlockheads(BlockState blockState, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult blockHitResult) {
        return PFMCookingForBlockHeadsCompat.onUseStove(blockState, world, pos, player, hand, blockHitResult);
    }
}
