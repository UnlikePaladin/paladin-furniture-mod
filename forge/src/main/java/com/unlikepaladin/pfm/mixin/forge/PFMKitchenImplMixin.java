package com.unlikepaladin.pfm.mixin.forge;

import com.unlikepaladin.pfm.compat.cookingforblockheads.forge.PFMCookingForBlockHeadsCompat;
import net.blay09.mods.cookingforblockheads.crafting.KitchenImpl;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Pseudo
@Mixin(KitchenImpl.class)
public class PFMKitchenImplMixin {
    @Redirect(method = "canProcess", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z"))
    private boolean isCookingTableBlock(BlockState instance, Block block) {
        return instance.getBlock() == block || instance.getBlock() == PFMCookingForBlockHeadsCompat.COOKING_TABLE_BLOCK;
    }
}
