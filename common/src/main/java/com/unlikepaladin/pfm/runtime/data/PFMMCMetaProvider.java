package com.unlikepaladin.pfm.runtime.data;

import com.mojang.bridge.game.PackType;
import com.unlikepaladin.pfm.runtime.PFMDataGenerator;
import com.unlikepaladin.pfm.runtime.PFMGenerator;
import com.unlikepaladin.pfm.runtime.PFMProvider;
import com.unlikepaladin.pfm.runtime.PFMRuntimeResources;
import net.minecraft.SharedConstants;
import net.minecraft.data.DataCache;
import org.apache.commons.io.IOUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class PFMMCMetaProvider extends PFMProvider {

    private PackInfo info;

    public PFMMCMetaProvider(PFMGenerator parent) {
        super(parent, "PFM MC Meta");
        parent.setProgress("Generating Minecraft Metadata");
    }

    public void setInfo(PackInfo info) {
        this.info = info;
    }

    @Override
    public void run() {
        startProviderRun();
        try(BufferedWriter writer = IOUtils.buffer(new FileWriter(new File(PFMRuntimeResources.createDirIfNeeded(getParent().getOutput()).toFile(), "pack.mcmeta")))) {
            writer.write("{\n");
            writer.write("  \"pack\":\n   {\n");
            writer.write("          \"pack_format\": ");
            writer.write(String.valueOf(SharedConstants.getGameVersion().getPackVersion(info.type)));
            writer.write(",\n           \"description\" : \"" + info.description + "\"\n  }\n");
            writer.write("}");
        } catch (IOException e) {
            getParent().getLogger().error("Writer exception: " + e);
            e.printStackTrace();
        }
        endProviderRun();
    }

    public record PackInfo(PackType type, String description) {
    }
}
