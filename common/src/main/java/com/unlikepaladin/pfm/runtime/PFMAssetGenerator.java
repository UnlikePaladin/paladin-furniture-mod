package com.unlikepaladin.pfm.runtime;

import com.google.common.base.Stopwatch;
import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.data.materials.StoneVariantRegistry;
import com.unlikepaladin.pfm.data.materials.WoodVariantRegistry;
import com.unlikepaladin.pfm.runtime.assets.PFMBlockstateModelProvider;
import com.unlikepaladin.pfm.runtime.assets.PFMLangProvider;
import com.unlikepaladin.pfm.runtime.data.PFMMCMetaProvider;
import com.unlikepaladin.pfm.utilities.PFMFileUtil;
import net.minecraft.SharedConstants;
import com.unlikepaladin.pfm.utilities.Version;
import net.minecraft.data.DataCache;
import net.minecraft.data.DataProvider;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class PFMAssetGenerator extends PFMGenerator {
    public static boolean FROZEN = false;

    public PFMAssetGenerator(Path output, boolean logOrDebug) {
        super(output, logOrDebug, LogManager.getLogger("PFM-Asset-Generation"));
    }

    public void run() throws IOException {
        if (!FROZEN) {
            setAssetsRunning(true);
            log("Packs:");
            for (ResourcePack pack : PFMRuntimeResources.RESOURCE_PACK_LIST) {
                log("\tPack {}", pack.getInfo().title().getString());
                for (String namespace : pack.getNamespaces(ResourceType.CLIENT_RESOURCES)) {
                    log("\t\tNamespace {}", namespace);
                }
            }
            FROZEN = true;

            Path pfmCacheDataFile = output.resolve("pfmCacheData.json");
            if (!pfmCacheDataFile.toFile().isFile()) {
                Files.deleteIfExists(pfmCacheDataFile);
                Files.createFile(pfmCacheDataFile);
                Files.writeString(pfmCacheDataFile, "{}");
            }
            PFMCache cached = PFMCache.fromJson(JSON_PARSER.parse(Files.readString(pfmCacheDataFile)));
            List<String> hashToCompare = hashDirectory(output.toFile(), false);
            List<Identifier> variants = new ArrayList<>();

            WoodVariantRegistry.getVariants().stream().sorted().forEach(woodVariant -> variants.add(woodVariant.identifier));
            StoneVariantRegistry.getVariants().stream().sorted().forEach(stoneVariant -> variants.add(stoneVariant.identifier));
            PFMCache current = new PFMCache(Version.getCurrentVersion(), PFMFileUtil.getModLoader(), hashToCompare, variants);

            if (!cached.equals(current)) {
                List<PFMProvider> providers = new ArrayList<>();
                //MinecraftClient.getInstance().setOverlay(new PFMGeneratingOverlay(MinecraftClient.getInstance().getOverlay(), this, MinecraftClient.getInstance(), true));
                getLogger().info("Starting PFM Asset Generation");
                PFMFileUtil.deleteDir(output.toFile());
                PFMRuntimeResources.createDirIfNeeded(output);
                Stopwatch stopwatch = Stopwatch.createStarted();


                PFMMCMetaProvider metaProvider = new PFMMCMetaProvider(this);
                metaProvider.setInfo(new PFMMCMetaProvider.PackInfo(ResourceType.CLIENT_RESOURCES, "PFM-Assets"));
                providers.add(metaProvider);
                providers.add(new PFMBlockstateModelProvider(this));
                providers.add(new PFMLangProvider(this));
                this.setTotalCount(providers.size());

                if (PaladinFurnitureMod.isClient)
                    ClientOverlaySetter.setOverlayToPFMOverlay(this);
                boolean allDone = false;

                ExecutorService executor = Executors.newFixedThreadPool(providers.size());
                List<? extends Future<?>> futures = providers.stream()
                        .map(provider -> executor.submit(provider::run))
                        .toList();

                while (!allDone) {
                    allDone = futures.stream().allMatch(Future::isDone);

                    int completedTasks = (int) futures.stream().filter(Future::isDone).count();
                    this.setCount(completedTasks);
                    if (PaladinFurnitureMod.isClient)
                        ClientOverlaySetter.updateScreen();
                }
                executor.shutdown();

                // Check for errors in providers
                for (Future<?> future : futures) {
                    try {
                        future.get(); // This will throw an exception if the task failed
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt(); // Restore interrupt status
                        error("Provider was interrupted: " + e.getMessage());
                    } catch (ExecutionException e) {
                        error("Provider failed with exception: " + e.getCause());
                        e.getCause().printStackTrace();
                    }
                }

                getLogger().info("Asset providers took: {} ms", stopwatch.elapsed(TimeUnit.MILLISECONDS));
                this.createPackIcon();

                Files.deleteIfExists(pfmCacheDataFile);
                Files.createFile(pfmCacheDataFile);
                List<String> newDataHash = hashDirectory(output.toFile(), false);
                PFMCache cache = new PFMCache(Version.getCurrentVersion(), PFMFileUtil.getModLoader(), newDataHash, variants);
                Files.writeString(pfmCacheDataFile, GSON.toJson(cache.toJson()), StandardOpenOption.APPEND);
            } else {
                getLogger().info("Data Hash for Assets and Variant List matched, skipping generation");
            }
            setAssetsRunning(false);
        }
    }
}
