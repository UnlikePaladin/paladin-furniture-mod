package com.unlikepaladin.pfm.utilities.fabric;

import com.unlikepaladin.pfm.PaladinFurnitureMod;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.SemanticVersion;
import net.fabricmc.loader.api.VersionParsingException;

import java.util.Objects;
import java.util.Optional;

public class VersionImpl {
    public static boolean getVersion(String targetVersionNum) {
        SemanticVersion currentVersion = null;
        SemanticVersion targetVersion = null;
        try {
           currentVersion = SemanticVersion.parse(FabricLoader.getInstance().getModContainer("pfm").get().getMetadata().getVersion().getFriendlyString());
           targetVersion = SemanticVersion.parse(targetVersionNum);
        } catch (VersionParsingException exception) {
            PaladinFurnitureMod.GENERAL_LOGGER.error("[Paladin's Furniture Update Check] Caught a VersionParsingException while parsing semantic versions!", exception);
        }
        return (currentVersion.compareTo(targetVersion) < 0);
    }

    public static String getCurrentVersion() {
        return FabricLoader.getInstance().getModContainer("pfm").get().getMetadata().getVersion().getFriendlyString();
    }

    public static boolean compareVersions(String targetVersionNum, String version2) {
        SemanticVersion currentVersion = null;
        SemanticVersion targetVersion = null;
        try {
            currentVersion = SemanticVersion.parse(FabricLoader.getInstance().getModContainer("pfm").get().getMetadata().getVersion().getFriendlyString());
            targetVersion = SemanticVersion.parse(targetVersionNum);
        } catch (VersionParsingException exception) {
            PaladinFurnitureMod.GENERAL_LOGGER.error("Caught a VersionParsingException while parsing semantic versions!", exception);
            return false;
        }
        return (currentVersion.compareTo(targetVersion) < 0);
    }
}
