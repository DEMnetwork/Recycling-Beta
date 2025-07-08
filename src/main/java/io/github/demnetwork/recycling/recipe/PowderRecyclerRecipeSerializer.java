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

import java.util.List;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketDecoder;
import net.minecraft.network.codec.ValueFirstEncoder;
import net.minecraft.util.Identifier;

public class PowderRecyclerRecipeSerializer implements RecipeSerializer<PowderRecyclerRecipe> {

  public static final PowderRecyclerRecipeSerializer INSTANCE = new PowderRecyclerRecipeSerializer();
  public static final Identifier ID = Identifier.of("recycling", "powder_recycler");

  @Override
  public MapCodec<PowderRecyclerRecipe> codec() {
    return RecordCodecBuilder.mapCodec(instance -> instance.group(
        PowderRecyclerRecipe.INPUT_CODEC.fieldOf("input").forGetter(PowderRecyclerRecipe::getInput),
        PowderRecyclerRecipe.ItemStack_CODEC.listOf().fieldOf("output")
            .forGetter(recipe -> List.of(recipe.getOutput())))
        .apply(instance, (input, outputList) -> {
          ItemStack[] outputs = new ItemStack[9];
          for (int i = 0; i < outputs.length; i++) {
            outputs[i] = i < outputList.size() ? outputList.get(i) : ItemStack.EMPTY;
          }
          return new PowderRecyclerRecipe(input, outputs);
        }));
  }

  @Override
  public PacketCodec<RegistryByteBuf, PowderRecyclerRecipe> packetCodec() {
    return PacketCodec.of(new ValueFirstEncoder<>() {
      @Override
      public void encode(PowderRecyclerRecipe value, RegistryByteBuf buf) {
        buf.encodeAsJson(codec().codec(), value);
      }

    }, new PacketDecoder<>() {
      @Override
      public PowderRecyclerRecipe decode(RegistryByteBuf buf) {
        return buf.decodeAsJson(codec().codec());
      }
    });
  }
}