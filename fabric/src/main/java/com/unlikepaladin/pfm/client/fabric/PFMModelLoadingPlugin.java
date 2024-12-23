package com.unlikepaladin.pfm.client.fabric;

import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.blocks.models.ModelHelper;
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
import com.unlikepaladin.pfm.client.model.PFMItemModel;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelModifier;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.item.model.ItemModelTypes;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.GroupableModel;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PFMModelLoadingPlugin implements ModelLoadingPlugin {
    @Override
    public void initialize(Context pluginContext) {
        pluginContext.modifyModelOnLoad().register((oldModel, context) -> {
            UnbakedModel model = loadModelResource(context.id());
            if (model != null)
                return model;
            return oldModel;
        });
        pluginContext.addModels(provideExtraModels());
    }

    public @Nullable UnbakedModel loadModelResource(Identifier id) {
        Identifier resourceId = id;
        if (id.getPath().contains("item")) {
            resourceId = id;
        }
        if (ModelHelper.containsIdentifier(UnbakedMirrorModel.MIRROR_MODEL_IDS, resourceId)){
            return new UnbakedMirrorModel(UnbakedMirrorModel.DEFAULT_TEXTURES[2], ModelHelper.getVanillaConcreteColor(resourceId), UnbakedMirrorModel.DEFAULT_TEXTURES[1], new ArrayList<>(), ModelHelper.getColor(resourceId));
        } else if (UnbakedBedModel.BED_MODEL_IDS.contains(resourceId)){
            return new UnbakedBedModel();
        }
        else if (UnbakedBasicTableModel.MODEL_IDS.contains(resourceId)){
            return new UnbakedBasicTableModel();
        }
        else if (UnbakedClassicTableModel.MODEL_IDS.contains(resourceId)){
            return new UnbakedClassicTableModel();
        }
        else if (UnbakedLogTableModel.TABLE_MODEL_IDS.contains(resourceId)){
            return new UnbakedLogTableModel();
        }
        else if (UnbakedDinnerTableModel.TABLE_MODEL_IDS.contains(resourceId)){
            return new UnbakedDinnerTableModel();
        }
        else if (UnbakedModernDinnerTableModel.TABLE_MODEL_IDS.contains(resourceId)){
            return new UnbakedModernDinnerTableModel();
        }
        else if (UnbakedClassicNightstandModel.NIGHSTAND_MODEL_IDS.contains(resourceId)){
            return new UnbakedClassicNightstandModel();
        }
        else if (UnbakedChairModel.CHAIR_MODEL_IDS.contains(resourceId)){
            return new UnbakedChairModel();
        }
        else if (UnbakedChairDinnerModel.CHAIR_DINNER_MODEL_IDS.contains(resourceId)){
            return new UnbakedChairDinnerModel();
        }
        else if (UnbakedChairModernModel.CHAIR_MODERN_MODEL_IDS.contains(resourceId)){
            return new UnbakedChairModernModel();
        }
        else if (UnbakedChairClassicModel.CHAIR_CLASSIC_MODEL_IDS.contains(resourceId)){
            return new UnbakedChairClassicModel();
        }
        else if (UnbakedSimpleStoolModel.SIMPLE_STOOL_MODEL_IDS.contains(resourceId)){
            return new UnbakedSimpleStoolModel();
        }
        else if (UnbakedClassicStoolModel.CLASSIC_STOOL_MODEL_IDS.contains(resourceId)){
            return new UnbakedClassicStoolModel();
        }
        else if (UnbakedModernStoolModel.MODERN_STOOL_MODEL_IDS.contains(resourceId)){
            return new UnbakedModernStoolModel();
        }
        else if (UnbakedLogStoolModel.LOG_STOOL_MODEL_IDS.contains(resourceId)){
            return new UnbakedLogStoolModel();
        }
        else if (UnbakedKitchenCounterModel.COUNTER_MODEL_IDS.contains(resourceId)){
            return new UnbakedKitchenCounterModel();
        }
        else if (UnbakedKitchenDrawerModel.DRAWER_MODEL_IDS.contains(resourceId)){
            return new UnbakedKitchenDrawerModel();
        }
        else if (UnbakedKitchenWallCounterModel.COUNTER_MODEL_IDS.contains(resourceId)){
            return new UnbakedKitchenWallCounterModel();
        }
        else if (UnbakedKitchenWallDrawerModel.DRAWER_MODEL_IDS.contains(resourceId)){
            return new UnbakedKitchenWallDrawerModel();
        }
        else if (UnbakedKitchenCabinetModel.CABINET_MODEL_IDS.contains(resourceId)){
            return new UnbakedKitchenCabinetModel();
        }
        else if (UnbakedKitchenCounterOvenModel.OVEN_MODEL_IDS.contains(resourceId)){
            return new UnbakedKitchenCounterOvenModel();
        }
        else if (UnbakedKitchenSinkModel.SINK_MODEL_IDS.contains(resourceId)){
            return new UnbakedKitchenSinkModel();
        }
        else if (UnbakedKitchenWallDrawerSmallModel.DRAWER_MODEL_IDS.contains(resourceId)){
            return new UnbakedKitchenWallDrawerSmallModel();
        }
        else if (UnbakedIronFridgeModel.IRON_FRIDGE_MODEL_IDS.contains(resourceId)){
            return new UnbakedIronFridgeModel();
        }
        else if (UnbakedFridgeModel.FRIDGE_MODEL_IDS.contains(resourceId)){
            return new UnbakedFridgeModel(resourceId);
        }
        else if (UnbakedFreezerModel.FREEZER_MODEL_IDS.contains(resourceId)){
            return new UnbakedFreezerModel(resourceId);
        }
        else if (UnbakedBasicLampModel.LAMP_MODEL_IDS.contains(resourceId)){
            return new UnbakedBasicLampModel();
        }
        else if (UnbakedLadderModel.LADDER_MODEL_IDS.contains(resourceId)){
            return new UnbakedLadderModel();
        }
        else if (UnbakedCoffeeBasicTableModel.MODEL_IDS.contains(resourceId)){
            return new UnbakedCoffeeBasicTableModel();
        }
        else if (UnbakedModernCoffeeTableModel.TABLE_MODEL_IDS.contains(resourceId)){
            return new UnbakedModernCoffeeTableModel();
        }
        else if (UnbakedClassicCoffeeTableModel.MODEL_IDS.contains(resourceId)){
            return new UnbakedClassicCoffeeTableModel();
        }
        else
            return null;
    }
    
    public List<Identifier> provideExtraModels() {
        List<Identifier> out = new ArrayList<>();
        for (Identifier id : UnbakedBedModel.BED_MODEL_PARTS_BASE) {
            out.add(id);
        }
        for (Identifier id : UnbakedBasicTableModel.BASIC_MODEL_PARTS_BASE) {
            out.add(id);
        }
        for (Identifier id : UnbakedClassicTableModel.CLASSIC_MODEL_PARTS_BASE) {
            out.add(id);
        }
        for (Identifier id : UnbakedLogTableModel.LOG_MODEL_PARTS_BASE) {
            out.add(id);
        }
        for (Identifier id : UnbakedDinnerTableModel.DINNER_MODEL_PARTS_BASE) {
            out.add(id);
        }
        for (Identifier id : UnbakedModernDinnerTableModel.MODERN_DINNER_MODEL_PARTS_BASE) {
            out.add(id);
        }
        for (Identifier id : UnbakedClassicNightstandModel.NIGHTSTAND_MODEL_PARTS_BASE) {
            out.add(id);
        }
        for (Identifier id : UnbakedChairModel.CHAIR_PARTS_BASE) {
            out.add(id);
        }
        for (Identifier id : UnbakedChairDinnerModel.CHAIR_DINNER_PARTS_BASE) {
            out.add(id);
        }
        for (Identifier id : UnbakedChairModernModel.CHAIR_MODERN_PARTS_BASE) {
            out.add(id);
        }
        for (Identifier id : UnbakedChairClassicModel.CHAIR_CLASSIC_PARTS_BASE) {
            out.add(id);
        }
        for (Identifier id : UnbakedSimpleStoolModel.SIMPLE_STOOL_PARTS_BASE) {
            out.add(id);
        }
        for (Identifier id : UnbakedClassicStoolModel.CLASSIC_STOOL_PARTS_BASE) {
            out.add(id);
        }
        for (Identifier id : UnbakedModernStoolModel.MODERN_STOOL_PARTS_BASE) {
            out.add(id);
        }
        for (Identifier id : UnbakedLogStoolModel.LOG_STOOL_PARTS_BASE) {
            out.add(id);
        }
        for (Identifier id : UnbakedKitchenCounterModel.COUNTER_MODEL_PARTS_BASE) {
            out.add(id);
        }
        for (Identifier id : UnbakedKitchenDrawerModel.COUNTER_MODEL_PARTS_BASE) {
            out.add(id);
        }
        for (Identifier id : UnbakedKitchenCabinetModel.CABINET_MODEL_PARTS_BASE) {
            out.add(id);
        }
        for (Identifier id : UnbakedKitchenWallDrawerModel.COUNTER_MODEL_PARTS_BASE) {
            out.add(id);
        }
        for (Identifier id : UnbakedKitchenWallCounterModel.COUNTER_MODEL_PARTS_BASE) {
            out.add(id);
        }
        for (Identifier id : UnbakedKitchenCounterOvenModel.OVEN_MODEL_PARTS_BASE) {
            out.add(id);
        }
        for (Identifier id : UnbakedKitchenSinkModel.SINK_MODEL_PARTS_BASE) {
            out.add(id);
        }
        for (Identifier id : UnbakedKitchenWallDrawerSmallModel.DRAWER_MODEL_PARTS_BASE) {
            out.add(id);
        }
        for (Identifier id : UnbakedLadderModel.LADDER_PARTS_BASE) {
            out.add(id);
        }
        for (Identifier id : UnbakedCoffeeBasicTableModel.BASIC_MODEL_PARTS_BASE) {
            out.add(id);
        }
        for (Identifier id : UnbakedModernCoffeeTableModel.MODERN_COFFEE_MODEL_PARTS_BASE) {
            out.add(id);
        }
        for (Identifier id : UnbakedClassicCoffeeTableModel.CLASSIC_MODEL_PARTS_BASE) {
            out.add(id);
        }
        out.addAll(UnbakedMirrorModel.ALL_MODEL_IDS);
        out.addAll(UnbakedIronFridgeModel.ALL_MODEL_IDS);
        out.addAll(UnbakedFridgeModel.ALL_MODEL_IDS);
        out.addAll(UnbakedFreezerModel.ALL_MODEL_IDS);
        out.addAll(UnbakedBasicLampModel.ALL_MODEL_IDS);
        return out;
    }

}
