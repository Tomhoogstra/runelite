package net.runelite.client.plugins.grouptileindicators;

import net.runelite.client.input.KeyListener;

import javax.inject.Inject;
import java.awt.event.KeyEvent;

public class GroupTileIndicatorsInputListener implements KeyListener {

    @Inject
    private GroupTileIndicatorsPlugin plugin;

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
      if(e.getKeyCode() == KeyEvent.VK_SHIFT){
        plugin.setHotKeyPressed(true);
        plugin.updateMenus(true);
     }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_SHIFT){
            plugin.setHotKeyPressed(false);
            plugin.updateMenus(false);
        }
    }
}
