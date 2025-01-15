package com.unlikepaladin.pfm.client;

import com.unlikepaladin.pfm.runtime.PFMRuntimeResources;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.metadata.PackResourceMetadata;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class PathPackRPWrapper implements ResourcePack {
    private final Supplier<ResourcePack> delegate;
    private final PackResourceMetadata packResourceMetadata;

    public PathPackRPWrapper(Supplier<ResourcePack> delegate, PackResourceMetadata packResourceMetadata) {
        this.delegate = delegate;
        this.packResourceMetadata = packResourceMetadata;
    }

    @Nullable
    @Override
    public InputStream openRoot(String fileName) throws IOException {
        if (PFMRuntimeResources.ready && fileName.equals("pack.png")) {
            return delegate.get().openRoot(fileName);
        }
        return InputStream.nullInputStream();
    }

    @Override
    public InputStream open(ResourceType type, Identifier id) throws IOException {
        if (PFMRuntimeResources.ready)
            return delegate.get().open(type, id);
        return null;
    }

    @Override
    public Collection<Identifier> findResources(ResourceType type, String namespace, String prefix, int maxDepth, Predicate<String> pathFilter) {
        if (PFMRuntimeResources.ready)
            return delegate.get().findResources(type, namespace, prefix, maxDepth, pathFilter);
        return new ArrayList<>();
    }

    @Override
    public boolean contains(ResourceType type, Identifier id) {
        if (PFMRuntimeResources.ready)
            return delegate.get().contains(type, id);
        return false;
    }

    @Override
    public Set<String> getNamespaces(ResourceType type) {
        if (PFMRuntimeResources.ready)
            return delegate.get().getNamespaces(type);
        return Set.of("pfm");
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

    @Override
    public String getName() {
        return "PFM-Runtime-RP";
    }

    @Override
    public void close() {
        if (PFMRuntimeResources.ready)
            delegate.get().close();
    }
}
