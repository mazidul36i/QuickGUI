package com.gliesestudio.mc.quickgui.listener;

import com.gliesestudio.mc.quickgui.QuickGUI;
import com.gliesestudio.mc.quickgui.inventory.QuickGuiHolder;
import com.gliesestudio.mc.quickgui.manager.GuiManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class GuiListener implements Listener {

    private static final Logger log = LoggerFactory.getLogger(GuiListener.class);
    private final GuiManager guiManager;

    public GuiListener(GuiManager guiManager, QuickGUI plugin) {
        this.guiManager = guiManager;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory == null ||
                !(clickedInventory.getHolder(false) instanceof QuickGuiHolder) ||
                !(event.getWhoClicked() instanceof Player player)
        ) {
            // It's not our inventory, ignore it.
            return;
        }

        int slot = event.getSlot();
        ItemStack currentItem = event.getCurrentItem();
        ClickType clickType = event.getClick();

        log.info("Click type: {}, slot: {}", clickType, slot);
        log.info("Current item: {}", currentItem);

        // Check if the clicked inventory is one of our custom GUIs
        for (String guiName : guiManager.getGuiNames()) {
            if (clickedInventory.equals(guiManager.getGui(guiName))) {
                event.setCancelled(true); // Prevent players from taking items
                guiManager.executeCommand(player, guiName, slot);
                return;
            }
        }
    }
}
