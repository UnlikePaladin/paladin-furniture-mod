package com.unlikepaladin.pfm.blocks.blockentities;

import com.unlikepaladin.pfm.blocks.MicrowaveBlock;
import com.unlikepaladin.pfm.menus.MicrowaveScreenHandler;
import com.unlikepaladin.pfm.registry.BlockEntities;
import com.unlikepaladin.pfm.registry.SoundIDs;
import dev.architectury.injectables.annotations.ExpectPlatform;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.block.entity.ViewerCountManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.*;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class MicrowaveBlockEntity extends LockableContainerBlockEntity implements NamedScreenHandlerFactory, SidedInventory, RecipeUnlocker{
    public boolean isActive = false;
    private final ServerRecipeManager.MatchGetter<SingleStackRecipeInput, ? extends AbstractCookingRecipe> matchGetter;

    public MicrowaveBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntities.MICROWAVE_BLOCK_ENTITY, pos, state);
        this.recipeType = RecipeType.SMOKING;
        world = this.getWorld();
        this.matchGetter = ServerRecipeManager.createCachedMatchGetter(recipeType);
    }

    //Slot 0 = input, 2 = output, 1 = fuel
    private final ViewerCountManager stateManager = new ViewerCountManager() {
        @Override
        protected void onContainerOpen(World world, BlockPos pos, BlockState state) {
            if (state.getBlock() instanceof MicrowaveBlock) {
                MicrowaveBlockEntity.this.playSound(state, SoundEvents.BLOCK_IRON_TRAPDOOR_OPEN, 0);
                MicrowaveBlockEntity.this.setOpen(state, true);
            }
        }

        @Override
        protected void onContainerClose(World world, BlockPos pos, BlockState state) {
            if (state.getBlock() instanceof MicrowaveBlock) {
                MicrowaveBlockEntity.this.playSound(state, SoundEvents.BLOCK_IRON_TRAPDOOR_CLOSE, 0);
                MicrowaveBlockEntity.this.setOpen(state, false);
            }
        }

        @Override
        protected void onViewerCountUpdate(World world, BlockPos pos, BlockState state, int oldViewerCount, int newViewerCount) {
        }

        @Override
        protected boolean isPlayerViewing(PlayerEntity player) {
            if (player.currentScreenHandler instanceof MicrowaveScreenHandler) {
                Inventory inventory = ((MicrowaveScreenHandler) player.currentScreenHandler).getInventory();
                return inventory == MicrowaveBlockEntity.this;
            }
            return false;
        }
    };

    void playSound(BlockState state, SoundEvent soundEvent, int pitch) {
        Vec3i vec3i = state.get(MicrowaveBlock.FACING).getVector();
        double d = (double) this.pos.getX() + 0.5 + (double) vec3i.getX() / 2.0;
        double e = (double) this.pos.getY() + 0.5 + (double) vec3i.getY() / 2.0;
        double f = (double) this.pos.getZ() + 0.5 + (double) vec3i.getZ() / 2.0;
        float i = pitch == 0 ? (i = this.world.random.nextFloat() * 0.2f + 0.9f) : (i = pitch);
        this.world.playSound(null, d, e, f, soundEvent, SoundCategory.BLOCKS, 0.5f, i);
    }

    private static final int[] TOP_SLOTS = new int[]{0};
    public DefaultedList<ItemStack> inventory = DefaultedList.ofSize(size(), ItemStack.EMPTY);
    int cookTime;
    int cookTimeTotal;

    public PropertyDelegate getPropertyDelegate() {
        return propertyDelegate;
    }

    protected final PropertyDelegate propertyDelegate = new PropertyDelegate() {

        @Override
        public int get(int index) {
            switch (index) {
                case 0: {
                    return MicrowaveBlockEntity.this.cookTime;
                }
                case 1: {
                    return MicrowaveBlockEntity.this.cookTimeTotal;
                }
            }
            return 0;
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0: {
                    MicrowaveBlockEntity.this.cookTime = value;
                    break;
                }
                case 1: {
                    MicrowaveBlockEntity.this.cookTimeTotal = value;
                    break;
                }
            }
        }

        @Override
        public int size() {
            return 2;
        }
    };
    private final Object2IntOpenHashMap<Identifier> recipesUsed = new Object2IntOpenHashMap();
    private final RecipeType<? extends AbstractCookingRecipe> recipeType;

    @Override
    public void onOpen(PlayerEntity player) {
        if (!this.removed && !player.isSpectator()) {
            this.stateManager.openContainer(player, this.getWorld(), this.getPos(), this.getCachedState());
        }
    }

    @Override
    public void onClose(PlayerEntity player) {
        if (!this.removed && !player.isSpectator()) {
            this.stateManager.closeContainer(player, this.getWorld(), this.getPos(), this.getCachedState());
        }
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("container.pfm.microwave");
    }


    private static int getCookTime(ServerWorld world, MicrowaveBlockEntity microwave) {
        SingleStackRecipeInput singlerecipeinput = new SingleStackRecipeInput(microwave.getStack(0));
        return microwave.matchGetter
                .getFirstMatch(singlerecipeinput, world)
                .map(recipe -> recipe.value().getCookingTime())
                .orElse(200);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        Inventories.readNbt(nbt, this.inventory, registryLookup);
        this.cookTime = nbt.getShort("CookTime");
        this.cookTimeTotal = nbt.getShort("CookTimeTotal");
        NbtCompound nbtCompound = nbt.getCompound("RecipesUsed");
        this.isActive = nbt.getBoolean("isActive");
        for (String string : nbtCompound.getKeys()) {
            this.recipesUsed.put(Identifier.of(string), nbtCompound.getInt(string));
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.putShort("CookTime", (short)this.cookTime);
        nbt.putShort("CookTimeTotal", (short)this.cookTimeTotal);
        Inventories.writeNbt(nbt, this.inventory, registryLookup);
        NbtCompound nbtCompound = new NbtCompound();
        nbt.putBoolean("isActive", this.isActive);
        this.recipesUsed.forEach((identifier, integer) -> nbtCompound.putInt(identifier.toString(), (int)integer));
        nbt.put("RecipesUsed", nbtCompound);
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        if (side == Direction.UP || side == Direction.DOWN) {
            return TOP_SLOTS;
        }
        return null;
    }


    @Override
    protected Text getContainerName() {
        return getDisplayName();
    }

    @Override
    protected DefaultedList<ItemStack> getHeldStacks() {
        return inventory;
    }

    @Override
    protected void setHeldStacks(DefaultedList<ItemStack> inventory) {
        this.inventory = inventory;
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new MicrowaveScreenHandler(this, syncId, playerInventory, this, this.propertyDelegate);
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return this.isValid(slot, stack);
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return dir == Direction.DOWN && getRecipe(new SingleStackRecipeInput(stack)) == null;
    }

    public Recipe<?> getRecipe(SingleStackRecipeInput inventory) {
        Optional<? extends RecipeEntry<? extends AbstractCookingRecipe>> entry = matchGetter.getFirstMatch(inventory, (ServerWorld) world);
        return entry.<Recipe<?>>map(RecipeEntry::value).orElse(null);
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        return slot == 0;
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemStack : this.inventory) {
            if (itemStack.isEmpty()) continue;
            return false;
        }
        return true;
    }

    public ItemStack getStack(int slot) {
        return this.inventory.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack stack =  Inventories.splitStack(this.inventory, slot, amount);
        world.updateListeners(pos, this.getCachedState(), this.getCachedState(), Block.NOTIFY_LISTENERS);
        this.markDirty();
        return stack;
    }

    @Override
    public ItemStack removeStack(int slot) {
        ItemStack stack =  Inventories.removeStack(this.inventory, slot);
        world.updateListeners(pos, this.getCachedState(), this.getCachedState(), Block.NOTIFY_LISTENERS);
        this.markDirty();
        return stack;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        ItemStack itemStack = this.inventory.get(slot);
        boolean bl = !stack.isEmpty() && ItemStack.areItemsAndComponentsEqual(itemStack, stack);
        this.inventory.set(slot, stack);
        stack.capCount(this.getMaxCount(stack));
        if (slot == 0 && !bl && world instanceof ServerWorld) {
            this.cookTimeTotal = getCookTime((ServerWorld) this.world, this);
            this.cookTime = 0;
            this.markDirty();
            world.updateListeners(pos, this.getCachedState(), this.getCachedState(), Block.NOTIFY_LISTENERS);
        }
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        if (this.world.getBlockEntity(this.pos) != this) {
            return false;
        }
        return player.squaredDistanceTo((double) this.pos.getX() + 0.5, (double) this.pos.getY() + 0.5, (double) this.pos.getZ() + 0.5) <= 64.0;
    }

    @Override
    public RecipeEntry<?> getLastRecipe() {
        return null;
    }


    public static boolean canAcceptRecipeOutput(DynamicRegistryManager registryManager, RecipeEntry<? extends AbstractCookingRecipe> input, @Nullable Recipe<?> recipe, SingleStackRecipeInput slots, int count) {
        if (slots.isEmpty() || recipe == null) {
            return false;
        }
        ItemStack itemStack = input.value().craft(slots, registryManager);
        if (itemStack.isEmpty()) {
            return false;
        }
        ItemStack itemStack2 = slots.getStackInSlot(0);
        if (itemStack2.getCount() < count && itemStack2.getCount() < itemStack2.getMaxCount()) {
            return true;
        }
        return itemStack2.getCount() < itemStack.getMaxCount();
    }

    @Override
    public void clear() {
        this.inventory.clear();
        world.updateListeners(pos, this.getCachedState(), this.getCachedState(), Block.NOTIFY_LISTENERS);
    }

    @Override
    public void setLastRecipe(@Nullable RecipeEntry<?> recipe) {
        if (recipe != null) {
            Identifier identifier = recipe.id().getValue();
            this.recipesUsed.addTo(identifier, 1);
        }
    }

    private static boolean craftRecipe(ServerRecipeManager.MatchGetter<SingleStackRecipeInput, SmokingRecipe> recipeMatchGetter, ServerWorld world, DefaultedList<ItemStack> slots, int count) {
        SingleStackRecipeInput singleStackRecipeInput = new SingleStackRecipeInput(slots.getFirst());
        ItemStack itemStack2 = recipeMatchGetter.getFirstMatch(singleStackRecipeInput, world)
                .map(recipe -> recipe.value().craft(singleStackRecipeInput, world.getRegistryManager()))
                .orElse(slots.getFirst());
        slots.set(0, itemStack2.copy());
        return true;
    }

    void setOpen(BlockState state, boolean open) {
        this.world.setBlockState(this.getPos(), state.with(MicrowaveBlock.OPEN, open), Block.NOTIFY_LISTENERS | Block.REDRAW_ON_MAIN_THREAD);
        world.updateListeners(pos, this.getCachedState(), this.getCachedState(), Block.NOTIFY_LISTENERS);
    }

    public static void tick(World world1, BlockPos pos, BlockState state, MicrowaveBlockEntity blockEntity, ServerRecipeManager.MatchGetter<SingleStackRecipeInput, SmokingRecipe> recipeMatchGetter) {
        ServerWorld world = (ServerWorld) world1;
        boolean bl = blockEntity.isActive;
        boolean bl2 = false;
        ItemStack itemStack = blockEntity.inventory.get(0);
        if (blockEntity.isActive || !itemStack.isEmpty()) {
            RecipeEntry<? extends AbstractCookingRecipe> recipeEntry = world.getRecipeManager().getFirstMatch(blockEntity.recipeType, new SingleStackRecipeInput(itemStack), world).orElse(null);
            Recipe recipe = recipeEntry != null ? recipeEntry.value() : null;
            int i = blockEntity.getMaxCountPerStack();
            if (blockEntity.isActive && canAcceptRecipeOutput(world.getRegistryManager(), recipeEntry, recipe, new SingleStackRecipeInput(blockEntity.inventory.get(0)), i)) {
                ++blockEntity.cookTime;
                if (blockEntity.cookTime == blockEntity.cookTimeTotal) {
                    blockEntity.cookTime = 0;
                    blockEntity.cookTimeTotal = getCookTime(world, blockEntity);
                    if (craftRecipe(recipeMatchGetter, world, blockEntity.inventory, i)) {
                        blockEntity.setLastRecipe(recipeEntry);
                        blockEntity.world.setBlockState(pos, state = state.with(MicrowaveBlock.POWERED, false), Block.NOTIFY_LISTENERS | Block.REDRAW_ON_MAIN_THREAD);
                        blockEntity.playSound(state, SoundIDs.MICROWAVE_BEEP_EVENT, 1);
                        blockEntity.setActiveonClient(blockEntity, false);
                        world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS);
                    }
                    bl2 = true;
                }
                else {
                    blockEntity.playSound(state, SoundIDs.MICROWAVE_RUNNING_EVENT, 1);
                }
            } else {
                blockEntity.cookTime = 0;
                if(itemStack.isEmpty()) {
                    blockEntity.setActiveonClient(blockEntity,false);
                    world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS);
                }
            }
        } else if (!blockEntity.isActive && blockEntity.cookTime > 0) {
            blockEntity.cookTime = MathHelper.clamp(blockEntity.cookTime - 2, 0, blockEntity.cookTimeTotal);
        }
        if (bl != blockEntity.isActive) {
            bl2 = true;
        }
        if (bl2) {
            markDirty(world, pos, state);
        }

    }

    public Direction getFacing() {
        return this.getCachedState().get(MicrowaveBlock.FACING);
    }

    public void setActive(boolean active) {
        this.isActive = active;
        NbtCompound nbtCompound = new NbtCompound();
        nbtCompound.putBoolean("isActive", active);
        this.writeNbt(nbtCompound, world.getRegistryManager());
        this.markDirty();
        world.setBlockState(pos, this.getCachedState().with(MicrowaveBlock.POWERED, true), Block.NOTIFY_LISTENERS);
    }

    @ExpectPlatform
    public static void setActiveonClient(MicrowaveBlockEntity microwaveBlockEntity, boolean active) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static BlockEntityType.BlockEntityFactory<? extends MicrowaveBlockEntity> getFactory() {
        throw new UnsupportedOperationException();
    }
}


