package com.unlikepaladin.pfm.data;

import net.minecraft.registry.RegistryKey;

public interface PFMTag<T> {
    PFMTag<T> add(T... values);
    PFMTag<T> addKey(RegistryKey<T>... keys);
}
