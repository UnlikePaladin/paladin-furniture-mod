package com.unlikepaladin.pfm.utilities.forge;

import com.unlikepaladin.pfm.utilities.PFMFileUtil;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;

public class PFMFileUtilImpl {
    public static Path getGamePath() {
        return FMLPaths.GAMEDIR.relative().normalize();
    }

    public static PFMFileUtil.ModLoader getModLoader() {
        return PFMFileUtil.ModLoader.MINECRAFTFORGE;
    }
}
