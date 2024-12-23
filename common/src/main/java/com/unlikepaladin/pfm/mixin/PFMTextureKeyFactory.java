package com.unlikepaladin.pfm.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.client.data.TextureKey;
import net.minecraft.world.poi.PointOfInterestType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Set;

@Mixin(TextureKey.class)
public interface PFMTextureKeyFactory {
    @Invoker("<init>")
    static TextureKey newTextureKey(String name, @Nullable TextureKey parent){
        throw new AssertionError();
    }
}
