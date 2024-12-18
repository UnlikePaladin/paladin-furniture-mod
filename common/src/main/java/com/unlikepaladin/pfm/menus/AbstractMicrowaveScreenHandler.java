package com.unlikepaladin.pfm.menus;

import com.unlikepaladin.pfm.blocks.blockentities.MicrowaveBlockEntity;
import com.unlikepaladin.pfm.fabric.menus.slots.SizeableSlot;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.recipe.book.RecipeBookType;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;

public abstract class AbstractMicrowaveScreenHandler extends AbstractRecipeScreenHandler {
    private final Inventory inventory;
    private final PropertyDelegate propertyDelegate;
    protected final World world;
    private final RecipeType<? extends AbstractCookingRecipe> recipeType;
    private final RecipeBookType category;
    public boolean isActive;
    public MicrowaveBlockEntity microwaveBlockEntity;
    private final RecipePropertySet recipePropertySet;

    // Client Constructor
    protected AbstractMicrowaveScreenHandler(ScreenHandlerType<?> type, RecipeType<? extends AbstractCookingRecipe> recipeType, RecipeBookType category, int syncId, PlayerInventory playerInventory, MicrowaveData packet) {
        this((MicrowaveBlockEntity) playerInventory.player.getWorld().getBlockEntity(packet.pos()), type, recipeType, category, syncId, playerInventory, new SimpleInventory(1), new ArrayPropertyDelegate(2));
        this.isActive = packet.isActive();
    }

    // Server Constructor
    protected AbstractMicrowaveScreenHandler(MicrowaveBlockEntity microwaveBlockEntity, ScreenHandlerType<?> type, RecipeType<? extends AbstractCookingRecipe> recipeType, RecipeBookType category, int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate) {
        super(type, syncId);
        this.microwaveBlockEntity = microwaveBlockEntity;
        int i;
        this.recipeType = recipeType;
        this.category = category;
        AbstractMicrowaveScreenHandler.checkSize(inventory, 1);
        AbstractMicrowaveScreenHandler.checkDataCount(propertyDelegate, 2);
        this.inventory = inventory;
        inventory.onOpen(playerInventory.player);
        this.propertyDelegate = propertyDelegate;
        this.world = playerInventory.player.getEntityWorld();
        this.recipePropertySet = this.world.getRecipeManager().getPropertySet(RecipePropertySet.CAMPFIRE_INPUT);
        this.addSlot(new SizeableSlot(playerInventory.player, inventory, 0, 78, 40));

        for (i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
        this.addProperties(propertyDelegate);

    }

    @Override
    public void populateRecipeFinder(RecipeFinder finder) {
        if (this.inventory instanceof RecipeInputProvider) {
            ((RecipeInputProvider) this.inventory).provideRecipeInputs(finder);
        }
    }

    public boolean getActive() {
        return isActive;
    }

    @ExpectPlatform
    public static void setActive(MicrowaveBlockEntity blockEntity, boolean isActive){
        blockEntity.isActive = isActive;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasStack()) {
            ItemStack itemStack2 = slot.getStack();
            itemStack = itemStack2.copy();
            if (index == 0 ? !this.insertItem(itemStack2, 3, 37, false) : (this.isCookable(itemStack2) ? !this.insertItemToSlot(itemStack2, 0, 1, false) : (index >= 3 && index < 30 ? !this.insertItem(itemStack2, 30, 37, false) : index >= 30 && index < 37 && !this.insertItem(itemStack2, 3, 30, false)))) {
                return ItemStack.EMPTY;
            }
            if (itemStack2.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
            if (itemStack2.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTakeItem(player, itemStack2);
        }
        return itemStack;
    }

    protected boolean insertItemToSlot(ItemStack stack, int startIndex, int endIndex, boolean fromLast) {
        ItemStack itemStack;
        Slot slot;
        boolean bl = false;
        int i = startIndex;
        if (fromLast) {
            i = endIndex - 1;
        }
        if (!stack.isEmpty()) {
            i = fromLast ? endIndex - 1 : startIndex;
            while (fromLast ? i >= startIndex : i < endIndex) {
                slot = this.slots.get(i);
                itemStack = slot.getStack();
                if (itemStack.isEmpty() && slot.canInsert(stack)) {
                    if (stack.getCount() > slot.getMaxItemCount()) {
                        slot.setStack(stack.split(slot.getMaxItemCount()));
                    } else {
                        slot.setStack(stack.split(stack.getCount()));
                    }
                    slot.markDirty();
                    bl = true;
                    break;
                }
                if (fromLast) {
                    --i;
                    continue;
                }
                ++i;
            }
        }
        return bl;
    }

    protected boolean isCookable(ItemStack itemStack) {
        return this.recipePropertySet.canUse(itemStack);
    }

    public int getCookProgress() {
        int i = this.propertyDelegate.get(0);
        int j = this.propertyDelegate.get(1);
        if (j == 0 || i == 0) {
            return 0;
        }
        return i * 24 / j;
    }


    public boolean isActive() {
        return this.propertyDelegate.get(0) != 0;
    }

    @Override
    public RecipeBookType getCategory() {
        return this.category;
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        this.inventory.onClose(player);
    }

    public static final PacketCodec<RegistryByteBuf, MicrowaveData> PACKET_CODEC = PacketCodec.of(MicrowaveData::write, MicrowaveData::new);
    public record MicrowaveData(BlockPos pos, Boolean isActive) {
        public MicrowaveData(RegistryByteBuf buf) {
            this(buf.readBlockPos(), buf.readBoolean());
        }
        public void write(RegistryByteBuf buf) {
            buf.writeBlockPos(pos);
            buf.writeBoolean(isActive);
        }
    }

    @Override
    public PostFillAction fillInputSlots(boolean craftAll, boolean creative, RecipeEntry<?> recipe, ServerWorld world, PlayerInventory inventory) {
        final List<Slot> list = List.of(this.getSlot(0));
        return InputSlotFiller.fill(new InputSlotFiller.Handler<>() {
            @Override
            public void populateRecipeFinder(RecipeFinder finder) {
                AbstractMicrowaveScreenHandler.this.populateRecipeFinder(finder);
            }

            @Override
            public void clear() {
                list.forEach(slot -> slot.setStackNoCallbacks(ItemStack.EMPTY));
            }

            @Override
            public boolean matches(RecipeEntry<AbstractCookingRecipe> entry) {
                return entry.value().matches(new SingleStackRecipeInput(AbstractMicrowaveScreenHandler.this.inventory.getStack(0)), world);
            }
        }, 1, 1, List.of(this.getSlot(0)), list, inventory, (RecipeEntry<AbstractCookingRecipe>)recipe, craftAll, creative);
    }
}

