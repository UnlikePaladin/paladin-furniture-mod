package com.unlikepaladin.pfm.compat.cookingforblockheads.neoforge;

import com.unlikepaladin.pfm.blocks.StoveBlock;
import com.unlikepaladin.pfm.blocks.blockentities.StovePacket;
import com.unlikepaladin.pfm.blocks.neoforge.StoveBlockImpl;
import com.unlikepaladin.pfm.compat.cookingforblockheads.neoforge.menu.StoveScreenHandlerBalm;
import com.unlikepaladin.pfm.menus.StoveScreenHandler;
import com.unlikepaladin.pfm.registry.BlockEntities;
import com.unlikepaladin.pfm.registry.PaladinFurnitureModBlocksItems;
import com.unlikepaladin.pfm.registry.TriFunc;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.container.CombinedContainer;
import net.blay09.mods.balm.api.container.ContainerUtils;
import net.blay09.mods.balm.neoforge.provider.NeoForgeBalmProviders;
import net.blay09.mods.cookingforblockheads.api.CacheHint;
import net.blay09.mods.cookingforblockheads.api.IngredientToken;
import net.blay09.mods.cookingforblockheads.api.KitchenItemProcessor;
import net.blay09.mods.cookingforblockheads.api.KitchenItemProvider;
import net.blay09.mods.cookingforblockheads.block.entity.FridgeBlockEntity;
import net.blay09.mods.cookingforblockheads.item.ModItems;
import net.blay09.mods.cookingforblockheads.kitchen.ContainerKitchenItemProvider;
import net.blay09.mods.cookingforblockheads.tag.ModItemTags;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Ingredient;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.Function;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;

public class PFMCookingForBlockHeadsCompat {
    public static final PFMCookingTableBlock COOKING_TABLE_BLOCK = new PFMCookingTableBlock(AbstractBlock.Settings.copy(Blocks.GRAY_CONCRETE));//PaladinFurnitureModBlocksItems.GRAY_STOVE));
    public static TriFunc<Integer, PlayerInventory, StoveScreenHandler.StoveData, StoveScreenHandlerBalm> getStoveScreenHandler() {
        return (integer, playerInventory, data) -> {
            BlockPos pos = data.pos();
            BlockEntity blockEntity = playerInventory.player.getWorld().getBlockEntity(pos);
            return new StoveScreenHandlerBalm(integer, playerInventory, (StoveBlockEntityBalm)blockEntity);
        };
    }

    public static <D extends StovePacket> PacketCodec<RegistryByteBuf, D> getStovePacket() {
        return (PacketCodec<RegistryByteBuf, D>) StoveScreenHandler.PACKET_CODEC;
    }

    public static void openMenuScreen(World world, BlockPos pos, PlayerEntity player) {
        StoveBlockEntityBalm stove = (StoveBlockEntityBalm)world.getBlockEntity(pos);
        if (!world.isClient) {
            Balm.getNetworking().openGui(player, stove);
        }
    }

    public static BlockEntity getStoveBlockEntity(BlockPos pos, BlockState state) {
        return new StoveBlockEntityBalm(pos, state);
    }

    public static <T extends BlockEntity> BlockEntityTicker<T> getStoveTicker(World world, BlockEntityType<T> type) {
        if (world.isClient) {
            return StoveBlockImpl.checkType(type, BlockEntities.STOVE_BLOCK_ENTITY, StoveBlockEntityBalm::clientTick);
        } else {
            return StoveBlockImpl.checkType(type, BlockEntities.STOVE_BLOCK_ENTITY, StoveBlockEntityBalm::serverTick);
        }
    }

    public static ItemActionResult onUseStove(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack heldItem = player.getStackInHand(hand);
        if (heldItem.getItem() == ModItems.heatingUnit) {
            return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        } else if (hit.getSide() == Direction.UP && heldItem.isIn(ModItemTags.UTENSILS)) {
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
                StoveBlockEntityBalm tileOven = (StoveBlockEntityBalm)level.getBlockEntity(pos);
                if (tileOven != null && tileOven.getToolItem(index).isEmpty()) {
                    ItemStack toolItem = heldItem.split(1);
                    tileOven.setToolItem(index, toolItem);
                }
            }
            return ItemActionResult.SUCCESS;
        } else {
            StoveBlockEntityBalm oven = (StoveBlockEntityBalm)level.getBlockEntity(pos);
            if (hit.getSide() == state.get(Properties.HORIZONTAL_FACING) && oven != null) {
                if (player.isSneaking()) {
                    return ItemActionResult.SUCCESS;
                }

                if (!heldItem.isEmpty() && oven.getSmeltingResult(heldItem) != ItemStack.EMPTY) {
                    heldItem = ContainerUtils.insertItemStacked(oven.getInputContainer(), heldItem, false);
                    player.setStackInHand(hand, heldItem);

                    return ItemActionResult.SUCCESS;
                } else if (!heldItem.isEmpty() && StoveBlockEntityBalm.isItemFuel(heldItem)) {
                    heldItem = ContainerUtils.insertItemStacked(oven.getFuelContainer(), heldItem, false);
                    player.setStackInHand(hand, heldItem);
                    return ItemActionResult.SUCCESS;
                }
            }
            if (!level.isClient) {
                Balm.getNetworking().openGui(player, oven);
            }
            return ItemActionResult.SUCCESS;
        }
    }


    public static void registerCapabilities(RegisterCapabilitiesEvent event) {

        event.registerBlockEntity(((NeoForgeBalmProviders)Balm.getProviders()).getBlockCapability(KitchenItemProvider.class), BlockEntities.FREEZER_BLOCK_ENTITY, (entity, side) -> {
            return new ContainerKitchenItemProvider(entity){
                private final ItemStack snowStack;
                private final ItemStack iceStack;
                {
                    this.snowStack = new ItemStack(Items.SNOWBALL);
                    this.iceStack = new ItemStack(Blocks.ICE);
                }

                @Override
                public IngredientToken findIngredient(Ingredient ingredient, Collection<IngredientToken> ingredientTokens, CacheHint cacheHint) {
                    IngredientToken result = applyIceUnit(ingredient::test);
                    if (result != null)
                        return result;

                    return super.findIngredient(ingredient, ingredientTokens, cacheHint);
                }

                @Override
                public IngredientToken findIngredient(ItemStack itemStack, Collection<IngredientToken> ingredientTokens, CacheHint cacheHint) {
                    IngredientToken result = applyIceUnit(stack -> ItemStack.areItemsEqual(stack, itemStack));
                    if (result != null)
                        return result;

                    return super.findIngredient(itemStack, ingredientTokens, cacheHint);
                }

                private @Nullable IngredientToken applyIceUnit(Function<ItemStack, Boolean> predicate) {
                    if (predicate.apply(this.snowStack))
                        return new FridgeBlockEntity.IceUnitIngredientToken(ContainerUtils.copyStackWithSize(this.snowStack, 64));
                    else
                        return predicate.apply(this.iceStack) ? new FridgeBlockEntity.IceUnitIngredientToken(ContainerUtils.copyStackWithSize(this.iceStack, 64)) : null;
                }
            };
        });

        event.registerBlockEntity(((NeoForgeBalmProviders)Balm.getProviders()).getBlockCapability(KitchenItemProvider.class), BlockEntities.STOVE_BLOCK_ENTITY, (entity, side) -> {
            StoveBlockEntityBalm ovenBlockEntityBalm = (StoveBlockEntityBalm) entity;
            return new ContainerKitchenItemProvider(new CombinedContainer(ovenBlockEntityBalm.toolsContainer, ovenBlockEntityBalm.outputContainer));
        });

        event.registerBlockEntity(((NeoForgeBalmProviders)Balm.getProviders()).getBlockCapability(KitchenItemProcessor.class), BlockEntities.STOVE_BLOCK_ENTITY, (entity, side) -> {
            return (StoveBlockEntityBalm) entity;
        });
    }
}
