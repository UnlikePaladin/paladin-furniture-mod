package com.unlikepaladin.pfm.client;

import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.blocks.*;
import com.unlikepaladin.pfm.blocks.blockentities.LampBlockEntity;
import com.unlikepaladin.pfm.data.FurnitureBlock;
import com.unlikepaladin.pfm.data.materials.VariantBase;
import com.unlikepaladin.pfm.data.materials.WoodVariant;
import com.unlikepaladin.pfm.data.materials.WoodVariantRegistry;
import com.unlikepaladin.pfm.registry.PaladinFurnitureModBlocksItems;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.item.Item;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class ColorRegistry {
    public static void registerBlockColors(){
        registerBlockColor(PaladinFurnitureModBlocksItems.BASIC_TOILET, addToiletColor());
        registerBlockColor(PaladinFurnitureModBlocksItems.BASIC_BATHTUB, addWaterColor());
        registerBlockColor(PaladinFurnitureModBlocksItems.BASIC_SINK, addWaterColor());
        registerBlockColor(PaladinFurnitureModBlocksItems.BASIC_LAMP, (state, world, pos, tintIndex) -> {
            BlockEntity entity = world.getBlockEntity(pos);
            if (entity != null && tintIndex == 1) {
                if (entity instanceof LampBlockEntity) {
                    DyeColor color = ((LampBlockEntity)entity).getPFMColor();
                    return color.getFireworkColor();
                }
            } else if (entity != null && tintIndex == 0) {
                if (getBlockColor(((LampBlockEntity)entity).getVariant().getLogBlock()) != null) {
                    return getBlockColor(((LampBlockEntity)entity).getVariant().getLogBlock()).getColor(state, world, pos, tintIndex);
                }
            }
            return 0xFFFFFF;
        });
        PaladinFurnitureMod.pfmModCompatibilities.forEach(pfmModCompatibility -> {
            if (pfmModCompatibility.getClientModCompatiblity().isPresent()){
                pfmModCompatibility.getClientModCompatiblity().get().registerBlockColors();
            }
        });
        PaladinFurnitureModBlocksItems.furnitureEntryMap.forEach((key, value) -> {
            value.getVariantToBlockMap().forEach((variantBase, block) -> {
                BlockColorProvider blockColorProvider = getBlockColor(variantBase.getBaseBlock());
                if (key.isAssignableFrom(KitchenSinkBlock.class)) {
                    registerBlockColor(block, ((state, world, pos, tintIndex) -> {
                        if (tintIndex == 1) {
                            return addWaterColor().getColor(state, world, pos, tintIndex);
                        } else if (blockColorProvider == null) {
                            return 0xFFFFFFF;
                        }
                        return blockColorProvider.getColor(state, world, pos, tintIndex);
                    }));
                } else {
                    if (blockColorProvider != null) {
                        registerBlockColor(block, blockColorProvider);
                    }
                }
            });
            value.getVariantToBlockMapNonBase().forEach((variantBase, block) -> {
                BlockColorProvider blockColorProvider = getBlockColor(variantBase.getBaseBlock());
                if (key.isAssignableFrom(KitchenSinkBlock.class)) {
                    registerBlockColor(block, ((state, world, pos, tintIndex) -> {
                        if (tintIndex == 1) {
                            return addWaterColor().getColor(state, world, pos, tintIndex);
                        } else if (blockColorProvider == null) {
                            return 0xFFFFFFF;
                        }
                        return blockColorProvider.getColor(state, world, pos, tintIndex);
                    }));
                } else {
                    if (blockColorProvider != null) {
                        registerBlockColor(block, blockColorProvider);
                    }
                }
            });
        });
    }

    public static void registerBlockRenderLayers() {
        registerBlockToRenderLayer(PaladinFurnitureModBlocksItems.IRON_CHAIN, RenderLayer.getCutout());
        registerBlockToRenderLayer(PaladinFurnitureModBlocksItems.MESH_TRASHCAN, RenderLayer.getCutout());
        registerBlockToRenderLayer(PaladinFurnitureModBlocksItems.WHITE_MIRROR, RenderLayer.getCutout());
        registerBlockToRenderLayer(PaladinFurnitureModBlocksItems.GRAY_MIRROR, RenderLayer.getCutout());
        registerBlockToRenderLayer(PaladinFurnitureModBlocksItems.BASIC_LAMP, RenderLayer.getCutout());
        registerBlockToRenderLayer(PaladinFurnitureModBlocksItems.KITCHEN_STOVETOP, RenderLayer.getCutout());
    }

    public static void registerItemColors() {
        registerItemColor(PaladinFurnitureModBlocksItems.BASIC_BATHTUB.asItem(), (stack, index) -> index == 1 ?  0x3c44a9 : 0xFFFFFF);
        registerItemColor(PaladinFurnitureModBlocksItems.BASIC_SINK.asItem(), (stack, index) -> index == 1 ?  0x3c44a9 : 0xFFFFFF);
        registerItemColor(PaladinFurnitureModBlocksItems.BASIC_LAMP_ITEM, (stack, tintIndex) -> {
            if (stack.hasNbt() && tintIndex == 1) {
                return DyeColor.byName(stack.getSubNbt("BlockEntityTag").getString("color"), DyeColor.WHITE).getMapColor().color;
            } else if (stack.hasNbt() && tintIndex == 0) {
                WoodVariant variantBase = WoodVariantRegistry.getVariant(Identifier.tryParse(stack.getSubNbt("BlockEntityTag").getString("variant")));
                if (getItemColor(variantBase.getLogBlock().asItem()) != null) {
                    return getItemColor(variantBase.getLogBlock().asItem()).getColor(stack, tintIndex);
                }
            }
            return 0xFFFFFF;
        });

        PaladinFurnitureModBlocksItems.furnitureEntryMap.forEach((key, value) -> {
            value.getVariantToBlockMap().forEach((variantBase, block) -> {
                ItemColorProvider itemColorProvider = getItemColor(variantBase.getBaseBlock().asItem());
                if (itemColorProvider != null) {
                    registerItemColor(block.asItem(), itemColorProvider);
                }
            });
            value.getVariantToBlockMapNonBase().forEach((variantBase, block) -> {
                ItemColorProvider itemColorProvider = getItemColor(variantBase.getBaseBlock().asItem());
                if (itemColorProvider != null) {
                    registerItemColor(block.asItem(), itemColorProvider);
                }
            });
        });
    }

    @ExpectPlatform
    public static void registerItemColor(Item item, ItemColorProvider colorProvider) {
        throw new RuntimeException();
    }
    @ExpectPlatform
    public static void registerBlockColor(Block block, BlockColorProvider blockColorProvider){
        throw new RuntimeException();
    }
    @ExpectPlatform
    public static BlockColorProvider getBlockColor(Block block){
        throw new RuntimeException();
    }

    @ExpectPlatform
    public static ItemColorProvider getItemColor(Item item){
        throw new RuntimeException();
    }

    @ExpectPlatform
    public static void registerBlockToRenderLayer(Block block, RenderLayer renderLayer){
        throw new RuntimeException();
    }

    private static BlockColorProvider addToiletColor() {
        return (state, view, pos, index) -> state.get(BasicToiletBlock.TOILET_STATE) !=  ToiletState.DIRTY ? BiomeColors.getWaterColor(view, pos) : 0x534230;
    }

    private static BlockColorProvider addWaterColor() {
        return (state, view, pos, index) -> index == 1 ? BiomeColors.getWaterColor(view, pos) : 0xFFFFFF;
    }
}
