package com.unlikepaladin.pfm.data.fabric;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class PaladinFurnitureModDataEntrypoint implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {

    }
/*
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator dataGenerator) {
        dataGenerator.addProvider(PFMBlockTagProvider::new);
        dataGenerator.addProvider(PFMLootTableProvider::new);
        dataGenerator.addProvider(PFMRecipeProvider::new);
    }

    private static class PFMLootTableProvider extends FabricBlockLootTablesProvider {
        private PFMLootTableProvider(FabricDataGenerator dataGenerator) {
            super(dataGenerator);
        }

        @Override
        protected void generateBlockLootTables() {
            Stream<Block> blocks = PaladinFurnitureModBlocksItems.streamBlocks();
            blocks.forEach(this::addDrop);
            Block[] beds = PaladinFurnitureModBlocksItems.getBeds();
            Arrays.stream(beds).forEach(bed -> this.addDrop(bed, (Block block) -> dropsWithProperty(block, BedBlock.PART, BedPart.HEAD)));
            BasicBathtubBlock.basicBathtubBlockStream().forEach(basicBathtubBlock -> this.addDrop(basicBathtubBlock, (Block block) -> dropsWithProperty(block, BedBlock.PART, BedPart.HEAD)));
        }
    }
    

    public static class PFMBlockTagProvider extends FabricTagProvider.BlockTagProvider {
        private PFMBlockTagProvider(FabricDataGenerator dataGenerator) {
            super(dataGenerator);
        }

        @Override
        protected void generateTags() {
            KitchenCounterBlock[] stoneCounters = KitchenCounterBlock.streamStoneCounters().map(FurnitureBlock::getBlock).toArray(KitchenCounterBlock[]::new);
            KitchenCabinetBlock[] stoneCabinets = KitchenCabinetBlock.streamStoneCabinets().map(FurnitureBlock::getBlock).toArray(KitchenCabinetBlock[]::new);
            KitchenDrawerBlock[] stoneDrawers = KitchenDrawerBlock.streamStoneDrawers().map(FurnitureBlock::getBlock).toArray(KitchenDrawerBlock[]::new);
            KitchenCounterOvenBlock[] stoneCounterOvens = KitchenCounterOvenBlock.streamStoneCounterOvens().map(FurnitureBlock::getBlock).toArray(KitchenCounterOvenBlock[]::new);
            KitchenWallCounterBlock[] stoneWallCounters = KitchenWallCounterBlock.streamWallStoneCounters().map(FurnitureBlock::getBlock).toArray(KitchenWallCounterBlock[]::new);
            KitchenWallDrawerBlock[] stoneWallDrawers = KitchenWallDrawerBlock.streamWallStoneDrawers().map(FurnitureBlock::getBlock).toArray(KitchenWallDrawerBlock[]::new);
            KitchenWallDrawerSmallBlock[] stoneWallSmallDrawers = KitchenWallDrawerSmallBlock.streamStoneWallSmallDrawers().map(FurnitureBlock::getBlock).toArray(KitchenWallDrawerSmallBlock[]::new);

            KitchenSinkBlock[] stoneSinks = KitchenSinkBlock.streamStoneSinks().map(FurnitureBlock::getBlock).toArray(KitchenSinkBlock[]::new);
            BasicChairBlock[] stoneBasicChairs = BasicChairBlock.streamStoneBasicChairs().map(FurnitureBlock::getBlock).toArray(BasicChairBlock[]::new);
            BasicTableBlock[] stoneBasicTables = BasicTableBlock.streamStoneBasicTables().map(FurnitureBlock::getBlock).toArray(BasicTableBlock[]::new);
            ClassicChairBlock[] stoneClassicChairs = ClassicChairBlock.streamWoodClassicChairs().map(FurnitureBlock::getBlock).toArray(ClassicChairBlock[]::new);
            ClassicChairDyeableBlock[] stoneDyeableClassicChairs = ClassicChairDyeableBlock.streamStoneDyeableChair().toList().toArray(new ClassicChairDyeableBlock[0]);
            ClassicStoolBlock[] stoneClassicStools = ClassicStoolBlock.streamStoneClassicStools().map(FurnitureBlock::getBlock).toArray(ClassicStoolBlock[]::new);

            ClassicTableBlock[] stoneClassicTables = ClassicTableBlock.streamStoneClassicTables().map(FurnitureBlock::getBlock).toArray(ClassicTableBlock[]::new);
            DinnerChairBlock[] stoneDinnerChairs = DinnerChairBlock.streamStoneDinnerChairs().map(FurnitureBlock::getBlock).toArray(DinnerChairBlock[]::new);
            DinnerTableBlock[] stoneDinnerTables = DinnerTableBlock.streamStoneDinnerTables().map(FurnitureBlock::getBlock).toArray(DinnerTableBlock[]::new);
            ModernChairBlock[] stoneModernChairs = ModernChairBlock.streamStoneModernChairs().map(FurnitureBlock::getBlock).toArray(ModernChairBlock[]::new);
            ModernStoolBlock[] stoneModernStools = ModernStoolBlock.streamStoneModernStools().map(FurnitureBlock::getBlock).toArray(ModernStoolBlock[]::new);
            ModernDinnerTableBlock[] stoneModernDinnerTables = ModernDinnerTableBlock.streamStoneModernDinnerTables().map(FurnitureBlock::getBlock).toArray(ModernDinnerTableBlock[]::new);
            ClassicNightstandBlock[] stoneClassicNightstands = ClassicNightstandBlock.streamStoneClassicNightstands().map(FurnitureBlock::getBlock).toArray(ClassicNightstandBlock[]::new);
            LogTableBlock[] stoneNaturalTables = LogTableBlock.streamStoneNaturalTables().map(FurnitureBlock::getBlock).toArray(LogTableBlock[]::new);

            SimpleStoolBlock[] stoneSimpleStools = SimpleStoolBlock.streamStoneSimpleStools().map(FurnitureBlock::getBlock).toArray(SimpleStoolBlock[]::new);
            PendantBlock[] pendantLights = PendantBlock.streamPendantLights().toList().toArray(new PendantBlock[0]);
            SimpleLightBlock[] simpleLights = SimpleLightBlock.streamSimpleLights().toList().toArray(new SimpleLightBlock[0]);
            FridgeBlock[] fridges = FridgeBlock.streamFridges().map(FurnitureBlock::getBlock).toArray(FridgeBlock[]::new);
            FreezerBlock[] freezers = FreezerBlock.streamFreezers().map(FurnitureBlock::getBlock).toArray(FreezerBlock[]::new);
            LightSwitchBlock[] lightSwitches = LightSwitchBlock.streamlightSwitches().toList().toArray(new LightSwitchBlock[0]);
            MicrowaveBlock[] microwaves = MicrowaveBlock.streamMicrowaves().map(FurnitureBlock::getBlock).toArray(MicrowaveBlock[]::new);
            KitchenStovetopBlock[] kitchenStovetops = KitchenStovetopBlock.streamKitchenStovetop().toList().toArray(new KitchenStovetopBlock[0]);
            IronStoveBlock[] ironStoves = IronStoveBlock.streamIronStoves().map(FurnitureBlock::getBlock).toArray(IronStoveBlock[]::new);
            FroggyChairBlock[] froggyChairs = FroggyChairBlock.streamFroggyChair().map(FurnitureBlock::getBlock).toArray(FroggyChairBlock[]::new);
            StoveBlock[] stove = StoveBlock.streamStoves().map(FurnitureBlock::getBlock).toArray(StoveBlock[]::new);
            SimpleBedBlock[] simpleBeds = SimpleBedBlock.streamSimpleBeds().map(FurnitureBlock::getBlock).toArray(SimpleBedBlock[]::new);
            ClassicBedBlock[] classicBeds = ClassicBedBlock.streamClassicBeds().map(FurnitureBlock::getBlock).toArray(ClassicBedBlock[]::new);
            PlateBlock[] plates = PlateBlock.streamPlates().map(FurnitureBlock::getBlock).toArray(PlateBlock[]::new);
            CutleryBlock[] cutleries = CutleryBlock.streamCutlery().map(FurnitureBlock::getBlock).toArray(CutleryBlock[]::new);
            BasicToiletBlock[] basicToilets = BasicToiletBlock.streamBasicToilet().map(FurnitureBlock::getBlock).toArray(BasicToiletBlock[]::new);
            KitchenRangeHoodBlock[] rangeHoods = KitchenRangeHoodBlock.streamOvenRangeHoods().map(FurnitureBlock::getBlock).toArray(KitchenRangeHoodBlock[]::new);
            BasicSinkBlock[] sinkBlocks = BasicSinkBlock.streamSinks().toList().toArray(new BasicSinkBlock[0]);
            ShowerTowelBlock[] showerTowels = ShowerTowelBlock.streamShowerTowels().map(FurnitureBlock::getBlock).toArray(ShowerTowelBlock[]::new);

            this.getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE)
                    .add(showerTowels)
                    .add(stoneCounters)
                    .add(stoneCabinets)
                    .add(stoneDrawers)
                    .add(stoneCounterOvens)
                    .add(stoneSinks)
                    .add(stoneBasicChairs)
                    .add(stoneBasicTables)
                    .add(stoneClassicChairs)
                    .add(stoneDyeableClassicChairs)
                    .add(stoneClassicStools)
                    .add(stoneClassicTables)
                    .add(stoneDinnerChairs)
                    .add(stoneDinnerTables)
                    .add(stoneModernDinnerTables)
                    .add(stoneModernChairs)
                    .add(stoneModernStools)
                    .add(stoneSimpleStools)
                    .add(pendantLights)
                    .add(simpleLights)
                    .add(fridges)
                    .add(freezers)
                    .add(lightSwitches)
                    .add(microwaves)
                    .add(kitchenStovetops)
                    .add(ironStoves)
                    .add(froggyChairs)
                    .add(stove)
                    .add(stoneWallCounters)
                    .add(stoneWallDrawers)
                    .add(stoneWallSmallDrawers)
                    .add(stoneNaturalTables)
                    .add(stoneClassicNightstands)
                    .add(plates)
                    .add(cutleries)
                    .add(basicToilets)
                    .add(rangeHoods)
                    .add(PaladinFurnitureModBlocksItems.RAW_CONCRETE)
                    .add(PaladinFurnitureModBlocksItems.IRON_CHAIN)
                    .add(sinkBlocks)
                    .add(PaladinFurnitureModBlocksItems.BASIC_SHOWER_HANDLE)
                    .add(PaladinFurnitureModBlocksItems.BASIC_SHOWER_HEAD);

            KitchenCounterBlock[] woodCounters = KitchenCounterBlock.streamWoodCounters().map(FurnitureBlock::getBlock).toArray(KitchenCounterBlock[]::new);
            KitchenWallCounterBlock[] woodWallCounters = KitchenWallCounterBlock.streamWallWoodCounters().map(FurnitureBlock::getBlock).toArray(KitchenWallCounterBlock[]::new);
            KitchenWallDrawerBlock[] woodWallDrawers = KitchenWallDrawerBlock.streamWallWoodDrawers().map(FurnitureBlock::getBlock).toArray(KitchenWallDrawerBlock[]::new);
            KitchenCabinetBlock[] woodCabinets = KitchenCabinetBlock.streamWoodCabinets().map(FurnitureBlock::getBlock).toArray(KitchenCabinetBlock[]::new);
            KitchenDrawerBlock[] woodDrawers = KitchenDrawerBlock.streamWoodDrawers().map(FurnitureBlock::getBlock).toArray(KitchenDrawerBlock[]::new);
            KitchenWallDrawerSmallBlock[] woodWallSmallDrawers = KitchenWallDrawerSmallBlock.streamWoodWallSmallDrawers().map(FurnitureBlock::getBlock).toArray(KitchenWallDrawerSmallBlock[]::new);
            KitchenCounterOvenBlock[] woodCounterOvens = KitchenCounterOvenBlock.streamWoodCounterOvens().map(FurnitureBlock::getBlock).toArray(KitchenCounterOvenBlock[]::new);

            KitchenSinkBlock[] woodSinks = KitchenSinkBlock.streamWoodSinks().map(FurnitureBlock::getBlock).toArray(KitchenSinkBlock[]::new);

            BasicChairBlock[] woodBasicChairs = BasicChairBlock.streamWoodBasicChairs().map(FurnitureBlock::getBlock).toArray(BasicChairBlock[]::new);

            BasicTableBlock[] woodBasicTables = BasicTableBlock.streamWoodBasicTables().map(FurnitureBlock::getBlock).toArray(BasicTableBlock[]::new);
            ClassicChairBlock[] woodClassicChairs = ClassicChairBlock.streamWoodClassicChairs().map(FurnitureBlock::getBlock).toArray(ClassicChairBlock[]::new);
            ClassicChairDyeableBlock[] woodDyeableClassicChairs = ClassicChairDyeableBlock.streamWoodDyeableChair().map(FurnitureBlock::getBlock).toArray(ClassicChairDyeableBlock[]::new);
            ClassicStoolBlock[] woodClassicStools = ClassicStoolBlock.streamWoodClassicStools().map(FurnitureBlock::getBlock).toArray(ClassicStoolBlock[]::new);
            ClassicTableBlock[] woodClassicTables = ClassicTableBlock.streamWoodClassicTables().map(FurnitureBlock::getBlock).toArray(ClassicTableBlock[]::new);
            DinnerChairBlock[] woodDinnerChairs = DinnerChairBlock.streamWoodDinnerChairs().map(FurnitureBlock::getBlock).toArray(DinnerChairBlock[]::new);
            DinnerTableBlock[] woodDinnerTables = DinnerTableBlock.streamWoodDinnerTables().map(FurnitureBlock::getBlock).toArray(DinnerTableBlock[]::new);
            LogStoolBlock[] woodLogStools = LogStoolBlock.streamWoodLogStools().map(FurnitureBlock::getBlock).toArray(LogStoolBlock[]::new);
            LogTableBlock[] woodLogTables = LogTableBlock.streamWoodLogTables().map(FurnitureBlock::getBlock).toArray(LogTableBlock[]::new);
            ModernChairBlock[] woodModernChairs = ModernChairBlock.streamWoodModernChairs().map(FurnitureBlock::getBlock).toArray(ModernChairBlock[]::new);
            ModernDinnerTableBlock[] woodModernDinnerTables = ModernDinnerTableBlock.streamWoodModernDinnerTables().map(FurnitureBlock::getBlock).toArray(ModernDinnerTableBlock[]::new);

            ClassicNightstandBlock[] woodClassicNightstands = ClassicNightstandBlock.streamWoodClassicNightstands().map(FurnitureBlock::getBlock).toArray(ClassicNightstandBlock[]::new);
            ModernStoolBlock[] woodModernStools = ModernStoolBlock.streamWoodModernStools().map(FurnitureBlock::getBlock).toArray(ModernStoolBlock[]::new);
            SimpleStoolBlock[] woodSimpleStools = SimpleStoolBlock.streamWoodSimpleStools().map(FurnitureBlock::getBlock).toArray(SimpleStoolBlock[]::new);
            SimpleSofaBlock[] simpleSofas = SimpleSofaBlock.streamSimpleSofas().map(FurnitureBlock::getBlock).toArray(SimpleSofaBlock[]::new);
            ArmChairColoredBlock[] armChairDyeables = ArmChairColoredBlock.streamArmChairColored().map(FurnitureBlock::getBlock).toArray(ArmChairColoredBlock[]::new);
            ArmChairBlock[] armChairs = ArmChairBlock.streamArmChairs().map(FurnitureBlock::getBlock).toArray(ArmChairBlock[]::new);
            WorkingTableBlock[] workingTables = WorkingTableBlock.streamWorkingTables().toList().toArray(new WorkingTableBlock[0]);
            HerringbonePlankBlock[] herringbonePlanks = HerringbonePlankBlock.streamPlanks().map(FurnitureBlock::getBlock).toArray(HerringbonePlankBlock[]::new);
            SimpleBunkLadderBlock[] simpleBunkLadders = SimpleBunkLadderBlock.streamSimpleBunkLadder().map(FurnitureBlock::getBlock).toArray(SimpleBunkLadderBlock[]::new);

            this.getOrCreateTagBuilder(BlockTags.AXE_MINEABLE)
                    .add(showerTowels)
                    .add(woodCounters)
                    .add(woodCabinets)
                    .add(woodDrawers)
                    .add(woodCounterOvens)
                    .add(woodSinks)
                    .add(woodBasicChairs)
                    .add(woodBasicTables)
                    .add(woodClassicChairs)
                    .add(woodDyeableClassicChairs)
                    .add(woodClassicStools)
                    .add(woodClassicTables)
                    .add(woodDinnerChairs)
                    .add(woodDinnerTables)
                    .add(woodLogStools)
                    .add(woodLogTables)
                    .add(woodModernDinnerTables)
                    .add(woodModernChairs)
                    .add(woodModernStools)
                    .add(woodSimpleStools)
                    .add(simpleSofas)
                    .add(armChairDyeables)
                    .add(armChairs)
                    .add(woodClassicNightstands)
                    .add(workingTables)
                    .add(herringbonePlanks)
                    .add(simpleBeds)
                    .add(woodWallDrawers)
                    .add(woodWallCounters)
                    .add(woodWallSmallDrawers)
                    .add(simpleBunkLadders)
                    .add(classicBeds);

                this.getOrCreateTagBuilder(BlockTags.SHOVEL_MINEABLE)
                    .add(PaladinFurnitureModBlocksItems.RAW_CONCRETE_POWDER);

                this.getOrCreateTagBuilder(BlockTags.BEDS)
                    .add(simpleBeds)
                    .add(classicBeds);

                this.getOrCreateTagBuilder(BlockTags.CLIMBABLE)
                        .add(simpleBunkLadders);

            this.getOrCreateTagBuilder(PFMTags.getTuckableBlocks())
                    .add(woodBasicTables)
                    .add(stoneBasicTables)
                    .add(woodClassicTables)
                    .add(stoneClassicTables)
                    .add(woodDinnerTables)
                    .add(stoneDinnerTables)
                    .add(woodModernDinnerTables)
                    .add(stoneModernDinnerTables)
                    .add(woodLogTables)
                    .add(stoneNaturalTables);
        }

    }

    private static class PFMRecipeProvider extends FabricRecipesProvider {
        private PFMRecipeProvider(FabricDataGenerator dataGenerator) {
            super(dataGenerator);
        }

        @Override
        protected void generateRecipes(Consumer<RecipeJsonProvider> exporter) {
            FurnitureBlock[] woodClassicChairs = ClassicChairBlock.streamWoodClassicChairs().toList().toArray(new FurnitureBlock[0]);
            for (FurnitureBlock classicChair : woodClassicChairs) {
                offerClassicChairRecipe(classicChair.getBlock(), Ingredient.ofItems(classicChair.getSecondaryMaterial().asItem()), Ingredient.ofItems(classicChair.getBaseMaterial().asItem()), exporter);
            }

            FurnitureBlock[] stoneClassicChairs = ClassicChairBlock.streamStoneClassicChairs().toList().toArray(new FurnitureBlock[0]);
            for (FurnitureBlock classicChair : stoneClassicChairs) {
                offerClassicChairRecipe(classicChair.getBlock(), Ingredient.ofItems(classicChair.getSecondaryStoneMaterial().asItem()), Ingredient.ofItems(classicChair.getBaseStoneMaterial().asItem()), exporter);
            }

            FurnitureBlock[] woodBasicChairs = BasicChairBlock.streamWoodBasicChairs().toList().toArray(new FurnitureBlock[0]);
            for (FurnitureBlock basicChair : woodBasicChairs) {
                offerBasicChairRecipe(basicChair.getBlock(), Ingredient.ofItems(basicChair.getSecondaryMaterial().asItem()), Ingredient.ofItems(basicChair.getBaseMaterial().asItem()), exporter);
            }

            FurnitureBlock[] stoneBasicChairs = BasicChairBlock.streamStoneBasicChairs().toList().toArray(new FurnitureBlock[0]);
            for (FurnitureBlock basicChair : stoneBasicChairs) {
                offerBasicChairRecipe(basicChair.getBlock(), Ingredient.ofItems(basicChair.getSecondaryStoneMaterial().asItem()), Ingredient.ofItems(basicChair.getBaseStoneMaterial().asItem()), exporter);
            }

            FurnitureBlock[] woodDinnerChairs = DinnerChairBlock.streamWoodDinnerChairs().toList().toArray(new FurnitureBlock[0]);
            for (FurnitureBlock dinnerChair : woodDinnerChairs) {
                offerDinnerChairRecipe(dinnerChair.getBlock(), Ingredient.ofItems(dinnerChair.getSecondaryMaterial().asItem()), Ingredient.ofItems(dinnerChair.getBaseMaterial().asItem()), exporter);
            }

            FurnitureBlock[] stoneDinnerChairs = DinnerChairBlock.streamStoneDinnerChairs().toList().toArray(new FurnitureBlock[0]);
            for (FurnitureBlock dinnerChair : stoneDinnerChairs) {
                offerDinnerChairRecipe(dinnerChair.getBlock(), Ingredient.ofItems(dinnerChair.getSecondaryStoneMaterial().asItem()), Ingredient.ofItems(dinnerChair.getBaseStoneMaterial().asItem()), exporter);
            }

            FurnitureBlock[] woodModernChairs = ModernChairBlock.streamWoodModernChairs().toList().toArray(new FurnitureBlock[0]);
            for (FurnitureBlock modernChair : woodModernChairs) {
                offerModernChairRecipe(modernChair.getBlock(), Ingredient.ofItems(modernChair.getSecondaryMaterial().asItem()), Ingredient.ofItems(modernChair.getBaseMaterial().asItem()), exporter);
            }

            FurnitureBlock[] stoneModernChairs = ModernChairBlock.streamStoneModernChairs().toList().toArray(new FurnitureBlock[0]);
            for (FurnitureBlock modernChair : stoneModernChairs) {
                offerModernChairRecipe(modernChair.getBlock(), Ingredient.ofItems(modernChair.getSecondaryStoneMaterial().asItem()), Ingredient.ofItems(modernChair.getBaseStoneMaterial().asItem()), exporter);
            }

            FurnitureBlock[] woodClassicStools = ClassicStoolBlock.streamWoodClassicStools().toList().toArray(new FurnitureBlock[0]);
            for (FurnitureBlock classicStool : woodClassicStools) {
                offerClassicStoolRecipe(classicStool.getBlock(), Ingredient.ofItems(classicStool.getSecondaryMaterial().asItem()), Ingredient.ofItems(classicStool.getBaseMaterial().asItem()), exporter);
            }

            FurnitureBlock[] stoneClassicStools = ClassicStoolBlock.streamStoneClassicStools().toList().toArray(new FurnitureBlock[0]);
            for (FurnitureBlock classicStool : stoneClassicStools) {
                offerClassicStoolRecipe(classicStool.getBlock(), Ingredient.ofItems(classicStool.getSecondaryStoneMaterial().asItem()), Ingredient.ofItems(classicStool.getBaseStoneMaterial().asItem()), exporter);
            }

            FurnitureBlock[] herringbonePlanks = HerringbonePlankBlock.streamPlanks().toList().toArray(new FurnitureBlock[0]);
            for (FurnitureBlock herringbonePlank : herringbonePlanks) {
                offerHerringbonePlanks(herringbonePlank.getBlock(), herringbonePlank.getSlab().asItem(), exporter);
            }

            FurnitureBlock[] woodKitchenCabinets = KitchenCabinetBlock.streamWoodCabinets().toList().toArray(new FurnitureBlock[0]);
            for (FurnitureBlock kitchenCabinet : woodKitchenCabinets) {
                String cabinetName = kitchenCabinet.getBlock().toString();
                if (cabinetName.contains("light_wood")) {
                    offerCabinetRecipe(kitchenCabinet.getBlock(), Ingredient.ofItems(Registry.BLOCK.get(new Identifier("minecraft:" + "smooth_quartz"))), Ingredient.ofItems(Registry.BLOCK.get(new Identifier("minecraft:" + "stripped_oak_log"))), Ingredient.ofItems(Items.CHEST), exporter);
                } else if (cabinetName.contains("dark_wood")) {
                    offerCabinetRecipe(kitchenCabinet.getBlock(), Ingredient.ofItems(Registry.BLOCK.get(new Identifier("minecraft:" + "smooth_quartz"))), Ingredient.ofItems(Registry.BLOCK.get(new Identifier("minecraft:" + "stripped_dark_oak_log"))), Ingredient.ofItems(Items.CHEST), exporter);
                } else {
                    offerCabinetRecipe(kitchenCabinet.getBlock(), Ingredient.ofItems(kitchenCabinet.getSecondaryMaterial().asItem()), Ingredient.ofItems(kitchenCabinet.getBaseMaterial().asItem()), Ingredient.ofItems(Items.CHEST), exporter);
                }
            }

            FurnitureBlock[] woodKitchenCounterOvens = KitchenCounterOvenBlock.streamWoodCounterOvens().toList().toArray(new FurnitureBlock[0]);
            for (FurnitureBlock kitchenCounterOven : woodKitchenCounterOvens) {
                String cabinetName = kitchenCounterOven.getBlock().toString();
                if (cabinetName.contains("light_wood")) {
                    offerCounterAppliance(kitchenCounterOven.getBlock(), Ingredient.ofItems(Registry.BLOCK.get(new Identifier("minecraft:" + "smooth_quartz"))), Ingredient.ofItems(Registry.BLOCK.get(new Identifier("minecraft:" + "stripped_oak_log"))), Ingredient.ofItems(Items.FURNACE), exporter);
                } else if (cabinetName.contains("dark_wood")) {
                    offerCounterAppliance(kitchenCounterOven.getBlock(), Ingredient.ofItems(Registry.BLOCK.get(new Identifier("minecraft:" + "smooth_quartz"))), Ingredient.ofItems(Registry.BLOCK.get(new Identifier("minecraft:" + "stripped_dark_oak_log"))), Ingredient.ofItems(Items.FURNACE), exporter);
                } else {
                    offerCounterAppliance(kitchenCounterOven.getBlock(), Ingredient.ofItems(kitchenCounterOven.getSecondaryMaterial().asItem()), Ingredient.ofItems(kitchenCounterOven.getBaseMaterial().asItem()), Ingredient.ofItems(Items.FURNACE), exporter);
                }
            }

            FurnitureBlock[] woodKitchenCounters = KitchenCounterBlock.streamWoodCounters().toList().toArray(new FurnitureBlock[0]);
            for (FurnitureBlock KitchenCounterBlock : woodKitchenCounters) {
                String cabinetName = KitchenCounterBlock.getBlock().toString();
                if (cabinetName.contains("light_wood")) {
                    offerCounterRecipe(KitchenCounterBlock.getBlock(), Ingredient.ofItems(Registry.BLOCK.get(new Identifier("minecraft:" + "smooth_quartz"))), Ingredient.ofItems(Registry.BLOCK.get(new Identifier("minecraft:" + "stripped_oak_log"))), exporter);
                } else if (cabinetName.contains("dark_wood")) {
                    offerCounterRecipe(KitchenCounterBlock.getBlock(), Ingredient.ofItems(Registry.BLOCK.get(new Identifier("minecraft:" + "smooth_quartz"))), Ingredient.ofItems(Registry.BLOCK.get(new Identifier("minecraft:" + "stripped_dark_oak_log"))), exporter);
                } else {
                    offerCounterRecipe(KitchenCounterBlock.getBlock(), Ingredient.ofItems(KitchenCounterBlock.getSecondaryMaterial().asItem()), Ingredient.ofItems(KitchenCounterBlock.getBaseMaterial().asItem()), exporter);
                }
            }

            FurnitureBlock[] woodKitchenDrawers = KitchenDrawerBlock.streamWoodDrawers().toList().toArray(new FurnitureBlock[0]);
            for (FurnitureBlock kitchenDrawer : woodKitchenDrawers) {
                String cabinetName = kitchenDrawer.getBlock().toString();
                if (cabinetName.contains("light_wood")) {
                    offerCounterAppliance(kitchenDrawer.getBlock(), Ingredient.ofItems(Registry.BLOCK.get(new Identifier("minecraft:" + "smooth_quartz"))), Ingredient.ofItems(Registry.BLOCK.get(new Identifier("minecraft:" + "stripped_oak_log"))), Ingredient.ofItems(Items.CHEST), exporter);
                } else if (cabinetName.contains("dark_wood")) {
                    offerCounterAppliance(kitchenDrawer.getBlock(), Ingredient.ofItems(Registry.BLOCK.get(new Identifier("minecraft:" + "smooth_quartz"))), Ingredient.ofItems(Registry.BLOCK.get(new Identifier("minecraft:" + "stripped_dark_oak_log"))), Ingredient.ofItems(Items.CHEST), exporter);
                } else {
                    offerCounterAppliance(kitchenDrawer.getBlock(), Ingredient.ofItems(kitchenDrawer.getSecondaryMaterial().asItem()), Ingredient.ofItems(kitchenDrawer.getBaseMaterial().asItem()), Ingredient.ofItems(Items.CHEST), exporter);
                }
            }

            FurnitureBlock[] woodWallKitchenCounters = KitchenWallCounterBlock.streamWallWoodCounters().toList().toArray(new FurnitureBlock[0]);
            for (FurnitureBlock KitchenCounterBlock : woodWallKitchenCounters) {
                String cabinetName = KitchenCounterBlock.getBlock().toString();
                if (cabinetName.contains("light_wood")) {
                    offerCounterRecipe(KitchenCounterBlock.getBlock(), Ingredient.ofItems(Registry.BLOCK.get(new Identifier("minecraft:" + "smooth_quartz"))), Ingredient.ofItems(Registry.BLOCK.get(new Identifier("minecraft:" + "stripped_oak_log"))), exporter);
                } else if (cabinetName.contains("dark_wood")) {
                    offerCounterRecipe(KitchenCounterBlock.getBlock(), Ingredient.ofItems(Registry.BLOCK.get(new Identifier("minecraft:" + "smooth_quartz"))), Ingredient.ofItems(Registry.BLOCK.get(new Identifier("minecraft:" + "stripped_dark_oak_log"))), exporter);
                } else {
                    offerCounterRecipe(KitchenCounterBlock.getBlock(), Ingredient.ofItems(KitchenCounterBlock.getBaseMaterial().asItem()), Ingredient.ofItems(KitchenCounterBlock.getBaseMaterial().asItem()), exporter);
                }
            }

            FurnitureBlock[] woodWallKitchenDrawers = KitchenWallDrawerBlock.streamWallWoodDrawers().toList().toArray(new FurnitureBlock[0]);
            for (FurnitureBlock kitchenDrawer : woodWallKitchenDrawers) {
                String cabinetName = kitchenDrawer.getBlock().toString();
                if (cabinetName.contains("light_wood")) {
                    offerWallDrawer(kitchenDrawer.getBlock(), Ingredient.ofItems(Registry.BLOCK.get(new Identifier("minecraft:" + "smooth_quartz"))), Ingredient.ofItems(Registry.BLOCK.get(new Identifier("minecraft:" + "stripped_oak_log"))), Ingredient.ofItems(Items.CHEST), exporter);
                } else if (cabinetName.contains("dark_wood")) {
                    offerWallDrawer(kitchenDrawer.getBlock(), Ingredient.ofItems(Registry.BLOCK.get(new Identifier("minecraft:" + "smooth_quartz"))), Ingredient.ofItems(Registry.BLOCK.get(new Identifier("minecraft:" + "stripped_dark_oak_log"))), Ingredient.ofItems(Items.CHEST), exporter);
                } else {
                    offerWallDrawer(kitchenDrawer.getBlock(), Ingredient.ofItems(kitchenDrawer.getSecondaryMaterial().asItem()), Ingredient.ofItems(kitchenDrawer.getBaseMaterial().asItem()), Ingredient.ofItems(Items.CHEST), exporter);
                }
            }

            FurnitureBlock[] woodWallSmallKitchenDrawers = KitchenWallDrawerSmallBlock.streamWoodWallSmallDrawers().toList().toArray(new FurnitureBlock[0]);
            for (FurnitureBlock kitchenDrawer : woodWallSmallKitchenDrawers) {
                String cabinetName = kitchenDrawer.getBlock().toString();
                if (cabinetName.contains("light_wood")) {
                    offerWallDrawerSmall(kitchenDrawer.getBlock(), Ingredient.ofItems(Registry.BLOCK.get(new Identifier("minecraft:" + "smooth_quartz"))), Ingredient.ofItems(Registry.BLOCK.get(new Identifier("minecraft:" + "stripped_oak_log"))), Ingredient.ofItems(Items.CHEST), exporter);
                } else if (cabinetName.contains("dark_wood")) {
                    offerWallDrawerSmall(kitchenDrawer.getBlock(), Ingredient.ofItems(Registry.BLOCK.get(new Identifier("minecraft:" + "smooth_quartz"))), Ingredient.ofItems(Registry.BLOCK.get(new Identifier("minecraft:" + "stripped_dark_oak_log"))), Ingredient.ofItems(Items.CHEST), exporter);
                } else {
                    offerWallDrawerSmall(kitchenDrawer.getBlock(), Ingredient.ofItems(kitchenDrawer.getSecondaryMaterial().asItem()), Ingredient.ofItems(kitchenDrawer.getBaseMaterial().asItem()), Ingredient.ofItems(Items.CHEST), exporter);
                }
            }

            FurnitureBlock[] woodKitchenSinks = KitchenSinkBlock.streamWoodSinks().toList().toArray(new FurnitureBlock[0]);
            for (FurnitureBlock kitchenSink : woodKitchenSinks) {
                String cabinetName = kitchenSink.getBlock().toString();
                if (cabinetName.contains("light_wood")) {
                    offerSinkRecipe(kitchenSink.getBlock(), Ingredient.ofItems(Registry.BLOCK.get(new Identifier("minecraft:" + "smooth_quartz"))), Ingredient.ofItems(Registry.BLOCK.get(new Identifier("minecraft:" + "stripped_oak_log"))), Ingredient.ofItems(Items.BUCKET), Ingredient.ofItems(Items.IRON_INGOT), exporter);
                } else if (cabinetName.contains("dark_wood")) {
                    offerSinkRecipe(kitchenSink.getBlock(), Ingredient.ofItems(Registry.BLOCK.get(new Identifier("minecraft:" + "smooth_quartz"))), Ingredient.ofItems(Registry.BLOCK.get(new Identifier("minecraft:" + "stripped_dark_oak_log"))), Ingredient.ofItems(Items.BUCKET), Ingredient.ofItems(Items.IRON_INGOT), exporter);
                } else {
                    offerSinkRecipe(kitchenSink.getBlock(), Ingredient.ofItems(kitchenSink.getSecondaryMaterial().asItem()), Ingredient.ofItems(kitchenSink.getBaseMaterial().asItem()), Ingredient.ofItems(Items.BUCKET), Ingredient.ofItems(Items.IRON_INGOT), exporter);
                }
            }

            FurnitureBlock[] logStools = LogStoolBlock.streamWoodLogStools().toList().toArray(new FurnitureBlock[0]);
            for (FurnitureBlock logStool : logStools) {
                offerLogStoolRecipe(logStool.getBlock(), Ingredient.ofItems(logStool.getSecondaryMaterial().asItem()), exporter);
            }

            FurnitureBlock[] woodModernStools = ModernStoolBlock.streamWoodModernStools().toList().toArray(new FurnitureBlock[0]);
            for (FurnitureBlock modernStool : woodModernStools) {
                offerModernStoolRecipe(modernStool.getBlock(), Ingredient.ofItems(modernStool.getSecondaryMaterial().asItem()), Ingredient.ofItems(modernStool.getBaseMaterial().asItem()), exporter);
            }

            FurnitureBlock[] stoneModernStools = ModernStoolBlock.streamStoneModernStools().toList().toArray(new FurnitureBlock[0]);
            for (FurnitureBlock modernStool : stoneModernStools) {
                offerModernStoolRecipe(modernStool.getBlock(), Ingredient.ofItems(modernStool.getSecondaryStoneMaterial().asItem()), Ingredient.ofItems(modernStool.getBaseStoneMaterial().asItem()), exporter);
            }

            FurnitureBlock[] logTables = LogTableBlock.streamWoodLogTables().toList().toArray(new FurnitureBlock[0]);
            for (FurnitureBlock logTable : logTables) {
                if (logTable.getBlock().toString().contains("raw") && logTable.getBlock().toString().contains("stripped")){
                    offerLogTableRecipe(logTable.getBlock(), Ingredient.ofItems(logTable.getStrippedBaseMaterial().asItem()), Ingredient.ofItems(logTable.getStrippedBaseMaterial().asItem()), exporter);
                } else if (logTable.getBlock().toString().contains("stripped")){
                    offerLogTableRecipe(logTable.getBlock(), Ingredient.ofItems(logTable.getSecondaryMaterial().asItem()), Ingredient.ofItems(logTable.getStrippedBaseMaterial().asItem()), exporter);
                }
                else if (logTable.getBlock().toString().contains("raw")) {
                    offerLogTableRecipe(logTable.getBlock(), Ingredient.ofItems(logTable.getSecondaryMaterial().asItem()), Ingredient.ofItems(logTable.getSecondaryMaterial().asItem()), exporter);
                } else {
                    offerLogTableRecipe(logTable.getBlock(), Ingredient.ofItems(logTable.getSecondaryMaterial().asItem()), Ingredient.ofItems(logTable.getBaseMaterial().asItem()), exporter);
                }
            }

            FurnitureBlock[] stoneNaturalTables = LogTableBlock.streamStoneNaturalTables().toList().toArray(new FurnitureBlock[0]);
            for (FurnitureBlock naturalTable : stoneNaturalTables) {
                offerLogTableRecipe(naturalTable.getBlock(), Ingredient.ofItems(naturalTable.getSecondaryStoneMaterial().asItem()), Ingredient.ofItems(naturalTable.getBaseStoneMaterial().asItem()), exporter);
            }

            FurnitureBlock[] woodSimpleStools = SimpleStoolBlock.streamWoodSimpleStools().toList().toArray(new FurnitureBlock[0]);
            for (FurnitureBlock simpleStool : woodSimpleStools) {
                offerSimpleStoolRecipe(simpleStool.getBlock(), Ingredient.ofItems(simpleStool.getSecondaryMaterial().asItem()), Ingredient.ofItems(simpleStool.getBaseMaterial().asItem()), exporter);
            }

            FurnitureBlock[] stoneSimpleStools = SimpleStoolBlock.streamStoneSimpleStools().toList().toArray(new FurnitureBlock[0]);
            for (FurnitureBlock simpleStool : stoneSimpleStools) {
                offerSimpleStoolRecipe(simpleStool.getBlock(), Ingredient.ofItems(simpleStool.getSecondaryStoneMaterial().asItem()), Ingredient.ofItems(simpleStool.getBaseStoneMaterial().asItem()), exporter);
            }

            FurnitureBlock[] woodBasicTables = BasicTableBlock.streamWoodBasicTables().toList().toArray(new FurnitureBlock[0]);
            for (FurnitureBlock basicTable : woodBasicTables) {
                offerBasicTableRecipe(basicTable.getBlock(), Ingredient.ofItems(basicTable.getSecondaryMaterial().asItem()), Ingredient.ofItems(basicTable.getBaseMaterial().asItem()), exporter);
            }

            FurnitureBlock[] stoneBasicTables = BasicTableBlock.streamStoneBasicTables().toList().toArray(new FurnitureBlock[0]);
            for (FurnitureBlock simpleStool : stoneBasicTables) {
                offerBasicTableRecipe(simpleStool.getBlock(), Ingredient.ofItems(simpleStool.getSecondaryStoneMaterial().asItem()), Ingredient.ofItems(simpleStool.getBaseStoneMaterial().asItem()), exporter);
            }

            FurnitureBlock[] woodModernDinnerTables = ModernDinnerTableBlock.streamWoodModernDinnerTables().toList().toArray(new FurnitureBlock[0]);
            for (FurnitureBlock modernDinnerTables : woodModernDinnerTables) {
                offerModernDinnerTableRecipe(modernDinnerTables.getBlock(), Ingredient.ofItems(modernDinnerTables.getSecondaryMaterial().asItem()), Ingredient.ofItems(modernDinnerTables.getBaseMaterial().asItem()), exporter);
            }

            FurnitureBlock[] stoneModernDinnerTables = ModernDinnerTableBlock.streamStoneModernDinnerTables().toList().toArray(new FurnitureBlock[0]);
            for (FurnitureBlock modernDinnerTable : stoneModernDinnerTables) {
                offerModernDinnerTableRecipe(modernDinnerTable.getBlock(), Ingredient.ofItems(modernDinnerTable.getSecondaryStoneMaterial().asItem()), Ingredient.ofItems(modernDinnerTable.getBaseStoneMaterial().asItem()), exporter);
            }

            FurnitureBlock[] woodClassicTables = ClassicTableBlock.streamWoodClassicTables().toList().toArray(new FurnitureBlock[0]);
            for (FurnitureBlock classicTable : woodClassicTables) {
                offerClassicTableRecipe(classicTable.getBlock(), Ingredient.ofItems(classicTable.getSecondaryMaterial().asItem()), Ingredient.ofItems(classicTable.getBaseMaterial().asItem()), exporter);
            }

            FurnitureBlock[] stoneClassicTables = ClassicTableBlock.streamStoneClassicTables().toList().toArray(new FurnitureBlock[0]);
            for (FurnitureBlock classicTable : stoneClassicTables) {
                offerClassicTableRecipe(classicTable.getBlock(), Ingredient.ofItems(classicTable.getSecondaryStoneMaterial().asItem()), Ingredient.ofItems(classicTable.getBaseStoneMaterial().asItem()), exporter);
            }

            FurnitureBlock[] woodDinnerTables = DinnerTableBlock.streamWoodDinnerTables().toList().toArray(new FurnitureBlock[0]);
            for (FurnitureBlock dinnerTable : woodDinnerTables) {
                offerDinnerTableRecipe(dinnerTable.getBlock(), Ingredient.ofItems(dinnerTable.getSecondaryMaterial().asItem()), Ingredient.ofItems(dinnerTable.getBaseMaterial().asItem()), exporter);
            }

            FurnitureBlock[] stoneDinnerTables = DinnerTableBlock.streamStoneDinnerTables().toList().toArray(new FurnitureBlock[0]);
            for (FurnitureBlock dinnerTable : stoneDinnerTables) {
                offerDinnerTableRecipe(dinnerTable.getBlock(), Ingredient.ofItems(dinnerTable.getSecondaryStoneMaterial().asItem()), Ingredient.ofItems(dinnerTable.getBaseStoneMaterial().asItem()), exporter);
            }

            FurnitureBlock[] stoneKitchenCabinets = KitchenCabinetBlock.streamStoneCabinets().toList().toArray(new FurnitureBlock[0]);
            for (FurnitureBlock kitchenCabinet : stoneKitchenCabinets) {
                offerCabinetRecipe(kitchenCabinet.getBlock(), Ingredient.ofItems(kitchenCabinet.getSecondaryMaterial().asItem()), Ingredient.ofItems(kitchenCabinet.getBaseMaterial().asItem()), Ingredient.ofItems(Items.CHEST), exporter);
            }

            FurnitureBlock[] stoneKitchenCounterOvens = KitchenCounterOvenBlock.streamStoneCounterOvens().toList().toArray(new FurnitureBlock[0]);
            for (FurnitureBlock kitchenCounterOven : stoneKitchenCounterOvens) {
                offerCounterAppliance(kitchenCounterOven.getBlock(), Ingredient.ofItems(kitchenCounterOven.getSecondaryMaterial().asItem()), Ingredient.ofItems(kitchenCounterOven.getBaseMaterial().asItem()), Ingredient.ofItems(Items.FURNACE), exporter);
            }

            FurnitureBlock[] stoneKitchenCounters = KitchenCounterBlock.streamStoneCounters().toList().toArray(new FurnitureBlock[0]);
            for (FurnitureBlock KitchenCounterBlock : stoneKitchenCounters) {
                    offerCounterRecipe(KitchenCounterBlock.getBlock(), Ingredient.ofItems(KitchenCounterBlock.getSecondaryMaterial().asItem()), Ingredient.ofItems(KitchenCounterBlock.getBaseMaterial().asItem()), exporter);
            }

            FurnitureBlock[] stoneWallKitchenCounters = KitchenWallCounterBlock.streamWallStoneCounters().toList().toArray(new FurnitureBlock[0]);
            for (FurnitureBlock KitchenCounterBlock : stoneWallKitchenCounters) {
                offerCounterRecipe(KitchenCounterBlock.getBlock(), Ingredient.ofItems(KitchenCounterBlock.getBaseMaterial().asItem()), Ingredient.ofItems(KitchenCounterBlock.getBaseMaterial().asItem()), exporter);
            }

            FurnitureBlock[] stoneKitchenDrawers = KitchenDrawerBlock.streamStoneDrawers().toList().toArray(new FurnitureBlock[0]);
            for (FurnitureBlock kitchenDrawer : stoneKitchenDrawers) {
                offerCounterAppliance(kitchenDrawer.getBlock(), Ingredient.ofItems(kitchenDrawer.getSecondaryMaterial().asItem()), Ingredient.ofItems(kitchenDrawer.getBaseMaterial().asItem()), Ingredient.ofItems(Items.CHEST), exporter);
            }

            FurnitureBlock[] stoneWallKitchenDrawers = KitchenWallDrawerBlock.streamWallStoneDrawers().toList().toArray(new FurnitureBlock[0]);
            for (FurnitureBlock kitchenDrawer : stoneWallKitchenDrawers) {
                offerWallDrawer(kitchenDrawer.getBlock(), Ingredient.ofItems(kitchenDrawer.getSecondaryMaterial().asItem()), Ingredient.ofItems(kitchenDrawer.getBaseMaterial().asItem()), Ingredient.ofItems(Items.CHEST), exporter);
            }

            FurnitureBlock[] stoneWallSmallKitchenDrawers = KitchenWallDrawerSmallBlock.streamStoneWallSmallDrawers().toList().toArray(new FurnitureBlock[0]);
            for (FurnitureBlock kitchenDrawer : stoneWallSmallKitchenDrawers) {
                offerWallDrawerSmall(kitchenDrawer.getBlock(), Ingredient.ofItems(kitchenDrawer.getSecondaryMaterial().asItem()), Ingredient.ofItems(kitchenDrawer.getBaseMaterial().asItem()), Ingredient.ofItems(Items.CHEST), exporter);
            }


            FurnitureBlock[] stoneKitchenSinks = KitchenSinkBlock.streamStoneSinks().toList().toArray(new FurnitureBlock[0]);
            for (FurnitureBlock kitchenSink : stoneKitchenSinks) {
                offerSinkRecipe(kitchenSink.getBlock(), Ingredient.ofItems(kitchenSink.getSecondaryMaterial().asItem()), Ingredient.ofItems(kitchenSink.getBaseMaterial().asItem()), Ingredient.ofItems(Items.BUCKET), Ingredient.ofItems(Items.IRON_INGOT), exporter);
            }

            FurnitureBlock[] froggyChairs = FroggyChairBlock.streamFroggyChair().toList().toArray(new FurnitureBlock[0]);
            for (FurnitureBlock froggyChair : froggyChairs) {
                offerFroggyChairRecipe(froggyChair.getBlock(), Ingredient.ofItems(froggyChair.getFroggyChairMaterial().asItem()), exporter);
            }

            FurnitureBlock[] fridges = FridgeBlock.streamFridges().toList().toArray(new FurnitureBlock[0]);
            for (FurnitureBlock fridge : fridges) {
                offerFridgeRecipe(fridge.getBlock(), Ingredient.ofItems(fridge.getFridgeMaterial().asItem()), Ingredient.ofItems(Items.CHEST), exporter);
            }

            FurnitureBlock[] armChairs = ArmChairColoredBlock.streamArmChairColored().toList().toArray(new FurnitureBlock[0]);
            for (FurnitureBlock armChair : armChairs) {
                offerArmChair(armChair.getBlock(), Ingredient.ofItems(Items.OAK_LOG), Ingredient.ofItems(armChair.getArmChairMaterial().asItem()), exporter);
            }

            FurnitureBlock[] simpleSofas = SimpleSofaBlock.streamSimpleSofas().toList().toArray(new FurnitureBlock[0]);
            for (FurnitureBlock sofa : simpleSofas) {
                offerSimpleSofa(sofa.getBlock(), Ingredient.ofItems(Items.OAK_LOG), Ingredient.ofItems(sofa.getArmChairMaterial().asItem()), exporter);
            }

            FurnitureBlock[] leatherArmChairs =  ArmChairBlock.streamArmChairs().toList().toArray(new FurnitureBlock[0]);
            for (FurnitureBlock armChair : leatherArmChairs) {
                offerArmChair(armChair.getBlock(), Ingredient.ofItems(Items.OAK_LOG), Ingredient.ofItems(armChair.getArmChairMaterial().asItem()), exporter);
            }

            FurnitureBlock[] woolClassicChairs = ClassicChairDyeableBlock.streamWoodDyeableChair().toList().toArray(new FurnitureBlock[0]);
            for (FurnitureBlock classicChair : woolClassicChairs) {
                offerClassicChairRecipe(classicChair.getBlock(),  Ingredient.ofItems(Items.OAK_LOG), Ingredient.ofItems(classicChair.getArmChairMaterial()), exporter);
            }

            FurnitureBlock[] microwaves = MicrowaveBlock.streamMicrowaves().toList().toArray(new FurnitureBlock[0]);
            for (FurnitureBlock microwave : microwaves) {
                offerMicrowaveRecipe(microwave.getBlock(),  Ingredient.ofItems(microwave.getFridgeMaterial().asItem()), Ingredient.ofItems(Items.FURNACE), exporter);
            }

            FurnitureBlock[] stoves = StoveBlock.streamStoves().toList().toArray(new FurnitureBlock[0]);
            for (FurnitureBlock stove : stoves) {
                offerStoveRecipe(stove.getBlock(),  Ingredient.ofItems(stove.getFridgeMaterial().asItem()), Ingredient.ofItems(Items.FURNACE), exporter);
            }

            FurnitureBlock[] ironStove = IronStoveBlock.streamIronStoves().toList().toArray(new FurnitureBlock[0]);
            for (FurnitureBlock stove : ironStove) {
                offerStoveRecipe(stove.getBlock(),  Ingredient.ofItems(stove.getFridgeMaterial().asItem()), Ingredient.ofItems(Items.FURNACE), exporter);
            }

            FurnitureBlock[] rangeHoods = KitchenRangeHoodBlock.streamOvenRangeHoods().toList().toArray(new FurnitureBlock[0]);
            for (FurnitureBlock rangeHood : rangeHoods) {
                offerRangeHood(rangeHood.getBlock(),  Ingredient.ofItems(rangeHood.getFridgeMaterial().asItem()), Ingredient.ofItems(Items.REDSTONE_LAMP), exporter);
            }

            FurnitureBlock[] woodClassicNightStands = ClassicNightstandBlock.streamWoodClassicNightstands().toList().toArray(new FurnitureBlock[0]);
            for (FurnitureBlock nightStand : woodClassicNightStands) {
                offerClassicNightStandRecipe(nightStand.getBlock(),  Ingredient.ofItems(nightStand.getSecondaryMaterial()), Ingredient.ofItems(nightStand.getBaseMaterial()), exporter);
            }

            FurnitureBlock[] stoneClassicNightStands = ClassicNightstandBlock.streamStoneClassicNightstands().toList().toArray(new FurnitureBlock[0]);
            for (FurnitureBlock nightStand : stoneClassicNightStands) {
                offerClassicNightStandRecipe(nightStand.getBlock(), Ingredient.ofItems(nightStand.getSecondaryStoneMaterial().asItem()), Ingredient.ofItems(nightStand.getBaseStoneMaterial().asItem()), exporter);
            }

            FurnitureBlock[] simpleBeds = SimpleBedBlock.streamSimpleBeds().toList().toArray(new FurnitureBlock[0]);
            for (FurnitureBlock bed : simpleBeds) {
                offerSimpleBed(bed.getBlock(),  Ingredient.ofItems(bed.getBaseMaterial()), Ingredient.ofItems(bed.getBed()), exporter);
            }

            FurnitureBlock[] classicBeds = ClassicBedBlock.streamClassicBeds().toList().toArray(new FurnitureBlock[0]);
            for (FurnitureBlock bed : classicBeds) {
                offerClassicBed(bed.getBlock(),  Ingredient.ofItems(bed.getBaseMaterial()), Ingredient.ofItems(bed.getBed()), Ingredient.ofItems(bed.getFence()), exporter);
            }

            FurnitureBlock[] plates = PlateBlock.streamPlates().toList().toArray(new FurnitureBlock[0]);
            for (FurnitureBlock plate : plates) {
                offerPlate(plate.getBlock(),  Ingredient.ofItems(plate.getPlateMaterial()), Ingredient.ofItems(Items.ITEM_FRAME), Ingredient.ofItems(plate.getPlateDecoration()), exporter);
            }

            FurnitureBlock[] cutleries = CutleryBlock.streamCutlery().toList().toArray(new FurnitureBlock[0]);
            for (FurnitureBlock cutlery : cutleries) {
                offerCutlery(cutlery.getBlock(),  Ingredient.ofItems(cutlery.getCutleryMaterial()), exporter);
            }

            FurnitureBlock[] simpleBunkLadders = SimpleBunkLadderBlock.streamSimpleBunkLadder().toList().toArray(new FurnitureBlock[0]);
            for (FurnitureBlock simpleBunkLadder : simpleBunkLadders) {
                offerSimpleBunkLadderRecipe(simpleBunkLadder.getBlock(),  Ingredient.ofItems(simpleBunkLadder.getBaseMaterial()), exporter);
            }

            FurnitureBlock[] basicToilets = BasicToiletBlock.streamBasicToilet().toList().toArray(new FurnitureBlock[0]);
            for (FurnitureBlock toilet : basicToilets) {
                offerToiletRecipe(toilet.getBlock(),  Ingredient.ofItems(Items.STONE_BUTTON), Ingredient.ofItems(Blocks.QUARTZ_BLOCK), exporter);
            }

            FurnitureBlock[] showerTowels = ShowerTowelBlock.streamShowerTowels().toList().toArray(new FurnitureBlock[0]);
            for (FurnitureBlock towel : showerTowels) {
                offerShowerTowelRecipe(towel.getBlock(),  Ingredient.ofItems(towel.getWoolColor()), exporter);
            }
        }
    }


    public static void offerClassicChairRecipe(ItemConvertible output, Ingredient legMaterial, Ingredient baseMaterial, Consumer<RecipeJsonProvider> exporter) {
        SimpleFurnitureRecipeJsonFactory.create(output, 4).input('S', legMaterial).input('X', baseMaterial).pattern("S  ").pattern("SXX").pattern("S S").offerTo(exporter, new Identifier("pfm", output.asItem().getTranslationKey().replace("block.pfm.", "")));
    }

    public static void offerBasicChairRecipe(ItemConvertible output, Ingredient legMaterial, Ingredient baseMaterial, Consumer<RecipeJsonProvider> exporter) {
        SimpleFurnitureRecipeJsonFactory.create(output, 4).input('S', legMaterial).input('X', baseMaterial).pattern("X  ").pattern("XXX").pattern("S S").offerTo(exporter, new Identifier("pfm", output.asItem().getTranslationKey().replace("block.pfm.", "")));
    }

    public static void offerDinnerChairRecipe(ItemConvertible output, Ingredient legMaterial, Ingredient baseMaterial, Consumer<RecipeJsonProvider> exporter) {
        SimpleFurnitureRecipeJsonFactory.create(output, 4).input('S', legMaterial).input('X', baseMaterial).pattern("S  ").pattern("XXX").pattern("S S").offerTo(exporter, new Identifier("pfm", output.asItem().getTranslationKey().replace("block.pfm.", "")));
    }

    public static void offerModernChairRecipe(ItemConvertible output, Ingredient legMaterial, Ingredient baseMaterial, Consumer<RecipeJsonProvider> exporter) {
        SimpleFurnitureRecipeJsonFactory.create(output, 4).input('S', legMaterial).input('X', baseMaterial).pattern("X  ").pattern("SXX").pattern("S S").offerTo(exporter, new Identifier("pfm", output.asItem().getTranslationKey().replace("block.pfm.", "")));
    }

    public static void offerFroggyChairRecipe(ItemConvertible output, Ingredient legMaterial, Consumer<RecipeJsonProvider> exporter) {
        SimpleFurnitureRecipeJsonFactory.create(output, 4).input('X', legMaterial).pattern("X  ").pattern("XXX").pattern("X X").offerTo(exporter, new Identifier("pfm", output.asItem().getTranslationKey().replace("block.pfm.", "")));
    }

    public static void offerSimpleSofa(ItemConvertible output, Ingredient baseMaterial, Ingredient legMaterial, Consumer<RecipeJsonProvider> exporter) {
        SimpleFurnitureRecipeJsonFactory.create(output, 2).input('X', legMaterial).input('S', baseMaterial).pattern("X  ").pattern("SXS").pattern("S S").offerTo(exporter, new Identifier("pfm", output.asItem().getTranslationKey().replace("block.pfm.", "")));
    }

    public static void offerArmChair(ItemConvertible output, Ingredient baseMaterial, Ingredient legMaterial, Consumer<RecipeJsonProvider> exporter) {
        SimpleFurnitureRecipeJsonFactory.create(output, 2).input('X', legMaterial).input('S', baseMaterial).pattern("X  ").pattern("XXX").pattern("S S").offerTo(exporter, new Identifier("pfm", output.asItem().getTranslationKey().replace("block.pfm.", "")));
    }

    public static void offerClassicStoolRecipe(ItemConvertible output, Ingredient legMaterial, Ingredient baseMaterial, Consumer<RecipeJsonProvider> exporter) {
        SimpleFurnitureRecipeJsonFactory.create(output, 4).input('S', legMaterial).input('X', baseMaterial).pattern("SXX").pattern("S S").offerTo(exporter, new Identifier("pfm", output.asItem().getTranslationKey().replace("block.pfm.", "")));
    }

    public static void offerModernStoolRecipe(ItemConvertible output, Ingredient legMaterial, Ingredient baseMaterial, Consumer<RecipeJsonProvider> exporter) {
        SimpleFurnitureRecipeJsonFactory.create(output, 4).input('S', legMaterial).input('X', baseMaterial).pattern("XXX").pattern(" S ").offerTo(exporter, new Identifier("pfm", output.asItem().getTranslationKey().replace("block.pfm.", "")));
    }

    public static void offerSimpleStoolRecipe(ItemConvertible output, Ingredient legMaterial, Ingredient baseMaterial, Consumer<RecipeJsonProvider> exporter) {
        SimpleFurnitureRecipeJsonFactory.create(output, 4).input('S', legMaterial).input('X', baseMaterial).pattern("XXX").pattern("S S").offerTo(exporter, new Identifier("pfm", output.asItem().getTranslationKey().replace("block.pfm.", "")));
    }

    public static void offerHerringbonePlanks(ItemConvertible output, Item baseMaterial, Consumer<RecipeJsonProvider> exporter) {
        ShapedRecipeJsonFactory.create(output, 4).input('X', baseMaterial).pattern("XX").pattern("XX").criterion("has_wood_slabs", RecipesProvider.conditionsFromItem(baseMaterial)).offerTo(exporter, new Identifier("pfm", output.asItem().getTranslationKey().replace("block.pfm.", "")));
    }

    public static void offerCabinetRecipe(ItemConvertible output, Ingredient baseMaterial, Ingredient legMaterial, Ingredient chest, Consumer<RecipeJsonProvider> exporter) {
        SimpleFurnitureRecipeJsonFactory.create(output, 1).input('X', legMaterial).input('S', baseMaterial).input('Y', chest).pattern("XSX").pattern("XYX").pattern("XSX").offerTo(exporter, new Identifier("pfm", output.asItem().getTranslationKey().replace("block.pfm.", "")));
    }

    public static void offerCounterAppliance(ItemConvertible output, Ingredient baseMaterial, Ingredient legMaterial, Ingredient stove, Consumer<RecipeJsonProvider> exporter) {
        SimpleFurnitureRecipeJsonFactory.create(output, 1).input('X', legMaterial).input('S', baseMaterial).input('Y', stove).pattern("SSS").pattern("XYX").pattern("XXX").offerTo(exporter, new Identifier("pfm", output.asItem().getTranslationKey().replace("block.pfm.", "")));
    }

    public static void offerWallDrawer(ItemConvertible output, Ingredient baseMaterial, Ingredient legMaterial, Ingredient stove, Consumer<RecipeJsonProvider> exporter) {
        SimpleFurnitureRecipeJsonFactory.create(output, 1).input('X', legMaterial).input('S', baseMaterial).input('Y', stove).pattern("XXX").pattern("SYS").pattern("XXX").offerTo(exporter, new Identifier("pfm", output.asItem().getTranslationKey().replace("block.pfm.", "")));
    }

    public static void offerWallDrawerSmall(ItemConvertible output, Ingredient baseMaterial, Ingredient legMaterial, Ingredient stove, Consumer<RecipeJsonProvider> exporter) {
        SimpleFurnitureRecipeJsonFactory.create(output, 3).input('X', legMaterial).input('S', baseMaterial).input('Y', stove).pattern("XXX").pattern("SYS").offerTo(exporter, new Identifier("pfm", output.asItem().getTranslationKey().replace("block.pfm.", "")));
    }

    public static void offerCounterRecipe(ItemConvertible output, Ingredient baseMaterial, Ingredient legMaterial, Consumer<RecipeJsonProvider> exporter) {
        SimpleFurnitureRecipeJsonFactory.create(output, 6).input('X', legMaterial).input('S', baseMaterial).pattern("SSS").pattern("XXX").pattern("XXX").offerTo(exporter, new Identifier("pfm", output.asItem().getTranslationKey().replace("block.pfm.", "")));
    }

    public static void offerWallCounterRecipe(ItemConvertible output, Ingredient baseMaterial, Ingredient legMaterial, Consumer<RecipeJsonProvider> exporter) {
        SimpleFurnitureRecipeJsonFactory.create(output, 6).input('X', legMaterial).input('S', baseMaterial).pattern("SSS").pattern("XXX").pattern("XXX").offerTo(exporter, new Identifier("pfm", output.asItem().getTranslationKey().replace("block.pfm.", "")));
    }

    public static void offerSinkRecipe(ItemConvertible output, Ingredient baseMaterial, Ingredient legMaterial, Ingredient center, Ingredient ingot, Consumer<RecipeJsonProvider> exporter) {
        SimpleFurnitureRecipeJsonFactory.create(output, 1).input('X', legMaterial).input('S', baseMaterial).input('Z', ingot).input('Y', center).pattern("SZS").pattern("XYX").pattern("XXX").offerTo(exporter, new Identifier("pfm", output.asItem().getTranslationKey().replace("block.pfm.", "")));
    }

    public static void offerFridgeRecipe(ItemConvertible output, Ingredient legMaterial, Ingredient storage, Consumer<RecipeJsonProvider> exporter) {
        if (output.asItem().toString().contains("xbox")) {
            SimpleFurnitureRecipeJsonFactory.create(output, 1).input('X', legMaterial).input('Y', storage).input('Z', Ingredient.ofItems(Items.REDSTONE)).input('P', Ingredient.ofItems(Items.WHITE_CONCRETE)).pattern("XPX").pattern("XYX").pattern("XZX").offerTo(exporter, new Identifier("pfm", output.asItem().getTranslationKey().replace("block.pfm.", "")));
        }
        else {
            SimpleFurnitureRecipeJsonFactory.create(output, 1).input('X', legMaterial).input('Y', storage).input('Z', Ingredient.ofItems(Items.REDSTONE)).pattern("XXX").pattern("XYX").pattern("XZX").offerTo(exporter, new Identifier("pfm", output.asItem().getTranslationKey().replace("block.pfm.", "")));
        }
    }

    public static void offerLogStoolRecipe(ItemConvertible output, Ingredient legMaterial, Consumer<RecipeJsonProvider> exporter) {
        SimpleFurnitureRecipeJsonFactory.create(output, 4).input('S', legMaterial).pattern("S").offerTo(exporter, new Identifier("pfm", output.asItem().getTranslationKey().replace("block.pfm.", "")));
    }

    public static void offerLogTableRecipe(ItemConvertible output, Ingredient legMaterial, Ingredient baseMaterial, Consumer<RecipeJsonProvider> exporter) {
        SimpleFurnitureRecipeJsonFactory.create(output, 4).input('S', legMaterial).input('X', baseMaterial).pattern("XXX").pattern(" S ").pattern(" S ").offerTo(exporter, new Identifier("pfm", output.asItem().getTranslationKey().replace("block.pfm.", "")));
    }

    public static void offerBasicTableRecipe(ItemConvertible output, Ingredient legMaterial, Ingredient baseMaterial, Consumer<RecipeJsonProvider> exporter) {
        SimpleFurnitureRecipeJsonFactory.create(output, 4).input('S', legMaterial).input('X', baseMaterial).pattern("XXX").pattern("S S").pattern("SSS").offerTo(exporter, new Identifier("pfm", output.asItem().getTranslationKey().replace("block.pfm.", "")));
    }

    public static void offerModernDinnerTableRecipe(ItemConvertible output, Ingredient legMaterial, Ingredient baseMaterial, Consumer<RecipeJsonProvider> exporter) {
        SimpleFurnitureRecipeJsonFactory.create(output, 4).input('S', legMaterial).input('X', baseMaterial).pattern("XXX").pattern("SSS").pattern("S S").offerTo(exporter, new Identifier("pfm", output.asItem().getTranslationKey().replace("block.pfm.", "")));
    }

    public static void offerClassicTableRecipe(ItemConvertible output, Ingredient legMaterial, Ingredient baseMaterial, Consumer<RecipeJsonProvider> exporter) {
        SimpleFurnitureRecipeJsonFactory.create(output, 4).input('S', legMaterial).input('X', baseMaterial).pattern("XXX").pattern("S S").pattern("S S").offerTo(exporter, new Identifier("pfm", output.asItem().getTranslationKey().replace("block.pfm.", "")));
    }

    public static void offerClassicNightStandRecipe(ItemConvertible output, Ingredient legMaterial, Ingredient baseMaterial, Consumer<RecipeJsonProvider> exporter) {
        SimpleFurnitureRecipeJsonFactory.create(output, 4).input('S', legMaterial).input('X', baseMaterial).input('Z', Blocks.CHEST).pattern("SXS").pattern("SZS").pattern("S S").offerTo(exporter, new Identifier("pfm", output.asItem().getTranslationKey().replace("block.pfm.", "")));
    }

    public static void offerDinnerTableRecipe(ItemConvertible output, Ingredient legMaterial, Ingredient baseMaterial, Consumer<RecipeJsonProvider> exporter) {
        SimpleFurnitureRecipeJsonFactory.create(output, 4).input('S', legMaterial).input('X', baseMaterial).pattern("XXX").pattern(" S ").pattern("S S").offerTo(exporter, new Identifier("pfm", output.asItem().getTranslationKey().replace("block.pfm.", "")));
    }

    public static void offerMicrowaveRecipe(ItemConvertible output, Ingredient legMaterial, Ingredient storage, Consumer<RecipeJsonProvider> exporter) {
        SimpleFurnitureRecipeJsonFactory.create(output, 1).input('X', legMaterial).input('Y', storage).input('Z', Ingredient.ofItems(Items.REDSTONE)).pattern("XXX").pattern("XYX").pattern("XZX").offerTo(exporter, new Identifier("pfm", output.asItem().getTranslationKey().replace("block.pfm.", "")));
    }

    public static void offerStoveRecipe(ItemConvertible output, Ingredient legMaterial, Ingredient storage, Consumer<RecipeJsonProvider> exporter) {
        SimpleFurnitureRecipeJsonFactory.create(output, 1).input('X', legMaterial).input('Y', storage).pattern("XXX").pattern("XYX").pattern("XXX").offerTo(exporter, new Identifier("pfm", output.asItem().getTranslationKey().replace("block.pfm.", "")));
    }

    public static void offerRangeHood(ItemConvertible output, Ingredient legMaterial, Ingredient secondMaterial, Consumer<RecipeJsonProvider> exporter) {
        SimpleFurnitureRecipeJsonFactory.create(output, 1).input('X', legMaterial).input('Y', secondMaterial).pattern(" X ").pattern(" X ").pattern("XYX").offerTo(exporter, new Identifier("pfm", output.asItem().getTranslationKey().replace("block.pfm.", "")));
    }

    public static void offerSimpleBed(ItemConvertible output, Ingredient legMaterial, Ingredient baseBed, Consumer<RecipeJsonProvider> exporter) {
        SimpleFurnitureRecipeJsonFactory.create(output, 1).input('X', legMaterial).input('Z', baseBed).pattern("XZX").pattern("XXX").offerTo(exporter, new Identifier("pfm", output.asItem().getTranslationKey().replace("block.pfm.", "")));
    }

    public static void offerClassicBed(ItemConvertible output, Ingredient legMaterial, Ingredient baseBed, Ingredient fence, Consumer<RecipeJsonProvider> exporter) {
        SimpleFurnitureRecipeJsonFactory.create(output, 1).input('X', legMaterial).input('Z', baseBed).input('Y', fence).pattern("YZY").pattern("XXX").offerTo(exporter, new Identifier("pfm", output.asItem().getTranslationKey().replace("block.pfm.", "")));
    }
    public static void offerPlate(ItemConvertible output, Ingredient base, Ingredient frame, Ingredient decoration, Consumer<RecipeJsonProvider> exporter) {
        SimpleFurnitureRecipeJsonFactory.create(output, 4).input('X', base).input('Z', frame).input('Y', decoration).pattern("XYX").pattern("YZY").pattern("XYX").offerTo(exporter, new Identifier("pfm", output.asItem().getTranslationKey().replace("block.pfm.", "")));
    }
    public static void offerCutlery(ItemConvertible output, Ingredient base, Consumer<RecipeJsonProvider> exporter) {
        SimpleFurnitureRecipeJsonFactory.create(output, 4).input('X', base).pattern("X X").pattern("X X").offerTo(exporter, new Identifier("pfm", output.asItem().getTranslationKey().replace("block.pfm.", "")));
    }

    public static void offerSimpleBunkLadderRecipe(ItemConvertible output, Ingredient base, Consumer<RecipeJsonProvider> exporter) {
        SimpleFurnitureRecipeJsonFactory.create(output, 4).input('Y', base).input('X', Ingredient.ofItems(Items.STICK)).pattern("X X").pattern("XYX").pattern("X X").offerTo(exporter, new Identifier("pfm", output.asItem().getTranslationKey().replace("block.pfm.", "")));
    }

    public static void offerShowerTowelRecipe(ItemConvertible output, Ingredient base, Consumer<RecipeJsonProvider> exporter) {
        SimpleFurnitureRecipeJsonFactory.create(output, 2).input('Y', base).input('X', Ingredient.ofItems(Items.LIGHT_GRAY_CONCRETE)).pattern("XYX").pattern("YYY").offerTo(exporter, new Identifier("pfm", output.asItem().getTranslationKey().replace("block.pfm.", "")));
    }

    public static void offerToiletRecipe(ItemConvertible output, Ingredient base, Ingredient material, Consumer<RecipeJsonProvider> exporter) {
        SimpleFurnitureRecipeJsonFactory.create(output, 1).input('Y', base).input('X', material).input('Z', Ingredient.ofItems(Items.BUCKET)).pattern("XY ").pattern("XZX").pattern(" X ").offerTo(exporter, new Identifier("pfm", output.asItem().getTranslationKey().replace("block.pfm.", "")));
    }*/
}