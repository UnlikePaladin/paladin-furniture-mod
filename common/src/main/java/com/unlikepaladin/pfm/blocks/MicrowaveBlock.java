package com.unlikepaladin.pfm.blocks;

import com.mojang.serialization.MapCodec;
import com.unlikepaladin.pfm.blocks.blockentities.MicrowaveBlockEntity;
import com.unlikepaladin.pfm.data.FurnitureBlock;
import com.unlikepaladin.pfm.registry.BlockEntities;
import com.unlikepaladin.pfm.registry.Statistics;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CampfireCookingRecipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.ServerRecipeManager;
import net.minecraft.recipe.SmokingRecipe;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static com.unlikepaladin.pfm.blocks.ClassicChairBlock.rotateShape;

public class MicrowaveBlock extends HorizontalFacingBlockWithEntity implements DynamicRenderLayerInterface {
    public static final BooleanProperty OPEN = Properties.OPEN;
    public static final BooleanProperty POWERED = Properties.POWERED;
    private final Block baseBlock;
    private final BlockState baseBlockState;
    private static final List<FurnitureBlock> MICROWAVES = new ArrayList<>();
    public static final MapCodec<MicrowaveBlock> CODEC = createCodec(MicrowaveBlock::new);

    public MicrowaveBlock(Settings settings) {
        super(settings);
        setDefaultState(this.getStateManager().getDefaultState().with(OPEN, false).with(FACING, Direction.NORTH));
        this.baseBlockState = this.getDefaultState();
        this.baseBlock = baseBlockState.getBlock();
        MICROWAVES.add(new FurnitureBlock(this, "microwave"));
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    public static Stream<FurnitureBlock> streamMicrowaves() {
        return MICROWAVES.stream();
    }
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(Properties.HORIZONTAL_FACING);
        stateManager.add(OPEN);
        stateManager.add(POWERED);
    }
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient) {
            openScreen(player, state, world, pos);
            player.incrementStat(Statistics.MICROWAVE_USED);
            PiglinBrain.onGuardedBlockInteracted((ServerWorld) world,player, true);
        }
        return ActionResult.SUCCESS;
    }

    @ExpectPlatform
    public static void openScreen(PlayerEntity player, BlockState state, World world, BlockPos pos) {
        return;
    }
    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public boolean hasSidedTransparency(BlockState state) {
        return true;
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

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.isOf(newState.getBlock())) {
            return;
        }
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof Inventory) {
            ItemScatterer.spawn(world, pos, (Inventory) blockEntity);
            world.updateComparators(pos, this);
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(world, type, BlockEntities.MICROWAVE_BLOCK_ENTITY);
    }

    @Nullable
    protected static <T extends BlockEntity> BlockEntityTicker<T> checkType(World world, BlockEntityType<T> givenType, BlockEntityType<? extends MicrowaveBlockEntity> expectedType) {
        if (!world.isClient) {
            ServerRecipeManager.MatchGetter<SingleStackRecipeInput, SmokingRecipe> cachedcheck = ServerRecipeManager.createCachedMatchGetter(
                    RecipeType.SMOKING
            );
            return validateTicker(givenType, expectedType, (worldx, pos, statex, blockEntity) -> MicrowaveBlockEntity.tick(worldx, pos, statex, blockEntity, cachedcheck));
        }
        return null;
    }

    @Override
    public boolean isShapeFullCube(BlockState state, BlockView world, BlockPos pos) {
        return false;
    }

    private final VoxelShape MICROWAVE = VoxelShapes.union(createCuboidShape(1, 0, 4,15, 8, 14));

    private final VoxelShape MICROWAVE_SOUTH = rotateShape(Direction.NORTH, Direction.SOUTH, MICROWAVE);
    private final VoxelShape MICROWAVE_EAST = rotateShape(Direction.NORTH, Direction.EAST, MICROWAVE);
    private final VoxelShape MICROWAVE_WEST = rotateShape(Direction.NORTH, Direction.WEST, MICROWAVE);
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Direction direction = state.get(FACING);
        switch (direction) {
            case SOUTH -> {
                return MICROWAVE_SOUTH;
            }
            case EAST ->{
                return MICROWAVE_EAST;
            }
            case WEST -> {
                return MICROWAVE_WEST;
            }
            default -> {
                return MICROWAVE;
            }
        }
    }

    @Override
    public boolean canPathfindThrough(BlockState state, NavigationType type) {
        return false;
    }

    @Override
    public RenderLayer getCustomRenderLayer() {
        return RenderLayer.getTranslucent();
    }
}
