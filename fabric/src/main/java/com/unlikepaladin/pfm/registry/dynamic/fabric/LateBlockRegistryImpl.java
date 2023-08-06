package com.unlikepaladin.pfm.registry.dynamic.fabric;

import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.data.materials.WoodVariant;
import com.unlikepaladin.pfm.data.materials.WoodVariantRegistry;
import com.unlikepaladin.pfm.registry.PaladinFurnitureModBlocksItems;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.resource.featuretoggle.FeatureFlag;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registry;
import net.minecraft.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class LateBlockRegistryImpl {

    public static <T extends Block> T registerLateBlock(String blockName, Supplier<T> blockSupplier, boolean registerItem, Pair<String, ItemGroup> group) {
        T block = Registry.register(Registries.BLOCK, new Identifier(PaladinFurnitureMod.MOD_ID, blockName), blockSupplier.get());
        if (registerItem) {
            PaladinFurnitureModBlocksItems.BLOCKS.add(block);
            registerLateBlockItem(blockName, block, group);
        }
        return block;
    }
    public static void registerLateBlockItem(String itemName, Block block, Pair<String, ItemGroup> group) {
        registerLateItem(itemName, () -> new BlockItem(block, new FabricItemSettings()), group);
        if (block.getDefaultState().getMaterial() == Material.WOOD || block.getDefaultState().getMaterial() == Material.WOOL) {
            FlammableBlockRegistry.getDefaultInstance().add(block, 20, 5);
            FuelRegistry.INSTANCE.add(block, 300);
        }
    }
    public static void registerLateItem(String itemName, Supplier<Item> itemSup, Pair<String, ItemGroup> group) {
        Item item = itemSup.get();
        Registry.register(Registries.ITEM, new Identifier(PaladinFurnitureMod.MOD_ID, itemName), item);
        if (!PaladinFurnitureModBlocksItems.ITEM_GROUP_LIST_MAP.containsKey(group)) {
            PaladinFurnitureModBlocksItems.ITEM_GROUP_LIST_MAP.put(group, new ArrayList<>());
        }
        PaladinFurnitureModBlocksItems.ITEM_GROUP_LIST_MAP.get(group).add(item);
        if (item == PaladinFurnitureModBlocksItems.BASIC_LAMP_ITEM) {
            ItemGroupEvents.modifyEntriesEvent(group.getRight()).register(entries -> {
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
                        break;
                    }
                    for (DyeColor color : DyeColor.values()) {
                        ItemStack stack = new ItemStack(item);
                        NbtCompound beTag = new NbtCompound();
                        beTag.putString("color", color.asString());
                        beTag.putString("variant", variant.getIdentifier().toString());
                        stack.setSubNbt("BlockEntityTag", beTag);
                        stacks.add(stack);
                    }
                }
                entries.addAll(stacks);
            } );
        } else {
            ItemGroupEvents.modifyEntriesEvent(group.getRight()).register(entries -> entries.add(item));
        }
    }

    public static <T extends Block> T registerLateBlockClassic(String blockName, T block, boolean registerItem, Pair<String, ItemGroup> group) {
        Registry.register(Registries.BLOCK, new Identifier(PaladinFurnitureMod.MOD_ID, blockName), block);
        if (registerItem) {
            PaladinFurnitureModBlocksItems.BLOCKS.add(block);
            registerLateBlockItem(blockName, block, group);
        }
        return block;
    }
}
