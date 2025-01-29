package com.unlikepaladin.pfm.utilities.fabric;

import com.unlikepaladin.pfm.utilities.PFMFileUtil;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resource.ResourcePack;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public class PFMFileUtilImpl {
    @Nullable
    public static MinecraftServer currentServer;

    public static Path getGamePath() {
        return FabricLoader.getInstance().getGameDir();
    }

    public static List<ResourcePack> getSubPacks(ResourcePack pack) {
        return Collections.singletonList(pack);
    }

    public static PFMFileUtil.ModLoader getModLoader() {
        return PFMFileUtil.ModLoader.FABRIC;
    }

    public static MinecraftServer getCurrentServer() {
        return currentServer;
    }
}
