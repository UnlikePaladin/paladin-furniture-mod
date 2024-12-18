package com.unlikepaladin.pfm.registry.fabric;

import com.unlikepaladin.pfm.networking.*;
import com.unlikepaladin.pfm.registry.NetworkIDs;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class NetworkRegistryFabric {
    public static void registerPackets() {
        PayloadTypeRegistry.playC2S().register(NetworkIDs.MICROWAVE_ACTIVATE_PACKET_ID, MicrowaveActivatePayload.PACKET_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(NetworkIDs.MICROWAVE_ACTIVATE_PACKET_ID,
                (payload, context) -> {payload.handle(context.server(), context.player());});

        PayloadTypeRegistry.playC2S().register(NetworkIDs.TRASHCAN_CLEAR, TrashcanClearPayload.PACKET_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(NetworkIDs.TRASHCAN_CLEAR,
                (payload, context) -> {payload.handle(context.server(), context.player());});

        PayloadTypeRegistry.playC2S().register(NetworkIDs.TOILET_USE_ID, ToiletUsePayload.PACKET_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(NetworkIDs.TOILET_USE_ID,
                ((payload, context) -> payload.handle(context.server(), context.player())));

        PayloadTypeRegistry.playS2C().register(NetworkIDs.SYNC_FURNITURE_RECIPES, SyncRecipesPayload.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(NetworkIDs.CONFIG_SYNC_ID, SyncConfigPayload.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(NetworkIDs.MICROWAVE_UPDATE_PACKET_ID, MicrowaveUpdatePayload.PACKET_CODEC);
    }
}
