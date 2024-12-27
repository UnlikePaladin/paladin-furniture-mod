package com.unlikepaladin.pfm.compat.cookingforblockheads.fabric;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.unlikepaladin.pfm.blocks.blockentities.FreezerBlockEntity;
import net.blay09.mods.balm.api.container.BalmContainerProvider;
import net.blay09.mods.balm.api.container.ContainerUtils;
import net.blay09.mods.balm.api.provider.BalmProvider;
import net.blay09.mods.balm.api.provider.BalmProviderHolder;
import net.blay09.mods.cookingforblockheads.api.CacheHint;
import net.blay09.mods.cookingforblockheads.api.IngredientToken;
import net.blay09.mods.cookingforblockheads.api.KitchenItemProvider;
import net.blay09.mods.cookingforblockheads.block.entity.FridgeBlockEntity;
import net.blay09.mods.cookingforblockheads.kitchen.ContainerKitchenItemProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

public class FreezerBlockEntityBalm extends FreezerBlockEntity implements BalmContainerProvider, BalmProviderHolder, BlockEntityContract {
    private final KitchenItemProvider itemProvider;

    public FreezerBlockEntityBalm(BlockPos pos, BlockState state) {
        super(pos, state);
        this.itemProvider = new ContainerKitchenItemProvider(this){
            private final ItemStack snowStack;
            private final ItemStack iceStack;
            {
                this.snowStack = new ItemStack(Items.SNOWBALL);
                this.iceStack = new ItemStack(Blocks.ICE);
            }

            @Override
            public IngredientToken findIngredient(Ingredient ingredient, Collection<IngredientToken> ingredientTokens, CacheHint cacheHint) {
                IngredientToken result = applyIceUnit(ingredient::test);
                if (result != null)
                    return result;

                return super.findIngredient(ingredient, ingredientTokens, cacheHint);
            }

            @Override
            public IngredientToken findIngredient(ItemStack itemStack, Collection<IngredientToken> ingredientTokens, CacheHint cacheHint) {
                IngredientToken result = applyIceUnit(stack -> ItemStack.areItemsEqual(stack, itemStack));
                if (result != null)
                    return result;

                return super.findIngredient(itemStack, ingredientTokens, cacheHint);
            }

            private @Nullable IngredientToken applyIceUnit(Function<ItemStack, Boolean> predicate) {
                if (predicate.apply(this.snowStack))
                    return new FridgeBlockEntity.IceUnitIngredientToken(ContainerUtils.copyStackWithSize(this.snowStack, 64));
                else
                    return predicate.apply(this.iceStack) ? new FridgeBlockEntity.IceUnitIngredientToken(ContainerUtils.copyStackWithSize(this.iceStack, 64)) : null;
            }
        };
    }

    @Override
    public Inventory getContainer() {
        return this;
    }

    public List<BalmProvider<?>> getProviders() {
        return Lists.newArrayList(new BalmProvider[]{new BalmProvider(KitchenItemProvider.class, this.itemProvider)});
    }

    private final Map<Class<?>, BalmProvider<?>> providers = new HashMap<>();
    private final Map<Pair<Direction, Class<?>>, BalmProvider<?>> sidedProviders = new HashMap<>();
    private boolean providersInitialized;
    @Override
    public <T> T getProvider(Class<T> clazz) {
        if (!this.providersInitialized) {
            List<BalmProviderHolder> providers = new ArrayList<>();
            this.buildProviders(providers);

            for (BalmProviderHolder providerHolder : providers) {
                Iterator var5 = providerHolder.getProviders().iterator();

                while (var5.hasNext()) {
                    BalmProvider<?> provider = (BalmProvider) var5.next();
                    this.providers.put(provider.getProviderClass(), provider);
                }

                var5 = providerHolder.getSidedProviders().iterator();

                while (var5.hasNext()) {
                    Pair<Direction, BalmProvider<?>> pair = (Pair) var5.next();
                    Direction direction = pair.getFirst();
                    BalmProvider<?> provider = pair.getSecond();
                    this.sidedProviders.put(Pair.of(direction, provider.getProviderClass()), provider);
                }
            }

            this.providersInitialized = true;
        }

        BalmProvider<?> found = this.providers.get(clazz);
        return found != null ? (T) found.getInstance() : null;
    }
}