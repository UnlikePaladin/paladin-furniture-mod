package com.unlikepaladin.pfm.entity.model;

import com.google.common.collect.ImmutableList;
import com.unlikepaladin.pfm.entity.ChairEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.state.EntityRenderState;

public class ModelEmpty extends EntityModel<EntityRenderState> {
    private final ModelPart base;

    public ModelEmpty(ModelPart modelPart) {
        super(modelPart);
        this.base = modelPart.getChild(EntityModelPartNames.CUBE);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild(EntityModelPartNames.CUBE, ModelPartBuilder.create().uv(0, 0).cuboid(0F, 0F, 0F, 0F, 0F, 0F), ModelTransform.pivot(0F, 0F, 0F));
        return TexturedModelData.of(modelData, 16, 16);
    }

}