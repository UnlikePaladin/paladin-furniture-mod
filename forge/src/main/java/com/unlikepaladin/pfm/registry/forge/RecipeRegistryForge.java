package com.unlikepaladin.pfm.registry.forge;

import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.compat.cookingforblockheads.forge.PFMCookingForBlockHeadsCompat;
import com.unlikepaladin.pfm.recipes.DynamicFurnitureRecipe;
import com.unlikepaladin.pfm.recipes.FreezingRecipe;
import com.unlikepaladin.pfm.recipes.FurnitureRecipe;
import com.unlikepaladin.pfm.recipes.SimpleFurnitureRecipe;
import com.unlikepaladin.pfm.recipes.forge.FurnitureSerializerForge;
import com.unlikepaladin.pfm.registry.RecipeTypes;
import net.minecraft.recipe.CookingRecipeSerializer;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "pfm", bus = Mod.EventBusSubscriber.Bus.MOD)
public class RecipeRegistryForge {

    @SubscribeEvent
    public static void registerRecipeSerializers(RegistryEvent.Register<RecipeSerializer<?>> event) {
        event.getRegistry().register(
                (RecipeTypes.FREEZING_RECIPE_SERIALIZER = new CookingRecipeSerializer<>(FreezingRecipe::new, 200)).setRegistryName(RecipeTypes.FREEZING_ID)
        );
        event.getRegistry().register(
                (RecipeTypes.SIMPLE_FURNITURE_SERIALIZER = new FurnitureSerializerForge<>(new SimpleFurnitureRecipe.Serializer())).setRegistryName(RecipeTypes.SIMPLE_FURNITURE_ID)
        );
        event.getRegistry().register(
                (RecipeTypes.DYNAMIC_FURNITURE_SERIALIZER = new FurnitureSerializerForge<>(new DynamicFurnitureRecipe.Serializer())).setRegistryName(RecipeTypes.DYNAMIC_FURNITURE_ID)
        );
        // Can't run resource gen until the recipe serializer has been registered or it dies because it needs the ID
        // PFMRuntimeResources.prepareAsyncResourceGen(); Had to disable async gen because Forge dies and I can't be bothered to figure out why, this is cursed enough as it is
        if (PaladinFurnitureMod.getModList().contains("cookingforblockheads"))
            PFMCookingForBlockHeadsCompat.initBlockConnectors();
    }


    @SubscribeEvent
    public static void registerRecipeTypes(RegistryEvent.Register<RecipeSerializer<?>> event) {
        RecipeTypes.FREEZING_RECIPE = Registry.register(Registry.RECIPE_TYPE, RecipeTypes.FREEZING_ID,  new RecipeType<FreezingRecipe>() {
            @Override
            public String toString() {return RecipeTypes.FREEZING_ID.getPath();}
        });
        RecipeTypes.FURNITURE_RECIPE = Registry.register(Registry.RECIPE_TYPE, RecipeTypes.FURNITURE_ID,  new RecipeType<FurnitureRecipe>() {
            @Override
            public String toString() {return RecipeTypes.FURNITURE_ID.getPath();}
        });
    }



}
