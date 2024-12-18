package com.unlikepaladin.pfm.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.*;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static com.unlikepaladin.pfm.blocks.KitchenDrawerBlock.rotateShape;

public class IronFridgeBlock extends FridgeBlock {
    private Supplier<FreezerBlock> freezer;
    public IronFridgeBlock(Settings settings, Supplier<FreezerBlock> freezer) {
        super(settings, freezer);
        this.freezer = freezer;
    }

    protected static final Map<Direction, VoxelShape> FRIDGE_SINGLE = new HashMap<>(){{put(Direction.NORTH, VoxelShapes.union(createCuboidShape(12, 2, -0.5,13, 15, 1),createCuboidShape(1, 1, 1,15, 16, 2),createCuboidShape(1, 0, 2,15, 16, 16)));}};
    protected static final Map<Direction, VoxelShape> FRIDGE_TOP = new HashMap<>(){{put(Direction.NORTH, VoxelShapes.union(createCuboidShape(12, 0, -0.5,13, 15, 1),createCuboidShape(1, 0, 1,15, 16, 16)));}};
    protected static final Map<Direction, VoxelShape> FRIDGE_MIDDLE = new HashMap<>(){{put(Direction.NORTH, VoxelShapes.union(createCuboidShape(12, 0, -0.5,13, 16, 1),createCuboidShape(1, 0, 1,15, 16, 16)));}};
    protected static final Map<Direction, VoxelShape> FRIDGE_BOTTOM = new HashMap<>(){{put(Direction.NORTH, VoxelShapes.union(createCuboidShape(12, 2, -0.5,13, 16, 1),createCuboidShape(1, 1, 1,15, 16, 2),createCuboidShape(1, 0, 2,15, 16, 16)));}};
    protected static final Map<Direction, VoxelShape> FRIDGE = new HashMap<>(){{put(Direction.NORTH, VoxelShapes.union(createCuboidShape(12, 1, -0.5, 13, 15, 1), createCuboidShape(1, 0, 1,15, 16, 2),createCuboidShape(1, -16, 2,15, 16, 16)));}};

    protected static final Map<Direction, VoxelShape> FRIDGE_SINGLE_OPEN = new HashMap<>(){{put(Direction.NORTH, VoxelShapes.union(createCuboidShape(-0.5, 2, -10,1, 15, -9),createCuboidShape(1, 1, -12,3, 16, 3),createCuboidShape(1, 1, 3,15, 16, 16),createCuboidShape(1, 0, 2,15, 1, 16)));}};
    protected static final Map<Direction, VoxelShape> FRIDGE_TOP_OPEN = new HashMap<>(){{put(Direction.NORTH, VoxelShapes.union(createCuboidShape(-0.5, 0, -10,1, 15, -9),createCuboidShape(1, 0, -12,3, 16, 3),createCuboidShape(1, 0, 3,15, 16, 16)));}};
    protected static final Map<Direction, VoxelShape> FRIDGE_MIDDLE_OPEN = new HashMap<>(){{put(Direction.NORTH, VoxelShapes.union(createCuboidShape(-0.5, 0, -10,1, 16, -9),createCuboidShape(1, 0, -12,3, 16, 3),createCuboidShape(1, 0, 3,15, 16, 16)));}};
    protected static final Map<Direction, VoxelShape> FRIDGE_BOTTOM_OPEN = new HashMap<>(){{put(Direction.NORTH, VoxelShapes.union(createCuboidShape(-0.5, 2, -10, 1, 16, -9),createCuboidShape(1, 1, -12,3, 16, 3),createCuboidShape(1, 1, 3,15, 16, 16),createCuboidShape(1, 0, 2,15, 1, 16)));}};
    protected static final Map<Direction, VoxelShape> FRIDGE_OPEN = new HashMap<>(){{put(Direction.NORTH, VoxelShapes.union(createCuboidShape(1, 1, -12,3, 16, 2),createCuboidShape(-0.5, 1, -10,1, 15, -9),createCuboidShape(2, 0, 1,15, 1, 2),createCuboidShape(1, -16, 2,15, 16, 16)));}};


    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Direction dir = state.get(FACING).getOpposite();
        Boolean open = state.get(OPEN);
        boolean bottom = state.isOf(world.getBlockState(pos.up()).getBlock());
        boolean top = state.isOf(world.getBlockState(pos.down()).getBlock());
        boolean hasFreezer = world.getBlockState(pos.down()).getBlock() instanceof IronFreezerBlock;

        if (top && bottom) {
            if (open) {
                if (!FRIDGE_MIDDLE_OPEN.containsKey(dir))
                    FRIDGE_MIDDLE_OPEN.put(dir, rotateShape(Direction.NORTH, dir, FRIDGE_MIDDLE_OPEN.get(Direction.NORTH)));
                return FRIDGE_MIDDLE_OPEN.get(dir);
            }
            if (!FRIDGE_MIDDLE.containsKey(dir))
                FRIDGE_MIDDLE.put(dir, rotateShape(Direction.NORTH, dir, FRIDGE_MIDDLE.get(Direction.NORTH)));
            return FRIDGE_MIDDLE.get(dir);
        } else if (bottom) {
            if (open) {
                if (!FRIDGE_BOTTOM_OPEN.containsKey(dir))
                    FRIDGE_BOTTOM_OPEN.put(dir, rotateShape(Direction.NORTH, dir, FRIDGE_BOTTOM_OPEN.get(Direction.NORTH)));
                return FRIDGE_BOTTOM_OPEN.get(dir);
            }
            if (!FRIDGE_BOTTOM.containsKey(dir))
                FRIDGE_BOTTOM.put(dir, rotateShape(Direction.NORTH, dir, FRIDGE_BOTTOM.get(Direction.NORTH)));
            return FRIDGE_BOTTOM.get(dir);
        } else if (top) {
            if (open) {
                if (!FRIDGE_TOP_OPEN.containsKey(dir))
                    FRIDGE_TOP_OPEN.put(dir, rotateShape(Direction.NORTH, dir, FRIDGE_TOP_OPEN.get(Direction.NORTH)));
                return FRIDGE_TOP_OPEN.get(dir);
            }
            if (!FRIDGE_TOP.containsKey(dir))
                FRIDGE_TOP.put(dir, rotateShape(Direction.NORTH, dir, FRIDGE_TOP.get(Direction.NORTH)));
            return FRIDGE_TOP.get(dir);
        } else if (hasFreezer) {
            if (open) {
                if (!FRIDGE_OPEN.containsKey(dir))
                    FRIDGE_OPEN.put(dir, rotateShape(Direction.NORTH, dir, FRIDGE_OPEN.get(Direction.NORTH)));
                return FRIDGE_OPEN.get(dir);
            }
            if (!FRIDGE.containsKey(dir))
                FRIDGE.put(dir, rotateShape(Direction.NORTH, dir, FRIDGE.get(Direction.NORTH)));
            return FRIDGE.get(dir);
        } else {
            if (open) {
                if (!FRIDGE_SINGLE_OPEN.containsKey(dir))
                    FRIDGE_SINGLE_OPEN.put(dir, rotateShape(Direction.NORTH, dir, FRIDGE_SINGLE_OPEN.get(Direction.NORTH)));
                return FRIDGE_SINGLE_OPEN.get(dir);
            }
            if (!FRIDGE_SINGLE.containsKey(dir))
                FRIDGE_SINGLE.put(dir, rotateShape(Direction.NORTH, dir, FRIDGE_SINGLE.get(Direction.NORTH)));
            return FRIDGE_SINGLE.get(dir);
        }
    }

    public void onBreakInCreative(World world, BlockPos pos, BlockState state, PlayerEntity player) {
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return super.canPlaceAt(state, world, pos);
    }

    @Override
    public void onBroken(WorldAccess world, BlockPos pos, BlockState state) {
        super.onBroken(world, pos, state);
    }
}
