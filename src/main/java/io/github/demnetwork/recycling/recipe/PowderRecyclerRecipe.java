/*
 *   Copyright (c) 2025 DEMnetwork
 *   All rights reserved.

 *   Permission is hereby granted, free of charge, to any person obtaining a copy
 *   of this software and associated documentation files (the "Software"), to deal
 *   in the Software without restriction, including without limitation the rights
 *   to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *   copies of the Software, and to permit persons to whom the Software is
 *   furnished to do so, subject to the following conditions:
 
 *   The above copyright notice and this permission notice shall be included in all
 *   copies or substantial portions of the Software.
 
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *   FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *   AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *   LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *   SOFTWARE.
 */

package io.github.demnetwork.recycling.recipe;

import static io.github.demnetwork.recycling.Recycling.LOGGER;
import static io.github.demnetwork.recycling.Recycling.MOD_ID;
import java.lang.reflect.Field;
import java.util.function.Function;
import com.mojang.datafixers.util.Function3;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.component.MergedComponentMap;
import io.github.demnetwork.recycling.Recycling;
import io.github.demnetwork.recycling.item.RecyclingItems;
import net.minecraft.component.ComponentChanges;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.World;

public class PowderRecyclerRecipe implements Recipe<PowderRecyclerRecipeInput> {
    private final ItemStack[] output;
    private final ItemStack input;
    public static final RecipeType<PowderRecyclerRecipe> POWDER_RECYCLER_RECIPE_TYPE = Registry
            .register(Registries.RECIPE_TYPE, Identifier.of(MOD_ID, "powder_recycler"), new RecipeType<>() {
            });
    public static final RecipeSerializer<PowderRecyclerRecipe> POWDER_RECYCLER_RECIPE_SERIALIZER = Registry.register(
            Registries.RECIPE_SERIALIZER, Identifier.of(Recycling.MOD_ID, "powder_recycler"),
            PowderRecyclerRecipeSerializer.INSTANCE);
    private static final ItemStack PLACEHOLDER_STACK = new ItemStack(RecyclingItems.PLACHOLDER_ITEM, 1);
    public static final Codec<ItemStack> ItemStack_CODEC = Codec.lazyInitialized(
            () -> RecordCodecBuilder.create(
                    instance -> instance.group(
                            Identifier.CODEC.fieldOf("item").forGetter(new Function<>() {

                                @Override
                                public Identifier apply(ItemStack t) {
                                    return Registries.ITEM.getId(t.getItem());
                                }

                            }),
                            Codecs.rangedInt(1, Integer.MAX_VALUE).fieldOf("count").orElse(1)
                                    .forGetter(ItemStack::getCount),
                            ComponentChanges.CODEC.optionalFieldOf("components", ComponentChanges.EMPTY)
                                    .forGetter(new Function<ItemStack, ComponentChanges>() {

                                        @SuppressWarnings("unchecked")
                                        @Override
                                        public ComponentChanges apply(ItemStack t) {
                                            try {
                                                Class<ItemStack> clazz = (Class<ItemStack>) t.getClass();
                                                Field f = clazz.getDeclaredField("component");
                                                f.setAccessible(true);
                                                MergedComponentMap map = (MergedComponentMap) f.get(t);
                                                return map.getChanges();
                                            } catch (Exception e) {
                                                throw new RuntimeException(e);
                                            }
                                        }

                                    }))
                            .apply(instance, new Function3<>() {

                                @Override
                                public ItemStack apply(Identifier t1, Integer t2, ComponentChanges t3) {
                                    Item item = Registries.ITEM.get(t1);
                                    if (item == null || item == Registries.ITEM.get(Identifier.ofVanilla("air"))) {
                                        Recycling.LOGGER
                                                .warn("Unknown item \'" + t1 + "\'. Replacing with Empty ItemStack");
                                        return ItemStack.EMPTY;
                                    }
                                    if (t2.intValue() > 4096)
                                        LOGGER.warn("Is count: " + t2.intValue()
                                                + "correct? High item counts may cause crashes");
                                    ItemStack iStack = new ItemStack(item, t2);
                                    iStack.applyChanges(t3);
                                    return iStack;
                                }
                            })));
    static final Codec<ItemStack> INPUT_CODEC = Codec.lazyInitialized(
            () -> RecordCodecBuilder.create(
                    instance -> instance.group(
                            Identifier.CODEC.fieldOf("item").forGetter(new Function<>() {

                                @Override
                                public Identifier apply(ItemStack t) {
                                    return Registries.ITEM.getId(t.getItem());
                                }

                            }),
                            Codecs.rangedInt(1, 1).fieldOf("count").orElse(1)
                                    .forGetter(ItemStack::getCount),
                            ComponentChanges.CODEC.optionalFieldOf("components", ComponentChanges.EMPTY)
                                    .forGetter(new Function<ItemStack, ComponentChanges>() {

                                        @SuppressWarnings("unchecked")
                                        @Override
                                        public ComponentChanges apply(ItemStack t) {
                                            try {
                                                Class<ItemStack> clazz = (Class<ItemStack>) t.getClass();
                                                Field f = clazz.getDeclaredField("component");
                                                f.setAccessible(true);
                                                MergedComponentMap map = (MergedComponentMap) f.get(t);
                                                return map.getChanges();
                                            } catch (Exception e) {
                                                throw new RuntimeException(e);
                                            }
                                        }

                                    }))
                            .apply(instance, new Function3<>() {

                                @Override
                                public ItemStack apply(Identifier t1, Integer t2, ComponentChanges t3) {
                                    Item item = Registries.ITEM.get(t1);
                                    if (item == null || item == Registries.ITEM.get(Identifier.ofVanilla("air"))) {
                                        Recycling.LOGGER
                                                .warn("Unknown item \'" + t1
                                                        + "\'. Replacing with a ItemStack with \'recycling:placholder\' item");
                                        return PLACEHOLDER_STACK;
                                    }
                                    ItemStack iStack = new ItemStack(item, t2);
                                    iStack.applyChanges(t3);
                                    return iStack;
                                }
                            })));

    public static final void register() {
        LOGGER.info("Registering \'recycling:powder_recycler\' recipe type, and Recipe Serializer");
    }

    public PowderRecyclerRecipe(ItemStack input, ItemStack[] output) {
        this.output = output.clone();
        this.input = input;
    }

    @Override
    public boolean matches(PowderRecyclerRecipeInput input, World world) {
        if (world.isClient)
            return false;
        return ItemStack.areItemsEqual(this.input, input.getStackInSlot(0));
    }

    @Override
    public ItemStack craft(PowderRecyclerRecipeInput input, WrapperLookup registries) {
        return ItemStack.EMPTY; // We don't implement it;
    }

    @Override
    public PowderRecyclerRecipeSerializer getSerializer() {
        return PowderRecyclerRecipeSerializer.INSTANCE;
    }

    @Override
    public RecipeType<? extends Recipe<PowderRecyclerRecipeInput>> getType() {
        return POWDER_RECYCLER_RECIPE_TYPE;
    }

    @Override
    public IngredientPlacement getIngredientPlacement() {
        return IngredientPlacement.forSingleSlot(net.minecraft.recipe.Ingredient.ofItems(input.getItem()));
    }

    public static Ingredient getIngredient(PowderRecyclerRecipe recipe) {
        return net.minecraft.recipe.Ingredient.ofItems(recipe.input.getItem());
    }

    @Override
    public RecipeBookCategory getRecipeBookCategory() {
        return null;
    }

    public ItemStack[] getOutput() {
        return output.clone();
    }

    public ItemStack getInput() {
        return this.input;
    }

}
