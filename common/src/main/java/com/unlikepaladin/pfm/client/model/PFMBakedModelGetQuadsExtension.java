package com.unlikepaladin.pfm.client.model;

import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import net.minecraft.util.math.random.Random;

public interface PFMBakedModelGetQuadsExtension {
    List<BakedQuad> getQuads(@Nullable Direction face, Random random);

    List<BakedQuad> getQuadsCached(@Nullable Direction face, Random random);
}
