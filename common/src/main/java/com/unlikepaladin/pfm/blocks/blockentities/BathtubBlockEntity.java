package com.unlikepaladin.pfm.blocks.blockentities;

import com.unlikepaladin.pfm.blocks.BasicBathtubBlock;
import com.unlikepaladin.pfm.blocks.KitchenSinkBlock;
import com.unlikepaladin.pfm.registry.BlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BedBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BathtubBlockEntity extends BedBlockEntity {
    public BathtubBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state, DyeColor.WHITE);
    }

    @Override
    public BlockEntityType<?> getType() {
        return BlockEntities.BATHTUB_BLOCK_ENTITY;
    }

    private int fillTimer = 0;
    private boolean isFilling = false;

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return this.writeIdentifyingTubData(new NbtCompound());
    }

    private NbtCompound writeIdentifyingTubData(NbtCompound nbt) {
        Identifier identifier = BlockEntityType.getId(this.getType());
        if (identifier == null) {
            throw new RuntimeException(this.getClass() + " is missing a mapping! This is a bug!");
        }
        nbt.putString("id", identifier.toString());
        nbt.putInt("x", this.pos.getX());
        nbt.putInt("y", this.pos.getY());
        nbt.putInt("z", this.pos.getZ());
        nbt.putInt("tubTimer", this.fillTimer);
        nbt.putBoolean("isTubFilling", this.isFilling);
        return nbt;
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        fillTimer = nbt.getInt("tubTimer");
        isFilling = nbt.getBoolean("isTubFilling");
        super.readNbt(nbt, registryLookup);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.putInt("tubTimer", fillTimer);
        nbt.putBoolean("isTubFilling", isFilling);
    }

    public void setFillTimer(int fillTimer) {
        this.fillTimer = fillTimer;
    }

    public void setFilling(boolean isFilling) {
        if (isFilling){
            world.playSound(null, pos, SoundEvents.BLOCK_WATER_AMBIENT, SoundCategory.BLOCKS, 0.7f, 1.0f);
        }
        this.isFilling = isFilling;
    }

    public static void tick(World world, BlockPos pos, BlockState state, BathtubBlockEntity blockEntity) {
        if (blockEntity.isFilling) {
            if (blockEntity.fillTimer >= 30) {
                blockEntity.setFillTimer(0);
                blockEntity.setFilling(false);
            } else {
                if (world.isClient) {
                    BasicBathtubBlock.spawnParticles(blockEntity.getCachedState().get(Properties.HORIZONTAL_FACING), blockEntity.world, blockEntity.getPos());
                }
                blockEntity.fillTimer++;
            }
        }
    }

    @Override
    public boolean supports(BlockState state) {
        return getType().supports(state);
    }
}
