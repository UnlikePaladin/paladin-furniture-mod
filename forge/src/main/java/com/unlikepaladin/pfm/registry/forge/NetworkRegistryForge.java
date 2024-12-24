package com.unlikepaladin.pfm.registry.forge;

import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.advancements.PFMCriteria;
import com.unlikepaladin.pfm.networking.SyncRecipesPayload;
import com.unlikepaladin.pfm.networking.forge.*;
import io.netty.util.AttributeKey;
import net.minecraft.server.network.ServerPlayerConfigurationTask;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.event.network.GatherLoginConfigurationTasksEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.*;
import net.minecraftforge.network.config.SimpleConfigurationTask;
import org.apache.commons.lang3.function.TriConsumer;

@Mod.EventBusSubscriber(modid = "pfm", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class NetworkRegistryForge {

    public static final SimpleChannel PFM_CHANNEL = ChannelBuilder.named(
            Identifier.of(PaladinFurnitureMod.MOD_ID, "main_channel")
    ).networkProtocolVersion(1).simpleChannel();
    public static final AttributeKey<ForgePacketHandler> CONTEXT = AttributeKey.newInstance("pfm:handshake");


    public static void registerPackets() {
        int id = 0;

        PFM_CHANNEL.messageBuilder(MicrowaveUpdatePacket.class, NetworkDirection.PLAY_TO_CLIENT).encoder(MicrowaveUpdatePacket::encode).decoder(MicrowaveUpdatePacket::decode).consumerNetworkThread(CONTEXT, MicrowaveUpdatePacket::handle).add();
        PFM_CHANNEL.messageBuilder(MicrowaveActivePacket.class, NetworkDirection.PLAY_TO_SERVER).encoder(MicrowaveActivePacket::encode).decoder(MicrowaveActivePacket::decode).consumerNetworkThread(CONTEXT, MicrowaveActivePacket::handle).add();
        PFM_CHANNEL.messageBuilder(ToiletUsePacket.class, NetworkDirection.PLAY_TO_SERVER).encoder(ToiletUsePacket::encode).decoder(ToiletUsePacket::decode).consumerNetworkThread(CONTEXT, ToiletUsePacket::handle).add();
        PFM_CHANNEL.messageBuilder(TrashcanClearPacket.class, NetworkDirection.PLAY_TO_SERVER).encoder(TrashcanClearPacket::encode).decoder(TrashcanClearPacket::decode).consumerNetworkThread(CONTEXT, TrashcanClearPacket::handle).add();
        PFM_CHANNEL.messageBuilder(SyncConfigPacket.class, NetworkDirection.CONFIGURATION_TO_CLIENT).encoder(SyncConfigPacket::encode).decoder(SyncConfigPacket::decode).consumerNetworkThread(CONTEXT, SyncConfigPacket::handle).add();
        PFM_CHANNEL.messageBuilder(SyncRecipesPayload.class, NetworkDirection.PLAY_TO_CLIENT).encoder(SyncRecipesPayload::write).decoder(SyncRecipesPayload::new).consumerNetworkThread(CONTEXT, (forgePacketHandler, syncRecipesPayload, context) -> {context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> syncRecipesPayload::handle)); context.setPacketHandled(true);}).add();

        // PFM_CHANNEL.registerMessage(++id, ResetConfigPacket.class, ResetConfigPacket::encode, ResetConfigPacket::decode, ResetConfigPacket::handle);
    }

    @SubscribeEvent
    public static void onServerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayerEntity) {
            if (PaladinFurnitureMod.getPFMConfig().shouldGiveGuideBook()) {
                //Give book
                PFMCriteria.GUIDE_BOOK_CRITERION.trigger((ServerPlayerEntity) event.getEntity());
            }
        }
   }

    @SubscribeEvent
    public static void onConfigSync(GatherLoginConfigurationTasksEvent event) {
        event.addTask(new SimpleConfigurationTask(new ServerPlayerConfigurationTask.Key("pfm:sync_config"), (context) -> {
            NetworkRegistryForge.PFM_CHANNEL.send(new SyncConfigPacket(PaladinFurnitureMod.getPFMConfig().options), context.getConnection());
        }));
    }
}
