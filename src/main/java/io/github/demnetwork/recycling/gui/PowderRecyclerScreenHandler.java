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

package io.github.demnetwork.recycling.gui;

import static io.github.demnetwork.recycling.Recycling.LOGGER;

import java.util.Optional;
import io.github.demnetwork.recycling.recipe.PowderRecyclerModifer;
import io.github.demnetwork.recycling.recipe.PowderRecyclerRecipe;
import io.github.demnetwork.recycling.recipe.PowderRecyclerRecipeInput;
import io.github.demnetwork.recycling.registry.PowderRecyclerModifierRegistry;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.ServerRecipeManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class PowderRecyclerScreenHandler extends ScreenHandler {
    private final PlayerInventory playerInventory;
    final Inventory inventory;
    public static final ScreenHandlerType<PowderRecyclerScreenHandler> TYPE = Registry.register(
            Registries.SCREEN_HANDLER,
            Identifier.of("recycling", "powder_recycler"),
            new ScreenHandlerType<>(new ScreenHandlerFactory<>(PowderRecyclerScreenHandler.class), null));

    public static void regGUI() {
        LOGGER.info("We need to register the GUIs because Mojang wants;");
    }

    public PowderRecyclerScreenHandler(int syncId, PlayerInventory inventory) {
        this(syncId, inventory, new SimpleInventory(11));
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return player.isAlive(); // Only if player is alive
    }

    public PowderRecyclerScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(TYPE, syncId);
        this.inventory = inventory;
        this.playerInventory = playerInventory;
        checkSize(inventory, 11);
        inventory.onOpen(playerInventory.player);

        this.addSlot(new Slot(inventory, 0, 56, 17));
        this.addSlot(new Slot(inventory, 1, 56, 53) {
            @Override
            public boolean canInsert(ItemStack is) {
                return PowderRecyclerModifierRegistry.getModifer(is) != null;
            }

            @Override
            public boolean canTakeItems(PlayerEntity player) {
                return true;
            }
        });

        int outputStartX = 116;
        int outputStartY = 17;
        int slotSize = 18;
        int slotIndex = 2;
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 3; ++col) {
                this.addSlot(
                        new Slot(inventory, slotIndex++, outputStartX + col * slotSize, outputStartY + row * slotSize) {
                            @Override
                            public boolean canInsert(ItemStack stack) {
                                return false;
                            }

                            @Override
                            public boolean canTakeItems(PlayerEntity player) {
                                return true;
                            }
                        });
            }
        }

        // Player inventory slots (3 rows x 9 columns)
        int playerInvStartY = 84;
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * slotSize,
                        playerInvStartY + row * slotSize));
            }
        }

        // Player hotbar slots (1 row x 9 columns)
        int hotbarY = playerInvStartY + 58;
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * slotSize, hotbarY));
        }
    }

    public boolean canRecycle() {
        return !(inventory.getStack(0).isEmpty());
    }

    public void tryRecycle() {
        // Input must be in slot 0
        ItemStack inputStack = inventory.getStack(0);
        if (inputStack.isEmpty())
            return;

        // Search for a matching recipe
        final World world = this.playerInventory.player.getWorld(); // Access the world from player inventory
        if (world.isClient)
            return;

        ServerRecipeManager manager = (ServerRecipeManager) world.getServer().getRecipeManager();
        Optional<RecipeEntry<PowderRecyclerRecipe>> result = manager.getFirstMatch(
                PowderRecyclerRecipe.POWDER_RECYCLER_RECIPE_TYPE,
                new PowderRecyclerRecipeInput(inputStack),
                world);
        if (result.isPresent()) {
            PowderRecyclerRecipe recipe = result.get().value();
            ItemStack[] outputs = recipe.getOutput();
            ItemStack mod = inventory.getStack(1);
            if (!mod.isEmpty()) {
                PowderRecyclerModifer modifier = PowderRecyclerModifierRegistry.getModifer(mod);
                for (int i = 0; i < outputs.length; i++) {
                    outputs[i] = outputs[i].copy();
                }
                outputs = modifier.modifyOutput(mod, outputs).clone();
                if (mod.getDamage() >= mod.getMaxDamage()) {
                    LOGGER.info("Removing broken modifier");
                    inventory.setStack(1, ItemStack.EMPTY.copy());
                }
            }
            for (int i = 0; i < outputs.length; i++) {
                ItemStack output = outputs[i].copy();
                ItemStack slotStack = inventory.getStack(i + 2);
                if (slotStack.isEmpty()) {
                    int count = output.getCount();
                    if (count < 99) {
                        inventory.setStack(i + 2, output.copy());
                    } else {
                        int r = count % 80;
                        output.setCount(r);
                        inventory.setStack(i + 2, output.copy());
                        int sCount = (count - r) / 80;
                        output.setCount(80);
                        for (int i2 = 0; i2 < sCount; i2++) {
                            final PlayerEntity player = playerInventory.player;
                            world.spawnEntity(new ItemEntity(world, player.getX(), player.getY(), player.getZ(),
                                    outputs[i].copy()));
                        }
                    }
                } else {
                    if (slotStack.itemMatches(outputs[i].getRegistryEntry())) {
                        int count = slotStack.getCount();
                        int tcount = output.getCount() + count;
                        if (tcount < 99) {
                            slotStack.increment(output.getCount());
                        } else {
                            int r = tcount % 80;
                            output.setCount(r);
                            inventory.setStack(i + 2, output.copy());
                            int sCount = (tcount - r) / 80;
                            output.setCount(80);
                            for (int i2 = 0; i2 < sCount; i2++) {
                                final PlayerEntity player = playerInventory.player;
                                world.spawnEntity(new ItemEntity(world, player.getX(), player.getY(), player.getZ(),
                                        output.copy()));
                            }
                        }
                    } else {
                        int count = output.getCount();
                        if (count < 99) {
                            inventory.setStack(i + 2, output.copy());
                        } else {
                            int r = count % 80;
                            output.setCount(r);
                            inventory.setStack(i + 2, output.copy());
                            int sCount = (count - r) / 80;
                            output.setCount(80);
                            for (int i2 = 0; i2 < sCount; i2++) {
                                final PlayerEntity player = playerInventory.player;
                                world.spawnEntity(new ItemEntity(world, player.getX(), player.getY(), player.getZ(),
                                        output.copy()));
                            }
                        }
                    }
                }
            }
            inputStack.decrement(1);
            if (inputStack.isEmpty()) {
                inventory.setStack(0, ItemStack.EMPTY);
            } else {
                inventory.setStack(0, inputStack);
            }
            this.inventory.markDirty();
        } else {
            this.playerInventory.player.sendMessage(
                    Text.translatable("text.powder_recycler.error.unrecyclable"),
                    true);
        }
    }
}
