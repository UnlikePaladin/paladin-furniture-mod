package com.unlikepaladin.pfm.data.materials;

import net.minecraft.block.*;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.registry.Registries;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registry;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class WoodVariantRegistry extends VariantRegistryBase<WoodVariant> {
    public static final WoodVariant OAK = new WoodVariant(Identifier.of("oak"), Blocks.OAK_PLANKS, Blocks.OAK_LOG);
    public static final WoodVariantRegistry INSTANCE = new WoodVariantRegistry();
    public static Collection<String> getNamespaces() {
        return INSTANCE.variants.values().stream().map(VariantBase::getNamespace).collect(Collectors.toUnmodifiableList());
    };

    public static Collection<WoodVariant> getVariants() {
        return Collections.unmodifiableCollection(INSTANCE.variants.values());
    }
    @Nullable
    public static WoodVariant getVariant(Identifier name) {
        return INSTANCE.variants.getOrDefault(name, OAK);
    }

    /**
     * Simplified Wood/Block detection based on MoonlightLib<a href="https://github.com/MehVahdJukaar/Moonlight/blob/multi-loader/common/src/main/java/net/mehvahdjukaar/moonlight/api/set/BlockTypeRegistry.java#L18">...</a>
     */
    public Optional<WoodVariant> getVariantFromBlock(Block baseBlock, Identifier blockId) {
        String name = null;
        String path = blockId.getPath();
        if (blockId.getNamespace().equals("tfc")) {
            if (path.contains("wood/planks/")) {
                Optional<Block> log = Registries.BLOCK.getOptionalValue(
                        Identifier.of(blockId.getNamespace(), path.replace("planks", "log")));
                if (log.isPresent()) {
                    Identifier id = Identifier.of(blockId.getNamespace(), path.replace("wood/planks/", ""));
                    return Optional.of(new WoodVariant(id, baseBlock, log.get()));
                }
            }
            return Optional.empty();
        }
        if (path.endsWith("_planks")) {
            name = path.substring(0, path.length() - "_planks".length());
        } else if (path.startsWith("planks_")) {
            name = path.substring("planks_".length());
        } else if (path.endsWith("_plank")) {
            name = path.substring(0, path.length() - "_plank".length());
        } else if (path.startsWith("plank_")) {
            name = path.substring("plank_".length());
        }
        String namespace = blockId.getNamespace();
        if (!namespace.equals("cozy_home") && name != null && !namespace.equals("securitycraft") &&
                !namespace.equals("absentbydesign") && !(namespace.equals("terrestria") && path.contains("sakura")) && !(namespace.equals("betternether") && path.contains("nether_mushroom")) && !namespace.equals("chipped") && !(namespace.equals("regions_unexplored") && path.contains("alpha"))) {

            BlockState state = baseBlock.getDefaultState();
            // can't check if the block is a full one, so I do this. Adding some checks here
            if (state.getProperties().size() <= 2 && !(baseBlock instanceof SlabBlock)) {
                // needs to use wood sound type
                // if (state.getSoundType() == SoundType.WOOD) { //wood from tcon has diff sounds
                BlockSoundGroup soundGroup = state.getSoundGroup();
                NoteBlockInstrument instrument = state.getInstrument();
                // and have correct material
                if (soundGroup == BlockSoundGroup.BAMBOO_WOOD || soundGroup == BlockSoundGroup.CHERRY_WOOD || soundGroup == BlockSoundGroup.WOOD || soundGroup == BlockSoundGroup.NETHER_WOOD || instrument == NoteBlockInstrument.BASS) {
                    // we do not allow "/" in the wood name
                    name = name.replace("/", "_");
                    Identifier id = Identifier.of(blockId.getNamespace(), name);
                    Block logBlock = findLog(id);
                    if (logBlock != null) {
                        return Optional.of(new WoodVariant(id, baseBlock, logBlock));
                    }
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public WoodVariant getDefaultType() {
        return OAK;
    }

    @Override
    public Class<WoodVariant> getType() {
        return WoodVariant.class;
    }

    @Nullable
    private static Block findLog(Identifier id) {
        Identifier[] test = {
                Identifier.of(id.getNamespace(), id.getPath() + "_log"),
                Identifier.of(id.getNamespace(), "log_" + id.getPath()),
                Identifier.of(id.getNamespace(), id.getPath() + "log"),
                Identifier.of(id.getPath() + "_log"),
                Identifier.of("log_" + id.getPath()),
                Identifier.of(id.getPath() + "log"),
                Identifier.of(id.getNamespace(), id.getPath() + "_stem"),
                Identifier.of(id.getNamespace(), "stem_" + id.getPath()),
                Identifier.of(id.getPath() + "_stem"),
                Identifier.of("stem_" + id.getPath()),
                Identifier.of(id.getNamespace(), "stalk_" + id.getPath()),
                Identifier.of(id.getPath() + "_stalk"),
                Identifier.of("stalk_" + id.getPath())
        };
        Block temp = null;
        for (Identifier r : test) {
            if (Registries.BLOCK.containsId(r)) {
                temp = Registries.BLOCK.get(r);
                break;
            }
        }
        return temp;
    }
}
