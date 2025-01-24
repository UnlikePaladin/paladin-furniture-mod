package com.unlikepaladin.pfm.utilities.neoforge;


import com.unlikepaladin.pfm.utilities.PFMFileUtil;
import net.minecraft.resource.ResourcePack;
import net.neoforged.fml.loading.FMLPaths;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class PFMFileUtilImpl {
    public static Path getGamePath() {
        return FMLPaths.GAMEDIR.relative().normalize();
    }

    public static List<ResourcePack> getSubPacks(ResourcePack pack) {
        return List.of(pack);
    }

    public static PFMFileUtil.ModLoader getModLoader() {
        return PFMFileUtil.ModLoader.FABRIC;
    }
}
