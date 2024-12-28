package com.unlikepaladin.pfm.blocks;

import com.mojang.serialization.MapCodec;
import com.unlikepaladin.pfm.blocks.blockentities.LampBlockEntity;
import com.unlikepaladin.pfm.data.materials.WoodVariant;
import com.unlikepaladin.pfm.data.materials.WoodVariantRegistry;
import com.unlikepaladin.pfm.items.LightSwitchItem;
import com.unlikepaladin.pfm.items.PFMComponents;
import com.unlikepaladin.pfm.registry.PaladinFurnitureModBlocksItems;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
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
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public class BasicLampBlock extends PowerableBlock implements BlockEntityProvider {
    private static final BooleanProperty LIT = Properties.LIT;
    public static final MapCodec<BasicLampBlock> CODEC = createCodec(BasicLampBlock::new);

    public BasicLampBlock(AbstractBlock.Settings settings) {
        super(settings);
        setDefaultState(this.getStateManager().getDefaultState().with(LIT, false).with(POWERLOCKED, false));
    }

    @Override
    protected MapCodec<? extends PowerableBlock> getCodec() {
        return CODEC;
    }

    @Override
    public void setPowered(World world, BlockPos lightPos, boolean powered) {
        BlockState state = world.getBlockState(lightPos);
        world.setBlockState(lightPos, state.with(LIT, powered).with(POWERLOCKED,powered));
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof LampBlockEntity) {
            DyeColor color = itemStack.getOrDefault(PFMComponents.COLOR_COMPONENT, DyeColor.WHITE);
            WoodVariant variant = WoodVariantRegistry.getVariant(itemStack.getOrDefault(PFMComponents.VARIANT_COMPONENT, WoodVariantRegistry.OAK.identifier));
            ((LampBlockEntity) blockEntity).setPFMColor(color);
            ((LampBlockEntity) blockEntity).setVariant(variant);
        }
        super.onPlaced(world, pos, state, placer, itemStack);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        super.appendProperties(stateManager);
        stateManager.add(LIT);
        stateManager.add(POWERLOCKED);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return getBlockEntity(pos, state);
    }

    @ExpectPlatform
    public static BlockEntity getBlockEntity(BlockPos pos, BlockState state) {
        return null;
    }

    @Override
    public boolean isShapeFullCube(BlockState state, BlockView world, BlockPos pos) {
        return false;
    }

    @Override
    public boolean canPathfindThrough(BlockState state, NavigationType type) {
        return false;
    }

    @Override
    public MapColor getDefaultMapColor() {
        return super.getDefaultMapColor();
    }

    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof LampBlockEntity lampBlockEntity) {
            if (!world.isClient && !player.isCreative()) {
                ItemStack itemStack = new ItemStack(PaladinFurnitureModBlocksItems.BASIC_LAMP);
                itemStack.set(PFMComponents.VARIANT_COMPONENT, lampBlockEntity.getVariant().identifier);
                itemStack.set(PFMComponents.COLOR_COMPONENT, lampBlockEntity.getPFMColor());
                ItemEntity itemEntity = new ItemEntity(world, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, itemStack);
                itemEntity.setToDefaultPickupDelay();
                world.spawnEntity(itemEntity);
            }
        }

        return super.onBreak(world, pos, state, player);
    }

    private static final VoxelShape SINGLE = VoxelShapes.union(createCuboidShape(7, 1.5, 7, 9, 6, 9), createCuboidShape(3, 0, 3,13, 1.5, 13),createCuboidShape(1.5, 5, 1.5,14.5, 16, 14.5));
    private static final VoxelShape TOP = VoxelShapes.union(createCuboidShape(7, 0, 7,9, 6, 9),createCuboidShape(1.5, 5, 1.5,14.5, 16, 14.5));
    private static final VoxelShape MIDDLE = VoxelShapes.union(createCuboidShape(7, 0, 7,9, 16, 9));
    private static final VoxelShape BOTTOM = VoxelShapes.union(createCuboidShape(7, 1.5, 7, 9, 16, 9), createCuboidShape(3, 0, 3,13, 1.5, 13));
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        boolean up = world.getBlockState(pos.up()).getBlock() instanceof BasicLampBlock;
        boolean down = world.getBlockState(pos.down()).getBlock() instanceof BasicLampBlock;
        if (up && down) {
            return MIDDLE;
        } else if (up) {
            return BOTTOM;
        } else if (down)
        {
            return TOP;
        }
        return SINGLE;
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (stack.getItem() instanceof LightSwitchItem)
            return ItemActionResult.SKIP_DEFAULT_BLOCK_INTERACTION;

        return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }

        if (!state.get(POWERLOCKED)) {
            togglePower(state, world, pos);
            float f = state.get(LIT) ? 0.9f : 0.8f;
            world.playSound(null, pos, SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.BLOCKS, 0.3f, f);
            world.emitGameEvent(player, state.get(LIT) ? GameEvent.BLOCK_ACTIVATE : GameEvent.BLOCK_DEACTIVATE, pos);
            return ActionResult.CONSUME;
        }
        return super.onUse(state, world, pos, player, hit);
    }

    public BlockState togglePower(BlockState state, World world, BlockPos pos) {
        state = state.cycle(LIT);
        world.setBlockState(pos, state, Block.NOTIFY_ALL);
        world.updateNeighborsAlways(pos, this);
        return state;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        return direction.getAxis().isVertical() && neighborState.getBlock() instanceof BasicLampBlock ? state.with(LIT, neighborState.get(LIT)).with(POWERLOCKED, neighborState.get(POWERLOCKED)) : super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state) {
        ItemStack stack = super.getPickStack(world, pos, state);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof LampBlockEntity) {
            stack.set(PFMComponents.VARIANT_COMPONENT, ((LampBlockEntity) blockEntity).getVariant().identifier);
            stack.set(PFMComponents.COLOR_COMPONENT, ((LampBlockEntity) blockEntity).getPFMColor());
        }
        return stack;
    }
}
