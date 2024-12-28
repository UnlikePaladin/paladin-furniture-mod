package com.unlikepaladin.pfm.compat.cookingforblockheads.forge;

import com.google.common.collect.Lists;
import com.unlikepaladin.pfm.blocks.StoveBlock;
import com.unlikepaladin.pfm.compat.cookingforblockheads.forge.menu.StoveScreenHandlerBalm;
import com.unlikepaladin.pfm.registry.BlockEntities;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.block.entity.BalmBlockEntity;
import net.blay09.mods.balm.api.container.*;
import net.blay09.mods.balm.api.energy.BalmEnergyStorageProvider;
import net.blay09.mods.balm.api.energy.EnergyStorage;
import net.blay09.mods.balm.api.menu.BalmMenuProvider;
import net.blay09.mods.balm.api.provider.BalmProvider;
import net.blay09.mods.cookingforblockheads.CookingForBlockheadsConfig;
import net.blay09.mods.cookingforblockheads.api.capability.DefaultKitchenItemProvider;
import net.blay09.mods.cookingforblockheads.api.capability.IKitchenItemProvider;
import net.blay09.mods.cookingforblockheads.api.capability.IKitchenSmeltingProvider;
import net.blay09.mods.cookingforblockheads.api.event.OvenCookedEvent;
import net.blay09.mods.cookingforblockheads.block.OvenBlock;
import net.blay09.mods.cookingforblockheads.compat.Compat;
import net.blay09.mods.cookingforblockheads.registry.CookingRegistry;
import net.blay09.mods.cookingforblockheads.tile.IMutableNameable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class StoveBlockEntityBalm extends BalmBlockEntity implements IKitchenSmeltingProvider, BalmMenuProvider, IMutableNameable, BalmContainerProvider, BalmEnergyStorageProvider {
    private static final int COOK_TIME = 200;
    private final DefaultContainer container = new DefaultContainer(20) {
        public boolean isValid(int slot, ItemStack itemStack) {
            if (slot < 3) {
                return !StoveBlockEntityBalm.this.getSmeltingResult(itemStack).isEmpty();
            } else {
                return slot != 3 || StoveBlockEntityBalm.isItemFuel(itemStack);
            }
        }

        public void slotChanged(int slot) {
            if (slot >= 7 && slot < 16) {
                StoveBlockEntityBalm.this.slotCookTime[slot - 7] = 0;
            }

            StoveBlockEntityBalm.this.isDirty = true;
            StoveBlockEntityBalm.this.markDirty();
        }
    };
    private final PropertyDelegate dataAccess = new PropertyDelegate() {
        public int get(int id) {
            if (id == 0) {
                return StoveBlockEntityBalm.this.furnaceBurnTime;
            } else if (id == 1) {
                return StoveBlockEntityBalm.this.currentItemBurnTime;
            } else {
                return id >= 2 && id <= 11 ? StoveBlockEntityBalm.this.slotCookTime[id - 2] : 0;
            }
        }

        public void set(int id, int value) {
            if (id == 0) {
                StoveBlockEntityBalm.this.furnaceBurnTime = value;
            } else if (id == 1) {
                StoveBlockEntityBalm.this.currentItemBurnTime = value;
            } else if (id >= 2 && id <= 11) {
                StoveBlockEntityBalm.this.slotCookTime[id - 2] = value;
            }

        }

        public int size() {
            return 11;
        }
    };
    private final EnergyStorage energyStorage = new EnergyStorage(10000) {
        public int fill(int maxReceive, boolean simulate) {
            if (!simulate) {
                StoveBlockEntityBalm.this.markDirty();
            }

            return super.fill(maxReceive, simulate);
        }

        public int drain(int maxExtract, boolean simulate) {
            if (!simulate) {
                StoveBlockEntityBalm.this.markDirty();
            }

            return super.drain(maxExtract, simulate);
        }
    };
    private final SubContainer inputContainer;
    private final SubContainer fuelContainer;
    private final SubContainer outputContainer;
    private final SubContainer processingContainer;
    private final SubContainer toolsContainer;
    private final DefaultKitchenItemProvider itemProvider;
    private Text customName;
    private boolean isFirstTick;
    public int[] slotCookTime;
    public int furnaceBurnTime;
    public int currentItemBurnTime;
    private boolean isDirty;
    private boolean hasPowerUpgrade;
    private Direction facing;
    private final Inventory singleSlotRecipeWrapper;

    public StoveBlockEntityBalm(BlockPos pos, BlockState state) {
        super(BlockEntities.STOVE_BLOCK_ENTITY, pos, state);
        this.inputContainer = new SubContainer(this.container, 0, 3);
        this.fuelContainer = new SubContainer(this.container, 3, 4);
        this.outputContainer = new SubContainer(this.container, 4, 7);
        this.processingContainer = new SubContainer(this.container, 7, 16);
        this.toolsContainer = new SubContainer(this.container, 16, 20);
        this.itemProvider = new DefaultKitchenItemProvider(new CombinedContainer(this.toolsContainer, this.outputContainer));
        this.isFirstTick = true;
        this.slotCookTime = new int[9];
        this.singleSlotRecipeWrapper = new DefaultContainer(1);
    }

    public boolean onSyncedBlockEvent(int id, int type) {
        return super.onSyncedBlockEvent(id, type);
    }

    public static void clientTick(World level, BlockPos pos, BlockState state, BlockEntity blockEntity) {
        if (blockEntity instanceof StoveBlockEntityBalm stoveBlockEntityBalm) {
            stoveBlockEntityBalm.clientTick(level, pos, state);
        }
    }

    public void clientTick(World level, BlockPos pos, BlockState state) {
    }

    public static void serverTick(World level, BlockPos pos, BlockState state, BlockEntity blockEntity) {
        if (blockEntity instanceof StoveBlockEntityBalm stoveBlockEntityBalm) {
            stoveBlockEntityBalm.serverTick(level, pos, state);
        }
    }

    public void serverTick(World level, BlockPos pos, BlockState state) {
        if (this.isFirstTick && state.getBlock() instanceof StoveBlock) {
            this.facing = state.get(StoveBlock.FACING);
            this.isFirstTick = false;
        }

        if (this.isDirty) {
            this.sync();
            this.isDirty = false;
        }

        boolean hasChanged = false;
        int burnPotential = 200 - this.furnaceBurnTime;
        if (this.hasPowerUpgrade && burnPotential > 0 && this.shouldConsumeFuel()) {
            this.furnaceBurnTime += this.energyStorage.drain(burnPotential, false);
        }

        if (this.furnaceBurnTime > 0) {
            --this.furnaceBurnTime;
        }

        if (!level.isClient) {
            int firstEmptySlot;
            ItemStack containerItem;
            if (this.furnaceBurnTime == 0 && this.shouldConsumeFuel()) {
                for(firstEmptySlot = 0; firstEmptySlot < this.fuelContainer.size(); ++firstEmptySlot) {
                    ItemStack fuelItem = this.fuelContainer.getStack(firstEmptySlot);
                    if (!fuelItem.isEmpty()) {
                        this.currentItemBurnTime = this.furnaceBurnTime = (int)Math.max(1.0, (double)((float)getBurnTime(fuelItem)) * CookingForBlockheadsConfig.getActive().ovenFuelTimeMultiplier);
                        if (this.furnaceBurnTime != 0) {
                            containerItem = Balm.getHooks().getCraftingRemainingItem(fuelItem);
                            fuelItem.decrement(1);
                            if (fuelItem.isEmpty()) {
                                this.fuelContainer.setStack(firstEmptySlot, containerItem);
                            }

                            hasChanged = true;
                        }
                        break;
                    }
                }
            }

            firstEmptySlot = -1;
            int firstTransferSlot = -1;

            ItemStack itemStack;
            int i;
            for(i = 0; i < this.processingContainer.size(); ++i) {
                itemStack = this.processingContainer.getStack(i);
                if (!itemStack.isEmpty()) {
                    if (this.slotCookTime[i] != -1) {
                        double maxCookTime = 200.0 * CookingForBlockheadsConfig.getActive().ovenCookTimeMultiplier;
                        if ((double)this.slotCookTime[i] >= maxCookTime && firstTransferSlot == -1) {
                            firstTransferSlot = i;
                        } else {
                            if (this.furnaceBurnTime > 0) {
                                this.slotCookTime[i]++;
                            }

                            if ((double)this.slotCookTime[i] >= maxCookTime) {
                                ItemStack smeltingResult = this.getSmeltingResult(itemStack);
                                if (!smeltingResult.isEmpty()) {
                                    ItemStack resultStack = smeltingResult.copy();
                                    this.processingContainer.setStack(i, resultStack);
                                    Balm.getEvents().fireEvent(new OvenCookedEvent(level, this.pos, resultStack));
                                    this.slotCookTime[i] = -1;
                                    if (firstTransferSlot == -1) {
                                        firstTransferSlot = i;
                                    }
                                }
                            }
                        }
                    } else if (firstTransferSlot == -1) {
                        firstTransferSlot = i;
                    }
                } else if (firstEmptySlot == -1) {
                    firstEmptySlot = i;
                }
            }

            if (firstTransferSlot != -1) {
                containerItem = this.processingContainer.getStack(firstTransferSlot);
                containerItem = ContainerUtils.insertItemStacked(this.outputContainer, containerItem, false);
                this.processingContainer.setStack(firstTransferSlot, containerItem);
                if (containerItem.isEmpty()) {
                    this.slotCookTime[firstTransferSlot] = 0;
                }

                hasChanged = true;
            }

            if (firstEmptySlot != -1) {
                for(i = 0; i < this.inputContainer.size(); ++i) {
                    itemStack = this.inputContainer.getStack(i);
                    if (!itemStack.isEmpty()) {
                        this.processingContainer.setStack(firstEmptySlot, itemStack.split(1));
                        if (itemStack.getCount() <= 0) {
                            this.inputContainer.setStack(i, ItemStack.EMPTY);
                        }
                        break;
                    }
                }
            }
        }

        if (hasChanged) {
            this.markDirty();
        }

    }

    public ItemStack getSmeltingResult(ItemStack itemStack) {
        ItemStack result = CookingRegistry.getSmeltingResult(itemStack);
        if (!result.isEmpty()) {
            return result;
        } else {
            this.singleSlotRecipeWrapper.setStack(0, itemStack);
            Recipe<?> recipe = this.world.getRecipeManager().getFirstMatch(RecipeType.SMELTING, this.singleSlotRecipeWrapper, this.world).orElse(null);
            if (recipe != null) {
                result = recipe.getOutput();
                if (!result.isEmpty() && result.getItem().isFood()) {
                    return result;
                }
            }

            return !result.isEmpty() && CookingRegistry.isNonFoodRecipe(result) ? result : ItemStack.EMPTY;
        }
    }

    public static boolean isItemFuel(ItemStack itemStack) {
        if (CookingForBlockheadsConfig.getActive().ovenRequiresCookingOil) {
            return itemStack.isIn(Compat.getCookingOilTag());
        } else {
            return getBurnTime(itemStack) > 0;
        }
    }

    protected static int getBurnTime(ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return 0;
        } else {
            return CookingForBlockheadsConfig.getActive().ovenRequiresCookingOil && itemStack.isIn(Compat.getCookingOilTag()) ? 800 : Balm.getHooks().getBurnTime(itemStack);
        }
    }

    private boolean shouldConsumeFuel() {
        for(int i = 0; i < this.processingContainer.size(); ++i) {
            ItemStack cookingStack = this.processingContainer.getStack(i);
            if (!cookingStack.isEmpty() && this.slotCookTime[i] != -1) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void readNbt(NbtCompound tagCompound) {
        super.readNbt(tagCompound);
        this.container.deserialize(tagCompound.getCompound("ItemHandler"));
        this.furnaceBurnTime = tagCompound.getShort("BurnTime");
        this.currentItemBurnTime = tagCompound.getShort("CurrentItemBurnTime");
        this.slotCookTime = tagCompound.getIntArray("CookTimes");
        if (this.slotCookTime.length != 9) {
            this.slotCookTime = new int[9];
        }

        this.hasPowerUpgrade = tagCompound.getBoolean("HasPowerUpgrade");
        this.energyStorage.setEnergy(tagCompound.getInt("EnergyStored"));
        if (tagCompound.contains("CustomName", 8)) {
            this.customName = Text.Serializer.fromJson(tagCompound.getString("CustomName"));
        }

    }

    @Override
    public void writeNbt(NbtCompound tagCompound) {
        super.writeNbt(tagCompound);
        tagCompound.put("ItemHandler", this.container.serialize());
        tagCompound.putShort("BurnTime", (short)this.furnaceBurnTime);
        tagCompound.putShort("CurrentItemBurnTime", (short)this.currentItemBurnTime);
        tagCompound.putIntArray("CookTimes", ArrayUtils.clone(this.slotCookTime));
        tagCompound.putBoolean("HasPowerUpgrade", this.hasPowerUpgrade);
        tagCompound.putInt("EnergyStored", this.energyStorage.getEnergy());
        if (this.customName != null) {
            tagCompound.putString("CustomName", Text.Serializer.toJson(this.customName));
        }
    }

    public void balmFromClientTag(NbtCompound tag) {
    }

    public NbtCompound balmToClientTag(NbtCompound tag) {
        return tag;
    }

    public boolean hasPowerUpgrade() {
        return this.hasPowerUpgrade;
    }

    public void setHasPowerUpgrade(boolean hasPowerUpgrade) {
        this.hasPowerUpgrade = hasPowerUpgrade;
        BlockState state = this.world.getBlockState(this.pos);
        this.world.setBlockState(this.pos, (BlockState)state.with(OvenBlock.POWERED, hasPowerUpgrade));
        this.markDirty();
    }

    public boolean isBurning() {
        return this.furnaceBurnTime > 0;
    }

    public float getBurnTimeProgress() {
        return this.currentItemBurnTime == 0 && this.furnaceBurnTime > 0 ? 1.0F : (float)this.furnaceBurnTime / (float)this.currentItemBurnTime;
    }

    public float getCookProgress(int i) {
        return (float)this.slotCookTime[i] / (float)(200.0 * CookingForBlockheadsConfig.getActive().ovenCookTimeMultiplier);
    }

    public ItemStack smeltItem(ItemStack itemStack) {
        return ContainerUtils.insertItemStacked(this.inputContainer, itemStack, false);
    }

    public ItemStack getToolItem(int i) {
        return this.toolsContainer.getStack(i);
    }

    public void setToolItem(int i, ItemStack itemStack) {
        this.toolsContainer.setStack(i, itemStack);
    }

    public Inventory getContainer(Direction side) {
        if (side == null) {
            return this.getContainer();
        } else {
            SubContainer subContainer;
            switch (side) {
                case UP -> subContainer = this.inputContainer;
                case DOWN -> subContainer = this.outputContainer;
                default -> subContainer = this.fuelContainer;
            }

            return subContainer;
        }
    }

    public List<BalmProvider<?>> getProviders() {
        return List.of(new BalmProvider<>(IKitchenItemProvider.class, this.itemProvider), new BalmProvider<>(IKitchenSmeltingProvider.class, this));
    }

    public Inventory getInputContainer() {
        return this.inputContainer;
    }

    public Inventory getFuelContainer() {
        return this.fuelContainer;
    }

    public Direction getFacing() {
        return this.facing == null ? Direction.NORTH : this.facing;
    }

    public @Nullable ScreenHandler createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return new StoveScreenHandlerBalm(i, playerInventory, this);
    }

    public Box balmGetRenderBoundingBox() {
        return new Box(this.pos.add(-1, 0, -1), this.pos.add(2, 1, 2));
    }

    public Text getName() {
        return this.customName != null ? this.customName : this.getDefaultName();
    }

    public void setCustomName(Text customName) {
        this.customName = customName;
        this.markDirty();
    }

    public boolean hasCustomName() {
        return this.customName != null;
    }

    public @Nullable Text getCustomName() {
        return this.customName;
    }

    public Text getDisplayName() {
        return this.getName();
    }

    public Text getDefaultName() {
        return new TranslatableText("container.cookingforblockheads.oven");
    }

    public Inventory getContainer() {
        return this.container;
    }

    public PropertyDelegate getContainerData() {
        return this.dataAccess;
    }

    public EnergyStorage getEnergyStorage() {
        return this.energyStorage;
    }

    @Nullable
    @Override
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        NbtCompound nbt = super.toInitialChunkDataNbt();
        nbt.put("ItemHandler", this.container.serialize());
        return nbt;
    }

    @Override
    public void handleUpdateTag(NbtCompound tag) {
        this.readNbt(tag);
        super.handleUpdateTag(tag);
    }

    @Override
    public void onDataPacket(ClientConnection net, BlockEntityUpdateS2CPacket pkt) {
        super.onDataPacket(net, pkt);
        this.container.deserialize(pkt.getNbt().getCompound("ItemHandler"));
    }

    protected void onContainerOpen(World world, BlockPos pos, BlockState state) {
        if (state.getBlock() instanceof StoveBlock){
            StoveBlockEntityBalm.this.playSound(state, SoundEvents.BLOCK_IRON_TRAPDOOR_OPEN);
            StoveBlockEntityBalm.this.setOpen(state, true);
        }
    }

    protected void onContainerClose(World world, BlockPos pos, BlockState state) {
        if (state.getBlock() instanceof StoveBlock) {
            StoveBlockEntityBalm.this.playSound(state, SoundEvents.BLOCK_IRON_TRAPDOOR_CLOSE);
            StoveBlockEntityBalm.this.setOpen(state, false);
        }
    }

    void setOpen(BlockState state, boolean open) {
        this.world.setBlockState(this.getPos(), state.with(Properties.OPEN, open), 3);
    }

    public void onClose(PlayerEntity player) {
        if (!this.removed && !player.isSpectator()) {
            this.onContainerClose(this.getWorld(), this.getPos(), this.getCachedState());
        }
    }

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
}
