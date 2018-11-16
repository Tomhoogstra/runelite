/*
 * Copyright (c) 2018, Tomas Slusny <slusnucky@gmail.com>
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
package net.runelite.client.plugins.grouptileindicators;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Provides;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.Tile;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.input.KeyManager;
import net.runelite.client.menus.MenuManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.util.Arrays;

@PluginDescriptor(
		name = "GTI",
		description = "Highlight tiles for everyone in the party",
		tags = {"group", "tile"}
)
public class GroupTileIndicatorsPlugin extends Plugin
{
	@Inject
	private OverlayManager overlayManager;

	@Inject
	private GroupTileIndicatorsOverlay overlay;

	public static GroupTileIndicatorsSocket webSocket = new GroupTileIndicatorsSocket();

	@Inject
	private Client client;

	@Inject
	private KeyManager keyManager;

	@Inject
	private MenuManager menuManager;


	@Inject
	private GroupTileIndicatorsConfig config;

	@Provides
    GroupTileIndicatorsConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(GroupTileIndicatorsConfig.class);
	}


	@Getter(AccessLevel.PACKAGE)
	@Setter(AccessLevel.PACKAGE)
	private boolean hotKeyPressed;

	@Inject
	private GroupTileIndicatorsInputListener input;

	@Subscribe
	public void onTick(GameTick e){

		/*
	for(NPC npc : client.getNpcs()){
		if(npc.getName() != null) {
			if (npc.getName().contains("Banker")) {
				if (npc.getWorldLocation().distanceTo(client.getLocalPlayer().getWorldLocation()) < 3) {
					System.out.println(npc.getIndex());
				}
			}
		}
		}
		*/

	}


	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event)
	{


		if (!event.getMenuOption().equals("Mark Entity"))
		{
			return;
		}

			if (overlay.npcs.contains(event.getId())) {
				overlay.npcs.removeIf(x -> x == event.getId());
				webSocket.socket.emit("removeTag", event.getId());
			} else {
				System.out.println(event.getId());
				overlay.npcs.add(event.getId());
				webSocket.socket.emit("tagNPC", event.getId());
			}






	}

	@Subscribe
	public void onMenuOptionClicked2(MenuOptionClicked event)
	{
		if (!event.getMenuOption().equals("Group Mark"))
		{
			return;
		}


		Tile target = client.getSelectedSceneTile();
		if(overlay.tiles.contains(target.getWorldLocation())){
			overlay.tiles.remove(target.getWorldLocation());
			webSocket.socket.emit("removeTile", target.getWorldLocation().getX() + ":" + target.getWorldLocation().getY() + ":"
					+ target.getWorldLocation().getPlane());
		}else{
			overlay.tiles.add(target.getWorldLocation());
			webSocket.socket.emit("newTile", target.getWorldLocation().getX() + ":" + target.getWorldLocation().getY() + ":"
					+ target.getWorldLocation().getPlane());

		}


	}

	@Subscribe
	public void onMenuEntryAdded(MenuEntryAdded event)
	{


		if (hotKeyPressed && event.getOption().equals("Walk here"))
		{
			MenuEntry[] menuEntries = client.getMenuEntries();
			menuEntries = Arrays.copyOf(menuEntries, menuEntries.length + 1);

			MenuEntry menuEntry = menuEntries[menuEntries.length - 1] = new MenuEntry();

			menuEntry.setOption("Group Mark");
			menuEntry.setTarget(event.getTarget());
			menuEntry.setType(MenuAction.CANCEL.getId());

			client.setMenuEntries(menuEntries);
		}
	}


	public void updateMenus(boolean pressed){
		if(pressed){
			menuManager.addNpcMenuOption("Mark Entity");
		}else{
			menuManager.removeNpcMenuOption("Mark Entity");
		}
	}

	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(overlay);
		keyManager.registerKeyListener(input);

	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(overlay);
		keyManager.unregisterKeyListener(input);
	}
}
