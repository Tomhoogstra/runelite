/*
 * Copyright (c) 2018, James Swindle <wilingua@gmail.com>
 * Copyright (c) 2018, Adam <Adam@sigterm.info>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.raidshelper;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Provides;
import net.runelite.api.*;
import net.runelite.api.events.GameObjectChanged;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.input.KeyManager;
import net.runelite.client.menus.MenuManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.util.List;
import java.util.regex.Pattern;

@PluginDescriptor(name = "Raid Helper")
public class RaidHelperPlugin extends Plugin
{
	private static final int MAX_ACTOR_VIEW_RANGE = 15;

	// Option added to NPC menu
	private static final String TAG = "Tag";

	private static final List<MenuAction> NPC_MENU_ACTIONS = ImmutableList.of(MenuAction.NPC_FIRST_OPTION, MenuAction.NPC_SECOND_OPTION,
		MenuAction.NPC_THIRD_OPTION, MenuAction.NPC_FOURTH_OPTION, MenuAction.NPC_FIFTH_OPTION);

	// Regex for splitting the hidden items in the config.
	private static final Splitter COMMA_SPLITTER = Splitter.on(Pattern.compile("\\s*,\\s*")).trimResults();

	@Inject
	private Client client;

	@Inject
	private MenuManager menuManager;

	@Inject
	private RaidHelperConfig config;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private RaidHelperSceneOverlay npcSceneOverlay;



	@Inject
	private KeyManager keyManager;

	@Inject
	private ClientThread clientThread;



	@Provides
	RaidHelperConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(RaidHelperConfig.class);
	}

	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(npcSceneOverlay);


	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(npcSceneOverlay);



	}

	@Subscribe
	public void onGameObjectSpawned(final GameObjectSpawned event)
	{

		GameObject gameObject = event.getGameObject();
		ObjectComposition comp = client.getObjectDefinition(gameObject.getId());
		if(comp.getName() != "null") {
			System.out.println(comp.getName());
			if (config.meme()) {
				if (comp.getName().toLowerCase().contains("small crystal") || gameObject.getId() == ObjectID.CRYSTAL_BOMB
						|| gameObject.getId() == ObjectID.SMALL_CRYSTALS) {
					npcSceneOverlay.objectsToDraw.add((gameObject));
				}
			} else {
				npcSceneOverlay.objectsToDraw.add((gameObject));
			}
		}
	}

	@Subscribe
	public void onGameObjectDespawned(final GameObjectDespawned event)
	{
		npcSceneOverlay.objectsToDraw.remove(event.getGameObject());
	}

	@Subscribe
	public void onGameObjectChanged(final GameObjectChanged event)
	{
		npcSceneOverlay.objectsToDraw.remove(event.getGameObject());
	}


}
