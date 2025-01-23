package com.unlikepaladin.pfm.compat.cookingforblockheads.forge;

import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.compat.PFMClientModCompatibility;
import com.unlikepaladin.pfm.compat.cookingforblockheads.PFMCookingForBlockheads;
import com.unlikepaladin.pfm.compat.cookingforblockheads.forge.client.PFMCookingForBlockheadsClient;
import com.unlikepaladin.pfm.registry.dynamic.LateBlockRegistry;
import com.unlikepaladin.pfm.runtime.data.SimpleFurnitureRecipeJsonFactory;
import com.unlikepaladin.pfm.runtime.data.PFMRecipeProvider;
import com.unlikepaladin.pfm.runtime.data.PFMTagProvider;
import net.blay09.mods.cookingforblockheads.CookingForBlockheads;
import net.blay09.mods.cookingforblockheads.item.ModItems;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Identifier;

import java.util.Optional;
import java.util.function.Consumer;

public class PFMCookingForBlockheadsImpl extends PFMCookingForBlockheads {
    private PFMClientModCompatibility clientModCompatibility;

    public PFMCookingForBlockheadsImpl() {
    }

    @Override
    public void generateTags() {
        super.generateTags();

        PFMTagProvider.getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE)
                .add(PFMCookingForBlockHeadsCompat.COOKING_TABLE_BLOCK);
    }

    @Override
    public void generateRecipes(Consumer<RecipeJsonProvider> exporter) {
        SimpleFurnitureRecipeJsonFactory.create(PFMCookingForBlockHeadsCompat.COOKING_TABLE_BLOCK, 4).group("kitchen").criterion(PFMRecipeProvider.getCriterionNameFromOutput(PFMCookingForBlockHeadsCompat.COOKING_TABLE_BLOCK), PFMRecipeProvider.conditionsFromItem(ModItems.recipeBook)).input(ModItems.recipeBook).input(Blocks.WHITE_CONCRETE, 2).input(Blocks.GRAY_CONCRETE).offerTo(exporter, new Identifier("pfm", PFMCookingForBlockHeadsCompat.COOKING_TABLE_BLOCK.asItem().getTranslationKey().replace("block.pfm.", "")));
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
