package com.unlikepaladin.pfm.compat.cookingforblockheads.forge;

import com.mojang.serialization.MapCodec;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.cookingforblockheads.block.entity.CookingTableBlockEntity;
import net.blay09.mods.cookingforblockheads.item.ModItems;
import net.blay09.mods.cookingforblockheads.util.ItemUtils;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class PFMCookingTableBlock extends BlockWithEntity {
    protected PFMCookingTableBlock(Settings arg) {
        super(arg);
    }

    MapCodec<PFMCookingTableBlock> CODEC = createCodec(PFMCookingTableBlock::new);
    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult blockHitResult) {
        ItemStack heldItem = player.getStackInHand(hand);
        CookingTableBlockEntity blockEntity = (CookingTableBlockEntity)world.getBlockEntity(pos);
        if (!heldItem.isEmpty()) {
            if (blockEntity != null) {
                if (!blockEntity.hasNoFilterBook() && heldItem.getItem() == ModItems.noFilterBook) {
                    blockEntity.setNoFilterBook(heldItem.split(1));
                    return ActionResult.SUCCESS;
                }
            }
        } else if (player.isSneaking() && blockEntity != null) {
            ItemStack noFilterBook = blockEntity.getNoFilterBook();
            if (!noFilterBook.isEmpty()) {
                if (!player.getInventory().insertStack(noFilterBook)) {
                    player.dropItem(noFilterBook, false);
                }
                blockEntity.setNoFilterBook(ItemStack.EMPTY);
                return ActionResult.SUCCESS;
            }
        }
        if (!world.isClient) {
            Balm.getNetworking().openGui(player, blockEntity);
        }
        return ActionResult.SUCCESS;
    }

    public void onStateReplaced(BlockState state, World level, BlockPos pos, BlockState newState, boolean isMoving) {
        CookingTableBlockEntity tileEntity = (CookingTableBlockEntity) level.getBlockEntity(pos);
        if (tileEntity != null && !state.isOf(newState.getBlock())) {
            ItemUtils.spawnItemStack(level, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, tileEntity.getNoFilterBook());
        }
        super.onStateReplaced(state, level, pos, newState, isMoving);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CookingTableBlockEntity(pos, state);
    }

    private static final VoxelShape SHAPE = VoxelShapes.union(createCuboidShape(3, 0, 3, 13,1,13));
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
}
