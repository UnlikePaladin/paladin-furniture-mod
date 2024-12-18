package com.unlikepaladin.pfm.blocks.blockentities.neoforge;

import com.unlikepaladin.pfm.blocks.blockentities.PFMToasterBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    public @NotNull NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return this.saveInitialChunkData(new NbtCompound(), registryLookup);
    }

    @Override
    public void handleUpdateTag(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        this.readNbt(tag, registryLookup);
    }

    @Override
    public void onDataPacket(ClientConnection net, BlockEntityUpdateS2CPacket pkt, RegistryWrapper.WrapperLookup registryLookup) {
        super.onDataPacket(net, pkt, registryLookup);
        this.getItems().clear();
        Inventories.readNbt(pkt.getNbt(), this.items, registryLookup);
    }
}
