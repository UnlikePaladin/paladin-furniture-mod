package com.unlikepaladin.pfm.items;

import com.unlikepaladin.pfm.blocks.PendantBlock;
import com.unlikepaladin.pfm.blocks.PowerableBlock;
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
import net.minecraft.nbt.NbtList;
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LightSwitchItem extends BlockItem {
    private Block block;

    public LightSwitchItem(Block block, Settings settings) {
        super(block, settings);
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
        BlockPos pos = context.getBlockPos();
        BlockState state = context.getWorld().getBlockState(context.getBlockPos());
        Block block = state.getBlock();
        if(block instanceof PowerableBlock){
           if (block instanceof PendantBlock){
               boolean isSingle = (!state.get(PendantBlock.DOWN) && !state.get(PendantBlock.UP));
               boolean isRoot = (state.get(PendantBlock.DOWN) && !state.get(PendantBlock.UP));

               if (isSingle || isRoot) {
                    addLight(context.getStack(), pos);
               }
               else {
                    if (context.getWorld().isClient)
                        context.getPlayer().sendMessage(Text.translatable("message.pfm.light_switch_not_canopy"), false);
               }
           }
           else {
               addLight(context.getStack(), pos);
           }
            return ActionResult.SUCCESS;
        }
        return super.useOnBlock(context);
    }

    @Override
    protected boolean canPlace(ItemPlacementContext context, BlockState state) {
        BlockPos pos = context.getBlockPos();
        WorldView world = context.getWorld();
        Direction side = context.getSide();
        NbtList lights = getLights(context.getStack());
        boolean canPlace = state.getBlock().canPlaceAt(state, world, pos) && side.getAxis().isHorizontal();

        if (!canPlace) {
            return false;
        }
        if (lights != null) {
            ArrayList<BlockPos> removedLights = new ArrayList<>();
            ArrayList<BlockPos> lightOffsets = new ArrayList<>();
            for (Iterator<NbtElement> iterator = lights.iterator(); iterator.hasNext();) {
                NbtElement nbtElement = iterator.next();
                BlockPos lightPos = BlockPos.fromLong(((NbtLong) nbtElement).longValue());
                double distance = Math.sqrt(lightPos.getSquaredDistance(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5));
                if (distance > 16) {
                    removedLights.add(BlockPos.fromLong(((NbtLong) nbtElement).longValue()));
                    iterator.remove();
                } else {
                    lightOffsets.add(pos.subtract(lightPos));
                }
            }
            context.getStack().setNbt(new NbtCompound());
            for (BlockPos blockPos : lightOffsets) {
                addLight(context.getStack(), blockPos);
            }

            if (!removedLights.isEmpty() && context.getWorld().isClient){
                context.getPlayer().sendMessage(Text.translatable("message.pfm.light_switch_far", removedLights.toString()), false);
            }
        }
        return true;
    }

    private void addLight(ItemStack stack, BlockPos pos)
    {
        NbtCompound nbtCompound = createTag(stack);
        if(!nbtCompound.contains("BlockEntityTag", NbtElement.COMPOUND_TYPE))
        {
            nbtCompound.put("BlockEntityTag", new NbtCompound());
        }

        NbtCompound blockEntityTag = nbtCompound.getCompound("BlockEntityTag");
        if(!blockEntityTag.contains("lights", NbtElement.LIST_TYPE))
        {
            blockEntityTag.put("lights", new NbtList());
        }

        NbtList tagList = (NbtList) blockEntityTag.get("lights");
        if(!containsLight(tagList, pos))
        {
            tagList.add(NbtLong.of(pos.asLong()));
        }
    }

    private boolean containsLight(NbtList tagList, BlockPos pos)
    {
        for(int i = 0; i < tagList.size(); i++)
        {
            NbtLong tagLong = (NbtLong) tagList.get(i);
            if(tagLong.longValue() == pos.asLong())
            {
                return true;
            }
        }
        return false;
    }

    @Nullable
    public static NbtList getLights(ItemStack stack)
    {
        if(stack.hasNbt()) {
            NbtCompound nbtCompound = stack.getNbt();
            if(nbtCompound.contains("BlockEntityTag", NbtElement.COMPOUND_TYPE))
            {
                NbtCompound blockEntityTag = nbtCompound.getCompound("BlockEntityTag");
                if(blockEntityTag.contains("lights", NbtElement.LIST_TYPE))
                {
                    return (NbtList) blockEntityTag.get("lights");
                }
            }
        }
        return null;
    }

    private static NbtCompound createTag(ItemStack stack)
    {
        if(!stack.hasNbt())
        {
            stack.setNbt(new NbtCompound());
        }
        return stack.getNbt();
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if (stack.hasNbt() && getLights(stack) != null) {
            int lightNum = getLights(stack).size();
            tooltip.add(Text.translatable("tooltip.pfm.light_switch_connected", lightNum));
        }
        super.appendTooltip(stack, world, tooltip, context);
    }
}
