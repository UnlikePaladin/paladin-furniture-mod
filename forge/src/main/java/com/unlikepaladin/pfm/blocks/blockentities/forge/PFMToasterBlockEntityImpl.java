package com.unlikepaladin.pfm.blocks.blockentities.forge;

import com.unlikepaladin.pfm.blocks.blockentities.PFMToasterBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class PFMToasterBlockEntityImpl extends PFMToasterBlockEntity{
    public PFMToasterBlockEntityImpl(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    public static boolean isMetal(ItemStack stack) {
        return stack.getItem().getTranslationKey().contains("iron");
    }

    public static void sandwichableToast(PFMToasterBlockEntity pfmToasterBlockEntity) {
    }

    public static BlockEntityType.BlockEntityFactory<? extends PFMToasterBlockEntity> getFactory() {
        return PFMToasterBlockEntityImpl::new;
    }

    @Nullable
    @Override
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    protected NbtCompound saveInitialChunkData(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        Inventories.writeNbt(nbt, items, true, registryLookup);
        return nbt;
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return this.saveInitialChunkData(new NbtCompound(), registryLookup);
    }

    @Override
    public void handleUpdateTag(NbtCompound tag, RegistryWrapper.WrapperLookup holders) {
       this.readNbt(tag, holders);
    }

    @Override
    public void onDataPacket(ClientConnection connection, BlockEntityUpdateS2CPacket pkt, RegistryWrapper.WrapperLookup lookup) {
        super.onDataPacket(connection, pkt, lookup);
        this.getItems().clear();
        Inventories.readNbt(pkt.getNbt(), this.items, lookup);
    }
}
