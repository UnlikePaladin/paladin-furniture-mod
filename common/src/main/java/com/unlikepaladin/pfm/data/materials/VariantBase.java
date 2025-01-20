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
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    abstract public Material getBaseMaterial();
    abstract public Material getSecondaryMaterial();

    abstract public T getVariantType();

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
            Optional<V> o = reg.getOrEmpty(
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
        for (Identifier r : targets) {
            if (reg.getOrEmpty(r).isPresent()) {
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
        Object v = this.getChild(key);
        return v instanceof Item ? (Item) v : null;
    }

    @Nullable
    public Block getBlockOfThis(String key) {
        Object v = this.getChild(key);
        return v instanceof Block ? (Block) v : null;
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
            VariantRegistryBase v = DynamicBlockRegistry.getBlockSet(this.getClass());
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
            if (current instanceof BlockItem) {
                BlockItem bi = (BlockItem) current;
                Object blockChanged = changeType(bi.getBlock(), originalMat, destinationMat);
                if (blockChanged instanceof Block) {
                    Item i = ((Block)blockChanged).asItem();
                    if (i != Items.AIR) changed = i;
                }
            }
        }
        if (changed instanceof Item) return ((Item)changed).asItem();
        return null;
    }

    // for blocks
    @Nullable
    public static Block changeBlockType(@NotNull Block current, VariantBase<?> originalMat, VariantBase<?> destinationMat) {
        Object changed = changeType(current, originalMat, destinationMat);
        // if block swap fails try to swap items instead
        if (changed == null) {
            if (current.asItem() != Items.AIR) {
                Object itemChanged = changeType(current.asItem(), originalMat, destinationMat);
                if (itemChanged instanceof BlockItem) {
                    Item i = ((BlockItem)itemChanged).asItem();
                    if (i != Items.AIR) changed = i;
                }
            }
        }
        if (changed instanceof Block) return (Block) changed;
        return null;
    }

    @Override
    public int compareTo(@NotNull VariantBase<T> o) {
        return identifier.compareTo(o.identifier);
    }
}
