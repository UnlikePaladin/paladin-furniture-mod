package com.unlikepaladin.pfm.utilities.forge;

import com.unlikepaladin.pfm.utilities.PFMFileUtil;
import net.minecraft.resource.ResourcePack;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public class PFMFileUtilImpl {
    public static Path getGamePath() {
        return FMLPaths.GAMEDIR.relative().normalize();
    }

    public static List<ResourcePack> getSubPacks(ResourcePack pack) {
        return Collections.singletonList(pack);
    }

    public static PFMFileUtil.ModLoader getModLoader() {
        return PFMFileUtil.ModLoader.MINECRAFTFORGE;
    }
}
