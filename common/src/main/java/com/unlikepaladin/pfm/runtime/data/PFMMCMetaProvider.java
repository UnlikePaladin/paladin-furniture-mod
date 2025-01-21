package com.unlikepaladin.pfm.runtime.data;

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
import java.util.Objects;

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
            writer.write(String.valueOf(SharedConstants.getGameVersion().getPackVersion()));
            writer.write(",\n           \"description\" : \"" + info.description + "\"\n  }\n");
            writer.write("}");
        } catch (IOException e) {
            getParent().getLogger().error("Writer exception: " + e);
            e.printStackTrace();
        }
        endProviderRun();
    }

    public static final class PackInfo {
        private final String description;

        public PackInfo(String description) {
            this.description = description;
        }

        public String description() {
            return description;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            PackInfo that = (PackInfo) obj;
            return Objects.equals(this.description, that.description);
        }

        @Override
        public int hashCode() {
            return Objects.hash(description);
        }

        @Override
        public String toString() {
            return "PackInfo[" +
                    "description=" + description + ']';
        }

    }
}
