package com.unlikepaladin.pfm.blocks.behavior;

import com.mojang.serialization.Codec;
import com.unlikepaladin.pfm.blocks.BasicBathtubBlock;
import com.unlikepaladin.pfm.registry.Statistics;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BannerPatternsComponent;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.potion.Potions;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Map;
import java.util.function.Predicate;

public interface BathtubBehavior {
    BathtubBehavior FILL_TUB_WITH_WATER = (state, world, pos, player, hand, stack) -> BathtubBehavior.fillTub(world, pos, player, hand, stack, state, SoundEvents.ITEM_BUCKET_EMPTY, true);
    Map<Item, BathtubBehavior> TUB_BEHAVIOR = BathtubBehavior.createMap();

    Codec<Map<Item, BathtubBehavior>> CODEC = Codec.unit(TUB_BEHAVIOR);
    static Object2ObjectOpenHashMap<Item, BathtubBehavior> createMap() {
        return (Object2ObjectOpenHashMap)Util.make(new Object2ObjectOpenHashMap(), (map) -> {
            map.defaultReturnValue(null);});
    }

    public ActionResult interact(BlockState var1, World var2, BlockPos var3, PlayerEntity var4, Hand var5, ItemStack var6);

    BathtubBehavior CLEAN_SHULKER_BOX = (state, world, pos, player, hand, stack) -> {
        if (state.get(BasicBathtubBlock.LEVEL_8) == 0) {
            return ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION;
        }
        Block block = Block.getBlockFromItem(stack.getItem());
        if (!(block instanceof ShulkerBoxBlock)) {
            return ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION;
        }
        if (!world.isClient) {
            player.setStackInHand(hand, stack.copyComponentsToNewStack(Blocks.SHULKER_BOX, 1));
            player.incrementStat(Stats.CLEAN_SHULKER_BOX);
            BasicBathtubBlock.decrementFluidLevel(state, world, pos);
        }
        return ActionResult.SUCCESS;
    };

    BathtubBehavior CLEAN_DYEABLE_ITEM = (state, world, pos, player, hand, stack) -> {
       if (state.get(BasicBathtubBlock.LEVEL_8) == 0) {
           return ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION;
       }
        if (!stack.isIn(ItemTags.DYEABLE)) {
            return ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION;
        }
        if (!stack.contains(DataComponentTypes.DYED_COLOR)) {
            return ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION;
        }
        if (!world.isClient) {
            stack.remove(DataComponentTypes.DYED_COLOR);
            player.incrementStat(Stats.CLEAN_ARMOR);
            BasicBathtubBlock.decrementFluidLevel(state, world, pos);
        }
        return ActionResult.SUCCESS;
};
    BathtubBehavior CLEAN_BANNER = (state, world, pos, player, hand, stack) -> {
        BannerPatternsComponent bannerPatternsComponent = stack.getOrDefault(DataComponentTypes.BANNER_PATTERNS, BannerPatternsComponent.DEFAULT);
        if (bannerPatternsComponent.layers().isEmpty() || state.get(BasicBathtubBlock.LEVEL_8) == 0) {
            return ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION;
        }
        if (!world.isClient) {
            ItemStack itemStack = stack.copyWithCount(1);
            itemStack.set(DataComponentTypes.BANNER_PATTERNS, bannerPatternsComponent.withoutTopLayer());
            if (!player.getAbilities().creativeMode) {
                stack.decrement(1);
            }
            if (stack.isEmpty()) {
                player.setStackInHand(hand, itemStack);
            } else if (player.getInventory().insertStack(itemStack)) {
                player.playerScreenHandler.syncState();
            } else {
                player.dropItem(itemStack, false);
            }
            player.incrementStat(Stats.CLEAN_BANNER);
            BasicBathtubBlock.decrementFluidLevel(state, world, pos);
        }
        return ActionResult.SUCCESS;
    };

    static ActionResult fillTub(World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack, BlockState state, SoundEvent soundEvent, boolean usedBucket) {
        if (!world.isClient) {
            player.incrementStat(Statistics.BATHTUB_FILLED);
            if (usedBucket) {
                Item item = stack.getItem();
                player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, new ItemStack(Items.BUCKET)));
                player.incrementStat(Stats.USED.getOrCreateStat(item));
            }
            int newLevel = (world.getBlockState(pos).get(BasicBathtubBlock.LEVEL_8) + 4);
            if (newLevel >= 0 && newLevel <= 8)  {
                world.setBlockState(pos, state.with(BasicBathtubBlock.LEVEL_8, newLevel));
            } else {
                world.setBlockState(pos, state.with(BasicBathtubBlock.LEVEL_8, 8));
            }
            world.playSound(null, pos, soundEvent, SoundCategory.BLOCKS, 1.0f, 1.0f);
        }
        return ActionResult.SUCCESS;
    }

    static ActionResult emptyTub(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack, ItemStack output, Predicate<BlockState> predicate, SoundEvent soundEvent) {
        if (!predicate.test(state)) {
            return ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION;
        }
        if (!world.isClient) {
            Item item = stack.getItem();
            player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, output));
            player.incrementStat(Statistics.USE_BATHTUB);
            player.incrementStat(Stats.USED.getOrCreateStat(item));
            int newLevel = (world.getBlockState(pos).get(BasicBathtubBlock.LEVEL_8) - 4);
            if (newLevel >= 0)  {
                world.setBlockState(pos, state.with(BasicBathtubBlock.LEVEL_8, newLevel));
            } else {
                world.setBlockState(pos, state.with(BasicBathtubBlock.LEVEL_8, 0));
            }
            world.playSound(null, pos, soundEvent, SoundCategory.BLOCKS, 1.0f, 1.0f);
        }
        return ActionResult.SUCCESS;
    }

    static void registerBucketBehavior(Map<Item, BathtubBehavior> behavior) {
        behavior.put(Items.WATER_BUCKET, FILL_TUB_WITH_WATER);
    }
    static void registerBehavior() {
        BathtubBehavior.registerBucketBehavior(TUB_BEHAVIOR);
        TUB_BEHAVIOR.put(Items.BUCKET, (state2, world, pos, player, hand, stack) -> BathtubBehavior.emptyTub(state2, world, pos, player, hand, stack, new ItemStack(Items.WATER_BUCKET), state -> state.get(BasicBathtubBlock.LEVEL_8) >= 4, SoundEvents.ITEM_BUCKET_FILL));
        TUB_BEHAVIOR.put(Items.GLASS_BOTTLE, (state, world, pos, player, hand, stack) -> {
            if (!world.isClient) {
                if (state.get(BasicBathtubBlock.LEVEL_8) == 0) {
                    return ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION;
                }
                Item item = stack.getItem();
                player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, PotionContentsComponent.createStack(Items.POTION, Potions.WATER)));
                player.incrementStat(Statistics.USE_BATHTUB);
                player.incrementStat(Stats.USED.getOrCreateStat(item));
                BasicBathtubBlock.decrementFluidLevel(state, world, pos);
                world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, 1.0f, 1.0f);
            }
            return ActionResult.SUCCESS;
        });
        TUB_BEHAVIOR.put(Items.POTION, (state, world, pos, player, hand, stack) -> {
            PotionContentsComponent potionContentsComponent = stack.get(DataComponentTypes.POTION_CONTENTS);
            if (state.get(BasicBathtubBlock.LEVEL_8) == 8 || potionContentsComponent != null && !potionContentsComponent.matches(Potions.WATER)) {
                return ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION;
            }
            if (!world.isClient) {
                player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, new ItemStack(Items.GLASS_BOTTLE)));
                player.incrementStat(Statistics.USE_BATHTUB);
                player.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));
                world.setBlockState(pos, state.cycle(BasicBathtubBlock.LEVEL_8));
                world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0f, 1.0f);
            }
            return ActionResult.SUCCESS;
        });
        TUB_BEHAVIOR.put(Items.LEATHER_BOOTS, CLEAN_DYEABLE_ITEM);
        TUB_BEHAVIOR.put(Items.LEATHER_LEGGINGS, CLEAN_DYEABLE_ITEM);
        TUB_BEHAVIOR.put(Items.LEATHER_CHESTPLATE, CLEAN_DYEABLE_ITEM);
        TUB_BEHAVIOR.put(Items.LEATHER_HELMET, CLEAN_DYEABLE_ITEM);
        TUB_BEHAVIOR.put(Items.LEATHER_HORSE_ARMOR, CLEAN_DYEABLE_ITEM);
        TUB_BEHAVIOR.put(Items.WHITE_BANNER, CLEAN_BANNER);
        TUB_BEHAVIOR.put(Items.GRAY_BANNER, CLEAN_BANNER);
        TUB_BEHAVIOR.put(Items.BLACK_BANNER, CLEAN_BANNER);
        TUB_BEHAVIOR.put(Items.BLUE_BANNER, CLEAN_BANNER);
        TUB_BEHAVIOR.put(Items.BROWN_BANNER, CLEAN_BANNER);
        TUB_BEHAVIOR.put(Items.CYAN_BANNER, CLEAN_BANNER);
        TUB_BEHAVIOR.put(Items.GREEN_BANNER, CLEAN_BANNER);
        TUB_BEHAVIOR.put(Items.LIGHT_BLUE_BANNER, CLEAN_BANNER);
        TUB_BEHAVIOR.put(Items.LIGHT_GRAY_BANNER, CLEAN_BANNER);
        TUB_BEHAVIOR.put(Items.LIME_BANNER, CLEAN_BANNER);
        TUB_BEHAVIOR.put(Items.MAGENTA_BANNER, CLEAN_BANNER);
        TUB_BEHAVIOR.put(Items.ORANGE_BANNER, CLEAN_BANNER);
        TUB_BEHAVIOR.put(Items.PINK_BANNER, CLEAN_BANNER);
        TUB_BEHAVIOR.put(Items.PURPLE_BANNER, CLEAN_BANNER);
        TUB_BEHAVIOR.put(Items.RED_BANNER, CLEAN_BANNER);
        TUB_BEHAVIOR.put(Items.YELLOW_BANNER, CLEAN_BANNER);
        TUB_BEHAVIOR.put(Items.WHITE_SHULKER_BOX, CLEAN_SHULKER_BOX);
        TUB_BEHAVIOR.put(Items.GRAY_SHULKER_BOX, CLEAN_SHULKER_BOX);
        TUB_BEHAVIOR.put(Items.BLACK_SHULKER_BOX, CLEAN_SHULKER_BOX);
        TUB_BEHAVIOR.put(Items.BLUE_SHULKER_BOX, CLEAN_SHULKER_BOX);
        TUB_BEHAVIOR.put(Items.BROWN_SHULKER_BOX, CLEAN_SHULKER_BOX);
        TUB_BEHAVIOR.put(Items.CYAN_SHULKER_BOX, CLEAN_SHULKER_BOX);
        TUB_BEHAVIOR.put(Items.GREEN_SHULKER_BOX, CLEAN_SHULKER_BOX);
        TUB_BEHAVIOR.put(Items.LIGHT_BLUE_SHULKER_BOX, CLEAN_SHULKER_BOX);
        TUB_BEHAVIOR.put(Items.LIGHT_GRAY_SHULKER_BOX, CLEAN_SHULKER_BOX);
        TUB_BEHAVIOR.put(Items.LIME_SHULKER_BOX, CLEAN_SHULKER_BOX);
        TUB_BEHAVIOR.put(Items.MAGENTA_SHULKER_BOX, CLEAN_SHULKER_BOX);
        TUB_BEHAVIOR.put(Items.ORANGE_SHULKER_BOX, CLEAN_SHULKER_BOX);
        TUB_BEHAVIOR.put(Items.PINK_SHULKER_BOX, CLEAN_SHULKER_BOX);
        TUB_BEHAVIOR.put(Items.PURPLE_SHULKER_BOX, CLEAN_SHULKER_BOX);
        TUB_BEHAVIOR.put(Items.RED_SHULKER_BOX, CLEAN_SHULKER_BOX);
        TUB_BEHAVIOR.put(Items.YELLOW_SHULKER_BOX, CLEAN_SHULKER_BOX);
        }
    }
