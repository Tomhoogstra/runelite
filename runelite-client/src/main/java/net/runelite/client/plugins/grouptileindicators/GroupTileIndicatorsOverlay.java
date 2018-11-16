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

import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.NPCComposition;
import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.*;

import javax.inject.Inject;
import java.awt.*;
import java.util.ArrayList;

public class GroupTileIndicatorsOverlay extends Overlay
{
	private final Client client;
	private final GroupTileIndicatorsConfig config;

	public static java.util.List<WorldPoint> tiles = new ArrayList<WorldPoint>();
	public static java.util.List<Integer> npcs = new ArrayList<>();

	@Inject
	private GroupTileIndicatorsOverlay(Client client, GroupTileIndicatorsConfig config)
	{
		System.out.println("Created Group Tile Indicator Overlay");
		this.client = client;
		this.config = config;
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
		setPriority(OverlayPriority.LOW);


	}

	@Override
	public Dimension render(Graphics2D graphics)
	{

		for(WorldPoint wp : tiles){
			if(wp.getPlane() == client.getPlane()){
				drawTile(graphics, wp);
			}
		}


		for(int i : npcs){
			NPC npc = client.getCachedNPCs()[i];
			if(npc != null) {

				int size = 1;
				NPCComposition composition = npc.getTransformedComposition();
				if (composition != null) {
					size = composition.getSize();
				}
				LocalPoint lp = npc.getLocalLocation();
				Polygon tilePoly = Perspective.getCanvasTileAreaPoly(client, lp, size);

				if (tilePoly != null) {
					graphics.setColor(Color.CYAN);
					graphics.setStroke(new BasicStroke(2));
					graphics.draw(tilePoly);

				}
			}
		}

		return null;
	}

	private void drawTile(Graphics2D graphics, WorldPoint point)
	{
		WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();

		if (point.distanceTo(playerLocation) >= 32)
		{
			return;
		}

		LocalPoint lp = LocalPoint.fromWorld(client, point);
		if (lp == null)
		{
			return;
		}

		Polygon poly = Perspective.getCanvasTilePoly(client, lp);
		if (poly == null)
		{
			return;
		}

		OverlayUtil.renderPolygon(graphics, poly, config.getTileColour());
	}
}
