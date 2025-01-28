package com.unlikepaladin.pfm.registry.dynamic;

import com.unlikepaladin.pfm.blocks.DyeableFurnitureBlock;
import com.unlikepaladin.pfm.data.materials.VariantBase;
import net.minecraft.block.Block;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FurnitureEntry<T extends Block> {
    private final HashMap<VariantBase<?>, Set<T>> variantToBlockMapList = new LinkedHashMap<>();

    private final HashMap<VariantBase<?>, T> variantToBlockMap = new LinkedHashMap<>();
    private final HashMap<VariantBase<?>, T> variantToBlockMapNonBase = new LinkedHashMap<>();
    final List<T> allBlocks = new ArrayList<>();

    public <J extends Block> void addBlock(VariantBase variantBase, J block1, boolean base){
        if (block1 != null) {
            T block = (T) block1;
            if (base)
                variantToBlockMap.put(variantBase, block);
            else
                variantToBlockMapNonBase.put(variantBase, block);

            if (!variantToBlockMapList.containsKey(variantBase)) {
                variantToBlockMapList.put(variantBase, new HashSet<>());
            }
            variantToBlockMapList.get(variantBase).add(block);
            allBlocks.add(block);
            this.type = (Class<T>) block1.getClass();
        } else {
            throw new UnsupportedOperationException("Block was not instance of T");
        }
    }

    public Optional<Block> getEntryFromVariant(VariantBase<?> variant) {
        return getEntryFromVariant(variant, false);
    }
    public Optional<Block> getEntryFromVariantAndColor(VariantBase<?> variant, DyeColor color) {
        if (variantToBlockMapList.containsKey(variant)) {
            for (T block : variantToBlockMapList.get(variant)) {
                if (block instanceof DyeableFurnitureBlock dyeableFurnitureBlock && dyeableFurnitureBlock.getPFMColor() == color) {
                    return Optional.of(block);
                }
            }
        }
        return getEntryFromVariant(variant, false);
    }

    public List<Identifier> getVariants() {
        return variantToBlockMap.keySet().stream().map(variantBase -> variantBase.identifier).toList();
    }

    public Optional<Block> getEntryFromVariant(VariantBase<?> variant, boolean stripped) {
        if (!stripped && variantToBlockMap.containsKey(variant)) {
            return Optional.of(variantToBlockMap.get(variant));
        } else if (variantToBlockMapNonBase.containsKey(variant)) {
            return Optional.of(variantToBlockMapNonBase.get(variant));
        }
        return Optional.empty();
    }

    private Class<T> type;

    public VariantBase<?> getVariantFromEntry(Block block) {
        if (block.getClass() == getTClass()) {
            for (Map.Entry<VariantBase<?>, T> entry : variantToBlockMap.entrySet()) {
                if (entry.getValue().equals(block)) {
                    return entry.getKey();
                }
            }
            for (Map.Entry<VariantBase<?>, T> entry : variantToBlockMapNonBase.entrySet()) {
                if (entry.getValue().equals(block)) {
                    return entry.getKey();
                }
            }
            for (Map.Entry<VariantBase<?>, Set<T>> entry : variantToBlockMapList.entrySet()) {
                if (entry.getValue().contains(block)) {
                    return entry.getKey();
                }
            }
        }
        return null;
    }

    public void addBlock(T block){
        if (block != null) {
            allBlocks.add(block);
        } else {
            throw new UnsupportedOperationException("Block was not instance of T");
        }
    }

    public HashMap<VariantBase<?>, T> getVariantToBlockMap() {
        return variantToBlockMap;
    }

    public HashMap<VariantBase<?>, T> getVariantToBlockMapNonBase() {
        return variantToBlockMapNonBase;
    }

    public Optional<T> getFromIdentifier(Identifier identifier, boolean base) {
        if (base){
            for (VariantBase<?> variantBase : variantToBlockMap.keySet()) {
                if (variantBase.identifier.equals(identifier)) {
                    return Optional.of(variantToBlockMap.get(variantBase));
                }
            }
        } else {
            for (VariantBase<?> variantBase : variantToBlockMapNonBase.keySet()) {
                if (variantBase.identifier.equals(identifier)) {
                    return Optional.of(variantToBlockMapNonBase.get(variantBase));
                }
            }
        }
        for (T block : allBlocks)  {
            if (block.getTranslationKey().contains(identifier.getPath())) {
                return Optional.of(block);
            }
        }
        return Optional.empty();
    }

    public T getFromVanillaWoodType(BoatEntity.Type woodType, boolean base){
        if (base) {
            for (VariantBase<?> variant : variantToBlockMap.keySet()){
                if (variant.getBaseBlock().equals(woodType.getBaseBlock())) {
                    return variantToBlockMap.get(variant);
                }
            }
        } else {
            for (VariantBase<?> variant : variantToBlockMapNonBase.keySet()){
                if (variant.getBaseBlock().equals(woodType.getBaseBlock())) {
                    return variantToBlockMapNonBase.get(variant);
                }
            }
        }
        return null;
    }

    public HashMap<VariantBase<?>, Set<T>> getVariantToBlockMapList() {
        return variantToBlockMapList;
    }

    public List<T> getAllBlocks() {
        return allBlocks;
    }

    public Class<T> getTClass() {
        return this.type;
    }
}
