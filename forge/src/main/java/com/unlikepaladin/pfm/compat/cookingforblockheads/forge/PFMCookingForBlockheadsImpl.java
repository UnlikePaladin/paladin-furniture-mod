package com.unlikepaladin.pfm.compat.cookingforblockheads.forge;

import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.blocks.*;
import com.unlikepaladin.pfm.compat.PFMClientModCompatibility;
import com.unlikepaladin.pfm.compat.cookingforblockheads.PFMCookingForBlockheads;
import com.unlikepaladin.pfm.compat.cookingforblockheads.forge.client.PFMCookingForBlockheadsClient;
import com.unlikepaladin.pfm.data.PFMTag;
import com.unlikepaladin.pfm.data.PFMTags;
import com.unlikepaladin.pfm.registry.PaladinFurnitureModBlocksItems;
import com.unlikepaladin.pfm.registry.dynamic.LateBlockRegistry;
import com.unlikepaladin.pfm.runtime.data.FurnitureRecipeJsonFactory;
import com.unlikepaladin.pfm.runtime.data.PFMRecipeProvider;
import com.unlikepaladin.pfm.runtime.data.PFMTagProvider;
import net.blay09.mods.cookingforblockheads.CookingForBlockheads;
import net.blay09.mods.cookingforblockheads.item.ModItems;
import net.blay09.mods.cookingforblockheads.tag.ModBlockTags;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PFMCookingForBlockheadsImpl extends PFMCookingForBlockheads {
    private PFMClientModCompatibility clientModCompatibility;

    public PFMCookingForBlockheadsImpl() {
    }

    @Override
    public void generateTags() {
        super.generateTags();

        PFMTagProvider.getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE)
                .add(PFMCookingForBlockHeadsCompat.COOKING_TABLE_BLOCK);

        PFMTagProvider.getOrCreateTagBuilder(ModBlockTags.COOKING_TABLES)
                .add(PFMCookingForBlockHeadsCompat.COOKING_TABLE_BLOCK);

        List<Block> storageBlocks = new ArrayList<>(PaladinFurnitureMod.furnitureEntryMap.get(KitchenDrawerBlock.class).getAllBlocks());
        storageBlocks.addAll(PaladinFurnitureMod.furnitureEntryMap.get(KitchenWallDrawerBlock.class).getAllBlocks());
        storageBlocks.addAll(PaladinFurnitureMod.furnitureEntryMap.get(KitchenCabinetBlock.class).getAllBlocks());
        storageBlocks.addAll(PaladinFurnitureMod.furnitureEntryMap.get(ClassicNightstandBlock.class).getAllBlocks());
        storageBlocks.addAll(PaladinFurnitureMod.furnitureEntryMap.get(KitchenWallDrawerSmallBlock.class).getAllBlocks());
        storageBlocks.addAll(PaladinFurnitureMod.furnitureEntryMap.get(KitchenCounterOvenBlock.class).getAllBlocks());
        storageBlocks.addAll(List.of(PaladinFurnitureModBlocksItems.WHITE_FRIDGE, PaladinFurnitureModBlocksItems.XBOX_FRIDGE, PaladinFurnitureModBlocksItems.GRAY_FRIDGE, PaladinFurnitureModBlocksItems.IRON_FRIDGE));

        PFMTagProvider.getOrCreateTagBuilder(ModBlockTags.KITCHEN_ITEM_PROVIDERS)
                .add(storageBlocks.toArray(new Block[0]));

        Block[] ovens = {PaladinFurnitureModBlocksItems.WHITE_STOVE, PaladinFurnitureModBlocksItems.GRAY_STOVE, PaladinFurnitureModBlocksItems.IRON_STOVE};
        Block[] freezers = {PaladinFurnitureModBlocksItems.GRAY_FREEZER, PaladinFurnitureModBlocksItems.IRON_FREEZER, PaladinFurnitureModBlocksItems.WHITE_FREEZER};

        PFMTag<Block> builder = PFMTagProvider.getOrCreateTagBuilder(ModBlockTags.KITCHEN_CONNECTORS);
        PaladinFurnitureMod.furnitureEntryMap.get(KitchenCounterBlock.class).getAllBlocks().forEach(builder::add);
        PaladinFurnitureMod.furnitureEntryMap.get(KitchenWallCounterBlock.class).getAllBlocks().forEach(builder::add);
        PaladinFurnitureMod.furnitureEntryMap.get(KitchenSinkBlock.class).getAllBlocks().forEach(builder::add);
        builder.add(storageBlocks.toArray(new Block[0]));
        builder.add(ovens);
        builder.add(freezers);
    }

    @Override
    public void generateRecipes(RecipeExporter exporter) {
        FurnitureRecipeJsonFactory.create(PFMCookingForBlockHeadsCompat.COOKING_TABLE_BLOCK, 4).group("kitchen").criterion(PFMRecipeProvider.getCriterionNameFromOutput(PFMCookingForBlockHeadsCompat.COOKING_TABLE_BLOCK), PFMRecipeProvider.conditionsFromItem(ModItems.recipeBook)).input(ModItems.recipeBook).input(Blocks.WHITE_CONCRETE, 2).input(Blocks.GRAY_CONCRETE).offerTo(exporter, Identifier.of("pfm", PFMCookingForBlockHeadsCompat.COOKING_TABLE_BLOCK.asItem().getTranslationKey().replace("block.pfm.", "")));
    }

    @Override
    public String getModId() {
        return CookingForBlockheads.MOD_ID;
    }

    @Override
    public void registerBlocks() {
        LateBlockRegistry.registerLateBlockClassic("cooking_table", PFMCookingForBlockHeadsCompat.COOKING_TABLE_BLOCK, true, PaladinFurnitureMod.FURNITURE_GROUP);
    }

    public static PFMCookingForBlockheads getInstance() {
        return new PFMCookingForBlockheadsImpl();
    }

    @Override
    public Optional<PFMClientModCompatibility> getClientModCompatiblity() {
        if (clientModCompatibility == null)
            clientModCompatibility = new PFMCookingForBlockheadsClient(this);
        return Optional.of(clientModCompatibility);
    }
}
