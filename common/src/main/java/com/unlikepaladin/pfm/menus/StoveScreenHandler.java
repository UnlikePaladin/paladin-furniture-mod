package com.unlikepaladin.pfm.menus;

import com.unlikepaladin.pfm.blocks.blockentities.StovePacket;
import com.unlikepaladin.pfm.registry.ScreenHandlerIDs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.screen.AbstractFurnaceScreenHandler;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.util.math.BlockPos;

public class StoveScreenHandler extends AbstractFurnaceScreenHandler {
    private final Inventory inventory;
    public StoveScreenHandler(int syncId, PlayerInventory playerInventory, StoveData data) {
        super(ScreenHandlerIDs.STOVE_SCREEN_HANDLER, RecipeType.SMOKING, RecipeBookCategory.SMOKER, syncId, playerInventory);
        this.inventory = (Inventory) playerInventory.player.getWorld().getBlockEntity(data.pos());
        inventory.onOpen(playerInventory.player);
    }

    public StoveScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate) {
        super(ScreenHandlerIDs.STOVE_SCREEN_HANDLER, RecipeType.SMOKING, RecipeBookCategory.SMOKER, syncId, playerInventory, inventory, propertyDelegate);
        this.inventory = inventory;
        inventory.onOpen(playerInventory.player);
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        this.inventory.onClose(player);
    }

    public static final PacketCodec<RegistryByteBuf, StoveData> PACKET_CODEC = PacketCodec.of(StoveData::write, StoveData::new);
    public record StoveData(BlockPos pos) implements StovePacket {
        public StoveData(RegistryByteBuf buf) {
            this(buf.readBlockPos());
        }
        public void write(RegistryByteBuf buf) {
            buf.writeBlockPos(pos);
        }
    }
}