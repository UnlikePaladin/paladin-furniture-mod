package com.unlikepaladin.pfm.compat.cookingforblockheads.forge;

import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.blocks.*;
import com.unlikepaladin.pfm.blocks.blockentities.StoveBlockEntity;
import com.unlikepaladin.pfm.blocks.forge.StoveBlockImpl;
import com.unlikepaladin.pfm.compat.cookingforblockheads.forge.menu.StoveScreenHandlerBalm;
import com.unlikepaladin.pfm.data.PFMBlockSettings;
import com.unlikepaladin.pfm.data.ToolType;
import com.unlikepaladin.pfm.registry.BlockEntities;
import com.unlikepaladin.pfm.registry.PaladinFurnitureModBlocksItems;
import com.unlikepaladin.pfm.registry.TriFunc;
import net.blay09.mods.cookingforblockheads.KitchenMultiBlock;
import net.blay09.mods.cookingforblockheads.item.ModItems;
import net.blay09.mods.cookingforblockheads.registry.CookingRegistry;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.Arrays;
import java.util.function.Function;

import java.util.ArrayList;
import java.util.List;

public class PFMCookingForBlockHeadsCompat {

    public static void initBlockConnectors() {
        List<Block> connectorBlocks = new ArrayList<>(PaladinFurnitureModBlocksItems.furnitureEntryMap.get(KitchenDrawerBlock.class).getAllBlocks());
        connectorBlocks.addAll(PaladinFurnitureModBlocksItems.furnitureEntryMap.get(KitchenWallDrawerBlock.class).getAllBlocks());
        connectorBlocks.addAll(PaladinFurnitureModBlocksItems.furnitureEntryMap.get(KitchenCabinetBlock.class).getAllBlocks());
        connectorBlocks.addAll(PaladinFurnitureModBlocksItems.furnitureEntryMap.get(ClassicNightstandBlock.class).getAllBlocks());
        connectorBlocks.addAll(PaladinFurnitureModBlocksItems.furnitureEntryMap.get(KitchenWallDrawerSmallBlock.class).getAllBlocks());
        connectorBlocks.addAll(PaladinFurnitureModBlocksItems.furnitureEntryMap.get(KitchenCounterOvenBlock.class).getAllBlocks());
        connectorBlocks.addAll(PaladinFurnitureModBlocksItems.furnitureEntryMap.get(KitchenSinkBlock.class).getAllBlocks());
        connectorBlocks.addAll(PaladinFurnitureModBlocksItems.furnitureEntryMap.get(KitchenWallCounterBlock.class).getAllBlocks());
        connectorBlocks.addAll(PaladinFurnitureModBlocksItems.furnitureEntryMap.get(KitchenCounterBlock.class).getAllBlocks());
        connectorBlocks.addAll(Arrays.asList(PaladinFurnitureModBlocksItems.WHITE_FRIDGE, PaladinFurnitureModBlocksItems.XBOX_FRIDGE, PaladinFurnitureModBlocksItems.GRAY_FRIDGE, PaladinFurnitureModBlocksItems.IRON_FRIDGE));
        connectorBlocks.addAll(Arrays.asList(PaladinFurnitureModBlocksItems.WHITE_STOVE, PaladinFurnitureModBlocksItems.GRAY_STOVE, PaladinFurnitureModBlocksItems.IRON_STOVE));
        connectorBlocks.addAll(Arrays.asList(PaladinFurnitureModBlocksItems.GRAY_FREEZER, PaladinFurnitureModBlocksItems.IRON_FREEZER, PaladinFurnitureModBlocksItems.WHITE_FREEZER));
        connectorBlocks.forEach(KitchenMultiBlock::registerConnectorBlock);
    }

    public static final PFMCookingTableBlock COOKING_TABLE_BLOCK = new PFMCookingTableBlock(PFMBlockSettings.breaksWithTool(AbstractBlock.Settings.copy(PaladinFurnitureModBlocksItems.GRAY_STOVE), ToolType.PICKAXE));
    public static <T extends ScreenHandler> TriFunc<Integer, PlayerInventory, PacketByteBuf, T> getStoveScreenHandler() {
        return (integer, playerInventory, packetByteBuf) -> {
            BlockPos pos = packetByteBuf.readBlockPos();
            BlockEntity blockEntity = playerInventory.player.world.getBlockEntity(pos);
            return (T) new StoveScreenHandlerBalm(integer, playerInventory, (StoveBlockEntityBalm)blockEntity);
        };
    }

    public static void openMenuScreen(World world, BlockPos pos, PlayerEntity player) {
        StoveBlockEntityBalm stove = (StoveBlockEntityBalm)world.getBlockEntity(pos);
        if (!world.isClient) {
            NetworkHooks.openGui((ServerPlayerEntity)player, stove, pos);
        }
    }

    public static BlockEntity getStoveBlockEntity() {
        return new StoveBlockEntityBalm();
    }

    public static ActionResult onUseStove(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack heldItem = player.getStackInHand(hand);
        if (heldItem.getItem() == ModItems.heatingUnit) {
            return ActionResult.PASS;
        } else if (hit.getSide() == Direction.UP && CookingRegistry.isToolItem(heldItem)) {
            Direction stateFacing = state.get(StoveBlock.FACING);
            double hx =  (hit.getPos().x - hit.getBlockPos().getX());
            double hz = (hit.getPos().z - hit.getBlockPos().getZ());
            switch (stateFacing) {
                case NORTH:
                    hx = 1.0 - (hit.getPos().x - hit.getBlockPos().getX());
                    hz = 1.0 - (hit.getPos().z - hit.getBlockPos().getZ());
                    break;
                case WEST:
                    hz = 1.0 - (hit.getPos().x - hit.getBlockPos().getX());
                    hx = (hit.getPos().z - hit.getBlockPos().getZ());
                    break;
                case EAST:
                    hz = (hit.getPos().x - hit.getBlockPos().getX());
                    hx = 1.0 - (hit.getPos().z - hit.getBlockPos().getZ());
            }
            int index = -1;
            if (hx < 0.5f && hz < 0.5f) {
                index = 1;
            } else if (hx >= 0.5f && hz < 0.5f) {
                index = 0;
            } else if (hx < 0.5f && hz >= 0.5f) {
                index = 3;
            } else if (hx >= 0.5f && hz >= 0.5f) {
                index = 2;
            }
            if (index != -1) {
                StoveBlockEntityBalm stove = (StoveBlockEntityBalm)level.getBlockEntity(pos);
                if (stove != null && stove.getToolItem(index).isEmpty()) {
                    ItemStack toolItem = heldItem.split(1);
                    stove.setToolItem(index, toolItem);
                }
            }
            return ActionResult.SUCCESS;
        } else {
            StoveBlockEntityBalm stove = (StoveBlockEntityBalm)level.getBlockEntity(pos);
            if (hit.getSide() == state.get(Properties.HORIZONTAL_FACING) && stove != null) {
                if (player.isSneaking()) {
                    return ActionResult.SUCCESS;
                }

                if (!heldItem.isEmpty() && stove.getSmeltingResult(heldItem) != ItemStack.EMPTY) {
                    heldItem = ItemHandlerHelper.insertItemStacked(stove.getInputContainer(), heldItem, false);
                    player.setStackInHand(hand, heldItem);

                    return ActionResult.SUCCESS;
                } else if (!heldItem.isEmpty() && StoveBlockEntityBalm.isItemFuel(heldItem)) {
                    heldItem = ItemHandlerHelper.insertItemStacked(stove.getFuelContainer(), heldItem, false);
                    player.setStackInHand(hand, heldItem);
                    return ActionResult.SUCCESS;
                }
            }
            if (!level.isClient) {
                NetworkHooks.openGui((ServerPlayerEntity)player, stove, pos);
            }
            return ActionResult.SUCCESS;
        }
    }
}
