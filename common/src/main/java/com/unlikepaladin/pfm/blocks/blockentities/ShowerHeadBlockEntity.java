package com.unlikepaladin.pfm.blocks.blockentities;

import com.unlikepaladin.pfm.registry.BlockEntities;
import com.unlikepaladin.pfm.registry.ParticleIDs;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.Random;

public class ShowerHeadBlockEntity extends BlockEntity {
    public ShowerHeadBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntities.SHOWER_HEAD_BLOCK_ENTITY, pos, state);
    }
    protected boolean isOpen = false;
    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return super.toInitialChunkDataNbt();
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putBoolean("isOpen", isOpen);
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        isOpen = nbt.getBoolean("isOpen");
        super.readNbt(nbt);
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean isOpen) {
        this.isOpen = isOpen;
    }

    public static void tick(World world, BlockPos pos, BlockState state, ShowerHeadBlockEntity blockEntity) {
        if (blockEntity.isOpen && world.isClient) {
            spawnParticles(blockEntity.getCachedState().get(Properties.HORIZONTAL_FACING), blockEntity.world, blockEntity.getPos());
        }
        if (blockEntity.isOpen) {
            world.playSound(null, pos, SoundEvents.WEATHER_RAIN, SoundCategory.BLOCKS, 0.1f, 8.0f);
        }
    }

    public static void spawnParticles(Direction facing, World world, BlockPos pos) {
        if (world.isClient) {
            int x = pos.getX(), y = pos.getY(), z = pos.getZ();
            if (facing == Direction.WEST) {
                addShowerParticles(world, pos, new float[]{0.55f, 0.2f, 0.5f}, new float[]{0.1f, 0f, 0.1f});
            }
            else if (facing == Direction.NORTH){
                addShowerParticles(world, pos, new float[]{0.5f, 0.2f, 0.55f}, new float[]{0.1f, 0f, 0.1f});
            }
            else if (facing == Direction.SOUTH){
                addShowerParticles(world, pos, new float[]{0.5f, 0.2f, 0.45f}, new float[]{0.1f, 0f, 0.1f});
            }
            else {
                addShowerParticles(world, pos, new float[]{0.45f, 0.2f, 0.5f}, new float[]{0.1f, 0f, 0.1f});
            }
        }
    }

    public static void addShowerParticles(World world, BlockPos pos, float[] offset, float[] difference) {
        int x = pos.getX(), y = pos.getY(), z = pos.getZ();
        Random rand = world.random;
        if (rand.nextBoolean()) {
            world.addParticle(ParticleIDs.WATER_DROP, true, x + (offset[0] - difference[0]), y + (offset[1] - difference[1]), z + (offset[2]), 0.0, 0.0, 0.0);
        } else {
            world.addParticle(ParticleIDs.WATER_DROP, true, x + (offset[0] - difference[0]), y + (offset[1] - difference[1]), z + (offset[2] - difference[2]), 0.0, 0.0, 0.0);
        }

        if (rand.nextBoolean()) {
            world.addParticle(ParticleIDs.WATER_DROP, true, x + (offset[0] - difference[0]), y + (offset[1] - difference[1]), z + (offset[2] + difference[2]), 0.0, 0.0, 0.0);
        } else {
            world.addParticle(ParticleIDs.WATER_DROP, true, x + (offset[0]), y + (offset[1] - difference[1]), z + (offset[2]), 0.0, 0.0, 0.0);
            world.addParticle(ParticleIDs.WATER_DROP, true, x + (offset[0]), y + (offset[1] - difference[1]), z + (offset[2] - difference[2]), 0.0, 0.0, 0.0);
       }

        if (rand.nextBoolean()) {
            world.addParticle(ParticleIDs.WATER_DROP, true, x + (offset[0]), y + (offset[1] - difference[1]), z + (offset[2] + difference[2]), 0.0, 0.0, 0.0);
            world.addParticle(ParticleIDs.WATER_DROP, true, x + (offset[0] + difference[0]), y + (offset[1] - difference[1]), z + (offset[2]), 0.0, 0.0, 0.0);
        } else {
            world.addParticle(ParticleIDs.WATER_DROP, true, x + (offset[0] + difference[0]), y + (offset[1] - difference[1]), z + (offset[2] - difference[2]), 0.0, 0.0, 0.0);
            world.addParticle(ParticleIDs.WATER_DROP, true, x + (offset[0] + difference[0]), y + (offset[1] - difference[1]), z + (offset[2] + difference[2]), 0.0, 0.0, 0.0);
        }
    }

    protected NbtCompound saveInitialChunkData(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putBoolean("isOpen", isOpen);
        return nbt;
    }

    @ExpectPlatform
    public static BlockEntityType.BlockEntityFactory<? extends ShowerHeadBlockEntity> getFactory() {
        throw new UnsupportedOperationException();
    }
}
