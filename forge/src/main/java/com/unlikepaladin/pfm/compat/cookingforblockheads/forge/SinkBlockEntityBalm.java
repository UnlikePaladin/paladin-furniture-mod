package com.unlikepaladin.pfm.compat.cookingforblockheads.forge;

import com.unlikepaladin.pfm.blocks.blockentities.SinkBlockEntity;
import net.blay09.mods.cookingforblockheads.api.capability.CapabilityKitchenConnector;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SinkBlockEntityBalm extends SinkBlockEntity {
    private final LazyOptional<CapabilityKitchenConnector.IKitchenConnector> connector;

    public SinkBlockEntityBalm() {
        super();
        this.connector = LazyOptional.of(CapabilityKitchenConnector.CAPABILITY::getDefaultInstance);
    }

    @Nonnull
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return CapabilityKitchenConnector.CAPABILITY.orEmpty(cap, this.connector);
    }

    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return new BlockEntityUpdateS2CPacket(this.pos, 0, this.toInitialChunkDataNbt());
    }
}
