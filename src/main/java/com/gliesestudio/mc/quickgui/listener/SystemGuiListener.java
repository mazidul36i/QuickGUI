package com.gliesestudio.mc.quickgui.listener;

import com.gliesestudio.mc.quickgui.QuickGUI;
import com.gliesestudio.mc.quickgui.commands.PluginCommands;
import com.gliesestudio.mc.quickgui.enums.CommandExecutor;
import com.gliesestudio.mc.quickgui.enums.ItemStackType;
import com.gliesestudio.mc.quickgui.inventory.SystemGuiHolder;
import com.gliesestudio.mc.quickgui.manager.SystemGuiManager;
import com.gliesestudio.mc.quickgui.utility.PluginUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SystemGuiListener implements Listener {

    private static final Logger log = LoggerFactory.getLogger(SystemGuiListener.class);
    private final QuickGUI plugin;

    private final SystemGuiManager systemGuiManager;
    private final ChatListener chatListener;

    public SystemGuiListener(QuickGUI plugin, SystemGuiManager systemGuiManager, ChatListener chatListener) {
        this.plugin = plugin;
        this.systemGuiManager = systemGuiManager;
        this.chatListener = chatListener;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory == null ||
                !(clickedInventory.getHolder(false) instanceof SystemGuiHolder holder) ||
                !(event.getWhoClicked() instanceof Player player)
        ) {
            // It's not our inventory, ignore it.
            return;
        }

        int slot = event.getSlot();
        ItemStack currentItem = event.getCurrentItem();
        ClickType clickType = event.getClick();
        ItemStackType itemStackType = null;
        String command = null;
        CommandExecutor commandExecutor = null;

        log.info("Click type: {}, slot: {}", clickType, slot);
        log.info("Current item: {}", currentItem);

        if (currentItem != null && currentItem.hasItemMeta() && currentItem.getItemMeta().hasCustomModelData()) {
            int itemStackId = currentItem.getItemMeta().getCustomModelData();
            itemStackType = ItemStackType.fromId(itemStackId);
            if (itemStackType != null) {
                if (ItemStackType.SYSTEM_BUTTON.equals(itemStackType) || ItemStackType.SYSTEM_FILLER.equals(itemStackType)) {
                    // Cancel event to prevent players from taking items.
                    event.setCancelled(true);
                }
            }

            // Fetch the command from the item meta.
            PersistentDataContainer dataContainer = currentItem.getItemMeta().getPersistentDataContainer();
            command = dataContainer.get(systemGuiManager.COMMAND_KEY(), PersistentDataType.STRING);
            commandExecutor = CommandExecutor.fromString(dataContainer.get(systemGuiManager.COMMAND_EXECUTOR_KEY(), PersistentDataType.STRING));
        }

        if (PluginCommands.Action.EDIT.equals(holder.getAction()))
            handleAdminGuiClick(holder, player, clickedInventory, command, itemStackType);
        else {
            event.setCancelled(true); // Prevent players from taking items
            handleGuiClick(player, commandExecutor, command);
        }
    }

    private void handleGuiClick(Player player, CommandExecutor commandExecutor, String command) {
        if (command == null) return;
        log.info("Executing command by {}: {}", commandExecutor, command);

        command = PluginUtils.replacePlaceholders(command, player);
        if (CommandExecutor.SERVER.equals(commandExecutor)) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        } else {
            Bukkit.dispatchCommand(player, command);
        }
    }

    private void handleAdminGuiClick(SystemGuiHolder systemGuiHolder, Player player, Inventory clickedInventory,
                                     String command, ItemStackType itemStackType) {
        log.info("Item stack type: {}, command: {}", itemStackType, command);
        if (ItemStackType.SYSTEM_BUTTON.equals(itemStackType)) {
            PluginCommands.SystemCommand systemCommand = PluginCommands.SystemCommand.fromString(command);

            // On click input commands
            if (systemCommand != null && systemCommand.getInputType() != null) {
                log.info("Awaiting {} input for player: {}", systemCommand.getInputType(), player.getUniqueId());
                chatListener.addAwaitingInput(player.getUniqueId(), systemCommand.getInputType(), systemGuiHolder);
                player.closeInventory();
                return;
            }

        }
    }

}
