package com.unlikepaladin.pfm.menus.neoforge;

import com.unlikepaladin.pfm.networking.SyncRecipesPayload;
import com.unlikepaladin.pfm.recipes.FurnitureRecipe;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;

public class WorkbenchScreenHandlerImpl {

    public static void sendSyncRecipesPayload(PlayerEntity player, World world, ArrayList<FurnitureRecipe> recipes) {
        SyncRecipesPayload syncRecipesPacket = new SyncRecipesPayload(recipes);
        PacketDistributor.sendToPlayer((ServerPlayerEntity) player, syncRecipesPacket);
    }
}
