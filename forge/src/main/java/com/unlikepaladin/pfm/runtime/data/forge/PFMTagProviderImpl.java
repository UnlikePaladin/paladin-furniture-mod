package com.unlikepaladin.pfm.runtime.data.forge;

import com.unlikepaladin.pfm.data.PFMTag;
import net.minecraft.data.tag.TagProvider;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.TagBuilder;
import net.minecraft.registry.Registry;
import net.minecraft.registry.tag.TagKey;

import java.util.function.Function;
import java.util.stream.Stream;

public class PFMTagProviderImpl {

    public static <T> PFMTag<T> getProviderPlatform(TagBuilder builder, Registry<T> registry, String modID) {
        return new ObjectBuilder<>(builder, t -> registry.getKey(t).get(), modID);
    }

    public static class ObjectBuilder<T> extends TagProvider.ProvidedTagBuilder<T>  implements PFMTag<T> {
        private final Function<T, RegistryKey<T>> valueToKey;

        ObjectBuilder(TagBuilder arg, Function<T, RegistryKey<T>> function, String modId) {
            super(arg, modId);
            this.valueToKey = function;
        }

        public ObjectBuilder<T> addTag(TagKey<T> arg) {
            super.addTag(arg);
            return this;
        }

        public final ObjectBuilder<T> add(T value) {
            this.add(this.valueToKey.apply(value));
            return this;
        }

        @SafeVarargs
        public final ObjectBuilder<T> add(T... values) {
            Stream.of(values).map(this.valueToKey).forEach(this::add);
            return this;
        }

        @Override
        public PFMTag<T> addKey(RegistryKey<T>... keys) {
            for (RegistryKey<T> key : keys){
                super.add(key);
            }
            return this;
        }

        public final RegistryKey<T> getKey(T value) {
            return this.valueToKey.apply(value);
        }
    }
}
