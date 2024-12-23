package com.unlikepaladin.pfm.client.model;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.unlikepaladin.pfm.client.ColorRegistry;
import com.unlikepaladin.pfm.data.materials.VariantHelper;
import com.unlikepaladin.pfm.items.PFMComponents;
import com.unlikepaladin.pfm.mixin.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.item.model.BasicItemModel;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.item.model.SpecialItemModel;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;
import net.minecraft.client.render.item.model.special.SpecialModelTypes;
import net.minecraft.client.render.item.tint.ConstantTintSource;
import net.minecraft.client.render.item.tint.TintSource;
import net.minecraft.client.render.item.tint.TintSourceTypes;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ResolvableModel;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PFMItemModel<T> implements ItemModel {
    private final SpecialModelRenderer<T> specialModelType;
    private final BakedModel model;
    private final List<TintSource> tints;
    private final List<TintSource> pfm$parentTints = new ArrayList<>();

    public PFMItemModel(BakedModel model, SpecialModelRenderer<T> specialModelType, List<TintSource> tints) {
        this.model = model;
        this.tints = tints;
        this.specialModelType = specialModelType;
    }

    @Override
    public void update(ItemRenderState state, ItemStack stack, ItemModelManager resolver, ModelTransformationMode transformationMode, @Nullable ClientWorld world, @Nullable LivingEntity user, int seed) {
        if (specialModelType != null) {
            ItemRenderState.LayerRenderState specialLayerRenderState = state.newLayer();
            specialLayerRenderState.setSpecialModel(this.specialModelType, this.specialModelType.getData(stack), this.model);
        }

        ItemRenderState.LayerRenderState layerRenderState = state.newLayer();
        if (stack.hasGlint()) {
            layerRenderState.setGlint(ItemRenderState.Glint.STANDARD);
        }

        RenderLayer renderLayer = RenderLayers.getItemLayer(stack);
        layerRenderState.setModel(this.model, renderLayer);

        if (ColorRegistry.itemColorProviders.containsKey(stack.getItem()) && pfm$parentTints.isEmpty()) {
            Item item = ColorRegistry.itemColorProviders.get(stack.getItem()).asItem();

            Identifier parentModelId = item.getDefaultStack().get(DataComponentTypes.ITEM_MODEL);

            ItemModel parentModel = MinecraftClient.getInstance().getBakedModelManager().getItemModel(parentModelId);
            pfm$parentTints.addAll(exploreForTints(stack, parentModel, world, user, seed, transformationMode));
        }

        List<TintSource> tintsToUse = pfm$parentTints.isEmpty() ? this.tints : pfm$parentTints;

        int tintCount = tintsToUse.size();
        if (tintCount == 0 && stack.contains(PFMComponents.COLOR_COMPONENT)) {
            tintCount = 2;
            tintsToUse = new ArrayList<>(tintCount);

            tintsToUse.add(new ConstantTintSource(0xffffff));
            tintsToUse.add(new ConstantTintSource(0xffffff));
        }

        int[] tintArray = layerRenderState.initTints(tintCount);

        for (int index = 0; index < tintCount; index++) {
            if (index == 1 && stack.contains(PFMComponents.COLOR_COMPONENT)) {
                tintArray[index] =  stack.getOrDefault(PFMComponents.COLOR_COMPONENT, DyeColor.WHITE).getMapColor().color;
            } else {
                tintArray[index] = tintsToUse.get(index).getTint(stack, world, user);
            }
        }

        if (stack.getItem() instanceof BlockItem && model instanceof PFMBakedModelSetPropertiesExtension) {
            ((PFMBakedModelSetPropertiesExtension) model).setBlockStateProperty(((BlockItem) stack.getItem()).getBlock().getDefaultState());
            if (stack.contains(PFMComponents.VARIANT_COMPONENT))
                ((PFMBakedModelSetPropertiesExtension) model).setVariant(VariantHelper.getVariant(stack.get(PFMComponents.VARIANT_COMPONENT)));
        }
    }

    private List<TintSource> exploreForTints(ItemStack itemStack, ItemModel model, ClientWorld world, LivingEntity user, int seed, ModelTransformationMode transformationMode) {
        switch (model) {
            case BasicItemModelAccessor accessor -> {
                return accessor.getTints();
            }
            case CompositeItemModelAccessor accessor -> {
                for (ItemModel itemModel : accessor.getItemModels()) {
                    List<TintSource> src = exploreForTints(itemStack, itemModel, world, user, seed, transformationMode);
                    if (!src.isEmpty())
                        return src;
                }
            }
            case ConditionItemModelAccessor accessor -> {
                boolean property = accessor.getProperty().getValue(itemStack, world, user, seed, transformationMode);
                if (property)
                    return exploreForTints(itemStack, accessor.getOnTrue(), world, user, seed, transformationMode);
                else
                    return exploreForTints(itemStack, accessor.getOnFalse(), world, user, seed, transformationMode);
            }
            case SelectItemModelAccessor<?> accessor -> {
                ItemModel itemModel = accessor.getCases().get(accessor.getProperty().getValue(itemStack, world, user, seed, transformationMode));
                return exploreForTints(itemStack, itemModel, world, user, seed, transformationMode);
            }
            case RangeDispatchItemModelAccessor accessor -> {
                ItemModel itemModel;
                float select = accessor.getProperty().getValue(itemStack, world, user, seed) * accessor.getScale();
                if (Float.isNaN(select)) {
                    itemModel = accessor.getFallback();
                } else {
                    int i = RangeDispatchItemModelAccessor.getIndex(accessor.getThresholds(), select);
                    itemModel = i == -1 ? accessor.getFallback() : accessor.getModels()[i];
                }
                return exploreForTints(itemStack, itemModel, world, user, seed, transformationMode);
            }
            case null, default -> {
                return List.of();
            }
        }
        return List.of();
    }

    @Environment(EnvType.CLIENT)
    public record Unbaked(Identifier model, Optional<SpecialModelRenderer.Unbaked> specialModel, List<TintSource> tints) implements ItemModel.Unbaked {
        public static final MapCodec<Unbaked> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                                Identifier.CODEC.fieldOf("model").forGetter(Unbaked::model),
                                SpecialModelTypes.CODEC.optionalFieldOf("special_model").forGetter(Unbaked::specialModel),
                                TintSourceTypes.CODEC.listOf().optionalFieldOf("tints", List.of()).forGetter(Unbaked::tints)
                        )
                        .apply(instance, Unbaked::new)
        );

        @Override
        public void resolve(Resolver resolver) {
            resolver.resolve(this.model);
        }

        @Override
        public ItemModel bake(BakeContext context) {
            BakedModel bakedModel = context.bake(this.model);
            if (specialModel.isPresent()) {
                SpecialModelRenderer<?> specialModelRenderer = this.specialModel.get().bake(context.entityModelSet());
                return new PFMItemModel<>(bakedModel, specialModelRenderer, this.tints);
            }
            return new PFMItemModel<>(bakedModel, null, this.tints);
        }

        @Override
        public MapCodec<Unbaked> getCodec() {
            return CODEC;
        }
    }
}
