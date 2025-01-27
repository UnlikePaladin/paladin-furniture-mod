package com.unlikepaladin.pfm.utilities.fabric;

import com.unlikepaladin.pfm.utilities.PFMFileUtil;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resource.ResourcePack;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public class PFMFileUtilImpl {
    public static Path getGamePath() {
        return FabricLoader.getInstance().getGameDir();
    }

    public static List<ResourcePack> getSubPacks(ResourcePack pack) {
        return Collections.singletonList(pack);
    }

    public static PFMFileUtil.ModLoader getModLoader() {
        return PFMFileUtil.ModLoader.FABRIC;
    }
}
