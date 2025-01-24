package com.unlikepaladin.pfm.utilities;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.resource.ResourcePack;
import net.minecraft.util.StringIdentifiable;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class PFMFileUtil {

    @ExpectPlatform
    public static Path getGamePath() {
        throw new RuntimeException();
    }

    public static void deleteDir(File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                if (! Files.isSymbolicLink(f.toPath())) {
                    deleteDir(f);
                }
            }
        }
        file.delete();
    }

    @ExpectPlatform
    public static List<ResourcePack> getSubPacks(ResourcePack pack) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static ModLoader getModLoader() {
        throw new AssertionError();
    }

    public enum ModLoader implements StringIdentifiable {
        MINECRAFTFORGE("minecraftforge"),
        FABRIC("fabric"),
        NEOFORGE("neoforge"),
        INVALID("");

        private String loader;
        ModLoader(String loader) {
            this.loader = loader;
        }

        public static ModLoader get(String modLoader) {
            for (ModLoader value : ModLoader.values()) {
                if (value.loader.equals(modLoader)) {
                    return value;
                }
            }
            return INVALID;
        }

        @Override
        public String asString() {
            return loader;
        }
    }
}
