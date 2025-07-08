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

package io.github.demnetwork.recycling.item;

import static io.github.demnetwork.recycling.Recycling.LOGGER;

import io.github.demnetwork.recycling.block.RecyclingBlocks;
import io.github.demnetwork.recycling.item.groups.RecyclingItemGroups;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public final class RecyclingItems {
    public static final Item RECYCLING_BLADES = regItem("blades",
            new Item(new Item.Settings()
                    .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of("recycling", "blades")))
                    .rarity(Rarity.COMMON)));
    public static final Item DIAMOND_MODIFIER = regItem("diamond_modifier",
            new Item(new Item.Settings()
                    .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of("recycling", "diamond_modifier")))
                    .rarity(Rarity.UNCOMMON).maxDamage(32).fireproof()));
    public static final Item PLACHOLDER_ITEM = regItem("placeholder",
            new Item(new Item.Settings()
                    .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of("recycling", "placeholder")))
                    .rarity(Rarity.EPIC).fireproof()));

    private static Item regItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of("recycling", name), item);
    }

    public static void itemReg() {
        LOGGER.info("Registering Recycling mod Items");
        Registry.register(Registries.ITEM_GROUP, RecyclingItemGroups.MAIN_GROUP_KEY,
                RecyclingItemGroups.MAIN_GROUP);
        ItemGroupEvents.modifyEntriesEvent(RecyclingItemGroups.MAIN_GROUP_KEY)
                .register(new ItemGroupEvents.ModifyEntries() {

                    @Override
                    public void modifyEntries(FabricItemGroupEntries entries) {
                        entries.add(RECYCLING_BLADES);
                        entries.add(RecyclingBlocks.POWDER_RECYCLER.asItem());
                    }
                });
        Registry.register(Registries.ITEM_GROUP, RecyclingItemGroups.MODIFIER_GROUP_KEY,
                RecyclingItemGroups.MODIFIER_GROUP);
        ItemGroupEvents.modifyEntriesEvent(RecyclingItemGroups.MODIFIER_GROUP_KEY)
                .register(new ItemGroupEvents.ModifyEntries() {

                    @Override
                    public void modifyEntries(FabricItemGroupEntries entries) {
                        entries.add(DIAMOND_MODIFIER);
                    }
                });
    }
}
