package com.unlikepaladin.pfm.utilities.fabric;

import com.unlikepaladin.pfm.mixin.fabric.PFMGroupResourcePackAccessor;
import net.fabricmc.fabric.impl.resource.loader.GroupResourcePack;
import com.unlikepaladin.pfm.utilities.PFMFileUtil;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resource.ResourcePack;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class PFMFileUtilImpl {
    public static Path getGamePath() {
        return FabricLoader.getInstance().getGameDir();
    }

    public static List<ResourcePack> getSubPacks(ResourcePack pack) {
        List<ResourcePack> list = new ArrayList<>();
        if (pack instanceof GroupResourcePack) {
            GroupResourcePack groupResourcePack = (GroupResourcePack) pack;
            list.addAll(((PFMGroupResourcePackAccessor)groupResourcePack).getPacks());
        }
        list.add(pack);
        return list;
    }

    public static PFMFileUtil.ModLoader getModLoader() {
        return PFMFileUtil.ModLoader.FABRIC;
    }
}
