package com.unlikepaladin.pfm.mixin;

import com.unlikepaladin.pfm.ducks.AbstractBlock$SettingsExtension;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.MapColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.function.Function;

@Mixin(AbstractBlock.Settings.class)
public class AbstractBlock$Settings implements AbstractBlock$SettingsExtension {
    @Shadow
    private Function<BlockState, MapColor> materialColorFactory;
    @Override
    public AbstractBlock.Settings pfm$setMapColor(MapColor color) {
        materialColorFactory = blockState -> color;
        return (AbstractBlock.Settings)(Object)(this);
    }
}
