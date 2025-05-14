package com.gliesestudio.mc.bettergui.listener;

import com.gliesestudio.mc.bettergui.BetterGUI;
import com.gliesestudio.mc.bettergui.manager.GuiManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class GuiListener implements Listener {

    private static final Logger log = LoggerFactory.getLogger(GuiListener.class);
    private final GuiManager guiManager;

    public GuiListener(GuiManager guiManager, BetterGUI plugin) {
        this.guiManager = guiManager;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory clickedInventory = event.getInventory();
        Player player = (Player) event.getWhoClicked();

        // Check if the clicked inventory is one of our custom GUIs
        for (String guiName : guiManager.getGuis().keySet()) {
            if (clickedInventory.equals(guiManager.getGui(guiName))) {
                log.info("Clicked inv of GUI: {}", guiName);
                event.setCancelled(true); // Prevent players from taking items
                int slot = event.getSlot();
                guiManager.executeCommand(guiName, slot, player);
                return;
            }
        }
    }
}
