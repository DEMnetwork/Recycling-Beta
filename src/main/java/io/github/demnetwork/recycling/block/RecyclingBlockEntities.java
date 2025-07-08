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

import static io.github.demnetwork.recycling.Recycling.LOGGER;
import java.lang.reflect.Constructor;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public final class RecyclingBlockEntities {
        public static final BlockEntityType<PowderRecyclerBE> POWDER_RECYCLER_BLOCK_ENTITY = register("powder_recycler",
                        new BlockEntityFactory<>(PowderRecyclerBE.class), RecyclingBlocks.POWDER_RECYCLER);

        private static <T extends BlockEntity> BlockEntityType<T> register(String name,
                        FabricBlockEntityTypeBuilder.Factory<? extends T> entityFactory, Block... blocks) {
                Identifier id = Identifier.of("recycling", name);
                return Registry.register(Registries.BLOCK_ENTITY_TYPE, id,
                                FabricBlockEntityTypeBuilder.<T>create(entityFactory, blocks).build());
        }

        public static final void regAll() {
                LOGGER.info("Registering BlockEntities, because Mojang doesn't want Intrusive Holders, and want us to register stuff before using.");
        }

        static final class BlockEntityFactory<T extends BlockEntity>
                        implements FabricBlockEntityTypeBuilder.Factory<T> {
                private final Constructor<T> cns;

                BlockEntityFactory(Class<T> clazz) {
                        try {
                                this.cns = clazz.getDeclaredConstructor(BlockPos.class, BlockState.class);
                                cns.setAccessible(true);
                        } catch (Exception e) {
                                LOGGER.error("Unable to instantiate factory", e);
                                throw new ExceptionInInitializerError(e);
                        }
                }

                @Override
                public T create(BlockPos blockPos, BlockState blockState) {
                        try {
                                return cns.newInstance(blockPos, blockState);
                        } catch (Exception e) {
                                LOGGER.error("Unable to instantiate BlockEntity", e);
                                throw new RuntimeException(e);
                        }

                }

        }
}
