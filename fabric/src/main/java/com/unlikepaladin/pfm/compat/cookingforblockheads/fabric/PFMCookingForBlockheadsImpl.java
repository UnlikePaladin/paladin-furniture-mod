package com.unlikepaladin.pfm.compat.cookingforblockheads.fabric;

import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.compat.PFMClientModCompatibility;
import com.unlikepaladin.pfm.compat.cookingforblockheads.PFMCookingForBlockheads;
import com.unlikepaladin.pfm.compat.cookingforblockheads.fabric.client.PFMCookingForBlockheadsClient;
import com.unlikepaladin.pfm.registry.BlockEntities;
import com.unlikepaladin.pfm.registry.dynamic.LateBlockRegistry;
import com.unlikepaladin.pfm.runtime.data.FurnitureRecipeJsonFactory;
import com.unlikepaladin.pfm.runtime.data.PFMRecipeProvider;
import com.unlikepaladin.pfm.runtime.data.PFMTagProvider;
import net.blay09.mods.balm.common.BalmBlockEntity;
import net.blay09.mods.cookingforblockheads.api.capability.IKitchenConnector;
import net.blay09.mods.cookingforblockheads.api.capability.IKitchenItemProvider;
import net.blay09.mods.cookingforblockheads.api.capability.IKitchenSmeltingProvider;
import net.blay09.mods.cookingforblockheads.item.ModItems;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.Identifier;

import java.util.Optional;
import java.util.function.Consumer;

public class PFMCookingForBlockheadsImpl extends PFMCookingForBlockheads {
    private PFMClientModCompatibility clientModCompatibility;
    @Override
    public void generateRecipes(RecipeExporter exporter) {
        FurnitureRecipeJsonFactory.create(PFMCookingForBlockHeadsCompat.COOKING_TABLE_BLOCK, 4).group("kitchen").criterion(PFMRecipeProvider.getCriterionNameFromOutput(PFMCookingForBlockHeadsCompat.COOKING_TABLE_BLOCK), PFMRecipeProvider.conditionsFromItem(ModItems.recipeBook)).input(ModItems.recipeBook).input(Blocks.WHITE_CONCRETE, 2).input(Blocks.GRAY_CONCRETE).offerTo(exporter, new Identifier("pfm", PFMCookingForBlockHeadsCompat.COOKING_TABLE_BLOCK.asItem().getTranslationKey().replace("block.pfm.", "")));
    }

    @Override
    public Optional<PFMClientModCompatibility> getClientModCompatiblity() {
        if (clientModCompatibility == null)
            clientModCompatibility = new PFMCookingForBlockheadsClient(this);
        return Optional.of(clientModCompatibility);
    }

    @Override
    public void generateTags() {
        super.generateTags();

        PFMTagProvider.getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE)
                .add(PFMCookingForBlockHeadsCompat.COOKING_TABLE_BLOCK);
    }

    @Override
    public String getModId() {
        return "cookingforblockheads";
    }

    @Override
    public void registerBlocks() {
        LateBlockRegistry.registerLateBlockClassic("cooking_table", PFMCookingForBlockHeadsCompat.COOKING_TABLE_BLOCK, true, PaladinFurnitureMod.FURNITURE_GROUP);
    }

    @Override
    public void registerBlockEntityTypes() {
        this.registerLookup("kitchen_smelting_provider", IKitchenSmeltingProvider.class, BlockEntities.STOVE_BLOCK_ENTITY);
        this.registerLookup("kitchen_item_provider", IKitchenItemProvider.class, BlockEntities.DRAWER_BLOCK_ENTITY, BlockEntities.FRIDGE_BLOCK_ENTITY, BlockEntities.FREEZER_BLOCK_ENTITY, BlockEntities.KITCHEN_DRAWER_SMALL_BLOCK_ENTITY, BlockEntities.KITCHEN_COUNTER_OVEN_BLOCK_ENTITY);
        this.registerLookup("kitchen_connector", IKitchenConnector.class, BlockEntities.DRAWER_BLOCK_ENTITY, BlockEntities.FRIDGE_BLOCK_ENTITY, BlockEntities.FREEZER_BLOCK_ENTITY, BlockEntities.KITCHEN_DRAWER_SMALL_BLOCK_ENTITY, BlockEntities.KITCHEN_COUNTER_OVEN_BLOCK_ENTITY, BlockEntities.STOVE_BLOCK_ENTITY, BlockEntities.SINK_BLOCK_ENTITY);
    }

    private <T> void registerLookup(String provName, Class<T> clazz, BlockEntityType<?>... blockEntities) {
        Identifier identifier = new Identifier(getModId(), provName);
        BlockApiLookup<T, Void> lookup = BlockApiLookup.get(identifier, clazz, Void.class);
        lookup.registerForBlockEntities((blockEntity, context) -> {
            return blockEntity instanceof BalmBlockEntity ? (T) ((BalmBlockEntity) blockEntity).getProvider(clazz) : ((BlockEntityContract)blockEntity).getProvider(clazz);
        }, blockEntities);
    }

    public static PFMCookingForBlockheads getInstance() {
        return new PFMCookingForBlockheadsImpl();
    }
}
