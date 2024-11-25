package com.unlikepaladin.pfm.neoforge;

import com.google.common.base.Suppliers;
import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.client.PathPackRPWrapper;
import com.unlikepaladin.pfm.client.neoforge.ColorRegistryNeoForge;
import com.unlikepaladin.pfm.config.PaladinFurnitureModConfig;
import com.unlikepaladin.pfm.registry.dynamic.neoforge.LateBlockRegistryNeoForge;
import com.unlikepaladin.pfm.registry.neoforge.*;
import net.minecraft.resource.*;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import com.unlikepaladin.pfm.runtime.PFMDataGenerator;
import com.unlikepaladin.pfm.runtime.PFMRuntimeResources;
import net.minecraft.SharedConstants;
import net.minecraft.resource.metadata.PackResourceMetadata;
import net.minecraft.text.Text;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddPackFindersEvent;


import java.io.IOException;
import java.util.List;
import java.util.Optional;


@Mod(PaladinFurnitureMod.MOD_ID)
public class PaladinFurnitureModNeoForge extends PaladinFurnitureMod {
    public static PaladinFurnitureModConfig pfmConfig;
    public PaladinFurnitureModNeoForge() {
        pfmConfig = new PaladinFurnitureModConfig(FMLPaths.CONFIGDIR.get());
        try {
            pfmConfig.initialize();
        } catch (IOException e) {
            GENERAL_LOGGER.error("Failed to initialize Paladin's Furniture configuration, default values will be used instead");
            GENERAL_LOGGER.error("", e);
        }
        this.commonInit();
        NeoForge.EVENT_BUS.register(EntityRegistryNeoForge.class);
        NeoForge.EVENT_BUS.register(BlockItemRegistryNeoForge.class);
        NeoForge.EVENT_BUS.register(StatisticsRegistryNeoForge.class);
        NeoForge.EVENT_BUS.register(ScreenHandlerRegistryNeoForge.class);
        NeoForge.EVENT_BUS.register(RecipeRegistryNeoForge.class);
        NeoForge.EVENT_BUS.register(BlockEntityRegistryNeoForge.class);
        NeoForge.EVENT_BUS.register(SoundRegistryNeoForge.class);
        NeoForge.EVENT_BUS.register(NetworkRegistryNeoForge.class);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(EventPriority.LOW, ColorRegistryNeoForge::registerBlockColors);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(EventPriority.LOWEST, ColorRegistryNeoForge::registerItemColors);
        NetworkRegistryNeoForge.registerPackets();
        LateBlockRegistryNeoForge.addDynamicBlockRegistration();
        PaladinFurnitureMod.isClient = FMLEnvironment.dist == Dist.CLIENT;
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ItemGroupRegistryNeoForge::registerItemGroups);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ItemGroupRegistryNeoForge::addToVanillaItemGroups);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(PaladinFurnitureModNeoForge::generateResources);

    }

    @SubscribeEvent
    public static void generateResources(AddPackFindersEvent event) {
        int data = SharedConstants.getGameVersion().getResourceVersion(ResourceType.SERVER_DATA);
        int resource = SharedConstants.getGameVersion().getResourceVersion(ResourceType.CLIENT_RESOURCES);
        if (event.getPackType() == ResourceType.CLIENT_RESOURCES) {
            PackResourceMetadata packResourceMetadata = new PackResourceMetadata(Text.literal("Runtime Generated Assets for PFM"), SharedConstants.getGameVersion().getResourceVersion(ResourceType.CLIENT_RESOURCES), Optional.empty());
            ResourcePackProfile.PackFactory packFactory = new ResourcePackProfile.PackFactory() {
                @Override
                public ResourcePack open(String name) {
                    return new PathPackRPWrapper(Suppliers.memoize(() -> {
                        if (!PFMDataGenerator.areAssetsRunning())
                            PFMRuntimeResources.prepareAndRunAssetGen(false);
                        return PFMRuntimeResources.ASSETS_PACK;}), packResourceMetadata);
                }

                @Override
                public ResourcePack openWithOverlays(String name, ResourcePackProfile.Metadata metadata) {
                    return this.open(name);
                }
            };
            ResourcePackProfile.Metadata metadata = new ResourcePackProfile.Metadata(Text.literal("Runtime Generated Assets for PFM"), data, resource, ResourcePackCompatibility.COMPATIBLE, FeatureFlags.DEFAULT_ENABLED_FEATURES, List.of(), false);
            event.addRepositorySource(profileAdder -> {
                profileAdder.accept(ResourcePackProfile.of("pfm-asset-resources", Text.literal("PFM Assets"), true,  packFactory, metadata, ResourcePackProfile.InsertionPosition.BOTTOM, false, ResourcePackSource.NONE));
            });
        } else if (event.getPackType() == ResourceType.SERVER_DATA) {
            PackResourceMetadata packResourceMetadata = new PackResourceMetadata(Text.literal("Runtime Generated Data for PFM"), SharedConstants.getGameVersion().getResourceVersion(ResourceType.SERVER_DATA), Optional.empty());
            ResourcePackProfile.PackFactory packFactory = new ResourcePackProfile.PackFactory() {
                @Override
                public ResourcePack open(String name) {
                    return new PathPackRPWrapper(Suppliers.memoize(() -> {
                        if (!PFMDataGenerator.isDataRunning())
                            PFMRuntimeResources.prepareAndRunDataGen(false);
                        return PFMRuntimeResources.DATA_PACK;}), packResourceMetadata);
                }

                @Override
                public ResourcePack openWithOverlays(String name, ResourcePackProfile.Metadata metadata) {
                    return this.open(name);
                }
            };
            ResourcePackProfile.Metadata metadata = new ResourcePackProfile.Metadata(Text.literal("Runtime Generated Data for PFM"), data, resource, ResourcePackCompatibility.COMPATIBLE, FeatureFlags.DEFAULT_ENABLED_FEATURES, List.of(), false);
            event.addRepositorySource(profileAdder -> {
                profileAdder.accept(ResourcePackProfile.of("pfm-data-resources", Text.literal("PFM Data"), true,  packFactory, metadata, ResourcePackProfile.InsertionPosition.BOTTOM, false, ResourcePackSource.NONE));
            });
        }
    }
}
