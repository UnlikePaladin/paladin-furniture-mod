package com.unlikepaladin.pfm.items;

import com.unlikepaladin.pfm.PaladinFurnitureMod;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.component.ComponentType;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

public class PFMComponents {
    public static void registerComponents() {
        PaladinFurnitureMod.GENERAL_LOGGER.info("Registering {} components", PaladinFurnitureMod.MOD_ID);
        VARIANT_COMPONENT = register(
                Identifier.of(PaladinFurnitureMod.MOD_ID, "variant"),
                ComponentType.<Identifier>builder().codec(Identifier.CODEC).packetCodec(Identifier.PACKET_CODEC).build()
        );

        COLOR_COMPONENT = register(
                Identifier.of(PaladinFurnitureMod.MOD_ID, "color"),
                ComponentType.<DyeColor>builder().codec(DyeColor.CODEC).packetCodec(DyeColor.PACKET_CODEC).build()
        );
    }

    @ExpectPlatform
    public static <T> ComponentType<T> register(Identifier id, ComponentType<T> type) {
        throw new AssertionError();
    }

    public static ComponentType<Identifier> VARIANT_COMPONENT;

    public static ComponentType<DyeColor> COLOR_COMPONENT;
}
