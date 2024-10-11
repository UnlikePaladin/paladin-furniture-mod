package com.unlikepaladin.pfm.items;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.client.item.TooltipType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FurnitureGuideBook extends Item {
    public FurnitureGuideBook(Settings settings) {
        super(settings);
    }
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        return openBook(world, user, hand);
    }
    @ExpectPlatform
    public static TypedActionResult<ItemStack> openBook(World world,PlayerEntity user, Hand hand) {
        throw new AssertionError();
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("pfm.patchouli.guide_book.subtitle"));
        super.appendTooltip(stack, context, tooltip, type);
    }
}
