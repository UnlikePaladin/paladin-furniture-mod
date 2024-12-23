package com.unlikepaladin.pfm.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.unlikepaladin.pfm.client.ColorRegistry;
import com.unlikepaladin.pfm.data.materials.WoodVariantRegistry;
import com.unlikepaladin.pfm.items.PFMComponents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.item.model.BasicItemModel;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.item.tint.TintSource;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(BasicItemModel.class)
public class BasicItemModelMixin {

    @Unique
    private ItemStack pfm$parentStack = ItemStack.EMPTY;

    @Unique
    private List<TintSource> pfm$parentTints;

    @Inject(method = "update", at = @At(value = "HEAD", target = "Lnet/minecraft/client/render/item/ItemRenderState$LayerRenderState;initTints(I)[I"))
    private void inject(ItemRenderState state, ItemStack stack, ItemModelManager resolver, ModelTransformationMode transformationMode, ClientWorld world, LivingEntity user, int seed, CallbackInfo ci) {
        if (ColorRegistry.itemColorProviders.containsKey(stack.getItem()) && pfm$parentTints == null) {
            Item item = ColorRegistry.itemColorProviders.get(stack.getItem()).asItem();

            Identifier parentModelId = item.getDefaultStack().get(DataComponentTypes.ITEM_MODEL);

            ItemModel parentModel = MinecraftClient.getInstance().getBakedModelManager().getItemModel(parentModelId);
            pfm$parentStack = item.getDefaultStack();
            this.pfm$parentTints = exploreForTints(parentModel, world, user, seed, transformationMode);
        }
        if (stack.contains(PFMComponents.VARIANT_COMPONENT)) {
            Item item = WoodVariantRegistry.getVariant(stack.get(PFMComponents.VARIANT_COMPONENT)).getLogBlock().asItem();

            Identifier parentModelId = item.getDefaultStack().get(DataComponentTypes.ITEM_MODEL);

            ItemModel parentModel = MinecraftClient.getInstance().getBakedModelManager().getItemModel(parentModelId);
            pfm$parentStack = item.getDefaultStack();
            this.pfm$parentTints = exploreForTints(parentModel, world, user, seed, transformationMode);
        }
    }

    @Unique
    private List<TintSource> exploreForTints(ItemModel model, ClientWorld world, LivingEntity user, int seed, ModelTransformationMode transformationMode) {
        switch (model) {
            case BasicItemModelAccessor accessor -> {
                return accessor.getTints();
            }
            case CompositeItemModelAccessor accessor -> {
                for (ItemModel itemModel : accessor.getItemModels()) {
                    List<TintSource> src = exploreForTints(itemModel, world, user, seed, transformationMode);
                    if (!src.isEmpty())
                        return src;
                }
            }
            case ConditionItemModelAccessor accessor -> {
                boolean property = accessor.getProperty().getValue(pfm$parentStack, world, user, seed, transformationMode);
                if (property)
                    return exploreForTints(accessor.getOnTrue(), world, user, seed, transformationMode);
                else
                    return exploreForTints(accessor.getOnFalse(), world, user, seed, transformationMode);
            }
            case SelectItemModelAccessor<?> accessor -> {
                ItemModel itemModel = accessor.getCases().get(accessor.getProperty().getValue(pfm$parentStack, world, user, seed, transformationMode));
                return exploreForTints(itemModel, world, user, seed, transformationMode);
            }
            case RangeDispatchItemModelAccessor accessor -> {
                ItemModel itemModel;
                float select = accessor.getProperty().getValue(pfm$parentStack, world, user, seed) * accessor.getScale();
                if (Float.isNaN(select)) {
                    itemModel = accessor.getFallback();
                } else {
                    int i = RangeDispatchItemModelAccessor.getIndex(accessor.getThresholds(), select);
                    itemModel = i == -1 ? accessor.getFallback() : accessor.getModels()[i];
                }
                return exploreForTints(itemModel, world, user, seed, transformationMode);
            }
            case null, default -> {
                return List.of();
            }
        }
        return List.of();
    }

    @WrapOperation(method = "update", at = @At(value = "INVOKE", target = "Ljava/util/List;get(I)Ljava/lang/Object;"))
    private <E> E swapTintIndx(List instance, int i, Operation<TintSource> original, @Share("currentColorIndex")LocalIntRef currentColorIndex) {
        currentColorIndex.set(i);
        if (pfm$parentTints != null) {
            return (E) pfm$parentTints.get(i);
        }
        return (E) original.call(instance, i);
    }

    @WrapOperation(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/tint/TintSource;getTint(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/entity/LivingEntity;)I"))
    private int swapTintColor(TintSource instance, ItemStack itemStack, ClientWorld clientWorld, LivingEntity livingEntity, Operation<Integer> original, @Share("currentColorIndex")LocalIntRef currentColorIndex) {
        if (currentColorIndex.get() == 1 && itemStack.contains(PFMComponents.COLOR_COMPONENT)) {
            return itemStack.getOrDefault(PFMComponents.COLOR_COMPONENT, DyeColor.WHITE).getMapColor().color;
        }
        if (pfm$parentTints != null) {
            return pfm$parentTints.get(currentColorIndex.get()).getTint(pfm$parentStack, clientWorld, livingEntity);
        }
        return original.call(instance, itemStack, clientWorld, livingEntity);
    }
}
