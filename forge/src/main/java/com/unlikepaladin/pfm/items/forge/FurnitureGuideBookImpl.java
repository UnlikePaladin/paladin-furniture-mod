package com.unlikepaladin.pfm.items.forge;

import com.unlikepaladin.pfm.items.FurnitureGuideBook;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraftforge.fml.ModList;
import vazkii.patchouli.api.PatchouliAPI;


public class FurnitureGuideBookImpl extends FurnitureGuideBook {
    public FurnitureGuideBookImpl(Item.Settings settings) {
        super(settings);
    }

    public static ActionResult openBook(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient() && ModList.get().isLoaded("patchouli")) {
            PatchouliAPI.get().openBookGUI((ServerPlayerEntity) user, Identifier.of("pfm:guide_book"));
            return ActionResult.SUCCESS;
        }
        else if (world.isClient && !ModList.get().isLoaded("patchouli"))
        {
            Text text = Text.translatable("message.pfm.patchouli_not_installed").setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://github.com/UnlikePaladin/paladins-furniture/wiki")));
            user.sendMessage(text,false);
        }
        return ActionResult.PASS;
    }
}
