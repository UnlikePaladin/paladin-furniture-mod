package com.unlikepaladin.pfm.client.forge;

import com.unlikepaladin.pfm.blocks.models.basicCoffeeTable.UnbakedCoffeeBasicTableModel;
import com.unlikepaladin.pfm.blocks.models.basicLamp.UnbakedBasicLampModel;
import com.unlikepaladin.pfm.blocks.models.basicTable.UnbakedBasicTableModel;
import com.unlikepaladin.pfm.blocks.models.bed.UnbakedBedModel;
import com.unlikepaladin.pfm.blocks.models.chair.UnbakedChairModel;
import com.unlikepaladin.pfm.blocks.models.chairClassic.UnbakedChairClassicModel;
import com.unlikepaladin.pfm.blocks.models.chairDinner.UnbakedChairDinnerModel;
import com.unlikepaladin.pfm.blocks.models.chairModern.UnbakedChairModernModel;
import com.unlikepaladin.pfm.blocks.models.classicCoffeeTable.UnbakedClassicCoffeeTableModel;
import com.unlikepaladin.pfm.blocks.models.classicNightstand.UnbakedClassicNightstandModel;
import com.unlikepaladin.pfm.blocks.models.classicStool.UnbakedClassicStoolModel;
import com.unlikepaladin.pfm.blocks.models.classicTable.UnbakedClassicTableModel;
import com.unlikepaladin.pfm.blocks.models.dinnerTable.UnbakedDinnerTableModel;
import com.unlikepaladin.pfm.blocks.models.fridge.UnbakedFreezerModel;
import com.unlikepaladin.pfm.blocks.models.fridge.UnbakedFridgeModel;
import com.unlikepaladin.pfm.blocks.models.fridge.UnbakedIronFridgeModel;
import com.unlikepaladin.pfm.blocks.models.kitchenCabinet.UnbakedKitchenCabinetModel;
import com.unlikepaladin.pfm.blocks.models.kitchenCounter.UnbakedKitchenCounterModel;
import com.unlikepaladin.pfm.blocks.models.kitchenCounterOven.UnbakedKitchenCounterOvenModel;
import com.unlikepaladin.pfm.blocks.models.kitchenDrawer.UnbakedKitchenDrawerModel;
import com.unlikepaladin.pfm.blocks.models.kitchenSink.UnbakedKitchenSinkModel;
import com.unlikepaladin.pfm.blocks.models.kitchenWallCounter.UnbakedKitchenWallCounterModel;
import com.unlikepaladin.pfm.blocks.models.kitchenWallDrawer.UnbakedKitchenWallDrawerModel;
import com.unlikepaladin.pfm.blocks.models.kitchenWallDrawerSmall.UnbakedKitchenWallDrawerSmallModel;
import com.unlikepaladin.pfm.blocks.models.ladder.UnbakedLadderModel;
import com.unlikepaladin.pfm.blocks.models.logStool.UnbakedLogStoolModel;
import com.unlikepaladin.pfm.blocks.models.logTable.UnbakedLogTableModel;
import com.unlikepaladin.pfm.blocks.models.mirror.UnbakedMirrorModel;
import com.unlikepaladin.pfm.blocks.models.modernCoffeeTable.UnbakedModernCoffeeTableModel;
import com.unlikepaladin.pfm.blocks.models.modernDinnerTable.UnbakedModernDinnerTableModel;
import com.unlikepaladin.pfm.blocks.models.modernStool.UnbakedModernStoolModel;
import com.unlikepaladin.pfm.blocks.models.simpleStool.UnbakedSimpleStoolModel;
import com.unlikepaladin.pfm.client.PaladinFurnitureModClient;
import com.unlikepaladin.pfm.client.ScreenRegistry;
import com.unlikepaladin.pfm.client.screens.*;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;

@Mod.EventBusSubscriber(modid = "pfm", bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class PaladinFurnitureModClientForge {

    private PaladinFurnitureModClientForge() {
    }

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        ColorRegistryForge.registerBlockRenderLayers();
        event.enqueueWork(PaladinFurnitureModClientForge::registerScreens);
        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory(
                        (client, parent) -> new PFMConfigScreen(client, parent)));
    }

    private static void registerScreens() {
        ScreenRegistry.registerScreens();
    }

    @SubscribeEvent
    public static void registerKeyBinding(RegisterKeyMappingsEvent event) {
        event.register(PaladinFurnitureModClient.USE_TOILET_KEYBIND = registerKey("key.pfm.toiletUse", "keybindings.category.pfm", GLFW.GLFW_KEY_U));
    }

    public static KeyBinding registerKey(String name, String category, int keyCode) {
        return new KeyBinding(
                name, // The translation key of the keybinding's name
                InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
                keyCode, // The keycode of the key
                category // The translation key of the keybinding's category.
        );
    }


    public static void registerExtraModels(Consumer<Identifier> event) {
        for (Identifier id : UnbakedBedModel.BED_MODEL_PARTS_BASE) {
            event.accept(id);
        }
        for (Identifier id : UnbakedBasicTableModel.BASIC_MODEL_PARTS_BASE) {
            event.accept(id);
        }
        for (Identifier id : UnbakedClassicTableModel.CLASSIC_MODEL_PARTS_BASE) {
            event.accept(id);
        }
        for (Identifier id : UnbakedLogTableModel.LOG_MODEL_PARTS_BASE) {
            event.accept(id);
        }
        for (Identifier id : UnbakedDinnerTableModel.DINNER_MODEL_PARTS_BASE) {
            event.accept(id);
        }
        for (Identifier id : UnbakedModernDinnerTableModel.MODERN_DINNER_MODEL_PARTS_BASE) {
            event.accept(id);
        }
        for (Identifier id : UnbakedClassicNightstandModel.NIGHTSTAND_MODEL_PARTS_BASE) {
            event.accept(id);
        }
        for (Identifier id : UnbakedChairModel.CHAIR_PARTS_BASE) {
            event.accept(id);
        }
        for (Identifier id : UnbakedChairDinnerModel.CHAIR_DINNER_PARTS_BASE) {
            event.accept(id);
        }
        for (Identifier id : UnbakedChairModernModel.CHAIR_MODERN_PARTS_BASE) {
            event.accept(id);
        }
        for (Identifier id : UnbakedChairClassicModel.CHAIR_CLASSIC_PARTS_BASE) {
            event.accept(id);
        }
        for (Identifier id : UnbakedSimpleStoolModel.SIMPLE_STOOL_PARTS_BASE) {
            event.accept(id);
        }
        for (Identifier id : UnbakedClassicStoolModel.CLASSIC_STOOL_PARTS_BASE) {
            event.accept(id);
        }
        for (Identifier id : UnbakedModernStoolModel.MODERN_STOOL_PARTS_BASE) {
            event.accept(id);
        }
        for (Identifier id : UnbakedLogStoolModel.LOG_STOOL_PARTS_BASE) {
            event.accept(id);
        }
        for (Identifier id : UnbakedKitchenCounterModel.COUNTER_MODEL_PARTS_BASE) {
            event.accept(id);
        }
        for (Identifier id : UnbakedKitchenDrawerModel.COUNTER_MODEL_PARTS_BASE) {
            event.accept(id);
        }
        for (Identifier id : UnbakedKitchenCabinetModel.CABINET_MODEL_PARTS_BASE) {
            event.accept(id);
        }
        for (Identifier id : UnbakedKitchenWallDrawerModel.COUNTER_MODEL_PARTS_BASE) {
            event.accept(id);
        }
        for (Identifier id : UnbakedKitchenWallCounterModel.COUNTER_MODEL_PARTS_BASE) {
            event.accept(id);
        }
        for (Identifier id : UnbakedKitchenCounterOvenModel.OVEN_MODEL_PARTS_BASE) {
            event.accept(id);
        }
        for (Identifier id : UnbakedKitchenSinkModel.SINK_MODEL_PARTS_BASE) {
            event.accept(id);
        }
        for (Identifier id : UnbakedKitchenWallDrawerSmallModel.DRAWER_MODEL_PARTS_BASE) {
            event.accept(id);
        }
        for (Identifier id : UnbakedLadderModel.LADDER_PARTS_BASE) {
            event.accept(id);
        }
        for (Identifier id : UnbakedCoffeeBasicTableModel.BASIC_MODEL_PARTS_BASE) {
            event.accept(id);
        }
        for (Identifier id : UnbakedModernCoffeeTableModel.MODERN_COFFEE_MODEL_PARTS_BASE) {
            event.accept(id);
        }
        for (Identifier id : UnbakedClassicCoffeeTableModel.CLASSIC_MODEL_PARTS_BASE) {
            event.accept(id);
        }
        UnbakedMirrorModel.ALL_MODEL_IDS.forEach(event);
        UnbakedIronFridgeModel.ALL_MODEL_IDS.forEach(event);
        UnbakedFridgeModel.ALL_MODEL_IDS.forEach(event);
        UnbakedFreezerModel.ALL_MODEL_IDS.forEach(event);
        UnbakedBasicLampModel.ALL_MODEL_IDS.forEach(event);
    }
}