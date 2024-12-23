package com.unlikepaladin.pfm.mixin.neoforge;


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
import net.minecraft.client.render.model.ReferencedModelsCollector;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.io.IOException;
import java.util.ArrayList;

@Mixin(ReferencedModelsCollector.class)
public abstract class PFMReferencedModelsCollectorMixin {

    @ModifyVariable(method = "getModel", at = @At(value = "STORE", ordinal = 0), ordinal = 0)
    private UnbakedModel pfm$loadModels(UnbakedModel olModel, Identifier olId) {
        Identifier resourceId = olId;
        if (ModelHelper.containsIdentifier(UnbakedMirrorModel.MIRROR_MODEL_IDS, resourceId)){
            return new UnbakedMirrorModel(UnbakedMirrorModel.DEFAULT_TEXTURES[2], ModelHelper.getVanillaConcreteColor(resourceId), UnbakedMirrorModel.DEFAULT_TEXTURES[1], new ArrayList<>(), ModelHelper.getColor(resourceId));
        } else if (UnbakedBedModel.BED_MODEL_IDS.contains(resourceId)){
            UnbakedModel model = new UnbakedBedModel();
            return model;
        }
        else if (UnbakedBasicTableModel.MODEL_IDS.contains(resourceId)){
            UnbakedModel model = new UnbakedBasicTableModel();
            return model;
        }
        else if (UnbakedClassicTableModel.MODEL_IDS.contains(resourceId)){
            UnbakedModel model = new UnbakedClassicTableModel();
            return model;
        }
        else if (UnbakedLogTableModel.TABLE_MODEL_IDS.contains(resourceId)){
            UnbakedModel model = new UnbakedLogTableModel();
            return model;
        }
        else if (UnbakedDinnerTableModel.TABLE_MODEL_IDS.contains(resourceId)){
            UnbakedModel model = new UnbakedDinnerTableModel();
            return model;
        }
        else if (UnbakedModernDinnerTableModel.TABLE_MODEL_IDS.contains(resourceId)){
            UnbakedModel model = new UnbakedModernDinnerTableModel();
            return model;
        }
        else if (UnbakedKitchenCounterModel.COUNTER_MODEL_IDS.contains(resourceId)){
            UnbakedModel model = new UnbakedKitchenCounterModel();
            return model;
        }
        else if (UnbakedKitchenDrawerModel.DRAWER_MODEL_IDS.contains(resourceId)){
            UnbakedModel model = new UnbakedKitchenDrawerModel();
            return model;
        }
        else if (UnbakedKitchenWallCounterModel.COUNTER_MODEL_IDS.contains(resourceId)){
            UnbakedModel model = new UnbakedKitchenWallCounterModel();
            return model;
        }
        else if (UnbakedKitchenWallDrawerModel.DRAWER_MODEL_IDS.contains(resourceId)){
            UnbakedModel model = new UnbakedKitchenWallDrawerModel();
            return model;
        }
        else if (UnbakedKitchenCabinetModel.CABINET_MODEL_IDS.contains(resourceId)){
            UnbakedModel model = new UnbakedKitchenCabinetModel();
            return model;
        }
        else if (UnbakedClassicNightstandModel.NIGHSTAND_MODEL_IDS.contains(resourceId)){
            UnbakedModel model = new UnbakedClassicNightstandModel();
            return model;
        }
        else if (UnbakedKitchenCounterOvenModel.OVEN_MODEL_IDS.contains(resourceId)){
            UnbakedModel model = new UnbakedKitchenCounterOvenModel();
            return model;
        }
        else if (UnbakedKitchenSinkModel.SINK_MODEL_IDS.contains(resourceId)){
            UnbakedModel model = new UnbakedKitchenSinkModel();
            return model;
        }
        else if (UnbakedKitchenWallDrawerSmallModel.DRAWER_MODEL_IDS.contains(resourceId)){
            UnbakedModel model = new UnbakedKitchenWallDrawerSmallModel();
            return model;
        }
        else if (ModelHelper.containsIdentifier(UnbakedIronFridgeModel.IRON_FRIDGE_MODEL_IDS.toArray(new Identifier[0]), resourceId)){
            UnbakedModel model = new UnbakedIronFridgeModel();
            return model;
        }
        else if (ModelHelper.containsIdentifier(UnbakedFridgeModel.FRIDGE_MODEL_IDS.toArray(new Identifier[0]), resourceId)){
            UnbakedModel model = new UnbakedFridgeModel(resourceId);
            return model;
        }
        else if (ModelHelper.containsIdentifier(UnbakedFreezerModel.FREEZER_MODEL_IDS.toArray(new Identifier[0]), resourceId)){
            UnbakedModel model = new UnbakedFreezerModel(resourceId);
            return model;
        }
        else if (UnbakedBasicLampModel.LAMP_MODEL_IDS.contains(resourceId)){
            UnbakedModel model = new UnbakedBasicLampModel();
            return model;
        }
        else if (UnbakedChairModel.CHAIR_MODEL_IDS.contains(resourceId)){
            UnbakedModel model = new UnbakedChairModel();
            return model;
        }
        else if (UnbakedChairDinnerModel.CHAIR_DINNER_MODEL_IDS.contains(resourceId)){
            UnbakedModel model = new UnbakedChairDinnerModel();
            return model;
        }
        else if (UnbakedChairModernModel.CHAIR_MODERN_MODEL_IDS.contains(resourceId)){
            UnbakedModel model = new UnbakedChairModernModel();
            return model;
        }
        else if (UnbakedChairClassicModel.CHAIR_CLASSIC_MODEL_IDS.contains(resourceId)){
            UnbakedModel model = new UnbakedChairClassicModel();
            return model;
        }
        else if (UnbakedSimpleStoolModel.SIMPLE_STOOL_MODEL_IDS.contains(resourceId)){
            UnbakedModel model = new UnbakedSimpleStoolModel();
            return model;
        }
        else if (UnbakedClassicStoolModel.CLASSIC_STOOL_MODEL_IDS.contains(resourceId)){
            UnbakedModel model = new UnbakedClassicStoolModel();
            return model;
        }
        else if (UnbakedModernStoolModel.MODERN_STOOL_MODEL_IDS.contains(resourceId)){
            UnbakedModel model = new UnbakedModernStoolModel();
            return model;
        }
        else if (UnbakedLogStoolModel.LOG_STOOL_MODEL_IDS.contains(resourceId)){
            UnbakedModel model = new UnbakedLogStoolModel();
            return model;
        }
        else if (UnbakedLadderModel.LADDER_MODEL_IDS.contains(resourceId)){
            UnbakedModel model = new UnbakedLadderModel();
            return model;
        }
        else if (UnbakedCoffeeBasicTableModel.MODEL_IDS.contains(resourceId)){
            UnbakedModel model = new UnbakedCoffeeBasicTableModel();
            return model;
        }
        else if (UnbakedModernCoffeeTableModel.TABLE_MODEL_IDS.contains(resourceId)){
            UnbakedModel model = new UnbakedModernCoffeeTableModel();
            return model;
        }
        else if (UnbakedClassicCoffeeTableModel.MODEL_IDS.contains(resourceId)){
            UnbakedModel model = new UnbakedClassicCoffeeTableModel();
            return model;
        }
        return olModel;
    }
}