package com.unlikepaladin.pfm.runtime.data;

import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.stream.JsonWriter;
import com.mojang.serialization.JsonOps;
import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.blocks.*;
import com.unlikepaladin.pfm.blocks.models.ModelHelper;
import com.unlikepaladin.pfm.data.FurnitureBlock;
import com.unlikepaladin.pfm.data.materials.VariantBase;
import com.unlikepaladin.pfm.data.materials.WoodVariant;
import com.unlikepaladin.pfm.data.materials.WoodVariantRegistry;
import com.unlikepaladin.pfm.items.PFMComponents;
import com.unlikepaladin.pfm.menus.WorkbenchScreenHandler;
import com.unlikepaladin.pfm.registry.PaladinFurnitureModBlocksItems;
import com.unlikepaladin.pfm.runtime.PFMDataGenerator;
import com.unlikepaladin.pfm.runtime.PFMGenerator;
import com.unlikepaladin.pfm.runtime.PFMProvider;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.ComponentMap;
import net.minecraft.data.server.recipe.CraftingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.CraftingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.*;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.*;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.*;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Stream;

// TODO : Remake EMI screen to work with the new recipe system

public class PFMRecipeProvider extends PFMProvider {

    public PFMRecipeProvider(PFMGenerator parent) {
        super(parent, "PFM Recipes");
        parent.setProgress("Generating Recipes");
    }

    // Create a registry wrapper lookup without dynamic entries such as biomes as they don't exist yet
    private static RegistryWrapper.WrapperLookup createWrapperLookup() {
        RegistryBuilder builder = new RegistryBuilder();
        return builder.createWrapperLookup(DynamicRegistryManager.of(Registries.REGISTRIES));
    }

    @Override
    public void run() {
        startProviderRun();
        Path path = getParent().getOutput();
        Set<RegistryKey<Recipe<?>>> set = Sets.newHashSet();
        WorkbenchScreenHandler.ALL_RECIPES.clear();
        WorkbenchScreenHandler.CRAFTABLE_RECIPES.clear();
        RegistryWrapper.WrapperLookup lookup = createWrapperLookup();
        generateRecipes(new RecipeExporter() {
            @Override
            public void accept(RegistryKey<Recipe<?>> recipeId, Recipe<?> recipe, @Nullable AdvancementEntry advancementEntry) {
                if (!set.add(recipeId)) {
                    getParent().getLogger().error("Duplicate recipe " + recipeId);
                    throw new IllegalStateException("Duplicate recipe " + recipeId);
                }
                if (recipe == null) {
                    getParent().getLogger().error("Recipe Json Provider is null");
                    throw new IllegalStateException("Recipe Json Provider is null");
                }
                RegistryOps<JsonElement> ops = lookup.getOps(JsonOps.INSTANCE);
                    saveRecipe(Recipe.CODEC.encodeStart(ops, recipe).getOrThrow(IllegalStateException::new), path.resolve("data/" + recipeId.getValue().getNamespace() + "/recipe/" + recipeId.getValue().getPath() + ".json"));
                    if (advancementEntry != null) {
                        saveRecipeAdvancement(Advancement.CODEC.encodeStart(ops, advancementEntry.value()).getOrThrow(IllegalStateException::new), path.resolve("data/" + recipeId.getValue().getNamespace() + "/advancement/" + advancementEntry.id().getPath() + ".json"));
                }
            }

            @Override
            public Advancement.Builder getAdvancementBuilder() {
                return Advancement.Builder.createUntelemetered().parent(CraftingRecipeJsonBuilder.ROOT);
            }

            @Override
            public void addRootAdvancement() {

            }
        });
        endProviderRun();
    }

    private void saveRecipe(JsonElement json, Path path) {
        try {
            String string = PFMDataGenerator.GSON.toJson(json);
            if (!Files.exists(path.getParent()))
                Files.createDirectories(path.getParent());

            Files.writeString(path, string);
        } catch (IOException var10) {
            getParent().getLogger().error("Couldn't save recipe {}", path, var10);
        }
    }

    private void saveRecipeAdvancement(JsonElement json, Path path) {
        try {
            String string = PFMDataGenerator.GSON.toJson(json);
            if (!Files.exists(path.getParent()))
                Files.createDirectories(path.getParent());

            Files.writeString(path, string);
        } catch (IOException var10) {
            getParent().getLogger().error("Couldn't save recipe advancement {}", path, var10);
        }
    }
    @ExpectPlatform
    protected static Identifier getId(Block block) {
        throw new AssertionError();    
    }
    protected void generateRecipes(RecipeExporter exporter) {
        List<Identifier> generatedRecipes = new ArrayList<>();

        offerBasicChairRecipe(BasicChairBlock.class, "secondary", "base", PaladinFurnitureMod.furnitureEntryMap.get(BasicChairBlock.class).getVariants(), exporter);
        offerDinnerChairRecipe(DinnerChairBlock.class, "secondary", "base", PaladinFurnitureMod.furnitureEntryMap.get(DinnerChairBlock.class).getVariants(), exporter);
        offerClassicChairRecipe(ClassicChairBlock.class, "secondary", "base", PaladinFurnitureMod.furnitureEntryMap.get(ClassicChairBlock.class).getVariants(), exporter);

        FurnitureBlock[] froggyChairs = FroggyChairBlock.streamFroggyChair().toList().toArray(new FurnitureBlock[0]);
        for (FurnitureBlock froggyChair : froggyChairs) {
            if (!generatedRecipes.contains(getId(froggyChair.getBlock()))) {
                offerFroggyChairRecipe(froggyChair.getBlock(), Ingredient.ofItems(froggyChair.getFroggyChairMaterial().asItem()), exporter);
                generatedRecipes.add(getId(froggyChair.getBlock()));
            }
        }
        FurnitureBlock[] woolClassicChairs = ClassicChairDyeableBlock.streamWoodDyeableChair().toList().toArray(new FurnitureBlock[0]);
        for (FurnitureBlock classicChair : woolClassicChairs) {
            if (!generatedRecipes.contains(getId(classicChair.getBlock()))) {
                offerClassicChairDyedRecipe(classicChair.getBlock(), Ingredient.ofItems(Items.OAK_LOG), Ingredient.ofItems(classicChair.getArmChairMaterial()), exporter);
                generatedRecipes.add(getId(classicChair.getBlock()));
            }
        }
        offerModernChairRecipe(ModernChairBlock.class, "secondary", "base", PaladinFurnitureMod.furnitureEntryMap.get(ModernChairBlock.class).getVariants(), exporter);

        FurnitureBlock[] armChairs = ArmChairColoredBlock.streamArmChairColored().toList().toArray(new FurnitureBlock[0]);
        for (FurnitureBlock armChair : armChairs) {
            if (!generatedRecipes.contains(getId(armChair.getBlock()))) {
                offerArmChairRecipe(armChair.getBlock(), Ingredient.ofItems(Items.OAK_LOG), Ingredient.ofItems(armChair.getArmChairMaterial().asItem()), exporter);
                generatedRecipes.add(getId(armChair.getBlock()));
            }
        }
        FurnitureBlock[] simpleSofas = SimpleSofaBlock.streamSimpleSofas().toList().toArray(new FurnitureBlock[0]);
        for (FurnitureBlock sofa : simpleSofas) {
            if (!generatedRecipes.contains(getId(sofa.getBlock()))) {
                offerSimpleSofaRecipe(sofa.getBlock(), Ingredient.ofItems(Items.OAK_LOG), Ingredient.ofItems(sofa.getArmChairMaterial().asItem()), exporter);
                generatedRecipes.add(getId(sofa.getBlock()));
            }
        }
        FurnitureBlock[] leatherArmChairs =  ArmChairBlock.streamArmChairs().toList().toArray(new FurnitureBlock[0]);
        for (FurnitureBlock armChair : leatherArmChairs) {
            if (!generatedRecipes.contains(getId(armChair.getBlock()))) {
                offerArmChairRecipe(armChair.getBlock(), Ingredient.ofItems(Items.OAK_LOG), Ingredient.ofItems(armChair.getArmChairMaterial().asItem()), exporter);
                generatedRecipes.add(getId(armChair.getBlock()));
            }
        }

        offerBasicTableRecipe(BasicTableBlock.class, "secondary", "base", PaladinFurnitureMod.furnitureEntryMap.get(BasicTableBlock.class).getVariants(), exporter);
        offerClassicTableRecipe(ClassicTableBlock.class, "secondary", "base", PaladinFurnitureMod.furnitureEntryMap.get(ClassicTableBlock.class).getVariants(), exporter);
        offerLogTableRecipe(LogTableBlock.class, "secondary", "base", PaladinFurnitureMod.furnitureEntryMap.get(LogTableBlock.class).getVariants(), exporter);
        offerLogTableRecipe(RawLogTableBlock.class, "secondary", "secondary", PaladinFurnitureMod.furnitureEntryMap.get(RawLogTableBlock.class).getVariants(), exporter);
        offerDinnerTableRecipe(DinnerTableBlock.class, "secondary", "base", PaladinFurnitureMod.furnitureEntryMap.get(DinnerTableBlock.class).getVariants(), exporter);
        offerModernDinnerTableRecipe(ModernDinnerTableBlock.class, "secondary", "base", PaladinFurnitureMod.furnitureEntryMap.get(ModernDinnerTableBlock.class).getVariants(), exporter);
        offerClassicNightStandRecipe(ClassicNightstandBlock.class, "secondary", "base", PaladinFurnitureMod.furnitureEntryMap.get(ClassicNightstandBlock.class).getVariants(), exporter);


        for (Block block : getVanillaBeds()) {
            offerSimpleBedRecipe(SimpleBedBlock.class, "base", PaladinFurnitureMod.furnitureEntryMap.get(SimpleBedBlock.class).getVariants(), Ingredient.ofItems(block), exporter);
            offerClassicBedRecipe(ClassicBedBlock.class, "base", PaladinFurnitureMod.furnitureEntryMap.get(ClassicBedBlock.class).getVariants(), Ingredient.ofItems(block), "fence", exporter);
        }

        offerSimpleBunkLadderRecipe(SimpleBunkLadderBlock.class, "base", PaladinFurnitureMod.furnitureEntryMap.get(SimpleBunkLadderBlock.class).getVariants(), exporter);

        offerLogStoolRecipe(LogStoolBlock.class, "secondary", PaladinFurnitureMod.furnitureEntryMap.get(LogStoolBlock.class).getVariants(), exporter);
        offerSimpleStoolRecipe(SimpleStoolBlock.class, "secondary", "base", PaladinFurnitureMod.furnitureEntryMap.get(SimpleStoolBlock.class).getVariants(), exporter);
        offerClassicStoolRecipe(ClassicStoolBlock.class, "secondary", "base", PaladinFurnitureMod.furnitureEntryMap.get(ClassicStoolBlock.class).getVariants(), exporter);
        offerModernStoolRecipe(ModernStoolBlock.class, "secondary", "base", PaladinFurnitureMod.furnitureEntryMap.get(ModernStoolBlock.class).getVariants(), exporter);
        offerCounterRecipe(KitchenCounterBlock.class, "secondary", "base", PaladinFurnitureMod.furnitureEntryMap.get(KitchenCounterBlock.class).getVariants(), exporter);
        offerKitchenSinkRecipe(KitchenSinkBlock.class, "secondary", "base", PaladinFurnitureMod.furnitureEntryMap.get(KitchenSinkBlock.class).getVariants(), Ingredient.ofItems(Items.BUCKET), Ingredient.ofItems(Items.IRON_INGOT), exporter);
        offerCounterApplianceRecipe(KitchenDrawerBlock.class, "secondary", "base", PaladinFurnitureMod.furnitureEntryMap.get(KitchenDrawerBlock.class).getVariants(), Ingredient.ofItems(Items.CHEST), exporter);
        offerCounterApplianceRecipe(KitchenCounterOvenBlock.class, "secondary", "base", PaladinFurnitureMod.furnitureEntryMap.get(KitchenCounterOvenBlock.class).getVariants(), Ingredient.ofItems(Items.FURNACE), exporter);
        offerCounterRecipe(KitchenWallCounterBlock.class, "base", "base", PaladinFurnitureMod.furnitureEntryMap.get(KitchenWallCounterBlock.class).getVariants(), exporter);
        offerWallDrawerRecipe(KitchenWallDrawerBlock.class, "secondary", "base", PaladinFurnitureMod.furnitureEntryMap.get(KitchenWallDrawerBlock.class).getVariants(), Ingredient.ofItems(Items.CHEST), exporter);
        offerWallDrawerSmallRecipe(KitchenWallDrawerSmallBlock.class, "secondary", "base", PaladinFurnitureMod.furnitureEntryMap.get(KitchenWallDrawerSmallBlock.class).getVariants(), Ingredient.ofItems(Items.CHEST), exporter);
        offerCabinetRecipe(KitchenCabinetBlock.class, "secondary", "base", PaladinFurnitureMod.furnitureEntryMap.get(KitchenCabinetBlock.class).getVariants(), Ingredient.ofItems(Items.CHEST), exporter);

        FurnitureBlock[] herringbonePlanks = HerringbonePlankBlock.streamPlanks().toList().toArray(new FurnitureBlock[0]);
        for (FurnitureBlock herringbonePlank : herringbonePlanks) {
            if (!generatedRecipes.contains(getId(herringbonePlank.getBlock()))) {
                offerHerringbonePlanks(herringbonePlank.getBlock(), herringbonePlank.getSlab().asItem(), exporter);
            }
        }
        FurnitureBlock[] fridges = FridgeBlock.streamFridges().toList().toArray(new FurnitureBlock[0]);
        for (FurnitureBlock fridge : fridges) {
            if (!generatedRecipes.contains(getId(fridge.getBlock()))) {
                offerFridgeRecipe(fridge.getBlock(), Ingredient.ofItems(fridge.getFridgeMaterial().asItem()), Ingredient.ofItems(Items.CHEST), exporter);
                generatedRecipes.add(getId(fridge.getBlock()));
            }
        }
        FurnitureBlock[] freezers = FreezerBlock.streamFreezers().toList().toArray(new FurnitureBlock[0]);
        for (FurnitureBlock freezer : freezers) {
            if (!generatedRecipes.contains(getId(freezer.getBlock()))) {
                offerFreezerRecipe(freezer.getBlock(), Ingredient.ofItems(freezer.getFridgeMaterial().asItem()), exporter);
                generatedRecipes.add(getId(freezer.getBlock()));
            }
        }
        FurnitureBlock[] microwaves = MicrowaveBlock.streamMicrowaves().toList().toArray(new FurnitureBlock[0]);
        for (FurnitureBlock microwave : microwaves) {
            if (!generatedRecipes.contains(getId(microwave.getBlock()))) {
                offerMicrowaveRecipe(microwave.getBlock(),  Ingredient.ofItems(microwave.getFridgeMaterial().asItem()), Ingredient.ofItems(Items.FURNACE), exporter);
                generatedRecipes.add(getId(microwave.getBlock()));
            }
        }
        FurnitureBlock[] rangeHoods = KitchenRangeHoodBlock.streamOvenRangeHoods().toList().toArray(new FurnitureBlock[0]);
        for (FurnitureBlock rangeHood : rangeHoods) {
            if (!generatedRecipes.contains(getId(rangeHood.getBlock()))) {
                offerRangeHoodRecipe(rangeHood.getBlock(),  Ingredient.ofItems(rangeHood.getFridgeMaterial().asItem()), Ingredient.ofItems(Items.REDSTONE_LAMP), exporter);
                generatedRecipes.add(getId(rangeHood.getBlock()));
            }
        }
        FurnitureBlock[] stoves = StoveBlock.streamStoves().toList().toArray(new FurnitureBlock[0]);
        for (FurnitureBlock stove : stoves) {
            if (!generatedRecipes.contains(getId(stove.getBlock()))) {
                offerStoveRecipe(stove.getBlock(),  Ingredient.ofItems(stove.getFridgeMaterial().asItem()), Ingredient.ofItems(Items.FURNACE), exporter);
                generatedRecipes.add(getId(stove.getBlock()));
            }
        }
        FurnitureBlock[] ironStove = IronStoveBlock.streamIronStoves().toList().toArray(new FurnitureBlock[0]);
        for (FurnitureBlock stove : ironStove) {
            if (!generatedRecipes.contains(getId(stove.getBlock()))) {
                offerStoveRecipe(stove.getBlock(),  Ingredient.ofItems(stove.getFridgeMaterial().asItem()), Ingredient.ofItems(Items.FURNACE), exporter);
                generatedRecipes.add(getId(stove.getBlock()));
            }
        }
        KitchenStovetopBlock[] stovetopBlocks = KitchenStovetopBlock.streamKitchenStovetop().toList().toArray(new KitchenStovetopBlock[0]);
        for (KitchenStovetopBlock stove : stovetopBlocks) {
            if (!generatedRecipes.contains(getId(stove))) {
                offerStovetopRecipe(stove, Ingredient.ofItems(Items.IRON_INGOT), Ingredient.ofItems(Blocks.GRAY_CONCRETE), exporter);
                generatedRecipes.add(getId(stove));
            }
        }
        FurnitureBlock[] plates = PlateBlock.streamPlates().toList().toArray(new FurnitureBlock[0]);
        for (FurnitureBlock plate : plates) {
            if (!generatedRecipes.contains(getId(plate.getBlock()))) {
                offerPlateRecipe(plate.getBlock(), Ingredient.ofItems(plate.getPlateMaterial()), Ingredient.ofItems(Items.ITEM_FRAME), Ingredient.ofItems(plate.getPlateDecoration()), exporter);
                generatedRecipes.add(getId(plate.getBlock()));
            }
        }
        FurnitureBlock[] cutleries = CutleryBlock.streamCutlery().toList().toArray(new FurnitureBlock[0]);
        for (FurnitureBlock cutlery : cutleries) {
            if (!generatedRecipes.contains(getId(cutlery.getBlock()))) {
                offerCutleryRecipe(cutlery.getBlock(), Ingredient.ofItems(cutlery.getCutleryMaterial()), exporter);
                generatedRecipes.add(getId(cutlery.getBlock()));
            }
        }
        PaladinFurnitureMod.furnitureEntryMap.get(PendantBlock.class).getAllBlocks().forEach((block) -> {
            if (!generatedRecipes.contains(getId(block))) {
                Block hang = Blocks.LIGHT_GRAY_CONCRETE;
                Block base;
                if (block == PaladinFurnitureModBlocksItems.GLASS_MODERN_PENDANT)
                    base = Blocks.WHITE_STAINED_GLASS;
                else if (block == PaladinFurnitureModBlocksItems.WHITE_MODERN_PENDANT)
                    base = Blocks.WHITE_CONCRETE;
                else
                    base = Blocks.GRAY_CONCRETE;
                offerPendantRecipe(block.asItem(), Ingredient.ofItems(base), Ingredient.ofItems(hang), exporter);
                generatedRecipes.add(getId(block));
            }
        });
        if (!generatedRecipes.contains(Registries.ITEM.getId(PaladinFurnitureModBlocksItems.LIGHT_SWITCH_ITEM))) {
            SimpleFurnitureRecipeJsonFactory.create(PaladinFurnitureModBlocksItems.LIGHT_SWITCH_ITEM.getDefaultStack().copyWithCount(6)).input(Blocks.WHITE_CONCRETE, 6).input(Blocks.LIGHT_GRAY_CONCRETE, 2).input(Items.REDSTONE).offerTo(exporter, Identifier.of("pfm", PaladinFurnitureModBlocksItems.LIGHT_SWITCH_ITEM.getTranslationKey().replace("block.pfm.", "")));
            generatedRecipes.add(Registries.ITEM.getId(PaladinFurnitureModBlocksItems.LIGHT_SWITCH_ITEM));
        }

        FurnitureBlock[] basicToilets = BasicToiletBlock.streamBasicToilet().toList().toArray(new FurnitureBlock[0]);
        for (FurnitureBlock toilet : basicToilets) {
            if (!generatedRecipes.contains(getId(toilet.getBlock()))) {
                offerToiletRecipe(toilet.getBlock(), Ingredient.ofItems(Items.STONE_BUTTON), Ingredient.ofItems(Blocks.QUARTZ_BLOCK), exporter);
                generatedRecipes.add(getId(toilet.getBlock()));
            }
        }

        if (!generatedRecipes.contains(getId(PaladinFurnitureModBlocksItems.WALL_TOILET_PAPER))) {
            offerWallToiletPaperRecipe(PaladinFurnitureModBlocksItems.WALL_TOILET_PAPER,  Ingredient.ofItems(Blocks.STONE), exporter);
            generatedRecipes.add(getId(PaladinFurnitureModBlocksItems.WALL_TOILET_PAPER));
        }

        if (!generatedRecipes.contains(getId(PaladinFurnitureModBlocksItems.BASIC_SINK))) {
            offerSinkRecipe(PaladinFurnitureModBlocksItems.BASIC_SINK, Ingredient.ofItems(Blocks.QUARTZ_BLOCK), exporter);
            generatedRecipes.add(getId(PaladinFurnitureModBlocksItems.BASIC_SINK));
        }

        if (!generatedRecipes.contains(getId(PaladinFurnitureModBlocksItems.BASIC_BATHTUB))) {
            offerBathtubRecipe(PaladinFurnitureModBlocksItems.BASIC_BATHTUB, Ingredient.ofItems(Blocks.QUARTZ_BLOCK), exporter);
            generatedRecipes.add(getId(PaladinFurnitureModBlocksItems.BASIC_BATHTUB));
        }

        if (!generatedRecipes.contains(getId(PaladinFurnitureModBlocksItems.BASIC_SHOWER_HEAD))) {
            offerShowerHeadRecipe(PaladinFurnitureModBlocksItems.BASIC_SHOWER_HEAD, Ingredient.ofItems(Items.WATER_BUCKET), exporter);
            offerShowerHandleRecipe(PaladinFurnitureModBlocksItems.BASIC_SHOWER_HANDLE_ITEM.getDefaultStack(), Ingredient.ofItems(Blocks.LEVER), exporter);
            generatedRecipes.add(getId(PaladinFurnitureModBlocksItems.BASIC_SHOWER_HEAD));
            generatedRecipes.add(Registries.ITEM.getId(PaladinFurnitureModBlocksItems.BASIC_SHOWER_HANDLE_ITEM));
        }

        if (!generatedRecipes.contains(getId(PaladinFurnitureModBlocksItems.MESH_TRASHCAN))) {
            SimpleFurnitureRecipeJsonFactory.create(PaladinFurnitureModBlocksItems.MESH_TRASHCAN, 1).input(Items.IRON_INGOT, 1).input(Items.ENDER_PEARL, 1).input(Blocks.IRON_BARS, 4).offerTo(exporter, Identifier.of("pfm", PaladinFurnitureModBlocksItems.MESH_TRASHCAN.asItem().getTranslationKey().replace("block.pfm.", "")));
            generatedRecipes.add(getId(PaladinFurnitureModBlocksItems.MESH_TRASHCAN));
        }
        if (!generatedRecipes.contains(getId(PaladinFurnitureModBlocksItems.TRASHCAN))) {
            SimpleFurnitureRecipeJsonFactory.create(PaladinFurnitureModBlocksItems.TRASHCAN, 1).input(Items.IRON_INGOT, 1).input(Items.ENDER_PEARL, 1).input(Blocks.IRON_BARS, 4).offerTo(exporter, Identifier.of("pfm", PaladinFurnitureModBlocksItems.TRASHCAN.asItem().getTranslationKey().replace("block.pfm.", "")));
            generatedRecipes.add(getId(PaladinFurnitureModBlocksItems.TRASHCAN));
        }

        FurnitureBlock[] showerTowels = ShowerTowelBlock.streamShowerTowels().toList().toArray(new FurnitureBlock[0]);
        for (FurnitureBlock towel : showerTowels) {
            if (!generatedRecipes.contains(getId(towel.getBlock()))) {
                offerShowerTowelRecipe(towel.getBlock(),  Ingredient.ofItems(towel.getWoolColor()), exporter);
                generatedRecipes.add(getId(towel.getBlock()));
            }
        }

        FurnitureBlock[] mirrors = MirrorBlock.streamMirrorBlocks().toList().toArray(new FurnitureBlock[0]);
        for (FurnitureBlock mirror : mirrors) {
            if (!generatedRecipes.contains(getId(mirror.getBlock()))) {
                offerMirrorRecipe(mirror.getBlock(), Ingredient.ofItems(mirror.getBaseMaterial()), exporter);
                generatedRecipes.add(getId(mirror.getBlock()));
            }
        }

        offerLampRecipes(exporter);

        offerBasicCoffeeTableRecipe(BasicCoffeeTableBlock.class, "secondary", "base", PaladinFurnitureMod.furnitureEntryMap.get(BasicCoffeeTableBlock.class).getVariants(), exporter);
        offerModernCoffeeTableRecipe(ModernCoffeeTableBlock.class, "secondary", "base", PaladinFurnitureMod.furnitureEntryMap.get(ModernCoffeeTableBlock.class).getVariants(), exporter);
        offerClassicCoffeeTableRecipe(ClassicCoffeeTableBlock.class, "secondary", "base", PaladinFurnitureMod.furnitureEntryMap.get(ClassicCoffeeTableBlock.class).getVariants(), exporter);

        PaladinFurnitureMod.pfmModCompatibilities.forEach(pfmModCompatibility -> pfmModCompatibility.generateRecipes(exporter));
    }

    public static void offerLampRecipes(RecipeExporter exporter) {
        for (DyeColor color : DyeColor.values()) {
            ComponentChanges.Builder builder = ComponentChanges.builder();
            builder.add(PFMComponents.COLOR_COMPONENT, color);
            DynamicFurnitureRecipeJsonFactory.create(BasicLampBlock.class, 1,  WoodVariantRegistry.getVariants().stream().map(woodVariant -> woodVariant.identifier).toList(), builder.build()).vanillaInput(ModelHelper.getWoolColor(color.asString()), 3).vanillaInput(Items.TORCH).vanillaInput(Items.REDSTONE).childInput("stripped_log", 2).offerTo(exporter, Identifier.of("pfm", String.format("basic_%s_lamp", color.asString())));
        }
    }

    public static Pair<Block, Block> getCounterMaterials(VariantBase<?> variantBase) {
        Block counterTop = variantBase.getSecondaryBlock();
        Block counterBase = variantBase.getBaseBlock();

        if (variantBase.identifier.getPath().equals("calcite") || variantBase.identifier.getPath().equals("netherite")) {
            Block temp = counterBase;
            counterBase = counterTop;
            counterTop  = temp;
        }
        return new Pair<>(counterBase, counterTop);
    }
    public Block getVanillaBed(Block block) {
        if (block instanceof SimpleBedBlock){
            String color = ((SimpleBedBlock) block).getPFMColor().getName();
            return Registries.BLOCK.get(Identifier.of("minecraft:" + color + "_bed"));
        }
        return null;
    }

    public List<Block> getVanillaBeds() {
        List<Block> beds = new ArrayList<>();
        Registries.BLOCK.stream().forEach(block -> {
            if (block instanceof BedBlock && Registries.BLOCK.getId(block).getNamespace().equals("minecraft"))
                beds.add(block);
        });
        return beds;
    }

    public static void offerBasicChairRecipe(Class<? extends Block> output, String legMaterial, String baseMaterial, List<Identifier> variants, RecipeExporter exporter) {
        DynamicFurnitureRecipeJsonFactory.create(output, 4, variants).group("chairs").childInput(legMaterial, 2).childInput(baseMaterial, 4).offerTo(exporter, Identifier.of("pfm", output.getSimpleName().replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase(Locale.US)));
    }

    public static void offerFroggyChairRecipe(ItemConvertible output, Ingredient baseMaterial, RecipeExporter exporter) {
        SimpleFurnitureRecipeJsonFactory.create(output, 4).group("chairs").criterion("has_concrete", conditionsFromIngredient(baseMaterial)).input(baseMaterial, 6).offerTo(exporter, Identifier.of("pfm", output.asItem().getTranslationKey().replace("block.pfm.", "")));
    }

    public static void offerDinnerChairRecipe(Class<? extends Block> output, String legMaterial, String baseMaterial, List<Identifier> variants, RecipeExporter exporter) {
        DynamicFurnitureRecipeJsonFactory.create(output, 4, variants).group("chairs").childInput(legMaterial, 3).childInput(baseMaterial, 3).offerTo(exporter, Identifier.of("pfm", output.getSimpleName().replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase(Locale.US)));
    }

    public static void offerClassicChairDyedRecipe(ItemConvertible output, Ingredient legMaterial, Ingredient baseMaterial, RecipeExporter exporter) {
        SimpleFurnitureRecipeJsonFactory.create(output, 4).group("chairs").criterion(getCriterionNameFromOutput(output), conditionsFromIngredient(baseMaterial)).input(legMaterial, 4).input(baseMaterial, 2).offerTo(exporter, Identifier.of("pfm", output.asItem().getTranslationKey().replace("block.pfm.", "")));
    }

    public static void offerClassicChairRecipe(Class<? extends Block> output, String legMaterial, String baseMaterial, List<Identifier> variants, RecipeExporter exporter) {
        DynamicFurnitureRecipeJsonFactory.create(output, 4, variants).group("chairs").childInput(legMaterial, 4).childInput(baseMaterial, 2).offerTo(exporter, Identifier.of("pfm", output.getSimpleName().replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase(Locale.US)));
    }

    public static void offerModernChairRecipe(Class<? extends Block> output, String legMaterial, String baseMaterial, List<Identifier> variants, RecipeExporter exporter) {
        DynamicFurnitureRecipeJsonFactory.create(output, 4, variants).group("chairs").childInput(legMaterial, 3).childInput(baseMaterial, 3).offerTo(exporter, Identifier.of("pfm", output.getSimpleName().replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase(Locale.US)));
    }
    public static void offerArmChairRecipe(ItemConvertible output, Ingredient baseMaterial, Ingredient legMaterial, RecipeExporter exporter) {
        SimpleFurnitureRecipeJsonFactory.create(output, 2).group("chairs").criterion("has_wool", conditionsFromIngredient(baseMaterial)).input(legMaterial, 4).input(baseMaterial, 2).offerTo(exporter, Identifier.of("pfm", output.asItem().getTranslationKey().replace("block.pfm.", "")));
    }

    public static void offerSimpleSofaRecipe(ItemConvertible output, Ingredient baseMaterial, Ingredient legMaterial, RecipeExporter exporter) {
        SimpleFurnitureRecipeJsonFactory.create(output, 2).group("chairs").criterion("has_wool", conditionsFromIngredient(baseMaterial)).input(legMaterial, 2).input(baseMaterial, 4).offerTo(exporter, Identifier.of("pfm", output.asItem().getTranslationKey().replace("block.pfm.", "")));
    }

    public static void offerBasicTableRecipe(Class<? extends Block> output, String legMaterial, String baseMaterial, List<Identifier> variants, RecipeExporter exporter) {
        DynamicFurnitureRecipeJsonFactory.create(output, 4, variants).group("tables").childInput(legMaterial, 5).childInput(baseMaterial, 3).offerTo(exporter, Identifier.of("pfm", output.getSimpleName().replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase(Locale.US)));
    }

    public static void offerBasicCoffeeTableRecipe(Class<? extends Block> output, String legMaterial, String baseMaterial, List<Identifier> variants, RecipeExporter exporter) {
        DynamicFurnitureRecipeJsonFactory.create(output, 4, variants).group("tables").childInput(legMaterial, 3).childInput(baseMaterial, 3).offerTo(exporter, Identifier.of("pfm", output.getSimpleName().replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase(Locale.US)));
    }

    public static void offerModernCoffeeTableRecipe(Class<? extends Block> output, String legMaterial, String baseMaterial, List<Identifier> variants, RecipeExporter exporter) {
        DynamicFurnitureRecipeJsonFactory.create(output, 4, variants).group("tables").childInput(legMaterial, 4).childInput(baseMaterial, 3).offerTo(exporter, Identifier.of("pfm", output.getSimpleName().replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase(Locale.US)));
    }

    public static void offerClassicCoffeeTableRecipe(Class<? extends Block> output, String legMaterial, String baseMaterial, List<Identifier> variants, RecipeExporter exporter) {
        DynamicFurnitureRecipeJsonFactory.create(output, 4, variants).group("tables").childInput(legMaterial, 2).childInput(baseMaterial, 3).offerTo(exporter, Identifier.of("pfm", output.getSimpleName().replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase(Locale.US)));
    }

    public static void offerClassicTableRecipe(Class<? extends Block> output, String legMaterial, String baseMaterial, List<Identifier> variants, RecipeExporter exporter) {
        DynamicFurnitureRecipeJsonFactory.create(output, 4, variants).group("tables").childInput(legMaterial, 4).childInput(baseMaterial, 3).offerTo(exporter, Identifier.of("pfm", output.getSimpleName().replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase(Locale.US)));
    }

    public static void offerLogTableRecipe(Class<? extends Block> output, String legMaterial, String baseMaterial, List<Identifier> variants, RecipeExporter exporter) {
        DynamicFurnitureRecipeJsonFactory.create(output, 4, variants).group("tables").childInput(legMaterial, 2).childInput(baseMaterial, 3).offerTo(exporter, Identifier.of("pfm", output.getSimpleName().replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase(Locale.US)));
    }

    public static void offerHerringbonePlanks(ItemConvertible output, Item baseMaterial, RecipeExporter exporter) {
        ShapedRecipeJsonBuilder.create(createWrapperLookup().getOrThrow(RegistryKeys.ITEM), RecipeCategory.BUILDING_BLOCKS, output, 4).input('X', baseMaterial).pattern("XX").pattern("XX").criterion("has_wood_slabs", conditionsFromItem(baseMaterial)).offerTo(exporter, RegistryKey.of(RegistryKeys.RECIPE ,Identifier.of("pfm", output.asItem().getTranslationKey().replace("block.pfm.", ""))));
    }

    public static void offerDinnerTableRecipe(Class<? extends Block> output, String legMaterial, String baseMaterial, List<Identifier> variants, RecipeExporter exporter) {
        DynamicFurnitureRecipeJsonFactory.create(output, 4, variants).group("tables").childInput(legMaterial, 3).childInput(baseMaterial, 3).offerTo(exporter, Identifier.of("pfm", output.getSimpleName().replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase(Locale.US)));
    }

    public static void offerModernDinnerTableRecipe(Class<? extends Block> output, String legMaterial, String baseMaterial, List<Identifier> variants, RecipeExporter exporter) {
        DynamicFurnitureRecipeJsonFactory.create(output, 4, variants).group("tables").childInput(legMaterial, 5).childInput(baseMaterial, 3).offerTo(exporter, Identifier.of("pfm", output.getSimpleName().replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase(Locale.US)));
    }

    public static void offerClassicNightStandRecipe(Class<? extends Block> output, String legMaterial, String baseMaterial, List<Identifier> variants, RecipeExporter exporter) {
        DynamicFurnitureRecipeJsonFactory.create(output, 4, variants).group("bedroom").childInput(legMaterial, 6).childInput(baseMaterial, 1).vanillaInput(Blocks.CHEST, 1).offerTo(exporter, Identifier.of("pfm", output.getSimpleName().replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase(Locale.US)));
    }

    public static String getCriterionNameFromOutput(ItemConvertible output) {
        return getCriterionNameFromOutput(output, "");
    }

    public static String getEmptyCriteria() {
        return "";
    }

    public static String getCriterionNameFromOutput(ItemConvertible output, String type) {
        if (Block.getBlockFromItem(output.asItem()) == null || Block.getBlockFromItem(output.asItem()) == Blocks.AIR || !PaladinFurnitureMod.furnitureEntryMap.containsKey(Block.getBlockFromItem(output.asItem()).getClass())) {
            return getItemPath(output);
        }
        if (PaladinFurnitureMod.furnitureEntryMap.get(Block.getBlockFromItem(output.asItem()).getClass()).getVariantFromEntry(Block.getBlockFromItem(output.asItem())) instanceof WoodVariant) {
            return type.isEmpty() ? "has_planks" : type;
        }
        else return getItemPath(output);
    }

    public static void offerSimpleBedRecipe(Class<? extends Block> output, String legMaterial, List<Identifier> variants, Ingredient baseBed, RecipeExporter exporter) {
        DyeColor color = ((BedBlock)((BlockItem)(baseBed.getMatchingItems().stream().findFirst().get().value())).getBlock()).getColor();
        ComponentChanges.Builder builder = ComponentChanges.builder();
        builder.add(PFMComponents.COLOR_COMPONENT, color);
        DynamicFurnitureRecipeJsonFactory.create(output, 1, variants, builder.build()).group("bedroom").childInput(legMaterial, 5).vanillaInput(baseBed, 1).offerTo(exporter, Identifier.of("pfm", output.getSimpleName().replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase(Locale.US) + "_"+ color.asString()));
    }

    public static void offerClassicBedRecipe(Class<? extends Block> output, String legMaterial, List<Identifier> variants, Ingredient baseBed, String fence, RecipeExporter exporter) {
        DyeColor color = ((BedBlock)((BlockItem)(baseBed.getMatchingItems().stream().findFirst().get().value())).getBlock()).getColor();
        ComponentChanges.Builder builder = ComponentChanges.builder();
        builder.add(PFMComponents.COLOR_COMPONENT, color);
        DynamicFurnitureRecipeJsonFactory.create(output, 1, variants, builder.build()).group("bedroom").childInput(legMaterial, 3).childInput(fence, 2).vanillaInput(baseBed, 1).offerTo(exporter, Identifier.of("pfm", output.getSimpleName().replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase(Locale.US) + "_"+ color.asString()));
    }

    public static void offerSimpleBunkLadderRecipe(Class<? extends Block> output, String base, List<Identifier> variants, RecipeExporter exporter) {
        DynamicFurnitureRecipeJsonFactory.create(output, 4, variants).group("bedroom").childInput(base, 1).vanillaInput(Ingredient.ofItems(Items.STICK), 6).offerTo(exporter, Identifier.of("pfm", output.getSimpleName().replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase(Locale.US)));
    }

    public static void offerLogStoolRecipe(Class<? extends Block> output, String legMaterial, List<Identifier> variants, RecipeExporter exporter) {
        DynamicFurnitureRecipeJsonFactory.create(output, 4, variants).group("stools").childInput(legMaterial, 1).offerTo(exporter, Identifier.of("pfm", output.getSimpleName().replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase(Locale.US)));
    }

    public static void offerSimpleStoolRecipe(Class<? extends Block> output, String legMaterial, String baseMaterial, List<Identifier> variants, RecipeExporter exporter) {
        DynamicFurnitureRecipeJsonFactory.create(output, 4, variants).group("stools").childInput(legMaterial, 2).childInput(baseMaterial, 3).offerTo(exporter, Identifier.of("pfm", output.getSimpleName().replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase(Locale.US)));
    }

    public static void offerClassicStoolRecipe(Class<? extends Block> output, String legMaterial, String baseMaterial, List<Identifier> variants, RecipeExporter exporter) {
        DynamicFurnitureRecipeJsonFactory.create(output, 4, variants).group("stools").childInput(legMaterial, 3).childInput(baseMaterial, 2).offerTo(exporter, Identifier.of("pfm", output.getSimpleName().replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase(Locale.US)));
    }

    public static void offerModernStoolRecipe(Class<? extends Block> output, String legMaterial, String baseMaterial, List<Identifier> variants, RecipeExporter exporter) {
        DynamicFurnitureRecipeJsonFactory.create(output, 4, variants).group("stools").childInput(legMaterial, 1).childInput(baseMaterial, 3).offerTo(exporter, Identifier.of("pfm", output.getSimpleName().replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase(Locale.US)));
    }

    public static void offerCounterRecipe(Class<? extends Block> output, String legMaterial, String baseMaterial, List<Identifier> variants, RecipeExporter exporter) {
        DynamicFurnitureRecipeJsonFactory.create(output, 6, variants).group("kitchen").childInput(legMaterial, 3).childInput(baseMaterial, 6).offerTo(exporter, Identifier.of("pfm", output.getSimpleName().replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase(Locale.US)));
    }

    public static void offerCounterApplianceRecipe(Class<? extends Block> output, String legMaterial, String baseMaterial, List<Identifier> variants, Ingredient appliance, RecipeExporter exporter) {
        DynamicFurnitureRecipeJsonFactory.create(output, 1, variants).group("kitchen").childInput(legMaterial, 3).childInput(baseMaterial, 5).vanillaInput(appliance).offerTo(exporter, Identifier.of("pfm", output.getSimpleName().replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase(Locale.US)));
    }

    public static void offerKitchenSinkRecipe(Class<? extends Block> output, String legMaterial, String baseMaterial, List<Identifier> variants, Ingredient center, Ingredient ingot, RecipeExporter exporter) {
        DynamicFurnitureRecipeJsonFactory.create(output, 1, variants).group("kitchen").childInput(legMaterial, 2).childInput(baseMaterial, 5).vanillaInput(ingot).vanillaInput(center).offerTo(exporter, Identifier.of("pfm", output.getSimpleName().replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase(Locale.US)));
    }

    public static void offerWallDrawerRecipe(Class<? extends Block> output, String legMaterial, String baseMaterial, List<Identifier> variants, Ingredient appliace, RecipeExporter exporter) {
        DynamicFurnitureRecipeJsonFactory.create(output, 1, variants).group("kitchen").childInput(legMaterial, 6).childInput(baseMaterial, 2).vanillaInput(appliace).offerTo(exporter, Identifier.of("pfm", output.getSimpleName().replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase(Locale.US)));
    }

    public static void offerWallDrawerSmallRecipe(Class<? extends Block> output, String legMaterial, String baseMaterial, List<Identifier> variants, Ingredient appliance, RecipeExporter exporter) {
        DynamicFurnitureRecipeJsonFactory.create(output, 3, variants).group("kitchen").childInput(legMaterial, 3).childInput(baseMaterial, 2).vanillaInput(appliance).offerTo(exporter, Identifier.of("pfm", output.getSimpleName().replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase(Locale.US)));
    }

    public static void offerCabinetRecipe(Class<? extends Block> output, String legMaterial, String baseMaterial, List<Identifier> variants, Ingredient chest, RecipeExporter exporter) {
        DynamicFurnitureRecipeJsonFactory.create(output, 3, variants).group("kitchen").childInput(legMaterial, 6).childInput(baseMaterial, 2).vanillaInput(chest).offerTo(exporter, Identifier.of("pfm", output.getSimpleName().replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase(Locale.US)));
    }

    public static void offerFridgeRecipe(ItemConvertible output, Ingredient legMaterial, Ingredient storage, RecipeExporter exporter) {
        if (output.asItem().toString().contains("xbox")) {
            SimpleFurnitureRecipeJsonFactory.create(output, 1).group("kitchen").criterion("has_" + getItemPath(legMaterial), conditionsFromIngredient(legMaterial)).input(legMaterial, 6).input(storage, 1).input(Ingredient.ofItems(Items.REDSTONE)).input(Ingredient.ofItems(Items.WHITE_CONCRETE)).offerTo(exporter, Identifier.of("pfm", output.asItem().getTranslationKey().replace("block.pfm.", "")));
        }
        else {
            SimpleFurnitureRecipeJsonFactory.create(output, 1).group("kitchen").criterion("has_" + getItemPath(legMaterial), conditionsFromIngredient(legMaterial)).input(legMaterial, 7).input(storage).offerTo(exporter, Identifier.of("pfm", output.asItem().getTranslationKey().replace("block.pfm.", "")));
        }
    }

    public static void offerFreezerRecipe(ItemConvertible output, Ingredient legMaterial, RecipeExporter exporter) {
        SimpleFurnitureRecipeJsonFactory.create(output, 1).group("kitchen").criterion("has_" + getItemPath(legMaterial), conditionsFromIngredient(legMaterial)).input(legMaterial, 7).input(Ingredient.ofItems(Items.REDSTONE), 2).offerTo(exporter, Identifier.of("pfm", output.asItem().getTranslationKey().replace("block.pfm.", "")));
    }

    public static void offerMicrowaveRecipe(ItemConvertible output, Ingredient legMaterial, Ingredient storage, RecipeExporter exporter) {
        SimpleFurnitureRecipeJsonFactory.create(output, 1).group("kitchen").criterion("has_" + getItemPath(legMaterial), conditionsFromIngredient(legMaterial)).input(legMaterial ,5).input(storage).input(Ingredient.ofItems(Items.REDSTONE)).offerTo(exporter, Identifier.of("pfm", output.asItem().getTranslationKey().replace("block.pfm.", "")));
    }

    public static void offerRangeHoodRecipe(ItemConvertible output, Ingredient legMaterial, Ingredient secondMaterial, RecipeExporter exporter) {
        SimpleFurnitureRecipeJsonFactory.create(output, 1).group("kitchen").criterion("has_" + getItemPath(legMaterial), conditionsFromIngredient(legMaterial)).input(legMaterial, 4).input(secondMaterial).offerTo(exporter, Identifier.of("pfm", output.asItem().getTranslationKey().replace("block.pfm.", "")));
    }

    public static void offerStoveRecipe(ItemConvertible output, Ingredient legMaterial, Ingredient storage, RecipeExporter exporter) {
        SimpleFurnitureRecipeJsonFactory.create(output, 1).group("kitchen").criterion("has_" + getItemPath(legMaterial), conditionsFromIngredient(legMaterial)).input(legMaterial, 8).input(storage).offerTo(exporter, Identifier.of("pfm", output.asItem().getTranslationKey().replace("block.pfm.", "")));
    }

    public static void offerStovetopRecipe(ItemConvertible output, Ingredient base, Ingredient material, RecipeExporter exporter) {
        SimpleFurnitureRecipeJsonFactory.create(output, 1).group("kitchen").criterion("has_" + getItemPath(base), conditionsFromIngredient(base)).input(base, 6).input(material, 2).input(Ingredient.ofItems(Items.FLINT_AND_STEEL)).offerTo(exporter, Identifier.of("pfm", output.asItem().getTranslationKey().replace("block.pfm.", "")));
    }

    public static void offerPlateRecipe(ItemConvertible output, Ingredient base, Ingredient frame, Ingredient decoration, RecipeExporter exporter) {
        SimpleFurnitureRecipeJsonFactory.create(output, 4).group("kitchen").criterion("has_" + getItemPath(base), conditionsFromIngredient(base)).input(base, 4).input(frame).input(decoration, 4).offerTo(exporter, Identifier.of("pfm", output.asItem().getTranslationKey().replace("block.pfm.", "")));
    }
    public static void offerCutleryRecipe(ItemConvertible output, Ingredient base, RecipeExporter exporter) {
        SimpleFurnitureRecipeJsonFactory.create(output, 4).group("kitchen").criterion("has_" + getItemPath(base), conditionsFromIngredient(base)).input(base, 4).offerTo(exporter, Identifier.of("pfm", output.asItem().getTranslationKey().replace("block.pfm.", "")));
    }

    public static void offerPendantRecipe(ItemConvertible output, Ingredient base,Ingredient hang, RecipeExporter exporter) {
        SimpleFurnitureRecipeJsonFactory.create(output, 4).group("lighting").criterion("has_" + getItemPath(base), conditionsFromIngredient(base)).input(base, 2).input(hang, 2).input(PaladinFurnitureModBlocksItems.SIMPLE_LIGHT).offerTo(exporter, Identifier.of("pfm", output.asItem().getTranslationKey().replace("block.pfm.", "")));
    }

    public static void offerToiletRecipe(ItemConvertible output, Ingredient base, Ingredient material, RecipeExporter exporter) {
        SimpleFurnitureRecipeJsonFactory.create(output, 1).group("bathroom").criterion("has_" + getItemPath(material), conditionsFromIngredient(material)).input(base).input(material, 4).input(Ingredient.ofItems(Items.BUCKET)).offerTo(exporter, Identifier.of("pfm", output.asItem().getTranslationKey().replace("block.pfm.", "")));
    }

    public static void offerWallToiletPaperRecipe(ItemConvertible output, Ingredient base, RecipeExporter exporter) {
        SimpleFurnitureRecipeJsonFactory.create(output, 1).group("bathroom").criterion("has_" + getItemPath(Items.PAPER), conditionsFromItem(Items.PAPER)).input(base, 1).input(Items.PAPER, 8).offerTo(exporter, Identifier.of("pfm", output.asItem().getTranslationKey().replace("block.pfm.", "")));
    }

    public static void offerSinkRecipe(ItemConvertible output, Ingredient base, RecipeExporter exporter) {
        SimpleFurnitureRecipeJsonFactory.create(output, 1).group("bathroom").criterion("has_" + getItemPath(base), conditionsFromIngredient(base)).input(base, 3).input(Items.STONE_BUTTON, 2).input(Items.IRON_INGOT, 1).input(Items.BUCKET, 1).offerTo(exporter, Identifier.of("pfm", output.asItem().getTranslationKey().replace("block.pfm.", "")));
    }

    public static void offerBathtubRecipe(ItemConvertible output, Ingredient base, RecipeExporter exporter) {
        SimpleFurnitureRecipeJsonFactory.create(output, 1).group("bathroom").criterion("has_" + getItemPath(base), conditionsFromIngredient(base)).input(base, 5).input(Items.STONE_BUTTON, 2).input(Items.BUCKET, 1).offerTo(exporter, Identifier.of("pfm", output.asItem().getTranslationKey().replace("block.pfm.", "")));
    }

    public static void offerShowerHeadRecipe(ItemConvertible output, Ingredient base, RecipeExporter exporter) {
        SimpleFurnitureRecipeJsonFactory.create(output, 1).group("bathroom").criterion("has_" + getItemPath(base), conditionsFromIngredient(base)).input(base, 1).input(Items.REDSTONE, 1).input(Items.IRON_INGOT, 1).offerTo(exporter, Identifier.of("pfm", output.asItem().getTranslationKey().replace("block.pfm.", "")));
    }

    public static void offerShowerHandleRecipe(ItemStack output, Ingredient base, RecipeExporter exporter) {
        SimpleFurnitureRecipeJsonFactory.create(output).group("bathroom").criterion("has_" + getItemPath(base), conditionsFromIngredient(base)).input(base, 2).input(Items.REDSTONE, 1).input(Items.IRON_INGOT, 1).offerTo(exporter, Identifier.of("pfm", output.getItem().getTranslationKey().replace("block.pfm.", "")));
    }

    public static void offerShowerTowelRecipe(ItemConvertible output, Ingredient base, RecipeExporter exporter) {
        SimpleFurnitureRecipeJsonFactory.create(output, 2).group("bathroom").criterion("has_" + getItemPath(base), conditionsFromIngredient(base)).input(base,4).input(Ingredient.ofItems(Items.LIGHT_GRAY_CONCRETE), 2).offerTo(exporter, Identifier.of("pfm", output.asItem().getTranslationKey().replace("block.pfm.", "")));
    }

    public static void offerMirrorRecipe(ItemConvertible output, Ingredient base, RecipeExporter exporter) {
        SimpleFurnitureRecipeJsonFactory.create(output, 2).group("bathroom").criterion("has_" + getItemPath(base), conditionsFromIngredient(base)).input(base,3).input(Ingredient.ofItems(Items.GLASS), 2).offerTo(exporter, Identifier.of("pfm", output.asItem().getTranslationKey().replace("block.pfm.", "")));
    }

    private static AdvancementCriterion<InventoryChangedCriterion.Conditions> conditionsFromItem(NumberRange.IntRange count, ItemConvertible item) {
        return conditionsFromItemPredicates(ItemPredicate.Builder.create().items(createWrapperLookup().getOrThrow(RegistryKeys.ITEM), item).count(count).build());
    }

    public static AdvancementCriterion<InventoryChangedCriterion.Conditions> conditionsFromItem(ItemConvertible item) {
        return conditionsFromItemPredicates(ItemPredicate.Builder.create().items(createWrapperLookup().getOrThrow(RegistryKeys.ITEM), item).build());
    }

    public static AdvancementCriterion<InventoryChangedCriterion.Conditions> conditionsFromIngredient(Ingredient item) {
        List<Item> items = new ArrayList<>();
        for (RegistryEntry<Item> item1:
                item.getMatchingItems()) {
            if (items.contains(item1.value()))
                continue;
            items.add(item1.value());
        }
        return conditionsFromItemPredicates(ItemPredicate.Builder.create().items(null, items.toArray(new Item[0])).build());
    }

    private static AdvancementCriterion<InventoryChangedCriterion.Conditions> conditionsFromTag(TagKey<Item> tag) {
        return conditionsFromItemPredicates(ItemPredicate.Builder.create().tag(createWrapperLookup().getOrThrow(RegistryKeys.ITEM), tag).build());
    }

    public static AdvancementCriterion<InventoryChangedCriterion.Conditions> conditionsFromPredicates(ItemPredicate.Builder... predicates) {
        return conditionsFromItemPredicates(Arrays.stream(predicates).map(ItemPredicate.Builder::build).toArray(ItemPredicate[]::new));
    }

    public static AdvancementCriterion<InventoryChangedCriterion.Conditions> conditionsFromItemPredicates(ItemPredicate... predicates) {
        return Criteria.INVENTORY_CHANGED.create(new InventoryChangedCriterion.Conditions(Optional.empty(), InventoryChangedCriterion.Conditions.Slots.ANY, List.of(predicates)));
    }

    private static String getItemPath(Ingredient item) {
        List<RegistryEntry<Item>> n = item.getMatchingItems();
        if (!n.isEmpty()) {
            return Registries.ITEM.getId(n.getFirst().value()).getPath();
        } else {
            return item.toString();
        }
    }
    private static String getItemPath(ItemConvertible item) {
        return Registries.ITEM.getId(item.asItem()).getPath();
    }
}
