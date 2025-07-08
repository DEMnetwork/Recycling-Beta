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

package io.github.demnetwork.recycling.registry;

import static io.github.demnetwork.recycling.Recycling.LOGGER;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import io.github.demnetwork.recycling.item.RecyclingItems;
import io.github.demnetwork.recycling.recipe.PowderRecyclerModifer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class PowderRecyclerModifierRegistry {
  private static final ConcurrentHashMap<Item, PowderRecyclerModifer> map = new ConcurrentHashMap<>();
  private static boolean reg = false;
  private static final Random RND = new Random(((System.currentTimeMillis() * 176L) ^ 27237894L));
  public static final PowderRecyclerModifer DIAMOND_MODIFIER = register(RecyclingItems.DIAMOND_MODIFIER,
      new PowderRecyclerModifer() {

        @Override
        public ItemStack[] modifyOutput(ItemStack modifierStack, ItemStack[] outputs) {
          double d = RND.nextDouble();
          if (d > 0.4) {
            return outputs;
          }
          boolean mod = false;
          for (int i = 0; i < outputs.length; i++) {
            ItemStack output = outputs[i].copy();
            if (output.getItem() == Registries.ITEM.get(Identifier.ofVanilla("diamond"))
                || output.getItem() == Registries.ITEM.get(Identifier.ofVanilla("air")))
              continue;
            d = RND.nextDouble();
            if (d < (0.5d / Math.pow(1.5, output.getCount()))) {
              outputs[i] = new ItemStack(Registries.ITEM.getEntry(Identifier.ofVanilla("diamond")).get(),
                  output.getCount(), output.getComponentChanges());
              if (mod == false) {
                mod = true;
              }
              LOGGER.info("Modified stack(" + output.getItem().toString() + ")");
            }
          }
          if (mod == true) {
            LOGGER.info("Modifer got damaged");
            modifierStack.setDamage(modifierStack.getDamage() + 1);
          }
          return outputs;

        }

      });

  public static PowderRecyclerModifer register(Item item, PowderRecyclerModifer modifier) {
    map.put(item, modifier);
    return modifier;
  }

  public static PowderRecyclerModifer getModifer(ItemStack is) {
    return getModifer(is.getItem());
  }

  public static PowderRecyclerModifer getModifer(Item item) {
    return map.getOrDefault(item, null);
  }

  public static void register() {
    if (reg)
      return;
    reg = true;
    LOGGER.info("Registering Built-in Powder Recycler Modifiers");
  }

}
