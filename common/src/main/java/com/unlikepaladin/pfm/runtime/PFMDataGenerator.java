package com.unlikepaladin.pfm.runtime;

import com.google.common.base.Stopwatch;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.data.materials.StoneVariantRegistry;
import com.unlikepaladin.pfm.data.materials.WoodVariantRegistry;
import com.unlikepaladin.pfm.runtime.data.PFMLootTableProvider;
import com.unlikepaladin.pfm.runtime.data.PFMMCMetaProvider;
import com.unlikepaladin.pfm.runtime.data.PFMRecipeProvider;
import com.unlikepaladin.pfm.runtime.data.PFMTagProvider;
import com.unlikepaladin.pfm.utilities.PFMFileUtil;
import com.unlikepaladin.pfm.utilities.Version;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class PFMDataGenerator extends PFMGenerator {
    public static boolean FROZEN = false;

    public PFMDataGenerator(Path output, boolean logOrDebug) {
        super(output, logOrDebug, LogManager.getLogger("PFM-DataGen"));
    }

    public void run() throws IOException {
        if (!FROZEN) {
            setDataRunning(true);
            log("Packs:");
            for (ResourcePack pack : PFMRuntimeResources.RESOURCE_PACK_LIST) {
                log("\tPack {}", pack.getName());
                for (String namespace : pack.getNamespaces(ResourceType.SERVER_DATA)) {
                    log("\t\tNamespace {}", namespace);
                }
            }
            FROZEN = true;
            Path pfmCacheDataFile = output.resolve("pfmCacheData.json");
            if (!pfmCacheDataFile.toFile().isFile()) {
                Files.deleteIfExists(pfmCacheDataFile);
                Files.createFile(pfmCacheDataFile);
                Files.write(
                        pfmCacheDataFile,
                        "{}".getBytes(StandardCharsets.UTF_8),
                        StandardOpenOption.APPEND
                );
            }
            String fileContent = new String(Files.readAllBytes(pfmCacheDataFile), StandardCharsets.UTF_8);
            PFMCache cached = PFMCache.fromJson(JSON_PARSER.parse(fileContent));
            List<String> hashToCompare = hashDirectory(output.toFile(), false);
            List<Identifier> variants = new ArrayList<>();

            WoodVariantRegistry.getVariants().stream().sorted().forEach(woodVariant -> variants.add(woodVariant.identifier));
            StoneVariantRegistry.getVariants().stream().sorted().forEach(stoneVariant -> variants.add(stoneVariant.identifier));
            PFMCache current = new PFMCache(Version.getCurrentVersion(), PFMFileUtil.getModLoader(), hashToCompare, variants);

            if (!cached.equals(current)) {
                List<PFMProvider> providers = new ArrayList<>();
                getLogger().info("Starting PFM Data Generation");
                PFMFileUtil.deleteDir(output.toFile());
                Stopwatch stopwatch = Stopwatch.createStarted();

                providers.add(new PFMTagProvider(this));
                providers.add(new PFMLootTableProvider(this));
                providers.add(new PFMRecipeProvider(this));

                PFMMCMetaProvider metaProvider = new PFMMCMetaProvider(this);
                metaProvider.setInfo(new PFMMCMetaProvider.PackInfo("PFM-Data"));
                providers.add(metaProvider);
                this.setTotalCount(providers.size());

                if (PaladinFurnitureMod.isClient)
                    ClientOverlaySetter.setOverlayToPFMOverlay(this);
                boolean allDone = false;

                ExecutorService executor = Executors.newFixedThreadPool(providers.size());
                List<? extends Future<?>> futures = providers.stream()
                        .map(provider -> executor.submit(provider::run))
                        .collect(Collectors.toList());

                while (!allDone) {
                    allDone = futures.stream().allMatch(Future::isDone);

                    int completedTasks = (int) futures.stream().filter(Future::isDone).count();
                    this.setCount(completedTasks);
                    if (PaladinFurnitureMod.isClient)
                        ClientOverlaySetter.updateScreen();
                }
                executor.shutdown();

                getLogger().info("Data providers took: {} ms", stopwatch.elapsed(TimeUnit.MILLISECONDS));

                Files.deleteIfExists(pfmCacheDataFile);
                Files.createFile(pfmCacheDataFile);
                List<String> newDataHash = hashDirectory(output.toFile(), false);
                PFMCache cache = new PFMCache(Version.getCurrentVersion(), PFMFileUtil.getModLoader(), newDataHash, variants);
                Files.write(
                        pfmCacheDataFile,
                        GSON.toJson(cache.toJson()).getBytes(StandardCharsets.UTF_8),
                        StandardOpenOption.APPEND
                );
            } else {
                getLogger().info("Data Hash for Game Data and Variant List matched, skipping generation");
            }
            setDataRunning(false);
        }
    }
}