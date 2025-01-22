package com.unlikepaladin.pfm.items.fabric;

import com.unlikepaladin.pfm.items.FurnitureGuideBook;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import vazkii.patchouli.api.PatchouliAPI;

public class FurnitureGuideBookImpl extends FurnitureGuideBook {
    public FurnitureGuideBookImpl(Item.Settings settings) {
        super(settings);
    }

    public static TypedActionResult<ItemStack> openBook(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient() && FabricLoader.getInstance().isModLoaded("patchouli")) {
                PatchouliAPI.get().openBookGUI((ServerPlayerEntity) user, new Identifier("pfm:guide_book"));
            return TypedActionResult.success(user.getStackInHand(hand));
        }
        else if (world.isClient && !FabricLoader.getInstance().isModLoaded("patchouli"))
        {
            Text text = new TranslatableText("message.pfm.patchouli_not_installed").setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://github.com/UnlikePaladin/paladins-furniture/wiki")));
            user.sendMessage(text,false);
        }
        return TypedActionResult.pass(user.getStackInHand(hand));
    }
}
