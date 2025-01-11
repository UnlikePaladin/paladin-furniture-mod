package com.unlikepaladin.pfm.runtime.assets;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.blocks.*;
import com.unlikepaladin.pfm.blocks.models.ModelHelper;
import com.unlikepaladin.pfm.blocks.models.basicLamp.UnbakedBasicLampModel;
import com.unlikepaladin.pfm.data.materials.StoneVariant;
import com.unlikepaladin.pfm.data.materials.VariantBase;
import com.unlikepaladin.pfm.mixin.PFMTextureKeyFactory;
import com.unlikepaladin.pfm.registry.PaladinFurnitureModBlocksItems;
import com.unlikepaladin.pfm.registry.TriFunc;
import com.unlikepaladin.pfm.runtime.PFMDataGenerator;
import com.unlikepaladin.pfm.runtime.PFMGenerator;
import com.unlikepaladin.pfm.runtime.PFMProvider;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataCache;
import net.minecraft.data.DataProvider;
import net.minecraft.data.client.model.*;
import net.minecraft.item.Item;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;

import java.nio.file.Path;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class PFMBlockstateModelProvider extends PFMProvider {

    public static Map<Block, Identifier> modelPathMap = new HashMap<>();

    public PFMBlockstateModelProvider(PFMGenerator parent) {
        super(parent, "PFM Blockstates and Models");
        parent.setProgress("Generating Blockstates and Models");
    }

    public void run(DataCache cache) {
        startProviderRun();
        Path path = getParent().getOutput();
        HashMap<Block, BlockStateSupplier> blockstates = Maps.newHashMap();
        Consumer<BlockStateSupplier> blockStateSupplierConsumer = blockStateSupplier -> {
            Block block = blockStateSupplier.getBlock();
            BlockStateSupplier blockStateSupplier2 = blockstates.put(block, blockStateSupplier);
            if (blockStateSupplier2 != null) {
                getParent().getLogger().error("Duplicate blockstate definition for " + block);
                throw new IllegalStateException("Duplicate blockstate definition for " + block);
            }
        };
        HashMap<Identifier, Supplier<JsonElement>> models = Maps.newHashMap();
        HashSet<Item> excludeBlockItem = Sets.newHashSet();
        BiConsumer<Identifier, Supplier<JsonElement>> identifierSupplierBiConsumer = (identifier, supplier) -> {
            Supplier<JsonElement> supplier2 = models.put(identifier, supplier);
            if (supplier2 != null) {
                getParent().getLogger().error("Duplicate model definition for " + identifier);
                throw new IllegalStateException("Duplicate model definition for " + identifier);
            }
        };
        Consumer<Item> excludeBlockItemConsumer = excludeBlockItem::add;
        new PFMBlockStateModelGenerator(blockStateSupplierConsumer, identifierSupplierBiConsumer, excludeBlockItemConsumer).registerModelsAndStates();
        modelPathMap.keySet().forEach(block -> {
            Item item = Item.BLOCK_ITEMS.get(block);
            if (item != null) {
                if (excludeBlockItem.contains(item)) {
                    return;
                }
                Identifier identifier = ModelIds.getItemModelId(item);
                if (!models.containsKey(identifier)) {
                    models.put(identifier, new SimpleModelSupplier(modelPathMap.get(block)));
                }
            }
        });
        this.writeJsons(cache, path, blockstates, PFMBlockstateModelProvider::getBlockStateJsonPath);
        this.writeJsons(cache, path, models, PFMBlockstateModelProvider::getModelJsonPath);
        endProviderRun();
    }

    private static Path getBlockStateJsonPath(Path root, Block block) {
        Identifier identifier = Registry.BLOCK.getId(block);
        return root.resolve("assets/" + identifier.getNamespace() + "/blockstates/" + identifier.getPath() + ".json");
    }

    private static Path getModelJsonPath(Path root, Identifier id) {
        return root.resolve("assets/" + id.getNamespace() + "/models/" + id.getPath() + ".json");
    }


    private <T> void writeJsons(DataCache cache, Path root, Map<T, ? extends Supplier<JsonElement>> jsons, BiFunction<Path, T, Path> locator) {
        jsons.forEach((object, supplier) -> {
            Path path2 = locator.apply(root, object);
            if (supplier != null && object != null && path2 != null)
                try {
                    DataProvider.writeToPath(PFMDataGenerator.GSON, cache, supplier.get(), path2);
                }
                catch (Exception exception) {
                    getParent().getLogger().error("Couldn't save {}", path2, exception);
                }
        });
    }
    static class PFMBlockStateModelGenerator {
        public static Map<Model, Identifier> ModelIDS = new HashMap<>();

        final Consumer<BlockStateSupplier> blockStateCollector;
        final BiConsumer<Identifier, Supplier<JsonElement>> modelCollector;

        final List<Identifier> generatedStates = new ArrayList<>();
        final Consumer<Item> simpleItemModelExemptionCollector;

        PFMBlockStateModelGenerator(Consumer<BlockStateSupplier> blockStateCollector, BiConsumer<Identifier, Supplier<JsonElement>> modelCollector, Consumer<Item> simpleItemModelExemptionCollector) {
            this.blockStateCollector = blockStateCollector;
            this.modelCollector = modelCollector;
            this.simpleItemModelExemptionCollector = simpleItemModelExemptionCollector;
        }

        public void registerModelsAndStates() {
            registerTuckableChairs();
            registerTables();
            registerNightStands();
            registerBeds();
            registerLadders();
            registerCounters();
            registerLamp();
        }

        public void registerTuckableChairs() {
            generateBlockStateForBlock(PaladinFurnitureModBlocksItems.furnitureEntryMap.get(BasicChairBlock.class).getVariantToBlockMap(), "chair", (block, identifiers) -> createOrientableTableBlockState(block, identifiers, 90));
            generateBlockStateForBlock(PaladinFurnitureModBlocksItems.furnitureEntryMap.get(BasicChairBlock.class).getVariantToBlockMapNonBase(), "chair", (block, identifiers) -> createOrientableTableBlockState(block, identifiers, 90));

            generateBlockStateForBlock(PaladinFurnitureModBlocksItems.furnitureEntryMap.get(DinnerChairBlock.class).getVariantToBlockMap(), "chair_dinner", (block, identifiers) -> createOrientableTableBlockState(block, identifiers, 90));
            generateBlockStateForBlock(PaladinFurnitureModBlocksItems.furnitureEntryMap.get(DinnerChairBlock.class).getVariantToBlockMapNonBase(), "chair_dinner", (block, identifiers) -> createOrientableTableBlockState(block, identifiers, 90));

            generateBlockStateForBlock(PaladinFurnitureModBlocksItems.furnitureEntryMap.get(ModernChairBlock.class).getVariantToBlockMap(), "chair_modern", (block, identifiers) -> createOrientableTableBlockState(block, identifiers, 90));
            generateBlockStateForBlock(PaladinFurnitureModBlocksItems.furnitureEntryMap.get(ModernChairBlock.class).getVariantToBlockMapNonBase(), "chair_modern", (block, identifiers) -> createOrientableTableBlockState(block, identifiers, 90));

            generateBlockStateForBlock(PaladinFurnitureModBlocksItems.furnitureEntryMap.get(ClassicChairBlock.class).getVariantToBlockMap(), "chair_classic", (block, identifiers) -> createOrientableTableBlockState(block, identifiers, 90));
            generateBlockStateForBlock(PaladinFurnitureModBlocksItems.furnitureEntryMap.get(ClassicChairBlock.class).getVariantToBlockMapNonBase(), "chair_classic", (block, identifiers) -> createOrientableTableBlockState(block, identifiers, 90));

            generateBlockStateForBlock(PaladinFurnitureModBlocksItems.furnitureEntryMap.get(LogStoolBlock.class).getVariantToBlockMap(), "log_stool", (block, identifiers) -> createOrientableTableBlockState(block, identifiers, 90));

            generateBlockStateForBlock(PaladinFurnitureModBlocksItems.furnitureEntryMap.get(SimpleStoolBlock.class).getVariantToBlockMap(), "simple_stool", (block, identifiers) -> createOrientableTableBlockState(block, identifiers, 90));
            generateBlockStateForBlock(PaladinFurnitureModBlocksItems.furnitureEntryMap.get(SimpleStoolBlock.class).getVariantToBlockMapNonBase(), "simple_stool", (block, identifiers) -> createOrientableTableBlockState(block, identifiers, 90));

            generateBlockStateForBlock(PaladinFurnitureModBlocksItems.furnitureEntryMap.get(ClassicStoolBlock.class).getVariantToBlockMap(), "classic_stool", (block, identifiers) -> createOrientableTableBlockState(block, identifiers, 90));
            generateBlockStateForBlock(PaladinFurnitureModBlocksItems.furnitureEntryMap.get(ClassicStoolBlock.class).getVariantToBlockMapNonBase(), "classic_stool", (block, identifiers) -> createOrientableTableBlockState(block, identifiers, 90));

            generateBlockStateForBlock(PaladinFurnitureModBlocksItems.furnitureEntryMap.get(ModernStoolBlock.class).getVariantToBlockMap(), "modern_stool", (block, identifiers) -> createOrientableTableBlockState(block, identifiers, 90));
            generateBlockStateForBlock(PaladinFurnitureModBlocksItems.furnitureEntryMap.get(ModernStoolBlock.class).getVariantToBlockMapNonBase(), "modern_stool", (block, identifiers) -> createOrientableTableBlockState(block, identifiers, 90));
        }

        public void registerTables() {
            generateBlockStateForBlock(PaladinFurnitureModBlocksItems.furnitureEntryMap.get(BasicTableBlock.class).getVariantToBlockMap(), "table_basic", PFMBlockStateModelGenerator::createAxisOrientableTableBlockState);
            generateBlockStateForBlock(PaladinFurnitureModBlocksItems.furnitureEntryMap.get(BasicTableBlock.class).getVariantToBlockMapNonBase(), "table_basic", PFMBlockStateModelGenerator::createAxisOrientableTableBlockState);

            generateBlockStateForBlock(PaladinFurnitureModBlocksItems.furnitureEntryMap.get(ClassicTableBlock.class).getVariantToBlockMap(), "table_classic", PFMBlockStateModelGenerator::createSingleStateBlockState);
            generateBlockStateForBlock(PaladinFurnitureModBlocksItems.furnitureEntryMap.get(ClassicTableBlock.class).getVariantToBlockMapNonBase(), "table_classic", PFMBlockStateModelGenerator::createSingleStateBlockState);

            generateBlockStateForBlock(PaladinFurnitureModBlocksItems.furnitureEntryMap.get(LogTableBlock.class).getVariantToBlockMap(), "log_table", PFMBlockStateModelGenerator::createOrientableTableBlockState);
            generateBlockStateForBlock(PaladinFurnitureModBlocksItems.furnitureEntryMap.get(LogTableBlock.class).getVariantToBlockMapNonBase(), "log_table", PFMBlockStateModelGenerator::createOrientableTableBlockState);

            generateBlockStateForBlock(PaladinFurnitureModBlocksItems.furnitureEntryMap.get(RawLogTableBlock.class).getVariantToBlockMap(), "log_table", PFMBlockStateModelGenerator::createOrientableTableBlockState);
            generateBlockStateForBlock(PaladinFurnitureModBlocksItems.furnitureEntryMap.get(RawLogTableBlock.class).getVariantToBlockMapNonBase(), "log_table", PFMBlockStateModelGenerator::createOrientableTableBlockState);

            generateBlockStateForBlock(PaladinFurnitureModBlocksItems.furnitureEntryMap.get(DinnerTableBlock.class).getVariantToBlockMap(), "dinner_table", (block, identifiers) -> createOrientableTableBlockState(block, identifiers, 90));
            generateBlockStateForBlock(PaladinFurnitureModBlocksItems.furnitureEntryMap.get(DinnerTableBlock.class).getVariantToBlockMapNonBase(), "dinner_table", (block, identifiers) -> createOrientableTableBlockState(block, identifiers, 90));

            generateBlockStateForBlock(PaladinFurnitureModBlocksItems.furnitureEntryMap.get(ModernDinnerTableBlock.class).getVariantToBlockMap(), "modern_dinner_table", (block, identifiers) -> createAxisOrientableTableBlockState(block, identifiers, 90));
            generateBlockStateForBlock(PaladinFurnitureModBlocksItems.furnitureEntryMap.get(ModernDinnerTableBlock.class).getVariantToBlockMapNonBase(), "modern_dinner_table", (block, identifiers) -> createAxisOrientableTableBlockState(block, identifiers, 90));

            generateBlockStateForBlock(PaladinFurnitureModBlocksItems.furnitureEntryMap.get(BasicCoffeeTableBlock.class).getVariantToBlockMap(), "coffee_table_basic", PFMBlockStateModelGenerator::createAxisOrientableTableBlockState);
            generateBlockStateForBlock(PaladinFurnitureModBlocksItems.furnitureEntryMap.get(BasicCoffeeTableBlock.class).getVariantToBlockMapNonBase(), "coffee_table_basic", PFMBlockStateModelGenerator::createAxisOrientableTableBlockState);

            generateBlockStateForBlock(PaladinFurnitureModBlocksItems.furnitureEntryMap.get(ModernCoffeeTableBlock.class).getVariantToBlockMap(), "coffee_table_modern", (block, identifiers) -> createAxisOrientableTableBlockState(block, identifiers, 90));
            generateBlockStateForBlock(PaladinFurnitureModBlocksItems.furnitureEntryMap.get(ModernCoffeeTableBlock.class).getVariantToBlockMapNonBase(), "coffee_table_modern", (block, identifiers) -> createAxisOrientableTableBlockState(block, identifiers, 90));

            generateBlockStateForBlock(PaladinFurnitureModBlocksItems.furnitureEntryMap.get(ClassicCoffeeTableBlock.class).getVariantToBlockMap(), "coffee_table_classic", PFMBlockStateModelGenerator::createSingleStateBlockState);
            generateBlockStateForBlock(PaladinFurnitureModBlocksItems.furnitureEntryMap.get(ClassicCoffeeTableBlock.class).getVariantToBlockMapNonBase(), "coffee_table_classic", PFMBlockStateModelGenerator::createSingleStateBlockState);

        }

        public void registerNightStands() {
            generateBlockStateForBlock(PaladinFurnitureModBlocksItems.furnitureEntryMap.get(ClassicNightstandBlock.class).getVariantToBlockMap(), "classic_nightstand", (block, identifiers) -> createOrientableTableBlockState(block, identifiers, 90));
            generateBlockStateForBlock(PaladinFurnitureModBlocksItems.furnitureEntryMap.get(ClassicNightstandBlock.class).getVariantToBlockMapNonBase(), "classic_nightstand", (block, identifiers) -> createOrientableTableBlockState(block, identifiers, 90));
        }

        public void registerBeds() {
            generateModelAndBlockStateForBed(PaladinFurnitureModBlocksItems.furnitureEntryMap.get(SimpleBedBlock.class).getVariantToBlockMapList(), "simple_bed", PFMBlockStateModelGenerator::createBedBlockState);
            generateModelAndBlockStateForBed(PaladinFurnitureModBlocksItems.furnitureEntryMap.get(ClassicBedBlock.class).getVariantToBlockMapList(), "simple_bed", PFMBlockStateModelGenerator::createBedBlockState);
        }

        public void registerLadders() {
            generateBlockStateForBlock(PaladinFurnitureModBlocksItems.furnitureEntryMap.get(SimpleBunkLadderBlock.class).getVariantToBlockMap(), "simple_bunk_ladder", PFMBlockStateModelGenerator::createOrientableTableBlockState);
        }

        public void registerCounters() {
            generateBlockStateForBlock(PaladinFurnitureModBlocksItems.furnitureEntryMap.get(KitchenCounterBlock.class).getVariantToBlockMap(), "kitchen_counter", PFMBlockStateModelGenerator::createOrientableKitchenCounter);
            generateBlockStateForBlock(PaladinFurnitureModBlocksItems.furnitureEntryMap.get(KitchenCounterBlock.class).getVariantToBlockMapNonBase(), "kitchen_counter", PFMBlockStateModelGenerator::createOrientableKitchenCounter);

            generateBlockStateForBlock(PaladinFurnitureModBlocksItems.furnitureEntryMap.get(KitchenDrawerBlock.class).getVariantToBlockMap(), "kitchen_drawer", PFMBlockStateModelGenerator::createOrientableKitchenCounter);
            generateBlockStateForBlock(PaladinFurnitureModBlocksItems.furnitureEntryMap.get(KitchenDrawerBlock.class).getVariantToBlockMapNonBase(), "kitchen_drawer", PFMBlockStateModelGenerator::createOrientableKitchenCounter);

            generateBlockStateForBlock(PaladinFurnitureModBlocksItems.furnitureEntryMap.get(KitchenCabinetBlock.class).getVariantToBlockMap(), "kitchen_cabinet", PFMBlockStateModelGenerator::createOrientableKitchenCounter);
            generateBlockStateForBlock(PaladinFurnitureModBlocksItems.furnitureEntryMap.get(KitchenCabinetBlock.class).getVariantToBlockMapNonBase(), "kitchen_cabinet", PFMBlockStateModelGenerator::createOrientableKitchenCounter);

            generateBlockStateForBlock(PaladinFurnitureModBlocksItems.furnitureEntryMap.get(KitchenWallDrawerBlock.class).getVariantToBlockMap(), "kitchen_wall_drawer", PFMBlockStateModelGenerator::createOrientableKitchenCounter);
            generateBlockStateForBlock(PaladinFurnitureModBlocksItems.furnitureEntryMap.get(KitchenWallDrawerBlock.class).getVariantToBlockMapNonBase(), "kitchen_wall_drawer", PFMBlockStateModelGenerator::createOrientableKitchenCounter);

            generateBlockStateForBlock(PaladinFurnitureModBlocksItems.furnitureEntryMap.get(KitchenWallCounterBlock.class).getVariantToBlockMap(), "kitchen_wall_counter", PFMBlockStateModelGenerator::createOrientableKitchenCounter);
            generateBlockStateForBlock(PaladinFurnitureModBlocksItems.furnitureEntryMap.get(KitchenWallCounterBlock.class).getVariantToBlockMapNonBase(), "kitchen_wall_counter", PFMBlockStateModelGenerator::createOrientableKitchenCounter);

            generateBlockStateForBlock(PaladinFurnitureModBlocksItems.furnitureEntryMap.get(KitchenWallDrawerSmallBlock.class).getVariantToBlockMap(), "kitchen_wall_small_drawer", PFMBlockStateModelGenerator::createOrientableKitchenCounter);
            generateBlockStateForBlock(PaladinFurnitureModBlocksItems.furnitureEntryMap.get(KitchenWallDrawerSmallBlock.class).getVariantToBlockMapNonBase(), "kitchen_wall_small_drawer", PFMBlockStateModelGenerator::createOrientableKitchenCounter);

            generateBlockStateForBlock(PaladinFurnitureModBlocksItems.furnitureEntryMap.get(KitchenCounterOvenBlock.class).getVariantToBlockMap(), "kitchen_counter_oven", (block, identifiers) -> createOrientableKitchenCounter(block, identifiers, "", "", "", 180));
            generateBlockStateForBlock(PaladinFurnitureModBlocksItems.furnitureEntryMap.get(KitchenCounterOvenBlock.class).getVariantToBlockMapNonBase(), "kitchen_counter_oven", (block, identifiers) -> createOrientableKitchenCounter(block, identifiers, "", "", "", 180));

            generateBlockStateForBlock(PaladinFurnitureModBlocksItems.furnitureEntryMap.get(KitchenSinkBlock.class).getVariantToBlockMap(), "kitchen_sink", PFMBlockStateModelGenerator::createOrientableKitchenCounter);
            generateBlockStateForBlock(PaladinFurnitureModBlocksItems.furnitureEntryMap.get(KitchenSinkBlock.class).getVariantToBlockMapNonBase(), "kitchen_sink", PFMBlockStateModelGenerator::createOrientableKitchenCounter);
        }

        public void registerLamp() {
            Identifier modelID = ModelIds.getBlockModelId(PaladinFurnitureModBlocksItems.BASIC_LAMP);
            this.blockStateCollector.accept(createSingleStateBlockState(PaladinFurnitureModBlocksItems.BASIC_LAMP, List.of(modelID)));
            PFMBlockstateModelProvider.modelPathMap.put(PaladinFurnitureModBlocksItems.BASIC_LAMP, UnbakedBasicLampModel.getItemModelId());
        }

        public static Texture createPlankBlockTexture(Boolean stripped, VariantBase<?> variantBase) {
            Identifier top = ModelHelper.getTextureId(variantBase.getBaseBlock());
            Identifier legs =  ModelHelper.getTextureId(variantBase.getBaseBlock());
            return new Texture().put(TextureKey.TEXTURE, top).put(LOG_KEY, legs);

        }

        public static Texture createRawBlockTexture(Boolean stripped, VariantBase<?> variantBase) {
            Identifier top = stripped ? ModelHelper.getTextureId((Block) variantBase.getChild("stripped_log")) : ModelHelper.getTextureId(variantBase.getSecondaryBlock());
            Identifier legs = stripped ? ModelHelper.getTextureId((Block) variantBase.getChild("stripped_log")) : ModelHelper.getTextureId(variantBase.getSecondaryBlock());
            return new Texture().put(TextureKey.TEXTURE, top).put(LOG_KEY, legs);
        }

        public static Texture createPlankLogBlockTexture(Boolean stripped, VariantBase<?> variantBase) {
            Identifier top = stripped ? ModelHelper.getTextureId((Block) variantBase.getChild("stripped_log")) : ModelHelper.getTextureId(variantBase.getBaseBlock());
            Identifier legs = stripped ? ModelHelper.getTextureId(variantBase.getBaseBlock()) : ModelHelper.getTextureId(variantBase.getSecondaryBlock());
            return new Texture().put(TextureKey.TEXTURE, top).put(LOG_KEY, legs);
        }

        public static Texture createCounterBlockTexture(Boolean stripped, VariantBase<?> variantBase) {
            Identifier counterBase = stripped ? ModelHelper.getTextureId((Block) variantBase.getChild("stripped_log")) : ModelHelper.getTextureId(variantBase.getBaseBlock());
            Identifier counterTop = stripped ? ModelHelper.getTextureId(variantBase.getBaseBlock()) : ModelHelper.getTextureId(variantBase.getSecondaryBlock());
            if (variantBase.identifier.getPath().equals("granite")) {
                counterTop = ModelHelper.getTextureId(Blocks.POLISHED_GRANITE);
                counterBase = ModelHelper.getTextureId(Blocks.WHITE_TERRACOTTA);
            } else if (variantBase.identifier.getPath().equals("calcite") || variantBase.identifier.getPath().equals("netherite")) {
                Identifier temp = counterBase;
                counterBase = counterTop;
                counterTop  = temp;
            } else if (variantBase.identifier.getPath().equals("andesite")) {
                counterTop = ModelHelper.getTextureId(Blocks.POLISHED_ANDESITE);
                counterBase = ModelHelper.getTextureId(Blocks.STRIPPED_OAK_LOG);
            } else if (variantBase.identifier.getPath().equals("deepslate")) {
                counterTop = ModelHelper.getTextureId(Blocks.POLISHED_DEEPSLATE);
                counterBase = ModelHelper.getTextureId(Blocks.DARK_OAK_PLANKS);
            } else if (variantBase.identifier.getPath().equals("blackstone")) {
                counterTop = ModelHelper.getTextureId(Blocks.POLISHED_BLACKSTONE);
                counterBase = ModelHelper.getTextureId(Blocks.CRIMSON_PLANKS);
            }
            return new Texture().put(TextureKey.TEXTURE, counterBase).put(LOG_KEY, counterTop);
        }

        public static Texture createLogLogTopBlockTexture(Boolean stripped, VariantBase<?> variantBase) {
            Identifier legs = stripped ? ModelHelper.getTextureId((Block) variantBase.getChild("stripped_log")) : ModelHelper.getTextureId(variantBase.getSecondaryBlock());
            Identifier top = stripped ? ModelHelper.getTextureId((Block) variantBase.getChild("stripped_log"), "_top") : ModelHelper.getTextureId(variantBase.getSecondaryBlock(), "_top");
            return new Texture().put(LOG_KEY, legs).put(LOG_TOP_KEY, top);
        }

        public void generateBlockStateForBlock(Map<VariantBase<?>, ? extends Block> variantBaseHashMap, String blockName, BiFunction<Block, List<Identifier>, BlockStateSupplier> stateSupplierBiFunction) {
            variantBaseHashMap.forEach((variantBase, block) -> {
                if (!generatedStates.contains(Registry.BLOCK.getId(block))) {
                    Identifier modelID = ModelIds.getBlockModelId(block);
                    Identifier id = new Identifier(modelID.getNamespace(), "block/" + blockName);
                    List<Identifier> ids = new ArrayList<>(1);
                    ids.add(id);
                    this.blockStateCollector.accept(stateSupplierBiFunction.apply(block, ids));
                    generatedStates.add(Registry.BLOCK.getId(block));
                }
            });
        }

        public void generateModelAndBlockStateForBed(HashMap<VariantBase<?>, ? extends Set<?>> variantBaseHashMap, String blockName, BiFunction<Block, List<Identifier>, BlockStateSupplier> stateSupplierBiFunction) {
            variantBaseHashMap.forEach((variantBase, blockList) -> {
                blockList.forEach(block1 -> {
                Block block = (Block) block1;
                if (!generatedStates.contains(Registry.BLOCK.getId(block))) {
                    Identifier modelID = ModelIds.getBlockModelId(block);
                    Identifier id = new Identifier(modelID.getNamespace(), "block/" + blockName);
                    List<Identifier> ids = new ArrayList<>(1);
                    ids.add(id);
                    this.blockStateCollector.accept(stateSupplierBiFunction.apply(block, ids));
                    generatedStates.add(Registry.BLOCK.getId(block));
                }});
            });

        }

        public void generateModelAndBlockStateForVariants(Map<VariantBase<?>, ? extends Block> variantBaseHashMap, String blockName, Model[] models, BiFunction<Block, List<Identifier>, BlockStateSupplier> stateSupplierBiFunction, BiFunction<Boolean, VariantBase<?>, Texture> textureBiFunction) {
            variantBaseHashMap.forEach((variantBase, block) -> {
                if (!generatedStates.contains(Registry.BLOCK.getId(block))) {
                    String blockName2 = blockName;

                    boolean stripped = block.getTranslationKey().contains("stripped");
                    Texture blockTexture = textureBiFunction.apply(stripped, variantBase);
                    List<Identifier> ids = new ArrayList<>();
                    String strippedprefix  = stripped ? "stripped_" : "";
                    if (block instanceof RawLogTableBlock) {
                        blockName2 = "raw_log_table";
                    }
                    if (variantBase instanceof StoneVariant && block instanceof LogTableBlock) {
                        blockName2 = blockName2.replace("log", "natural");
                    } else if (variantBase.isNetherWood() && block instanceof LogTableBlock) {
                        blockName2 = blockName2.replace("log", "stem");
                    }
                    Identifier modelID = ModelIds.getBlockModelId(block);
                    for (Model model : models) {
                        Identifier id = new Identifier(modelID.getNamespace(), ModelIDS.get(model).getPath().replace("template_", "").replace("template", "").replaceAll(blockName, strippedprefix + variantBase.asString() + "_" + blockName2).replace("block/", "block/" + blockName + "/").replace("//", "/"));
                        model.upload(id, blockTexture, this.modelCollector);
                        ids.add(id);
                    }
                    this.blockStateCollector.accept(stateSupplierBiFunction.apply(block, ids));
                    PFMBlockstateModelProvider.modelPathMap.put(block, ids.get(0));
                    generatedStates.add(Registry.BLOCK.getId(block));
                }
            });
        }

        public void generateModelAndBlockStateForBed(HashMap<VariantBase<?>, ? extends List<?>> variantBaseHashMap, String blockName, Model[] models, TriFunc<Block, List<Identifier>, String, BlockStateSupplier> stateSupplierBiFunction, BiFunction<Boolean, VariantBase<?>, Texture> textureBiFunction) {
            variantBaseHashMap.forEach((variantBase, blockList) -> {
                List<Identifier> allids = new ArrayList<>();
                blockList.forEach(block1 -> {
                    Block block = (Block) block1;
                    if (!generatedStates.contains(Registry.BLOCK.getId(block))) {
                        boolean stripped = block.getTranslationKey().contains("stripped");
                        Texture blockTexture = textureBiFunction.apply(stripped, variantBase);
                        Identifier modelID = ModelIds.getBlockModelId(block);
                        String color = block instanceof SimpleBedBlock ? ((SimpleBedBlock) block).getPFMColor().asString() : "";
                        List<Identifier> ids = new ArrayList<>();
                        for (Model model : models) {
                            Identifier id = new Identifier(modelID.getNamespace(), ModelIDS.get(model).getPath().replaceAll("white", color).replaceAll("template", variantBase.asString()));

                            if (allids.contains(id))
                                continue;
                            if (model == models[0]) {
                                block(blockName+"/template/full/"+ blockName+ "_"+color, TextureKey.TEXTURE).upload(id, blockTexture, this.modelCollector);
                            }  else {
                                model.upload(id, blockTexture, this.modelCollector);
                            }
                            ids.add(id);
                        }
                        allids.addAll(ids);
                        this.blockStateCollector.accept(stateSupplierBiFunction.apply(block, ids, color));
                        PFMBlockstateModelProvider.modelPathMap.put(block, ids.get(0));
                        generatedStates.add(Registry.BLOCK.getId(block));
                    }
                });
            });
        }
        public static final TextureKey LOG_KEY = of("log");
        public static final TextureKey LOG_TOP_KEY = of("log_top");
        public static final Model[] TEMPLATE_CHAIR = new Model[]{block("chair/template_chair", TextureKey.TEXTURE, LOG_KEY), block("chair/template_chair", "_tucked", TextureKey.TEXTURE, LOG_KEY)};
        public static final Model[] TEMPLATE_CHAIR_DINNER = new Model[]{block("chair_dinner/template_chair_dinner", TextureKey.TEXTURE, LOG_KEY), block("chair_dinner/template_chair_dinner","_tucked", TextureKey.TEXTURE, LOG_KEY)};
        public static final Model[] TEMPLATE_CHAIR_CLASSIC = new Model[]{block("chair_classic/template_chair_classic", TextureKey.TEXTURE, LOG_KEY), block("chair_classic/template_chair_classic","_tucked", TextureKey.TEXTURE, LOG_KEY)};
        public static final Model[] TEMPLATE_CHAIR_MODERN = new Model[]{block("chair_modern/template_chair_modern", TextureKey.TEXTURE, LOG_KEY), block("chair_modern/template_chair_modern","_tucked", TextureKey.TEXTURE, LOG_KEY)};
        public static final Model[] TEMPLATE_LOG_STOOL = new Model[]{block("log_stool/log_stool", LOG_KEY, LOG_TOP_KEY), block("log_stool/log_stool", "_tucked",LOG_KEY, LOG_TOP_KEY)};
        public static final Model[] TEMPLATE_SIMPLE_STOOL = new Model[]{block("simple_stool/simple_stool", TextureKey.TEXTURE, LOG_KEY), block("simple_stool/simple_stool", "_tucked", TextureKey.TEXTURE, LOG_KEY)};
        public static final Model[] TEMPLATE_CLASSIC_STOOL = new Model[]{block("classic_stool/classic_stool", TextureKey.TEXTURE, LOG_KEY), block("classic_stool/classic_stool", "_tucked", TextureKey.TEXTURE, LOG_KEY)};
        public static final Model[] TEMPLATE_MODERN_STOOL = new Model[]{block("modern_stool/modern_stool", TextureKey.TEXTURE, LOG_KEY), block("modern_stool/modern_stool", "_tucked", TextureKey.TEXTURE, LOG_KEY)};
        public static final Model[] TEMPLATE_BASIC_TABLE_ARRAY = new Model[]{block("table_basic/table_basic", TextureKey.TEXTURE, LOG_KEY), block("table_basic/table_basic_base", TextureKey.TEXTURE, LOG_KEY),  block("table_basic/table_basic_north_east", TextureKey.TEXTURE, LOG_KEY), block("table_basic/table_basic_north_west", TextureKey.TEXTURE, LOG_KEY), block("table_basic/table_basic_south_east", TextureKey.TEXTURE, LOG_KEY), block("table_basic/table_basic_south_west", TextureKey.TEXTURE, LOG_KEY), block("table_basic/table_basic_north_south_east_top", TextureKey.TEXTURE, LOG_KEY), block("table_basic/table_basic_north_south_west_top", TextureKey.TEXTURE, LOG_KEY), block("table_basic/table_basic_east_west_north", TextureKey.TEXTURE, LOG_KEY), block("table_basic/table_basic_east_west_south", TextureKey.TEXTURE, LOG_KEY), block("table_basic/table_basic_north_south_east_bottom", TextureKey.TEXTURE, LOG_KEY), block("table_basic/table_basic_north_south_west_bottom", TextureKey.TEXTURE, LOG_KEY), block("table_basic/table_basic_north_south_east", TextureKey.TEXTURE, LOG_KEY), block("table_basic/table_basic_north_south_west", TextureKey.TEXTURE, LOG_KEY), block("table_basic/table_basic_north_east_corner", TextureKey.TEXTURE, LOG_KEY), block("table_basic/table_basic_north_west_corner", TextureKey.TEXTURE, LOG_KEY), block("table_basic/table_basic_south_east_corner", TextureKey.TEXTURE, LOG_KEY), block("table_basic/table_basic_south_west_corner", TextureKey.TEXTURE, LOG_KEY)};
        public static final Model[] TEMPLATE_CLASSIC_TABLE_ARRAY = new Model[]{block("table_classic/table_classic", TextureKey.TEXTURE, LOG_KEY), block("table_classic/table_classic_middle", TextureKey.TEXTURE, LOG_KEY), block("table_classic/table_classic_one_uved", TextureKey.TEXTURE, LOG_KEY), block("table_classic/table_classic_one", TextureKey.TEXTURE, LOG_KEY), block("table_classic/table_classic_two_uved", TextureKey.TEXTURE, LOG_KEY), block("table_classic/table_classic_two", TextureKey.TEXTURE, LOG_KEY)};
        public static final Model[] TEMPLATE_LOG_TABLE_ARRAY = new Model[]{block("log_table/log_table", TextureKey.TEXTURE, LOG_KEY), block("log_table/log_table_right", TextureKey.TEXTURE, LOG_KEY), block("log_table/log_table_left", TextureKey.TEXTURE, LOG_KEY), block("log_table/log_table_middle", TextureKey.TEXTURE, LOG_KEY)};
        public static final Model[] TEMPLATE_DINNER_TABLE_ARRAY = new Model[]{block("dinner_table/dinner_table", TextureKey.TEXTURE, LOG_KEY), block("dinner_table/dinner_table_middle", TextureKey.TEXTURE, LOG_KEY), block("dinner_table/dinner_table_right", TextureKey.TEXTURE, LOG_KEY), block("dinner_table/dinner_table_left", TextureKey.TEXTURE, LOG_KEY)};
        public static final Model[] TEMPLATE_MODERN_DINNER_TABLE_ARRAY = new Model[]{block("table_modern_dinner/table_modern_dinner", TextureKey.TEXTURE, LOG_KEY), block("table_modern_dinner/table_modern_dinner_middle", TextureKey.TEXTURE, LOG_KEY), block("table_modern_dinner/table_modern_dinner_right", TextureKey.TEXTURE, LOG_KEY), block("table_modern_dinner/table_modern_dinner_left", TextureKey.TEXTURE, LOG_KEY)};
        public static final Model[] TEMPLATE_CLASSIC_NIGHTSTAND_ARRAY = new Model[]{block("classic_nightstand/classic_nightstand", TextureKey.TEXTURE, LOG_KEY), block("classic_nightstand/classic_nightstand_middle", TextureKey.TEXTURE, LOG_KEY), block("classic_nightstand/classic_nightstand_right", TextureKey.TEXTURE, LOG_KEY), block("classic_nightstand/classic_nightstand_left", TextureKey.TEXTURE, LOG_KEY), block("classic_nightstand/classic_nightstand_open", TextureKey.TEXTURE, LOG_KEY), block("classic_nightstand/classic_nightstand_middle_open", TextureKey.TEXTURE, LOG_KEY), block("classic_nightstand/classic_nightstand_right_open", TextureKey.TEXTURE, LOG_KEY), block("classic_nightstand/classic_nightstand_left_open", TextureKey.TEXTURE, LOG_KEY)};
        public static final Model[] TEMPLATE_SIMPLE_BED_ARRAY = new Model[]{block("simple_bed/template/full/simple_bed_white", TextureKey.TEXTURE), block("simple_bed/template/head/simple_bed_head", TextureKey.TEXTURE), block("simple_bed/template/head/simple_bed_head_left", TextureKey.TEXTURE), block("simple_bed/template/head/simple_bed_head_right", TextureKey.TEXTURE), block("simple_bed/template/foot/simple_bed_foot", TextureKey.TEXTURE), block("simple_bed/template/foot/simple_bed_foot_right", TextureKey.TEXTURE), block("simple_bed/template/foot/simple_bed_foot_left", TextureKey.TEXTURE), block("simple_bed/template/bunk/foot/simple_bed_foot_left", TextureKey.TEXTURE), block("simple_bed/template/bunk/foot/simple_bed_foot_right", TextureKey.TEXTURE), block("simple_bed/template/bunk/head/simple_bed_head", TextureKey.TEXTURE)};
        public static final Model[] TEMPLATE_CLASSIC_BED_ARRAY = new Model[]{block("classic_bed/template/full/classic_bed_white", TextureKey.TEXTURE), block("classic_bed/template/head/classic_bed_head", TextureKey.TEXTURE), block("classic_bed/template/head/classic_bed_head_left", TextureKey.TEXTURE), block("classic_bed/template/head/classic_bed_head_right", TextureKey.TEXTURE), block("classic_bed/template/foot/classic_bed_foot", TextureKey.TEXTURE), block("classic_bed/template/foot/classic_bed_foot_right", TextureKey.TEXTURE), block("classic_bed/template/foot/classic_bed_foot_left", TextureKey.TEXTURE), block("classic_bed/template/bunk/foot/classic_bed_foot_left", TextureKey.TEXTURE), block("classic_bed/template/bunk/foot/classic_bed_foot_right", TextureKey.TEXTURE)};
        public static final Model[] TEMPLATE_SIMPLE_BUNK_LADDER_ARRAY = new Model[]{block("simple_bunk_ladder/template/simple_ladder", TextureKey.TEXTURE), block("simple_bunk_ladder/template/simple_ladder_top", TextureKey.TEXTURE)};
        public static final Model[] TEMPLATE_KITCHEN_COUNTER = new Model[]{block("kitchen_counter/kitchen_counter", TextureKey.TEXTURE, LOG_KEY), block("kitchen_counter/kitchen_counter_edge_left", TextureKey.TEXTURE, LOG_KEY), block("kitchen_counter/kitchen_counter_edge_right", TextureKey.TEXTURE, LOG_KEY), block("kitchen_counter/kitchen_counter_inner_corner_left", TextureKey.TEXTURE, LOG_KEY), block("kitchen_counter/kitchen_counter_inner_corner_right", TextureKey.TEXTURE, LOG_KEY), block("kitchen_counter/kitchen_counter_outer_corner_right", TextureKey.TEXTURE, LOG_KEY), block("kitchen_counter/kitchen_counter_outer_corner_left", TextureKey.TEXTURE, LOG_KEY)};
        public static final Model[] TEMPLATE_KITCHEN_DRAWER = new Model[]{block("kitchen_drawer/kitchen_drawer", TextureKey.TEXTURE, LOG_KEY), block("kitchen_drawer/kitchen_drawer_edge_left", TextureKey.TEXTURE, LOG_KEY), block("kitchen_drawer/kitchen_drawer_edge_right", TextureKey.TEXTURE, LOG_KEY), block("kitchen_drawer/kitchen_drawer_inner_corner_left", TextureKey.TEXTURE, LOG_KEY), block("kitchen_drawer/kitchen_drawer_inner_corner_right", TextureKey.TEXTURE, LOG_KEY), block("kitchen_drawer/kitchen_drawer_outer_corner_right", TextureKey.TEXTURE, LOG_KEY), block("kitchen_drawer/kitchen_drawer_outer_corner_left", TextureKey.TEXTURE, LOG_KEY), block("kitchen_drawer/kitchen_drawer_open", TextureKey.TEXTURE, LOG_KEY), block("kitchen_drawer/kitchen_drawer_edge_left_open", TextureKey.TEXTURE, LOG_KEY), block("kitchen_drawer/kitchen_drawer_edge_right_open", TextureKey.TEXTURE, LOG_KEY), block("kitchen_drawer/kitchen_drawer_outer_corner_open_right", TextureKey.TEXTURE, LOG_KEY), block("kitchen_drawer/kitchen_drawer_outer_corner_open_left", TextureKey.TEXTURE, LOG_KEY)};
        public static final Model[] TEMPLATE_KITCHEN_CABINET = new Model[]{block("kitchen_cabinet/kitchen_cabinet", TextureKey.TEXTURE, LOG_KEY), block("kitchen_cabinet/kitchen_cabinet_inner_corner_left", TextureKey.TEXTURE, LOG_KEY), block("kitchen_cabinet/kitchen_cabinet_inner_corner_right", TextureKey.TEXTURE, LOG_KEY), block("kitchen_cabinet/kitchen_cabinet_outer_corner_right", TextureKey.TEXTURE, LOG_KEY), block("kitchen_cabinet/kitchen_cabinet_outer_corner_left", TextureKey.TEXTURE, LOG_KEY), block("kitchen_cabinet/kitchen_cabinet_open", TextureKey.TEXTURE, LOG_KEY), block("kitchen_cabinet/kitchen_cabinet_inner_corner_open_left", TextureKey.TEXTURE, LOG_KEY), block("kitchen_cabinet/kitchen_cabinet_inner_corner_open_right", TextureKey.TEXTURE, LOG_KEY), block("kitchen_cabinet/kitchen_cabinet_outer_corner_open_right", TextureKey.TEXTURE, LOG_KEY), block("kitchen_cabinet/kitchen_cabinet_outer_corner_open_left", TextureKey.TEXTURE, LOG_KEY)};
        public static final Model[] TEMPLATE_KITCHEN_WALL_DRAWER = new Model[]{block("kitchen_drawer/kitchen_drawer_middle", TextureKey.TEXTURE, LOG_KEY), block("kitchen_drawer/kitchen_drawer_middle_inner_corner_left", TextureKey.TEXTURE, LOG_KEY), block("kitchen_drawer/kitchen_drawer_middle_inner_corner_right", TextureKey.TEXTURE, LOG_KEY), block("kitchen_drawer/kitchen_drawer_middle_outer_corner_right", TextureKey.TEXTURE, LOG_KEY), block("kitchen_drawer/kitchen_drawer_middle_outer_corner_left", TextureKey.TEXTURE, LOG_KEY), block("kitchen_drawer/kitchen_drawer_middle_open", TextureKey.TEXTURE, LOG_KEY), block("kitchen_drawer/kitchen_drawer_middle_outer_corner_open_right", TextureKey.TEXTURE, LOG_KEY), block("kitchen_drawer/kitchen_drawer_middle_outer_corner_open_left", TextureKey.TEXTURE, LOG_KEY)};
        public static final Model[] TEMPLATE_KITCHEN_WALL_COUNTER = new Model[]{block("kitchen_counter/kitchen_counter_middle", TextureKey.TEXTURE, LOG_KEY), block("kitchen_counter/kitchen_counter_middle_inner_corner_left", TextureKey.TEXTURE, LOG_KEY), block("kitchen_counter/kitchen_counter_middle_inner_corner_right", TextureKey.TEXTURE, LOG_KEY), block("kitchen_counter/kitchen_counter_middle_outer_corner_right", TextureKey.TEXTURE, LOG_KEY), block("kitchen_counter/kitchen_counter_middle_outer_corner_left", TextureKey.TEXTURE, LOG_KEY)};
        public static final Model[] TEMPLATE_KITCHEN_WALL_DRAWER_SMALL = new Model[]{block("kitchen_wall_drawer_small/kitchen_wall_drawer_small", TextureKey.TEXTURE, LOG_KEY), block("kitchen_wall_drawer_small/kitchen_wall_drawer_small", "_open", TextureKey.TEXTURE, LOG_KEY)};
        public static final Model[] TEMPLATE_KITCHEN_COUNTER_OVEN = new Model[]{block("kitchen_counter_oven/kitchen_counter_oven", TextureKey.TEXTURE, LOG_KEY), block("kitchen_counter_oven/kitchen_counter_oven_middle",TextureKey.TEXTURE, LOG_KEY), block("kitchen_counter_oven/kitchen_counter_oven", "_open", TextureKey.TEXTURE, LOG_KEY), block("kitchen_counter_oven/kitchen_counter_oven_middle", "_open", TextureKey.TEXTURE, LOG_KEY)};
        public static final Model[] TEMPLATE_KITCHEN_SINK = new Model[]{block("kitchen_sink/kitchen_sink", TextureKey.TEXTURE, LOG_KEY), block("kitchen_sink/kitchen_sink_level1",TextureKey.TEXTURE, LOG_KEY), block("kitchen_sink/kitchen_sink_level2", TextureKey.TEXTURE, LOG_KEY), block("kitchen_sink/kitchen_sink_full", TextureKey.TEXTURE, LOG_KEY)};
        public static final Model[] TEMPLATE_LAMP_ARRAY = new Model[]{block("basic_lamp/basic_lamp_bottom", TextureKey.TEXTURE), block("basic_lamp/basic_lamp_middle", TextureKey.TEXTURE),  block("basic_lamp/basic_lamp_single", TextureKey.TEXTURE), block("basic_lamp/basic_lamp_top", TextureKey.TEXTURE)};

        private static Model make(TextureKey ... requiredTextures) {
            return new Model(Optional.empty(), Optional.empty(), requiredTextures);
        }

        private static Model block(String parent, TextureKey ... requiredTextures) {
            Identifier id = new Identifier(PaladinFurnitureMod.MOD_ID, "block/" + parent);
            Model model = new Model(Optional.of(id), Optional.empty(), requiredTextures);
            ModelIDS.put(model, id);
            return model;
        }

        private static Model item(String parent, TextureKey ... requiredTextures) {
            return new Model(Optional.of(new Identifier(PaladinFurnitureMod.MOD_ID, "item/" + parent)), Optional.empty(), requiredTextures);
        }

        private static Model block(String parent, String variant, TextureKey ... requiredTextures) {
            Identifier id = new Identifier(PaladinFurnitureMod.MOD_ID, "block/" + parent + variant);
            Model model = new Model(Optional.of(id), Optional.of(variant), requiredTextures);
            ModelIDS.put(model, id);
            return model;
        }

        private static TextureKey of(String name) {
            return PFMTextureKeyFactory.newTextureKey(name, null);
        }

        private static TextureKey of(String name, TextureKey parent) {
            return PFMTextureKeyFactory.newTextureKey(name, parent);
        }

        private static BlockStateSupplier createSingleStateBlockState(Block block, List<Identifier> modelIdentifiers) {
            BlockStateVariant variant;
            String path = modelIdentifiers.get(0).getPath();
            //Ugly hack to get the folder name for the Baked Block Model
            Identifier id = new Identifier(modelIdentifiers.get(0).getNamespace(), path.split(path.substring(path.lastIndexOf('/')))[0] + path.substring(path.lastIndexOf('/')));
            variant = (BlockStateVariant.create().put(VariantSettings.MODEL, id));
            return VariantsBlockStateSupplier.create(block, variant);
        }
        private static BlockStateSupplier createAxisOrientableTableBlockState(Block block, List<Identifier> modelIdentifiers, int rotation) {
            Map<Direction.Axis, BlockStateVariant> variantMap = new HashMap<>();
            String path = modelIdentifiers.get(0).getPath();
            Identifier id;

            if (modelIdentifiers.size() == 1) {
                id = modelIdentifiers.get(0);
            } else {
                id = new Identifier(modelIdentifiers.get(0).getNamespace(), path.split(path.substring(path.lastIndexOf('/')))[0] + path.substring(path.lastIndexOf('/')));
            }
            Integer[] rotationArray = new Integer[]{0, 90};
            for (int i = 0; rotationArray.length > i; i++) {
                if (rotationArray[i] + rotation > 90) {
                    if (rotationArray[i] == 90)
                        rotationArray[i] = 0;
                    else
                        rotationArray[i] = 90;
                } else {
                    rotationArray[i] += rotation;
                }
            }

            variantMap.put(Direction.Axis.Z, BlockStateVariant.create().put(VariantSettings.MODEL, id).put(VariantSettings.Y, VariantSettings.Rotation.valueOf('R'+String.valueOf(rotationArray[0]))));
            variantMap.put(Direction.Axis.X, BlockStateVariant.create().put(VariantSettings.MODEL, id).put(VariantSettings.Y, VariantSettings.Rotation.valueOf('R'+String.valueOf(rotationArray[1]))));
            return VariantsBlockStateSupplier.create(block).coordinate(BlockStateVariantMap.create(net.minecraft.state.property.Properties.HORIZONTAL_AXIS).register(axis -> {
                for (Direction.Axis axis1 : variantMap.keySet()) {
                    if (axis.equals(axis1))
                        return variantMap.get(axis1);
                }
                return null;
            }));
        }
        private static BlockStateSupplier createAxisOrientableTableBlockState(Block block, List<Identifier> modelIdentifiers) {
            return createAxisOrientableTableBlockState(block, modelIdentifiers, 0);
        }
        private static BlockStateSupplier createOrientableTableBlockState(Block block, List<Identifier> modelIdentifiers) {
            return createOrientableTableBlockState(block,  modelIdentifiers, 0);
        }
        private static BlockStateSupplier createOrientableTableBlockState(Block block, List<Identifier> modelIdentifiers, int rotation) {
            Map<Direction, BlockStateVariant> variantMap = new HashMap<>();
            String path = modelIdentifiers.get(0).getPath();
            Identifier id;
            if (modelIdentifiers.size() == 1) {
                id = modelIdentifiers.get(0);
            } else {
                id = new Identifier(modelIdentifiers.get(0).getNamespace(), path.split(path.substring(path.lastIndexOf('/')))[0] + path.substring(path.lastIndexOf('/')));
            }
            Integer[] rotationArray = new Integer[]{0, 90, 180, 270};
            for (int i = 0; rotationArray.length > i; i++) {
                if (rotationArray[i] + rotation > 270) {
                    if (rotationArray[i] == 270)
                        rotationArray[i] = 0;
                    else
                        rotationArray[i] = 90;
                } else {
                    rotationArray[i] += rotation;
                }
            }
            variantMap.put(Direction.NORTH, BlockStateVariant.create().put(VariantSettings.MODEL, id).put(VariantSettings.Y, VariantSettings.Rotation.valueOf('R'+String.valueOf(rotationArray[0]))));
            variantMap.put(Direction.EAST, BlockStateVariant.create().put(VariantSettings.MODEL, id).put(VariantSettings.Y, VariantSettings.Rotation.valueOf('R'+String.valueOf(rotationArray[1]))));
            variantMap.put(Direction.SOUTH, BlockStateVariant.create().put(VariantSettings.MODEL, id).put(VariantSettings.Y, VariantSettings.Rotation.valueOf('R'+String.valueOf(rotationArray[2]))));
            variantMap.put(Direction.WEST, BlockStateVariant.create().put(VariantSettings.MODEL, id).put(VariantSettings.Y, VariantSettings.Rotation.valueOf('R'+String.valueOf(rotationArray[3]))));
            return VariantsBlockStateSupplier.create(block).coordinate(BlockStateVariantMap.create(net.minecraft.state.property.Properties.HORIZONTAL_FACING).register(facing -> {
                for (Direction direction : variantMap.keySet()) {
                    if (facing.equals(direction))
                        return variantMap.get(direction);
                }
                return null;
            }));
        }
        private static BlockStateSupplier createOrientableKitchenCounter(Block block, List<Identifier> modelIdentifiers){
            return createOrientableKitchenCounter(block, modelIdentifiers, "", "", "", 0);
        }
        private static BlockStateSupplier createOrientableKitchenCounter(Block block, List<Identifier> modelIdentifiers, String override, String furnitureName, String replacement, int rotation) {
            Map<Direction, BlockStateVariant> variantMap = new HashMap<>();
            String path = modelIdentifiers.get(0).getPath().replaceAll(override, "");
            String name = path.split(path.substring(path.lastIndexOf('/')))[0] + path.substring(path.lastIndexOf('/'));
            Identifier id;
            if (modelIdentifiers.size() == 1) {
                id = modelIdentifiers.get(0);
            } else {
                id = new Identifier(modelIdentifiers.get(0).getNamespace(), name.replace(furnitureName, replacement));
            }
            Integer[] rotationArray = new Integer[]{0, 90, 180, 270};
            for (int i = 0; rotationArray.length > i; i++) {
                if (rotationArray[i] + rotation > 270) {
                    if (rotationArray[i] == 180)
                        rotationArray[i] = 0;
                    else
                        rotationArray[i] = 90;
                } else {
                    rotationArray[i] += rotation;
                }
            }
            variantMap.put(Direction.NORTH, BlockStateVariant.create().put(VariantSettings.MODEL, id).put(VariantSettings.Y, VariantSettings.Rotation.valueOf('R'+String.valueOf(rotationArray[0]))).put(VariantSettings.UVLOCK, true));
            variantMap.put(Direction.EAST, BlockStateVariant.create().put(VariantSettings.MODEL, id).put(VariantSettings.Y, VariantSettings.Rotation.valueOf('R'+String.valueOf(rotationArray[1]))).put(VariantSettings.UVLOCK, true));
            variantMap.put(Direction.SOUTH, BlockStateVariant.create().put(VariantSettings.MODEL, id).put(VariantSettings.Y, VariantSettings.Rotation.valueOf('R'+String.valueOf(rotationArray[2]))).put(VariantSettings.UVLOCK, true));
            variantMap.put(Direction.WEST, BlockStateVariant.create().put(VariantSettings.MODEL, id).put(VariantSettings.Y, VariantSettings.Rotation.valueOf('R'+String.valueOf(rotationArray[3]))).put(VariantSettings.UVLOCK, true));
            return VariantsBlockStateSupplier.create(block).coordinate(BlockStateVariantMap.create(net.minecraft.state.property.Properties.HORIZONTAL_FACING).register(facing -> {
                for (Direction direction : variantMap.keySet()) {
                    if (facing.equals(direction))
                        return variantMap.get(direction);
                }
                return null;
            }));
        }

        private static BlockStateSupplier createKitchenSink(Block block, List<Identifier> modelIdentifiers) {
            Map<Direction, VariantSettings.Rotation> rotationMap = new HashMap<>();
            Integer[] rotation = new Integer[]{0, 90, 180, 270};
            rotationMap.put(Direction.NORTH, VariantSettings.Rotation.valueOf('R'+String.valueOf(rotation[0])));
            rotationMap.put(Direction.EAST, VariantSettings.Rotation.valueOf('R'+String.valueOf(rotation[1])));
            rotationMap.put(Direction.SOUTH, VariantSettings.Rotation.valueOf('R'+String.valueOf(rotation[2])));
            rotationMap.put(Direction.WEST, VariantSettings.Rotation.valueOf('R'+String.valueOf(rotation[3])));

            return VariantsBlockStateSupplier.create(block).coordinate(BlockStateVariantMap.create(net.minecraft.state.property.Properties.HORIZONTAL_FACING, AbstractSinkBlock.LEVEL_4).register((facing, level) -> {
                return BlockStateVariant.create().put(VariantSettings.MODEL, modelIdentifiers.get(level)).put(VariantSettings.Y, rotationMap.get(facing)).put(VariantSettings.UVLOCK, true);
            }));
        }

        private static BlockStateSupplier createSmallKitchenDrawer(Block block, List<Identifier> modelIdentifiers, String override, String furnitureName, String replacement) {
            Map<Direction, BlockStateVariant> variantMap = new HashMap<>();
            Map<Direction, BlockStateVariant> variantMapOpen = new HashMap<>();
            Integer[] rotation = new Integer[]{0, 90, 180, 270};

            variantMap.put(Direction.NORTH, BlockStateVariant.create().put(VariantSettings.MODEL, modelIdentifiers.get(0)).put(VariantSettings.Y, VariantSettings.Rotation.valueOf('R'+String.valueOf(rotation[0]))).put(VariantSettings.UVLOCK, true));
            variantMap.put(Direction.EAST, BlockStateVariant.create().put(VariantSettings.MODEL, modelIdentifiers.get(0)).put(VariantSettings.Y, VariantSettings.Rotation.valueOf('R'+String.valueOf(rotation[1]))).put(VariantSettings.UVLOCK, true));
            variantMap.put(Direction.SOUTH, BlockStateVariant.create().put(VariantSettings.MODEL, modelIdentifiers.get(0)).put(VariantSettings.Y, VariantSettings.Rotation.valueOf('R'+String.valueOf(rotation[2]))).put(VariantSettings.UVLOCK, true));
            variantMap.put(Direction.WEST, BlockStateVariant.create().put(VariantSettings.MODEL, modelIdentifiers.get(0)).put(VariantSettings.Y, VariantSettings.Rotation.valueOf('R'+String.valueOf(rotation[3]))).put(VariantSettings.UVLOCK, true));

            variantMapOpen.put(Direction.NORTH, BlockStateVariant.create().put(VariantSettings.MODEL, modelIdentifiers.get(1)).put(VariantSettings.Y, VariantSettings.Rotation.valueOf('R'+String.valueOf(rotation[0]))).put(VariantSettings.UVLOCK, true));
            variantMapOpen.put(Direction.EAST, BlockStateVariant.create().put(VariantSettings.MODEL, modelIdentifiers.get(1)).put(VariantSettings.Y, VariantSettings.Rotation.valueOf('R'+String.valueOf(rotation[1]))).put(VariantSettings.UVLOCK, true));
            variantMapOpen.put(Direction.SOUTH, BlockStateVariant.create().put(VariantSettings.MODEL, modelIdentifiers.get(1)).put(VariantSettings.Y, VariantSettings.Rotation.valueOf('R'+String.valueOf(rotation[2]))).put(VariantSettings.UVLOCK, true));
            variantMapOpen.put(Direction.WEST, BlockStateVariant.create().put(VariantSettings.MODEL, modelIdentifiers.get(1)).put(VariantSettings.Y, VariantSettings.Rotation.valueOf('R'+String.valueOf(rotation[3]))).put(VariantSettings.UVLOCK, true));

            return VariantsBlockStateSupplier.create(block).coordinate(BlockStateVariantMap.create(net.minecraft.state.property.Properties.HORIZONTAL_FACING, net.minecraft.state.property.Properties.OPEN).register((facing, open) -> {
                for (Direction direction : variantMap.keySet()) {
                    if (facing.equals(direction))
                        return open ? variantMapOpen.get(direction) : variantMap.get(direction);
                }
                return null;
            }));
        }


        private static BlockStateSupplier createLadderBlockState(Block block, List<Identifier> modelIdentifiers) {
            When.PropertyCondition northFalse = When.create().set(net.minecraft.state.property.Properties.HORIZONTAL_FACING, Direction.NORTH).set(net.minecraft.state.property.Properties.UP, false);
            When.PropertyCondition northTrue = When.create().set(net.minecraft.state.property.Properties.HORIZONTAL_FACING, Direction.NORTH).set(net.minecraft.state.property.Properties.UP, true);
            When.PropertyCondition eastFalse = When.create().set(net.minecraft.state.property.Properties.HORIZONTAL_FACING, Direction.EAST).set(net.minecraft.state.property.Properties.UP, false);
            When.PropertyCondition eastTrue = When.create().set(net.minecraft.state.property.Properties.HORIZONTAL_FACING, Direction.EAST).set(net.minecraft.state.property.Properties.UP, true);
            When.PropertyCondition westFalse = When.create().set(net.minecraft.state.property.Properties.HORIZONTAL_FACING, Direction.WEST).set(net.minecraft.state.property.Properties.UP, false);
            When.PropertyCondition westTrue = When.create().set(net.minecraft.state.property.Properties.HORIZONTAL_FACING, Direction.WEST).set(net.minecraft.state.property.Properties.UP, true);
            When.PropertyCondition southFalse = When.create().set(net.minecraft.state.property.Properties.HORIZONTAL_FACING, Direction.SOUTH).set(net.minecraft.state.property.Properties.UP, false);
            When.PropertyCondition southTrue = When.create().set(net.minecraft.state.property.Properties.HORIZONTAL_FACING, Direction.SOUTH).set(net.minecraft.state.property.Properties.UP, true);
            return MultipartBlockStateSupplier.create(block)
                    .with(northFalse, BlockStateVariant.create().put(VariantSettings.MODEL, modelIdentifiers.get(0)))
                    .with(northTrue,  BlockStateVariant.create().put(VariantSettings.MODEL, modelIdentifiers.get(1)))
                    .with(eastFalse, BlockStateVariant.create().put(VariantSettings.MODEL, modelIdentifiers.get(0)).put(VariantSettings.Y, VariantSettings.Rotation.R90))
                    .with(eastTrue, BlockStateVariant.create().put(VariantSettings.MODEL, modelIdentifiers.get(1)).put(VariantSettings.Y, VariantSettings.Rotation.R90))
                    .with(westFalse, BlockStateVariant.create().put(VariantSettings.MODEL, modelIdentifiers.get(0)).put(VariantSettings.Y, VariantSettings.Rotation.R270))
                    .with(westTrue, BlockStateVariant.create().put(VariantSettings.MODEL, modelIdentifiers.get(1)).put(VariantSettings.Y, VariantSettings.Rotation.R270))
                    .with(southFalse, BlockStateVariant.create().put(VariantSettings.MODEL, modelIdentifiers.get(0)).put(VariantSettings.Y, VariantSettings.Rotation.R180))
                    .with(southTrue, BlockStateVariant.create().put(VariantSettings.MODEL, modelIdentifiers.get(1)).put(VariantSettings.Y, VariantSettings.Rotation.R180));
        }

        private static BlockStateSupplier createBedBlockState(Block block, List<Identifier> modelIdentifiers) {
            Map<Direction, BlockStateVariant> variantMap = new HashMap<>();
            Identifier id;
            if (modelIdentifiers.size() == 1) {
                id = modelIdentifiers.get(0);
            } else {
                id = ModelIds.getBlockModelId(block);
            }
            Integer[] rotationArray = new Integer[]{0, 90, 180, 270};
            variantMap.put(Direction.NORTH, BlockStateVariant.create().put(VariantSettings.MODEL, id).put(VariantSettings.Y, VariantSettings.Rotation.valueOf('R'+String.valueOf(rotationArray[0]))));
            variantMap.put(Direction.EAST, BlockStateVariant.create().put(VariantSettings.MODEL, id).put(VariantSettings.Y, VariantSettings.Rotation.valueOf('R'+String.valueOf(rotationArray[1]))));
            variantMap.put(Direction.SOUTH, BlockStateVariant.create().put(VariantSettings.MODEL, id).put(VariantSettings.Y, VariantSettings.Rotation.valueOf('R'+String.valueOf(rotationArray[2]))));
            variantMap.put(Direction.WEST, BlockStateVariant.create().put(VariantSettings.MODEL, id).put(VariantSettings.Y, VariantSettings.Rotation.valueOf('R'+String.valueOf(rotationArray[3]))));
            return VariantsBlockStateSupplier.create(block).coordinate(BlockStateVariantMap.create(net.minecraft.state.property.Properties.HORIZONTAL_FACING).register(facing -> {
                for (Direction direction : variantMap.keySet()) {
                    if (facing.equals(direction))
                        return variantMap.get(direction);
                }
                return null;
            }));
        }
        private static BlockStateSupplier createOrientableTuckableBlockState(Block block, List<Identifier> modelIdentifiers) {
            return createOrientableTuckableBlockState(block, modelIdentifiers, 0);
        }
        private static BlockStateSupplier createOrientableTuckableBlockState(Block block, List<Identifier> modelIdentifiers, int rotation) {
            Map<TuckableVariant, BlockStateVariant> variantList = new HashMap<>();
            Integer[] rotationArray = new Integer[]{90, 270, 180, 0};
            for (int i = 0; rotationArray.length > i; i++) {
                if (rotationArray[i] + rotation > 270) {
                    if (rotationArray[i] == 270)
                        rotationArray[i] = 0;
                    else
                        rotationArray[i] = 90;
                } else {
                    rotationArray[i] += rotation;
                }
            }
            for (int i = 0; i <= 1; i++) {
                boolean tucked =  i == 1;
                Identifier id = tucked ? modelIdentifiers.get(1) : modelIdentifiers.get(0);
                for (Direction direction : Direction.values())
                {
                    if (direction.getAxis().isVertical())
                        continue;
                    switch (direction) {
                        case NORTH -> {
                            variantList.put(new TuckableVariant(tucked, direction),BlockStateVariant.create().put(VariantSettings.MODEL, id).put(VariantSettings.Y, VariantSettings.Rotation.valueOf('R'+String.valueOf(rotationArray[0]))));
                            break;
                        }
                        case SOUTH -> {
                            variantList.put(new TuckableVariant(tucked, direction), BlockStateVariant.create().put(VariantSettings.MODEL, id).put(VariantSettings.Y, VariantSettings.Rotation.valueOf('R'+String.valueOf(rotationArray[1]))));
                            break;
                        }
                        case EAST ->  {
                            variantList.put(new TuckableVariant(tucked, direction), BlockStateVariant.create().put(VariantSettings.MODEL, id).put(VariantSettings.Y, VariantSettings.Rotation.valueOf('R'+String.valueOf(rotationArray[2]))));
                            break;
                        }
                        case WEST -> {
                            variantList.put(new TuckableVariant(tucked, direction), BlockStateVariant.create().put(VariantSettings.MODEL, id).put(VariantSettings.Y, VariantSettings.Rotation.valueOf('R'+String.valueOf(rotationArray[3]))));
                            break;
                        }
                    }
                }
            }
            return VariantsBlockStateSupplier.create(block).coordinate(BlockStateVariantMap.create(Properties.HORIZONTAL_FACING, BasicChairBlock.TUCKED).register((direction, aBoolean) -> {
                for (TuckableVariant tuckableVariant : variantList.keySet()){
                    if (tuckableVariant.direction.equals(direction) && tuckableVariant.tucked == aBoolean) {
                        return variantList.get(tuckableVariant);
                    }
                }
                return null;
            }));
        }

        private static class TuckableVariant {
            final boolean tucked;
            final Direction direction;
            static List<TuckableVariant> variants = new ArrayList<>();
            private TuckableVariant(boolean tucked, Direction direction) {
                this.tucked = tucked;
                this.direction = direction;
                variants.add(this);
            }

            public static TuckableVariant get(Direction direction, Boolean tucked) {
                for (TuckableVariant tuckableVariant : variants) {
                    if (tuckableVariant.tucked == tucked && direction.equals(tuckableVariant.direction))
                        return tuckableVariant;
                }
                return null;
            }
        }
    }
}
