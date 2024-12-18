package com.unlikepaladin.pfm.registry.neoforge;

import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.recipes.FreezingRecipe;
import com.unlikepaladin.pfm.recipes.FurnitureRecipe;
import com.unlikepaladin.pfm.recipes.neoforge.FurnitureSerializerNeoForge;
import com.unlikepaladin.pfm.registry.RecipeTypes;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.RegisterEvent;


@EventBusSubscriber(modid = "pfm", bus = EventBusSubscriber.Bus.MOD)
public class RecipeRegistryNeoForge {

    @SubscribeEvent
    public static void registerRecipeSerializers(RegisterEvent event) {
        event.register(Registries.RECIPE_SERIALIZER.getKey(), recipeSerializerRegisterHelper -> {
            recipeSerializerRegisterHelper.register(
                    Identifier.of(PaladinFurnitureMod.MOD_ID, "freezing"), RecipeTypes.FREEZING_RECIPE_SERIALIZER = new AbstractCookingRecipe.Serializer<>(FreezingRecipe::new, 200)
            );
            recipeSerializerRegisterHelper.register(
                    Identifier.of(PaladinFurnitureMod.MOD_ID, "furniture"), RecipeTypes.FURNITURE_SERIALIZER = new FurnitureSerializerNeoForge()
            );
            // Can't run resource gen until the recipe serializer has been registered or it dies because it needs the ID
            // PFMRuntimeResources.prepareAsyncResourceGen(); Had to disable async gen because Forge dies and I can't be bothered to figure out why, this is cursed enough as it is
        });
    }


    @SubscribeEvent
    public static void registerRecipeTypes(RegisterEvent event){
        event.register(Registries.RECIPE_TYPE.getKey(), recipeTypeRegisterHelper -> {
            recipeTypeRegisterHelper.register(Identifier.of(PaladinFurnitureMod.MOD_ID, "freezing"), RecipeTypes.FREEZING_RECIPE = new RecipeType<FreezingRecipe>() {
                @Override
                public String toString() {return "freezing";}
            });
            recipeTypeRegisterHelper.register(RecipeTypes.FURNITURE_ID, RecipeTypes.FURNITURE_RECIPE = new RecipeType<FurnitureRecipe>() {
                @Override
                public String toString() {return "furniture";}
            });
        });
    }



}
