package com.unlikepaladin.pfm.entity.render;

import com.unlikepaladin.pfm.entity.ChairEntity;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.util.Identifier;

public class ChairEntityRenderer extends EntityRenderer<ChairEntity, EntityRenderState> {
    private static final Identifier EMPTY_TEXTURE = Identifier.of("minecraft:textures/block/stone.png");
    public ChairEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public EntityRenderState createRenderState() {
        return new EntityRenderState();
    }
}
