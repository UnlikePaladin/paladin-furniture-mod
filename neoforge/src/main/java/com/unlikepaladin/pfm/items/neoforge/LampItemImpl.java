package com.unlikepaladin.pfm.items.neoforge;

import com.unlikepaladin.pfm.client.neoforge.PFMItemRenderer;
import com.unlikepaladin.pfm.items.LampItem;
import net.minecraft.block.Block;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.item.BlockItem;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

public class LampItemImpl extends LampItem {
    public LampItemImpl(Block block, Settings settings) {
        super(block, settings);
    }

    public static BlockItem getItemFactory(Block block, Settings settings) {
        return new LampItemImpl(block, settings);
    }
}
