package com.unlikepaladin.pfm.items;

import com.unlikepaladin.pfm.data.materials.WoodVariant;
import com.unlikepaladin.pfm.data.materials.WoodVariantRegistry;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.block.Block;
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
    public String getTranslationKey(ItemStack stack) {
        DyeColor color = DyeColor.WHITE;
        WoodVariant variant = WoodVariantRegistry.OAK;
        if (stack.hasNbt()) {
            if (stack.getSubNbt("BlockEntityTag").contains("color")) {
                color = DyeColor.byName(stack.getSubNbt("BlockEntityTag").getString("color"), DyeColor.WHITE);
            }
            if (stack.getSubNbt("BlockEntityTag").contains("variant")) {
                variant = WoodVariantRegistry.getVariant(Identifier.tryParse(stack.getSubNbt("BlockEntityTag").getString("variant")));
            }
        }
        return String.format("block.pfm.basic_%s_%s_lamp", color.asString(), variant.asString());
    }

    @Override
    public ItemStack getDefaultStack() {
        ItemStack stack = new ItemStack(this);
        NbtCompound tag = new NbtCompound();
        tag.putString("color", DyeColor.WHITE.asString());
        tag.putString("variant", WoodVariantRegistry.OAK.asString());
        stack.setSubNbt("BlockEntityTag", tag);
        return stack;
    }

    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
    }

    @ExpectPlatform
    public static BlockItem getItemFactory(Block block, Settings settings) {
        throw new UnsupportedOperationException();
    }
}
