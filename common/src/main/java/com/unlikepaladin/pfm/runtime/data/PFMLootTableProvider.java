package com.unlikepaladin.pfm.runtime.data;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.stream.JsonWriter;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.JsonOps;
import com.unlikepaladin.pfm.blocks.BasicBathtubBlock;
import com.unlikepaladin.pfm.registry.PaladinFurnitureModBlocksItems;
import com.unlikepaladin.pfm.runtime.PFMGenerator;
import com.unlikepaladin.pfm.runtime.PFMProvider;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.enums.BedPart;
import net.minecraft.data.DataWriter;
import net.minecraft.item.ItemConvertible;
import net.minecraft.loot.*;
import net.minecraft.loot.condition.BlockStatePropertyLootCondition;
import net.minecraft.loot.condition.LootConditionConsumingBuilder;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.registry.Registries;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.context.ContextType;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class PFMLootTableProvider extends PFMProvider {
    private final List<Pair<Supplier<Consumer<BiConsumer<Identifier, LootTable.Builder>>>, ContextType>> lootTypeGenerators = ImmutableList.of(Pair.of(PFMLootTableGenerator::new, LootContextTypes.BLOCK));

    public PFMLootTableProvider(PFMGenerator parent) {
        super(parent);
        parent.setProgress("Generating Loot Tables");
    }

    public CompletableFuture<?> run(DataWriter writer) {
        Path path = getParent().getOutput();
        HashMap<Identifier, LootTable> map = Maps.newHashMap();
        this.lootTypeGenerators.forEach((pair) -> pair.getFirst().get().accept((identifier, builder) -> {
            if (map.put(identifier, builder.type(pair.getSecond()).build()) != null) {
                throw new IllegalStateException("Duplicate loot table " + identifier);
            }
        }));
        map.forEach((identifier, lootTable) -> {
            Path path2 = getOutput(path, identifier);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            try (JsonWriter jsonWriter = new JsonWriter(new OutputStreamWriter(byteArrayOutputStream, StandardCharsets.UTF_8));){
                Files.createDirectories(path2.getParent());
                Files.createFile(path2);
                jsonWriter.setSerializeNulls(false);
                jsonWriter.setIndent("  ");
                JsonHelper.writeSorted(jsonWriter, LootTable.CODEC.encodeStart(JsonOps.INSTANCE, lootTable).getOrThrow((error) -> {
                    getParent().getLogger().warn("Failed to parse Loot table: {}", error);
                    return null;
                }), JSON_KEY_SORTING_COMPARATOR);
                jsonWriter.flush();
                Files.write(path2, byteArrayOutputStream.toByteArray(), StandardOpenOption.WRITE);
                byteArrayOutputStream.close();
            }
            catch (Exception exception) {
                getParent().getLogger().error("Couldn't save {}", path2, exception);
            }
        });
        return CompletableFuture.allOf();
    }

    public String getName() {
        return "PFM Loot Tables";
    }

    private static Path getOutput(Path rootOutput, Identifier lootTableId) {
        return rootOutput.resolve("data/" + lootTableId.getNamespace() + "/loot_table/" + lootTableId.getPath() + ".json");
    }

    static class PFMLootTableGenerator implements Consumer<BiConsumer<Identifier, LootTable.Builder>> {
        private final Map<Identifier, LootTable.Builder> lootTables = Maps.newHashMap();
        private final List<Block> pfmBlocks = new ArrayList<>();
        public void accept(BiConsumer<Identifier, LootTable.Builder> biConsumer) {
            List<Block> blocks = PaladinFurnitureModBlocksItems.BLOCKS;
            blocks.forEach(this::addDrop);
            Block[] beds = PaladinFurnitureModBlocksItems.getBeds();
            Arrays.stream(beds).forEach(bed -> this.addDrop(bed, (Block block) -> dropsWithProperty(block, BedBlock.PART, BedPart.HEAD)));
            BasicBathtubBlock.basicBathtubBlockStream().forEach(basicBathtubBlock -> this.addDrop(basicBathtubBlock, (Block block) -> dropsWithProperty(block, BedBlock.PART, BedPart.HEAD)));

            HashSet<Identifier> set = Sets.newHashSet();
            for (Block block : pfmBlocks) {
                if (block.getLootTableKey().isEmpty()) continue;

                Identifier identifier = block.getLootTableKey().get().getValue();
                if (!set.add(identifier)) continue;
                LootTable.Builder builder5 = this.lootTables.remove(identifier);
                if (builder5 == null) {
                    throw new IllegalStateException(String.format("Missing loottable '%s' for '%s'", identifier, Registries.BLOCK.getId(block)));
                }
                biConsumer.accept(identifier, builder5);
            }
            if (!this.lootTables.isEmpty()) {
                throw new IllegalStateException("Created block loot tables for non-blocks: " + this.lootTables.keySet());
            }
        }

        private void addDrop(Block block, Function<Block, LootTable.Builder> lootTableFunction) {
            this.addDrop(block, lootTableFunction.apply(block));
        }

        public void addDrop(Block block, Block drop) {
            this.addDrop(block, drops(drop));
        }

        public LootTable.Builder drops(ItemConvertible drop) {
            return LootTable.builder().pool(this.addSurvivesExplosionCondition(drop, LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0F)).with(ItemEntry.builder(drop))));
        }

        public void addDrop(Block block) {
            this.addDrop(block, block);
        }

        public final void addDrop(Block block, LootTable.Builder lootTable) {
            this.lootTables.put(block.getLootTableKey().get().getValue(), lootTable);
            this.pfmBlocks.add(block);
        }

        public <T extends Comparable<T> & StringIdentifiable> LootTable.Builder dropsWithProperty(Block drop, Property<T> property, T value) {
            return LootTable.builder().pool(this.addSurvivesExplosionCondition(drop, LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0F)).with(ItemEntry.builder(drop).conditionally(BlockStatePropertyLootCondition.builder(drop).properties(StatePredicate.Builder.create().exactMatch(property, value))))));
        }

        protected <T extends LootConditionConsumingBuilder<T>> T addSurvivesExplosionCondition(ItemConvertible drop, LootConditionConsumingBuilder<T> builder) {
            return builder.getThisConditionConsumingBuilder();
        }
    }
}

