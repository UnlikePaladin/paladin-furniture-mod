package com.unlikepaladin.pfm.recipes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.blocks.RawLogTableBlock;
import com.unlikepaladin.pfm.data.materials.VariantBase;
import com.unlikepaladin.pfm.data.materials.VariantHelper;
import com.unlikepaladin.pfm.data.materials.WoodVariant;
import com.unlikepaladin.pfm.items.PFMComponents;
import com.unlikepaladin.pfm.registry.RecipeTypes;
import net.minecraft.block.Block;
import net.minecraft.component.*;
import net.minecraft.component.ComponentMap;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.World;

import java.util.*;

public class DynamicFurnitureRecipe implements FurnitureRecipe {
    private final String group;
    private final FurnitureOutput furnitureOutput;
    private final List<Identifier> supportedVariants;
    private final FurnitureIngredients ingredients;
    public DynamicFurnitureRecipe(String group, FurnitureOutput furnitureOutput, List<Identifier> supportedVariants, FurnitureIngredients furnitureIngredients) {
        this.group = group;
        this.furnitureOutput = furnitureOutput;
        this.supportedVariants = supportedVariants;
        this.ingredients = furnitureIngredients;
    }

    Map<Identifier, List<FurnitureInnerRecipe>> furnitureInnerRecipes = Maps.newHashMap();
    public void constructInnerRecipes() {
        if (!furnitureInnerRecipes.isEmpty()) return;

        for (Identifier id : supportedVariants) {
            VariantBase<?> variant = VariantHelper.getVariant(id);
            if (variant == null || furnitureInnerRecipes.containsKey(id)) continue;
            Optional<Block> optionalOutput;
            ComponentChanges componentChanges = furnitureOutput.components != null ? furnitureOutput.components : ComponentChanges.EMPTY;
            ComponentMap.Builder builder = ComponentMap.builder();

            if (!componentChanges.isEmpty() && componentChanges.entrySet().stream().anyMatch(dataComponentTypeOptionalEntry -> dataComponentTypeOptionalEntry.getKey() == PFMComponents.COLOR_COMPONENT)) {
                optionalOutput = PaladinFurnitureMod.furnitureEntryMap.get(getOutputBlockClass()).getEntryFromVariantAndColor(variant, componentChanges.get(PFMComponents.COLOR_COMPONENT).get());
                if (!optionalOutput.get().asItem().getComponents().contains(PFMComponents.COLOR_COMPONENT)) {
                    componentChanges = componentChanges.withRemovedIf(dataComponentType -> dataComponentType == PFMComponents.COLOR_COMPONENT);
                    ComponentMapImpl.create(optionalOutput.get().asItem().getComponents(), componentChanges);
                }
                else
                    builder.addAll(ComponentMapImpl.create(optionalOutput.get().asItem().getComponents(), componentChanges));
            } else {
                optionalOutput = PaladinFurnitureMod.furnitureEntryMap.get(getOutputBlockClass()).getEntryFromVariant(variant);
                builder.addAll(ComponentMapImpl.create(optionalOutput.get().asItem().getComponents(), componentChanges));
            }
            if (optionalOutput.isEmpty()) continue;

            if (optionalOutput.get().asItem().getComponents().contains(PFMComponents.VARIANT_COMPONENT)) {
                builder.add(PFMComponents.VARIANT_COMPONENT, variant.identifier);
            }

            ItemStack output = new ItemStack(optionalOutput.get().asItem(), furnitureOutput.getOutputCount());
            ComponentMap finalComponents = builder.build();
            if (!finalComponents.isEmpty())
                output.applyComponentsFrom(builder.build());

            Map<String, Integer> childrenToCountMap = ingredients.variantChildren;

            List<Ingredient> stacks = Lists.newArrayList();
            for (Map.Entry<String, Integer> entry : childrenToCountMap.entrySet()) {
                stacks.add(Ingredient.ofStacks(new ItemStack(variant.getItemForRecipe(entry.getKey(), getOutputBlockClass()), entry.getValue())));
            }

            List<FurnitureInnerRecipe> recipes = new ArrayList<>();

            FurnitureInnerRecipe recipe = new FurnitureInnerRecipe(this, output, stacks);
            recipes.add(recipe);


            if (variant instanceof WoodVariant woodVariant && woodVariant.hasStripped()) {
                List<Ingredient> strippedIngredients = Lists.newArrayList();
                for (Map.Entry<String, Integer> entry : childrenToCountMap.entrySet()) {
                    strippedIngredients.add(Ingredient.ofStacks(new ItemStack(woodVariant.getItemForRecipe(entry.getKey(), getOutputBlockClass(), true), entry.getValue())));
                }
                if (getOutputBlockClass() == RawLogTableBlock.class) {
                    strippedIngredients.set(0, Ingredient.ofItems((Block)woodVariant.getChild("stripped_log")));
                }

                Optional<Block> strippedOptional = PaladinFurnitureMod.furnitureEntryMap.get(getOutputBlockClass()).getEntryFromVariant(variant, true);
                if (strippedOptional.isPresent()) {

                    ItemStack strippedOutput = new ItemStack(strippedOptional.get(), furnitureOutput.getOutputCount());
                    if (!finalComponents.isEmpty())
                        strippedOutput.applyComponentsFrom(finalComponents);

                    FurnitureInnerRecipe stripped = new FurnitureInnerRecipe(this, strippedOutput, strippedIngredients);
                    recipes.add(stripped);
                }
            }
            furnitureInnerRecipes.put(id, recipes);
        }
    }

    @Override
    public boolean matches(FurnitureRecipe.FurnitureRecipeInput inventory, World world) {
        constructInnerRecipes();

        for (Identifier id : furnitureInnerRecipes.keySet()) {
            List<FurnitureInnerRecipe> recipes = furnitureInnerRecipes.get(id);
            for (FurnitureInnerRecipe recipe : recipes) {
                if (recipe.matches(inventory, world))
                    return true;
            }
        }
        return false;
    }

    @Override
    public List<CraftableFurnitureRecipe> getAvailableOutputs(FurnitureRecipe.FurnitureRecipeInput input, RegistryWrapper.WrapperLookup registryManager) {
        constructInnerRecipes();
        PlayerInventory inventory = input.playerInventory();
        List<CraftableFurnitureRecipe> stacks = Lists.newArrayList();
        for (Identifier id : furnitureInnerRecipes.keySet()) {
            List<FurnitureInnerRecipe> recipes = furnitureInnerRecipes.get(id);
            for (FurnitureInnerRecipe recipe : recipes) {
                if (recipe.matches(input, inventory.player.getWorld()))
                    stacks.add(recipe);
            }
        }
        return stacks;
    }

    @Override
    public List<CraftableFurnitureRecipe> getInnerRecipes() {
        constructInnerRecipes();
        List<CraftableFurnitureRecipe> outputs = new ArrayList<>();
        for (List<FurnitureInnerRecipe> recipes : furnitureInnerRecipes.values())
            outputs.addAll(recipes);
        return outputs;
    }

    @Override
    public String outputClass() {
        return furnitureOutput.outputClass;
    }

    @Override
    public ItemStack craft(FurnitureRecipe.FurnitureRecipeInput inventory, RegistryWrapper.WrapperLookup registryManager) {
        PaladinFurnitureMod.GENERAL_LOGGER.warn("Something has tried to craft a dynamic furniture recipe without context");
        return ItemStack.EMPTY;
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registryManager) {
        PaladinFurnitureMod.GENERAL_LOGGER.warn("Something has tried to get the output of a dynamic furniture recipe without context");
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeTypes.DYNAMIC_FURNITURE_SERIALIZER;
    }

    protected Class<? extends Block> getOutputBlockClass() {
        return FurnitureOutput.getOutputBlockClass(furnitureOutput.outputClass);
    }

    public List<Identifier> getSupportedVariants() {
        return supportedVariants;
    }

    @Override
    public int getOutputCount(RegistryWrapper.WrapperLookup registryManager) {
        return furnitureOutput.getOutputCount();
    }

    @Override
    public CraftableFurnitureRecipe getInnerRecipeFromOutput(ItemStack stack) {
        constructInnerRecipes();
        if(outputToInnerRecipe.containsKey(stack)) {
            return outputToInnerRecipe.get(stack);
        }
        return outputItemToInnerRecipe.get(stack.getItem());
    }

    @Override
    public int getMaxInnerRecipeSize() {
        return ingredients.vanillaIngredients.size()+ingredients.variantChildren.size();
    }

    @Override
    public List<? extends CraftableFurnitureRecipe> getInnerRecipesForVariant(Identifier identifier){
        constructInnerRecipes();
        if (furnitureInnerRecipes.containsKey(identifier)) {
            return furnitureInnerRecipes.get(identifier);
        }
        return List.of();
    }


    @Override
    public String getName(RegistryWrapper.WrapperLookup registryManager) {
        return outputClass().replaceAll("(?<=[a-z])(?=[A-Z])", " ");
    }

    private FurnitureOutput getOutput() {
        return this.furnitureOutput;
    }

    private FurnitureIngredients getInnerIngredients() {
        return this.ingredients;
    }

    Map<ItemStack, FurnitureInnerRecipe> outputToInnerRecipe = new HashMap<>();
    Map<Item, FurnitureInnerRecipe> outputItemToInnerRecipe = new HashMap<>();
    public static final class FurnitureInnerRecipe implements CraftableFurnitureRecipe {
        private final DynamicFurnitureRecipe parentRecipe;
        private final ItemStack output;
        private final List<Ingredient> ingredients;
        private final List<Ingredient> combinedIngredients;
        public FurnitureInnerRecipe(DynamicFurnitureRecipe parentRecipe, ItemStack output, List<Ingredient> ingredients) {
            this.parentRecipe = parentRecipe;
            this.output = output;
            this.ingredients = ingredients;
            this.combinedIngredients = Lists.newArrayList();
            this.combinedIngredients.addAll(ingredients);
            this.combinedIngredients.addAll(parentRecipe.ingredients.vanillaIngredients);
            parentRecipe.outputToInnerRecipe.put(output, this);
            parentRecipe.outputItemToInnerRecipe.put(output.getItem(), this);
        }

        @Override
        public ItemStack getResult(RegistryWrapper.WrapperLookup registryManager) {
            return output;
        }

        @Override
        public List<Ingredient> getIngredients() {
            return combinedIngredients;
        }

        @Override
        public boolean matches(FurnitureRecipe.FurnitureRecipeInput inventory, World world) {
            List<Ingredient> allIngredients = getIngredients();
            BitSet hasIngredient = new BitSet(allIngredients.size());
            for (int i = 0; i < allIngredients.size(); i++) {
                Ingredient ingredient = allIngredients.get(i);
                for (ItemStack stack : ingredient.getMatchingStacks()) {
                    int countInInventory = inventory.playerInventory().count(stack.getItem());
                    if (countInInventory >= stack.getCount()) {
                        hasIngredient.set(i, true);
                        break;
                    }
                }
            }

            // Compares the numbers of bits that are true to the size
            return hasIngredient.cardinality() == allIngredients.size();
        }

        @Override
        public FurnitureRecipe parent() {
            return parentRecipe;
        }

        @Override
        public ItemStack craft(FurnitureRecipe.FurnitureRecipeInput inventory, RegistryWrapper.WrapperLookup registryManager) {
            return output.copy();
        }

        @Override
        public ItemStack getRecipeOuput() {
            return output;
        }
    }

    public static class FurnitureOutput {

        public static MapCodec<FurnitureOutput> CODEC = RecordCodecBuilder.mapCodec(furnitureOutputInstance -> furnitureOutputInstance.group(
                Codec.STRING.fieldOf("outputClass").forGetter(out -> out.outputClass),
                Codec.INT.optionalFieldOf("count", 1).forGetter(out -> out.outputCount),
                ComponentChanges.CODEC.optionalFieldOf("components", ComponentChanges.EMPTY).forGetter(out -> out.components)
        ).apply(furnitureOutputInstance, FurnitureOutput::new));

        private final String outputClass;
        private final int outputCount;
        private final ComponentChanges components;

        public FurnitureOutput(String outputClass, int outputCount, ComponentChanges components) {
            this.outputClass = outputClass;
            this.outputCount = outputCount;
            this.components = components;
        }

        public int getOutputCount() {
            return outputCount;
        }

        public ComponentChanges getComponents() {
            return components;
        }

        public String getOutputClass() {
            return outputClass;
        }

        public static FurnitureOutput read(RegistryByteBuf buf) {
            String outputClass = buf.readString();
            int count = buf.readInt();
            ComponentChanges componentChanges = ComponentChanges.PACKET_CODEC.decode(buf);
            return new FurnitureOutput(outputClass, count,  componentChanges);
        }

        public static void write(RegistryByteBuf buf, FurnitureOutput output) {
            buf.writeString(output.outputClass);
            buf.writeInt(output.outputCount);
            ComponentChanges.PACKET_CODEC.encode(buf, output.components);
        }

        public static Class<? extends Block> getOutputBlockClass(String outputClass) {
            try {
                return (Class<? extends Block>) Class.forName("com.unlikepaladin.pfm.blocks."+outputClass);
            } catch (ClassNotFoundException | ClassCastException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static final class FurnitureIngredients {
        public static Codec<FurnitureIngredients> CODEC = RecordCodecBuilder.create(furnitureIngredientsInstance -> furnitureIngredientsInstance.group(
                Ingredient.DISALLOW_EMPTY_CODEC.listOf().optionalFieldOf("vanillaIngredients", new ArrayList<>()).forGetter(ingredients -> ingredients.vanillaIngredients),
                Codecs.strictUnboundedMap(Codec.STRING, Codec.INT).fieldOf("variantChildren").forGetter(ingredients -> ingredients.variantChildren)
        ).apply(furnitureIngredientsInstance, FurnitureIngredients::new));

        private final List<Ingredient> vanillaIngredients;
        private final Map<String, Integer> variantChildren;

        public FurnitureIngredients(List<Ingredient> vanillaIngredients, Map<String, Integer> variantChildren) {
            this.vanillaIngredients = vanillaIngredients;
            this.variantChildren = variantChildren;
        }

        public static FurnitureIngredients read(RegistryByteBuf buf) {
            List<Ingredient> vanillaIngredients = buf.readCollection(Lists::newArrayListWithCapacity, buf1 -> Ingredient.PACKET_CODEC.decode(buf));
            Map<String, Integer> variantChildren = buf.readMap((PacketByteBuf::readString), (PacketByteBuf::readInt));
            return new FurnitureIngredients(vanillaIngredients, variantChildren);
        }

        public static void write(RegistryByteBuf buf, FurnitureIngredients ingredients) {
            buf.writeCollection(ingredients.vanillaIngredients, ((packetByteBuf, ingredient) -> Ingredient.PACKET_CODEC.encode((RegistryByteBuf) packetByteBuf, ingredient)));
            buf.writeMap(ingredients.variantChildren, PacketByteBuf::writeString, PacketByteBuf::writeInt);
        }
    }

    public static class Serializer implements RecipeSerializer<DynamicFurnitureRecipe> {
        MapCodec<DynamicFurnitureRecipe> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
                Codec.STRING.optionalFieldOf("group", "").forGetter(DynamicFurnitureRecipe::getGroup),
                FurnitureOutput.CODEC.fieldOf("result").forGetter(DynamicFurnitureRecipe::getOutput),
                Identifier.CODEC.listOf().fieldOf("supportedVariants").forGetter(DynamicFurnitureRecipe::getSupportedVariants),
                FurnitureIngredients.CODEC.fieldOf("ingredients").forGetter(DynamicFurnitureRecipe::getInnerIngredients)
        ).apply(instance, DynamicFurnitureRecipe::new));


        public static final PacketCodec<RegistryByteBuf, DynamicFurnitureRecipe> PACKET_CODEC = PacketCodec.ofStatic(
                Serializer::write, Serializer::read
        );

        @Override
        public MapCodec<DynamicFurnitureRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, DynamicFurnitureRecipe> packetCodec() {
            return PACKET_CODEC;
        }

        public static DynamicFurnitureRecipe read(RegistryByteBuf buf) {
            String group = buf.readString();
            List<Identifier> supportedVariants = buf.readList(PacketByteBuf::readIdentifier);
            FurnitureIngredients ingredients = FurnitureIngredients.read(buf);
            FurnitureOutput output = FurnitureOutput.read(buf);
            return new DynamicFurnitureRecipe(group, output, supportedVariants, ingredients);
        }

        public static void write(RegistryByteBuf buf, DynamicFurnitureRecipe recipe) {
            buf.writeString(recipe.group);
            buf.writeCollection(recipe.supportedVariants, PacketByteBuf::writeIdentifier);
            FurnitureIngredients.write(buf, recipe.ingredients);
            FurnitureOutput.write(buf, recipe.furnitureOutput);
        }


    }
}
