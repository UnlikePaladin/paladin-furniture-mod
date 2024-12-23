package com.unlikepaladin.pfm.blocks.blockentities;

import com.unlikepaladin.pfm.blocks.KitchenStovetopBlock;
import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.registry.BlockEntities;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.CampfireCookingRecipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipePropertySet;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Clearable;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;


public class StovetopBlockEntity extends BlockEntity implements Clearable {

    public final DefaultedList<ItemStack> itemsBeingCooked = DefaultedList.ofSize(4, ItemStack.EMPTY);
    private final int[] cookingTimes = new int[4];
    private final int[] cookingTotalTimes = new int[4];
    public StovetopBlockEntity( BlockPos pos, BlockState state) {
        super(BlockEntities.STOVE_TOP_BLOCK_ENTITY, pos, state);
    }
    public static void litServerTick(World world1, BlockPos pos, BlockState state, StovetopBlockEntity stovetopBlockEntity) {
        boolean bl = false;
        ServerWorld world = (ServerWorld) world1;
        for (int i = 0; i < stovetopBlockEntity.itemsBeingCooked.size(); ++i) {
            ItemStack itemStack = stovetopBlockEntity.itemsBeingCooked.get(i);
            if (itemStack.isEmpty()) continue;
            bl = true;
            if (stovetopBlockEntity.cookingTimes[i] < 600){
                stovetopBlockEntity.cookingTimes[i] = stovetopBlockEntity.cookingTimes[i] + 2;
            }
            if (stovetopBlockEntity.cookingTimes[i] < stovetopBlockEntity.cookingTotalTimes[i]) continue;
            SingleStackRecipeInput inventory = new SingleStackRecipeInput(itemStack);
            ItemStack itemStack2 = world.getRecipeManager().getFirstMatch(RecipeType.CAMPFIRE_COOKING, inventory, world).map(campfireCookingRecipe -> campfireCookingRecipe.value().craft(inventory, world.getRegistryManager())).orElse(itemStack);
                if (PaladinFurnitureMod.getPFMConfig().doesFoodPopOffStove()) {
                    ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), itemStack2);
                    stovetopBlockEntity.itemsBeingCooked.set(i, ItemStack.EMPTY);
                }
                else {
                    stovetopBlockEntity.itemsBeingCooked.set(i, itemStack2);
                }
            world.updateListeners(pos, state, state, Block.NOTIFY_ALL);
        }
        if (bl) {
            CampfireBlockEntity.markDirty(world, pos, state);
        }
    }

    public static void unlitServerTick(World world, BlockPos pos, BlockState state, StovetopBlockEntity stovetopBlockEntity) {
        boolean bl = false;
        for (int i = 0; i < stovetopBlockEntity.itemsBeingCooked.size(); ++i) {
            if (stovetopBlockEntity.cookingTimes[i] <= 0) continue;
            bl = true;
            stovetopBlockEntity.cookingTimes[i] = MathHelper.clamp(stovetopBlockEntity.cookingTimes[i] - 2, 0, stovetopBlockEntity.cookingTotalTimes[i]);
        }
        if (bl) {
            CampfireBlockEntity.markDirty(world, pos, state);
        }
    }

    public static void clientTick(World world, BlockPos pos, BlockState state, StovetopBlockEntity stovetopBlockEntity) {
        int i;
        Random random = world.random;
        i = state.get(KitchenStovetopBlock.FACING).rotateYClockwise().getHorizontalQuarterTurns();
        for (int j = 0; j < stovetopBlockEntity.itemsBeingCooked.size(); ++j) {
            ItemStack stack = stovetopBlockEntity.itemsBeingCooked.get(j);
            if (stack.isEmpty() || !(random.nextFloat() < 0.2f) || !world.getRecipeManager().getPropertySet(RecipePropertySet.CAMPFIRE_INPUT).canUse(stack)) continue;
            Direction direction = Direction.fromHorizontalQuarterTurns(Math.floorMod(j + i, 4));
            float f = 0.2125f;
            double x = pos.getX() + 0.5 - ((direction.getOffsetX() * f) + (direction.rotateYClockwise().getOffsetX() * f));
            double y = pos.getY() + 0.2;
            double z = pos.getZ() + 0.5 - ((direction.getOffsetZ() * f) + (direction.rotateYClockwise().getOffsetZ() * f));
            for (int k = 0; k < 4; ++k) {
                if (!(random.nextFloat() < 0.9f))
                    world.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0, 5.0E-4, 0.0);
            }
        }
    }

    public DefaultedList<ItemStack> getItemsBeingCooked() {
        return this.itemsBeingCooked;
    }

    public Inventory getInventory(){
        SimpleInventory inventory = new SimpleInventory(itemsBeingCooked.size());
        for (int i = 0; i < itemsBeingCooked.size(); i++) {
            inventory.setStack(i, itemsBeingCooked.get(i));
        }
        return inventory;
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        int[] is;
        super.readNbt(nbt, registryLookup);
        this.itemsBeingCooked.clear();
        Inventories.readNbt(nbt, this.itemsBeingCooked, registryLookup);
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
        Inventories.writeNbt(nbt, this.itemsBeingCooked, true, registryLookup);
        return nbt;
    }

    public ItemStack removeStack(int slot) {
        ItemStack stack = this.itemsBeingCooked.get(slot).copy();
        this.itemsBeingCooked.set(slot, ItemStack.EMPTY);
        updateListeners();
        return stack;
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

    private void updateListeners() {
        this.markDirty();
        this.getWorld().updateListeners(this.getPos(), this.getCachedState(), this.getCachedState(), Block.NOTIFY_ALL);
    }

    @Override
    public void clear() {
        this.itemsBeingCooked.clear();
        updateListeners();
    }

    @ExpectPlatform
    public static BlockEntityType.BlockEntityFactory<? extends StovetopBlockEntity> getFactory() {
        throw new UnsupportedOperationException();
    }
}

