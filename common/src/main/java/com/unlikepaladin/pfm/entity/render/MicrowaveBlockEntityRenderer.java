package com.unlikepaladin.pfm.entity.render;


import com.unlikepaladin.pfm.blocks.blockentities.MicrowaveBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeDisplayEntry;
import net.minecraft.recipe.RecipePropertySet;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;

public class MicrowaveBlockEntityRenderer<T extends MicrowaveBlockEntity> implements BlockEntityRenderer<T> {
    public ItemStack itemStack;
    private final ItemRenderer itemRenderer;
    private RecipePropertySet recipePropertySet;

    public MicrowaveBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        itemRenderer = ctx.getItemRenderer();
    }

    @Override
    public void render(T blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        itemStack = blockEntity.getStack(0);
        matrices.push();
        if (recipePropertySet == null)
            recipePropertySet = blockEntity.getWorld().getRecipeManager().getPropertySet(RecipePropertySet.SMOKER_INPUT);

        int lightAbove = WorldRenderer.getLightmapCoordinates(blockEntity.getWorld(), blockEntity.getPos().up());
        Direction facing = blockEntity.getFacing();
        float x,y,z;
        switch (facing) {
            case NORTH -> {
                x = 0.625f;
                y = 0.25f;
                z = 0.57f;
            }
            case SOUTH -> {
                x = 0.375f;
                y = 0.25f;
                z = 0.43f;
            }
            case WEST -> {
                x = 0.57f;
                y = 0.25f;
                z = 0.375f;
            }
            case EAST -> {
                x = 0.43f;
                y = 0.25f;
                z = 0.625f;
            }
            default -> throw new IllegalStateException("Unexpected value: " + facing);
        }
        matrices.translate(x, y ,z);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-facing.asRotation()));
        if (blockEntity.isActive && recipePropertySet.canUse(itemStack)) {
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((blockEntity.getWorld().getTime() + tickDelta) * 4));}
        matrices.scale(0.5f, 0.5f, 0.5f);
        this.itemRenderer.renderItem(itemStack, ModelTransformationMode.GROUND, lightAbove, overlay, matrices, vertexConsumers, blockEntity.getWorld(), 0);
        matrices.pop();
    }



}
