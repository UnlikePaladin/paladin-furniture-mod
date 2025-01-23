package com.unlikepaladin.pfm.blocks.models;

import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.blocks.BasicChairBlock;
import com.unlikepaladin.pfm.blocks.LogStoolBlock;
import com.unlikepaladin.pfm.blocks.SimpleBedBlock;
import com.unlikepaladin.pfm.data.materials.BlockType;
import com.unlikepaladin.pfm.data.materials.VariantBase;
import com.unlikepaladin.pfm.data.materials.WoodVariant;
import com.unlikepaladin.pfm.data.materials.WoodVariantRegistry;
import com.unlikepaladin.pfm.registry.PaladinFurnitureModBlocksItems;
import com.unlikepaladin.pfm.registry.dynamic.LateBlockRegistry;
import com.unlikepaladin.pfm.runtime.data.PFMRecipeProvider;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class AbstractBakedModel implements BakedModel {
    protected final ModelBakeSettings settings;
    private final List<BakedModel> templateBakedModels;

    public static boolean reloading = false;
    public AbstractBakedModel(ModelBakeSettings settings, List<BakedModel> templateBakedModels) {
        this.settings = settings;
        this.templateBakedModels = templateBakedModels;
    }

    public List<BakedModel> getTemplateBakedModels() {
        return templateBakedModels;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, Random random) {
        return Collections.emptyList();
    }

    @Override
    public boolean useAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean hasDepth() {
        return true;
    }

    @Override
    public boolean isSideLit() {
        return true;
    }

    @Override
    public boolean isBuiltin() {
        return false;
    }

    @Override
    public ModelTransformation getTransformation() {
        return templateBakedModels.get(0).getTransformation();
    }

    @Override
    public ModelOverrideList getOverrides() {
        return ModelOverrideList.EMPTY;
    }

    Map<Block, VariantBase<?>> blockVariantMap = new HashMap<>();
    public VariantBase<?> getVariant(BlockState state) {
        VariantBase<?> variant;
        if (blockVariantMap.containsKey(state.getBlock())) {
            variant = blockVariantMap.get(state.getBlock());
        } else {
            variant = PaladinFurnitureModBlocksItems.furnitureEntryMap.get(state.getBlock().getClass()).getVariantFromEntry(state.getBlock());
            blockVariantMap.put(state.getBlock(), variant);
        }
        return variant;
    }


    public <T> List<Sprite> getSpriteList(T element) {
        List<Sprite> spriteList = getSpriteListWrapped(element);
        if (spriteList == null || spriteList.isEmpty() || spriteList.size() < 2) {
            VariantBase<?> variant = WoodVariantRegistry.OAK;
            SpriteIdentifier mainTexture = new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, ModelHelper.getTextureId(variant.getBaseBlock()));
            SpriteIdentifier secondTexture = new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, ModelHelper.getTextureId(variant.getSecondaryBlock()));
            return List.of(mainTexture.getSprite(), secondTexture.getSprite());
        }
        return spriteList;
    }
    private final Map<BlockItem, BlockState> blockItemBlockStateMap = new HashMap<>();
    /**
        Accepts an ItemStack, a BlockState, a Block or a BlockItem
     */
    public <T> List<Sprite> getSpriteListWrapped(T element) {
        if (element instanceof BlockState) {
            BlockState state = (BlockState) element;
            return getSpriteFromState(state);
        }
        else if (element instanceof BlockItem) {
            BlockItem blockItem = (BlockItem) element;
            if (blockItemBlockStateMap.containsKey(blockItem))
                return getSpriteFromState(blockItemBlockStateMap.get(blockItem));

            BlockState state = blockItem.getBlock().getDefaultState();
            blockItemBlockStateMap.put(blockItem, state);
            return getSpriteFromState(state);
        } else if (element instanceof Block) {
            Block block = (Block) element;
            if (spriteList.containsKey(block))
                return spriteList.get(block);

            BlockState state = block.getDefaultState();
            return getSpriteFromState(state);
        } else if (element instanceof ItemStack && ((ItemStack) element).getItem() instanceof BlockItem) {
            BlockItem blockItem = (BlockItem)((ItemStack) element).getItem();
            if (blockItemBlockStateMap.containsKey(blockItem))
                return getSpriteFromState(blockItemBlockStateMap.get(blockItem));

            BlockState state = blockItem.getBlock().getDefaultState();
            blockItemBlockStateMap.put(blockItem, state);
            return getSpriteFromState(state);
        } else if (element == null) {
            return Collections.singletonList(getTemplateBakedModels().get(0).getParticleSprite());
        } else {
            PaladinFurnitureMod.GENERAL_LOGGER.error("Invalid element for sprite list method");
        }
        return Collections.singletonList(getTemplateBakedModels().get(0).getParticleSprite());
    }
    protected List<Sprite> getBedSprites(DyeColor color, BlockState state) {
        List<Sprite> list = new ArrayList<>(3);
        VariantBase<?> variant = getVariant(state);

        return list;
    }
    private final Map<Block, List<Sprite>> spriteList = new HashMap<>();
    private List<Sprite> getSpriteFromState(BlockState state) {
        if (spriteList.containsKey(state.getBlock()))
            return spriteList.get(state.getBlock());

        VariantBase<?> variant = getVariant(state);
        boolean stripped = state.getBlock().getTranslationKey().contains("stripped");
        List<Sprite> list = new ArrayList<>(3);
        if (state.getBlock() instanceof SimpleBedBlock) {
            DyeColor color = ModelHelper.getColor(Registry.BLOCK.getId(state.getBlock()));
            SpriteIdentifier mainTexture = new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, variant.getTexture(BlockType.PLANKS));
            SpriteIdentifier spriteIdentifier = TexturedRenderLayers.BED_TEXTURES[color.getId()];
            list.add(mainTexture.getSprite());
            list.add(spriteIdentifier.getSprite());
        }  else if (state.getBlock() instanceof LogStoolBlock) {
            SpriteIdentifier mainTexture = stripped ? new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, variant.getTexture(BlockType.STRIPPED_LOG)) : new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, variant.getTexture(BlockType.LOG));
            SpriteIdentifier secondTexture = stripped ? new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, variant.getTexture(BlockType.STRIPPED_LOG_TOP)) : new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, variant.getTexture(BlockType.LOG_TOP));
            list.add(mainTexture.getSprite());
            list.add(secondTexture.getSprite());
        } else if (!state.getBlock().getTranslationKey().contains("_raw_")) {
            SpriteIdentifier mainTexture = stripped ? new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, variant.getTexture(BlockType.STRIPPED_LOG)) : new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, variant.getTexture(BlockType.PRIMARY));
            SpriteIdentifier secondTexture = stripped ? new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, variant.getTexture(BlockType.PRIMARY)) : new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, variant.getTexture(BlockType.SECONDARY));
            list.add(mainTexture.getSprite());
            list.add(secondTexture.getSprite());
        } else {
            SpriteIdentifier mainTexture = stripped ? new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, variant.getTexture(BlockType.STRIPPED_LOG)) : new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, variant.getTexture(BlockType.SECONDARY));
            list.add(mainTexture.getSprite());
            list.add(mainTexture.getSprite());
        }
        boolean isKitchen = state.getBlock().getTranslationKey().contains("kitchen_");
        if (isKitchen && !(variant instanceof WoodVariant)) {
            Pair<Block, Block> counterMaterials = PFMRecipeProvider.getCounterMaterials(variant);
            SpriteIdentifier mainTexture = new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, ModelHelper.getTextureId(counterMaterials.getLeft()));
            SpriteIdentifier secondTexture = new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, ModelHelper.getTextureId(counterMaterials.getRight()));
            list.set(0, mainTexture.getSprite());
            list.set(1, secondTexture.getSprite());
        }
        spriteList.put(state.getBlock(), list);
        return list;
    }
}