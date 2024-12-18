package com.unlikepaladin.pfm.items.fabric;

import com.unlikepaladin.pfm.items.FurnitureGuideBook;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import vazkii.patchouli.api.PatchouliAPI;

public class FurnitureGuideBookImpl extends FurnitureGuideBook {
    public FurnitureGuideBookImpl(Item.Settings settings) {
        super(settings);
    }

    public static ActionResult openBook(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient() && FabricLoader.getInstance().isModLoaded("patchouli")) {
                PatchouliAPI.get().openBookGUI((ServerPlayerEntity) user, Identifier.of("pfm:guide_book"));
            return ActionResult.SUCCESS;
        }
        else if (world.isClient && !FabricLoader.getInstance().isModLoaded("patchouli"))
        {
            user.sendMessage(Text.translatable("message.pfm.patchouli_not_installed"),false);
        }
        return ActionResult.PASS;
    }
}
