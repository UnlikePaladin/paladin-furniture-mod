package com.unlikepaladin.pfm.blocks.blockentities;

import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.blocks.*;
import com.unlikepaladin.pfm.registry.BlockEntities;
import com.unlikepaladin.pfm.menus.StoveScreenHandler;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.CampfireCookingRecipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class StoveBlockEntity extends AbstractFurnaceBlockEntity {
    public StoveBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntities.STOVE_BLOCK_ENTITY, pos, state, RecipeType.SMOKING);
    }
    public StoveBlockEntity(BlockEntityType<?> entity, BlockPos pos, BlockState state) {
        super(entity, pos, state, RecipeType.SMOKING);
    }
     String blockname = this.getCachedState().getBlock().getTranslationKey();

    protected void onContainerOpen(World world, BlockPos pos, BlockState state) {
        if (state.getBlock() instanceof StoveBlock){
            StoveBlockEntity.this.playSound(state, SoundEvents.BLOCK_IRON_TRAPDOOR_OPEN);
            StoveBlockEntity.this.setOpen(state, true);
        }
    }

    protected void onContainerClose(World world, BlockPos pos, BlockState state) {
        if (state.getBlock() instanceof StoveBlock) {
            StoveBlockEntity.this.playSound(state, SoundEvents.BLOCK_IRON_TRAPDOOR_CLOSE);
            StoveBlockEntity.this.setOpen(state, false);
        }
    }

    void setOpen(BlockState state, boolean open) {
        this.world.setBlockState(this.getPos(), state.with(Properties.OPEN, open), 3);
    }

    @Override
    public void onClose(PlayerEntity player) {
        if (!this.removed && !player.isSpectator()) {
            this.onContainerClose(this.getWorld(), this.getPos(), this.getCachedState());
        }
    }

    @Override
    public void onOpen(PlayerEntity player) {
        if (!this.removed && !player.isSpectator()) {
            this.onContainerOpen(this.getWorld(), this.getPos(), this.getCachedState());
        }
    }

    void playSound(BlockState state, SoundEvent soundEvent) {
        Vec3i vec3i = state.get(Properties.HORIZONTAL_FACING).getVector();
        double d = (double)this.pos.getX() + 0.5 + (double)vec3i.getX() / 2.0;
        double e = (double)this.pos.getY() + 0.5 + (double)vec3i.getY() / 2.0;
        double f = (double)this.pos.getZ() + 0.5 + (double)vec3i.getZ() / 2.0;
        this.world.playSound(null, d, e, f, soundEvent, SoundCategory.BLOCKS, 0.5f, this.world.random.nextFloat() * 0.1f + 0.9f);
    }

    @Override
    protected Text getContainerName() {
        blockname = blockname.replace("block.pfm", "");
        if (this.getCachedState().getBlock() instanceof KitchenCounterOvenBlock) {
            return Text.translatable("container.pfm.kitchen_counter_oven");
        }
        return Text.translatable("container.pfm" + blockname);
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new StoveScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }

    protected final DefaultedList<ItemStack> itemsBeingCooked = DefaultedList.ofSize(4, ItemStack.EMPTY);
    private final int[] cookingTimes = new int[4];
    private final int[] cookingTotalTimes = new int[4];

    public DefaultedList<ItemStack> getItemsBeingCooked() {
        return this.itemsBeingCooked;
    }
    public Optional<RecipeEntry<CampfireCookingRecipe>> getRecipeFor(ItemStack item) {
        if (this.itemsBeingCooked.stream().noneMatch(ItemStack::isEmpty)) {
            return Optional.empty();
        }
        return ((ServerWorld)this.world).getRecipeManager().getFirstMatch(RecipeType.CAMPFIRE_COOKING, new SingleStackRecipeInput(item), this.world);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        int[] is;
        this.itemsBeingCooked.clear();
        readNbt(nbt, this.itemsBeingCooked, registryLookup);
        if (nbt.contains("CookingTimes", 11)) {
            is = nbt.getIntArray("CookingTimes");
            System.arraycopy(is, 0, this.cookingTimes, 0, Math.min(this.cookingTotalTimes.length, is.length));
        }
        if (nbt.contains("CookingTotalTimes", 11)) {
            is = nbt.getIntArray("CookingTotalTimes");
            System.arraycopy(is, 0, this.cookingTotalTimes, 0, Math.min(this.cookingTotalTimes.length, is.length));
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        this.saveInitialChunkData(nbt, registryLookup);
        nbt.putIntArray("CookingTimes", this.cookingTimes);
        nbt.putIntArray("CookingTotalTimes", this.cookingTotalTimes);
    }

    protected NbtCompound saveInitialChunkData(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        writeNbt(nbt, this.itemsBeingCooked, true, registryLookup);
        return nbt;
    }

    public static NbtCompound writeNbt(NbtCompound nbt, DefaultedList<ItemStack> stacks, boolean setIfEmpty, RegistryWrapper.WrapperLookup registryLookup) {
        NbtList nbtList = new NbtList();
        for (int i = 0; i < stacks.size(); ++i) {
            ItemStack itemStack = stacks.get(i);
            if (itemStack.isEmpty()) continue;
            NbtCompound nbtCompound = new NbtCompound();
            nbtCompound.putByte("Slot", (byte)i);
            nbtList.add(itemStack.toNbt(registryLookup, nbtCompound));
        }
        if (!nbtList.isEmpty() || setIfEmpty) {
            nbt.put("CookTopItems", nbtList);
        }
        return nbt;
    }

    public static void readNbt(NbtCompound nbt, DefaultedList<ItemStack> stacks, RegistryWrapper.WrapperLookup registryLookup) {
        NbtList nbtList = nbt.getList("CookTopItems", 10);
        for (int i = 0; i < nbtList.size(); ++i) {
            NbtCompound nbtCompound = nbtList.getCompound(i);
            int j = nbtCompound.getByte("Slot") & 0xFF;
            if (j < 0 || j >= stacks.size()) continue;
            stacks.set(j, ItemStack.fromNbt(registryLookup, nbtCompound).orElse(ItemStack.EMPTY));
        }
    }

    public ItemStack removeStack(int slot) {
        ItemStack stack = this.itemsBeingCooked.get(slot).copy();
        this.itemsBeingCooked.set(slot, ItemStack.EMPTY);
        updateListeners();
        return stack;
    }

    @Override
    public void clear() {
        this.itemsBeingCooked.clear();
    }


    private void updateListeners() {
        this.markDirty();
        this.getWorld().updateListeners(this.getPos(), this.getCachedState(), this.getCachedState(), Block.NOTIFY_ALL);
    }

    public static void litServerTick(World worldd, BlockPos pos, BlockState state, BlockEntity blockEntity) {
        ServerWorld world = (ServerWorld) worldd;
        if (blockEntity instanceof StoveBlockEntity) {
            StoveBlockEntity stoveBlockEntity = (StoveBlockEntity) blockEntity;
            boolean bl = false;
            for (int i = 0; i < stoveBlockEntity.itemsBeingCooked.size(); ++i) {
                ItemStack itemStack = stoveBlockEntity.itemsBeingCooked.get(i);
                if (itemStack.isEmpty()) continue;
                bl = true;
                int n = i;
                if (stoveBlockEntity.cookingTimes[n] < 600){
                    stoveBlockEntity.cookingTimes[n] = stoveBlockEntity.cookingTimes[n] + 2;
                }
                if (stoveBlockEntity.cookingTimes[i] < stoveBlockEntity.cookingTotalTimes[i]) continue;
                SingleStackRecipeInput inventory = new SingleStackRecipeInput(itemStack);
                ItemStack itemStack2 = world.getRecipeManager().getFirstMatch(RecipeType.CAMPFIRE_COOKING, inventory, world).map(campfireCookingRecipe -> campfireCookingRecipe.value().craft(inventory, world.getRegistryManager())).orElse(itemStack);
                    if (PaladinFurnitureMod.getPFMConfig().doesFoodPopOffStove()) {
                        ItemScatterer.spawn(world, pos.getX(), pos.up().getY(), pos.getZ(), itemStack2);
                        stoveBlockEntity.itemsBeingCooked.set(i, ItemStack.EMPTY);
                    }
                    else {
                        stoveBlockEntity.itemsBeingCooked.set(i, itemStack2);
                    }
                world.updateListeners(pos, state, state, Block.NOTIFY_ALL);
            }
            if (bl) {
                markDirty(world, pos, state);
            }
            tick(world, pos, state, stoveBlockEntity);
        }
    }

    public static void unlitServerTick(ServerWorld world, BlockPos pos, BlockState state, StoveBlockEntity stoveBlockEntity) {
        boolean bl = false;
        for (int i = 0; i < stoveBlockEntity.itemsBeingCooked.size(); ++i) {
            if (stoveBlockEntity.cookingTimes[i] <= 0) continue;
            bl = true;
            stoveBlockEntity.cookingTimes[i] = MathHelper.clamp(stoveBlockEntity.cookingTimes[i] - 2, 0, stoveBlockEntity.cookingTotalTimes[i]);
        }
        if (bl) {
            markDirty(world, pos, state);
        }
        tick(world, pos, state, stoveBlockEntity);
    }

    public static void clientTick(World world, BlockPos pos, BlockState state, BlockEntity blockEntity) {
        if (blockEntity instanceof StoveBlockEntity) {
            StoveBlockEntity stoveBlockEntity = (StoveBlockEntity) blockEntity;
            int i;
            Random random = world.random;
            i = state.get(StoveBlock.FACING).rotateYClockwise().getHorizontal();
            for (int j = 0; j < stoveBlockEntity.itemsBeingCooked.size(); ++j) {
                ItemStack stack = stoveBlockEntity.itemsBeingCooked.get(j);
                if (stack.isEmpty() || !(random.nextFloat() < 0.2f)) continue;
                Direction direction = Direction.fromHorizontal(Math.floorMod(j + i, 4));
                float f = 0.2125f;
                double x = pos.getX() + 0.5 - ((direction.getOffsetX() * f) + (direction.rotateYClockwise().getOffsetX() * f));
                double y = pos.getY() + 1.1;
                double z = pos.getZ() + 0.5 - ((direction.getOffsetZ() * f) + (direction.rotateYClockwise().getOffsetZ() * f));
                for (int k = 0; k < 4; ++k) {
                    if (!(random.nextFloat() < 0.9f))
                        world.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0, 5.0E-4, 0.0);
                }
            }
        }
    }

    public boolean addItem(ServerWorld world, @Nullable LivingEntity entity, ItemStack stack) {
        for (int i = 0; i < this.itemsBeingCooked.size(); i++) {
            ItemStack itemStack = this.itemsBeingCooked.get(i);
            if (itemStack.isEmpty()) {
                Optional<RecipeEntry<CampfireCookingRecipe>> optional = world.getRecipeManager()
                        .getFirstMatch(RecipeType.CAMPFIRE_COOKING, new SingleStackRecipeInput(stack), world);
                if (optional.isEmpty()) {
                    return false;
                }

                this.cookingTotalTimes[i] = ((CampfireCookingRecipe)((RecipeEntry<?>)optional.get()).value()).getCookingTime();
                this.cookingTimes[i] = 0;
                this.itemsBeingCooked.set(i, stack.splitUnlessCreative(1, entity));
                world.emitGameEvent(GameEvent.BLOCK_CHANGE, this.getPos(), GameEvent.Emitter.of(entity, this.getCachedState()));
                this.updateListeners();
                return true;
            }
        }

        return false;
    }

    @ExpectPlatform
    public static BlockEntityType.BlockEntityFactory<? extends BlockEntity> getFactory() {
        throw new UnsupportedOperationException();
    }
}
