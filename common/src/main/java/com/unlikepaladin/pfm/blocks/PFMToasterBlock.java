package com.unlikepaladin.pfm.blocks;

import com.mojang.serialization.MapCodec;
import com.unlikepaladin.pfm.blocks.blockentities.PFMToasterBlockEntity;
import com.unlikepaladin.pfm.registry.BlockEntities;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.block.WireOrientation;
import org.jetbrains.annotations.Nullable;

import static com.unlikepaladin.pfm.blocks.LogTableBlock.rotateShape;

public class PFMToasterBlock extends HorizontalFacingBlockWithEntity {
    public static final BooleanProperty ON = BooleanProperty.of("on");
    public static final MapCodec<PFMToasterBlock> CODEC = createCodec(PFMToasterBlock::new);
    public PFMToasterBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(FACING, ON);
    }

    public static final VoxelShape IRON_TOASTER = VoxelShapes.union(createCuboidShape(5, 0, 3,11, 7, 13));
    public static final VoxelShape IRON_TOASTER_WEST_EAST = rotateShape(Direction.NORTH, Direction.WEST, IRON_TOASTER);
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext ctx) {
        Direction dir = state.get(FACING);
        return switch (dir) {
            case EAST, WEST -> IRON_TOASTER_WEST_EAST;
            default -> IRON_TOASTER;
        };
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return PFMToasterBlockEntity.getFactory().create(pos, state);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return BasicToiletBlock.checkType(type, BlockEntities.TOASTER_BLOCK_ENTITY, PFMToasterBlockEntity::tick);
    }


    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return world.getBlockEntity(pos) instanceof PFMToasterBlockEntity ? ((PFMToasterBlockEntity)world.getBlockEntity(pos)).getComparatorOutput() : 0;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(Properties.HORIZONTAL_FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(Properties.HORIZONTAL_FACING, rotation.rotate(state.get(Properties.HORIZONTAL_FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(Properties.HORIZONTAL_FACING)));
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
        super.neighborUpdate(state, world, pos, sourceBlock, wireOrientation, notify);
        if (world.getBlockEntity(pos) instanceof PFMToasterBlockEntity) {
            boolean toasting = ((PFMToasterBlockEntity)world.getBlockEntity(pos)).isToasting();
            world.setBlockState(pos, world.getBlockState(pos).with(ON, toasting));
        }
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        if (world.getBlockEntity(pos) instanceof PFMToasterBlockEntity) {
            boolean toasting = ((PFMToasterBlockEntity)world.getBlockEntity(pos)).isToasting();
            world.setBlockState(pos, world.getBlockState(pos).with(ON, toasting));
        }

    }
    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof PFMToasterBlockEntity) {
                PFMToasterBlockEntity blockEntity = (PFMToasterBlockEntity)world.getBlockEntity(pos);

                for(int i = 0; i < 2; ++i) {
                    ItemEntity item = new ItemEntity(world, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, blockEntity.getItems().get(i));
                    world.spawnEntity(item);
                }

                world.updateNeighbors(pos, this);
            }

            super.onStateReplaced(state, world, pos, newState, moved);
        }

    }

    @Override
    protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.getBlockEntity(pos) instanceof PFMToasterBlockEntity) {
            PFMToasterBlockEntity blockEntity = (PFMToasterBlockEntity) world.getBlockEntity(pos);
            if (!player.isSneaking()) {
                if (!blockEntity.isToasting()) {
                    ItemEntity itemEntity;
                    if (!stack.isEmpty() && !isSandwich(stack)) {
                        if (!blockEntity.addItem(hand, player)) {
                            itemEntity = new ItemEntity(world, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.8D, (double)pos.getZ() + 0.5D, blockEntity.takeItem(player));
                            world.spawnEntity(itemEntity);
                        }
                    } else {
                        itemEntity = new ItemEntity(world, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.8D, (double)pos.getZ() + 0.5D, blockEntity.takeItem(player));
                        world.spawnEntity(itemEntity);
                    }
                }
                PFMToasterBlockEntity.sync(blockEntity, blockEntity.getWorld());
            } else if (!blockEntity.isToasting()) {
                blockEntity.startToasting(player);
            } else {
                blockEntity.stopToasting(player);
            }
        }

        return ActionResult.SUCCESS;
    }

    @ExpectPlatform
    public static boolean isSandwich(ItemStack stack) {
        throw new AssertionError();
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

}
