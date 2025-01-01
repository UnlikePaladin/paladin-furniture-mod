package com.unlikepaladin.pfm.items;

import com.unlikepaladin.pfm.blocks.BasicShowerHandleBlock;
import com.unlikepaladin.pfm.blocks.BasicShowerHeadBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtLong;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
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
            stack.setNbt(null);
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
        Direction playerFacing = context.getPlayerFacing();
        Direction placeDirection = context.getSide();

        boolean canPlace = state.getBlock().canPlaceAt(state, world, pos) && placeDirection.getAxis().isHorizontal();
        if (!canPlace) {
            return false;
        }
        if (showerHeadLong != null) {
            BlockPos headPos = BlockPos.fromLong(showerHeadLong.longValue());
            BlockPos placedPos = pos.offset(playerFacing);

            double distance = Math.sqrt(headPos.getSquaredDistance(placedPos.getX() + 0.5, placedPos.getY() + 0.5, placedPos.getZ() + 0.5));
            if (distance > 16 && world.isClient()){
                context.getPlayer().sendMessage(new TranslatableText("message.pfm.shower_handle_far", headPos.toString()), false);
            }
            if (distance > 16) {
                context.getStack().setNbt(null);
            } else {
                setShowerHeadPosNBT(context.getStack(), pos.subtract(headPos));
            }
            return state.getBlock().canPlaceAt(state, world, pos) && placeDirection.getAxis().isHorizontal();
        }
        return true;
    }

    private void setShowerHeadPosNBT(ItemStack stack, BlockPos pos)
    {
        NbtCompound nbtCompound = createNbt(stack);
        if(!nbtCompound.contains("BlockEntityTag", NbtElement.COMPOUND_TYPE))
        {
            nbtCompound.put("BlockEntityTag", new NbtCompound());
        }

        NbtCompound blockEntityTag = nbtCompound.getCompound("BlockEntityTag");
        if(!blockEntityTag.contains("showerHead", NbtElement.LONG_TYPE))
        {
            blockEntityTag.put("showerHead", NbtLong.of(0));
        }

        NbtLong showerHeadPos = (NbtLong) blockEntityTag.get("showerHead");
        if(showerHeadPos.longValue() != pos.asLong()) {
            blockEntityTag.put("showerHead", NbtLong.of(pos.asLong()));
        }
    }

    @Nullable
    public static NbtLong getShowerHead(ItemStack stack)
    {
        if (stack.hasNbt()) {
            NbtCompound stackNbt = stack.getNbt();
            if(stackNbt.contains("BlockEntityTag", NbtElement.COMPOUND_TYPE))
            {
                NbtCompound blockEntityTag = stackNbt.getCompound("BlockEntityTag");
                if(blockEntityTag.contains("showerHead", NbtElement.LONG_TYPE))
                {
                    return (NbtLong) blockEntityTag.get("showerHead");
                }
            }
        }
        return null;
    }

    private static NbtCompound createNbt(ItemStack stack)
    {
        if(!stack.hasNbt())
        {
            stack.setNbt(new NbtCompound());
        }
        return stack.getNbt();
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if (stack.hasNbt() && getShowerHead(stack) != null) {
            tooltip.add(new TranslatableText("tooltip.pfm.shower_handle_connected", 1));
        }
        super.appendTooltip(stack, world, tooltip, context);
    }
}
