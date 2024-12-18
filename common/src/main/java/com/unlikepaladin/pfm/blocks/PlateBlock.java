package com.unlikepaladin.pfm.blocks;

import com.mojang.serialization.MapCodec;
import com.unlikepaladin.pfm.blocks.blockentities.PlateBlockEntity;
import com.unlikepaladin.pfm.data.FurnitureBlock;
import com.unlikepaladin.pfm.registry.PaladinFurnitureModBlocksItems;
import com.unlikepaladin.pfm.registry.Statistics;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import static com.unlikepaladin.pfm.blocks.KitchenDrawerBlock.rotateShape;

public class PlateBlock extends HorizontalFacingBlockWithEntity {

    public static final BooleanProperty CUTLERY = BooleanProperty.of("cutlery");

    private static final List<FurnitureBlock> PLATES = new ArrayList<>();
    public static final MapCodec<PlateBlock> CODEC = createCodec(PlateBlock::new);

    public PlateBlock(AbstractBlock.Settings settings) {
        super(settings);
        setDefaultState(this.getStateManager().getDefaultState().with(FACING, Direction.NORTH).with(CUTLERY, false));
        PLATES.add(new FurnitureBlock(this, "plate"));
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    public static Stream<FurnitureBlock> streamPlates() {
        return PLATES.stream();
    }

    @Override
    protected ActionResult onUseWithItem(ItemStack itemStack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        PlateBlockEntity plateBlockEntity;
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof PlateBlockEntity && (itemStack.contains(DataComponentTypes.FOOD))) {
            if (!world.isClient && ((PlateBlockEntity)blockEntity).addItem(player.getAbilities().creativeMode ? itemStack.copy() : itemStack)) {
                player.incrementStat(Statistics.PLATE_USED);
                return ActionResult.SUCCESS;
            }
            return ActionResult.CONSUME;
        }
        if(Registries.BLOCK.get(Registries.ITEM.getId(itemStack.getItem())) instanceof CutleryBlock) {
            world.setBlockState(pos, state.with(CUTLERY, true));
            itemStack.decrement(1);
            return ActionResult.SUCCESS;
        }
        if (player.isSneaking() && blockEntity instanceof PlateBlockEntity) {
            plateBlockEntity = (PlateBlockEntity)blockEntity;
            if (!plateBlockEntity.getItemInPlate().isEmpty()) {
                if (!world.isClient) {
                    ItemEntity itemEntity = new ItemEntity(world, pos.getX() + 0.5D, pos.getY() + 0.8D, pos.getZ() + 0.5D, plateBlockEntity.removeItem());
                    world.spawnEntity(itemEntity);
                    player.incrementStat(Statistics.PLATE_USED);
                    return ActionResult.SUCCESS;
                }
                return ActionResult.CONSUME;
            }
        }
        if(blockEntity instanceof PlateBlockEntity){
            plateBlockEntity = (PlateBlockEntity)blockEntity;
                if (!plateBlockEntity.getItemInPlate().isEmpty()) {
                    ItemStack stack = plateBlockEntity.getItemInPlate();
                    spawnItemParticles(player, stack, 16);
                    if (Registries.ITEM.getId(stack.getItem()).toString().equals("sandwichable:sandwich")) {
                       eatSandwich(stack, world, player);
                    }
                    else {
                        if (!player.isCreative()) {
                            ItemEntity itemEntity = new ItemEntity(world, pos.getX() + 0.5D, pos.getY() + 0.8D, pos.getZ() + 0.5D, stack.finishUsing(world, player));
                            world.spawnEntity(itemEntity);
                        }
                    }
                    plateBlockEntity.removeItem();
                    player.incrementStat(Statistics.PLATE_USED);
                    return ActionResult.SUCCESS;
                }
        }
        return super.onUseWithItem(itemStack, state, world, pos, player, hand, hit);
    }

    @ExpectPlatform
    public static void eatSandwich(ItemStack stack, World world, PlayerEntity player) {

    }
    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (state.get(CUTLERY) && !player.getAbilities().creativeMode) {
            ItemEntity itemEntity = new ItemEntity( world, pos.getX() + 0.5D, pos.getY() + 0.8D, pos.getZ() + 0.5D, new ItemStack(PaladinFurnitureModBlocksItems.BASIC_CUTLERY, 1));
            world.spawnEntity(itemEntity);
        }
        return super.onBreak(world, pos, state, player);
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
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        super.appendProperties(stateManager);
        stateManager.add(CUTLERY);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing());
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, net.minecraft.util.math.random.Random random) {
        if (!state.canPlaceAt(world, pos)) {
            if (world instanceof World && state.get(CUTLERY)) {
                ItemEntity itemEntity = new ItemEntity((World) world, pos.getX() + 0.5D, pos.getY() + 0.8D, pos.getZ() + 0.5D, new ItemStack(PaladinFurnitureModBlocksItems.BASIC_CUTLERY, 1));
                ((World) world).spawnEntity(itemEntity);
            }
            return Blocks.AIR.getDefaultState();
        }
        return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    protected static final VoxelShape PLATE = VoxelShapes.union(createCuboidShape(2,0,3, 12,1,13));
    protected static final VoxelShape PLATE_SOUTH = rotateShape(Direction.NORTH, Direction.SOUTH, PLATE);
    protected static final VoxelShape PLATE_EAST = rotateShape(Direction.NORTH, Direction.EAST, PLATE);
    protected static final VoxelShape PLATE_WEST = rotateShape(Direction.NORTH, Direction.WEST, PLATE);
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
        Direction dir = state.get(FACING);
        return switch (dir) {
            case WEST -> PLATE_SOUTH;
            case NORTH -> PLATE_WEST;
            case SOUTH -> PLATE_EAST;
            default -> PLATE;
        };
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.isOf(newState.getBlock())) {
            return;
        }
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof PlateBlockEntity plateBlockEntity) {
            ItemScatterer.spawn(world, pos, plateBlockEntity.getInventory());
            world.updateComparators(pos, this);
            plateBlockEntity.markRemoved();
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    protected final Random random = new Random();
    private void spawnItemParticles(LivingEntity entity, ItemStack stack, int count) {
        for (int i = 0; i < count; ++i) {
            Vec3d vec3d = new Vec3d(((double)this.random.nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0);
            vec3d = vec3d.rotateX(-entity.getPitch() * ((float)Math.PI / 180));
            vec3d = vec3d.rotateY(-entity.getYaw() * ((float)Math.PI / 180));
            double d = (double)(-this.random.nextFloat()) * 0.6 - 0.3;
            Vec3d vec3d2 = new Vec3d(((double)this.random.nextFloat() - 0.5) * 0.3, d, 0.6);
            vec3d2 = vec3d2.rotateX(-entity.getPitch() * ((float)Math.PI / 180));
            vec3d2 = vec3d2.rotateY(-entity.getYaw() * ((float)Math.PI / 180));
            vec3d2 = vec3d2.add(entity.getX(), entity.getEyeY(), entity.getZ());
            entity.getEntityWorld().addParticle(new ItemStackParticleEffect(ParticleTypes.ITEM, stack), vec3d2.x, vec3d2.y, vec3d2.z, vec3d.x, vec3d.y + 0.05, vec3d.z);
        }
    }
    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        Direction direction = Direction.DOWN;
        return Block.sideCoversSmallSquare(world, pos.offset(direction), direction.getOpposite());
    }
}
