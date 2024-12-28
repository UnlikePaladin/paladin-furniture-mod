package com.unlikepaladin.pfm.compat.cookingforblockheads.neoforge;

import com.google.common.collect.Lists;
import com.unlikepaladin.pfm.blocks.blockentities.GenericStorageBlockEntity3x3;
import net.blay09.mods.balm.api.container.BalmContainerProvider;
import net.blay09.mods.balm.api.provider.BalmProvider;
import net.blay09.mods.balm.api.provider.BalmProviderHolder;
import net.blay09.mods.cookingforblockheads.api.KitchenItemProvider;
import net.blay09.mods.cookingforblockheads.kitchen.ContainerKitchenItemProvider;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class GenericStorageBlockEntityBalm3x3 extends GenericStorageBlockEntity3x3 implements BalmContainerProvider, BalmProviderHolder {
    private final KitchenItemProvider itemProvider;

    public GenericStorageBlockEntityBalm3x3(BlockPos pos, BlockState state) {
        super(pos, state);
        this.itemProvider = new ContainerKitchenItemProvider(this);
    }

    @Override
    public Inventory getContainer() {
        return this;
    }

    public List<BalmProvider<?>> getProviders() {
        return List.of(new BalmProvider<>(KitchenItemProvider.class, this.itemProvider));
    }

}