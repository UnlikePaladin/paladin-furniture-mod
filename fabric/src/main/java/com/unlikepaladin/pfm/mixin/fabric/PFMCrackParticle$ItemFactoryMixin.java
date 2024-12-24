package com.unlikepaladin.pfm.mixin.fabric;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.unlikepaladin.pfm.client.fabric.PFMBakedModelParticleExtension;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.CrackParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.BlockItem;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CrackParticle.ItemFactory.class)
public class PFMCrackParticle$ItemFactoryMixin {
    @ModifyReturnValue(method = "createParticle(Lnet/minecraft/particle/ItemStackParticleEffect;Lnet/minecraft/client/world/ClientWorld;DDDDDD)Lnet/minecraft/client/particle/Particle;", at = @At("RETURN"))
    public Particle modifyParticle(Particle original, ItemStackParticleEffect itemStackParticleEffect, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
        if (itemStackParticleEffect.getItemStack().getItem() instanceof BlockItem) {
            BlockState defaultState = ((BlockItem)itemStackParticleEffect.getItemStack().getItem()).getBlock().getDefaultState();
            BakedModel model = MinecraftClient.getInstance().getBakedModelManager().getBlockModels().getModel(defaultState);
            if (model instanceof PFMBakedModelParticleExtension) {
                ((PFMSpriteBillBoardParticleMixin)original).setSprite(((PFMBakedModelParticleExtension) model).pfm$getParticle(clientWorld, BlockPos.ofFloored(d, e, f), defaultState));
            }
        }
        return original;
    }
}
