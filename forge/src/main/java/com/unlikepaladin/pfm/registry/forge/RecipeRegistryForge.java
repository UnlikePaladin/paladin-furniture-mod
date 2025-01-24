package com.unlikepaladin.pfm.registry.forge;

import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.compat.cookingforblockheads.forge.PFMCookingForBlockHeadsCompat;
import com.unlikepaladin.pfm.recipes.DynamicFurnitureRecipe;
import com.unlikepaladin.pfm.recipes.FreezingRecipe;
import com.unlikepaladin.pfm.recipes.FurnitureRecipe;
import com.unlikepaladin.pfm.recipes.SimpleFurnitureRecipe;
import com.unlikepaladin.pfm.recipes.forge.FurnitureSerializerForge;
import com.unlikepaladin.pfm.registry.RecipeTypes;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import com.unlikepaladin.pfm.runtime.PFMRuntimeResources;
import net.minecraft.recipe.CookingRecipeSerializer;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

@Mod.EventBusSubscriber(modid = "pfm", bus = Mod.EventBusSubscriber.Bus.MOD)
public class RecipeRegistryForge {

    @SubscribeEvent
    public static void registerRecipeSerializers(RegisterEvent event) {
        event.register(ForgeRegistries.Keys.RECIPE_SERIALIZERS, recipeSerializerRegisterHelper -> {
            recipeSerializerRegisterHelper.register(
                    RecipeTypes.FREEZING_ID, RecipeTypes.FREEZING_RECIPE_SERIALIZER = new CookingRecipeSerializer<>(FreezingRecipe::new, 200)
            );
            recipeSerializerRegisterHelper.register(
                    RecipeTypes.SIMPLE_FURNITURE_ID, RecipeTypes.SIMPLE_FURNITURE_SERIALIZER = new FurnitureSerializerForge<>(new SimpleFurnitureRecipe.Serializer())
            );
            recipeSerializerRegisterHelper.register(
                    RecipeTypes.DYNAMIC_FURNITURE_ID, RecipeTypes.DYNAMIC_FURNITURE_SERIALIZER = new FurnitureSerializerForge<>(new DynamicFurnitureRecipe.Serializer())
            );
            // Can't run resource gen until the recipe serializer has been registered or it dies because it needs the ID
            // PFMRuntimeResources.prepareAsyncResourceGen(); Had to disable async gen because Forge dies and I can't be bothered to figure out why, this is cursed enough as it is
            if (PaladinFurnitureMod.getModList().contains("cookingforblockheads"))
                PFMCookingForBlockHeadsCompat.initBlockConnectors();
        });
    }

    @SubscribeEvent
    public static void registerRecipeTypes(RegisterEvent event){
        event.register(ForgeRegistries.Keys.RECIPE_TYPES, recipeTypeRegisterHelper -> {
            recipeTypeRegisterHelper.register(RecipeTypes.FREEZING_ID, RecipeTypes.FREEZING_RECIPE = new RecipeType<>() {
                @Override
                public String toString() {
                    return RecipeTypes.FREEZING_ID.getPath();
                }
            });
            recipeTypeRegisterHelper.register(RecipeTypes.FURNITURE_ID, RecipeTypes.FURNITURE_RECIPE = new RecipeType<>() {
                @Override
                public String toString() {
                    return RecipeTypes.FURNITURE_ID.getPath();
                }
            });
        });
    }


}
