package com.unlikepaladin.pfm.registry.dynamic.neoforge;

import com.mojang.datafixers.util.Pair;
import com.unlikepaladin.pfm.data.materials.DynamicBlockRegistry;
import com.unlikepaladin.pfm.mixin.neoforge.SimpleRegistryAccessor;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.SimpleRegistry;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class LateBlockRegistryNeoForge {
    private static boolean hasRegisteredBlockSets = false;
    private static Pair<List<Runnable>, List<Consumer<Registry<Item>>>> LATE_REGISTRATION_QUEUE = null;

    public static void addDynamicBlockRegistration(IEventBus bus) {
        if (LATE_REGISTRATION_QUEUE == null) {
            LATE_REGISTRATION_QUEUE = Pair.of(new ArrayList<>(), new ArrayList<>());
            bus.addListener(EventPriority.HIGHEST, LateBlockRegistryNeoForge::registerLateBlockAndItems);
        }
        Consumer<RegisterEvent> eventConsumer = registerEvent ->  {
            if (registerEvent.getRegistryKey().equals(Registries.BLOCK.getKey())) {
                Runnable blockEvent = () -> {
                    Registry<?> registry = registerEvent.getRegistry();
                    if (registry instanceof SimpleRegistry<?> fr) {
                        boolean frozen = ((SimpleRegistryAccessor)fr).isFrozen();
                        fr.unfreeze(false);
                        LateBlockRegistryImpl.registerBlocks((Registry<Block>) registry);
                        if (frozen) fr.freeze();
                    }
                };
                LATE_REGISTRATION_QUEUE.getFirst().add(blockEvent);
            }
        };
        Consumer<Registry<Item>> itemEvent = LateBlockRegistryImpl::registerItems;
        LATE_REGISTRATION_QUEUE.getSecond().add(itemEvent);

        bus.addListener(EventPriority.HIGHEST, eventConsumer);
    }

    public static void registerLateBlockAndItems(RegisterEvent event) {
        if (!event.getRegistryKey().equals(Registries.ITEM.getKey()))
            return;
        if (!hasRegisteredBlockSets) {
            DynamicBlockRegistry.initialize();
            hasRegisteredBlockSets = true;
        }
        Registry<Item> forgeRegistry = (Registry<Item>) event.getRegistry();
        LATE_REGISTRATION_QUEUE.getFirst().forEach(Runnable::run);
        LATE_REGISTRATION_QUEUE.getSecond().forEach(registerConsumer -> registerConsumer.accept(forgeRegistry));
        LATE_REGISTRATION_QUEUE.getFirst().clear();
        LATE_REGISTRATION_QUEUE.getSecond().clear();
    }
}
