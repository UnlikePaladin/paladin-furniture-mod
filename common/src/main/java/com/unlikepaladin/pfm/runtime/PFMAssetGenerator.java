package com.unlikepaladin.pfm.runtime;

import com.google.common.base.Stopwatch;
import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.client.screens.PFMGeneratingOverlay;
import com.unlikepaladin.pfm.runtime.assets.PFMBlockstateModelProvider;
import com.unlikepaladin.pfm.runtime.assets.PFMLangProvider;
import com.unlikepaladin.pfm.runtime.data.PFMMCMetaProvider;
import com.unlikepaladin.pfm.utilities.PFMFileUtil;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.data.DataCache;
import net.minecraft.data.DataProvider;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PFMAssetGenerator extends PFMGenerator {
    public static boolean FROZEN = false;
    private int count;
    private String progress;

    public PFMAssetGenerator(Path output, boolean logOrDebug) {
        super(output, logOrDebug, LogManager.getLogger("PFM-Asset-Generation"));
        count = 3;
    }

    public void run() throws IOException {
        if (!FROZEN) {
            count = 0;
            setAssetsRunning(true);
            log("Packs:");
            for (ResourcePack pack : PFMRuntimeResources.RESOURCE_PACK_LIST) {
                log("\tPack " + pack.getInfo().title().getString().replace("Pack Fabric Mod ", "").replaceAll("\"", ""));
                for (String namespace : pack.getNamespaces(ResourceType.CLIENT_RESOURCES)) {
                    log("\t\tNamespace " + namespace);
                }
            }
            FROZEN = true;

            Path modListPath = output.resolve("modsList");
            Path hashPath = output.resolve("dataHash");
            if (!modListPath.toFile().isFile()) {
                Files.deleteIfExists(modListPath);
                Files.createFile(modListPath);
            }
            if (!hashPath.toFile().isFile()) {
                Files.deleteIfExists(hashPath);
                Files.createFile(hashPath);
            }
            List<String> hashToCompare = hashDirectory(output.toFile(), false);
            List<String> oldHash = Files.readAllLines(hashPath);
            List<String> modList = Files.readAllLines(modListPath);
            if (!hashToCompare.toString().equals(oldHash.toString()) || !modList.toString().replace("[", "").replace("]", "").equals(PaladinFurnitureMod.getVersionMap().toString())) {
                List<DataProvider> dataProviders = new ArrayList<>();
                dataProviders.add(new PFMMCMetaProvider(this, ResourceType.CLIENT_RESOURCES, "PFM-Assets"));
                dataProviders.add(new PFMBlockstateModelProvider(this));

                //MinecraftClient.getInstance().setOverlay(new PFMGeneratingOverlay(MinecraftClient.getInstance().getOverlay(), this, MinecraftClient.getInstance(), true));
                getLogger().info("Starting PFM Asset Generation");
                PFMFileUtil.deleteDir(output.toFile());
                PFMRuntimeResources.createDirIfNeeded(output);
                Stopwatch stopwatch = Stopwatch.createStarted();
                Stopwatch stopwatch2 = Stopwatch.createUnstarted();

                for (DataProvider provider : dataProviders) {
                    log("Starting provider: {}", provider.getName());
                    stopwatch2.start();
                    provider.run(null);
                    count++;
                    stopwatch2.stop();
                    log("{} finished after {} ms", provider.getName(), stopwatch2.elapsed(TimeUnit.MILLISECONDS));
                    stopwatch2.reset();
                }

                log("Starting provider: {}", "PFM Lang");
                stopwatch2.start();
                new PFMLangProvider(this).run();
                count++;
                stopwatch2.stop();
                log("{} finished after {} ms", "PFM Lang", stopwatch2.elapsed(TimeUnit.MILLISECONDS));
                stopwatch2.reset();

                getLogger().info("Asset providers took: {} ms", stopwatch.elapsed(TimeUnit.MILLISECONDS));
                this.createPackIcon();
                Files.deleteIfExists(hashPath);
                Files.createFile(hashPath);
                List<String> newDataHash = hashDirectory(output.toFile(), false);
                Files.writeString(PFMRuntimeResources.createDirIfNeeded(hashPath), newDataHash.toString().replace("[", "").replace("]", ""), StandardOpenOption.APPEND);

                Files.deleteIfExists(modListPath);
                Files.createFile(modListPath);
                Files.writeString(PFMRuntimeResources.createDirIfNeeded(modListPath), PaladinFurnitureMod.getVersionMap().toString().replace("[", "").replace("]", ""), StandardOpenOption.APPEND);
            } else {
                log("Data Hash and Mod list matched, skipping generation");
            }
            setAssetsRunning(false);
        }
    }

    @Override
    public float getProgress() {
        return (float) count / 3;
    }

    @Override
    public void setProgress(String progress) {
        this.progress = progress;
    }

    @Override
    public String getProgressString() {
        return progress;
    }
}
