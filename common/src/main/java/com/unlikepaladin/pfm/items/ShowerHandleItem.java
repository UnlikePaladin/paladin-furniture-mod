package com.unlikepaladin.pfm.items;

import com.unlikepaladin.pfm.blocks.BasicShowerHandleBlock;
import com.unlikepaladin.pfm.blocks.BasicShowerHeadBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtLong;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class ShowerHandleItem extends BlockItem {
    private Supplier<BasicShowerHandleBlock> block;

    public ShowerHandleItem(Supplier<BasicShowerHandleBlock> block, Settings settings) {
        super(block.get(), settings);
        this.block = block;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if (world.isClient) {
            return new TypedActionResult<>(ActionResult.FAIL, stack);
        }
        if (player.isSneaking()) {
            stack.remove(DataComponentTypes.BLOCK_ENTITY_DATA);
            createNbt(stack);
            return new TypedActionResult<>(ActionResult.SUCCESS, stack);
        }
        return new TypedActionResult<>(ActionResult.PASS, stack);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        super.useOnBlock(context);
        BlockPos pos = context.getBlockPos();
        BlockState state = context.getWorld().getBlockState(context.getBlockPos());
        Block block = state.getBlock();
        if(block instanceof BasicShowerHeadBlock){
            setShowerHeadPosNBT(context.getStack(), pos);
        }
        return ActionResult.SUCCESS;
    }

    @Override
    protected boolean canPlace(ItemPlacementContext context, BlockState state) {
        BlockPos pos = context.getBlockPos();
        WorldView world = context.getWorld();
        NbtLong showerHeadLong = getShowerHead(context.getStack());
        Direction playerFacing = context.getHorizontalPlayerFacing();
        Direction placeDirection = context.getSide();

        boolean canPlace = state.canPlaceAt(world, pos) && placeDirection.getAxis().isHorizontal();
        if (!canPlace) {
            return false;
        }
        if (showerHeadLong != null) {
            BlockPos headPos = BlockPos.fromLong(showerHeadLong.longValue());
            BlockPos placedPos = pos.offset(playerFacing);

            double distance = Math.sqrt(headPos.getSquaredDistance(placedPos.getX() + 0.5, placedPos.getY() + 0.5, placedPos.getZ() + 0.5));
            if (distance > 16 && world.isClient()){
                context.getPlayer().sendMessage(Text.translatable("message.pfm.shower_handle_far", headPos.toString()), false);
            }
            if (distance > 16) {
                context.getStack().remove(DataComponentTypes.BLOCK_ENTITY_DATA);
                createNbt(context.getStack());
            } else {
                setShowerHeadPosNBT(context.getStack(), pos.subtract(headPos));
            }
            return state.canPlaceAt(world, pos) && placeDirection.getAxis().isHorizontal();
        }
        return true;
    }

    private void setShowerHeadPosNBT(ItemStack stack, BlockPos pos) {
        NbtCompound nbtCompound = createNbt(stack);
        if(!nbtCompound.contains("showerHead", NbtElement.LONG_TYPE)) {
            nbtCompound.put("showerHead", NbtLong.of(0));
        }

        NbtLong showerHeadPos = (NbtLong) nbtCompound.get("showerHead");
        if(showerHeadPos.longValue() != pos.asLong()) {
            nbtCompound.put("showerHead", NbtLong.of(pos.asLong()));
        }
        stack.set(DataComponentTypes.BLOCK_ENTITY_DATA, NbtComponent.of(nbtCompound));
    }

    @Nullable
    public static NbtLong getShowerHead(ItemStack stack) {
        if (stack.contains(DataComponentTypes.BLOCK_ENTITY_DATA)) {
            NbtCompound blockEntityTag = stack.get(DataComponentTypes.BLOCK_ENTITY_DATA).getNbt();
            if(blockEntityTag.contains("showerHead", NbtElement.LONG_TYPE)) {
                return (NbtLong) blockEntityTag.get("showerHead");
            }
        }
        return null;
    }

    private static NbtCompound createNbt(ItemStack stack) {
        if(!stack.contains(DataComponentTypes.BLOCK_ENTITY_DATA))
        {
            NbtCompound nbtCompound = new NbtCompound();
            nbtCompound.putString("id", "pfm:shower_handle_block_entity");
            stack.set(DataComponentTypes.BLOCK_ENTITY_DATA, NbtComponent.of(nbtCompound));
        }
        return stack.get(DataComponentTypes.BLOCK_ENTITY_DATA).copyNbt();
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        if (stack.contains(DataComponentTypes.BLOCK_ENTITY_DATA) && getShowerHead(stack) != null) {
            tooltip.add(Text.translatable("tooltip.pfm.shower_handle_connected", 1));
        }
        super.appendTooltip(stack, context, tooltip, type);
    }
}
