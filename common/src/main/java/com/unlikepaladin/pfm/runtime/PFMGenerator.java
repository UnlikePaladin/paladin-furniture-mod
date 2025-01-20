package com.unlikepaladin.pfm.runtime;

import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.gson.*;
import com.unlikepaladin.pfm.data.materials.VariantBase;
import com.unlikepaladin.pfm.utilities.PFMFileUtil;
import com.unlikepaladin.pfm.utilities.Version;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public abstract class PFMGenerator implements PFMResourceProgress {
    protected final Path output;
    private final boolean logOrDebug;
    private final Logger logger;
    public static final HashFunction SHA1 = Hashing.sha1();
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    public static final JsonParser JSON_PARSER = new JsonParser();

    private static boolean assetsRunning = false;
    private static boolean dataRunning = false;
    private String progress;
    private String notification = null;

    private int progressCount = 0;
    private int totalCount = 0;

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public synchronized void setNotification(String notification) {
        this.notification = notification;
    }

    public synchronized void incrementCount() {
        progressCount++;
    }

    public void setCount(int count) {
        this.progressCount = count;
        this.setProgress("Progress: " + progressCount + " / " + totalCount);
    }

    protected PFMGenerator(Path output, boolean logOrDebug, Logger logger) {
        this.output = output;
        this.logOrDebug = logOrDebug;
        this.logger = logger;
    }

    protected void createPackIcon() {
        File file = new File(output.toFile(), "pack.png");
        try {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(PFMRuntimeResources.getImageData()));
            ImageIO.write(image, "png", file);
            image.flush();
        } catch (IOException e) {
            logger.warn("Failed to create resource icon {}", e.getMessage());
        }
    }

    public static boolean areAssetsRunning() {
        return assetsRunning;
    }

    public static boolean isDataRunning() {
        return dataRunning;
    }
    protected static void setAssetsRunning(boolean running) {
        assetsRunning = running;
    }
    protected static void setDataRunning(boolean running) {
        dataRunning = running;
    }

    public Logger getLogger() {
        return logger;
    }

    public Path getOutput() {
        return output;
    }

    public Path getOrCreateSubDirectory(String name) {
        return PFMRuntimeResources.createDirIfNeeded(output.resolve(name));
    }

    public abstract void  run() throws IOException;

    public synchronized void log(String s, Object p0) {
        log(s, p0, "");
    }

    public synchronized void log(String s) {
        log(s, "", "");
    }

    public synchronized void log(String s, Object p0, Object p1) {
        if (logOrDebug)
            logger.info(s, p0, p1);
        else
            logger.debug(s, p0, p1);
    }

    public List<String> hashDirectory(File directory, boolean includeHiddenFiles) throws IOException {
        if (!directory.isDirectory()) {
            logger.error("Not a directory");
            throw new IllegalArgumentException("Not a directory");
        }
        Vector<String> fileStreams = new Vector<>();
        collectFiles(directory, fileStreams, includeHiddenFiles);
        return fileStreams;
    }

    private void collectFiles(File directory, List<String> hashList,
                                     boolean includeHiddenFiles) throws IOException {
        File[] fileArray = directory.listFiles();
        if (fileArray != null) {
            List<File> files = new ArrayList<>(Arrays.asList(fileArray));
            files.removeIf(file -> file.getName().equals("pfmCacheData.json"));
            files.sort(Comparator.comparing(File::getName));

            for (File file : files) {
                if (file == null || file.getName().equals("pfmCacheData.json"))
                    continue;
                if (includeHiddenFiles || !Files.isHidden(file.toPath())) {
                    if (file.isDirectory()) {
                        collectFiles(file, hashList, includeHiddenFiles);
                    } else {
                        FileInputStream stream = new FileInputStream(file);
                        try {
                            HashCode code = HashCode.fromBytes(Files.readAllBytes(file.toPath()));
                            hashList.add(code.toString());
                        } catch (Exception e) {
                            logger.warn("File {} was less than 1 byte or invalid, skipping, {}", file.getName(), e);
                        }
                        stream.close();
                    }
                }
            }
        }
    }

    @Override
    public float getProgress() {
        return (float)  progressCount/ totalCount;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

    @Override
    public String getProgressString() {
        return progress;
    }

    @Override
    public String getNotificationProgressString() {
        return notification;
    }

    public static final class PFMCache {
        private final String modVersion;
        private final PFMFileUtil.ModLoader modLoader;
        private final List<String> folderHash;
        private  final List<Identifier> variants;

        public PFMCache(String modVersion, PFMFileUtil.ModLoader modLoader, List<String> folderHash, List<Identifier> variants) {
            this.modVersion = modVersion;
            this.modLoader = modLoader;
            this.folderHash = folderHash;
            this.variants = variants;
        }

        public String modVersion() {
            return modVersion;
        }

        public PFMFileUtil.ModLoader modLoader() {
            return modLoader;
        }

        public List<String> folderHash() {
            return folderHash;
        }

        public List<Identifier> variants() {
            return variants;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (PFMCache) obj;
            return Objects.equals(this.modVersion, that.modVersion) &&
                    Objects.equals(this.modLoader, that.modLoader) &&
                    Objects.equals(this.variants, that.variants) &&
                    Objects.equals(this.folderHash, that.folderHash);
        }

        @Override
        public int hashCode() {
            return Objects.hash(modVersion, modLoader, folderHash, variants);
        }

        @Override
        public String toString() {
            return "PFMCache{" +
                    "modVersion='" + modVersion + '\'' +
                    ", modLoader=" + modLoader +
                    ", variants=" + variants +
                    ", folderHash=" + folderHash +
                    '}';
        }

        public JsonElement toJson() {
            JsonObject obj = new JsonObject();
            obj.addProperty("mod_version", modVersion);
            obj.addProperty("mod_loader", modLoader.asString());

            JsonArray registeredVariants = new JsonArray();
            for (Identifier variant : variants) {
                registeredVariants.add(variant.toString());
            }
            obj.add("block_variants", registeredVariants);

            JsonArray folderHashArray = new JsonArray();
            for (String hash : folderHash) {
                folderHashArray.add(hash);
            }
            obj.add("folder_hash", folderHashArray);

            return obj;
        }

        public static PFMCache fromJson(JsonElement json) {
            if (json.isJsonObject()) {
                JsonObject jsonObject = json.getAsJsonObject();
                String modVersion = "0";
                List<String> folderHash = new ArrayList<>();
                PFMFileUtil.ModLoader modLoader = PFMFileUtil.ModLoader.INVALID;
                List<Identifier> variants = new ArrayList<>();

                if (jsonObject.has("mod_version")) {
                    modVersion = jsonObject.get("mod_version").getAsString();
                }
                if (jsonObject.has("mod_loader")) {
                    modLoader = PFMFileUtil.ModLoader.get(jsonObject.get("mod_loader").getAsString());
                }
                if (jsonObject.has("block_variants") && jsonObject.get("block_variants").isJsonArray()) {
                    for (JsonElement jsonElement : jsonObject.getAsJsonArray("block_variants")) {
                        variants.add(Identifier.tryParse(jsonElement.getAsString()));
                    }
                }

                if (jsonObject.has("folder_hash") && jsonObject.get("folder_hash").isJsonArray()) {
                    for (JsonElement jsonElement : jsonObject.getAsJsonArray("folder_hash")) {
                        folderHash.add(jsonElement.getAsString());
                    }
                }
                return new PFMCache(modVersion, modLoader, folderHash, variants);
            }
            return new PFMCache("0", PFMFileUtil.ModLoader.INVALID, Collections.emptyList(), Collections.emptyList());
        }
    }
}
