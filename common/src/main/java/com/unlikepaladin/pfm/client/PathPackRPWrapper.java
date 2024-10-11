package com.unlikepaladin.pfm.client;

import com.unlikepaladin.pfm.runtime.PFMRuntimeResources;
import net.minecraft.resource.InputSupplier;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackInfo;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.metadata.PackResourceMetadata;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class PathPackRPWrapper implements ResourcePack {
    private final Supplier<ResourcePack> delegate;
    private final PackResourceMetadata packResourceMetadata;
    private final ResourcePackInfo resourcePackInfo;

    public PathPackRPWrapper(Supplier<ResourcePack> delegate, PackResourceMetadata packResourceMetadata, ResourcePackInfo packInfo) {
        this.delegate = delegate;
        this.packResourceMetadata = packResourceMetadata;
        this.resourcePackInfo = packInfo;
    }

    @Nullable
    @Override
    public InputSupplier<InputStream> openRoot(String... segments) {
        if (PFMRuntimeResources.ready && Arrays.asList(segments).contains("pack.png")) {
            return delegate.get().openRoot(segments);
        }
        return null;
    }

    @Override
    public InputSupplier<InputStream> open(ResourceType type, Identifier id) {
        if (PFMRuntimeResources.ready)
            return delegate.get().open(type, id);
        return null;
    }

    @Override
    public void findResources(ResourceType type, String namespace, String prefix, ResultConsumer consumer) {
        if (PFMRuntimeResources.ready)
            delegate.get().findResources(type, namespace, prefix, consumer);
    }

    @Override
    public Set<String> getNamespaces(ResourceType type) {
        if (PFMRuntimeResources.ready)
            return delegate.get().getNamespaces(type);
        return null;
    }

    @Nullable
    @Override
    public <T> T parseMetadata(ResourceMetadataReader<T> metaReader) throws IOException {
        if (metaReader.getKey().equals("pack")) {
            return (T) packResourceMetadata;
        }
        if (PFMRuntimeResources.ready)
            return delegate.get().parseMetadata(metaReader);
        return null;
    }

    public String getName() {
        return "PFM-Runtime-RP";
    }

    @Override
    public ResourcePackInfo getInfo() {
        return resourcePackInfo;
    }

    @Override
    public void close() {
        if (PFMRuntimeResources.ready)
            delegate.get().close();
    }
}
