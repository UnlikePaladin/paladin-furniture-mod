package com.unlikepaladin.pfm.runtime;

import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

    private static boolean assetsRunning = false;
    private static boolean dataRunning = false;
    private String progress;

    private int progressCount = 0;
    private int totalCount = 0;

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public synchronized void incrementCount() {
        progressCount++;
    }

    public void setCount(int count) {
        this.progressCount = count;
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
        if (true)
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
            files.removeIf(file -> file.getName().contains("dataHash") || file.getName().contains("modsList"));
            files.sort(Comparator.comparing(File::getName));

            for (File file : files) {
                if (file.getName().contains("dataHash") || file.getName().contains("modsList") || file == null)
                    continue;
                if (includeHiddenFiles || !Files.isHidden(file.toPath())) {
                    if (file.isDirectory()) {
                        collectFiles(file, hashList, includeHiddenFiles);
                    } else {
                        FileInputStream stream = new FileInputStream(file);
                        try {
                            HashCode code = HashCode.fromBytes(stream.readAllBytes());
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
}
