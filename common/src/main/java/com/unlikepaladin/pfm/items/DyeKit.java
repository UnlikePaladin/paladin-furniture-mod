package com.unlikepaladin.pfm.items;

import com.unlikepaladin.pfm.blocks.DyeableFurnitureBlock;
import com.unlikepaladin.pfm.blocks.blockentities.DyeableFurnitureBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.registry.Registries;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.registry.Registry;
import net.minecraft.world.World;

public class DyeKit extends Item {
    private final DyeColor color;

    public DyeKit(Settings settings, DyeColor color) {
        super(settings);
        this.color = color;
    }

    public DyeColor getColor() {
        return this.color;
    }
    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        PlayerEntity playerEntity = context.getPlayer();
        ItemStack stack = context.getStack();
        BlockPos blockPos = context.getBlockPos();
        World world = context.getWorld();
        BlockState blockState = world.getBlockState(blockPos);
        if (playerEntity.isSneaking()) {
            boolean dyed = false;
            if(blockState.getBlock() instanceof DyeableFurnitureBlock && stack.getItem() instanceof DyeKit) {
                world.playSound(null, blockPos, SoundEvents.ITEM_DYE_USE, SoundCategory.BLOCKS, 1.0F, 1.0F);
                String newBlock= blockState.getBlock().toString();
                newBlock = newBlock.replace(((DyeableFurnitureBlock) blockState.getBlock()).getPFMColor().toString(), getColor().toString()).replace("block.pfm.","").replace("Block{", "").replace("}", "");
                BlockState blockState1 = Registries.BLOCK.get(Identifier.of(newBlock)).getStateWithProperties(blockState);
                world.setBlockState(blockPos, blockState1, 3);
                stack.decrement(1);
                dyed = true;
            }
            if (world.getBlockEntity(blockPos) instanceof DyeableFurnitureBlockEntity && stack.getItem() instanceof DyeKit) {
                world.playSound(null, blockPos, SoundEvents.ITEM_DYE_USE, SoundCategory.BLOCKS, 1.0F, 1.0F);
                DyeableFurnitureBlockEntity dyeableFurnitureBlockEntity = (DyeableFurnitureBlockEntity) world.getBlockEntity(blockPos);
                dyeableFurnitureBlockEntity.setPFMColor(getColor());
                world.updateListeners(blockPos, blockState, blockState, Block.NOTIFY_ALL);
                if (!dyed)
                    stack.decrement(1);
                dyed = true;
            }
            if (dyed)
                return ActionResult.CONSUME;
        }
        return ActionResult.PASS;
    }


    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (entity instanceof SheepEntity) {
            SheepEntity sheepEntity = (SheepEntity)entity;
            if (sheepEntity.isAlive() && !sheepEntity.isSheared() && sheepEntity.getColor() != ((DyeKit) stack.getItem()).getColor()) {
                sheepEntity.getEntityWorld().playSoundFromEntity(user, sheepEntity, SoundEvents.ITEM_DYE_USE, SoundCategory.PLAYERS, 1.0F, 1.0F);
                if (!user.getWorld().isClient) {
                    sheepEntity.setColor(this.color);
                    stack.decrement(1);
                }

                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }
}
