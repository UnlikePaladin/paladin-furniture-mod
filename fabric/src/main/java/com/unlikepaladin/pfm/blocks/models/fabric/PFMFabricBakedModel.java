package com.unlikepaladin.pfm.blocks.models.fabric;

import com.mojang.datafixers.util.Pair;
import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.blocks.models.AbstractBakedModel;
import com.unlikepaladin.pfm.client.fabric.PFMBakedModelParticleExtension;
import com.unlikepaladin.pfm.client.model.PFMBakedModelGetQuadsExtension;
import com.unlikepaladin.pfm.client.model.PFMBakedModelSetPropertiesExtension;
import com.unlikepaladin.pfm.data.materials.VariantBase;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.model.SpriteFinder;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexFormatElement;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

public abstract class PFMFabricBakedModel extends AbstractBakedModel implements FabricBakedModel, PFMBakedModelParticleExtension, PFMBakedModelSetPropertiesExtension {
    protected BlockState blockState;
    protected VariantBase<?> variant;

    public PFMFabricBakedModel(ModelBakeSettings settings, List<BakedModel> bakedModels) {
        super(settings, bakedModels);
    }

    public void pushTextureTransform(QuadEmitter context, Sprite sprite) {
        context.pushTransform(quad -> {
            Sprite originalSprite = SpriteFinder.get(MinecraftClient.getInstance().getBakedModelManager().getAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE)).find(quad, 0);
            if (originalSprite.getContents().getId() != sprite.getContents().getId()) {
                for (int index = 0; index < 4; index++) {
                    float frameU = originalSprite.getFrameFromU(quad.u(index));
                    float frameV = originalSprite.getFrameFromV(quad.v(index));
                    quad.uv(index, sprite.getFrameU(frameU), sprite.getFrameV(frameV));
                }
            }
            return true;
        });
    }
    public void pushTextureTransform(QuadEmitter context, List<Sprite> toReplace, List<Sprite> replacement) {
        pushTextureTransform(context, toReplace, replacement, SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
    }
    public void pushTextureTransform(QuadEmitter context, List<Sprite> toReplace, List<Sprite> replacement, Identifier atlasId) {
        context.pushTransform(quad -> {
            if (replacement != null && toReplace != null ){
                Sprite originalSprite = SpriteFinder.get(MinecraftClient.getInstance().getBakedModelManager().getAtlas(atlasId)).find(quad, 0);
                Identifier keyId = originalSprite.getContents().getId();
                int textureIndex = IntStream.range(0, toReplace.size())
                        .filter(i -> keyId.equals(toReplace.get(i).getContents().getId()))
                        .findFirst()
                        .orElse(-1);

                if (textureIndex != -1 && !toReplace.equals(replacement)) {
                    Sprite sprite = replacement.get(textureIndex);
                    for (int index = 0; index < 4; index++) {
                        float frameU = originalSprite.getFrameFromU(quad.u(index));
                        float frameV = originalSprite.getFrameFromV(quad.v(index));
                        quad.uv(index, sprite.getFrameU(frameU), sprite.getFrameV(frameV));
                    }
                }
            }
            return true;
        });
    }


    @Override
    public Sprite pfm$getParticle(World world, BlockPos pos, BlockState state) {
        return pfm$getParticle(state);
    }

    @Override
    public Sprite getParticleSprite() {
        return getTemplateBakedModels().get(0).getParticleSprite();
    }

    @Override
    public void setBlockStateProperty(BlockState state) {
        this.blockState = state;
    }

    @Override
    public void setVariant(VariantBase<?> variant) {
        this.variant = variant;
    }

    @Override
    public BlockState getBlockStateProperty() {
        return blockState;
    }

    @Override
    public VariantBase<?> getVariant() {
        return variant;
    }
}
