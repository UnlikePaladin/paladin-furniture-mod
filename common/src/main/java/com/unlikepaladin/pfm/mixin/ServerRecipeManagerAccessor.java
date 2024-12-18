package com.unlikepaladin.pfm.mixin;

import net.minecraft.recipe.PreparedRecipes;
import net.minecraft.recipe.ServerRecipeManager;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerRecipeManager.class)
public interface ServerRecipeManagerAccessor {
    @Intrinsic
    @Accessor("preparedRecipes")
    PreparedRecipes getPreparedRecipes();
}
