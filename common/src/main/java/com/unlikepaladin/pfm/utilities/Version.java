package com.unlikepaladin.pfm.utilities;

import com.unlikepaladin.pfm.PaladinFurnitureMod;
import dev.architectury.injectables.annotations.ExpectPlatform;

public class Version {
    private final String version;

    public Version(String version) {
        this.version = version;
    }

    @ExpectPlatform
    public static boolean getVersion(String targetVersionNum) {
        PaladinFurnitureMod.GENERAL_LOGGER.error("[Paladin's Furniture Update Check] Unable to get version from mod!");
        return false;
    }

    @ExpectPlatform
    public static boolean compareVersions(String targetVersionNum, String version2) {
        PaladinFurnitureMod.GENERAL_LOGGER.error("Unable to compare versions!");
        return false;
    }


    @Override
    public String toString() {
        return super.toString();
    }

    @ExpectPlatform
    public static String getCurrentVersion() {
        return "Version is: 0";
    }
}
