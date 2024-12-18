package com.unlikepaladin.pfm.entity;

import com.google.common.collect.UnmodifiableIterator;
import com.unlikepaladin.pfm.blocks.AbstractSittableBlock;
import com.unlikepaladin.pfm.blocks.BasicBathtubBlock;
import com.unlikepaladin.pfm.blocks.BasicToiletBlock;
import com.unlikepaladin.pfm.blocks.ToiletState;
import com.unlikepaladin.pfm.client.PaladinFurnitureModClient;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.entity.Dismounting;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ChairEntity extends MobEntity {
    public ChairEntity(EntityType<? extends ChairEntity> type, World world) {
        super(type, world);
        this.noClip = true;
    }

    @ExpectPlatform
    public static void fart(BlockPos pos) {
        throw new AssertionError();
    }

    @Override
    public void tick() {
            if (!this.hasPassengers()) {
                if (!this.getWorld().isClient){
                    this.discard();
                }
            }
            else if (this.getWorld().getBlockState(this.getBlockPos()).getBlock() instanceof BasicToiletBlock && getWorld().isClient()){
                if (PaladinFurnitureModClient.USE_TOILET_KEYBIND.isPressed() && this.getWorld().getBlockState(this.getBlockPos()).get(BasicToiletBlock.TOILET_STATE) == ToiletState.CLEAN) {
                    fart(this.getBlockPos());
                }
                super.tick();
            }
            else if (this.getWorld().getBlockState(this.getBlockPos()).getBlock() instanceof AbstractSittableBlock || this.getWorld().getBlockState(this.getBlockPos()).getBlock() instanceof BasicBathtubBlock){
                super.tick();
            }
            else {
                if (!this.getWorld().isClient){
                    this.removeAllPassengers();
                    this.discard();
                }
            }
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        this.setVelocity(Vec3d.ZERO);
    }

    @Override
    public boolean isAlive() {
        return !this.isRemoved();
    }


    public ActionResult interactAt(PlayerEntity player, Vec3d hitPos, Hand hand) {
        return super.interactAt(player, hitPos, hand);
    }

    @Override
    public boolean isPushedByFluids() {
        return false;
    }

    @Override
    public Vec3d updatePassengerForDismount(LivingEntity passenger) {
        Direction direction = this.getMovementDirection();
        if (this.getWorld().getBlockState(this.getBlockPos()).getBlock() instanceof AbstractSittableBlock) {
            direction = this.getWorld().getBlockState(this.getBlockPos()).get(AbstractSittableBlock.FACING).getOpposite();
        }
        if (direction.getAxis() != Direction.Axis.Y) {
            int[][] dismountingOffsets = Dismounting.getDismountOffsets(direction);
            BlockPos chairPos = this.getBlockPos();
            BlockPos.Mutable dismountPos = new BlockPos.Mutable();

            for (EntityPose entityPose : passenger.getPoses()) {
                Box box = passenger.getBoundingBox(entityPose);
                for (int[] dismountingOffset : dismountingOffsets) {
                    dismountPos.set(chairPos.getX() + dismountingOffset[0], chairPos.getY() + 0.3, chairPos.getZ() + dismountingOffset[1]);
                    double dismountHeight = this.getWorld().getDismountHeight(dismountPos);
                    if (Dismounting.canDismountInBlock(dismountHeight)) {
                        Vec3d vec3d = Vec3d.ofCenter(dismountPos, dismountHeight);
                        if (Dismounting.canPlaceEntityAt(this.getWorld(), passenger, box.offset(vec3d))) {
                            passenger.setPose(entityPose);
                            return vec3d;
                        }
                    }
                }
            }
        }
        return super.updatePassengerForDismount(passenger);
    }

    public static DefaultAttributeContainer.Builder createMobAttributes(){
        return MobEntity.createMobAttributes().add(EntityAttributes.MAX_HEALTH, 0);
    }

    @Override
    public boolean shouldDismountUnderwater() {
        return false;
    }
}
