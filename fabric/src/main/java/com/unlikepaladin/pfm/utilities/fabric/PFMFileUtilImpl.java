package com.unlikepaladin.pfm.utilities.fabric;

import com.unlikepaladin.pfm.utilities.PFMFileUtil;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public class PFMFileUtilImpl {
    public static Path getGamePath() {
        return FabricLoader.getInstance().getGameDir();
    }

    public static PFMFileUtil.ModLoader getModLoader() {
        return PFMFileUtil.ModLoader.FABRIC;
    }
}
