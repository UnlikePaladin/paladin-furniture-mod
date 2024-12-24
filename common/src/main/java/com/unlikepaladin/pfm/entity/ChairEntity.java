package com.unlikepaladin.pfm.entity;

import com.google.common.collect.UnmodifiableIterator;
import com.unlikepaladin.pfm.blocks.AbstractSittableBlock;
import com.unlikepaladin.pfm.blocks.BasicBathtubBlock;
import com.unlikepaladin.pfm.blocks.BasicToiletBlock;
import com.unlikepaladin.pfm.blocks.ToiletState;
import com.unlikepaladin.pfm.client.PaladinFurnitureModClient;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

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
                if (!this.world.isClient){
                    this.remove();
                }
            }
            else if (this.world.getBlockState(this.getBlockPos()).getBlock() instanceof BasicToiletBlock && world.isClient()){
                if (PaladinFurnitureModClient.USE_TOILET_KEYBIND.isPressed() && this.world.getBlockState(this.getBlockPos()).get(BasicToiletBlock.TOILET_STATE) == ToiletState.CLEAN) {
                    fart(this.getBlockPos());
                }
                super.tick();
            }
            else if (this.world.getBlockState(this.getBlockPos()).getBlock() instanceof AbstractSittableBlock || this.world.getBlockState(this.getBlockPos()).getBlock() instanceof BasicBathtubBlock){
                super.tick();
            }
            else {
                if (!this.world.isClient){
                    this.removeAllPassengers();
                    this.remove();
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
        return !this.removed;
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
        if (this.world.getBlockState(this.getBlockPos()).getBlock() instanceof AbstractSittableBlock) {
            direction = this.world.getBlockState(this.getBlockPos()).get(AbstractSittableBlock.FACING).getOpposite();
        }
        if (direction.getAxis() != Direction.Axis.Y) {
            int[][] dismountingOffsets = Dismounting.getDismountOffsets(direction);
            BlockPos chairPos = this.getBlockPos();
            BlockPos.Mutable dismountPos = new BlockPos.Mutable();

            for (EntityPose entityPose : passenger.getPoses()) {
                Box box = passenger.getBoundingBox(entityPose);
                for (int[] dismountingOffset : dismountingOffsets) {
                    dismountPos.set(chairPos.getX() + dismountingOffset[0], chairPos.getY() + 0.3, chairPos.getZ() + dismountingOffset[1]);
                    double dismountHeight = this.world.getDismountHeight(dismountPos);
                    if (Dismounting.canDismountInBlock(dismountHeight)) {
                        Vec3d vec3d = Vec3d.ofCenter(dismountPos, dismountHeight);
                        if (Dismounting.canPlaceEntityAt(this.world, passenger, box.offset(vec3d))) {
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
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 0);
    }

    @Nullable
    @Override
    public Entity getPrimaryPassenger() {
        return !this.getPassengerList().isEmpty() ? this.getPassengerList().get(0) : null;
    }

    @Override
    public boolean canBeRiddenInWater() {
        return true;
    }
}
