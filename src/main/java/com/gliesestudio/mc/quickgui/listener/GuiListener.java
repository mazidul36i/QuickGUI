package com.gliesestudio.mc.quickgui.listener;

import com.gliesestudio.mc.quickgui.QuickGUI;
import com.gliesestudio.mc.quickgui.gui.GuiHolder;
import com.gliesestudio.mc.quickgui.gui.OpenMode;
import com.gliesestudio.mc.quickgui.gui.SystemGuiHolder;
import com.gliesestudio.mc.quickgui.gui.command.GuiCommandExecutor;
import com.gliesestudio.mc.quickgui.gui.item.GuiItem;
import com.gliesestudio.mc.quickgui.gui.item.GuiItemAction;
import com.gliesestudio.mc.quickgui.gui.item.GuiItemActionType;
import com.gliesestudio.mc.quickgui.utility.PluginUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class GuiListener implements Listener {

    private static final Logger log = LoggerFactory.getLogger(GuiListener.class);
    private final QuickGUI plugin;

    public GuiListener(QuickGUI plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(@NotNull InventoryClickEvent event) {
        Inventory clickedInventory = event.getClickedInventory();
        InventoryAction inventoryAction = event.getAction();
        if (clickedInventory == null ||
                !(clickedInventory.getHolder(false) instanceof GuiHolder holder) ||
                clickedInventory.getHolder(false) instanceof SystemGuiHolder ||
                !(event.getWhoClicked() instanceof Player player)
        ) {
            // It's not our inventory, ignore it.
            return;
        }

        int slot = event.getSlot();
        ItemStack cursorItem = event.getCursor().clone();
        ClickType clickType = event.getClick();

        if (OpenMode.EDIT.equals(holder.getMode())) {
            if (clickType.isRightClick()) event.setCancelled(true);
            if (InventoryAction.UNKNOWN.equals(inventoryAction)) return;
            handleEditGuiClick(player, holder, cursorItem, slot, clickType, inventoryAction);
            return;
        }

        event.setCancelled(true); // Prevent players from taking items
        GuiItem guiItem = holder.getGuiItem(slot);
        handleUserGuiClick(player, guiItem, clickType);
    }

    private void handleUserGuiClick(Player player, GuiItem guiItem, ClickType clickType) {
        // Get the action based on the click type
        GuiItemAction action = guiItem.getAction(clickType);
        executeAction(player, action);
    }

    /**
     * Execute the action commands for the player.
     *
     * @param player the player who triggered the action
     * @param action the action to execute
     */
    private static void executeAction(Player player, GuiItemAction action) {
        if (action == null) return;
        if (action.hasCommands()) {
            if (!action.hasPermission() || (action.hasPermission() && player.hasPermission(action.getPermission()))) {
                action.getCommands().forEach(command -> {
                    String parsedCommand = PluginUtils.replacePlaceholders(command, player);
                    if (GuiCommandExecutor.SERVER.equals(action.getExecutor())) {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), parsedCommand);
                    } else {
                        Bukkit.dispatchCommand(player, parsedCommand);
                    }
                });
            } else {
                player.sendMessage("§cYou do not have permission to perform this action");
            }
        }

        // Close the inventory if the action requires it.
        if (action.isCloseInv()) {
            player.closeInventory();
        }
    }

    // TODO: also add on drag - items added
    private void handleEditGuiClick(Player player, GuiHolder holder, ItemStack itemStack, int slot, ClickType clickType,
                                    InventoryAction inventoryAction) {
        log.info("Item stack: {}", itemStack);
        if (InventoryAction.NOTHING.equals(inventoryAction)) return;
        if (clickType.isLeftClick()) {
            onEditGuiItemChange(player, holder, itemStack, slot);
        } else if (clickType.isRightClick()) {
            // TODO: open item edit gui
            player.sendMessage("Will open edit item gui");
        }
    }

    private void onEditGuiItemChange(Player player, GuiHolder holder, ItemStack itemStack, int slot) {
        // Update config in async task
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            plugin.getEditGuiService().editGuiItem(player, holder, itemStack, slot);
        });
    }

}
