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

import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import com.mojang.serialization.MapCodec;
import io.github.demnetwork.recycling.Recycling;

public class PowderRecycler extends BlockWithEntity {

    public PowderRecycler(Settings settings) {
        super(settings);
    }

    @Override
    public boolean shouldDropItemsOnExplosion(Explosion explosion) {
        return true;
    }

    public static int getLuminance(BlockState currentBlockState) {
        return 5;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        try {
            return new PowderRecyclerBE(pos, state);
        } catch (Throwable e) {
            Recycling.LOGGER.error("Something went wrong", e);
            throw new RuntimeException(
                    "Recycling mod failed to instantiate the block_entity of \'recycling:powder_recycler\'!", e);
        }
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return super.createCodec(PowderRecycler::new);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.getBlockEntity(pos) instanceof PowderRecyclerBE be) {
            player.openHandledScreen(be);
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof PowderRecyclerBE recyclerBE) {
                for (int i = 0; i < recyclerBE.size(); i++) {
                    ItemStack stack = recyclerBE.getStack(i);
                    if (!stack.isEmpty()) {
                        ItemEntity entity = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                                stack);
                        world.spawnEntity(entity);
                    }
                }
            }
            world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
        }
        return super.onBreak(world, pos, state, player);
    }

}
