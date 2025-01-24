package com.unlikepaladin.pfm.recipes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.blocks.RawLogTableBlock;
import com.unlikepaladin.pfm.data.materials.VariantBase;
import com.unlikepaladin.pfm.data.materials.VariantHelper;
import com.unlikepaladin.pfm.data.materials.WoodVariant;
import com.unlikepaladin.pfm.registry.PaladinFurnitureModBlocksItems;
import com.unlikepaladin.pfm.registry.RecipeTypes;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.World;

import java.util.*;


public class DynamicFurnitureRecipe implements FurnitureRecipe {
    private final Identifier id;
    private final String group;
    private final FurnitureOutput furnitureOutput;
    private final List<Identifier> supportedVariants;
    private final FurnitureIngredients ingredients;
    public DynamicFurnitureRecipe(Identifier id, String group, FurnitureOutput furnitureOutput, List<Identifier> supportedVariants, FurnitureIngredients furnitureIngredients) {
        this.id = id;
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

            NbtCompound outputCompound = furnitureOutput.nbt != null ? furnitureOutput.nbt.copy() : null;

            if (outputCompound != null && outputCompound.contains("color")) {
                optionalOutput = PaladinFurnitureMod.furnitureEntryMap.get(getOutputBlockClass()).getEntryFromVariantAndColor(variant, DyeColor.byName(outputCompound.getString("color"), DyeColor.WHITE));
                outputCompound.remove("color");
            } else {
                optionalOutput = PaladinFurnitureMod.furnitureEntryMap.get(getOutputBlockClass()).getEntryFromVariant(variant);
            }
            if (optionalOutput.isEmpty()) continue;

            if (outputCompound != null && outputCompound.contains("variantInNbt") && outputCompound.getBoolean("variantInNbt")) {
                NbtCompound compound;
                 if (outputCompound.contains("BlockEntityTag", NbtElement.COMPOUND_TYPE))
                     compound = outputCompound.getCompound("BlockEntityTag");
                 else {
                     compound = new NbtCompound();
                     outputCompound.put("BlockEntityTag", compound);
                }
                compound.putString("variant", id.toString());
            }
            ItemStack output = new ItemStack(optionalOutput.get().asItem(), furnitureOutput.getOutputCount());
            if (outputCompound != null && !outputCompound.isEmpty())
                output.setNbt(outputCompound.copy());

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
                    if (outputCompound != null  && !outputCompound.isEmpty())
                        output.setNbt(outputCompound.copy());

                    FurnitureInnerRecipe stripped = new FurnitureInnerRecipe(this, strippedOutput, strippedIngredients);
                    recipes.add(stripped);
                }
            }
            furnitureInnerRecipes.put(id, recipes);
        }
    }

    @Override
    public boolean matches(PlayerInventory inventory, World world) {
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
    public List<CraftableFurnitureRecipe> getAvailableOutputs(PlayerInventory inventory, DynamicRegistryManager registryManager) {
        constructInnerRecipes();
        List<CraftableFurnitureRecipe> stacks = Lists.newArrayList();
        for (Identifier id : furnitureInnerRecipes.keySet()) {
            List<FurnitureInnerRecipe> recipes = furnitureInnerRecipes.get(id);
            for (FurnitureInnerRecipe recipe : recipes) {
                if (recipe.matches(inventory, inventory.player.getWorld()))
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
    public ItemStack craft(PlayerInventory inventory, DynamicRegistryManager registryManager) {
        PaladinFurnitureMod.GENERAL_LOGGER.warn("Something has tried to craft a dynamic furniture recipe without context");
        return ItemStack.EMPTY;
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager registryManager) {
        PaladinFurnitureMod.GENERAL_LOGGER.warn("Something has tried to get the output of a dynamic furniture recipe without context");
        return ItemStack.EMPTY;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeTypes.DYNAMIC_FURNITURE_SERIALIZER;
    }

    protected Class<? extends Block> getOutputBlockClass() {
        try {
            return (Class<? extends Block>) Class.forName("com.unlikepaladin.pfm.blocks."+furnitureOutput.getOutputClass());
        } catch (ClassNotFoundException | ClassCastException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Identifier> getSupportedVariants() {
        return supportedVariants;
    }

    @Override
    public int getOutputCount(DynamicRegistryManager registryManager) {
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
    public String getName(DynamicRegistryManager registryManager) {
        return outputClass().replaceAll("(?<=[a-z])(?=[A-Z])", " ");
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
        public ItemStack getOutput(DynamicRegistryManager registryManager) {
            return output;
        }

        @Override
        public List<Ingredient> getIngredients() {
            return combinedIngredients;
        }

        @Override
        public boolean matches(PlayerInventory inventory, World world) {
            List<Ingredient> allIngredients = getIngredients();
            BitSet hasIngredient = new BitSet(allIngredients.size());
            for (int i = 0; i < allIngredients.size(); i++) {
                Ingredient ingredient = allIngredients.get(i);
                for (ItemStack stack : ingredient.getMatchingStacks()) {
                    int countInInventory = inventory.count(stack.getItem());
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
        public ItemStack craft(PlayerInventory inventory, DynamicRegistryManager registryManager) {
            return output.copy();
        }

        @Override
        public ItemStack getRecipeOuput() {
            return output;
        }
    }

    public static class FurnitureOutput {
        private final String outputClass;
        private final int outputCount;
        private final NbtCompound nbt;

        private FurnitureOutput(String outputClass, int outputCount, NbtCompound nbt) {
            this.outputClass = outputClass;
            this.outputCount = outputCount;
            this.nbt = nbt;
        }

        public int getOutputCount() {
            return outputCount;
        }

        public NbtCompound getNbt() {
            return nbt;
        }

        public String getOutputClass() {
            return outputClass;
        }

        public static FurnitureOutput read(JsonObject json) {
            NbtCompound nbtCompound = null;
            if (json.has("tag")) {
                nbtCompound = new NbtCompound();
                for(Map.Entry<String, JsonElement> jsonObject : json.get("tag").getAsJsonObject().entrySet()) {
                    nbtCompound.put(jsonObject.getKey(), JsonOps.INSTANCE.convertTo(NbtOps.INSTANCE, jsonObject.getValue()));
                }
            }
            return new FurnitureOutput(JsonHelper.getString(json, "outputClass"), JsonHelper.getInt(json, "count", 1), nbtCompound);
        }

        public static FurnitureOutput read(PacketByteBuf buf) {
            String outputClass = buf.readString();
            int count = buf.readInt();
            NbtCompound nbt = buf.readNbt();
            return new FurnitureOutput(outputClass, count, nbt);
        }

        public static void write(PacketByteBuf buf, FurnitureOutput output) {
            buf.writeString(output.outputClass);
            buf.writeInt(output.outputCount);
            buf.writeNbt(output.nbt);
        }

    }

    public static final class FurnitureIngredients {
        private final List<Ingredient> vanillaIngredients;
        private final Map<String, Integer> variantChildren;

        public FurnitureIngredients(List<Ingredient> vanillaIngredients, Map<String, Integer> variantChildren) {
            this.vanillaIngredients = vanillaIngredients;
            this.variantChildren = variantChildren;
        }

        public static FurnitureIngredients read(JsonObject json) {
            List<Ingredient> vanillaIngredients = new ArrayList<>();
            json.getAsJsonArray("vanillaIngredients").forEach(element -> vanillaIngredients.add(Ingredient.fromJson(element)));
            Map<String, Integer> variantChildren = readChildrenCount(json.get("variantChildren").getAsJsonObject());
            return new FurnitureIngredients(vanillaIngredients, variantChildren);
        }

        public static FurnitureIngredients read(PacketByteBuf buf) {
            List<Ingredient> vanillaIngredients = buf.readCollection(Lists::newArrayListWithCapacity, Ingredient::fromPacket);
            Map<String, Integer> variantChildren = buf.readMap((PacketByteBuf::readString), (PacketByteBuf::readInt));
            return new FurnitureIngredients(vanillaIngredients, variantChildren);
        }

        private static Map<String, Integer> readChildrenCount(JsonObject json) {
            HashMap<String, Integer> map = Maps.newHashMap();
            for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
                map.put(entry.getKey(), entry.getValue().getAsInt());
            }
            return map;
        }

        public static void write(PacketByteBuf buf, FurnitureIngredients ingredients) {
            buf.writeCollection(ingredients.vanillaIngredients, ((packetByteBuf, ingredient) -> ingredient.write(buf)));
            buf.writeMap(ingredients.variantChildren, PacketByteBuf::writeString, PacketByteBuf::writeInt);
        }

    }

    public static class Serializer implements RecipeSerializer<DynamicFurnitureRecipe> {
       /* Codec<DynamicFurnitureRecipe> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
                Codec.STRING.optionalFieldOf("group", "").forGetter(DynamicFurnitureRecipe::getGroup),
                Codec.STRING.fieldOf("outputBlock").forGetter(DynamicFurnitureRecipe::getOutputBlock),
                Identifier.CODEC.listOf().fieldOf("variants").forGetter(DynamicFurnitureRecipe::getSupportedVariants)
        ).apply(instance, DynamicFurnitureRecipe::new));
        */

        @Override
        public DynamicFurnitureRecipe read(Identifier id, JsonObject json) {
            String group = JsonHelper.getString(json, "group", "");

            List<Identifier> supportedVariants = new ArrayList<>();
            JsonHelper.getArray(json, "supportedVariants").forEach(jsonElement -> supportedVariants.add(Identifier.tryParse(jsonElement.getAsString())));
            FurnitureIngredients ingredients = FurnitureIngredients.read(json);

            return new DynamicFurnitureRecipe(id, group, FurnitureOutput.read(json.getAsJsonObject("result")), supportedVariants, ingredients);
        }

        @Override
        public DynamicFurnitureRecipe read(Identifier id, PacketByteBuf buf) {
            String group = buf.readString();
            List<Identifier> supportedVariants = buf.readList(PacketByteBuf::readIdentifier);
            FurnitureIngredients ingredients = FurnitureIngredients.read(buf);
            FurnitureOutput output = FurnitureOutput.read(buf);
            return new DynamicFurnitureRecipe(id, group, output, supportedVariants, ingredients);
        }

        @Override
        public void write(PacketByteBuf buf, DynamicFurnitureRecipe recipe) {
            buf.writeString(recipe.group);
            buf.writeCollection(recipe.supportedVariants, PacketByteBuf::writeIdentifier);
            FurnitureIngredients.write(buf, recipe.ingredients);
            FurnitureOutput.write(buf, recipe.furnitureOutput);
        }
    }
}
