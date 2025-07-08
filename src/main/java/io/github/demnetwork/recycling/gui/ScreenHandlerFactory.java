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

import java.lang.reflect.Constructor;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.*;

public class ScreenHandlerFactory<T extends ScreenHandler> implements ScreenHandlerType.Factory<T> {
    final Constructor<T> cns;

    ScreenHandlerFactory(Class<T> type) {
        try {
            this.cns = type.getConstructor(int.class, PlayerInventory.class);
        } catch (Exception e) {
            LOGGER.error("Exception occoured", e);
            throw new RuntimeException("Failed to create Factory");
        }
    }

    @Override
    public T create(int syncId, PlayerInventory playerInventory) {
        try {
            return cns.newInstance(syncId, playerInventory);
        } catch (Exception e) {
            return null;
        }
    }

}
