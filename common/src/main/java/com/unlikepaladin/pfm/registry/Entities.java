package com.unlikepaladin.pfm.registry;

import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.entity.ChairEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class Entities {
    public static final EntityType<ChairEntity> CHAIR = EntityType.Builder.create(ChairEntity::new, SpawnGroup.MISC).dimensions(0.0F, 0.0F).makeFireImmune().disableSummon().build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(PaladinFurnitureMod.MOD_ID, "chair")));

}
