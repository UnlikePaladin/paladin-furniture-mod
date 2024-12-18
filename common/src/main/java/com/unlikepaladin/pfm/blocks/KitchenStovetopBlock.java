package com.unlikepaladin.pfm.blocks;

import com.mojang.serialization.MapCodec;
import com.unlikepaladin.pfm.blocks.blockentities.StovetopBlockEntity;
import com.unlikepaladin.pfm.registry.BlockEntities;
import com.unlikepaladin.pfm.registry.Statistics;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CampfireCookingRecipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipePropertySet;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
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
import java.util.Optional;
import java.util.stream.Stream;

import static com.unlikepaladin.pfm.blocks.KitchenDrawerBlock.rotateShape;

public class KitchenStovetopBlock extends HorizontalFacingBlockWithEntity {
    public static final BooleanProperty LIT = Properties.LIT;
    private static final List<KitchenStovetopBlock> KITCHEN_STOVETOPS = new ArrayList<>();
    public static final MapCodec<KitchenStovetopBlock> CODEC = createCodec(KitchenStovetopBlock::new);

    public KitchenStovetopBlock(Settings settings) {
        super(settings);
        setDefaultState(this.getStateManager().getDefaultState().with(FACING, Direction.NORTH).with(LIT, true));
        KITCHEN_STOVETOPS.add(this);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    public static Stream<KitchenStovetopBlock> streamKitchenStovetop() {
        return KITCHEN_STOVETOPS.stream();
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        Block neighborBlock = world.getBlockState(pos.down()).getBlock();
        return neighborBlock instanceof KitchenCounterOvenBlock || neighborBlock instanceof KitchenCounterBlock;
    }

    @Override
    protected ActionResult onUseWithItem(ItemStack itemStack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        Optional<RecipeEntry<CampfireCookingRecipe>> optional;
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof StovetopBlockEntity stovetopBlockEntity && world.getRecipeManager().getPropertySet(RecipePropertySet.CAMPFIRE_INPUT).canUse(itemStack)) {
            if (world instanceof ServerWorld serverWorld) {
                if (stovetopBlockEntity.addItem(serverWorld, player, itemStack)) {
                    player.incrementStat(Statistics.STOVETOP_USED);
                    return ActionResult.SUCCESS;
                }
            }
            return ActionResult.CONSUME;
        }
        return super.onUseWithItem(itemStack, state, world, pos, player, hand, hit);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        StovetopBlockEntity stovetopBlockEntity;
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if(blockEntity instanceof StovetopBlockEntity){
            stovetopBlockEntity = (StovetopBlockEntity)blockEntity;
            for (int i = 0; i < stovetopBlockEntity.getItemsBeingCooked().size(); i++) {
                ItemStack stack = stovetopBlockEntity.getItemsBeingCooked().get(i);
                if (stack.isEmpty()) continue;
                if(world instanceof ServerWorld serverWorld && serverWorld.getRecipeManager().getFirstMatch(RecipeType.CAMPFIRE_COOKING, new SingleStackRecipeInput(stack), world).isEmpty()) {
                    ItemEntity itemEntity = new ItemEntity(world, pos.getX() + 0.5D, pos.getY() + 0.8D, pos.getZ() + 0.5D, stovetopBlockEntity.removeStack(i));
                    world.spawnEntity(itemEntity);
                    player.incrementStat(Statistics.STOVETOP_USED);
                    return ActionResult.SUCCESS;
                }
            }
            return ActionResult.CONSUME;
        }

        return ActionResult.PASS;
    }


    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        return direction == Direction.DOWN && !this.canPlaceAt(state, world, pos) ? Blocks.AIR.getDefaultState() : super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing());
    }

    protected static final VoxelShape STOVETOP = VoxelShapes.union(createCuboidShape(0, 0, 1, 16, 1, 15));
    protected static final VoxelShape STOVETOP_SOUTH = rotateShape(Direction.NORTH, Direction.SOUTH, STOVETOP);
    protected static final VoxelShape STOVETOP_EAST = rotateShape(Direction.NORTH, Direction.EAST, STOVETOP);
    protected static final VoxelShape STOVETOP_WEST = rotateShape(Direction.NORTH, Direction.WEST, STOVETOP);
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
        Direction dir = state.get(FACING);
        return switch (dir) {
            case WEST -> STOVETOP_EAST;
            case NORTH -> STOVETOP_SOUTH;
            case SOUTH -> STOVETOP;
            default -> STOVETOP_WEST;
        };
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
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LIT, FACING);
    }
    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (world.isClient) {
            if (state.get(LIT)) {
                return BasicToiletBlock.checkType(type, BlockEntities.STOVE_TOP_BLOCK_ENTITY, StovetopBlockEntity::clientTick);
            }
        } else {
            if (state.get(LIT)) {
                return BasicToiletBlock.checkType(type, BlockEntities.STOVE_TOP_BLOCK_ENTITY, StovetopBlockEntity::litServerTick);
            }
            return BasicToiletBlock.checkType(type, BlockEntities.STOVE_TOP_BLOCK_ENTITY, StovetopBlockEntity::unlitServerTick);
        }
        return null;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public boolean canPathfindThrough(BlockState state, NavigationType type) {
        return false;
    }
    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.isOf(newState.getBlock())) {
            return;
        }
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof StovetopBlockEntity stovetopBlockEntity) {
            ItemScatterer.spawn(world, pos, stovetopBlockEntity.getInventory());
            world.updateComparators(pos, this);
            stovetopBlockEntity.markRemoved();
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }
}
