package com.unlikepaladin.pfm.networking;

import com.unlikepaladin.pfm.recipes.DynamicFurnitureRecipe;
import com.unlikepaladin.pfm.recipes.FurnitureRecipe;
import com.unlikepaladin.pfm.recipes.SimpleFurnitureRecipe;
import com.unlikepaladin.pfm.registry.NetworkIDs;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

import java.util.ArrayList;
import java.util.Objects;

public final class SyncRecipesPayload implements CustomPayload {
    public static final PacketCodec<RegistryByteBuf, SyncRecipesPayload> PACKET_CODEC = CustomPayload.codecOf(SyncRecipesPayload::write, SyncRecipesPayload::new);
    private final ArrayList<FurnitureRecipe> recipes;

    public SyncRecipesPayload(ArrayList<FurnitureRecipe> recipes) {
        this.recipes = recipes;
    }

    public SyncRecipesPayload(RegistryByteBuf buf) {
        int recipeCount = buf.readInt();
        this.recipes = new ArrayList<>(recipeCount);
        for (int i = 0; i < recipeCount; i++) {
            int j = buf.readInt();
            if (j == 0) {
                recipes.add(SimpleFurnitureRecipe.Serializer.read(buf));
            } else {
                recipes.add(DynamicFurnitureRecipe.Serializer.read(buf));
            }
        }
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return NetworkIDs.SYNC_FURNITURE_RECIPES;
    }

    public void write(RegistryByteBuf buf) {
        buf.writeInt(recipes.size());
        for (FurnitureRecipe recipe : recipes) {
            if (recipe instanceof DynamicFurnitureRecipe) {
                buf.writeInt(1);
                recipe.write(buf);
            } else {
                buf.writeInt(0);
                recipe.write(buf);
            }
        }
    }

    public void handle() {
        ClientSyncRecipesPayloadHandler.handlePacket(this.recipes);
    }

    public ArrayList<FurnitureRecipe> recipes() {
        return recipes;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (SyncRecipesPayload) obj;
        return Objects.equals(this.recipes, that.recipes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(recipes);
    }

    @Override
    public String toString() {
        return "SyncRecipesPayload[" +
                "recipes=" + recipes + ']';
    }

}
