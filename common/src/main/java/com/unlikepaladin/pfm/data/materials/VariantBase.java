package com.unlikepaladin.pfm.data.materials;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.resource.featuretoggle.FeatureFlag;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.registry.Registry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

public abstract class VariantBase<T> implements StringIdentifiable, Comparable<VariantBase<T>> {
    private final BiMap<String, Object> children = HashBiMap.create();
    public final Identifier identifier;

    protected VariantBase(Identifier id) {
        this.identifier = id;
    }

    @Environment(EnvType.CLIENT)
    public abstract Identifier getTexture(BlockType type);

    public abstract String getPath();
    public abstract Block getBaseBlock();

    public abstract Block getSecondaryBlock();

    public abstract boolean isNetherWood();

    abstract public Material getVanillaMaterial();

    abstract public T getVariantType();

    abstract public List<FeatureFlag> getFeatureList();

    public String getNamespace() {
        return this.identifier.getNamespace();
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    @FunctionalInterface
    public interface SetFinder<T extends VariantBase<T>> extends Supplier<Optional<T>> {
        Optional<T> get();
    }

    public abstract boolean isVanilla();

    @Nullable
    protected <V> V findRelatedEntry(String appendedName, Registry<V> reg) {
        return findRelatedEntry(appendedName, "", reg);
    }

    @Nullable
    protected <V> V findRelatedEntry(String append, String postPend, Registry<V> reg) {
        if (this.identifier.getNamespace().equals("tfc")) {
            var o = reg.getOrEmpty(
                    new Identifier(identifier.getNamespace(), "wood/" + postPend + "/" + identifier.getPath()));
            if (o.isPresent()) return o.get();
        }
        String post = postPend.isEmpty() ? "" : "_" + postPend;
        Identifier[] targets = {
                new Identifier(identifier.getNamespace(), identifier.getPath() + "_" + append + post),
                new Identifier(identifier.getNamespace(), append + "_" + identifier.getPath() + post),
                new Identifier(identifier.getNamespace(), identifier.getPath() + "_planks_" + append + post),
        };
        V found = null;
        for (var r : targets) {
            if (reg.containsId(r)) {
                found = reg.get(r);
                break;
            }
        }
        return found;
    }

    public Set<Map.Entry<String, Object>> getChildren() {
        return this.children.entrySet();
    }

    @Nullable
    public Item getItemOfThis(String key) {
        var v = this.getChild(key);
        return v instanceof Item i ? i : null;
    }

    @Nullable
    public Block getBlockOfThis(String key) {
        var v = this.getChild(key);
        return v instanceof Block b ? b : null;
    }

    @Nullable
    public Object getChild(String key) {
        if (this.children.get(key) != null)
            return this.children.get(key);
        return getBaseBlock();
    }

    @Nullable
    public ItemConvertible getItemForRecipe(String key, Class<? extends Block> blockClass) {
        if (Objects.equals(key, "base"))
            return getBaseBlock();
        else if (Objects.equals(key, "secondary"))
            return getSecondaryBlock();
        else if (this.children.get(key) != null)
            return (ItemConvertible) this.children.get(key);
        return getBaseBlock();
    }


    public void addChild(String genericName, @Nullable Object itemLike) {
        if (itemLike != null) {
            this.children.put(genericName, itemLike);
            var v = DynamicBlockRegistry.getBlockSet(this.getClass());
            if (v != null) {
                v.mapBlockToType(itemLike, this);
            }
        }
    }

    public abstract void initializeChildrenBlocks();

    public abstract void initializeChildrenItems();

    public abstract Block mainChild();

    @Nullable
    public String getChildKey(Object child) {
        return children.inverse().get(child);
    }

    @Nullable
    public static Object changeType(Object current, VariantBase<?> originalMat, VariantBase<?> destinationMat) {
        if (destinationMat == originalMat) return current;
        String key = originalMat.getChildKey(current);
        if (key != null) {
            return destinationMat.getChild(key);
        }
        return null;
    }

    // for items
    @Nullable
    public static Item changeItemType(Item current, VariantBase<?> originalMat, VariantBase<?> destinationMat) {
        Object changed = changeType(current, originalMat, destinationMat);
        // if item swap fails try to swap blocks instead
        if (changed == null) {
            if (current instanceof BlockItem bi) {
                var blockChanged = changeType(bi.getBlock(), originalMat, destinationMat);
                if (blockChanged instanceof Block il) {
                    Item i = il.asItem();
                    if (i != Items.AIR) changed = i;
                }
            }
        }
        if (changed instanceof Item il) return il.asItem();
        return null;
    }

    // for blocks
    @Nullable
    public static Block changeBlockType(@NotNull Block current, VariantBase<?> originalMat, VariantBase<?> destinationMat) {
        Object changed = changeType(current, originalMat, destinationMat);
        // if block swap fails try to swap items instead
        if (changed == null) {
            if (current.asItem() != Items.AIR) {
                var itemChanged = changeType(current.asItem(), originalMat, destinationMat);
                if (itemChanged instanceof BlockItem bi) {
                    Item i = bi.asItem();
                    if (i != Items.AIR) changed = i;
                }
            }
        }
        if (changed instanceof Block b) return b;
        return null;
    }

    @Override
    public int compareTo(@NotNull VariantBase<T> o) {
        return identifier.compareTo(o.identifier);
    }
}
