package com.unlikepaladin.pfm.networking;

import com.unlikepaladin.pfm.menus.WorkbenchScreenHandler;
import com.unlikepaladin.pfm.recipes.FurnitureRecipe;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

import java.util.List;

public class ClientSyncRecipesPayloadHandler {
    public static void handlePacket(List<FurnitureRecipe> recipes) {
        MinecraftClient client = MinecraftClient.getInstance();
        client.execute(() -> {
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if (player.getWorld() != null && player.currentScreenHandler instanceof WorkbenchScreenHandler) {
                ((WorkbenchScreenHandler) player.currentScreenHandler).setAllRecipes(player.getWorld(), recipes);
                ((WorkbenchScreenHandler) player.currentScreenHandler).updateInput();
            }
        });
    }
}
