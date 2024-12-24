package com.unlikepaladin.pfm.mixin.fabric;

import com.unlikepaladin.pfm.client.fabric.PFMBakedModelParticleExtension;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.BlockDustParticle;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockDustParticle.class)
public abstract class PFMBlockDustParticleMixin extends SpriteBillboardParticle {
    protected PFMBlockDustParticleMixin(ClientWorld clientWorld, double d, double e, double f) {
        super(clientWorld, d, e, f);
    }

    @Inject(method = "<init>(Lnet/minecraft/client/world/ClientWorld;DDDDDDLnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;)V", at = @At("TAIL"))
    public void setCustomModelParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, BlockState state, BlockPos blockPos, CallbackInfo ci){
        if (state != null) {
            BakedModel model = MinecraftClient.getInstance().getBakedModelManager().getBlockModels().getModel(state);
            if (model instanceof PFMBakedModelParticleExtension) {
                this.setSprite(((PFMBakedModelParticleExtension) model).pfm$getParticle(world, BlockPos.ofFloored(x, y, z), state));
            }
        }
    }
}