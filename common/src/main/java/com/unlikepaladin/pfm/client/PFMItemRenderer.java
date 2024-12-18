package com.unlikepaladin.pfm.client;

import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.blocks.SimpleBedBlock;
import com.unlikepaladin.pfm.blocks.blockentities.PFMBedBlockEntity;
import com.unlikepaladin.pfm.blocks.models.basicLamp.UnbakedBasicLampModel;
import com.unlikepaladin.pfm.blocks.models.bed.UnbakedBedModel;
import com.unlikepaladin.pfm.data.materials.WoodVariant;
import com.unlikepaladin.pfm.data.materials.WoodVariantRegistry;
import com.unlikepaladin.pfm.registry.PaladinFurnitureModBlocksItems;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BedBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.*;

public class PFMItemRenderer extends BuiltinModelItemRenderer {
    private final PFMBedBlockEntity renderBed;
    private final BlockEntityRenderDispatcher blockEntityRenderDispatcher;
    public PFMItemRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher, EntityModelLoader loader) {
        super(blockEntityRenderDispatcher, loader);
        this.blockEntityRenderDispatcher = blockEntityRenderDispatcher;
        if (PaladinFurnitureMod.furnitureEntryMap.get(SimpleBedBlock.class) != null ) {
            renderBed = new PFMBedBlockEntity(BlockPos.ORIGIN, PaladinFurnitureMod.furnitureEntryMap.get(SimpleBedBlock.class).getVariantToBlockMapList().get(WoodVariantRegistry.OAK).iterator().next().getDefaultState());
        } else {
            renderBed = null;
        }
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
                bakedModels.get(woodVariant).put(part, ((PFMBakedModelManagerAccessor)MinecraftClient.getInstance().getBakedModelManager()).pfm$getModelFromNormalID(Identifier.of(PaladinFurnitureMod.MOD_ID, part.replaceAll("template", woodVariant.asString()))));
            }
        }
        return bakedModels.get(variantBase).get(modelParts.get(index));
    }

    public static Map<Boolean, BakedModel> bedModel = new HashMap<>();
    public BakedModel getBedModel(boolean classic) {
        if (bedModel.containsKey(classic) && bedModel.get(classic) != null) {
            return bedModel.get(classic);
        }
        bedModel.put(classic, classic ? UnbakedBedModel.inventoryModels.getRight() : UnbakedBedModel.inventoryModels.getLeft());
        return bedModel.get(classic);
    }

    @Override
    public void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        boolean leftHanded = MinecraftClient.getInstance().player != null && MinecraftClient.getInstance().player.getMainArm() == Arm.LEFT && mode.isFirstPerson();

        if (stack.getItem() instanceof BlockItem && ((BlockItem) stack.getItem()).getBlock() instanceof SimpleBedBlock) {
            matrices.push();

            Block block = ((BlockItem) stack.getItem()).getBlock();
            BakedModel bedModel = getBedModel(stack.getItem().getTranslationKey().contains("classic"));
            bedModel.getTransformation().getTransformation(mode).apply(leftHanded, matrices);
            matrices.translate(-.5, -.5, -.5); // Replicate ItemRenderer's translation

            this.renderBed.setColor(((SimpleBedBlock)block).getColor());
            this.blockEntityRenderDispatcher.renderEntity(renderBed, matrices, vertexConsumers, light, overlay);
            matrices.pop();
        }
    }
}
