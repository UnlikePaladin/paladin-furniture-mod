package com.unlikepaladin.pfm.items;

import com.unlikepaladin.pfm.data.materials.WoodVariant;
import com.unlikepaladin.pfm.data.materials.WoodVariantRegistry;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.block.Block;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

import java.util.Map;

public class LampItem extends BlockItem {
    public LampItem(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public Text getName(ItemStack stack) {
        DyeColor color = DyeColor.WHITE;
        WoodVariant variant = WoodVariantRegistry.OAK;

        if (stack.contains(PFMComponents.COLOR_COMPONENT)) {
            color = stack.get(PFMComponents.COLOR_COMPONENT);
        }
        if (stack.contains(PFMComponents.VARIANT_COMPONENT)) {
            variant = WoodVariantRegistry.getVariant(stack.get(PFMComponents.VARIANT_COMPONENT));
        }
        return Text.translatable(String.format("block.pfm.basic_%s_%s_lamp", color.asString(), variant.asString()));
    }

    @Override
    public ItemStack getDefaultStack() {
        ItemStack stack = new ItemStack(this);
        stack.set(PFMComponents.COLOR_COMPONENT, DyeColor.WHITE);
        stack.set(PFMComponents.VARIANT_COMPONENT, WoodVariantRegistry.OAK.identifier);
        return stack;
    }

    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
    }

    @ExpectPlatform
    public static BlockItem getItemFactory(Block block, Settings settings) {
        throw new UnsupportedOperationException();
    }
}
