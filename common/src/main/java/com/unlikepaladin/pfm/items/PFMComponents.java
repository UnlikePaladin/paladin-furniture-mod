package com.unlikepaladin.pfm.items;

import com.mojang.serialization.Codec;
import com.unlikepaladin.pfm.PaladinFurnitureMod;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.component.DataComponentType;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

public class PFMComponents {
    public static void registerComponents() {
        PaladinFurnitureMod.GENERAL_LOGGER.info("Registering {} components", PaladinFurnitureMod.MOD_ID);
        VARIANT_COMPONENT = register(
                Identifier.of(PaladinFurnitureMod.MOD_ID, "variant"),
                DataComponentType.<Identifier>builder().codec(Identifier.CODEC).packetCodec(Identifier.PACKET_CODEC).build()
        );

        COLOR_COMPONENT = register(
                Identifier.of(PaladinFurnitureMod.MOD_ID, "color"),
                DataComponentType.<DyeColor>builder().codec(DyeColor.CODEC).packetCodec(DyeColor.PACKET_CODEC).build()
        );
    }

    @ExpectPlatform
    public static <T> DataComponentType<T> register(Identifier id, DataComponentType<T> type) {
        throw new AssertionError();
    }

    public static DataComponentType<Identifier> VARIANT_COMPONENT;

    public static DataComponentType<DyeColor> COLOR_COMPONENT;
}
