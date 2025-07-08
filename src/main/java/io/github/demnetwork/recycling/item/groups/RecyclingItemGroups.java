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

package io.github.demnetwork.recycling.item.groups;

import java.util.function.Supplier;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import io.github.demnetwork.recycling.item.RecyclingItems;

public class RecyclingItemGroups {
  public static final RegistryKey<ItemGroup> MAIN_GROUP_KEY = RegistryKey.of(Registries.ITEM_GROUP.getKey(),
      Identifier.of("recycling", "main"));
  public static final ItemGroup MAIN_GROUP = FabricItemGroup.builder()
      .icon(new Supplier<>() {

        @Override
        public ItemStack get() {
          return new ItemStack(RecyclingItems.RECYCLING_BLADES, 1);
        }

      })
      .displayName(Text.literal("Recycling"))
      .build();
  public static final RegistryKey<ItemGroup> MODIFIER_GROUP_KEY = RegistryKey.of(Registries.ITEM_GROUP.getKey(),
      Identifier.of("recycling", "modifier"));
  public static final ItemGroup MODIFIER_GROUP = FabricItemGroup.builder()
      .icon(new Supplier<>() {

        @Override
        public ItemStack get() {
          return new ItemStack(RecyclingItems.DIAMOND_MODIFIER, 1);
        }

      })
      .displayName(Text.translatable("group.recycling.modifier"))
      .build();

}
