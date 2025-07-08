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

package io.github.demnetwork.recycling.block;

import io.github.demnetwork.recycling.gui.PowderRecyclerScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public final class PowderRecyclerBE extends BlockEntity implements NamedScreenHandlerFactory, Inventory {
    public PowderRecyclerBE(BlockPos pos, BlockState state) {
        super(RecyclingBlockEntities.POWDER_RECYCLER_BLOCK_ENTITY, pos, state);
    }

    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(11, ItemStack.EMPTY);

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new PowderRecyclerScreenHandler(syncId, playerInventory, this);
    }

    @Override
    public void clear() {
        items.clear();
    }

    @Override
    public int size() {
        return 11;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public ItemStack getStack(int slot) {
        return items.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack result = ItemStack.EMPTY;
        ItemStack stack = getStack(slot);
        if (!stack.isEmpty()) {
            result = stack.split(amount);
            if (stack.isEmpty())
                setStack(slot, ItemStack.EMPTY);
            markDirty();
        }
        return result;
    }

    @Override
    public ItemStack removeStack(int slot) {
        ItemStack result = items.get(slot);
        items.set(slot, ItemStack.EMPTY);
        markDirty();
        return result;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        items.set(slot, stack);
        markDirty();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return this.world != null && this.pos != null &&
                player.squaredDistanceTo(Vec3d.ofCenter(this.pos)) <= 256;
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("text.powder_recycler.gui.name");
    }

    @Override
    public void writeNbt(NbtCompound nbt, WrapperLookup lookup) {
        super.writeNbt(nbt, lookup);
        Inventories.writeNbt(nbt, items, lookup);
        markDirty();
    }

    @Override
    public void readNbt(NbtCompound nbt, WrapperLookup lookup) {
        super.readNbt(nbt, lookup);
        Inventories.readNbt(nbt, items, lookup);
    }

}
