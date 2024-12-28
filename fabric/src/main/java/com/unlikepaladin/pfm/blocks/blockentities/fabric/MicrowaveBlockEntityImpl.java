package com.unlikepaladin.pfm.blocks.blockentities.fabric;

import com.unlikepaladin.pfm.blocks.blockentities.MicrowaveBlockEntity;
import com.unlikepaladin.pfm.menus.MicrowaveScreenHandler;
import com.unlikepaladin.pfm.networking.MicrowaveUpdatePayload;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

import org.jetbrains.annotations.Nullable;
import java.util.Collection;

public class MicrowaveBlockEntityImpl extends MicrowaveBlockEntity implements ExtendedScreenHandlerFactory<MicrowaveScreenHandler.MicrowaveData> {
    public MicrowaveBlockEntityImpl(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    public static void setActiveonClient(MicrowaveBlockEntity microwaveBlockEntity, boolean active) {
        microwaveBlockEntity.setActive(active);
        Collection<ServerPlayerEntity> watchingPlayers = PlayerLookup.tracking(microwaveBlockEntity);
        // Look at the other methods of `PlayerStream` to capture different groups of players.
        // We'll get to this later
        RegistryByteBuf clientData = new RegistryByteBuf(Unpooled.buffer(), microwaveBlockEntity.getWorld().getRegistryManager());
        clientData.writeBoolean(active);
        clientData.writeBlockPos(microwaveBlockEntity.getPos());
        // Then we'll send the packet to all the players
        watchingPlayers.forEach(player -> {
                    ServerPlayNetworking.send(player, new MicrowaveUpdatePayload(clientData));
                }
        );
    }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public MicrowaveScreenHandler.MicrowaveData getScreenOpeningData(ServerPlayerEntity player) {
        return new MicrowaveScreenHandler.MicrowaveData(getPos(), this.isActive);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }

    public static BlockEntityType.BlockEntityFactory<? extends MicrowaveBlockEntity> getFactory() {
        return MicrowaveBlockEntityImpl::new;
    }
}
