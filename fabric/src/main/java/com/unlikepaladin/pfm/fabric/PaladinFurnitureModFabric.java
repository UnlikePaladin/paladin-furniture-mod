package com.unlikepaladin.pfm.fabric;

import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.advancements.PFMCriteria;
import com.unlikepaladin.pfm.advancements.fabric.CriteriaRegistryFabric;
import com.unlikepaladin.pfm.blocks.BasicChairBlock;
import com.unlikepaladin.pfm.compat.cookingforblockheads.fabric.PFMCookingForBlockHeadsCompat;
import com.unlikepaladin.pfm.config.PaladinFurnitureModConfig;
import com.unlikepaladin.pfm.config.option.AbstractConfigOption;
import com.unlikepaladin.pfm.data.materials.DynamicBlockRegistry;
import com.unlikepaladin.pfm.data.materials.WoodVariantRegistry;
import com.unlikepaladin.pfm.items.PFMComponents;
import com.unlikepaladin.pfm.networking.SyncConfigPayload;
import com.unlikepaladin.pfm.registry.*;
import com.unlikepaladin.pfm.registry.dynamic.LateBlockRegistry;
import com.unlikepaladin.pfm.registry.fabric.*;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.registry.*;
import net.minecraft.registry.Registry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

public class PaladinFurnitureModFabric extends PaladinFurnitureMod implements ModInitializer, DedicatedServerModInitializer {

    public static final Identifier FURNITURE_DYED_ID = Identifier.of("pfm:furniture_dyed");
    public static SoundEvent FURNITURE_DYED_EVENT = SoundEvent.of(FURNITURE_DYED_ID);
    public static final Logger GENERAL_LOGGER = LogManager.getLogger();


    public static PaladinFurnitureModConfig pfmConfig;
    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.
        pfmConfig = new PaladinFurnitureModConfig(FabricLoader.getInstance().getConfigDir());
        try {
            pfmConfig.initialize();
        } catch (IOException e) {
            GENERAL_LOGGER.error("Failed to initialize Paladin's Furniture configuration, default values will be used instead");
            GENERAL_LOGGER.error("", e);
        }
        this.commonInit();

        EntityRegistryFabric.registerEntities();
        PaladinFurnitureModFabric.initializeItemGroup();
        PFMComponents.registerComponents();
        BlockItemRegistryFabric.registerItems();
        BlockItemRegistryFabric.registerBlocks();
        // PFMRuntimeResources.prepareAsyncResourceGen(); No async gen because Forge won't behave, blame it.
        StatisticsRegistryFabric.registerStatistics();
        SoundRegistryFabric.registerSounds();
        NetworkRegistryFabric.registerPackets();
        ScreenHandlerRegistryFabric.registerScreenHandlers();
        RecipeRegistryFabric.registerRecipes();
        ParticleTypeRegistryFabric.registerParticleTypes();
        CriteriaRegistryFabric.registerCriteria();
        ServerPlayConnectionEvents.JOIN.register(PaladinFurnitureModFabric::onServerJoin);
    }


    public static void onServerJoin(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {
        // Give book
        if (getPFMConfig().shouldGiveGuideBook()) {
            PFMCriteria.GUIDE_BOOK_CRITERION.trigger(handler.getPlayer());
        }

        // Sync Config
        RegistryByteBuf buffer = new RegistryByteBuf(Unpooled.buffer(), server.getRegistryManager());
        Collection<AbstractConfigOption> configOptions = PaladinFurnitureMod.getPFMConfig().options.values();
        buffer.writeCollection(configOptions, AbstractConfigOption::writeConfigOption);
        sender.sendPacket(new SyncConfigPayload(buffer));
    }
//Identifier.of(MOD_ID, "dye_kits")
    public static void initializeItemGroup() {
        PaladinFurnitureMod.DYE_KITS.setRight(Registry.register(Registries.ITEM_GROUP, Identifier.of(MOD_ID, "dye_kits"), FabricItemGroup.builder()
                .displayName(Text.translatable("itemGroup.pfm.dye_kits"))
                .icon(() -> new ItemStack(PaladinFurnitureModBlocksItems.DYE_KIT_RED))
                .entries((enabledFeatures, stacks) -> {
                    stacks.add(new ItemStack(PaladinFurnitureModBlocksItems.DYE_KIT_RED));
                    stacks.add(new ItemStack(PaladinFurnitureModBlocksItems.DYE_KIT_ORANGE));
                    stacks.add(new ItemStack(PaladinFurnitureModBlocksItems.DYE_KIT_YELLOW));
                    stacks.add(new ItemStack(PaladinFurnitureModBlocksItems.DYE_KIT_GREEN));
                    stacks.add(new ItemStack(PaladinFurnitureModBlocksItems.DYE_KIT_LIME));
                    stacks.add(new ItemStack(PaladinFurnitureModBlocksItems.DYE_KIT_CYAN));
                    stacks.add(new ItemStack(PaladinFurnitureModBlocksItems.DYE_KIT_BLUE));
                    stacks.add(new ItemStack(PaladinFurnitureModBlocksItems.DYE_KIT_LIGHT_BLUE));
                    stacks.add(new ItemStack(PaladinFurnitureModBlocksItems.DYE_KIT_PURPLE));
                    stacks.add(new ItemStack(PaladinFurnitureModBlocksItems.DYE_KIT_MAGENTA));
                    stacks.add(new ItemStack(PaladinFurnitureModBlocksItems.DYE_KIT_PINK));
                    stacks.add(new ItemStack(PaladinFurnitureModBlocksItems.DYE_KIT_BROWN));
                    stacks.add(new ItemStack(PaladinFurnitureModBlocksItems.DYE_KIT_WHITE));
                    stacks.add(new ItemStack(PaladinFurnitureModBlocksItems.DYE_KIT_GRAY));
                    stacks.add(new ItemStack(PaladinFurnitureModBlocksItems.DYE_KIT_LIGHT_GRAY));
                    stacks.add(new ItemStack(PaladinFurnitureModBlocksItems.DYE_KIT_BLACK));
                })
                .build()));

        PaladinFurnitureMod.FURNITURE_GROUP.setRight(Registry.register(Registries.ITEM_GROUP, Identifier.of(MOD_ID, "furniture"), FabricItemGroup.builder()
                .displayName(Text.translatable("itemGroup.pfm.furniture"))
                .icon(() -> PaladinFurnitureMod.furnitureEntryMap.get(BasicChairBlock.class).getVariantToBlockMap().get(WoodVariantRegistry.OAK).asItem().getDefaultStack())
                .entries((displayContext, stacks) -> {

                        }
                ).build()));
    }

    @Override
    public void onInitializeServer() {
        PaladinFurnitureMod.isClient = false;
        registerLateEntries();
        replaceHomePOIStates();
    }

    public static void registerLateEntries() {
        DynamicBlockRegistry.initialize();
        try {
            LateBlockRegistry.registerBlocks();
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        BlockEntityRegistry.registerBlockEntities();
    }
}
