package com.unlikepaladin.pfm.blocks;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.Direction;

public abstract class HorizontalFacingBlockWithEntity extends BlockWithEntity {

    public static final EnumProperty<Direction> FACING;

    protected HorizontalFacingBlockWithEntity(AbstractBlock.Settings settings) {
        super(settings.luminance((state) -> 0).emissiveLighting((blockstate, b, c) -> false));
    }

    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    static {
        FACING = Properties.HORIZONTAL_FACING;
    }
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(FACING);
    }

}
