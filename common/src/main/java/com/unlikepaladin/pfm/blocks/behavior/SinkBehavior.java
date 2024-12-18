package com.unlikepaladin.pfm.blocks.behavior;

import com.unlikepaladin.pfm.blocks.BasicBathtubBlock;
import com.unlikepaladin.pfm.blocks.KitchenSinkBlock;
import com.unlikepaladin.pfm.registry.Statistics;
import net.minecraft.block.*;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BannerPatternsComponent;
import net.minecraft.component.type.DyedColorComponent;
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
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import java.util.Map;
import java.util.function.Predicate;

public interface SinkBehavior extends CauldronBehavior {

    SinkBehavior FILL_SINK_WITH_WATER = (state, world, pos, player, hand, stack) -> SinkBehavior.fillCauldron(world, pos, player, hand, stack, state.with(KitchenSinkBlock.LEVEL_4, 3), SoundEvents.ITEM_BUCKET_EMPTY);
    CauldronBehaviorMap WATER_SINK_BEHAVIOR = CauldronBehavior.createMap("sink");
    CauldronBehavior CLEAN_SHULKER_BOX = (state, world, pos, player, hand, stack) -> {
        if (state.get(KitchenSinkBlock.LEVEL_4) == 0) {
            return ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION;
        }
        Block block = Block.getBlockFromItem(stack.getItem());
        if (!(block instanceof ShulkerBoxBlock)) {
            return ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION;
        }
        if (!world.isClient) {
            player.setStackInHand(hand, stack.copyComponentsToNewStack(Blocks.SHULKER_BOX, 1));
            player.incrementStat(Stats.CLEAN_SHULKER_BOX);
            KitchenSinkBlock.decrementFluidLevel(state, world, pos);
        }
        return ActionResult.SUCCESS;
    };

    CauldronBehavior CLEAN_DYEABLE_ITEM = (state, world, pos, player, hand, stack) -> {
       if (state.get(KitchenSinkBlock.LEVEL_4) == 0) {
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
            KitchenSinkBlock.decrementFluidLevel(state, world, pos);
        }
        return ActionResult.SUCCESS;
};
    CauldronBehavior CLEAN_BANNER = (state, world, pos, player, hand, stack) -> {
        BannerPatternsComponent bannerPatternsComponent = stack.getOrDefault(DataComponentTypes.BANNER_PATTERNS, BannerPatternsComponent.DEFAULT);
        if (bannerPatternsComponent.layers().isEmpty() || state.get(KitchenSinkBlock.LEVEL_4) == 0) {
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
            KitchenSinkBlock.decrementFluidLevel(state, world, pos);
        }
        return ActionResult.SUCCESS;
    };

    static ActionResult fillCauldron(World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack, BlockState state, SoundEvent soundEvent) {
        if (!world.isClient) {
            Item item = stack.getItem();
            player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, new ItemStack(Items.BUCKET)));
            player.incrementStat(Statistics.SINK_FILLED);
            player.incrementStat(Stats.USED.getOrCreateStat(item));
            world.setBlockState(pos, state);
            world.playSound(null, pos, soundEvent, SoundCategory.BLOCKS, 1.0f, 1.0f);
            world.emitGameEvent(null, GameEvent.FLUID_PLACE, pos);
        }
        return ActionResult.SUCCESS;
    }

    static ActionResult emptyCauldron(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack, ItemStack output, Predicate<BlockState> predicate, SoundEvent soundEvent) {
        if (!predicate.test(state)) {
            return ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION;
        }
        if (!world.isClient) {
            Item item = stack.getItem();
            player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, output));
            player.incrementStat(Statistics.USE_SINK);
            player.incrementStat(Stats.USED.getOrCreateStat(item));
            world.setBlockState(pos, state.with(KitchenSinkBlock.LEVEL_4, 0));
            world.playSound(null, pos, soundEvent, SoundCategory.BLOCKS, 1.0f, 1.0f);
            world.emitGameEvent(null, GameEvent.FLUID_PICKUP, pos);
        }
        return ActionResult.SUCCESS;
    }

    static void registerBucketBehavior(Map<Item, CauldronBehavior> behavior) {
        behavior.put(Items.WATER_BUCKET, FILL_SINK_WITH_WATER);
    }
    static void registerBehavior() {
        WATER_SINK_BEHAVIOR.map().put(Items.POTION, (state, world, pos, player, hand, stack) -> {
            PotionContentsComponent potionContentsComponent = stack.get(DataComponentTypes.POTION_CONTENTS);
            if (potionContentsComponent != null && !potionContentsComponent.matches(Potions.WATER)) {
                return ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION;
            }
            if (!world.isClient) {
                Item item = stack.getItem();
                player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, new ItemStack(Items.GLASS_BOTTLE)));
                player.incrementStat(Statistics.USE_SINK);
                player.incrementStat(Stats.USED.getOrCreateStat(item));
                world.setBlockState(pos, state.with(KitchenSinkBlock.LEVEL_4, state.get(KitchenSinkBlock.LEVEL_4) + 1));
                world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0f, 1.0f);
                world.emitGameEvent(null, GameEvent.FLUID_PLACE, pos);
            }
            return ActionResult.SUCCESS;
        });



        SinkBehavior.registerBucketBehavior(WATER_SINK_BEHAVIOR.map());
        WATER_SINK_BEHAVIOR.map().put(Items.BUCKET, (state2, world, pos, player, hand, stack) -> SinkBehavior.emptyCauldron(state2, world, pos, player, hand, stack, new ItemStack(Items.WATER_BUCKET), state -> state.get(KitchenSinkBlock.LEVEL_4) == 3, SoundEvents.ITEM_BUCKET_FILL));
        WATER_SINK_BEHAVIOR.map().put(Items.GLASS_BOTTLE, (state, world, pos, player, hand, stack) -> {
            if (!world.isClient) {
                if (state.get(KitchenSinkBlock.LEVEL_4) == 0) {
                    return ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION;
                }
                Item item = stack.getItem();
                player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, PotionContentsComponent.createStack(Items.POTION, Potions.WATER)));
                player.incrementStat(Statistics.USE_SINK);
                player.incrementStat(Stats.USED.getOrCreateStat(item));
                KitchenSinkBlock.decrementFluidLevel(state, world, pos);
                world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, 1.0f, 1.0f);
                world.emitGameEvent(null, GameEvent.FLUID_PICKUP, pos);
            }
            return ActionResult.SUCCESS;
        });
        WATER_SINK_BEHAVIOR.map().put(Items.POTION, (state, world, pos, player, hand, stack) -> {
            PotionContentsComponent potionContentsComponent = stack.get(DataComponentTypes.POTION_CONTENTS);
            if (state.get(KitchenSinkBlock.LEVEL_4) == 3 || potionContentsComponent != null && !potionContentsComponent.matches(Potions.WATER)) {
                return ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION;
            }
            if (!world.isClient) {
                player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, new ItemStack(Items.GLASS_BOTTLE)));
                player.incrementStat(Statistics.USE_SINK);
                player.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));
                world.setBlockState(pos, state.cycle(KitchenSinkBlock.LEVEL_4));
                world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0f, 1.0f);
                world.emitGameEvent(null, GameEvent.FLUID_PLACE, pos);
            }
            return ActionResult.SUCCESS;
        });
        WATER_SINK_BEHAVIOR.map().put(Items.LEATHER_BOOTS, CLEAN_DYEABLE_ITEM);
        WATER_SINK_BEHAVIOR.map().put(Items.LEATHER_LEGGINGS, CLEAN_DYEABLE_ITEM);
        WATER_SINK_BEHAVIOR.map().put(Items.LEATHER_CHESTPLATE, CLEAN_DYEABLE_ITEM);
        WATER_SINK_BEHAVIOR.map().put(Items.LEATHER_HELMET, CLEAN_DYEABLE_ITEM);
        WATER_SINK_BEHAVIOR.map().put(Items.LEATHER_HORSE_ARMOR, CLEAN_DYEABLE_ITEM);
        WATER_SINK_BEHAVIOR.map().put(Items.WHITE_BANNER, CLEAN_BANNER);
        WATER_SINK_BEHAVIOR.map().put(Items.GRAY_BANNER, CLEAN_BANNER);
        WATER_SINK_BEHAVIOR.map().put(Items.BLACK_BANNER, CLEAN_BANNER);
        WATER_SINK_BEHAVIOR.map().put(Items.BLUE_BANNER, CLEAN_BANNER);
        WATER_SINK_BEHAVIOR.map().put(Items.BROWN_BANNER, CLEAN_BANNER);
        WATER_SINK_BEHAVIOR.map().put(Items.CYAN_BANNER, CLEAN_BANNER);
        WATER_SINK_BEHAVIOR.map().put(Items.GREEN_BANNER, CLEAN_BANNER);
        WATER_SINK_BEHAVIOR.map().put(Items.LIGHT_BLUE_BANNER, CLEAN_BANNER);
        WATER_SINK_BEHAVIOR.map().put(Items.LIGHT_GRAY_BANNER, CLEAN_BANNER);
        WATER_SINK_BEHAVIOR.map().put(Items.LIME_BANNER, CLEAN_BANNER);
        WATER_SINK_BEHAVIOR.map().put(Items.MAGENTA_BANNER, CLEAN_BANNER);
        WATER_SINK_BEHAVIOR.map().put(Items.ORANGE_BANNER, CLEAN_BANNER);
        WATER_SINK_BEHAVIOR.map().put(Items.PINK_BANNER, CLEAN_BANNER);
        WATER_SINK_BEHAVIOR.map().put(Items.PURPLE_BANNER, CLEAN_BANNER);
        WATER_SINK_BEHAVIOR.map().put(Items.RED_BANNER, CLEAN_BANNER);
        WATER_SINK_BEHAVIOR.map().put(Items.YELLOW_BANNER, CLEAN_BANNER);
        WATER_SINK_BEHAVIOR.map().put(Items.WHITE_SHULKER_BOX, CLEAN_SHULKER_BOX);
        WATER_SINK_BEHAVIOR.map().put(Items.GRAY_SHULKER_BOX, CLEAN_SHULKER_BOX);
        WATER_SINK_BEHAVIOR.map().put(Items.BLACK_SHULKER_BOX, CLEAN_SHULKER_BOX);
        WATER_SINK_BEHAVIOR.map().put(Items.BLUE_SHULKER_BOX, CLEAN_SHULKER_BOX);
        WATER_SINK_BEHAVIOR.map().put(Items.BROWN_SHULKER_BOX, CLEAN_SHULKER_BOX);
        WATER_SINK_BEHAVIOR.map().put(Items.CYAN_SHULKER_BOX, CLEAN_SHULKER_BOX);
        WATER_SINK_BEHAVIOR.map().put(Items.GREEN_SHULKER_BOX, CLEAN_SHULKER_BOX);
        WATER_SINK_BEHAVIOR.map().put(Items.LIGHT_BLUE_SHULKER_BOX, CLEAN_SHULKER_BOX);
        WATER_SINK_BEHAVIOR.map().put(Items.LIGHT_GRAY_SHULKER_BOX, CLEAN_SHULKER_BOX);
        WATER_SINK_BEHAVIOR.map().put(Items.LIME_SHULKER_BOX, CLEAN_SHULKER_BOX);
        WATER_SINK_BEHAVIOR.map().put(Items.MAGENTA_SHULKER_BOX, CLEAN_SHULKER_BOX);
        WATER_SINK_BEHAVIOR.map().put(Items.ORANGE_SHULKER_BOX, CLEAN_SHULKER_BOX);
        WATER_SINK_BEHAVIOR.map().put(Items.PINK_SHULKER_BOX, CLEAN_SHULKER_BOX);
        WATER_SINK_BEHAVIOR.map().put(Items.PURPLE_SHULKER_BOX, CLEAN_SHULKER_BOX);
        WATER_SINK_BEHAVIOR.map().put(Items.RED_SHULKER_BOX, CLEAN_SHULKER_BOX);
        WATER_SINK_BEHAVIOR.map().put(Items.YELLOW_SHULKER_BOX, CLEAN_SHULKER_BOX);
        }
    }
