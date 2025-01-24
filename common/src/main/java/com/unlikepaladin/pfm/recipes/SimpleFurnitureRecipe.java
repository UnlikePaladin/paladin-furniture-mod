package com.unlikepaladin.pfm.recipes;

import com.google.gson.*;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.registry.PaladinFurnitureModBlocksItems;
import com.unlikepaladin.pfm.registry.RecipeTypes;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.*;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.registry.Registry;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.World;

import java.util.*;

public class SimpleFurnitureRecipe implements FurnitureRecipe, FurnitureRecipe.CraftableFurnitureRecipe {
    final String group;
    final ItemStack output;
    final DefaultedList<Ingredient> input;

    public SimpleFurnitureRecipe(String group, ItemStack output, List<Ingredient> input) {
        this.group = group;
        this.output = output;
        this.input = DefaultedList.copyOf(Ingredient.EMPTY, input.toArray(Ingredient[]::new));
    }

    @Override
    public DefaultedList<Ingredient> getIngredients() {
        return this.input;
    }

    @Override
    public boolean matches(PlayerInventory playerInventory, World world) {
        List<Ingredient> ingredients = this.getIngredients();
        BitSet hasIngredients = new BitSet(ingredients.size());

        for (int i = 0; i < ingredients.size(); i++) {
            Ingredient ingredient = ingredients.get(i);
            for (ItemStack stack : ingredient.getMatchingStacks()) {
                if (playerInventory.count(stack.getItem()) >= stack.getCount()) {
                    hasIngredients.set(i);
                    break;
                }
            }
        }
        return hasIngredients.cardinality() == ingredients.size();
    }

    @Override
    public FurnitureRecipe parent() {
        return this;
    }

    @Override
    public ItemStack getRecipeOuput() {
        return output;
    }


    @Override
    public ItemStack craft(PlayerInventory playerInventory, DynamicRegistryManager registryManager) {
        if (this.output.getNbt() != null && this.output.getNbt().isEmpty()) {
            ItemStack stack = this.output.copy();
            stack.setNbt(null);
            return stack;
        }
        return this.output.copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public String getGroup() {
        return this.group;
    }

    @Override
    public ItemStack getResult(DynamicRegistryManager registryManager) {
        return this.output;
    }

    @Override
    public ItemStack createIcon() {
        return PaladinFurnitureModBlocksItems.WORKING_TABLE.asItem().getDefaultStack();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeTypes.SIMPLE_FURNITURE_SERIALIZER;
    }

    @Override
    public String outputClass() {
        return this.output.getItem().getClass().getSimpleName();
    }

    @Override
    public boolean isIgnoredInRecipeBook() {
        return true;
    }

    @Override
    public List<CraftableFurnitureRecipe> getInnerRecipes() {
        return Collections.singletonList(this);
    }

    public static class Serializer
            implements RecipeSerializer<SimpleFurnitureRecipe> {

        Codec<SimpleFurnitureRecipe> CODEC = RecordCodecBuilder.create(simpleFurnitureRecipeInstance ->
                simpleFurnitureRecipeInstance.group(
                        Codec.STRING.optionalFieldOf("group", "").forGetter(SimpleFurnitureRecipe::getGroup),
                        FURNITURE_RESULT.fieldOf("result").forGetter(recipe -> recipe.output),
                        Ingredient.DISALLOW_EMPTY_CODEC.listOf().fieldOf("ingredients").forGetter(SimpleFurnitureRecipe::getIngredients))
                        .apply(simpleFurnitureRecipeInstance, SimpleFurnitureRecipe::new)
        );

        private static final Codec<Item> CRAFTING_RESULT_ITEM = Codecs.validate(Registries.ITEM.getCodec(), (item) -> {
            return item == Items.AIR ? DataResult.error(() -> {
                return "Crafting result must not be minecraft:air";
            }) : DataResult.success(item);
        });

        private static final Codec<NbtCompound> NBT_CODEC = Codecs.xor(
                Codec.STRING, NbtCompound.CODEC
        ).flatXmap(either -> either.map(s -> {
            try {
                return DataResult.success(StringNbtReader.parse(s));
            } catch (CommandSyntaxException e) {
                return DataResult.error(e::getMessage);
            }
        }, DataResult::success), nbtCompound -> DataResult.success(Either.left(nbtCompound.asString())));


        public static final Codec<NbtCompound> OUTPUT_TAGS = Codec.unboundedMap(Codec.STRING, NBT_CODEC).comapFlatMap(stringNbtCompoundMap -> {
            NbtCompound compound = new NbtCompound();
            stringNbtCompoundMap.forEach(compound::put);
            return DataResult.success(compound);
        }, nbtCompound -> {
            Map<String, NbtCompound> map = new HashMap<>();
            Set<String> keys = nbtCompound.getKeys();
            keys.forEach(s -> {
                NbtCompound compound = new NbtCompound();
                if (nbtCompound.get(s) instanceof NbtCompound) {
                    compound = nbtCompound.getCompound(s);
                } else {
                    compound.put(s, nbtCompound.get(s));
                }
                map.put(s, compound);
            });
            return map;
        });

        public static final Codec<ItemStack> FURNITURE_RESULT = RecordCodecBuilder.create((instance) -> {
            return instance.group(CRAFTING_RESULT_ITEM.fieldOf("item").forGetter(ItemStack::getItem), Codecs.createStrictOptionalFieldCodec(Codecs.POSITIVE_INT, "count", 1).forGetter(ItemStack::getCount), Codecs.createStrictOptionalFieldCodec(OUTPUT_TAGS, "tag", new NbtCompound()).forGetter(ItemStack::getNbt)).apply(instance, (item, integer, nbtElement) -> {
                ItemStack stack = new ItemStack(item, integer);
                stack.setNbt(nbtElement);
                return stack;
            });
        });

        @Override
        public Codec<SimpleFurnitureRecipe> codec() {
            return CODEC;
        }

        @Override
        public SimpleFurnitureRecipe read(PacketByteBuf packetByteBuf) {
            String string = packetByteBuf.readString();
            int i = packetByteBuf.readVarInt();
            DefaultedList<Ingredient> defaultedList = DefaultedList.ofSize(i, Ingredient.EMPTY);
            for (int j = 0; j < defaultedList.size(); ++j) {
                defaultedList.set(j, Ingredient.fromPacket(packetByteBuf));
            }
            ItemStack itemStack = packetByteBuf.readItemStack();
            return new SimpleFurnitureRecipe(string, itemStack, defaultedList);
        }

        @Override
        public void write(PacketByteBuf packetByteBuf, SimpleFurnitureRecipe simpleFurnitureRecipe) {
            packetByteBuf.writeString(simpleFurnitureRecipe.group);
            packetByteBuf.writeVarInt(simpleFurnitureRecipe.input.size());
            for (Ingredient ingredient : simpleFurnitureRecipe.input) {
                ingredient.write(packetByteBuf);
            }
            packetByteBuf.writeItemStack(simpleFurnitureRecipe.output);
        }
    }
}
