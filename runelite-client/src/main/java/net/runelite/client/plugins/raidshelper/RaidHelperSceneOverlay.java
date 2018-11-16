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

import com.google.common.collect.ImmutableList;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.kit.KitType;
import net.runelite.api.queries.InventoryWidgetItemQuery;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetID;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.plugins.reorderprayers.PrayerTabState;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.util.QueryRunner;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class RaidHelperSceneOverlay extends Overlay {


    public List<GameObject> objectsToDraw = new ArrayList<GameObject>();
    private final Map<String, BufferedImage> storedOutlines = new HashMap<>();
    private final Client client;
    private final QueryRunner queryRunner;
    private final RaidHelperConfig config;

    private final RaidHelperPlugin plugin;

    int[] meleeIds = {ItemID.DRAGON_DEFENDER, ItemID.VOID_MELEE_HELM, ItemID.FIRE_CAPE, ItemID.AMULET_OF_TORTURE, ItemID.PRIMORDIAL_BOOTS
            , ItemID.BARROWS_GLOVES, ItemID.AMULET_OF_FURY, ItemID.BERSERKER_RING_I};
    int[] rangeIds = {ItemID.NECKLACE_OF_ANGUISH, ItemID.VOID_RANGER_HELM, ItemID.PEGASIAN_BOOTS, ItemID.ARCHERS_RING_I, ItemID.AVAS_ASSEMBLER};
    int[] rangeIdsCrossbow = {ItemID.NECKLACE_OF_ANGUISH, ItemID.VOID_RANGER_HELM, ItemID.PEGASIAN_BOOTS, ItemID.ARCHERS_RING_I, ItemID.AVAS_ASSEMBLER, ItemID.ODIUM_WARD};
    int[] mageIds = {ItemID.OCCULT_NECKLACE, ItemID.VOID_MAGE_HELM, ItemID.IMBUED_ZAMORAK_CAPE, ItemID.IMBUED_SARADOMIN_CAPE, ItemID.IMBUED_GUTHIX_CAPE,
        ItemID.AHRIMS_ROBESKIRT, ItemID.AHRIMS_ROBETOP, ItemID.ETERNAL_BOOTS, ItemID.TORMENTED_BRACELET,
    ItemID.AHRIMS_ROBESKIRT_100, ItemID.AHRIMS_ROBETOP_100, ItemID.BOOK_OF_DARKNESS, ItemID.SEERS_RING_I};
    @Inject
    RaidHelperSceneOverlay(QueryRunner query, Client client, RaidHelperConfig config, RaidHelperPlugin plugin) {
        this.queryRunner = query;
        this.client = client;
        this.config = config;
        this.plugin = plugin;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics) {


        if (config.showSwaps()) {
            //System.out.println(client.getLocalPlayer().getPlayerComposition().getEquipmentId(KitType.HANDS));
            int weapon = client.getLocalPlayer().getPlayerComposition().getEquipmentId(KitType.WEAPON);
            if (weapon == ItemID.TOXIC_BLOWPIPE) {

                for (WidgetItem w : getInventoryWidgets()) {
                    if (Arrays.stream(rangeIds).anyMatch(i -> i == w.getId())) {
                        graphics.setColor(Color.RED);
                        graphics.setStroke(new BasicStroke(4));
                        graphics.drawRect(w.getCanvasLocation().getX() + 1, w.getCanvasLocation().getY() + 1, 30, 30);

                    }
                }
            }else if(weapon == ItemID.DRAGON_HUNTER_CROSSBOW){
                for (WidgetItem w : getInventoryWidgets()) {
                    if (Arrays.stream(rangeIdsCrossbow).anyMatch(i -> i == w.getId())) {
                        graphics.setColor(Color.RED);
                        graphics.setStroke(new BasicStroke(4));
                        graphics.drawRect(w.getCanvasLocation().getX() + 1, w.getCanvasLocation().getY() + 1, 30, 30);

                    }
                }
            }else if(weapon == ItemID.TRIDENT_OF_THE_SWAMP ){
                for (WidgetItem w : getInventoryWidgets()) {
                    if (Arrays.stream(mageIds).anyMatch(i -> i == w.getId())) {
                        graphics.setColor(Color.RED);
                        graphics.setStroke(new BasicStroke(4));
                        graphics.drawRect(w.getCanvasLocation().getX() + 1, w.getCanvasLocation().getY() + 1, 30, 30);

                    }
                }
            } else {
                for (WidgetItem w : getInventoryWidgets()) {

                    if (Arrays.stream(meleeIds).anyMatch(i -> i == w.getId())) {
                        graphics.setColor(Color.RED);
                        graphics.setStroke(new BasicStroke(4));
                        graphics.drawRect(w.getCanvasLocation().getX() + 1, w.getCanvasLocation().getY() + 1, 30, 30);

                    }
                }
            }
        }

        if (config.showPrayer()) {

            List<Widget> prayerWidgets = PRAYER_WIDGET_INFO_LIST.stream()
                    .map(client::getWidget)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            if(prayerWidgets.size() > 0) {
                if (!prayerWidgets.get(0).isHidden()) {


                    graphics.setColor(Color.MAGENTA);
                    graphics.setStroke(new BasicStroke(4));
                    graphics.drawRect(prayerWidgets.get(0).getCanvasLocation().getX() + 1, prayerWidgets.get(0).getCanvasLocation().getY() + 1, 30, 30);

                    graphics.setColor(Color.GREEN);
                    graphics.drawRect(prayerWidgets.get(1).getCanvasLocation().getX() + 1, prayerWidgets.get(1).getCanvasLocation().getY() + 1, 30, 30);

                    graphics.setColor(Color.RED);
                    graphics.drawRect(prayerWidgets.get(2).getCanvasLocation().getX() + 1, prayerWidgets.get(2).getCanvasLocation().getY() + 1, 30, 30);

                }
            }
        }

        for (GameObject g : objectsToDraw) {
            LocalPoint lp = g.getLocalLocation();
            Polygon tilePoly = Perspective.getCanvasTileAreaPoly(client, lp, 1);

            if (tilePoly != null) {
                graphics.setColor(Color.RED);
                graphics.setStroke(new BasicStroke(2));
                graphics.draw(tilePoly);

            }
        }

        return null;
    }

    void clearStoredOutlines() {
        storedOutlines.clear();
    }

    private BufferedImage getOutline(final int id, final Color color) {
        final String key = getStringGeneratedId(id, color);
        BufferedImage stored = storedOutlines.get(key);
        if (stored != null) {
            return stored;
        }

        final SpritePixels itemSprite = client.createItemSprite(id, 1, 1, 0, 0, true, 710);
        final BufferedImage generatedPicture = itemSprite.toBufferedOutline(color);
        storedOutlines.put(key, generatedPicture);
        return generatedPicture;
    }

    private PrayerTabState getPrayerTabState() {
        HashTable<WidgetNode> componentTable = client.getComponentTable();
        for (WidgetNode widgetNode : componentTable.getNodes()) {
            if (widgetNode.getId() == WidgetID.PRAYER_GROUP_ID) {
                return PrayerTabState.PRAYERS;
            } else if (widgetNode.getId() == WidgetID.QUICK_PRAYERS_GROUP_ID) {
                return PrayerTabState.QUICK_PRAYERS;
            }
        }
        return PrayerTabState.NONE;
    }

    private String getStringGeneratedId(final int id, final Color color) {
        return id + "." + color.getRGB();
    }

    private Collection<WidgetItem> getInventoryWidgets() {
        Query inventoryQuery = new InventoryWidgetItemQuery();
        WidgetItem[] inventoryWidgetItems = queryRunner.runQuery(inventoryQuery);


        Collection<WidgetItem> jewellery = new ArrayList<>();
        jewellery.addAll(Arrays.asList(inventoryWidgetItems));

        return jewellery;
    }

    private static final List<WidgetInfo> PRAYER_WIDGET_INFO_LIST = ImmutableList.of(

            WidgetInfo.PRAYER_PROTECT_FROM_MAGIC,
            WidgetInfo.PRAYER_PROTECT_FROM_MISSILES,
            WidgetInfo.PRAYER_PROTECT_FROM_MELEE

    );

}

