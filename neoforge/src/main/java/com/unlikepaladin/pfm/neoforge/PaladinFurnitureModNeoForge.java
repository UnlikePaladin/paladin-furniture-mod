package com.unlikepaladin.pfm.neoforge;

import com.google.common.base.Suppliers;
import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.client.PathPackRPWrapper;
import com.unlikepaladin.pfm.client.neoforge.ColorRegistryNeoForge;
import com.unlikepaladin.pfm.config.PaladinFurnitureModConfig;
import com.unlikepaladin.pfm.registry.dynamic.neoforge.LateBlockRegistryNeoForge;
import com.unlikepaladin.pfm.registry.neoforge.*;
import com.unlikepaladin.pfm.utilities.Version;
import net.minecraft.registry.VersionedIdentifier;
import net.minecraft.resource.*;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import com.unlikepaladin.pfm.runtime.PFMDataGenerator;
import com.unlikepaladin.pfm.runtime.PFMRuntimeResources;
import net.minecraft.SharedConstants;
import net.minecraft.resource.metadata.PackResourceMetadata;
import net.minecraft.text.Text;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
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
    public PaladinFurnitureModNeoForge(IEventBus modEventBus) {
        pfmConfig = new PaladinFurnitureModConfig(FMLPaths.CONFIGDIR.get());
        try {
            pfmConfig.initialize();
        } catch (IOException e) {
            GENERAL_LOGGER.error("Failed to initialize Paladin's Furniture configuration, default values will be used instead");
            GENERAL_LOGGER.error("", e);
        }
        this.commonInit();
        modEventBus.register(EntityRegistryNeoForge.class);
        modEventBus.register(BlockItemRegistryNeoForge.class);
        modEventBus.register(StatisticsRegistryNeoForge.class);
        modEventBus.register(ScreenHandlerRegistryNeoForge.class);
        modEventBus.register(RecipeRegistryNeoForge.class);
        modEventBus.register(BlockEntityRegistryNeoForge.class);
        modEventBus.register(SoundRegistryNeoForge.class);
        modEventBus.addListener(NetworkRegistryNeoForge::register);
        modEventBus.addListener(EventPriority.LOW, ColorRegistryNeoForge::registerBlockColors);
        modEventBus.addListener(EventPriority.LOWEST, ColorRegistryNeoForge::registerItemColors);
        LateBlockRegistryNeoForge.addDynamicBlockRegistration(modEventBus);
        PaladinFurnitureMod.isClient = FMLEnvironment.dist == Dist.CLIENT;
        NeoForge.EVENT_BUS.addListener(NetworkRegistryNeoForge::onServerJoin);
        modEventBus.addListener(ItemGroupRegistryNeoForge::registerItemGroups);
        modEventBus.addListener(ItemGroupRegistryNeoForge::addToVanillaItemGroups);
        modEventBus.addListener(PaladinFurnitureModNeoForge::generateResources);
    }

    @SubscribeEvent
    public static void generateResources(AddPackFindersEvent event) {
        int data = SharedConstants.getGameVersion().getResourceVersion(ResourceType.SERVER_DATA);
        int resource = SharedConstants.getGameVersion().getResourceVersion(ResourceType.CLIENT_RESOURCES);
        if (event.getPackType() == ResourceType.CLIENT_RESOURCES) {
            PackResourceMetadata packResourceMetadata = new PackResourceMetadata(Text.literal("Runtime Generated Assets for PFM"), SharedConstants.getGameVersion().getResourceVersion(ResourceType.CLIENT_RESOURCES), Optional.empty());
            ResourcePackProfile.PackFactory packFactory = new ResourcePackProfile.PackFactory() {
                @Override
                public ResourcePack open(ResourcePackInfo info) {
                    return new PathPackRPWrapper(Suppliers.memoize(() -> {
                        if (!PFMDataGenerator.areAssetsRunning())
                            PFMRuntimeResources.prepareAndRunAssetGen(false);
                        return PFMRuntimeResources.getAssetsPack(info);}), packResourceMetadata, info);
                }

                @Override
                public ResourcePack openWithOverlays(ResourcePackInfo info, ResourcePackProfile.Metadata metadata) {
                    return this.open(info);
                }
            };
            event.addRepositorySource(profileAdder -> {
                profileAdder.accept(ResourcePackProfile.create(new ResourcePackInfo("pfm-asset-resources", Text.literal("PFM Assets"), ResourcePackSource.NONE, Optional.of(new VersionedIdentifier(PaladinFurnitureMod.MOD_ID, "pfm_assets", Version.getCurrentVersion()))),  packFactory, ResourceType.CLIENT_RESOURCES, new ResourcePackPosition(true, ResourcePackProfile.InsertionPosition.BOTTOM, false)));
            });
        } else if (event.getPackType() == ResourceType.SERVER_DATA) {
            PackResourceMetadata packResourceMetadata = new PackResourceMetadata(Text.literal("Runtime Generated Data for PFM"), SharedConstants.getGameVersion().getResourceVersion(ResourceType.SERVER_DATA), Optional.empty());
            ResourcePackProfile.PackFactory packFactory = new ResourcePackProfile.PackFactory() {
                @Override
                public ResourcePack open(ResourcePackInfo info) {
                    return new PathPackRPWrapper(Suppliers.memoize(() -> {
                        if (!PFMDataGenerator.isDataRunning())
                            PFMRuntimeResources.prepareAndRunDataGen(false);
                        return PFMRuntimeResources.getDataPack(info);}), packResourceMetadata, info);
                }

                @Override
                public ResourcePack openWithOverlays(ResourcePackInfo info, ResourcePackProfile.Metadata metadata) {
                    return this.open(info);
                }
            };
            event.addRepositorySource(profileAdder -> {
                profileAdder.accept(ResourcePackProfile.create(new ResourcePackInfo("pfm-data-resources", Text.literal("PFM Data"), ResourcePackSource.NONE, Optional.of(new VersionedIdentifier(PaladinFurnitureMod.MOD_ID, "pfm_data", Version.getCurrentVersion()))),  packFactory, ResourceType.SERVER_DATA, new ResourcePackPosition(true, ResourcePackProfile.InsertionPosition.BOTTOM, false)));
            });
        }
    }
}
