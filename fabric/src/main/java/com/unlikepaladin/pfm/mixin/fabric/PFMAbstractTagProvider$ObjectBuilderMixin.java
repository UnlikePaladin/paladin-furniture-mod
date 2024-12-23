package com.unlikepaladin.pfm.mixin.fabric;

import net.minecraft.block.Block;
import net.minecraft.data.tag.ValueLookupTagProvider;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.TagBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.function.Function;

@Mixin(ValueLookupTagProvider.ObjectBuilder.class)
public interface PFMAbstractTagProvider$ObjectBuilderMixin {
    @Invoker("<init>")
    static <T> ValueLookupTagProvider.ObjectBuilder<T> newTagProvider(TagBuilder builder, Function<T, RegistryKey<T>> valueToKey) {
        throw new AssertionError();
    }
}
