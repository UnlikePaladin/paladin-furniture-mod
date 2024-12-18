package com.unlikepaladin.pfm.client.neoforge;

import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.blocks.models.basicLamp.UnbakedBasicLampModel;
import com.unlikepaladin.pfm.data.materials.WoodVariant;
import com.unlikepaladin.pfm.data.materials.WoodVariantRegistry;
import com.unlikepaladin.pfm.items.PFMComponents;
import com.unlikepaladin.pfm.registry.PaladinFurnitureModBlocksItems;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.neoforged.neoforge.client.ClientHooks;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PFMItemRenderer extends BuiltinModelItemRenderer {
    public static final PFMItemRenderer INSTANCE = new PFMItemRenderer();
    public PFMItemRenderer() {
        super(MinecraftClient.getInstance().getBlockEntityRenderDispatcher(), MinecraftClient.getInstance().getEntityModelLoader());
    }

    static Map<WoodVariant, Map<String, BakedModel>> bakedModels = new LinkedHashMap<>();
    static List<String> modelParts = new ArrayList<>();
    public BakedModel getLampPartFromVariant(WoodVariant variantBase, int index) {
        if (!bakedModels.isEmpty() && !modelParts.isEmpty()) {
            return bakedModels.get(variantBase).get(modelParts.get(index));
        }
        modelParts.clear();
        bakedModels.clear();
        modelParts.addAll(UnbakedBasicLampModel.MODEL_PARTS_BASE);
        modelParts.addAll(UnbakedBasicLampModel.STATIC_PARTS);
        for (WoodVariant woodVariant : WoodVariantRegistry.getVariants()) {
            bakedModels.put(woodVariant, new LinkedHashMap<>());
            for (String part : modelParts) {
                bakedModels.get(woodVariant).put(part, MinecraftClient.getInstance().getBakedModelManager().getModel(ModelIdentifier.ofInventoryVariant(Identifier.of(PaladinFurnitureMod.MOD_ID, part.replaceAll("template", woodVariant.asString())))));
            }
        }
        return bakedModels.get(variantBase).get(modelParts.get(index));
    }

    @Override
    public void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (stack.isOf(PaladinFurnitureModBlocksItems.BASIC_LAMP_ITEM)) {
            WoodVariant variant = WoodVariantRegistry.OAK;
            if (stack.contains(PFMComponents.VARIANT_COMPONENT)) {
                variant = WoodVariantRegistry.getVariant(stack.get(PFMComponents.VARIANT_COMPONENT));
            }

            boolean glint = stack.hasGlint();

            ItemRenderer renderer = MinecraftClient.getInstance().getItemRenderer();
            boolean leftHanded = MinecraftClient.getInstance().player != null && MinecraftClient.getInstance().player.getMainArm() == Arm.LEFT && mode.isFirstPerson();
            VertexConsumer consumer = ItemRenderer.getItemGlintConsumer(vertexConsumers, RenderLayers.getItemLayer(stack), true, glint);

            matrices.pop();


            BakedModel lampShadeModel = ClientHooks.handleCameraTransforms(matrices, getLampPartFromVariant(variant, 4), mode, leftHanded);

            matrices.translate(-.5, -.5, -.5); // Replicate ItemRenderer's translation

            renderer.renderBakedItemModel(lampShadeModel, stack, light, overlay, matrices, consumer);

            BakedModel poleModel = getLampPartFromVariant(variant, 2);
            renderer.renderBakedItemModel(poleModel, stack, light, overlay, matrices, consumer);

            BakedModel bulbModel = getLampPartFromVariant(variant, 5);
            renderer.renderBakedItemModel(bulbModel, stack, light, overlay, matrices, consumer);
            matrices.push();
        }
    }
}
