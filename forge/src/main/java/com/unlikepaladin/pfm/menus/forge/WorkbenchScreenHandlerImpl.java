package com.unlikepaladin.pfm.menus.forge;

import com.unlikepaladin.pfm.networking.SyncRecipesPayload;
import com.unlikepaladin.pfm.recipes.FurnitureRecipe;
import com.unlikepaladin.pfm.registry.forge.NetworkRegistryForge;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.network.PacketDistributor;

import java.util.ArrayList;

public class WorkbenchScreenHandlerImpl {

    public static void sendSyncRecipesPayload(PlayerEntity player, World world, ArrayList<FurnitureRecipe> recipes) {
        SyncRecipesPayload syncRecipesPacket = new SyncRecipesPayload(recipes);
        NetworkRegistryForge.PFM_CHANNEL.send(syncRecipesPacket, PacketDistributor.PLAYER.with((ServerPlayerEntity) player));
    }
}
