package com.unlikepaladin.pfm.client.fabric;

import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.client.screens.PFMConfigScreen;
import com.unlikepaladin.pfm.config.option.Side;
import com.unlikepaladin.pfm.networking.MicrowaveUpdatePayload;
import com.unlikepaladin.pfm.networking.SyncConfigPayload;
import com.unlikepaladin.pfm.networking.fabric.LeaveEventHandlerFabric;
import com.unlikepaladin.pfm.registry.NetworkIDs;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class ClientPacketRegistry {

    public static void registerClientPackets() {
        ClientPlayNetworking.registerGlobalReceiver(NetworkIDs.MICROWAVE_UPDATE_PACKET_ID, (payload, context) -> { payload.handle(context.player(), context.client());});
        ClientPlayNetworking.registerGlobalReceiver(NetworkIDs.CONFIG_SYNC_ID,
                (payload, context) -> {
                    context.client().execute(() -> {
                        payload.configOptionMap().forEach((title, configOption) -> {
                            PFMConfigScreen.isOnServer = true;
                            if (configOption.getSide() == Side.SERVER) {
                                LeaveEventHandlerFabric.originalConfigValues.put(title, PaladinFurnitureMod.getPFMConfig().options.get(title).getValue());
                                PaladinFurnitureMod.getPFMConfig().options.get(title).setValue(configOption.getValue());
                            }
                        });
                    });
                }
        );
    }
}
