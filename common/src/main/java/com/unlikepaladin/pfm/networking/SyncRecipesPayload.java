package com.unlikepaladin.pfm.networking;

import com.unlikepaladin.pfm.menus.WorkbenchScreenHandler;
import com.unlikepaladin.pfm.recipes.FurnitureRecipe;
import com.unlikepaladin.pfm.registry.NetworkIDs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

import java.util.ArrayList;

public record SyncRecipesPayload(ArrayList<FurnitureRecipe> recipes) implements CustomPayload {
    public static final PacketCodec<RegistryByteBuf, SyncRecipesPayload> PACKET_CODEC = CustomPayload.codecOf(SyncRecipesPayload::write, SyncRecipesPayload::new);

    public SyncRecipesPayload(RegistryByteBuf buf) {
        this((ArrayList<FurnitureRecipe>) buf.readCollection(ArrayList::new, buf1 -> FurnitureRecipe.Serializer.read(buf)));
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return NetworkIDs.SYNC_FURNITURE_RECIPES;
    }

    public void write(RegistryByteBuf buf) {
        buf.writeCollection(recipes, (registry, recipe) -> FurnitureRecipe.Serializer.write((RegistryByteBuf) registry, recipe));
    }


    public void handle(PlayerEntity player, MinecraftClient client) {
        client.execute(() -> {
            if (player.getWorld() != null && player.currentScreenHandler instanceof WorkbenchScreenHandler) {
                ((WorkbenchScreenHandler) player.currentScreenHandler).setAllRecipes(recipes);
                ((WorkbenchScreenHandler) player.currentScreenHandler).updateInput();
            }
        });
    }
}
