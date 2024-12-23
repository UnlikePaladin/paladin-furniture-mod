package com.unlikepaladin.pfm.client.fabric;

import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.blocks.DyeableFurnitureBlock;
import com.unlikepaladin.pfm.blocks.blockentities.DyeableFurnitureBlockEntity;
import com.unlikepaladin.pfm.client.PaladinFurnitureModClient;
import com.unlikepaladin.pfm.client.ScreenRegistry;
import com.unlikepaladin.pfm.client.fabric.modelLoaders.PFMModelLoadingV1;
import com.unlikepaladin.pfm.client.model.PFMBedModelRenderer;
import com.unlikepaladin.pfm.client.model.PFMItemModel;
import com.unlikepaladin.pfm.client.screens.PFMConfigScreen;
import com.unlikepaladin.pfm.config.option.Side;
import com.unlikepaladin.pfm.fabric.PaladinFurnitureModFabric;
import com.unlikepaladin.pfm.networking.MicrowaveUpdatePayload;
import com.unlikepaladin.pfm.networking.SyncConfigPayload;
import com.unlikepaladin.pfm.networking.fabric.LeaveEventHandlerFabric;
import com.unlikepaladin.pfm.registry.NetworkIDs;
import com.unlikepaladin.pfm.registry.PaladinFurnitureModBlocksItems;
import com.unlikepaladin.pfm.registry.fabric.NetworkRegistryFabric;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.SpecialBlockRendererRegistry;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.item.model.ItemModelTypes;
import net.minecraft.client.render.item.model.special.SpecialModelTypes;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class PaladinFurnitureModClientFabric implements ClientModInitializer {
    public static final Logger CLIENT_LOGGER = LogManager.getLogger();

    @Override
    public void onInitializeClient() {
        PaladinFurnitureMod.isClient = true;
        PaladinFurnitureModFabric.registerLateEntries();
        PaladinFurnitureModFabric.replaceHomePOIStates();
        ColorRegistryFabric.registerAll();
        ClientPacketRegistry.registerClientPackets();
        registerModels();

        PaladinFurnitureModClient.USE_TOILET_KEYBIND = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.pfm.toiletUse", // The translation key of the keybinding's name
                InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
                GLFW.GLFW_KEY_U, // The keycode of the key
                "keybindings.category.pfm" // The translation key of the keybinding's category.
        ));
        EntityRenderRegistryFabric.registerRender();

        ScreenRegistry.registerScreens();
        if (FabricLoader.getInstance().isModLoaded("fabric-model-loading-api-v1")) {
            PFMModelLoadingV1.registerV1Plugin();
        }
        ParticleProviderRegistryFabric.registerParticleFactories();
        ClientPlayConnectionEvents.DISCONNECT.register(LeaveEventHandlerFabric::onServerLeave);
    }

    public static void registerModels() {
        ItemModelTypes.ID_MAPPER.put(Identifier.of(PaladinFurnitureMod.MOD_ID, "furniture_model"), PFMItemModel.Unbaked.CODEC);
        SpecialModelTypes.ID_MAPPER.put(Identifier.of(PaladinFurnitureMod.MOD_ID, "pfm_bed"), PFMBedModelRenderer.Unbaked.CODEC);
        for (Block block : PaladinFurnitureModBlocksItems.getBeds()) {
            if (block instanceof DyeableFurnitureBlock)
                SpecialBlockRendererRegistry.register(block, new PFMBedModelRenderer.Unbaked(((DyeableFurnitureBlock) block).getPFMColor()));
        }
    }

}