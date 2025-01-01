package com.unlikepaladin.pfm.registry.dynamic;

import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.blocks.*;
import com.unlikepaladin.pfm.blocks.behavior.SinkBehavior;
import com.unlikepaladin.pfm.compat.PFMModCompatibility;
import com.unlikepaladin.pfm.data.materials.*;
import com.unlikepaladin.pfm.items.LampItem;
import com.unlikepaladin.pfm.items.LightSwitchItem;
import com.unlikepaladin.pfm.items.ShowerHandleItem;
import com.unlikepaladin.pfm.registry.BlockItemRegistry;
import com.unlikepaladin.pfm.registry.PaladinFurnitureModBlocksItems;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.block.*;
import net.minecraft.block.enums.BedPart;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.resource.featuretoggle.FeatureFlag;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Pair;
import net.minecraft.world.biome.Biome;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Supplier;

public class LateBlockRegistry {

    @ExpectPlatform
    public static <T extends Block> T registerLateBlock(String blockName, Supplier<T> block, boolean registerItem, Pair<String, ItemGroup> group)
    {
        return block.get();
    }

    @ExpectPlatform
    public static <T extends Block> T registerLateBlockClassic(String blockName, T block, boolean registerItem, Pair<String, ItemGroup> group)
    {
        return block;
    }

    @ExpectPlatform
    public static void registerLateItem(String itemName, Supplier<Item> item, Pair<String, ItemGroup> group) {
        return;
    }

    public static <T extends Block> T registerLateBlock(String blockName, Supplier<T> blockSupplier, int count, Pair<String, ItemGroup> group) {
        T block = registerLateBlock(blockName, blockSupplier, false, group);
        PaladinFurnitureModBlocksItems.BLOCKS.add(block);
        registerLateItem(blockName, () -> new BlockItem(block, new Item.Settings().maxCount(count)), group);
        return block;
    }

    public static void registerBlocks() throws InvocationTargetException, InstantiationException, IllegalAccessException {
        PaladinFurnitureMod.pfmModCompatibilities.forEach(PFMModCompatibility::createBlocks);
        PaladinFurnitureMod.furnitureEntryMap.put(BasicChairBlock.class, new FurnitureEntry<>() {{
            for (WoodVariant variant : WoodVariantRegistry.getVariants()) {
                MapColor color = variant.getBaseBlock().getDefaultMapColor();
                this.addBlock(variant, LateBlockRegistry.registerLateBlock(variant.asString()+"_chair", () -> new BasicChairBlock(AbstractBlock.Settings.copy(variant.getBaseBlock()).mapColor(color).nonOpaque()), true, PaladinFurnitureMod.FURNITURE_GROUP), true);
                if (variant.hasStripped())
                    this.addBlock(variant, LateBlockRegistry.registerLateBlock("stripped_" + variant.asString()+"_chair", () -> new BasicChairBlock(AbstractBlock.Settings.copy(variant.getBaseBlock()).mapColor(color).nonOpaque()), true, PaladinFurnitureMod.FURNITURE_GROUP), false);
            }
            for (StoneVariant variant : StoneVariantRegistry.getVariants()) {
                MapColor color = variant.getBaseBlock().getDefaultMapColor();
                this.addBlock(variant, LateBlockRegistry.registerLateBlock( variant.asString()+"_chair", () -> new BasicChairBlock(AbstractBlock.Settings.copy(variant.getBaseBlock()).mapColor(color).nonOpaque()), true, PaladinFurnitureMod.FURNITURE_GROUP), true);
        }}});
        PaladinFurnitureMod.furnitureEntryMap.put(DinnerChairBlock.class, new FurnitureEntry<>() {{
            for (WoodVariant variant : WoodVariantRegistry.getVariants()) {
                MapColor color = variant.getBaseBlock().getDefaultMapColor();
                this.addBlock(variant, LateBlockRegistry.registerLateBlock(variant.asString()+"_chair_dinner", () -> new DinnerChairBlock(AbstractBlock.Settings.copy(variant.getBaseBlock()).mapColor(color).nonOpaque()), true, PaladinFurnitureMod.FURNITURE_GROUP), true);
                if (variant.hasStripped())
                    this.addBlock(variant, LateBlockRegistry.registerLateBlock("stripped_" + variant.asString()+"_chair_dinner", () -> new DinnerChairBlock(AbstractBlock.Settings.copy(variant.getBaseBlock()).mapColor(color).nonOpaque()), true, PaladinFurnitureMod.FURNITURE_GROUP), false);
            }
            for (StoneVariant variant : StoneVariantRegistry.getVariants()) {
                MapColor color = variant.getBaseBlock().getDefaultMapColor();
                this.addBlock(variant, LateBlockRegistry.registerLateBlock( variant.asString()+"_chair_dinner", () -> new DinnerChairBlock(AbstractBlock.Settings.copy(variant.getBaseBlock()).mapColor(color).nonOpaque()), true, PaladinFurnitureMod.FURNITURE_GROUP), true);
            }}});
        PaladinFurnitureMod.furnitureEntryMap.put(ClassicChairBlock.class, new FurnitureEntry<>() {{
            for (WoodVariant variant : WoodVariantRegistry.getVariants()) {
                MapColor color = variant.getBaseBlock().getDefaultMapColor();
                this.addBlock(variant, LateBlockRegistry.registerLateBlock(variant.asString()+"_chair_classic", () -> new ClassicChairBlock(AbstractBlock.Settings.copy(variant.getBaseBlock()).mapColor(color).nonOpaque()), true, PaladinFurnitureMod.FURNITURE_GROUP), true);
                if (variant.hasStripped())
                    this.addBlock(variant, LateBlockRegistry.registerLateBlock("stripped_" + variant.asString()+"_chair_classic", () -> new ClassicChairBlock(AbstractBlock.Settings.copy(variant.getBaseBlock()).mapColor(color).nonOpaque()), true, PaladinFurnitureMod.FURNITURE_GROUP), false);
            }
            for (StoneVariant variant : StoneVariantRegistry.getVariants()) {
                MapColor color = variant.getBaseBlock().getDefaultMapColor();
                this.addBlock(variant, LateBlockRegistry.registerLateBlock( variant.asString()+"_chair_classic", () -> new ClassicChairBlock(AbstractBlock.Settings.copy(variant.getBaseBlock()).mapColor(color).nonOpaque()), true, PaladinFurnitureMod.FURNITURE_GROUP), true);
            }}});
        PaladinFurnitureMod.furnitureEntryMap.put(ClassicChairDyeableBlock.class, new FurnitureEntry<>() {{
            int i = 0;
            for (DyeColor color : DyeColor.values()) {
                if (i > 15)
                    break;
                this.addBlock(registerLateBlock("oak_chair_classic_" + color.getName(), () -> new ClassicChairDyeableBlock(color, AbstractBlock.Settings.copy(PaladinFurnitureMod.furnitureEntryMap.get(BasicChairBlock.class).allBlocks.get(0))), true, PaladinFurnitureMod.FURNITURE_GROUP));
                i++;
            }
        }});

        PaladinFurnitureMod.furnitureEntryMap.put(ModernChairBlock.class, new FurnitureEntry<>() {{
            for (WoodVariant variant : WoodVariantRegistry.getVariants()) {
                MapColor color = variant.getBaseBlock().getDefaultMapColor();
                this.addBlock(variant, LateBlockRegistry.registerLateBlock(variant.asString()+"_chair_modern", () -> new ModernChairBlock(AbstractBlock.Settings.copy(variant.getBaseBlock()).mapColor(color).nonOpaque()), true, PaladinFurnitureMod.FURNITURE_GROUP), true);
                if (variant.hasStripped())
                    this.addBlock(variant, LateBlockRegistry.registerLateBlock("stripped_" + variant.asString()+"_chair_modern", () -> new ModernChairBlock(AbstractBlock.Settings.copy(variant.getBaseBlock()).mapColor(color).nonOpaque()), true, PaladinFurnitureMod.FURNITURE_GROUP), false);
            }
            for (StoneVariant variant : StoneVariantRegistry.getVariants()) {
                MapColor color = variant.getBaseBlock().getDefaultMapColor();
                this.addBlock(variant, LateBlockRegistry.registerLateBlock( variant.asString()+"_chair_modern", () -> new ModernChairBlock(AbstractBlock.Settings.copy(variant.getBaseBlock()).mapColor(color).nonOpaque()), true, PaladinFurnitureMod.FURNITURE_GROUP), true);
            }}});
        PaladinFurnitureMod.furnitureEntryMap.put(FroggyChairBlock.class, new FurnitureEntry<>() {{
            this.addBlock(registerLateBlock("froggy_chair", () -> new FroggyChairBlock(AbstractBlock.Settings.create().strength(9.0f).resistance(8.0f).nonOpaque().requiresTool().mapColor(MapColor.GREEN)), true, PaladinFurnitureMod.FURNITURE_GROUP));
            this.addBlock(registerLateBlock("froggy_chair_pink", () -> new FroggyChairBlock(AbstractBlock.Settings.copy(this.allBlocks.get(0)).mapColor(MapColor.PINK)), true, PaladinFurnitureMod.FURNITURE_GROUP));
            this.addBlock(registerLateBlock("froggy_chair_light_blue", () -> new FroggyChairBlock(AbstractBlock.Settings.copy(this.allBlocks.get(0)).mapColor(MapColor.LIGHT_BLUE)), true, PaladinFurnitureMod.FURNITURE_GROUP));
            this.addBlock(registerLateBlock("froggy_chair_blue", () -> new FroggyChairBlock(AbstractBlock.Settings.copy(this.allBlocks.get(0)).mapColor(MapColor.BLUE)), true, PaladinFurnitureMod.FURNITURE_GROUP));
            this.addBlock(registerLateBlock("froggy_chair_orange", () -> new FroggyChairBlock(AbstractBlock.Settings.copy(this.allBlocks.get(0)).mapColor(MapColor.ORANGE)), true, PaladinFurnitureMod.FURNITURE_GROUP));
            this.addBlock(registerLateBlock("froggy_chair_yellow", () -> new FroggyChairBlock(AbstractBlock.Settings.copy(this.allBlocks.get(0)).mapColor(MapColor.YELLOW)), true, PaladinFurnitureMod.FURNITURE_GROUP));
        }});
        PaladinFurnitureMod.furnitureEntryMap.put(SimpleSofaBlock.class, new FurnitureEntry<>() {{
            int i = 0;
            for (DyeColor color : DyeColor.values()) {
                if (i > 15)
                    break;
                this.addBlock(registerLateBlock(color.getName() + "_simple_sofa", () -> new SimpleSofaBlock(color, AbstractBlock.Settings.create().burnable().strength(2.0f).resistance(2.0f).nonOpaque().sounds(BlockSoundGroup.WOOL).mapColor(color.getMapColor())), true, PaladinFurnitureMod.FURNITURE_GROUP));
                i++;
            }
        }});
        PaladinFurnitureMod.furnitureEntryMap.put(ArmChairBlock.class, new FurnitureEntry<>() {{
            this.addBlock(registerLateBlock("arm_chair_leather", () -> new ArmChairBlock(AbstractBlock.Settings.create().strength(2.0f).resistance(2.0f).nonOpaque().sounds(BlockSoundGroup.WOOL)), true, PaladinFurnitureMod.FURNITURE_GROUP));
        }});
        PaladinFurnitureMod.furnitureEntryMap.put(ArmChairColoredBlock.class, new FurnitureEntry<>() {{
            int i = 0;
            for (DyeColor color : DyeColor.values()) {
                if (i > 15)
                    break;
                this.addBlock(registerLateBlock(color.getName() + "_arm_chair", () -> new ArmChairColoredBlock(color, AbstractBlock.Settings.create().burnable().strength(2.0f).resistance(2.0f).nonOpaque().sounds(BlockSoundGroup.WOOL).mapColor(color.getMapColor())), true, PaladinFurnitureMod.FURNITURE_GROUP));
                i++;
            }
        }});

        PaladinFurnitureMod.furnitureEntryMap.put(BasicTableBlock.class, new FurnitureEntry<>() {{
            for (WoodVariant variant : WoodVariantRegistry.getVariants()) {
                MapColor color = variant.getBaseBlock().getDefaultMapColor();
                this.addBlock(variant, LateBlockRegistry.registerLateBlock(variant.asString()+"_table_basic", () -> new BasicTableBlock(AbstractBlock.Settings.copy(variant.getBaseBlock()).mapColor(color).nonOpaque()), true, PaladinFurnitureMod.FURNITURE_GROUP), true);
                if (variant.hasStripped())
                    this.addBlock(variant, LateBlockRegistry.registerLateBlock("stripped_" + variant.asString()+"_table_basic", () -> new BasicTableBlock(AbstractBlock.Settings.copy(variant.getBaseBlock()).mapColor(color).nonOpaque()), true, PaladinFurnitureMod.FURNITURE_GROUP), false);
            }
            for (StoneVariant variant : StoneVariantRegistry.getVariants()) {
                MapColor color = variant.getBaseBlock().getDefaultMapColor();
                this.addBlock(variant, LateBlockRegistry.registerLateBlock( variant.asString()+"_table_basic", () -> new BasicTableBlock(AbstractBlock.Settings.copy(variant.getBaseBlock()).mapColor(color).nonOpaque()), true, PaladinFurnitureMod.FURNITURE_GROUP), true);
            }}});
        PaladinFurnitureMod.furnitureEntryMap.put(ClassicTableBlock.class, new FurnitureEntry<>() {{
            for (WoodVariant variant : WoodVariantRegistry.getVariants()) {
                MapColor color = variant.getBaseBlock().getDefaultMapColor();
                this.addBlock(variant, LateBlockRegistry.registerLateBlock(variant.asString()+"_table_classic", () -> new ClassicTableBlock(AbstractBlock.Settings.copy(variant.getBaseBlock()).mapColor(color).nonOpaque()), true, PaladinFurnitureMod.FURNITURE_GROUP), true);
                if (variant.hasStripped())
                    this.addBlock(variant, LateBlockRegistry.registerLateBlock("stripped_" + variant.asString()+"_table_classic", () -> new ClassicTableBlock(AbstractBlock.Settings.copy(variant.getBaseBlock()).mapColor(color).nonOpaque()), true, PaladinFurnitureMod.FURNITURE_GROUP), false);
            }
            for (StoneVariant variant : StoneVariantRegistry.getVariants()) {
                MapColor color = variant.getBaseBlock().getDefaultMapColor();
                this.addBlock(variant, LateBlockRegistry.registerLateBlock( variant.asString()+"_table_classic", () -> new ClassicTableBlock(AbstractBlock.Settings.copy(variant.getBaseBlock()).mapColor(color).nonOpaque()), true, PaladinFurnitureMod.FURNITURE_GROUP), true);
            }}});

        PaladinFurnitureMod.furnitureEntryMap.put(LogTableBlock.class, new FurnitureEntry<>() {{
            for (WoodVariant variant : WoodVariantRegistry.getVariants()) {
                MapColor color = variant.getBaseBlock().getDefaultMapColor();
                String postfix = variant.isNetherWood() ? "stem" : "log";
                this.addBlock(variant, LateBlockRegistry.registerLateBlock(variant.asString()+"_table_" + postfix, () -> new LogTableBlock(AbstractBlock.Settings.copy(variant.getBaseBlock()).mapColor(color).nonOpaque()), true, PaladinFurnitureMod.FURNITURE_GROUP), true);
                if (variant.hasStripped())
                    this.addBlock(variant, LateBlockRegistry.registerLateBlock("stripped_" + variant.asString()+"_table_" + postfix, () -> new LogTableBlock(AbstractBlock.Settings.copy(variant.getBaseBlock()).mapColor(color).nonOpaque()), true, PaladinFurnitureMod.FURNITURE_GROUP), false);
            }
            for (StoneVariant variant : StoneVariantRegistry.getVariants()) {
                MapColor color = variant.getBaseBlock().getDefaultMapColor();
                this.addBlock(variant, LateBlockRegistry.registerLateBlock( variant.asString()+"_table_natural", () -> new LogTableBlock(AbstractBlock.Settings.copy(variant.getBaseBlock()).mapColor(color).nonOpaque()), true, PaladinFurnitureMod.FURNITURE_GROUP), true);
            }}});

        PaladinFurnitureMod.furnitureEntryMap.put(RawLogTableBlock.class, new FurnitureEntry<>() {{
            for (WoodVariant variant : WoodVariantRegistry.getVariants()) {
                MapColor color = variant.getBaseBlock().getDefaultMapColor();
                String postfix = variant.isNetherWood() ? "stem" : "log";
                this.addBlock(variant, LateBlockRegistry.registerLateBlock(variant.asString()+"_raw_table_" + postfix, () -> new RawLogTableBlock(AbstractBlock.Settings.copy(variant.getBaseBlock()).mapColor(color).nonOpaque()), true, PaladinFurnitureMod.FURNITURE_GROUP), true);
                if (variant.hasStripped())
                    this.addBlock(variant, LateBlockRegistry.registerLateBlock("stripped_" +variant.asString()+"_raw_table_" + postfix, () -> new RawLogTableBlock(AbstractBlock.Settings.copy(variant.getBaseBlock()).mapColor(color).nonOpaque()), true, PaladinFurnitureMod.FURNITURE_GROUP), false);
            }
        }});
        PaladinFurnitureMod.furnitureEntryMap.put(DinnerTableBlock.class, new FurnitureEntry<>() {{
            for (WoodVariant variant : WoodVariantRegistry.getVariants()) {
                MapColor color = variant.getBaseBlock().getDefaultMapColor();
                this.addBlock(variant, LateBlockRegistry.registerLateBlock(variant.asString()+"_table_dinner", () -> new DinnerTableBlock(AbstractBlock.Settings.copy(variant.getBaseBlock()).mapColor(color).nonOpaque()), true, PaladinFurnitureMod.FURNITURE_GROUP), true);
                if (variant.hasStripped())
                    this.addBlock(variant, LateBlockRegistry.registerLateBlock("stripped_" + variant.asString()+"_table_dinner", () -> new DinnerTableBlock(AbstractBlock.Settings.copy(variant.getBaseBlock()).mapColor(color).nonOpaque()), true, PaladinFurnitureMod.FURNITURE_GROUP), false);
            }
            for (StoneVariant variant : StoneVariantRegistry.getVariants()) {
                MapColor color = variant.getBaseBlock().getDefaultMapColor();
                this.addBlock(variant, LateBlockRegistry.registerLateBlock( variant.asString()+"_table_dinner", () -> new DinnerTableBlock(AbstractBlock.Settings.copy(variant.getBaseBlock()).mapColor(color).nonOpaque()), true, PaladinFurnitureMod.FURNITURE_GROUP), true);
        }}});
        PaladinFurnitureMod.furnitureEntryMap.put(ModernDinnerTableBlock.class, new FurnitureEntry<>() {{
            for (WoodVariant variant : WoodVariantRegistry.getVariants()) {
                MapColor color = variant.getBaseBlock().getDefaultMapColor();
                this.addBlock(variant, LateBlockRegistry.registerLateBlock(variant.asString()+"_table_modern_dinner", () -> new ModernDinnerTableBlock(AbstractBlock.Settings.copy(variant.getBaseBlock()).mapColor(color).nonOpaque()), true, PaladinFurnitureMod.FURNITURE_GROUP), true);
                if (variant.hasStripped())
                    this.addBlock(variant, LateBlockRegistry.registerLateBlock("stripped_" + variant.asString()+"_table_modern_dinner", () -> new ModernDinnerTableBlock(AbstractBlock.Settings.copy(variant.getBaseBlock()).mapColor(color).nonOpaque()), true, PaladinFurnitureMod.FURNITURE_GROUP), false);
            }
            for (StoneVariant variant : StoneVariantRegistry.getVariants()) {
                MapColor color = variant.getBaseBlock().getDefaultMapColor();
                this.addBlock(variant, LateBlockRegistry.registerLateBlock( variant.asString()+"_table_modern_dinner", () -> new ModernDinnerTableBlock(AbstractBlock.Settings.copy(variant.getBaseBlock()).mapColor(color).nonOpaque()), true, PaladinFurnitureMod.FURNITURE_GROUP), true);
        }}});
        PaladinFurnitureMod.furnitureEntryMap.put(BasicCoffeeTableBlock.class, new FurnitureEntry<>() {{
            for (WoodVariant variant : WoodVariantRegistry.getVariants()) {
                MapColor color = variant.getBaseBlock().getDefaultMapColor();
                this.addBlock(variant, LateBlockRegistry.registerLateBlock(variant.asString()+"_coffee_table_basic", () -> new BasicCoffeeTableBlock(AbstractBlock.Settings.copy(variant.getBaseBlock()).mapColor(color).nonOpaque()), true, PaladinFurnitureMod.FURNITURE_GROUP), true);
                if (variant.hasStripped())
                    this.addBlock(variant, LateBlockRegistry.registerLateBlock("stripped_" + variant.asString()+"_coffee_table_basic", () -> new BasicCoffeeTableBlock(AbstractBlock.Settings.copy(variant.getBaseBlock()).mapColor(color).nonOpaque()), true, PaladinFurnitureMod.FURNITURE_GROUP), false);
            }
            for (StoneVariant variant : StoneVariantRegistry.getVariants()) {
                MapColor color = variant.getBaseBlock().getDefaultMapColor();
                this.addBlock(variant, LateBlockRegistry.registerLateBlock( variant.asString()+"_coffee_table_basic", () -> new BasicCoffeeTableBlock(AbstractBlock.Settings.copy(variant.getBaseBlock()).mapColor(color).nonOpaque()), true, PaladinFurnitureMod.FURNITURE_GROUP), true);
            }}});
        PaladinFurnitureMod.furnitureEntryMap.put(ModernCoffeeTableBlock.class, new FurnitureEntry<>() {{
            for (WoodVariant variant : WoodVariantRegistry.getVariants()) {
                MapColor color = variant.getBaseBlock().getDefaultMapColor();
                this.addBlock(variant, LateBlockRegistry.registerLateBlock(variant.asString()+"_coffee_table_modern", () -> new ModernCoffeeTableBlock(AbstractBlock.Settings.copy(variant.getBaseBlock()).mapColor(color).nonOpaque()), true, PaladinFurnitureMod.FURNITURE_GROUP), true);
                if (variant.hasStripped())
                    this.addBlock(variant, LateBlockRegistry.registerLateBlock("stripped_" + variant.asString()+"_coffee_table_modern", () -> new ModernCoffeeTableBlock(AbstractBlock.Settings.copy(variant.getBaseBlock()).mapColor(color).nonOpaque()), true, PaladinFurnitureMod.FURNITURE_GROUP), false);
            }
            for (StoneVariant variant : StoneVariantRegistry.getVariants()) {
                MapColor color = variant.getBaseBlock().getDefaultMapColor();
                this.addBlock(variant, LateBlockRegistry.registerLateBlock( variant.asString()+"_coffee_table_modern", () -> new ModernCoffeeTableBlock(AbstractBlock.Settings.copy(variant.getBaseBlock()).mapColor(color).nonOpaque()), true, PaladinFurnitureMod.FURNITURE_GROUP), true);
            }}});
        PaladinFurnitureMod.furnitureEntryMap.put(ClassicCoffeeTableBlock.class, new FurnitureEntry<>() {{
            for (WoodVariant variant : WoodVariantRegistry.getVariants()) {
                MapColor color = variant.getBaseBlock().getDefaultMapColor();
                this.addBlock(variant, LateBlockRegistry.registerLateBlock(variant.asString()+"_coffee_table_classic", () -> new ClassicCoffeeTableBlock(AbstractBlock.Settings.copy(variant.getBaseBlock()).mapColor(color).nonOpaque()), true, PaladinFurnitureMod.FURNITURE_GROUP), true);
                if (variant.hasStripped())
                    this.addBlock(variant, LateBlockRegistry.registerLateBlock("stripped_" + variant.asString()+"_coffee_table_classic", () -> new ClassicCoffeeTableBlock(AbstractBlock.Settings.copy(variant.getBaseBlock()).mapColor(color).nonOpaque()), true, PaladinFurnitureMod.FURNITURE_GROUP), false);
            }
            for (StoneVariant variant : StoneVariantRegistry.getVariants()) {
                MapColor color = variant.getBaseBlock().getDefaultMapColor();
                this.addBlock(variant, LateBlockRegistry.registerLateBlock( variant.asString()+"_coffee_table_classic", () -> new ClassicCoffeeTableBlock(AbstractBlock.Settings.copy(variant.getBaseBlock()).mapColor(color).nonOpaque()), true, PaladinFurnitureMod.FURNITURE_GROUP), true);
            }}});
        PaladinFurnitureMod.furnitureEntryMap.put(ClassicNightstandBlock.class, new FurnitureEntry<>() {{
            for (WoodVariant variant : WoodVariantRegistry.getVariants()) {
                MapColor color = variant.getBaseBlock().getDefaultMapColor();
                this.addBlock(variant, LateBlockRegistry.registerLateBlock(variant.asString()+"_classic_nightstand", () -> new ClassicNightstandBlock(AbstractBlock.Settings.copy(variant.getBaseBlock()).mapColor(color).nonOpaque()), true, PaladinFurnitureMod.FURNITURE_GROUP), true);
                if (variant.hasStripped())
                    this.addBlock(variant, LateBlockRegistry.registerLateBlock("stripped_" + variant.asString()+"_classic_nightstand", () -> new ClassicNightstandBlock(AbstractBlock.Settings.copy(variant.getBaseBlock()).mapColor(color).nonOpaque()), true, PaladinFurnitureMod.FURNITURE_GROUP), false);
            }
            for (StoneVariant variant : StoneVariantRegistry.getVariants()) {
                MapColor color = variant.getBaseBlock().getDefaultMapColor();
                this.addBlock(variant, LateBlockRegistry.registerLateBlock( variant.asString()+"_classic_nightstand", () -> new ClassicNightstandBlock(AbstractBlock.Settings.copy(variant.getBaseBlock()).mapColor(color).nonOpaque()), true, PaladinFurnitureMod.FURNITURE_GROUP), true);
            }}});
        PaladinFurnitureMod.furnitureEntryMap.put(SimpleBedBlock.class, new FurnitureEntry<>() {{
            for (WoodVariant variant : WoodVariantRegistry.getVariants()) {
                int i = 0;for (DyeColor color : DyeColor.values()) {
                if (i > 15)
                        break;
                    SimpleBedBlock block = LateBlockRegistry.registerLateBlock(variant.asString() + "_" + color.getName() +  "_simple_bed", () -> new SimpleBedBlock(color, AbstractBlock.Settings.create().mapColor(state -> state.get(BedBlock.PART) == BedPart.FOOT ? color.getMapColor() : MapColor.WHITE_GRAY).sounds(variant.getBaseBlock().getSoundGroup(variant.getBaseBlock().getDefaultState())).requires(variant.getFeatureList().toArray(new FeatureFlag[0])).strength(0.2f).nonOpaque()), 1, PaladinFurnitureMod.FURNITURE_GROUP);                    this.addBlock(variant, block, true);
                    PaladinFurnitureModBlocksItems.beds.add(block);
                    i++;
                }
            }
        }});
        PaladinFurnitureMod.furnitureEntryMap.put(ClassicBedBlock.class, new FurnitureEntry<>() {{
            for (WoodVariant variant : WoodVariantRegistry.getVariants()) {
                int i = 0;for (DyeColor color : DyeColor.values()) {
                if (i > 15)
                        break;
                    ClassicBedBlock block = LateBlockRegistry.registerLateBlock(variant.asString() + "_" + color.getName() +  "_classic_bed", () -> new ClassicBedBlock(color, AbstractBlock.Settings.create().mapColor(state -> state.get(BedBlock.PART) == BedPart.FOOT ? color.getMapColor() : MapColor.WHITE_GRAY).sounds(variant.getBaseBlock().getSoundGroup(variant.getBaseBlock().getDefaultState())).strength(0.2f).nonOpaque().requires(variant.getFeatureList().toArray(new FeatureFlag[0]))), 1, PaladinFurnitureMod.FURNITURE_GROUP);
                    this.addBlock(variant, block, true);
                    PaladinFurnitureModBlocksItems.beds.add(block);
                    i++;
                }
            }
        }});
        PaladinFurnitureMod.furnitureEntryMap.put(SimpleBunkLadderBlock.class, new FurnitureEntry<>() {{
            for (WoodVariant variant : WoodVariantRegistry.getVariants()) {
                MapColor color = variant.getBaseBlock().getDefaultMapColor();
                this.addBlock(variant, LateBlockRegistry.registerLateBlock(variant.asString()+"_simple_bunk_ladder", () -> new SimpleBunkLadderBlock(AbstractBlock.Settings.copy(variant.getBaseBlock()).mapColor(color).nonOpaque()), true, PaladinFurnitureMod.FURNITURE_GROUP), true);
            }}});
        PaladinFurnitureMod.furnitureEntryMap.put(LogStoolBlock.class, new FurnitureEntry<>() {{
            for (WoodVariant variant : WoodVariantRegistry.getVariants()) {
                MapColor color = variant.getBaseBlock().getDefaultMapColor();
                String postfix = variant.isNetherWood() ? "stem" : "log";
                this.addBlock(variant, LateBlockRegistry.registerLateBlock(variant.asString() + "_" + postfix + "_stool", () -> new LogStoolBlock(AbstractBlock.Settings.copy(variant.getBaseBlock()).mapColor(color).nonOpaque()), true, PaladinFurnitureMod.FURNITURE_GROUP), true);
            }}});
        PaladinFurnitureMod.furnitureEntryMap.put(SimpleStoolBlock.class, new FurnitureEntry<>() {{
            for (WoodVariant variant : WoodVariantRegistry.getVariants()) {
                MapColor color = variant.getBaseBlock().getDefaultMapColor();
                this.addBlock(variant, LateBlockRegistry.registerLateBlock(variant.asString()+"_simple_stool", () -> new SimpleStoolBlock(AbstractBlock.Settings.copy(variant.getBaseBlock()).mapColor(color).nonOpaque()), true, PaladinFurnitureMod.FURNITURE_GROUP), true);
                if (variant.hasStripped())
                    this.addBlock(variant, LateBlockRegistry.registerLateBlock("stripped_" + variant.asString()+"_simple_stool", () -> new SimpleStoolBlock(AbstractBlock.Settings.copy(variant.getBaseBlock()).mapColor(color).nonOpaque()), true, PaladinFurnitureMod.FURNITURE_GROUP), false);
            }
            for (StoneVariant variant : StoneVariantRegistry.getVariants()) {
                MapColor color = variant.getBaseBlock().getDefaultMapColor();
                this.addBlock(variant, LateBlockRegistry.registerLateBlock( variant.asString()+"_simple_stool", () -> new SimpleStoolBlock(AbstractBlock.Settings.copy(variant.getBaseBlock()).mapColor(color).nonOpaque()), true, PaladinFurnitureMod.FURNITURE_GROUP), true);
            }}});
        PaladinFurnitureMod.furnitureEntryMap.put(ClassicStoolBlock.class, new FurnitureEntry<>() {{
            for (WoodVariant variant : WoodVariantRegistry.getVariants()) {
                MapColor color = variant.getBaseBlock().getDefaultMapColor();
                this.addBlock(variant, LateBlockRegistry.registerLateBlock(variant.asString()+"_classic_stool", () -> new ClassicStoolBlock(AbstractBlock.Settings.copy(variant.getBaseBlock()).mapColor(color).nonOpaque()), true, PaladinFurnitureMod.FURNITURE_GROUP), true);
                if (variant.hasStripped())
                    this.addBlock(variant, LateBlockRegistry.registerLateBlock("stripped_" + variant.asString()+"_classic_stool", () -> new ClassicStoolBlock(AbstractBlock.Settings.copy(variant.getBaseBlock()).mapColor(color).nonOpaque()), true, PaladinFurnitureMod.FURNITURE_GROUP), false);
            }
            for (StoneVariant variant : StoneVariantRegistry.getVariants()) {
                MapColor color = variant.getBaseBlock().getDefaultMapColor();
                this.addBlock(variant, LateBlockRegistry.registerLateBlock( variant.asString()+"_classic_stool", () -> new ClassicStoolBlock(AbstractBlock.Settings.copy(variant.getBaseBlock()).mapColor(color).nonOpaque()), true, PaladinFurnitureMod.FURNITURE_GROUP), true);
            }}});

        PaladinFurnitureMod.furnitureEntryMap.put(ModernStoolBlock.class, new FurnitureEntry<>() {{
            for (WoodVariant variant : WoodVariantRegistry.getVariants()) {
                MapColor color = variant.getBaseBlock().getDefaultMapColor();
                this.addBlock(variant, LateBlockRegistry.registerLateBlock(variant.asString()+"_modern_stool", () -> new ModernStoolBlock(AbstractBlock.Settings.copy(variant.getBaseBlock()).mapColor(color).nonOpaque()), true, PaladinFurnitureMod.FURNITURE_GROUP), true);
                if (variant.hasStripped())
                    this.addBlock(variant, LateBlockRegistry.registerLateBlock("stripped_" + variant.asString()+"_modern_stool", () -> new ModernStoolBlock(AbstractBlock.Settings.copy(variant.getBaseBlock()).mapColor(color).nonOpaque()), true, PaladinFurnitureMod.FURNITURE_GROUP), false);
            }
            for (StoneVariant variant : StoneVariantRegistry.getVariants()) {
                MapColor color = variant.getBaseBlock().getDefaultMapColor();
                this.addBlock(variant, LateBlockRegistry.registerLateBlock( variant.asString()+"_modern_stool", () -> new ModernStoolBlock(AbstractBlock.Settings.copy(variant.getBaseBlock()).mapColor(color).nonOpaque()), true, PaladinFurnitureMod.FURNITURE_GROUP), true);
            }
            for (ExtraStoolVariant variant : ExtraStoolVariant.values()) {
                MapColor color = variant.getBaseBlock().getDefaultMapColor();
                this.addBlock(variant, LateBlockRegistry.registerLateBlock( variant.asString()+"_modern_stool", () -> new ModernStoolBlock(AbstractBlock.Settings.copy(variant.getBaseBlock()).mapColor(color).nonOpaque()), true, PaladinFurnitureMod.FURNITURE_GROUP), true);
            }
        }});

        PaladinFurnitureMod.furnitureEntryMap.put(KitchenDrawerBlock.class, new FurnitureEntry<KitchenDrawerBlock>());
        PaladinFurnitureMod.furnitureEntryMap.put(KitchenCabinetBlock.class, new FurnitureEntry<KitchenCabinetBlock>());
        PaladinFurnitureMod.furnitureEntryMap.put(KitchenSinkBlock.class, new FurnitureEntry<KitchenSinkBlock>());
        PaladinFurnitureMod.furnitureEntryMap.put(KitchenCounterOvenBlock.class, new FurnitureEntry<KitchenCounterOvenBlock>());
        PaladinFurnitureMod.furnitureEntryMap.put(KitchenWallCounterBlock.class, new FurnitureEntry<KitchenWallCounterBlock>());
        PaladinFurnitureMod.furnitureEntryMap.put(KitchenWallDrawerBlock.class, new FurnitureEntry<KitchenWallDrawerBlock>());
        PaladinFurnitureMod.furnitureEntryMap.put(KitchenWallDrawerSmallBlock.class, new FurnitureEntry<KitchenWallDrawerSmallBlock>());
        PaladinFurnitureMod.furnitureEntryMap.put(KitchenCounterBlock.class, new FurnitureEntry<>() {{
            for (WoodVariant variant : WoodVariantRegistry.getVariants()) {
                MapColor color = variant.getBaseBlock().getDefaultMapColor();
                this.addBlock(variant, LateBlockRegistry.registerLateBlock(variant.asString()+"_kitchen_counter", () -> new KitchenCounterBlock(AbstractBlock.Settings.copy(variant.getBaseBlock()).mapColor(color).nonOpaque()), true, PaladinFurnitureMod.FURNITURE_GROUP), true);
                PaladinFurnitureMod.furnitureEntryMap.get(KitchenDrawerBlock.class).addBlock(variant, LateBlockRegistry.registerLateBlock(variant.asString()+"_kitchen_drawer", () -> new KitchenDrawerBlock(AbstractBlock.Settings.copy(variant.getBaseBlock()).mapColor(color).nonOpaque()), true, PaladinFurnitureMod.FURNITURE_GROUP), true);
                PaladinFurnitureMod.furnitureEntryMap.get(KitchenCabinetBlock.class).addBlock(variant, LateBlockRegistry.registerLateBlock(variant.asString()+"_kitchen_cabinet", () -> new KitchenCabinetBlock(AbstractBlock.Settings.copy(variant.getBaseBlock()).mapColor(color).nonOpaque()), true, PaladinFurnitureMod.FURNITURE_GROUP), true);
                PaladinFurnitureMod.furnitureEntryMap.get(KitchenSinkBlock.class).addBlock(variant, LateBlockRegistry.registerLateBlock(variant.asString()+"_kitchen_sink", () -> new KitchenSinkBlock(AbstractBlock.Settings.copy(variant.getBaseBlock()).mapColor(color).nonOpaque(), Biome.Precipitation.RAIN, SinkBehavior.WATER_SINK_BEHAVIOR), true, PaladinFurnitureMod.FURNITURE_GROUP), true);
                PaladinFurnitureMod.furnitureEntryMap.get(KitchenCounterOvenBlock.class).addBlock(variant, LateBlockRegistry.registerLateBlock(variant.asString()+"_kitchen_counter_oven", () -> new KitchenCounterOvenBlock(AbstractBlock.Settings.copy(variant.getBaseBlock()).mapColor(color).nonOpaque()), true, PaladinFurnitureMod.FURNITURE_GROUP), true);
                PaladinFurnitureMod.furnitureEntryMap.get(KitchenWallCounterBlock.class).addBlock(variant, LateBlockRegistry.registerLateBlock(variant.asString()+"_kitchen_wall_counter", () -> new KitchenWallCounterBlock(AbstractBlock.Settings.copy(variant.getBaseBlock()).mapColor(color).nonOpaque()), true, PaladinFurnitureMod.FURNITURE_GROUP), true);
                PaladinFurnitureMod.furnitureEntryMap.get(KitchenWallDrawerBlock.class).addBlock(variant, LateBlockRegistry.registerLateBlock(variant.asString()+"_kitchen_wall_drawer", () -> new KitchenWallDrawerBlock(AbstractBlock.Settings.copy(variant.getBaseBlock()).mapColor(color).nonOpaque()), true, PaladinFurnitureMod.FURNITURE_GROUP), true);
                PaladinFurnitureMod.furnitureEntryMap.get(KitchenWallDrawerSmallBlock.class).addBlock(variant, LateBlockRegistry.registerLateBlock(variant.asString()+"_kitchen_wall_small_drawer", () -> new KitchenWallDrawerSmallBlock(AbstractBlock.Settings.copy(variant.getBaseBlock()).mapColor(color).nonOpaque()), true, PaladinFurnitureMod.FURNITURE_GROUP), true);
                if (variant.hasStripped()) {
                    this.addBlock(variant, LateBlockRegistry.registerLateBlock("stripped_" + variant.asString()+"_kitchen_counter", () -> new KitchenCounterBlock(AbstractBlock.Settings.copy(variant.getBaseBlock()).mapColor(color).nonOpaque()), true, PaladinFurnitureMod.FURNITURE_GROUP), false);
                    PaladinFurnitureMod.furnitureEntryMap.get(KitchenDrawerBlock.class).addBlock(variant, LateBlockRegistry.registerLateBlock("stripped_" + variant.asString()+"_kitchen_drawer", () -> new KitchenDrawerBlock(AbstractBlock.Settings.copy(variant.getBaseBlock()).mapColor(color).nonOpaque()), true, PaladinFurnitureMod.FURNITURE_GROUP), false);
                    PaladinFurnitureMod.furnitureEntryMap.get(KitchenCabinetBlock.class).addBlock(variant, LateBlockRegistry.registerLateBlock("stripped_" + variant.asString()+"_kitchen_cabinet", () -> new KitchenCabinetBlock(AbstractBlock.Settings.copy(variant.getBaseBlock()).mapColor(color).nonOpaque()), true, PaladinFurnitureMod.FURNITURE_GROUP), false);
                    PaladinFurnitureMod.furnitureEntryMap.get(KitchenSinkBlock.class).addBlock(variant, LateBlockRegistry.registerLateBlock("stripped_" + variant.asString()+"_kitchen_sink", () -> new KitchenSinkBlock(AbstractBlock.Settings.copy(variant.getBaseBlock()).mapColor(color).nonOpaque(), Biome.Precipitation.RAIN, SinkBehavior.WATER_SINK_BEHAVIOR), true, PaladinFurnitureMod.FURNITURE_GROUP), false);
                    PaladinFurnitureMod.furnitureEntryMap.get(KitchenCounterOvenBlock.class).addBlock(variant, LateBlockRegistry.registerLateBlock("stripped_" + variant.asString()+"_kitchen_counter_oven", () -> new KitchenCounterOvenBlock(AbstractBlock.Settings.copy(variant.getBaseBlock()).mapColor(color).nonOpaque()), true, PaladinFurnitureMod.FURNITURE_GROUP), false);
                    PaladinFurnitureMod.furnitureEntryMap.get(KitchenWallCounterBlock.class).addBlock(variant, LateBlockRegistry.registerLateBlock("stripped_" + variant.asString()+"_kitchen_wall_counter", () -> new KitchenWallCounterBlock(AbstractBlock.Settings.copy(variant.getBaseBlock()).mapColor(color).nonOpaque()), true, PaladinFurnitureMod.FURNITURE_GROUP), false);
                    PaladinFurnitureMod.furnitureEntryMap.get(KitchenWallDrawerBlock.class).addBlock(variant, LateBlockRegistry.registerLateBlock("stripped_" + variant.asString()+"_kitchen_wall_drawer", () -> new KitchenWallDrawerBlock(AbstractBlock.Settings.copy(variant.getBaseBlock()).mapColor(color).nonOpaque()), true, PaladinFurnitureMod.FURNITURE_GROUP), false);
                    PaladinFurnitureMod.furnitureEntryMap.get(KitchenWallDrawerSmallBlock.class).addBlock(variant, LateBlockRegistry.registerLateBlock("stripped_" + variant.asString()+"_kitchen_wall_small_drawer", () -> new KitchenWallDrawerSmallBlock(AbstractBlock.Settings.copy(variant.getBaseBlock()).mapColor(color).nonOpaque()), true, PaladinFurnitureMod.FURNITURE_GROUP), false);
                }
            }
            for (StoneVariant variant : StoneVariantRegistry.getVariants()) {if (variant.identifier.getPath().equals("quartz"))
                    continue;
                MapColor color = variant.getBaseBlock().getDefaultMapColor();
                this.addBlock(variant, LateBlockRegistry.registerLateBlock( variant.asString()+"_kitchen_counter", () -> new KitchenCounterBlock(AbstractBlock.Settings.copy(variant.getBaseBlock()).mapColor(color).nonOpaque()), true, PaladinFurnitureMod.FURNITURE_GROUP), true);
                PaladinFurnitureMod.furnitureEntryMap.get(KitchenDrawerBlock.class).addBlock(variant, LateBlockRegistry.registerLateBlock( variant.asString()+"_kitchen_drawer", () -> new KitchenDrawerBlock(AbstractBlock.Settings.copy(variant.getBaseBlock()).mapColor(color).nonOpaque()), true, PaladinFurnitureMod.FURNITURE_GROUP), true);
                PaladinFurnitureMod.furnitureEntryMap.get(KitchenCabinetBlock.class).addBlock(variant, LateBlockRegistry.registerLateBlock( variant.asString()+"_kitchen_cabinet", () -> new KitchenCabinetBlock(AbstractBlock.Settings.copy(variant.getBaseBlock()).mapColor(color).nonOpaque()), true, PaladinFurnitureMod.FURNITURE_GROUP), true);
                PaladinFurnitureMod.furnitureEntryMap.get(KitchenSinkBlock.class).addBlock(variant, LateBlockRegistry.registerLateBlock(variant.asString()+"_kitchen_sink", () -> new KitchenSinkBlock(AbstractBlock.Settings.copy(variant.getBaseBlock()).mapColor(color).nonOpaque(), Biome.Precipitation.RAIN, SinkBehavior.WATER_SINK_BEHAVIOR), true, PaladinFurnitureMod.FURNITURE_GROUP), true);
                PaladinFurnitureMod.furnitureEntryMap.get(KitchenCounterOvenBlock.class).addBlock(variant, LateBlockRegistry.registerLateBlock(variant.asString()+"_kitchen_counter_oven", () -> new KitchenCounterOvenBlock(AbstractBlock.Settings.copy(variant.getBaseBlock()).mapColor(color).nonOpaque()), true, PaladinFurnitureMod.FURNITURE_GROUP), true);
                PaladinFurnitureMod.furnitureEntryMap.get(KitchenWallCounterBlock.class).addBlock(variant, LateBlockRegistry.registerLateBlock(variant.asString()+"_kitchen_wall_counter", () -> new KitchenWallCounterBlock(AbstractBlock.Settings.copy(variant.getBaseBlock()).mapColor(color).nonOpaque()), true, PaladinFurnitureMod.FURNITURE_GROUP), true);
                PaladinFurnitureMod.furnitureEntryMap.get(KitchenWallDrawerSmallBlock.class).addBlock(variant, LateBlockRegistry.registerLateBlock(variant.asString()+"_kitchen_wall_small_drawer", () -> new KitchenWallDrawerSmallBlock(AbstractBlock.Settings.copy(variant.getBaseBlock()).mapColor(color).nonOpaque()), true, PaladinFurnitureMod.FURNITURE_GROUP), true);
                PaladinFurnitureMod.furnitureEntryMap.get(KitchenWallDrawerBlock.class).addBlock(variant, LateBlockRegistry.registerLateBlock(variant.asString()+"_kitchen_wall_drawer", () -> new KitchenWallDrawerBlock(AbstractBlock.Settings.copy(variant.getBaseBlock()).mapColor(color).nonOpaque()), true, PaladinFurnitureMod.FURNITURE_GROUP), true);
            }
            for (ExtraCounterVariant variant : ExtraCounterVariant.values()) {
                MapColor color = variant.getBaseBlock().getDefaultMapColor();
                this.addBlock(variant, LateBlockRegistry.registerLateBlock( variant.asString()+"_kitchen_counter", () -> new KitchenCounterBlock(AbstractBlock.Settings.copy(variant.getBaseBlock()).mapColor(color).nonOpaque()), true, PaladinFurnitureMod.FURNITURE_GROUP), true);
                PaladinFurnitureMod.furnitureEntryMap.get(KitchenDrawerBlock.class).addBlock(variant, LateBlockRegistry.registerLateBlock( variant.asString()+"_kitchen_drawer", () -> new KitchenDrawerBlock(AbstractBlock.Settings.copy(variant.getBaseBlock()).mapColor(color).nonOpaque()), true, PaladinFurnitureMod.FURNITURE_GROUP), true);
                PaladinFurnitureMod.furnitureEntryMap.get(KitchenCabinetBlock.class).addBlock(variant, LateBlockRegistry.registerLateBlock( variant.asString()+"_kitchen_cabinet", () -> new KitchenCabinetBlock(AbstractBlock.Settings.copy(variant.getBaseBlock()).mapColor(color).nonOpaque()), true, PaladinFurnitureMod.FURNITURE_GROUP), true);
                PaladinFurnitureMod.furnitureEntryMap.get(KitchenSinkBlock.class).addBlock(variant, LateBlockRegistry.registerLateBlock(variant.asString()+"_kitchen_sink", () -> new KitchenSinkBlock(AbstractBlock.Settings.copy(variant.getBaseBlock()).mapColor(color).nonOpaque(), Biome.Precipitation.RAIN, SinkBehavior.WATER_SINK_BEHAVIOR), true, PaladinFurnitureMod.FURNITURE_GROUP), true);
                PaladinFurnitureMod.furnitureEntryMap.get(KitchenCounterOvenBlock.class).addBlock(variant, LateBlockRegistry.registerLateBlock(variant.asString()+"_kitchen_counter_oven", () -> new KitchenCounterOvenBlock(AbstractBlock.Settings.copy(variant.getBaseBlock()).mapColor(color).nonOpaque()), true, PaladinFurnitureMod.FURNITURE_GROUP), true);
                PaladinFurnitureMod.furnitureEntryMap.get(KitchenWallCounterBlock.class).addBlock(variant, LateBlockRegistry.registerLateBlock(variant.asString()+"_kitchen_wall_counter", () -> new KitchenWallCounterBlock(AbstractBlock.Settings.copy(variant.getBaseBlock()).mapColor(color).nonOpaque()), true, PaladinFurnitureMod.FURNITURE_GROUP), true);
                PaladinFurnitureMod.furnitureEntryMap.get(KitchenWallDrawerBlock.class).addBlock(variant, LateBlockRegistry.registerLateBlock(variant.asString()+"_kitchen_wall_drawer", () -> new KitchenWallDrawerBlock(AbstractBlock.Settings.copy(variant.getBaseBlock()).mapColor(color).nonOpaque()), true, PaladinFurnitureMod.FURNITURE_GROUP), true);
                PaladinFurnitureMod.furnitureEntryMap.get(KitchenWallDrawerSmallBlock.class).addBlock(variant, LateBlockRegistry.registerLateBlock(variant.asString()+"_kitchen_wall_small_drawer", () -> new KitchenWallDrawerSmallBlock(AbstractBlock.Settings.copy(variant.getBaseBlock()).mapColor(color).nonOpaque()), true, PaladinFurnitureMod.FURNITURE_GROUP), true);
            }}});
        PaladinFurnitureMod.furnitureEntryMap.put(FreezerBlock.class, new FurnitureEntry<FreezerBlock>() {{
            this.addBlock(LateBlockRegistry.registerLateBlock( "white_freezer", () -> PaladinFurnitureModBlocksItems.WHITE_FREEZER, true, PaladinFurnitureMod.FURNITURE_GROUP));
            this.addBlock(LateBlockRegistry.registerLateBlock( "gray_freezer", () -> PaladinFurnitureModBlocksItems.GRAY_FREEZER, true, PaladinFurnitureMod.FURNITURE_GROUP));
            this.addBlock(LateBlockRegistry.registerLateBlock( "iron_freezer", () -> PaladinFurnitureModBlocksItems.IRON_FREEZER, true, PaladinFurnitureMod.FURNITURE_GROUP));
        }});

        PaladinFurnitureMod.furnitureEntryMap.put(FridgeBlock.class, new FurnitureEntry<FridgeBlock>() {{
            this.addBlock(LateBlockRegistry.registerLateBlock( "white_fridge", () -> PaladinFurnitureModBlocksItems.WHITE_FRIDGE, true, PaladinFurnitureMod.FURNITURE_GROUP));
            this.addBlock(LateBlockRegistry.registerLateBlock( "gray_fridge", () -> PaladinFurnitureModBlocksItems.GRAY_FRIDGE, true, PaladinFurnitureMod.FURNITURE_GROUP));
            this.addBlock( LateBlockRegistry.registerLateBlock( "iron_fridge", () -> PaladinFurnitureModBlocksItems.IRON_FRIDGE, true, PaladinFurnitureMod.FURNITURE_GROUP));
            this.addBlock( LateBlockRegistry.registerLateBlock( "xbox_fridge", () -> PaladinFurnitureModBlocksItems.XBOX_FRIDGE, true, PaladinFurnitureMod.FURNITURE_GROUP));
        }});
        PaladinFurnitureMod.furnitureEntryMap.put(MicrowaveBlock.class, new FurnitureEntry<MicrowaveBlock>() {{
            this.addBlock(LateBlockRegistry.registerLateBlock( "iron_microwave", () -> PaladinFurnitureModBlocksItems.IRON_MICROWAVE, true, PaladinFurnitureMod.FURNITURE_GROUP));
        }});

        PaladinFurnitureMod.furnitureEntryMap.put(KitchenRangeHoodBlock.class, new FurnitureEntry<KitchenRangeHoodBlock>() {{
            this.addBlock( LateBlockRegistry.registerLateBlock( "white_oven_range_hood", () -> PaladinFurnitureModBlocksItems.WHITE_OVEN_RANGEHOOD, true, PaladinFurnitureMod.FURNITURE_GROUP));
            this.addBlock( LateBlockRegistry.registerLateBlock( "gray_oven_range_hood", () -> PaladinFurnitureModBlocksItems.GRAY_OVEN_RANGEHOOD, true, PaladinFurnitureMod.FURNITURE_GROUP));
            this.addBlock( LateBlockRegistry.registerLateBlock( "iron_oven_range_hood", () -> PaladinFurnitureModBlocksItems.IRON_OVEN_RANGEHOOD, true, PaladinFurnitureMod.FURNITURE_GROUP));
        }});
        PaladinFurnitureMod.furnitureEntryMap.put(StoveBlock.class, new FurnitureEntry<StoveBlock>() {{
            this.addBlock( LateBlockRegistry.registerLateBlock( "white_stove", () -> PaladinFurnitureModBlocksItems.WHITE_STOVE, true, PaladinFurnitureMod.FURNITURE_GROUP));
            this.addBlock( LateBlockRegistry.registerLateBlock( "gray_stove", () -> PaladinFurnitureModBlocksItems.GRAY_STOVE, true, PaladinFurnitureMod.FURNITURE_GROUP));
            this.addBlock( LateBlockRegistry.registerLateBlock( "iron_stove",() -> PaladinFurnitureModBlocksItems.IRON_STOVE, true, PaladinFurnitureMod.FURNITURE_GROUP));
        }});
        PaladinFurnitureMod.furnitureEntryMap.put(KitchenStovetopBlock.class, new FurnitureEntry<KitchenStovetopBlock>() {{
            this.addBlock( LateBlockRegistry.registerLateBlock( "kitchen_stovetop",() -> PaladinFurnitureModBlocksItems.KITCHEN_STOVETOP, true, PaladinFurnitureMod.FURNITURE_GROUP));
        }});
        PaladinFurnitureMod.furnitureEntryMap.put(PFMToasterBlock.class, new FurnitureEntry<>() {{
            addBlock(registerLateBlockClassic("iron_toaster", PaladinFurnitureModBlocksItems.TOASTER_BLOCK, true, PaladinFurnitureMod.FURNITURE_GROUP));
        }});
        PaladinFurnitureMod.furnitureEntryMap.put(PlateBlock.class, new FurnitureEntry<PlateBlock>() {{
            this.addBlock(LateBlockRegistry.registerLateBlock( "basic_plate",() -> PaladinFurnitureModBlocksItems.BASIC_PLATE, true, PaladinFurnitureMod.FURNITURE_GROUP));
        }});
        PaladinFurnitureMod.furnitureEntryMap.put(CutleryBlock.class, new FurnitureEntry<CutleryBlock>() {{
            this.addBlock( LateBlockRegistry.registerLateBlock( "basic_cutlery",() -> PaladinFurnitureModBlocksItems.BASIC_CUTLERY, true, PaladinFurnitureMod.FURNITURE_GROUP));
        }});
        PaladinFurnitureMod.furnitureEntryMap.put(PendantBlock.class, new FurnitureEntry<PendantBlock>() {{
            this.addBlock( LateBlockRegistry.registerLateBlock( "gray_modern_pendant",() -> PaladinFurnitureModBlocksItems.GRAY_MODERN_PENDANT, true, PaladinFurnitureMod.FURNITURE_GROUP));
            this.addBlock( LateBlockRegistry.registerLateBlock( "white_modern_pendant",() -> PaladinFurnitureModBlocksItems.WHITE_MODERN_PENDANT, true, PaladinFurnitureMod.FURNITURE_GROUP));
            this.addBlock( LateBlockRegistry.registerLateBlock( "glass_modern_pendant",() -> PaladinFurnitureModBlocksItems.GLASS_MODERN_PENDANT, true, PaladinFurnitureMod.FURNITURE_GROUP));
        }});
        PaladinFurnitureMod.furnitureEntryMap.put(SimpleLightBlock.class, new FurnitureEntry<SimpleLightBlock>() {{
            this.addBlock( LateBlockRegistry.registerLateBlock( "simple_light",() -> PaladinFurnitureModBlocksItems.SIMPLE_LIGHT, true, PaladinFurnitureMod.FURNITURE_GROUP));
        }});
        PaladinFurnitureModBlocksItems.LIGHT_SWITCH_ITEM = new LightSwitchItem(PaladinFurnitureModBlocksItems.LIGHT_SWITCH, new Item.Settings());
        PaladinFurnitureMod.furnitureEntryMap.put(LightSwitchBlock.class, new FurnitureEntry<LightSwitchBlock>() {{
            this.addBlock( LateBlockRegistry.registerLateBlock( "light_switch",() -> PaladinFurnitureModBlocksItems.LIGHT_SWITCH, false, PaladinFurnitureMod.FURNITURE_GROUP));
            PaladinFurnitureModBlocksItems.BLOCKS.add(PaladinFurnitureModBlocksItems.LIGHT_SWITCH);
            LateBlockRegistry.registerLateItem("light_switch",() -> PaladinFurnitureModBlocksItems.LIGHT_SWITCH_ITEM, PaladinFurnitureMod.FURNITURE_GROUP);
        }});
        PaladinFurnitureMod.furnitureEntryMap.put(BasicToiletBlock.class, new FurnitureEntry<BasicToiletBlock>() {{
            this.addBlock( LateBlockRegistry.registerLateBlock( "basic_toilet",() -> PaladinFurnitureModBlocksItems.BASIC_TOILET, true, PaladinFurnitureMod.FURNITURE_GROUP));
        }});
        PaladinFurnitureMod.furnitureEntryMap.put(WallToiletPaperBlock.class, new FurnitureEntry<WallToiletPaperBlock>() {{
            this.addBlock( LateBlockRegistry.registerLateBlock( "wall_toilet_paper",() -> PaladinFurnitureModBlocksItems.WALL_TOILET_PAPER, true, PaladinFurnitureMod.FURNITURE_GROUP));
        }});
        PaladinFurnitureMod.furnitureEntryMap.put(BasicSinkBlock.class, new FurnitureEntry<BasicSinkBlock>() {{
            this.addBlock(LateBlockRegistry.registerLateBlock( "basic_sink",() -> PaladinFurnitureModBlocksItems.BASIC_SINK, true, PaladinFurnitureMod.FURNITURE_GROUP));
        }});
        PaladinFurnitureMod.furnitureEntryMap.put(BasicBathtubBlock.class, new FurnitureEntry<BasicBathtubBlock>() {{
            this.addBlock( LateBlockRegistry.registerLateBlock( "basic_bathtub",() -> PaladinFurnitureModBlocksItems.BASIC_BATHTUB, true, PaladinFurnitureMod.FURNITURE_GROUP));
        }});
        PaladinFurnitureMod.furnitureEntryMap.put(BasicShowerHeadBlock.class, new FurnitureEntry<BasicShowerHeadBlock>() {{
            this.addBlock( LateBlockRegistry.registerLateBlock( "basic_shower_head",() -> PaladinFurnitureModBlocksItems.BASIC_SHOWER_HEAD, true, PaladinFurnitureMod.FURNITURE_GROUP));
        }});
        PaladinFurnitureModBlocksItems.BASIC_SHOWER_HANDLE_ITEM = new ShowerHandleItem(() -> PaladinFurnitureModBlocksItems.BASIC_SHOWER_HANDLE, new Item.Settings());
        PaladinFurnitureMod.furnitureEntryMap.put(BasicShowerHandleBlock.class, new FurnitureEntry<BasicShowerHandleBlock>() {{
            this.addBlock( LateBlockRegistry.registerLateBlock( "basic_shower_handle",() -> PaladinFurnitureModBlocksItems.BASIC_SHOWER_HANDLE, false, PaladinFurnitureMod.FURNITURE_GROUP));
            LateBlockRegistry.registerLateItem( "basic_shower_handle",() -> PaladinFurnitureModBlocksItems.BASIC_SHOWER_HANDLE_ITEM, PaladinFurnitureMod.FURNITURE_GROUP);
        }});
        PaladinFurnitureMod.furnitureEntryMap.put(TrashcanBlock.class, new FurnitureEntry<TrashcanBlock>() {{
            this.addBlock( LateBlockRegistry.registerLateBlock( "trashcan",() -> PaladinFurnitureModBlocksItems.TRASHCAN, true, PaladinFurnitureMod.FURNITURE_GROUP));
        }});
        PaladinFurnitureMod.furnitureEntryMap.put(InnerTrashcanBlock.class, new FurnitureEntry<InnerTrashcanBlock>() {{
            this.addBlock( LateBlockRegistry.registerLateBlock( "mesh_trashcan",() -> PaladinFurnitureModBlocksItems.MESH_TRASHCAN, true, PaladinFurnitureMod.FURNITURE_GROUP));
        }});
        PaladinFurnitureMod.furnitureEntryMap.put(ShowerTowelBlock.class, new FurnitureEntry<ShowerTowelBlock>() {{
            int i = 0;
            for (DyeColor color : DyeColor.values()) {
                if (i > 15)
                    break;
                this.addBlock(registerLateBlock(color.getName() + "_shower_towel", () -> new ShowerTowelBlock(color, AbstractBlock.Settings.create().burnable().strength(2.0f).resistance(2.0f).nonOpaque().sounds(BlockSoundGroup.WOOL).mapColor(color.getMapColor())), true, PaladinFurnitureMod.FURNITURE_GROUP));
                i++;
            }
        }});
        if (!BlockItemRegistry.isModLoaded("immersive_portals") || PaladinFurnitureMod.getLoader() == PaladinFurnitureMod.Loader.FORGE) {
            PaladinFurnitureModBlocksItems.WHITE_MIRROR = new MirrorBlock(AbstractBlock.Settings.create().mapColor(MapColor.WHITE).nonOpaque());
            PaladinFurnitureModBlocksItems.GRAY_MIRROR = new MirrorBlock(AbstractBlock.Settings.create().mapColor(MapColor.GRAY).nonOpaque());
        }
        registerLateBlock("white_mirror",() -> PaladinFurnitureModBlocksItems.WHITE_MIRROR, true, PaladinFurnitureMod.FURNITURE_GROUP);
        registerLateBlock("gray_mirror",() -> PaladinFurnitureModBlocksItems.GRAY_MIRROR, true, PaladinFurnitureMod.FURNITURE_GROUP);
        PaladinFurnitureMod.furnitureEntryMap.put(BasicLampBlock.class, new FurnitureEntry<>() {{
            addBlock(registerLateBlockClassic("basic_lamp", PaladinFurnitureModBlocksItems.BASIC_LAMP, false, PaladinFurnitureMod.FURNITURE_GROUP));
        }});
        PaladinFurnitureModBlocksItems.BASIC_LAMP_ITEM = LampItem.getItemFactory(PaladinFurnitureModBlocksItems.BASIC_LAMP, new Item.Settings());
        LateBlockRegistry.registerLateItem( "basic_lamp", () -> PaladinFurnitureModBlocksItems.BASIC_LAMP_ITEM, PaladinFurnitureMod.FURNITURE_GROUP);
        PaladinFurnitureMod.pfmModCompatibilities.forEach(PFMModCompatibility::registerBlocks);
        PaladinFurnitureMod.pfmModCompatibilities.forEach(PFMModCompatibility::registerItems);
    }
}
