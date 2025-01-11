package com.unlikepaladin.pfm.registry.forge;

import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.recipes.FreezingRecipe;
import com.unlikepaladin.pfm.recipes.FurnitureRecipe;
import com.unlikepaladin.pfm.recipes.forge.FurnitureSerializerForge;
import com.unlikepaladin.pfm.registry.RecipeTypes;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

@Mod.EventBusSubscriber(modid = "pfm", bus = Mod.EventBusSubscriber.Bus.MOD)
public class RecipeRegistryForge {

    @SubscribeEvent
    public static void registerRecipeSerializers(RegisterEvent event) {
        event.register(ForgeRegistries.Keys.RECIPE_SERIALIZERS, recipeSerializerRegisterHelper -> {
            recipeSerializerRegisterHelper.register(
                    Identifier.of(PaladinFurnitureMod.MOD_ID, "freezing"), RecipeTypes.FREEZING_RECIPE_SERIALIZER = new AbstractCookingRecipe.Serializer<>(FreezingRecipe::new, 200)
            );
            recipeSerializerRegisterHelper.register(
                    Identifier.of(PaladinFurnitureMod.MOD_ID, "furniture"), RecipeTypes.FURNITURE_SERIALIZER = new FurnitureSerializerForge()
            );
            // Can't run resource gen until the recipe serializer has been registered or it dies because it needs the ID
            // PFMRuntimeResources.prepareAsyncResourceGen(); Had to disable async gen because Forge dies and I can't be bothered to figure out why, this is cursed enough as it is
        });
    }


    @SubscribeEvent
    public static void registerRecipeTypes(RegisterEvent event){
        event.register(ForgeRegistries.Keys.RECIPE_TYPES, recipeTypeRegisterHelper -> {
            recipeTypeRegisterHelper.register(RecipeTypes.FREEZING_ID, RecipeTypes.FREEZING_RECIPE = new RecipeType<FreezingRecipe>() {
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
