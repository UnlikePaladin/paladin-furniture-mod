package com.unlikepaladin.pfm.blocks.models.basicLamp.fabric;

import com.unlikepaladin.pfm.blocks.BasicLampBlock;
import com.unlikepaladin.pfm.blocks.blockentities.LampBlockEntity;
import com.unlikepaladin.pfm.blocks.models.fabric.PFMFabricBakedModel;
import com.unlikepaladin.pfm.data.materials.BlockType;
import com.unlikepaladin.pfm.data.materials.WoodVariant;
import com.unlikepaladin.pfm.data.materials.WoodVariantRegistry;
import com.unlikepaladin.pfm.items.PFMComponents;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.World;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

public class FabricBasicLampModel extends PFMFabricBakedModel {
    public FabricBasicLampModel(ModelBakeSettings settings, List<BakedModel> modelParts) {
        super(settings, modelParts);
    }

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    @Override
    public void emitBlockQuads(QuadEmitter context, BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, Predicate<@Nullable Direction> cullTest) {
        WoodVariant variant = WoodVariantRegistry.OAK;
        BlockEntity entity = blockView.getBlockEntity(pos);
        int onOffset = state.get(Properties.LIT) ? 1 : 0;
        if (entity instanceof LampBlockEntity) {
            variant = ((LampBlockEntity) entity).getVariant();
        }
        boolean up = blockView.getBlockState(pos.up()).getBlock() instanceof BasicLampBlock;
        boolean down = blockView.getBlockState(pos.down()).getBlock() instanceof BasicLampBlock;
        pushTextureTransform(context, getOakStrippedLogSprite(), getVariantStrippedLogSprite(variant));
        if (up && down) {
            (getTemplateBakedModels().get(1)).emitBlockQuads(context, blockView, state, pos, randomSupplier, cullTest);
        } else if (up) {
            (getTemplateBakedModels().get(0)).emitBlockQuads(context, blockView, state, pos, randomSupplier, cullTest);
        } else if (down)
        {
            (getTemplateBakedModels().get(3)).emitBlockQuads(context, blockView, state, pos, randomSupplier, cullTest);
            (getTemplateBakedModels().get(5+onOffset)).emitBlockQuads(context, blockView, state, pos, randomSupplier, cullTest);
            (getTemplateBakedModels().get(4)).emitBlockQuads(context, blockView, state, pos, randomSupplier, cullTest);
        }
        else {
            (getTemplateBakedModels().get(4)).emitBlockQuads(context, blockView, state, pos, randomSupplier, cullTest);
            (getTemplateBakedModels().get(2)).emitBlockQuads(context, blockView, state, pos, randomSupplier, cullTest);
            (getTemplateBakedModels().get(5+onOffset)).emitBlockQuads(context, blockView, state, pos, randomSupplier, cullTest);
        }
        context.popTransform();
    }

    @Override
    public void emitItemQuads(QuadEmitter context, Supplier<Random> randomSupplier) {
        WoodVariant variant = WoodVariantRegistry.OAK;
        if (getVariant() != null) {
            variant = (WoodVariant) getVariant();
        }
        pushTextureTransform(context, getOakStrippedLogSprite(), getVariantStrippedLogSprite(variant));
        (getTemplateBakedModels().get(4)).emitItemQuads(context, randomSupplier);
        (getTemplateBakedModels().get(2)).emitItemQuads(context, randomSupplier);
        (getTemplateBakedModels().get(5)).emitItemQuads(context, randomSupplier);
        context.popTransform();
    }

    @Override
    public ModelTransformation getTransformation() {
        return getTemplateBakedModels().get(2).getTransformation();
    }

    static List<Sprite> oakSprite = new ArrayList<>();
    static List<Sprite> getOakStrippedLogSprite() {
        if (!oakSprite.isEmpty())
            return oakSprite;
        Sprite wood = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE,  Identifier.of("minecraft:block/stripped_oak_log")).getSprite();
        oakSprite.add(wood);
        return oakSprite;
    }

    Map<WoodVariant, List<Sprite>> sprites = new HashMap<>();
    List<Sprite> getVariantStrippedLogSprite(WoodVariant variant) {
        if (sprites.containsKey(variant))
            return sprites.get(variant);

        Sprite wood = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, variant.getTexture(BlockType.STRIPPED_LOG)).getSprite();
        List<Sprite> spriteList = new ArrayList<>();
        spriteList.add(wood);
        sprites.put(variant, spriteList);
        return spriteList;
    }

    @Override
    public Sprite pfm$getParticle(BlockState state) {
        return getTemplateBakedModels().get(4).getParticleSprite();
    }

    @Override
    public Sprite pfm$getParticle(World world, BlockPos pos, BlockState state) {
        BlockEntity entity = world.getBlockEntity(pos);
        WoodVariant variant = WoodVariantRegistry.OAK;
        if (world.getBlockEntity(pos) instanceof LampBlockEntity) {
            variant = ((LampBlockEntity) entity).getVariant();
        }
        return getVariantStrippedLogSprite(variant).get(0);
    }
}
