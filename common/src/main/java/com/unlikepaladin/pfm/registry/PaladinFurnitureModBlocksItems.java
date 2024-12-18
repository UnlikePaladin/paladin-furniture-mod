package com.unlikepaladin.pfm.registry;


import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.blocks.*;
import com.unlikepaladin.pfm.blocks.behavior.BathtubBehavior;
import com.unlikepaladin.pfm.blocks.behavior.SinkBehavior;
import com.unlikepaladin.pfm.items.DyeKit;
import com.unlikepaladin.pfm.registry.dynamic.LateBlockRegistry;
import net.minecraft.block.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.property.Properties;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Pair;
import net.minecraft.world.biome.Biome;

import java.util.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.ToIntFunction;
import java.util.stream.Stream;

public class PaladinFurnitureModBlocksItems {
    public static final List<Block> BLOCKS = new ArrayList<>();
    public static final Map<Pair<String, ItemGroup>, Set<Item>> ITEM_GROUP_LIST_MAP = new LinkedHashMap<>();
    public static Set<BlockState> originalHomePOIBedStates = new HashSet<>();
    public static final FreezerBlock WHITE_FREEZER = new FreezerBlock(AbstractBlock.Settings.create().resistance(3.5f).strength(5.0f).sounds(BlockSoundGroup.STONE).mapColor(MapColor.WHITE).registryKey(LateBlockRegistry.getBlockRegistryKey("white_freezer")), () -> PaladinFurnitureModBlocksItems.WHITE_FRIDGE);
    public static final FridgeBlock WHITE_FRIDGE = new FridgeBlock(AbstractBlock.Settings.copy(WHITE_FREEZER).nonOpaque().registryKey(LateBlockRegistry.getBlockRegistryKey("white_fridge")), () -> PaladinFurnitureModBlocksItems.WHITE_FREEZER);
    public static final FreezerBlock GRAY_FREEZER = new FreezerBlock(AbstractBlock.Settings.create().resistance(3.5f).strength(5.0f).sounds(BlockSoundGroup.STONE).mapColor(MapColor.GRAY).registryKey(LateBlockRegistry.getBlockRegistryKey("gray_freezer")), () -> PaladinFurnitureModBlocksItems.GRAY_FRIDGE);
    public static final FridgeBlock GRAY_FRIDGE = new FridgeBlock(AbstractBlock.Settings.copy(GRAY_FREEZER).nonOpaque().registryKey(LateBlockRegistry.getBlockRegistryKey("gray_fridge")), () -> PaladinFurnitureModBlocksItems.GRAY_FREEZER);
    public static final FreezerBlock IRON_FREEZER = new IronFreezerBlock(AbstractBlock.Settings.create().resistance(3.5f).strength(5.0f).sounds(BlockSoundGroup.METAL).mapColor(MapColor.IRON_GRAY).registryKey(LateBlockRegistry.getBlockRegistryKey("iron_freezer")), () -> PaladinFurnitureModBlocksItems.IRON_FRIDGE);
    public static final FridgeBlock IRON_FRIDGE = new IronFridgeBlock(AbstractBlock.Settings.copy(IRON_FREEZER).nonOpaque().registryKey(LateBlockRegistry.getBlockRegistryKey("iron_fridge")), () -> PaladinFurnitureModBlocksItems.IRON_FREEZER);
    public static final FridgeBlock XBOX_FRIDGE = new XboxFridgeBlock(AbstractBlock.Settings.copy(WHITE_FREEZER).resistance(1200.0F).nonOpaque().mapColor(MapColor.BLACK).registryKey(LateBlockRegistry.getBlockRegistryKey("xbox_fridge")), null);

    public static final StoveBlock WHITE_STOVE = new StoveBlock(AbstractBlock.Settings.copy(WHITE_FREEZER).registryKey(LateBlockRegistry.getBlockRegistryKey("white_stove")));
    public static final KitchenRangeHoodBlock WHITE_OVEN_RANGEHOOD = new KitchenRangeHoodBlock(AbstractBlock.Settings.copy(WHITE_FREEZER).nonOpaque().registryKey(LateBlockRegistry.getBlockRegistryKey("white_oven_range_hood")));
    public static final StoveBlock GRAY_STOVE = new StoveBlock(AbstractBlock.Settings.copy(GRAY_FREEZER).registryKey(LateBlockRegistry.getBlockRegistryKey("gray_stove")));
    public static final KitchenRangeHoodBlock GRAY_OVEN_RANGEHOOD = new KitchenRangeHoodBlock(AbstractBlock.Settings.copy(GRAY_FREEZER).nonOpaque().registryKey(LateBlockRegistry.getBlockRegistryKey("gray_oven_range_hood")));
    public static final StoveBlock IRON_STOVE = new IronStoveBlock(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK).registryKey(LateBlockRegistry.getBlockRegistryKey("iron_stove")));
    public static final KitchenRangeHoodBlock IRON_OVEN_RANGEHOOD = new KitchenRangeHoodBlock(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK).nonOpaque().registryKey(LateBlockRegistry.getBlockRegistryKey("iron_oven_range_hood")));
    public static final MicrowaveBlock IRON_MICROWAVE = new MicrowaveBlock(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK).registryKey(LateBlockRegistry.getBlockRegistryKey("iron_microwave")));
    public static final TrashcanBlock TRASHCAN = new TrashcanBlock(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK).registryKey(LateBlockRegistry.getBlockRegistryKey("trashcan")));
    public static final InnerTrashcanBlock MESH_TRASHCAN = new InnerTrashcanBlock(AbstractBlock.Settings.copy(Blocks.CHAIN).nonOpaque().registryKey(LateBlockRegistry.getBlockRegistryKey("mesh_trashcan")));

    public static final Item DYE_KIT_YELLOW = new DyeKit(new Item.Settings().maxCount(16).registryKey(LateBlockRegistry.getItemRegistryKey("dye_kit_yellow")), DyeColor.YELLOW);
    public static final Item DYE_KIT_BLUE = new DyeKit(new Item.Settings().maxCount(16).registryKey(LateBlockRegistry.getItemRegistryKey("dye_kit_blue")), DyeColor.BLUE);
    public static final Item DYE_KIT_WHITE = new DyeKit(new Item.Settings().maxCount(16).registryKey(LateBlockRegistry.getItemRegistryKey("dye_kit_white")), DyeColor.WHITE);
    public static final Item DYE_KIT_PINK = new DyeKit(new Item.Settings().maxCount(16).registryKey(LateBlockRegistry.getItemRegistryKey("dye_kit_pink")), DyeColor.PINK);
    public static final Item DYE_KIT_PURPLE = new DyeKit(new Item.Settings().maxCount(16).registryKey(LateBlockRegistry.getItemRegistryKey("dye_kit_purple")), DyeColor.PURPLE);
    public static final Item DYE_KIT_GREEN = new DyeKit(new Item.Settings().maxCount(16).registryKey(LateBlockRegistry.getItemRegistryKey("dye_kit_green")), DyeColor.GREEN);
    public static final Item DYE_KIT_LIGHT_BLUE = new DyeKit(new Item.Settings().maxCount(16).registryKey(LateBlockRegistry.getItemRegistryKey("dye_kit_light_blue")), DyeColor.LIGHT_BLUE);
    public static final Item DYE_KIT_LIGHT_GRAY = new DyeKit(new Item.Settings().maxCount(16).registryKey(LateBlockRegistry.getItemRegistryKey("dye_kit_light_gray")), DyeColor.LIGHT_GRAY);
    public static final Item DYE_KIT_LIME = new DyeKit(new Item.Settings().maxCount(16).registryKey(LateBlockRegistry.getItemRegistryKey("dye_kit_lime")), DyeColor.LIME);
    public static final Item DYE_KIT_ORANGE = new DyeKit(new Item.Settings().maxCount(16).registryKey(LateBlockRegistry.getItemRegistryKey("dye_kit_orange")), DyeColor.ORANGE);
    public static final Item DYE_KIT_BLACK = new DyeKit(new Item.Settings().maxCount(16).registryKey(LateBlockRegistry.getItemRegistryKey("dye_kit_black")), DyeColor.BLACK);
    public static final Item DYE_KIT_BROWN = new DyeKit(new Item.Settings().maxCount(16).registryKey(LateBlockRegistry.getItemRegistryKey("dye_kit_brown")), DyeColor.BROWN);
    public static final Item DYE_KIT_MAGENTA = new DyeKit(new Item.Settings().maxCount(16).registryKey(LateBlockRegistry.getItemRegistryKey("dye_kit_magenta")), DyeColor.MAGENTA);
    public static final Item DYE_KIT_RED = new DyeKit(new Item.Settings().maxCount(16).registryKey(LateBlockRegistry.getItemRegistryKey("dye_kit_red")), DyeColor.RED);
    public static final Item DYE_KIT_CYAN = new DyeKit(new Item.Settings().maxCount(16).registryKey(LateBlockRegistry.getItemRegistryKey("dye_kit_cyan")), DyeColor.CYAN);
    public static final Item DYE_KIT_GRAY = new DyeKit(new Item.Settings().maxCount(16).registryKey(LateBlockRegistry.getItemRegistryKey("dye_kit_gray")), DyeColor.GRAY);

    public static final Block ACACIA_HERRINGBONE_PLANKS = new HerringbonePlankBlock(AbstractBlock.Settings.copy(Blocks.ACACIA_PLANKS).sounds(BlockSoundGroup.WOOD).registryKey(LateBlockRegistry.getBlockRegistryKey("acacia_herringbone_planks")));
    public static final Block SPRUCE_HERRINGBONE_PLANKS = new HerringbonePlankBlock(AbstractBlock.Settings.copy(Blocks.SPRUCE_PLANKS).sounds(BlockSoundGroup.WOOD).registryKey(LateBlockRegistry.getBlockRegistryKey("spruce_herringbone_planks")));
    public static final Block OAK_HERRINGBONE_PLANKS = new HerringbonePlankBlock(AbstractBlock.Settings.copy(Blocks.OAK_PLANKS).sounds(BlockSoundGroup.WOOD).registryKey(LateBlockRegistry.getBlockRegistryKey("oak_herringbone_planks")));
    public static final Block DARK_OAK_HERRINGBONE_PLANKS = new HerringbonePlankBlock(AbstractBlock.Settings.copy(Blocks.DARK_OAK_PLANKS).sounds(BlockSoundGroup.WOOD).registryKey(LateBlockRegistry.getBlockRegistryKey("dark_oak_herringbone_planks")));
    public static final Block JUNGLE_HERRINGBONE_PLANKS = new HerringbonePlankBlock(AbstractBlock.Settings.copy(Blocks.JUNGLE_PLANKS).sounds(BlockSoundGroup.WOOD).registryKey(LateBlockRegistry.getBlockRegistryKey("jungle_herringbone_planks")));
    public static final Block BIRCH_HERRINGBONE_PLANKS = new HerringbonePlankBlock(AbstractBlock.Settings.copy(Blocks.BIRCH_PLANKS).sounds(BlockSoundGroup.WOOD).registryKey(LateBlockRegistry.getBlockRegistryKey("birch_herringbone_planks")));
    public static final Block WARPED_HERRINGBONE_PLANKS = new HerringbonePlankBlock(AbstractBlock.Settings.copy(Blocks.WARPED_PLANKS).sounds(BlockSoundGroup.WOOD).registryKey(LateBlockRegistry.getBlockRegistryKey("warped_herringbone_planks")));
    public static final Block CRIMSON_HERRINGBONE_PLANKS = new HerringbonePlankBlock(AbstractBlock.Settings.copy(Blocks.CRIMSON_PLANKS).sounds(BlockSoundGroup.WOOD).registryKey(LateBlockRegistry.getBlockRegistryKey("crimson_herringbone_planks")));
    public static final Block MANGROVE_HERRINGBONE_PLANKS = new HerringbonePlankBlock(AbstractBlock.Settings.copy(Blocks.MANGROVE_PLANKS).sounds(BlockSoundGroup.WOOD).registryKey(LateBlockRegistry.getBlockRegistryKey("mangrove_herringbone_planks")));
    public static final Block BAMBOO_HERRINGBONE_PLANKS = new HerringbonePlankBlock(AbstractBlock.Settings.copy(Blocks.BAMBOO_PLANKS).sounds(BlockSoundGroup.BAMBOO_WOOD).registryKey(LateBlockRegistry.getBlockRegistryKey("bamboo_herringbone_planks")));
    public static final Block CHERRY_HERRINGBONE_PLANKS = new HerringbonePlankBlock(AbstractBlock.Settings.copy(Blocks.CHERRY_PLANKS).sounds(BlockSoundGroup.CHERRY_WOOD).registryKey(LateBlockRegistry.getBlockRegistryKey("cherry_herringbone_planks")));

    public static final Block RAW_CONCRETE = new Block(AbstractBlock.Settings.copy(Blocks.GRAY_CONCRETE).sounds(BlockSoundGroup.STONE).registryKey(LateBlockRegistry.getBlockRegistryKey("raw_concrete")));
    public static final Block RAW_CONCRETE_POWDER = new ConcretePowderBlock(RAW_CONCRETE, AbstractBlock.Settings.copy(Blocks.GRAY_CONCRETE_POWDER).sounds(BlockSoundGroup.SAND).registryKey(LateBlockRegistry.getBlockRegistryKey("raw_concrete_powder")));
    public static final Block LEATHER_BLOCK = new Block(AbstractBlock.Settings.copy(Blocks.WHITE_WOOL).sounds(BlockSoundGroup.WOOL).mapColor(MapColor.ORANGE).registryKey(LateBlockRegistry.getBlockRegistryKey("leather_block")));

    public static final Block IRON_CHAIN = new ChainBlock(AbstractBlock.Settings.copy(Blocks.IRON_BARS).sounds(BlockSoundGroup.METAL).registryKey(LateBlockRegistry.getBlockRegistryKey("iron_chain")));
    public static final PendantBlock GRAY_MODERN_PENDANT = new PendantBlock(AbstractBlock.Settings.copy(Blocks.IRON_BARS).sounds(BlockSoundGroup.STONE).nonOpaque().luminance(createLightLevelFromLitBlockState(15)).mapColor(MapColor.GRAY).registryKey(LateBlockRegistry.getBlockRegistryKey("gray_modern_pendant")));
    public static final PendantBlock WHITE_MODERN_PENDANT = new PendantBlock(AbstractBlock.Settings.copy(Blocks.IRON_BARS).sounds(BlockSoundGroup.STONE).nonOpaque().luminance(createLightLevelFromLitBlockState(15)).mapColor(MapColor.WHITE).registryKey(LateBlockRegistry.getBlockRegistryKey("white_modern_pendant")));
    public static final PendantBlock GLASS_MODERN_PENDANT = new PendantBlock(AbstractBlock.Settings.copy(Blocks.IRON_BARS).sounds(BlockSoundGroup.STONE).nonOpaque().luminance(createLightLevelFromLitBlockState(15)).mapColor(MapColor.OFF_WHITE).registryKey(LateBlockRegistry.getBlockRegistryKey("glass_modern_pendant")));
    public static final SimpleLightBlock SIMPLE_LIGHT = new SimpleLightBlock(AbstractBlock.Settings.copy(Blocks.IRON_BARS).sounds(BlockSoundGroup.STONE).nonOpaque().luminance(createLightLevelFromLitBlockState(15)).mapColor(MapColor.LIGHT_GRAY).registryKey(LateBlockRegistry.getBlockRegistryKey("simple_light")));

    public static final LightSwitchBlock LIGHT_SWITCH = new LightSwitchBlock(AbstractBlock.Settings.copy(Blocks.WHITE_CONCRETE).sounds(BlockSoundGroup.STONE).nonOpaque().mapColor(MapColor.WHITE).registryKey(LateBlockRegistry.getBlockRegistryKey("light_switch")));
    public static Item LIGHT_SWITCH_ITEM;
    public static Item FURNITURE_BOOK;
    public static final Block BASIC_LAMP = new BasicLampBlock(AbstractBlock.Settings.copy(Blocks.OAK_PLANKS).luminance(createLightLevelFromLitBlockState(15)).registryKey(LateBlockRegistry.getBlockRegistryKey("basic_lamp")));
    public static final Block TOASTER_BLOCK = new PFMToasterBlock(AbstractBlock.Settings.copy(IRON_STOVE).registryKey(LateBlockRegistry.getBlockRegistryKey("iron_toaster")));
    private static ToIntFunction<BlockState> createLightLevelFromLitBlockState(int litLevel) {
        return state -> state.get(Properties.LIT) ? litLevel : 0;
    }

    public static final WorkingTableBlock WORKING_TABLE = new WorkingTableBlock(AbstractBlock.Settings.copy(Blocks.CRAFTING_TABLE).sounds(BlockSoundGroup.WOOD).registryKey(LateBlockRegistry.getBlockRegistryKey("working_table")));

    public static final KitchenStovetopBlock KITCHEN_STOVETOP = new KitchenStovetopBlock(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK).registryKey(LateBlockRegistry.getBlockRegistryKey("kitchen_stovetop")));

    public static final PlateBlock BASIC_PLATE = new PlateBlock(AbstractBlock.Settings.copy(Blocks.WHITE_CONCRETE).nonOpaque().registryKey(LateBlockRegistry.getBlockRegistryKey("basic_plate")));
    public static final CutleryBlock BASIC_CUTLERY = new CutleryBlock(AbstractBlock.Settings.copy(Blocks.GRAY_CONCRETE).nonOpaque().registryKey(LateBlockRegistry.getBlockRegistryKey("basic_cutlery")));

    public static final BasicToiletBlock BASIC_TOILET = new BasicToiletBlock(AbstractBlock.Settings.copy(Blocks.SMOOTH_QUARTZ).nonOpaque().registryKey(LateBlockRegistry.getBlockRegistryKey("basic_toilet")));
    public static final WallToiletPaperBlock WALL_TOILET_PAPER = new WallToiletPaperBlock(AbstractBlock.Settings.create().mapColor(MapColor.OFF_WHITE).nonOpaque().registryKey(LateBlockRegistry.getBlockRegistryKey("wall_toilet_paper")));
    public static final BasicBathtubBlock BASIC_BATHTUB = new BasicBathtubBlock(AbstractBlock.Settings.copy(Blocks.SMOOTH_QUARTZ).nonOpaque().registryKey(LateBlockRegistry.getBlockRegistryKey("basic_bathtub")), BathtubBehavior.TUB_BEHAVIOR, Biome.Precipitation.RAIN);


    public static Block WHITE_MIRROR;
    public static Block GRAY_MIRROR;

    public static final BasicShowerHeadBlock BASIC_SHOWER_HEAD = new BasicShowerHeadBlock(AbstractBlock.Settings.copy(Blocks.SMOOTH_QUARTZ).nonOpaque().registryKey(LateBlockRegistry.getBlockRegistryKey("basic_shower_head")));
    public static final BasicShowerHandleBlock BASIC_SHOWER_HANDLE = new BasicShowerHandleBlock(AbstractBlock.Settings.copy(Blocks.SMOOTH_QUARTZ).nonOpaque().registryKey(LateBlockRegistry.getBlockRegistryKey("basic_shower_handle")));
    public static Item BASIC_SHOWER_HANDLE_ITEM;
    public static Item BASIC_LAMP_ITEM;

    public static final BasicSinkBlock BASIC_SINK = new BasicSinkBlock(AbstractBlock.Settings.copy(Blocks.SMOOTH_QUARTZ).nonOpaque().registryKey(LateBlockRegistry.getBlockRegistryKey("basic_sink")), Biome.Precipitation.RAIN, SinkBehavior.WATER_SINK_BEHAVIOR);
    public static final List<BedBlock> beds = new ArrayList<>();

    public static Block[] getBeds() {
        List<Block> blocks = new ArrayList<>(PaladinFurnitureMod.furnitureEntryMap.get(SimpleBedBlock.class).getAllBlocks());
        blocks.addAll(PaladinFurnitureMod.furnitureEntryMap.get(ClassicBedBlock.class).getAllBlocks());
        return blocks.toArray(new Block[0]);
    }
    public static Stream<Block> streamBlocks() {
        return BLOCKS.stream();
    }
}