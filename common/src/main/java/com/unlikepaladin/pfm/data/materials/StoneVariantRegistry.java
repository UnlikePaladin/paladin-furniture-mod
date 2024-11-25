package com.unlikepaladin.pfm.data.materials;

import net.minecraft.block.*;
import net.minecraft.block.enums.Instrument;
import net.minecraft.registry.Registries;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

public class StoneVariantRegistry extends VariantRegistryBase<StoneVariant> {
    public static final StoneVariant STONE = new StoneVariant(new Identifier("stone"), Blocks.STONE, Blocks.COBBLESTONE);
    public static final StoneVariantRegistry INSTANCE = new StoneVariantRegistry();
    public static Collection<String> getNamespaces() {
        return INSTANCE.variants.values().stream().map(VariantBase::getNamespace).collect(Collectors.toUnmodifiableList());
    };

    public static Collection<StoneVariant> getVariants() {
        return Collections.unmodifiableCollection(INSTANCE.variants.values());
    }
    @Nullable
    public static StoneVariant getVariant(Identifier name) {
        return INSTANCE.variants.getOrDefault(name, STONE);
    }

    /**
     * Simplified Wood/Block detection based on MoonlightLib<a href="https://github.com/MehVahdJukaar/Moonlight/blob/multi-loader/common/src/main/java/net/mehvahdjukaar/moonlight/api/set/BlockTypeRegistry.java#L18">...</a>
     */
    public Optional<StoneVariant> getVariantFromBlock(Block baseBlock, Identifier blockId) {
        String name = null;
        String path = blockId.getPath();
        if (path.endsWith("_polished")) {
            name = path.substring(0, path.length() - "_polished".length());
        } else if (path.startsWith("polished_")) {
            name = path.substring("polished_".length());
        } else if (path.endsWith("_polish")) {
            name = path.substring(0, path.length() - "_polish".length());
        } else if (path.startsWith("polish_")) {
            name = path.substring("polish_".length());
        }
        String namespace = blockId.getNamespace();
        if (!namespace.equals("cozy_home") && name != null && !namespace.equals("securitycraft") &&
                !namespace.equals("absentbydesign") && !namespace.equals("chipped") && !namespace.equals("extshape")) {

            BlockState state = baseBlock.getDefaultState();
            // can't check if the block is a full one, so I do this. Adding some checks here
            if (state.getProperties().size() <= 2 && !(baseBlock instanceof SlabBlock) && !name.contains("slab")) {
                // needs to use wood sound type
                // if (state.getSoundType() == SoundType.WOOD) { //wood from tcon has diff sounds
                BlockSoundGroup soundGroup = state.getSoundGroup();
                Instrument instrument = state.getInstrument();
                // and have correct material
                if (soundGroup == BlockSoundGroup.DEEPSLATE || soundGroup == BlockSoundGroup.POLISHED_DEEPSLATE || soundGroup == BlockSoundGroup.STONE || instrument == Instrument.BASEDRUM) {
                    // we do not allow "/" in the wood name
                    name = name.replace("/", "_");
                    Identifier id = new Identifier(blockId.getNamespace(), name);
                    Block rawBlock = findRaw(id);
                    if (rawBlock != null) {
                        return Optional.of(new StoneVariant(id, baseBlock, rawBlock));
                    }
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public StoneVariant getDefaultType() {
        return STONE;
    }

    @Override
    public Class<StoneVariant> getType() {
        return StoneVariant.class;
    }

    @Nullable
    private static Block findRaw(Identifier id) {
        Identifier[] test = {
                new Identifier(id.getNamespace(), id.getPath()),
                new Identifier(id.getNamespace(), id.getPath() + "raw"),
                new Identifier(id.getNamespace(), id.getPath() + "_raw"),
                new Identifier(id.getNamespace(), "raw_" + id.getPath()),
                new Identifier(id.getNamespace(), "raw" + id.getPath()),
                new Identifier(id.getNamespace(), id.getPath() + "_cobble"),
                new Identifier(id.getNamespace(), id.getPath() + "cobble"),
                new Identifier(id.getNamespace(), "cobble_" + id.getPath()),
                new Identifier(id.getNamespace(), "cobble" + id.getPath()),
                new Identifier(id.getPath()),
                new Identifier(id.getPath() + "raw"),
                new Identifier(id.getPath() + "_raw"),
                new Identifier("raw_" + id.getPath()),
                new Identifier("raw" + id.getPath()),
                new Identifier(id.getPath() + "_cobble"),
                new Identifier(id.getPath() + "cobble"),
                new Identifier("cobble_" + id.getPath()),
                new Identifier("cobble" + id.getPath())
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
