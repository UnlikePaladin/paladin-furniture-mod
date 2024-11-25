package com.unlikepaladin.pfm.forge;

import com.google.common.base.Suppliers;
import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.client.PathPackRPWrapper;
import com.unlikepaladin.pfm.client.forge.ColorRegistryForge;
import com.unlikepaladin.pfm.config.PaladinFurnitureModConfig;
import com.unlikepaladin.pfm.data.forge.PFMTagsImpl;
import com.unlikepaladin.pfm.items.forge.PFMComponentsImpl;
import com.unlikepaladin.pfm.registry.BlockItemRegistry;
import com.unlikepaladin.pfm.registry.dynamic.forge.LateBlockRegistryForge;
import com.unlikepaladin.pfm.registry.forge.*;
import com.unlikepaladin.pfm.utilities.Version;
import net.minecraft.registry.VersionedIdentifier;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.resource.*;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import com.unlikepaladin.pfm.runtime.PFMDataGenerator;
import com.unlikepaladin.pfm.runtime.PFMRuntimeResources;
import net.minecraft.SharedConstants;
import net.minecraft.resource.metadata.PackResourceMetadata;
import net.minecraft.text.Text;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;
import java.util.List;
import java.util.Optional;


@Mod(PaladinFurnitureMod.MOD_ID)
public class PaladinFurnitureModForge extends PaladinFurnitureMod {
    public static PaladinFurnitureModConfig pfmConfig;
    public PaladinFurnitureModForge(FMLJavaModLoadingContext loadContext) {
        pfmConfig = new PaladinFurnitureModConfig(FMLPaths.CONFIGDIR.get());
        try {
            pfmConfig.initialize();
        } catch (IOException e) {
            GENERAL_LOGGER.error("Failed to initialize Paladin's Furniture configuration, default values will be used instead");
            GENERAL_LOGGER.error("", e);
        }
        this.commonInit();
        MinecraftForge.EVENT_BUS.register(EntityRegistryForge.class);
        MinecraftForge.EVENT_BUS.register(BlockItemRegistryForge.class);
        MinecraftForge.EVENT_BUS.register(StatisticsRegistryForge.class);
        MinecraftForge.EVENT_BUS.register(ScreenHandlerRegistryForge.class);
        MinecraftForge.EVENT_BUS.register(RecipeRegistryForge.class);
        MinecraftForge.EVENT_BUS.register(BlockEntityRegistryForge.class);
        MinecraftForge.EVENT_BUS.register(SoundRegistryForge.class);
        MinecraftForge.EVENT_BUS.register(NetworkRegistryForge.class);
        MinecraftForge.EVENT_BUS.register(PFMComponentsImpl.class);
        loadContext.getModEventBus().addListener(EventPriority.LOW, ColorRegistryForge::registerBlockColors);
        loadContext.getModEventBus().addListener(EventPriority.LOWEST, ColorRegistryForge::registerItemColors);
        NetworkRegistryForge.registerPackets();
        LateBlockRegistryForge.addDynamicBlockRegistration(loadContext);
        PaladinFurnitureMod.isClient = FMLEnvironment.dist == Dist.CLIENT;
        loadContext.getModEventBus().addListener(ItemGroupRegistryForge::registerItemGroups);
        loadContext.getModEventBus().addListener(ItemGroupRegistryForge::addToVanillaItemGroups);
        loadContext.getModEventBus().addListener(PaladinFurnitureModForge::generateResources);
    }

    @SubscribeEvent
    public static void generateResources(AddPackFindersEvent event) {
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
