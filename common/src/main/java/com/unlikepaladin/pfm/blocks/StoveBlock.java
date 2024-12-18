package com.unlikepaladin.pfm.blocks;

import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.blocks.blockentities.StoveBlockEntity;
import com.unlikepaladin.pfm.blocks.blockentities.StovetopBlockEntity;
import com.unlikepaladin.pfm.data.FurnitureBlock;
import com.unlikepaladin.pfm.registry.Statistics;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.CampfireCookingRecipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipePropertySet;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.unlikepaladin.pfm.blocks.KitchenDrawerBlock.rotateShape;

public class StoveBlock extends SmokerBlock implements DynamicRenderLayerInterface {
    private static final List<FurnitureBlock> STOVES = new ArrayList<>();
    public static final BooleanProperty OPEN = Properties.OPEN;
    public StoveBlock(Settings settings) {
        super(settings);
        if (this.getClass().isAssignableFrom(StoveBlock.class)){
            STOVES.add(new FurnitureBlock(this, "stove"));
        }
        setDefaultState(this.getStateManager().getDefaultState().with(FACING, Direction.NORTH).with(LIT, false).with(OPEN, false));
    }
    public static Stream<FurnitureBlock> streamStoves() {
        return STOVES.stream();
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, LIT, OPEN);
    }
    @Override
    protected void openScreen(World world, BlockPos pos, PlayerEntity player) {
        openMenuScreen(world, pos, player);
    }

    @ExpectPlatform
    public static void openMenuScreen(World world, BlockPos pos, PlayerEntity player) {

    }

    @Override
    protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (PaladinFurnitureMod.getModList().contains("cookingforblockheads")) {
            return onUseCookingForBlockheads(state, world, pos, player, hand, hit);
        } else {
            if (world.isClient) {
                return ActionResult.SUCCESS;
            }
            if (hit.getSide() == Direction.UP && world.getBlockEntity(pos) instanceof StoveBlockEntity) {
                ItemStack itemStack;
                Optional<RecipeEntry<CampfireCookingRecipe>> optional;
                BlockEntity blockEntity = world.getBlockEntity(pos);
                if (blockEntity instanceof StoveBlockEntity stoveBlockEntity && world.getRecipeManager().getPropertySet(RecipePropertySet.CAMPFIRE_INPUT).canUse(itemStack = player.getStackInHand(hand))) {
                    if (world instanceof ServerWorld serverWorld) {
                        if (stoveBlockEntity.addItem(serverWorld, player, itemStack)) {
                            player.incrementStat(Statistics.STOVETOP_USED);
                            return ActionResult.SUCCESS;
                        }
                    }
                    return ActionResult.CONSUME;
                }
                if(blockEntity instanceof StoveBlockEntity stoveBlockEntity){
                    for (int i = 0; i < stoveBlockEntity.getItemsBeingCooked().size(); i++) {
                        ItemStack itemStack1 = stoveBlockEntity.getItemsBeingCooked().get(i);
                        if (itemStack1.isEmpty()) continue;
                        if(world instanceof ServerWorld serverWorld && serverWorld.getRecipeManager().getFirstMatch(RecipeType.CAMPFIRE_COOKING, new SingleStackRecipeInput(stack), world).isEmpty()) {
                            ItemEntity itemEntity = new ItemEntity(world, pos.getX() + 0.5D, pos.getY() + 0.8D, pos.getZ() + 0.5D, stoveBlockEntity.removeStack(i));
                            world.spawnEntity(itemEntity);
                            player.incrementStat(Statistics.STOVE_OPENED);
                            return ActionResult.SUCCESS;
                        }
                    }
                    return ActionResult.CONSUME;
                }
                return ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION;
            }
            else{
                this.openScreen(world, pos, player);
            }
            return ActionResult.CONSUME;
        }
    }


    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    protected static final VoxelShape STOVE = VoxelShapes.union(createCuboidShape(0, 0, 1, 16, 1, 16),createCuboidShape(0, 1, 0, 16, 16, 16),createCuboidShape(0, 16, 15, 16, 19, 16));
    protected static final VoxelShape STOVE_SOUTH = rotateShape(Direction.NORTH, Direction.SOUTH, STOVE);
    protected static final VoxelShape STOVE_WEST = rotateShape(Direction.NORTH, Direction.WEST, STOVE);
    protected static final VoxelShape STOVE_EAST = rotateShape(Direction.NORTH, Direction.EAST, STOVE);

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
        Direction dir = state.get(FACING);
        return switch (dir) {
            case WEST -> STOVE_WEST;
            case NORTH -> STOVE;
            case SOUTH -> STOVE_SOUTH;
            default -> STOVE_EAST;
        };
    }
    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (!state.get(LIT)) {
            return;
        }
        double x = (double)pos.getX() + 0.5;
        double y = pos.getY();
        double z = (double)pos.getZ() + 0.5;
        if (random.nextDouble() < 0.1) {
            world.playSound(x, y, z, SoundEvents.BLOCK_SMOKER_SMOKE, SoundCategory.BLOCKS, 1.0f, 1.0f, false);
        }
        int min = 0;
        int max = 3;
        int griddleChosen = (int)Math.floor(Math.random()*(max-min+1)+min);
        switch (griddleChosen) {
            case 0 -> world.addParticle(ParticleTypes.SMOKE, x - 0.25, y + 1.1, z - 0.2, 0.0, 0.0, 0.0);
            case 1 -> world.addParticle(ParticleTypes.SMOKE, x + 0.25, y + 1.1, z - 0.2, 0.0, 0.0, 0.0);
            case 2 -> world.addParticle(ParticleTypes.SMOKE, x + 0.25, y + 1.1, z + 0.2, 0.0, 0.0, 0.0);
            case 3 -> world.addParticle(ParticleTypes.SMOKE, x - 0.25, y + 1.1, z + 0.2, 0.0, 0.0, 0.0);
        }
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return getBlockEntity(pos, state);
    }

    @ExpectPlatform
    public static BlockEntity getBlockEntity(BlockPos pos, BlockState state) {
        return null;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return getModdedTicker(world, state, type);
    }

    @ExpectPlatform
    public static <T extends BlockEntity> BlockEntityTicker<T> getModdedTicker(World world, BlockState state, BlockEntityType<T> type){
        throw new AssertionError();
    }

    @ExpectPlatform
    public static ActionResult onUseCookingForBlockheads(BlockState blockState, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult blockHitResult){
        throw new AssertionError();
    }
    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.isOf(newState.getBlock())) {
            return;
        }
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof StoveBlockEntity)
        {
            if (world instanceof ServerWorld) {
                ItemScatterer.spawn(world, pos, (StoveBlockEntity)blockEntity);
                ((StoveBlockEntity)blockEntity).getRecipesUsedAndDropExperience((ServerWorld) world, Vec3d.ofCenter(pos));
                ItemScatterer.spawn(world, pos, ((StoveBlockEntity)blockEntity).getItemsBeingCooked());
            }
            world.updateComparators(pos, this);
        }
        super.onStateReplaced(state, world, pos, newState, moved);
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
