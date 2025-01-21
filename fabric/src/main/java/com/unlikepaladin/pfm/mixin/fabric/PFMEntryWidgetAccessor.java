package com.unlikepaladin.pfm.mixin.fabric;

import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.gui.widget.EntryWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EntryWidget.class)
public interface PFMEntryWidgetAccessor {
    @Invoker("getCurrentEntry")
    EntryStack pfm$getCurrentShowingStack();
}
