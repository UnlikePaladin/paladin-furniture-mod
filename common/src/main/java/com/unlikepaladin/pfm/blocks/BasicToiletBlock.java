package com.unlikepaladin.pfm.blocks;

import com.unlikepaladin.pfm.blocks.blockentities.ToiletBlockEntity;
import com.unlikepaladin.pfm.data.FurnitureBlock;
import com.unlikepaladin.pfm.entity.ChairEntity;
import com.unlikepaladin.pfm.registry.SoundIDs;
import com.unlikepaladin.pfm.registry.Statistics;
import com.unlikepaladin.pfm.registry.BlockEntities;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.particle.EntityEffectParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.Potions;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.unlikepaladin.pfm.blocks.DinnerTableBlock.rotateShape;

public class BasicToiletBlock extends AbstractSittableBlock implements BlockEntityProvider {
    private static final List<FurnitureBlock> BASIC_TOILET = new ArrayList<>();
    public static final EnumProperty<ToiletState> TOILET_STATE = EnumProperty.of("toilet", ToiletState.class);
    public BasicToiletBlock(Settings settings) {
        super(settings);
        setDefaultState(this.getStateManager().getDefaultState().with(Properties.HORIZONTAL_FACING, Direction.NORTH).with(TOILET_STATE, ToiletState.EMPTY));
        if(this.getClass().isAssignableFrom(BasicToiletBlock.class)){
            BASIC_TOILET.add(new FurnitureBlock(this, "basic_toilet"));
        }
    }
    public static Stream<FurnitureBlock> streamBasicToilet() {
        return BASIC_TOILET.stream();
    }


    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(TOILET_STATE);
        super.appendProperties(stateManager);
    }

    @Override
    protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            player.incrementStat(Statistics.TOILET_USED);
        }

        if (!world.isClient && state.get(TOILET_STATE) == ToiletState.EMPTY && (stack.getItem() == Items.POTION) && stack.contains(DataComponentTypes.POTION_CONTENTS) && stack.get(DataComponentTypes.POTION_CONTENTS) != null && stack.get(DataComponentTypes.POTION_CONTENTS).matches(Potions.WATER)) {
            world.setBlockState(pos, state.with(TOILET_STATE, ToiletState.CLEAN));
            if (!player.getAbilities().creativeMode)
                player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, new ItemStack(Items.GLASS_BOTTLE)));
            world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0f, 1.0f);
            return ActionResult.SUCCESS;
        } else if (!world.isClient && state.get(TOILET_STATE) == ToiletState.EMPTY && (player.getStackInHand(hand).getItem() == Items.WATER_BUCKET)) {
            world.setBlockState(pos, state.with(TOILET_STATE, ToiletState.CLEAN));
            if (!player.getAbilities().creativeMode)
                player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, new ItemStack(Items.BUCKET)));
            world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0f, 1.0f);
            return ActionResult.SUCCESS;
        }

        return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient && state.get(TOILET_STATE) == ToiletState.EMPTY) {
            BlockPos sourcePos = pos.down().down();
            BlockState sourceState = world.getBlockState(sourcePos);
            if (sourceState.getFluidState().getFluid() == Fluids.WATER && !sourceState.getFluidState().isEmpty()) {
                if (sourceState.getProperties().contains(Properties.WATERLOGGED)) {
                    world.setBlockState(sourcePos, sourceState.with(Properties.WATERLOGGED, false)); }
                else {
                    world.setBlockState(sourcePos, Blocks.AIR.getDefaultState());
                }
                world.setBlockState(pos, state.with(TOILET_STATE, ToiletState.CLEAN));
                return ActionResult.SUCCESS;
            } else if (world.getEntitiesByClass(ChairEntity.class, new Box(pos), Entity::hasPassengers).isEmpty()) {
                player.sendMessage(Text.translatable("message.pfm.toilet_use"), false);
            }
        }
        else if (!world.isClient && (state.get(TOILET_STATE) == ToiletState.DIRTY)) {
            world.setBlockState(pos, state.with(TOILET_STATE, ToiletState.FLUSHING));
            world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundIDs.TOILET_FLUSHING_EVENT, SoundCategory.BLOCKS, 0.3f, 1.0f);
            ToiletBlockEntity blockEntity = (ToiletBlockEntity) world.getBlockEntity(pos);
            blockEntity.setFlushTimer(0);
            return ActionResult.SUCCESS;
        }
        return super.onUse(state, world, pos, player, hit);
    }

    @Override
    public Function<Settings, AbstractSittableBlock> getChairConstructor() {
        return BasicToiletBlock::new;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        super.randomDisplayTick(state, world, pos, random);
        if(state.get(TOILET_STATE) != ToiletState.DIRTY) {
            return;
        }
        double x = pos.getX() + 0.5;
        double y = pos.getY();
        double z = pos.getZ() + 0.5;
        if (random.nextDouble() < 0.2)
            world.addParticle(EntityEffectParticleEffect.create(ParticleTypes.ENTITY_EFFECT, 1), x, y + 0.6, z, 0.8, 1.0, 0.0);
        if (random.nextDouble() < 0.009) {
            world.addParticle(EntityEffectParticleEffect.create(ParticleTypes.ENTITY_EFFECT, 1), x, y + 0.6, z, 0.18, 0.0, 0.34);
        }
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ToiletBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, BlockEntities.TOILET_BLOCK_ENTITY, ToiletBlockEntity::tick);
    }

    public static void setClean(BlockState state, World world, BlockPos pos) {
        BlockPos sourcePos = pos.down().down();
        BlockState sourceState = world.getBlockState(sourcePos);
        if (sourceState.getFluidState().getFluid() == Fluids.WATER && !sourceState.getFluidState().isEmpty()) {
            if (sourceState.getProperties().contains(Properties.WATERLOGGED)) {
                world.setBlockState(sourcePos, sourceState.with(Properties.WATERLOGGED, false)); }
            else {
                world.setBlockState(sourcePos, Blocks.AIR.getDefaultState());
            }
            world.setBlockState(pos, state.with(TOILET_STATE, ToiletState.CLEAN));
            return;
        }
        world.setBlockState(pos, state.with(BasicToiletBlock.TOILET_STATE, ToiletState.EMPTY), NOTIFY_ALL);
    }
    protected static final VoxelShape TOILET_WEST = VoxelShapes.union(createCuboidShape(2, 1, 4.2,14, 6, 11.7),createCuboidShape(1, 0, 3.2,15, 1, 12.7),createCuboidShape(5, 5, 2.5,16, 10, 13.5),createCuboidShape(0, 6, 2.5,5, 20, 13.5),createCuboidShape(5, 8, 3.5,6, 21, 12.5));
    protected static final VoxelShape TOILET_NORTH = rotateShape(Direction.WEST, Direction.NORTH, TOILET_WEST);
    protected static final VoxelShape TOILET_EAST = rotateShape(Direction.WEST, Direction.EAST, TOILET_WEST);
    protected static final VoxelShape TOILET_SOUTH = rotateShape(Direction.WEST, Direction.SOUTH, TOILET_WEST);

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
        Direction dir = state.get(FACING);
        return switch (dir){
            case NORTH -> TOILET_NORTH;
            case SOUTH -> TOILET_SOUTH;
            case EAST -> TOILET_EAST;
            default -> TOILET_WEST;
        };
    }

    @Nullable
    protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> checkType(BlockEntityType<A> givenType, BlockEntityType<E> expectedType, BlockEntityTicker<? super E> ticker) {
        return expectedType == givenType ? (BlockEntityTicker<A>) ticker : null;
    }

}

