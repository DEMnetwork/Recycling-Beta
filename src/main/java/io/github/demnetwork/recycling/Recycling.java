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

package io.github.demnetwork.recycling;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.github.demnetwork.recycling.block.RecyclingBlockEntities;
import io.github.demnetwork.recycling.block.RecyclingBlocks;
import io.github.demnetwork.recycling.gui.PowderRecyclerScreenHandler;
import io.github.demnetwork.recycling.item.RecyclingItems;
import io.github.demnetwork.recycling.network.RecycleRequestPayload;
import io.github.demnetwork.recycling.recipe.PowderRecyclerRecipe;
import io.github.demnetwork.recycling.registry.PowderRecyclerModifierRegistry;

public class Recycling implements ModInitializer {
	public static final String MOD_ID = "recycling";
	public static final Identifier RECYCLE_PACKET_ID = Identifier.of(MOD_ID, "powder_recycler_recycle");

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Initilizing Recycling Mod beta-v1.0.0");
		LOGGER.info("OS Name: " + System.getProperty("os.name"));
		RecyclingItems.itemReg();
		RecyclingBlocks.regAll();
		LOGGER.info("Initializing and registering BlockEntities");
		try {
			RecyclingBlockEntities.regAll();
		} catch (Throwable e) {
			LOGGER.error("Recycling failed to intitalize", e);
			throw new Error("Mod failed to initialize", e);
		}
		PowderRecyclerRecipe.register();
		PowderRecyclerScreenHandler.regGUI();
		PayloadTypeRegistry.playC2S().register(RecycleRequestPayload.ID, RecycleRequestPayload.CODEC);

		ServerPlayNetworking.registerGlobalReceiver(
				RecycleRequestPayload.ID,
				new ServerPlayNetworking.PlayPayloadHandler<RecycleRequestPayload>() {
					@Override
					public void receive(final RecycleRequestPayload payload, final ServerPlayNetworking.Context context) {
						final PlayerEntity player = context.player();
						final MinecraftServer server = player.getServer();
						if (server != null) {
							server.execute(new Runnable() {
								@Override
								public void run() {
									String pString = payload.getString();
									if ("PowderRecycler".equals(pString)) {
										if (player.currentScreenHandler instanceof final PowderRecyclerScreenHandler handler) {
											handler.tryRecycle();
										} else
											LOGGER.warn(player.getName().getLiteralString()
													+ " attemped to recycle using \'recycling:powder_recycler\' recycling capabilities without its GUI open");
									}
								}
							});
						}
					}
				});
		PowderRecyclerModifierRegistry.register();
	}
}