package com.unlikepaladin.pfm.menus.fabric;

import com.unlikepaladin.pfm.networking.SyncRecipesPayload;
import com.unlikepaladin.pfm.recipes.FurnitureRecipe;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class WorkbenchScreenHandlerImpl {
    public static void sendSyncRecipesPayload(PlayerEntity player, World world, ArrayList<FurnitureRecipe> recipes) {
        ServerPlayNetworking.send((ServerPlayerEntity) player, new SyncRecipesPayload(recipes));
    }
}
