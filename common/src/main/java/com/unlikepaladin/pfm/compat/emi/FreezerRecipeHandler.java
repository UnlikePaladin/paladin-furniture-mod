package com.unlikepaladin.pfm.compat.emi;
/*
import com.google.common.collect.Lists;
import com.unlikepaladin.pfm.menus.AbstractFreezerScreenHandler;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.handler.StandardRecipeHandler;
import net.minecraft.screen.slot.Slot;

import java.util.List;

public class FreezerRecipeHandler implements StandardRecipeHandler<AbstractFreezerScreenHandler> {
    private final EmiRecipeCategory category;

    public FreezerRecipeHandler(EmiRecipeCategory category) {
        this.category = category;
    }

    @Override
    public List<Slot> getInputSources(AbstractFreezerScreenHandler handler) {
        List<Slot> list = Lists.newArrayList();
        list.add(handler.getSlot(0));
        int invStart = 3;
        for (int i = invStart; i < invStart + 36; i++) {
            list.add(handler.getSlot(i));
        }
        return list;
    }

    @Override
    public List<Slot> getCraftingSlots(AbstractFreezerScreenHandler handler) {
        return List.of(handler.slots.get(0));
    }

    @Override
    public boolean supportsRecipe(EmiRecipe recipe) {
        return recipe.getCategory() == category && recipe.supportsRecipeTree();
    }
}*/
