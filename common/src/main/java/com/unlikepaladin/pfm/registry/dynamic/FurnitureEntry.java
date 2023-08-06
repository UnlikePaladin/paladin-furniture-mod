package com.unlikepaladin.pfm.registry.dynamic;

import com.unlikepaladin.pfm.data.materials.VariantBase;
import net.minecraft.block.Block;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.Identifier;

import java.util.*;
import java.util.function.Supplier;

public class FurnitureEntry<T extends Block> {
    private final HashMap<VariantBase<?>, List<T>> variantToBlockMapList = new LinkedHashMap<>();

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
                variantToBlockMapList.put(variantBase, new ArrayList<>());
            }
            variantToBlockMapList.get(variantBase).add(block);
            allBlocks.add(block);
            this.type = (Class<T>) block1.getClass();
        } else {
            throw new UnsupportedOperationException("Block was not instance of T");
        }
    }
    private Class<T> type;

    public VariantBase<?> getVariantFromEntry(Block block) {
        if (block.getClass() == getTClass())
            if (variantToBlockMap.containsValue(block)) {
                for (Map.Entry<VariantBase<?>, T> entry:
                     variantToBlockMap.entrySet()) {
                    if (entry.getValue().equals(block))
                        return entry.getKey();
                }}
            else if (variantToBlockMapNonBase.containsValue(block)) {
                for (Map.Entry<VariantBase<?>, T> entry:
                        variantToBlockMapNonBase.entrySet()) {
                    if (entry.getValue().equals(block))
                        return entry.getKey();
                }}
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

    public HashMap<VariantBase<?>, List<T>> getVariantToBlockMapList() {
        return variantToBlockMapList;
    }

    public List<T> getAllBlocks() {
        return allBlocks;
    }

    public Class<T> getTClass() {
        return this.type;
    }
}
