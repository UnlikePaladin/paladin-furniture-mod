package com.unlikepaladin.pfm.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.tracy.TracyFrameCapturer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MinecraftClient.class)
public interface PFMMinecraftClientAcccessor {
    @Nullable
    @Accessor("tracyFrameCapturer")
    TracyFrameCapturer getFrameCapturer();
}
