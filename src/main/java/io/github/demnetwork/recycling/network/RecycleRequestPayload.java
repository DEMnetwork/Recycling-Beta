/*
 * Copyright (c) 2025 DEMnetwork
 * All rights reserved.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.github.demnetwork.recycling.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.*;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public class RecycleRequestPayload implements CustomPayload {
  public static final Id<RecycleRequestPayload> ID = new CustomPayload.Id<>(
      Identifier.of("recycling", "recycle"));

  private final String str;

  public RecycleRequestPayload(String s) {
    this.str = s;
  }

  @Override
  public Id<? extends CustomPayload> getId() {
    return ID;
  }

  public void write(RegistryByteBuf buf) {
    buf.writeString(str);
  }

  public static final PacketCodec<RegistryByteBuf, RecycleRequestPayload> CODEC = PacketCodec.of(
      new ValueFirstEncoder<RegistryByteBuf, RecycleRequestPayload>() {
        @Override
        public void encode(RecycleRequestPayload value, RegistryByteBuf buf) {
          buf.writeString(value.str);
        }
      },
      new PacketDecoder<RegistryByteBuf, RecycleRequestPayload>() {
        @Override
        public RecycleRequestPayload decode(RegistryByteBuf buf) {
          return new RecycleRequestPayload(buf.readString());
        }
      });

  public String getString() {
    return this.str;
  }
}
