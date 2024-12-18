package com.unlikepaladin.pfm.registry;

import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.networking.*;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public class NetworkIDs {
    public static CustomPayload.Id<MicrowaveUpdatePayload> MICROWAVE_UPDATE_PACKET_ID = new CustomPayload.Id<>(Identifier.of(PaladinFurnitureMod.MOD_ID, "microwave_button_update"));
    public static final CustomPayload.Id<MicrowaveActivatePayload> MICROWAVE_ACTIVATE_PACKET_ID = new CustomPayload.Id<>(Identifier.of(PaladinFurnitureMod.MOD_ID, "microwave_activate"));
    public static final CustomPayload.Id<TrashcanClearPayload> TRASHCAN_CLEAR = new CustomPayload.Id<>(Identifier.of(PaladinFurnitureMod.MOD_ID, "trashcan_clear"));
    public static final CustomPayload.Id<SyncRecipesPayload> SYNC_FURNITURE_RECIPES = new CustomPayload.Id<>(Identifier.of(PaladinFurnitureMod.MOD_ID, "sync_furniture_recipes"));

    public static CustomPayload.Id<ToiletUsePayload> TOILET_USE_ID = new CustomPayload.Id<>(Identifier.of(PaladinFurnitureMod.MOD_ID, "toilet_use"));

    public static CustomPayload.Id<SyncConfigPayload> CONFIG_SYNC_ID = new CustomPayload.Id<>(Identifier.of(PaladinFurnitureMod.MOD_ID, "config_sync"));


}
