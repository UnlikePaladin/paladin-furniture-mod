package com.unlikepaladin.pfm.runtime.data;

import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.blocks.*;
import com.unlikepaladin.pfm.blocks.models.ModelHelper;
import com.unlikepaladin.pfm.data.FurnitureBlock;
import com.unlikepaladin.pfm.data.materials.VariantBase;
import com.unlikepaladin.pfm.data.materials.WoodVariant;
import com.unlikepaladin.pfm.data.materials.WoodVariantRegistry;
import com.unlikepaladin.pfm.menus.WorkbenchScreenHandler;
import com.unlikepaladin.pfm.mixin.PFMIngredientMatchingStacksAccessor;
import com.unlikepaladin.pfm.registry.PaladinFurnitureModBlocksItems;
import com.unlikepaladin.pfm.runtime.PFMDataGenerator;
import com.unlikepaladin.pfm.runtime.PFMGenerator;
import com.unlikepaladin.pfm.runtime.PFMProvider;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonFactory;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.recipe.Ingredient;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.registry.Registry;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class PFMRecipeProvider extends PFMProvider {

    public PFMRecipeProvider(PFMGenerator parent) {
        super(parent, "PFM Recipes");
        parent.setProgress("Generating Recipes");
    }

    @Override
    public void run() {
        startProviderRun();
        Path path = getParent().getOutput();
        HashSet<Identifier> set = Sets.newHashSet();
        WorkbenchScreenHandler.ALL_RECIPES.clear();
        generateRecipes(recipeJsonProvider -> {
            if (!set.add(recipeJsonProvider.getRecipeId())) {
                getParent().getLogger().error("Duplicate recipe " + recipeJsonProvider.getRecipeId());
                throw new IllegalStateException("Duplicate recipe " + recipeJsonProvider.getRecipeId());
            }
            if (recipeJsonProvider == null) {
                getParent().getLogger().error("Recipe Json Provider is null");
                throw new IllegalStateException("Recipe Json Provider is null");
            }
            saveRecipe(recipeJsonProvider.toJson(), path.resolve("data/" + recipeJsonProvider.getRecipeId().getNamespace() + "/recipes/" + recipeJsonProvider.getRecipeId().getPath() + ".json"));
            JsonObject jsonObject = recipeJsonProvider.toAdvancementJson();
            if (jsonObject != null) {
                saveRecipeAdvancement(jsonObject, path.resolve("data/" + recipeJsonProvider.getRecipeId().getNamespace() + "/advancements/" + recipeJsonProvider.getAdvancementId().getPath() + ".json"));
            }
        });
        saveRecipeAdvancement(Advancement.Task.create().criterion("has_planks", conditionsFromTag(ItemTags.PLANKS)).toJson(), path.resolve("data/pfm/advancements/recipes/root.json"));
        endProviderRun();
    }

    private void saveRecipe(JsonObject json, Path path) {
        try {
            String string = PFMDataGenerator.GSON.toJson(json);
            if (!Files.exists(path.getParent()))
                Files.createDirectories(path.getParent());

            Files.write(path, string.getBytes(StandardCharsets.UTF_8));
        } catch (IOException var10) {
            getParent().getLogger().error("Couldn't save recipe {}", path, var10);
        }
    }

    private void saveRecipeAdvancement(JsonObject json, Path path) {
        try {
            String string = PFMDataGenerator.GSON.toJson(json);
            if (!Files.exists(path.getParent()))
                Files.createDirectories(path.getParent());

            Files.write(path, string.getBytes(StandardCharsets.UTF_8));
        } catch (IOException var10) {
            getParent().getLogger().error("Couldn't save recipe advancement {}", path, var10);
        }
    }
    @ExpectPlatform
    protected static Identifier getId(Block block) {
        throw new AssertionError();    
    }
    protected void generateRecipes(Consumer<RecipeJsonProvider> exporter) {
        List<Identifier> generatedRecipes = new ArrayList<>();

        offerBasicChairRecipe(BasicChairBlock.class, "secondary", "base", PaladinFurnitureModBlocksItems.furnitureEntryMap.get(BasicChairBlock.class).getVariants(), exporter);
        offerDinnerChairRecipe(DinnerChairBlock.class, "secondary", "base", PaladinFurnitureModBlocksItems.furnitureEntryMap.get(DinnerChairBlock.class).getVariants(), exporter);
        offerClassicChairRecipe(ClassicChairBlock.class, "secondary", "base", PaladinFurnitureModBlocksItems.furnitureEntryMap.get(ClassicChairBlock.class).getVariants(), exporter);

        List<FurnitureBlock> froggyChairs = FroggyChairBlock.streamFroggyChair().collect(Collectors.toList());
        for (FurnitureBlock froggyChair : froggyChairs) {
            if (!generatedRecipes.contains(getId(froggyChair.getBlock()))) {
                offerFroggyChairRecipe(froggyChair.getBlock(), Ingredient.ofItems(froggyChair.getFroggyChairMaterial().asItem()), exporter);
                generatedRecipes.add(getId(froggyChair.getBlock()));
            }
        }
        List<FurnitureBlock> woolClassicChairs = ClassicChairDyeableBlock.streamWoodDyeableChair().collect(Collectors.toList());
        for (FurnitureBlock classicChair : woolClassicChairs) {
            if (!generatedRecipes.contains(getId(classicChair.getBlock()))) {
                offerClassicChairDyedRecipe(classicChair.getBlock(), Ingredient.ofItems(Items.OAK_LOG), Ingredient.ofItems(classicChair.getArmChairMaterial()), exporter);
                generatedRecipes.add(getId(classicChair.getBlock()));
            }
        }
        offerModernChairRecipe(ModernChairBlock.class, "secondary", "base", PaladinFurnitureModBlocksItems.furnitureEntryMap.get(ModernChairBlock.class).getVariants(), exporter);

        List<FurnitureBlock> armChairs = ArmChairColoredBlock.streamArmChairColored().collect(Collectors.toList());
        for (FurnitureBlock armChair : armChairs) {
            if (!generatedRecipes.contains(getId(armChair.getBlock()))) {
                offerArmChairRecipe(armChair.getBlock(), Ingredient.ofItems(Items.OAK_LOG), Ingredient.ofItems(armChair.getArmChairMaterial().asItem()), exporter);
                generatedRecipes.add(getId(armChair.getBlock()));
            }
        }
        List<FurnitureBlock> simpleSofas = SimpleSofaBlock.streamSimpleSofas().collect(Collectors.toList());
        for (FurnitureBlock sofa : simpleSofas) {
            if (!generatedRecipes.contains(getId(sofa.getBlock()))) {
                offerSimpleSofaRecipe(sofa.getBlock(), Ingredient.ofItems(Items.OAK_LOG), Ingredient.ofItems(sofa.getArmChairMaterial().asItem()), exporter);
                generatedRecipes.add(getId(sofa.getBlock()));
            }
        }
        List<FurnitureBlock> leatherArmChairs = ArmChairBlock.streamArmChairs().collect(Collectors.toList());
        for (FurnitureBlock armChair : leatherArmChairs) {
            if (!generatedRecipes.contains(getId(armChair.getBlock()))) {
                offerArmChairRecipe(armChair.getBlock(), Ingredient.ofItems(Items.OAK_LOG), Ingredient.ofItems(armChair.getArmChairMaterial().asItem()), exporter);
                generatedRecipes.add(getId(armChair.getBlock()));
            }
        }

        offerBasicTableRecipe(BasicTableBlock.class, "secondary", "base", PaladinFurnitureModBlocksItems.furnitureEntryMap.get(BasicTableBlock.class).getVariants(), exporter);
        offerClassicTableRecipe(ClassicTableBlock.class, "secondary", "base", PaladinFurnitureModBlocksItems.furnitureEntryMap.get(ClassicTableBlock.class).getVariants(), exporter);
        offerLogTableRecipe(LogTableBlock.class, "secondary", "base", PaladinFurnitureModBlocksItems.furnitureEntryMap.get(LogTableBlock.class).getVariants(), exporter);
        offerLogTableRecipe(RawLogTableBlock.class, "secondary", "secondary", PaladinFurnitureModBlocksItems.furnitureEntryMap.get(RawLogTableBlock.class).getVariants(), exporter);
        offerDinnerTableRecipe(DinnerTableBlock.class, "secondary", "base", PaladinFurnitureModBlocksItems.furnitureEntryMap.get(DinnerTableBlock.class).getVariants(), exporter);
        offerModernDinnerTableRecipe(ModernDinnerTableBlock.class, "secondary", "base", PaladinFurnitureModBlocksItems.furnitureEntryMap.get(ModernDinnerTableBlock.class).getVariants(), exporter);
        offerClassicNightStandRecipe(ClassicNightstandBlock.class, "secondary", "base", PaladinFurnitureModBlocksItems.furnitureEntryMap.get(ClassicNightstandBlock.class).getVariants(), exporter);


        for (Block block : getVanillaBeds()) {
            offerSimpleBedRecipe(SimpleBedBlock.class, "base", PaladinFurnitureModBlocksItems.furnitureEntryMap.get(SimpleBedBlock.class).getVariants(), Ingredient.ofItems(block), exporter);
            offerClassicBedRecipe(ClassicBedBlock.class, "base", PaladinFurnitureModBlocksItems.furnitureEntryMap.get(ClassicBedBlock.class).getVariants(), Ingredient.ofItems(block), "fence", exporter);
        }

        offerSimpleBunkLadderRecipe(SimpleBunkLadderBlock.class, "base", PaladinFurnitureModBlocksItems.furnitureEntryMap.get(SimpleBunkLadderBlock.class).getVariants(), exporter);

        offerLogStoolRecipe(LogStoolBlock.class, "secondary", PaladinFurnitureModBlocksItems.furnitureEntryMap.get(LogStoolBlock.class).getVariants(), exporter);
        offerSimpleStoolRecipe(SimpleStoolBlock.class, "secondary", "base", PaladinFurnitureModBlocksItems.furnitureEntryMap.get(SimpleStoolBlock.class).getVariants(), exporter);
        offerClassicStoolRecipe(ClassicStoolBlock.class, "secondary", "base", PaladinFurnitureModBlocksItems.furnitureEntryMap.get(ClassicStoolBlock.class).getVariants(), exporter);
        offerModernStoolRecipe(ModernStoolBlock.class, "secondary", "base", PaladinFurnitureModBlocksItems.furnitureEntryMap.get(ModernStoolBlock.class).getVariants(), exporter);
        offerCounterRecipe(KitchenCounterBlock.class, "secondary", "base", PaladinFurnitureModBlocksItems.furnitureEntryMap.get(KitchenCounterBlock.class).getVariants(), exporter);
        offerKitchenSinkRecipe(KitchenSinkBlock.class, "secondary", "base", PaladinFurnitureModBlocksItems.furnitureEntryMap.get(KitchenSinkBlock.class).getVariants(), Ingredient.ofItems(Items.BUCKET), Ingredient.ofItems(Items.IRON_INGOT), exporter);
        offerCounterApplianceRecipe(KitchenDrawerBlock.class, "secondary", "base", PaladinFurnitureModBlocksItems.furnitureEntryMap.get(KitchenDrawerBlock.class).getVariants(), Ingredient.ofItems(Items.CHEST), exporter);
        offerCounterApplianceRecipe(KitchenCounterOvenBlock.class, "secondary", "base", PaladinFurnitureModBlocksItems.furnitureEntryMap.get(KitchenCounterOvenBlock.class).getVariants(), Ingredient.ofItems(Items.FURNACE), exporter);
        offerCounterRecipe(KitchenWallCounterBlock.class, "base", "base", PaladinFurnitureModBlocksItems.furnitureEntryMap.get(KitchenWallCounterBlock.class).getVariants(), exporter);
        offerWallDrawerRecipe(KitchenWallDrawerBlock.class, "secondary", "base", PaladinFurnitureModBlocksItems.furnitureEntryMap.get(KitchenWallDrawerBlock.class).getVariants(), Ingredient.ofItems(Items.CHEST), exporter);
        offerWallDrawerSmallRecipe(KitchenWallDrawerSmallBlock.class, "secondary", "base", PaladinFurnitureModBlocksItems.furnitureEntryMap.get(KitchenWallDrawerSmallBlock.class).getVariants(), Ingredient.ofItems(Items.CHEST), exporter);
        offerCabinetRecipe(KitchenCabinetBlock.class, "secondary", "base", PaladinFurnitureModBlocksItems.furnitureEntryMap.get(KitchenCabinetBlock.class).getVariants(), Ingredient.ofItems(Items.CHEST), exporter);

        List<FurnitureBlock> herringbonePlanks = HerringbonePlankBlock.streamPlanks().collect(Collectors.toList());
        for (FurnitureBlock herringbonePlank : herringbonePlanks) {
            if (!generatedRecipes.contains(getId(herringbonePlank.getBlock()))) {
                offerHerringbonePlanks(herringbonePlank.getBlock(), herringbonePlank.getSlab().asItem(), exporter);
            }
        }
        List<FurnitureBlock> fridges = FridgeBlock.streamFridges().collect(Collectors.toList());
        for (FurnitureBlock fridge : fridges) {
            if (!generatedRecipes.contains(getId(fridge.getBlock()))) {
                offerFridgeRecipe(fridge.getBlock(), Ingredient.ofItems(fridge.getFridgeMaterial().asItem()), Ingredient.ofItems(Items.CHEST), exporter);
                generatedRecipes.add(getId(fridge.getBlock()));
            }
        }
        List<FurnitureBlock> freezers = FreezerBlock.streamFreezers().collect(Collectors.toList());
        for (FurnitureBlock freezer : freezers) {
            if (!generatedRecipes.contains(getId(freezer.getBlock()))) {
                offerFreezerRecipe(freezer.getBlock(), Ingredient.ofItems(freezer.getFridgeMaterial().asItem()), exporter);
                generatedRecipes.add(getId(freezer.getBlock()));
            }
        }
        List<FurnitureBlock> microwaves = MicrowaveBlock.streamMicrowaves().collect(Collectors.toList());
        for (FurnitureBlock microwave : microwaves) {
            if (!generatedRecipes.contains(getId(microwave.getBlock()))) {
                offerMicrowaveRecipe(microwave.getBlock(),  Ingredient.ofItems(microwave.getFridgeMaterial().asItem()), Ingredient.ofItems(Items.FURNACE), exporter);
                generatedRecipes.add(getId(microwave.getBlock()));
            }
        }
        List<FurnitureBlock> rangeHoods = KitchenRangeHoodBlock.streamOvenRangeHoods().collect(Collectors.toList());
        for (FurnitureBlock rangeHood : rangeHoods) {
            if (!generatedRecipes.contains(getId(rangeHood.getBlock()))) {
                offerRangeHoodRecipe(rangeHood.getBlock(),  Ingredient.ofItems(rangeHood.getFridgeMaterial().asItem()), Ingredient.ofItems(Items.REDSTONE_LAMP), exporter);
                generatedRecipes.add(getId(rangeHood.getBlock()));
            }
        }
        List<FurnitureBlock> stoves = StoveBlock.streamStoves().collect(Collectors.toList());
        for (FurnitureBlock stove : stoves) {
            if (!generatedRecipes.contains(getId(stove.getBlock()))) {
                offerStoveRecipe(stove.getBlock(),  Ingredient.ofItems(stove.getFridgeMaterial().asItem()), Ingredient.ofItems(Items.FURNACE), exporter);
                generatedRecipes.add(getId(stove.getBlock()));
            }
        }
        List<FurnitureBlock> ironStove = IronStoveBlock.streamIronStoves().collect(Collectors.toList());
        for (FurnitureBlock stove : ironStove) {
            if (!generatedRecipes.contains(getId(stove.getBlock()))) {
                offerStoveRecipe(stove.getBlock(),  Ingredient.ofItems(stove.getFridgeMaterial().asItem()), Ingredient.ofItems(Items.FURNACE), exporter);
                generatedRecipes.add(getId(stove.getBlock()));
            }
        }
        List<KitchenStovetopBlock> stovetopBlocks = KitchenStovetopBlock.streamKitchenStovetop().collect(Collectors.toList());
        for (KitchenStovetopBlock stove : stovetopBlocks) {
            if (!generatedRecipes.contains(getId(stove))) {
                offerStovetopRecipe(stove, Ingredient.ofItems(Items.IRON_INGOT), Ingredient.ofItems(Blocks.GRAY_CONCRETE), exporter);
                generatedRecipes.add(getId(stove));
            }
        }
        List<FurnitureBlock> plates = PlateBlock.streamPlates().collect(Collectors.toList());
        for (FurnitureBlock plate : plates) {
            if (!generatedRecipes.contains(getId(plate.getBlock()))) {
                offerPlateRecipe(plate.getBlock(), Ingredient.ofItems(plate.getPlateMaterial()), Ingredient.ofItems(Items.ITEM_FRAME), Ingredient.ofItems(plate.getPlateDecoration()), exporter);
                generatedRecipes.add(getId(plate.getBlock()));
            }
        }
        List<FurnitureBlock> cutleries = CutleryBlock.streamCutlery().collect(Collectors.toList());
        for (FurnitureBlock cutlery : cutleries) {
            if (!generatedRecipes.contains(getId(cutlery.getBlock()))) {
                offerCutleryRecipe(cutlery.getBlock(), Ingredient.ofItems(cutlery.getCutleryMaterial()), exporter);
                generatedRecipes.add(getId(cutlery.getBlock()));
            }
        }
        PaladinFurnitureModBlocksItems.furnitureEntryMap.get(PendantBlock.class).getAllBlocks().forEach((block) -> {
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
        if (!generatedRecipes.contains(Registry.ITEM.getId(PaladinFurnitureModBlocksItems.LIGHT_SWITCH_ITEM))) {
            SimpleFurnitureRecipeJsonFactory.create(PaladinFurnitureModBlocksItems.LIGHT_SWITCH_ITEM, 6).input(Blocks.WHITE_CONCRETE, 6).input(Blocks.LIGHT_GRAY_CONCRETE, 2).input(Items.REDSTONE).offerTo(exporter, new Identifier("pfm", PaladinFurnitureModBlocksItems.LIGHT_SWITCH_ITEM.getTranslationKey().replace("block.pfm.", "")));
            generatedRecipes.add(Registry.ITEM.getId(PaladinFurnitureModBlocksItems.LIGHT_SWITCH_ITEM));
        }

        List<FurnitureBlock> basicToilets = BasicToiletBlock.streamBasicToilet().collect(Collectors.toList());
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
            offerShowerHandleRecipe(PaladinFurnitureModBlocksItems.BASIC_SHOWER_HANDLE_ITEM, Ingredient.ofItems(Blocks.LEVER), exporter);
            generatedRecipes.add(getId(PaladinFurnitureModBlocksItems.BASIC_SHOWER_HEAD));
            generatedRecipes.add(Registry.ITEM.getId(PaladinFurnitureModBlocksItems.BASIC_SHOWER_HANDLE_ITEM));
        }

        if (!generatedRecipes.contains(getId(PaladinFurnitureModBlocksItems.MESH_TRASHCAN))) {
            SimpleFurnitureRecipeJsonFactory.create(PaladinFurnitureModBlocksItems.MESH_TRASHCAN, 1).input(Items.IRON_INGOT, 1).input(Items.ENDER_PEARL, 1).input(Blocks.IRON_BARS, 4).offerTo(exporter, new Identifier("pfm", PaladinFurnitureModBlocksItems.MESH_TRASHCAN.asItem().getTranslationKey().replace("block.pfm.", "")));
            generatedRecipes.add(getId(PaladinFurnitureModBlocksItems.MESH_TRASHCAN));
        }
        if (!generatedRecipes.contains(getId(PaladinFurnitureModBlocksItems.TRASHCAN))) {
            SimpleFurnitureRecipeJsonFactory.create(PaladinFurnitureModBlocksItems.TRASHCAN, 1).input(Items.IRON_INGOT, 1).input(Items.ENDER_PEARL, 1).input(Blocks.IRON_BARS, 4).offerTo(exporter, new Identifier("pfm", PaladinFurnitureModBlocksItems.TRASHCAN.asItem().getTranslationKey().replace("block.pfm.", "")));
            generatedRecipes.add(getId(PaladinFurnitureModBlocksItems.TRASHCAN));
        }

        List<FurnitureBlock> showerTowels = ShowerTowelBlock.streamShowerTowels().collect(Collectors.toList());
        for (FurnitureBlock towel : showerTowels) {
            if (!generatedRecipes.contains(getId(towel.getBlock()))) {
                offerShowerTowelRecipe(towel.getBlock(),  Ingredient.ofItems(towel.getWoolColor()), exporter);
                generatedRecipes.add(getId(towel.getBlock()));
            }
        }

        List<FurnitureBlock> mirrors = MirrorBlock.streamMirrorBlocks().collect(Collectors.toList());
        for (FurnitureBlock mirror : mirrors) {
            if (!generatedRecipes.contains(getId(mirror.getBlock()))) {
                offerMirrorRecipe(mirror.getBlock(), Ingredient.ofItems(mirror.getBaseMaterial()), exporter);
                generatedRecipes.add(getId(mirror.getBlock()));
            }
        }

        offerLampRecipes(exporter);

        offerBasicCoffeeTableRecipe(BasicCoffeeTableBlock.class, "secondary", "base", PaladinFurnitureModBlocksItems.furnitureEntryMap.get(BasicCoffeeTableBlock.class).getVariants(), exporter);
        offerModernCoffeeTableRecipe(ModernCoffeeTableBlock.class, "secondary", "base", PaladinFurnitureModBlocksItems.furnitureEntryMap.get(ModernCoffeeTableBlock.class).getVariants(), exporter);
        offerClassicCoffeeTableRecipe(ClassicCoffeeTableBlock.class, "secondary", "base", PaladinFurnitureModBlocksItems.furnitureEntryMap.get(ClassicCoffeeTableBlock.class).getVariants(), exporter);

        PaladinFurnitureMod.pfmModCompatibilities.forEach(pfmModCompatibility -> pfmModCompatibility.generateRecipes(exporter));
    }

    public static void offerLampRecipes(Consumer<RecipeJsonProvider> exporter) {
        for (DyeColor color : DyeColor.values()) {
            NbtCompound beTag = new NbtCompound();
            beTag.putString("color", color.asString());
            NbtCompound tag = new NbtCompound();
            tag.put("BlockEntityTag", beTag);
            tag.putBoolean("variantInNbt", true);

            DynamicFurnitureRecipeJsonFactory.create(BasicLampBlock.class, 1,  WoodVariantRegistry.getVariants().stream().map(woodVariant -> woodVariant.identifier).collect(Collectors.toList()), tag).vanillaInput(ModelHelper.getWoolColor(color.asString()), 3).vanillaInput(Items.TORCH).vanillaInput(Items.REDSTONE).childInput("stripped_log", 2).offerTo(exporter, new Identifier("pfm", String.format("basic_%s_lamp", color.asString())));
        }
    }

    public static Pair<Block, Block> getCounterMaterials(VariantBase<?> variantBase) {
        Block counterTop = variantBase.getSecondaryBlock();
        Block counterBase = variantBase.getBaseBlock();

        if (variantBase.identifier.getPath().equals("netherite")) {
            Block temp = counterBase;
            counterBase = counterTop;
            counterTop  = temp;
        }
        return new Pair<>(counterBase,counterTop);
    }
    public Block getVanillaBed(Block block) {
        if (block instanceof SimpleBedBlock){
            String color = ((SimpleBedBlock) block).getPFMColor().getName();
            return Registry.BLOCK.get(new Identifier("minecraft:" + color + "_bed"));
        }
        return null;
    }

    public List<Block> getVanillaBeds() {
        List<Block> beds = new ArrayList<>();
        Registry.BLOCK.stream().forEach(block -> {
            if (block instanceof BedBlock && Registry.BLOCK.getId(block).getNamespace().equals("minecraft"))
                beds.add(block);
        });
        return beds;
    }

    public static void offerBasicChairRecipe(Class<? extends Block> output, String legMaterial, String baseMaterial, List<Identifier> variants, Consumer<RecipeJsonProvider> exporter) {
        DynamicFurnitureRecipeJsonFactory.create(output, 4, variants).group("chairs").childInput(legMaterial, 2).childInput(baseMaterial, 4).offerTo(exporter, new Identifier("pfm", output.getSimpleName().replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase(Locale.US)));
    }

    public static void offerFroggyChairRecipe(ItemConvertible output, Ingredient baseMaterial, Consumer<RecipeJsonProvider> exporter) {
        SimpleFurnitureRecipeJsonFactory.create(output, 4).group("chairs").criterion("has_concrete", conditionsFromIngredient(baseMaterial)).input(baseMaterial, 6).offerTo(exporter, new Identifier("pfm", output.asItem().getTranslationKey().replace("block.pfm.", "")));
    }

    public static void offerDinnerChairRecipe(Class<? extends Block> output, String legMaterial, String baseMaterial, List<Identifier> variants, Consumer<RecipeJsonProvider> exporter) {
        DynamicFurnitureRecipeJsonFactory.create(output, 4, variants).group("chairs").childInput(legMaterial, 3).childInput(baseMaterial, 3).offerTo(exporter, new Identifier("pfm", output.getSimpleName().replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase(Locale.US)));
    }

    public static void offerClassicChairDyedRecipe(ItemConvertible output, Ingredient legMaterial, Ingredient baseMaterial, Consumer<RecipeJsonProvider> exporter) {
        SimpleFurnitureRecipeJsonFactory.create(output, 4).group("chairs").criterion(getCriterionNameFromOutput(output), conditionsFromIngredient(baseMaterial)).input(legMaterial, 4).input(baseMaterial, 2).offerTo(exporter, new Identifier("pfm", output.asItem().getTranslationKey().replace("block.pfm.", "")));
    }

    public static void offerClassicChairRecipe(Class<? extends Block> output, String legMaterial, String baseMaterial, List<Identifier> variants, Consumer<RecipeJsonProvider> exporter) {
        DynamicFurnitureRecipeJsonFactory.create(output, 4, variants).group("chairs").childInput(legMaterial, 4).childInput(baseMaterial, 2).offerTo(exporter, new Identifier("pfm", output.getSimpleName().replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase(Locale.US)));
    }

    public static void offerModernChairRecipe(Class<? extends Block> output, String legMaterial, String baseMaterial, List<Identifier> variants, Consumer<RecipeJsonProvider> exporter) {
        DynamicFurnitureRecipeJsonFactory.create(output, 4, variants).group("chairs").childInput(legMaterial, 3).childInput(baseMaterial, 3).offerTo(exporter, new Identifier("pfm", output.getSimpleName().replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase(Locale.US)));

    }
    public static void offerArmChairRecipe(ItemConvertible output, Ingredient baseMaterial, Ingredient legMaterial, Consumer<RecipeJsonProvider> exporter) {
        SimpleFurnitureRecipeJsonFactory.create(output, 2).group("chairs").criterion("has_wool", conditionsFromIngredient(baseMaterial)).input(legMaterial, 4).input(baseMaterial, 2).offerTo(exporter, new Identifier("pfm", output.asItem().getTranslationKey().replace("block.pfm.", "")));
    }

    public static void offerSimpleSofaRecipe(ItemConvertible output, Ingredient baseMaterial, Ingredient legMaterial, Consumer<RecipeJsonProvider> exporter) {
        SimpleFurnitureRecipeJsonFactory.create(output, 2).group("chairs").criterion("has_wool", conditionsFromIngredient(baseMaterial)).input(legMaterial, 2).input(baseMaterial, 4).offerTo(exporter, new Identifier("pfm", output.asItem().getTranslationKey().replace("block.pfm.", "")));
    }

    public static void offerBasicTableRecipe(Class<? extends Block> output, String legMaterial, String baseMaterial, List<Identifier> variants, Consumer<RecipeJsonProvider> exporter) {
        DynamicFurnitureRecipeJsonFactory.create(output, 4, variants).group("tables").childInput(legMaterial, 5).childInput(baseMaterial, 3).offerTo(exporter, new Identifier("pfm", output.getSimpleName().replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase(Locale.US)));
    }

    public static void offerBasicCoffeeTableRecipe(Class<? extends Block> output, String legMaterial, String baseMaterial, List<Identifier> variants, Consumer<RecipeJsonProvider> exporter) {
        DynamicFurnitureRecipeJsonFactory.create(output, 4, variants).group("tables").childInput(legMaterial, 3).childInput(baseMaterial, 3).offerTo(exporter, new Identifier("pfm", output.getSimpleName().replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase(Locale.US)));
    }

    public static void offerModernCoffeeTableRecipe(Class<? extends Block> output, String legMaterial, String baseMaterial, List<Identifier> variants, Consumer<RecipeJsonProvider> exporter) {
        DynamicFurnitureRecipeJsonFactory.create(output, 4, variants).group("tables").childInput(legMaterial, 4).childInput(baseMaterial, 3).offerTo(exporter, new Identifier("pfm", output.getSimpleName().replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase(Locale.US)));
    }

    public static void offerClassicCoffeeTableRecipe(Class<? extends Block> output, String legMaterial, String baseMaterial, List<Identifier> variants, Consumer<RecipeJsonProvider> exporter) {
        DynamicFurnitureRecipeJsonFactory.create(output, 4, variants).group("tables").childInput(legMaterial, 2).childInput(baseMaterial, 3).offerTo(exporter, new Identifier("pfm", output.getSimpleName().replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase(Locale.US)));
    }

    public static void offerClassicTableRecipe(Class<? extends Block> output, String legMaterial, String baseMaterial, List<Identifier> variants, Consumer<RecipeJsonProvider> exporter) {
        DynamicFurnitureRecipeJsonFactory.create(output, 4, variants).group("tables").childInput(legMaterial, 4).childInput(baseMaterial, 3).offerTo(exporter, new Identifier("pfm", output.getSimpleName().replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase(Locale.US)));
    }

    public static void offerLogTableRecipe(Class<? extends Block> output, String legMaterial, String baseMaterial, List<Identifier> variants, Consumer<RecipeJsonProvider> exporter) {
        DynamicFurnitureRecipeJsonFactory.create(output, 4, variants).group("tables").childInput(legMaterial, 2).childInput(baseMaterial, 3).offerTo(exporter, new Identifier("pfm", output.getSimpleName().replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase(Locale.US)));
    }

    public static void offerHerringbonePlanks(ItemConvertible output, Item baseMaterial, Consumer<RecipeJsonProvider> exporter) {
        ShapedRecipeJsonFactory.create(output, 4).input('X', baseMaterial).pattern("XX").pattern("XX").criterion("has_wood_slabs", conditionsFromItem(baseMaterial)).offerTo(exporter, new Identifier("pfm", output.asItem().getTranslationKey().replace("block.pfm.", "")));
    }

    public static void offerDinnerTableRecipe(Class<? extends Block> output, String legMaterial, String baseMaterial, List<Identifier> variants, Consumer<RecipeJsonProvider> exporter) {
        DynamicFurnitureRecipeJsonFactory.create(output, 4, variants).group("tables").childInput(legMaterial, 3).childInput(baseMaterial, 3).offerTo(exporter, new Identifier("pfm", output.getSimpleName().replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase(Locale.US)));
    }

    public static void offerModernDinnerTableRecipe(Class<? extends Block> output, String legMaterial, String baseMaterial, List<Identifier> variants, Consumer<RecipeJsonProvider> exporter) {
        DynamicFurnitureRecipeJsonFactory.create(output, 4, variants).group("tables").childInput(legMaterial, 5).childInput(baseMaterial, 3).offerTo(exporter, new Identifier("pfm", output.getSimpleName().replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase(Locale.US)));
    }

    public static void offerClassicNightStandRecipe(Class<? extends Block> output, String legMaterial, String baseMaterial, List<Identifier> variants, Consumer<RecipeJsonProvider> exporter) {
        DynamicFurnitureRecipeJsonFactory.create(output, 4, variants).group("bedroom").childInput(legMaterial, 6).childInput(baseMaterial, 1).vanillaInput(Blocks.CHEST, 1).offerTo(exporter, new Identifier("pfm", output.getSimpleName().replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase(Locale.US)));
    }

    public static String getCriterionNameFromOutput(ItemConvertible output) {
        return getCriterionNameFromOutput(output, "");
    }

    public static String getEmptyCriteria() {
        return "";
    }

    public static String getCriterionNameFromOutput(ItemConvertible output, String type) {
        if (Block.getBlockFromItem(output.asItem()) == null || Block.getBlockFromItem(output.asItem()) == Blocks.AIR || !PaladinFurnitureModBlocksItems.furnitureEntryMap.containsKey(Block.getBlockFromItem(output.asItem()).getClass())) {
            return getItemPath(output);
        }
        if (PaladinFurnitureModBlocksItems.furnitureEntryMap.get(Block.getBlockFromItem(output.asItem()).getClass()).getVariantFromEntry(Block.getBlockFromItem(output.asItem())) instanceof WoodVariant) {
            return type.isEmpty() ? "has_planks" : type;
        }
        else return getItemPath(output);
    }

    public static void offerSimpleBedRecipe(Class<? extends Block> output, String legMaterial, List<Identifier> variants, Ingredient baseBed, Consumer<RecipeJsonProvider> exporter) {
        DyeColor color = ((BedBlock)((BlockItem)Arrays.stream(PFMRecipeProvider.pfm$getMatchingStacks(baseBed)).findFirst().get().getItem()).getBlock()).getColor();
        NbtCompound tag = new NbtCompound();
        tag.putString("color", color.asString());
        DynamicFurnitureRecipeJsonFactory.create(output, 1, variants, tag).group("bedroom").childInput(legMaterial, 5).vanillaInput(baseBed, 1).offerTo(exporter, new Identifier("pfm", output.getSimpleName().replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase(Locale.US) + "_"+ color.asString()));

    }

    public static void offerClassicBedRecipe(Class<? extends Block> output, String legMaterial, List<Identifier> variants, Ingredient baseBed, String fence, Consumer<RecipeJsonProvider> exporter) {
        DyeColor color = ((BedBlock)((BlockItem)Arrays.stream(PFMRecipeProvider.pfm$getMatchingStacks(baseBed)).findFirst().get().getItem()).getBlock()).getColor();
        NbtCompound tag = new NbtCompound();
        tag.putString("color", color.asString());
        DynamicFurnitureRecipeJsonFactory.create(output, 1, variants, tag).group("bedroom").childInput(legMaterial, 3).childInput(fence, 2).vanillaInput(baseBed, 1).offerTo(exporter, new Identifier("pfm", output.getSimpleName().replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase(Locale.US) + "_"+ ((BedBlock)((BlockItem)Arrays.stream(PFMRecipeProvider.pfm$getMatchingStacks(baseBed)).findFirst().get().getItem()).getBlock()).getColor()));
    }

    public static void offerSimpleBunkLadderRecipe(Class<? extends Block> output, String base, List<Identifier> variants, Consumer<RecipeJsonProvider> exporter) {
        DynamicFurnitureRecipeJsonFactory.create(output, 4, variants).group("bedroom").childInput(base, 1).vanillaInput(Ingredient.ofItems(Items.STICK), 6).offerTo(exporter, new Identifier("pfm", output.getSimpleName().replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase(Locale.US)));
    }

    public static void offerLogStoolRecipe(Class<? extends Block> output, String legMaterial, List<Identifier> variants, Consumer<RecipeJsonProvider> exporter) {
        DynamicFurnitureRecipeJsonFactory.create(output, 4, variants).group("stools").childInput(legMaterial, 1).offerTo(exporter, new Identifier("pfm", output.getSimpleName().replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase(Locale.US)));
    }

    public static void offerSimpleStoolRecipe(Class<? extends Block> output, String legMaterial, String baseMaterial, List<Identifier> variants, Consumer<RecipeJsonProvider> exporter) {
        DynamicFurnitureRecipeJsonFactory.create(output, 4, variants).group("stools").childInput(legMaterial, 2).childInput(baseMaterial, 3).offerTo(exporter, new Identifier("pfm", output.getSimpleName().replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase(Locale.US)));
    }

    public static void offerClassicStoolRecipe(Class<? extends Block> output, String legMaterial, String baseMaterial, List<Identifier> variants, Consumer<RecipeJsonProvider> exporter) {
        DynamicFurnitureRecipeJsonFactory.create(output, 4, variants).group("stools").childInput(legMaterial, 3).childInput(baseMaterial, 2).offerTo(exporter, new Identifier("pfm", output.getSimpleName().replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase(Locale.US)));
    }

    public static void offerModernStoolRecipe(Class<? extends Block> output, String legMaterial, String baseMaterial, List<Identifier> variants, Consumer<RecipeJsonProvider> exporter) {
        DynamicFurnitureRecipeJsonFactory.create(output, 4, variants).group("stools").childInput(legMaterial, 1).childInput(baseMaterial, 3).offerTo(exporter, new Identifier("pfm", output.getSimpleName().replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase(Locale.US)));
    }

    public static void offerCounterRecipe(Class<? extends Block> output, String legMaterial, String baseMaterial, List<Identifier> variants, Consumer<RecipeJsonProvider> exporter) {
        DynamicFurnitureRecipeJsonFactory.create(output, 6, variants).group("kitchen").childInput(legMaterial, 3).childInput(baseMaterial, 6).offerTo(exporter, new Identifier("pfm", output.getSimpleName().replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase(Locale.US)));
    }

    public static void offerCounterApplianceRecipe(Class<? extends Block> output, String legMaterial, String baseMaterial, List<Identifier> variants, Ingredient appliance, Consumer<RecipeJsonProvider> exporter) {
        DynamicFurnitureRecipeJsonFactory.create(output, 1, variants).group("kitchen").childInput(legMaterial, 3).childInput(baseMaterial, 5).vanillaInput(appliance).offerTo(exporter, new Identifier("pfm", output.getSimpleName().replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase(Locale.US)));
    }

    public static void offerKitchenSinkRecipe(Class<? extends Block> output, String legMaterial, String baseMaterial, List<Identifier> variants, Ingredient center, Ingredient ingot, Consumer<RecipeJsonProvider> exporter) {
        DynamicFurnitureRecipeJsonFactory.create(output, 1, variants).group("kitchen").childInput(legMaterial, 2).childInput(baseMaterial, 5).vanillaInput(ingot).vanillaInput(center).offerTo(exporter, new Identifier("pfm", output.getSimpleName().replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase(Locale.US)));
    }

    public static void offerWallDrawerRecipe(Class<? extends Block> output, String legMaterial, String baseMaterial, List<Identifier> variants, Ingredient appliace, Consumer<RecipeJsonProvider> exporter) {
        DynamicFurnitureRecipeJsonFactory.create(output, 1, variants).group("kitchen").childInput(legMaterial, 6).childInput(baseMaterial, 2).vanillaInput(appliace).offerTo(exporter, new Identifier("pfm", output.getSimpleName().replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase(Locale.US)));
    }

    public static void offerWallDrawerSmallRecipe(Class<? extends Block> output, String legMaterial, String baseMaterial, List<Identifier> variants, Ingredient appliance, Consumer<RecipeJsonProvider> exporter) {
        DynamicFurnitureRecipeJsonFactory.create(output, 3, variants).group("kitchen").childInput(legMaterial, 3).childInput(baseMaterial, 2).vanillaInput(appliance).offerTo(exporter, new Identifier("pfm", output.getSimpleName().replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase(Locale.US)));
    }

    public static void offerCabinetRecipe(Class<? extends Block> output, String legMaterial, String baseMaterial, List<Identifier> variants, Ingredient chest, Consumer<RecipeJsonProvider> exporter) {
        DynamicFurnitureRecipeJsonFactory.create(output, 3, variants).group("kitchen").childInput(legMaterial, 6).childInput(baseMaterial, 2).vanillaInput(chest).offerTo(exporter, new Identifier("pfm", output.getSimpleName().replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase(Locale.US)));
    }

    public static void offerFridgeRecipe(ItemConvertible output, Ingredient legMaterial, Ingredient storage, Consumer<RecipeJsonProvider> exporter) {
        if (output.asItem().toString().contains("xbox")) {
            SimpleFurnitureRecipeJsonFactory.create(output, 1).group("kitchen").criterion("has_" + getItemPath(legMaterial), conditionsFromIngredient(legMaterial)).input(legMaterial, 6).input(storage, 1).input(Ingredient.ofItems(Items.REDSTONE)).input(Ingredient.ofItems(Items.WHITE_CONCRETE)).offerTo(exporter, new Identifier("pfm", output.asItem().getTranslationKey().replace("block.pfm.", "")));
        }
        else {
            SimpleFurnitureRecipeJsonFactory.create(output, 1).group("kitchen").criterion("has_" + getItemPath(legMaterial), conditionsFromIngredient(legMaterial)).input(legMaterial, 7).input(storage).offerTo(exporter, new Identifier("pfm", output.asItem().getTranslationKey().replace("block.pfm.", "")));
        }
    }

    public static void offerFreezerRecipe(ItemConvertible output, Ingredient legMaterial, Consumer<RecipeJsonProvider> exporter) {
        SimpleFurnitureRecipeJsonFactory.create(output, 1).group("kitchen").criterion("has_" + getItemPath(legMaterial), conditionsFromIngredient(legMaterial)).input(legMaterial, 7).input(Ingredient.ofItems(Items.REDSTONE), 2).offerTo(exporter, new Identifier("pfm", output.asItem().getTranslationKey().replace("block.pfm.", "")));
    }

    public static void offerMicrowaveRecipe(ItemConvertible output, Ingredient legMaterial, Ingredient storage, Consumer<RecipeJsonProvider> exporter) {
        SimpleFurnitureRecipeJsonFactory.create(output, 1).group("kitchen").criterion("has_" + getItemPath(legMaterial), conditionsFromIngredient(legMaterial)).input(legMaterial ,5).input(storage).input(Ingredient.ofItems(Items.REDSTONE)).offerTo(exporter, new Identifier("pfm", output.asItem().getTranslationKey().replace("block.pfm.", "")));
    }

    public static void offerRangeHoodRecipe(ItemConvertible output, Ingredient legMaterial, Ingredient secondMaterial, Consumer<RecipeJsonProvider> exporter) {
        SimpleFurnitureRecipeJsonFactory.create(output, 1).group("kitchen").criterion("has_" + getItemPath(legMaterial), conditionsFromIngredient(legMaterial)).input(legMaterial, 4).input(secondMaterial).offerTo(exporter, new Identifier("pfm", output.asItem().getTranslationKey().replace("block.pfm.", "")));
    }

    public static void offerStoveRecipe(ItemConvertible output, Ingredient legMaterial, Ingredient storage, Consumer<RecipeJsonProvider> exporter) {
        SimpleFurnitureRecipeJsonFactory.create(output, 1).group("kitchen").criterion("has_" + getItemPath(legMaterial), conditionsFromIngredient(legMaterial)).input(legMaterial, 8).input(storage).offerTo(exporter, new Identifier("pfm", output.asItem().getTranslationKey().replace("block.pfm.", "")));
    }

    public static void offerStovetopRecipe(ItemConvertible output, Ingredient base, Ingredient material, Consumer<RecipeJsonProvider> exporter) {
        SimpleFurnitureRecipeJsonFactory.create(output, 1).group("kitchen").criterion("has_" + getItemPath(base), conditionsFromIngredient(base)).input(base, 6).input(material, 2).input(Ingredient.ofItems(Items.FLINT_AND_STEEL)).offerTo(exporter, new Identifier("pfm", output.asItem().getTranslationKey().replace("block.pfm.", "")));
    }

    public static void offerPlateRecipe(ItemConvertible output, Ingredient base, Ingredient frame, Ingredient decoration, Consumer<RecipeJsonProvider> exporter) {
        SimpleFurnitureRecipeJsonFactory.create(output, 4).group("kitchen").criterion("has_" + getItemPath(base), conditionsFromIngredient(base)).input(base, 4).input(frame).input(decoration, 4).offerTo(exporter, new Identifier("pfm", output.asItem().getTranslationKey().replace("block.pfm.", "")));
    }
    public static void offerCutleryRecipe(ItemConvertible output, Ingredient base, Consumer<RecipeJsonProvider> exporter) {
        SimpleFurnitureRecipeJsonFactory.create(output, 4).group("kitchen").criterion("has_" + getItemPath(base), conditionsFromIngredient(base)).input(base, 4).offerTo(exporter, new Identifier("pfm", output.asItem().getTranslationKey().replace("block.pfm.", "")));
    }

    public static void offerPendantRecipe(ItemConvertible output, Ingredient base,Ingredient hang, Consumer<RecipeJsonProvider> exporter) {
        SimpleFurnitureRecipeJsonFactory.create(output, 4).group("lighting").criterion("has_" + getItemPath(base), conditionsFromIngredient(base)).input(base, 2).input(hang, 2).input(PaladinFurnitureModBlocksItems.SIMPLE_LIGHT).offerTo(exporter, new Identifier("pfm", output.asItem().getTranslationKey().replace("block.pfm.", "")));
    }

    public static void offerToiletRecipe(ItemConvertible output, Ingredient base, Ingredient material, Consumer<RecipeJsonProvider> exporter) {
        SimpleFurnitureRecipeJsonFactory.create(output, 1).group("bathroom").criterion("has_" + getItemPath(material), conditionsFromIngredient(material)).input(base).input(material, 4).input(Ingredient.ofItems(Items.BUCKET)).offerTo(exporter, new Identifier("pfm", output.asItem().getTranslationKey().replace("block.pfm.", "")));
    }

    public static void offerWallToiletPaperRecipe(ItemConvertible output, Ingredient base, Consumer<RecipeJsonProvider> exporter) {
        SimpleFurnitureRecipeJsonFactory.create(output, 1).group("bathroom").criterion("has_" + getItemPath(Items.PAPER), conditionsFromItem(Items.PAPER)).input(base, 1).input(Items.PAPER, 8).offerTo(exporter, new Identifier("pfm", output.asItem().getTranslationKey().replace("block.pfm.", "")));
    }

    public static void offerSinkRecipe(ItemConvertible output, Ingredient base, Consumer<RecipeJsonProvider> exporter) {
        SimpleFurnitureRecipeJsonFactory.create(output, 1).group("bathroom").criterion("has_" + getItemPath(base), conditionsFromIngredient(base)).input(base, 3).input(Items.STONE_BUTTON, 2).input(Items.IRON_INGOT, 1).input(Items.BUCKET, 1).offerTo(exporter, new Identifier("pfm", output.asItem().getTranslationKey().replace("block.pfm.", "")));
    }

    public static void offerBathtubRecipe(ItemConvertible output, Ingredient base, Consumer<RecipeJsonProvider> exporter) {
        SimpleFurnitureRecipeJsonFactory.create(output, 1).group("bathroom").criterion("has_" + getItemPath(base), conditionsFromIngredient(base)).input(base, 5).input(Items.STONE_BUTTON, 2).input(Items.BUCKET, 1).offerTo(exporter, new Identifier("pfm", output.asItem().getTranslationKey().replace("block.pfm.", "")));
    }

    public static void offerShowerHeadRecipe(ItemConvertible output, Ingredient base, Consumer<RecipeJsonProvider> exporter) {
        SimpleFurnitureRecipeJsonFactory.create(output, 1).group("bathroom").criterion("has_" + getItemPath(base), conditionsFromIngredient(base)).input(base, 1).input(Items.REDSTONE, 1).input(Items.IRON_INGOT, 1).offerTo(exporter, new Identifier("pfm", output.asItem().getTranslationKey().replace("block.pfm.", "")));
    }

    public static void offerShowerHandleRecipe(ItemConvertible output, Ingredient base, Consumer<RecipeJsonProvider> exporter) {
        SimpleFurnitureRecipeJsonFactory.create(output, 1).group("bathroom").criterion("has_" + getItemPath(base), conditionsFromIngredient(base)).input(base, 2).input(Items.REDSTONE, 1).input(Items.IRON_INGOT, 1).offerTo(exporter, new Identifier("pfm", output.asItem().getTranslationKey().replace("block.pfm.", "")));
    }

    public static void offerShowerTowelRecipe(ItemConvertible output, Ingredient base, Consumer<RecipeJsonProvider> exporter) {
        SimpleFurnitureRecipeJsonFactory.create(output, 2).group("bathroom").criterion("has_" + getItemPath(base), conditionsFromIngredient(base)).input(base,4).input(Ingredient.ofItems(Items.LIGHT_GRAY_CONCRETE), 2).offerTo(exporter, new Identifier("pfm", output.asItem().getTranslationKey().replace("block.pfm.", "")));
    }

    public static void offerMirrorRecipe(ItemConvertible output, Ingredient base, Consumer<RecipeJsonProvider> exporter) {
        SimpleFurnitureRecipeJsonFactory.create(output, 2).group("bathroom").criterion("has_" + getItemPath(base), conditionsFromIngredient(base)).input(base,3).input(Ingredient.ofItems(Items.GLASS), 2).offerTo(exporter, new Identifier("pfm", output.asItem().getTranslationKey().replace("block.pfm.", "")));
    }

    private static InventoryChangedCriterion.Conditions conditionsFromItem(NumberRange.IntRange count, ItemConvertible item) {
        return conditionsFromItemPredicates(ItemPredicate.Builder.create().item(item).build());
    }

    public static InventoryChangedCriterion.Conditions conditionsFromItem(ItemConvertible item) {
        return conditionsFromItemPredicates(ItemPredicate.Builder.create().item(item).build());
    }

    public static InventoryChangedCriterion.Conditions conditionsFromIngredient(Ingredient item) {
        List<Item> items = new ArrayList<>();
        for (ItemStack item1:
                pfm$getMatchingStacks(item)) {
            if (items.contains(item1.getItem()))
                continue;
            items.add(item1.getItem());
        }
        return conditionsFromItemPredicates(ItemPredicate.Builder.create().item(items.get(0)).build());
    }

    public static ItemStack[] pfm$getMatchingStacks(Ingredient ingredient) {
        PFMIngredientMatchingStacksAccessor accessor = (PFMIngredientMatchingStacksAccessor)(Object)ingredient;
        if (accessor.getMatchingStacks() == null)
            accessor.invokeCacheMatchingStacks();
        return accessor.getMatchingStacks();
    }

    private static InventoryChangedCriterion.Conditions conditionsFromTag(Tag<Item> tag) {
        return conditionsFromItemPredicates(ItemPredicate.Builder.create().tag(tag).build());
    }

    private static InventoryChangedCriterion.Conditions conditionsFromItemPredicates(ItemPredicate ... items) {
        return new InventoryChangedCriterion.Conditions(EntityPredicate.Extended.EMPTY, NumberRange.IntRange.ANY, NumberRange.IntRange.ANY, NumberRange.IntRange.ANY, items);
    }

    private static String getItemPath(Ingredient item) {
        ItemStack[] n = pfm$getMatchingStacks(item);
        if (n.length > 0) {
            return Registry.ITEM.getId(n[0].getItem()).getPath();
        } else {
            return item.toString();
        }
    }
    private static String getItemPath(ItemConvertible item) {
        return Registry.ITEM.getId(item.asItem()).getPath();
    }
}
