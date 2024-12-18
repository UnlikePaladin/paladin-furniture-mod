package com.unlikepaladin.pfm.registry.dynamic.fabric;

import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.blocks.AbstractSittableBlock;
import com.unlikepaladin.pfm.data.materials.WoodVariant;
import com.unlikepaladin.pfm.data.materials.WoodVariantRegistry;
import com.unlikepaladin.pfm.items.PFMComponents;
import com.unlikepaladin.pfm.registry.PaladinFurnitureModBlocksItems;
import com.unlikepaladin.pfm.registry.dynamic.LateBlockRegistry;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.fabricmc.fabric.api.registry.FuelRegistryEvents;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.resource.featuretoggle.FeatureFlag;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registry;
import net.minecraft.util.Pair;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.Supplier;

public class LateBlockRegistryImpl {

    public static <T extends Block> T registerLateBlock(String blockName, Supplier<T> blockSupplier, boolean registerItem, Pair<String, ItemGroup> group) {
        T block = Registry.register(Registries.BLOCK, Identifier.of(PaladinFurnitureMod.MOD_ID, blockName), blockSupplier.get());
        if (registerItem) {
            PaladinFurnitureModBlocksItems.BLOCKS.add(block);
            registerLateBlockItem(blockName, block, group);
        }
        return block;
    }
    public static void registerLateBlockItem(String itemName, Block block, Pair<String, ItemGroup> group) {
        registerLateItem(itemName, () -> new BlockItem(block, new Item.Settings().registryKey(LateBlockRegistry.getItemRegistryKey(itemName)).useBlockPrefixedTranslationKey()), group);
        if (AbstractSittableBlock.isWoodBased(block.getDefaultState())) {
            FlammableBlockRegistry.getDefaultInstance().add(block, 20, 5);
            FuelRegistryEvents.BUILD.register((builder, context) -> {
                builder.add(block, 300);
            });

        }
    }
    public static void registerLateItem(String itemName, Supplier<Item> itemSup, Pair<String, ItemGroup> group) {
        Item item = itemSup.get();
        Registry.register(Registries.ITEM, Identifier.of(PaladinFurnitureMod.MOD_ID, itemName), item);
        if (!PaladinFurnitureModBlocksItems.ITEM_GROUP_LIST_MAP.containsKey(group)) {
            PaladinFurnitureModBlocksItems.ITEM_GROUP_LIST_MAP.put(group, new LinkedHashSet<>());
        }
        PaladinFurnitureModBlocksItems.ITEM_GROUP_LIST_MAP.get(group).add(item);
        if (item == PaladinFurnitureModBlocksItems.BASIC_LAMP_ITEM) {
            ItemGroupEvents.modifyEntriesEvent(Registries.ITEM_GROUP.getKey(group.getRight()).get()).register(entries -> {
                List<ItemStack> stacks = new ArrayList<>();
                for (WoodVariant variant : WoodVariantRegistry.getVariants()) {
                    boolean variantEnabled = true;
                    for (FeatureFlag flag : variant.getFeatureList()) {
                        if (!entries.getEnabledFeatures().contains(flag)) {
                            variantEnabled = false;
                            break;
                        }
                    }
                    if (!variantEnabled) {
                        continue;
                    }
                    for (DyeColor color : DyeColor.values()) {
                        ItemStack stack = new ItemStack(item);
                        stack.set(PFMComponents.VARIANT_COMPONENT, variant.getIdentifier());
                        stack.set(PFMComponents.COLOR_COMPONENT, color);
                        stacks.add(stack);
                    }
                }
                entries.addAll(stacks);
            } );
        } else {
            ItemGroupEvents.modifyEntriesEvent(Registries.ITEM_GROUP.getKey(group.getRight()).get()).register(entries -> entries.add(item));
        }
    }

    public static <T extends Block> T registerLateBlockClassic(String blockName, T block, boolean registerItem, Pair<String, ItemGroup> group) {
        Registry.register(Registries.BLOCK, Identifier.of(PaladinFurnitureMod.MOD_ID, blockName), block);
        if (registerItem) {
            PaladinFurnitureModBlocksItems.BLOCKS.add(block);
            registerLateBlockItem(blockName, block, group);
        }
        return block;
    }
}
