package com.gliesestudio.mc.quickgui.listener;

import com.gliesestudio.mc.quickgui.QuickGUI;
import com.gliesestudio.mc.quickgui.enums.SystemCommand;
import com.gliesestudio.mc.quickgui.gui.GUI;
import com.gliesestudio.mc.quickgui.gui.GuiHolder;
import com.gliesestudio.mc.quickgui.gui.OpenMode;
import com.gliesestudio.mc.quickgui.gui.SystemGuiHolder;
import com.gliesestudio.mc.quickgui.gui.item.GuiItem;
import com.gliesestudio.mc.quickgui.gui.item.GuiItemAction;
import com.gliesestudio.mc.quickgui.gui.item.GuiItemActionType;
import com.gliesestudio.mc.quickgui.service.EditLoreService;
import com.gliesestudio.mc.quickgui.utility.CollectionUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SystemGuiListener implements Listener {

    private static final Logger log = LoggerFactory.getLogger(SystemGuiListener.class);
    private final QuickGUI plugin;

    private final ChatListener chatListener;

    private final EditLoreService editLoreService;

    public SystemGuiListener(QuickGUI plugin, ChatListener chatListener) {
        this.plugin = plugin;
        this.chatListener = chatListener;
        this.editLoreService = plugin.getEditLoreService();
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
        ClickType clickType = event.getClick();
        event.setCancelled(true); // Prevent players from taking items

        GuiItem guiItem = holder.getSystemGuiItem(slot);
        if (guiItem == null) return;
        handleGuiClick(holder, player, guiItem, clickType);
    }

    private void handleGuiClick(SystemGuiHolder systemGuiHolder, Player player, GuiItem guiItem, ClickType clickType) {
        if (!guiItem.hasActions()) {
            log.info("There are no actions for this item");
            return;
        }

        // Get the action based on the click type
        GuiItemAction action = switch (clickType) {
            case LEFT -> guiItem.getActions().get(GuiItemActionType.LEFT);
            case SHIFT_LEFT -> guiItem.getActions().get(GuiItemActionType.SHIFT_LEFT);
            case MIDDLE -> guiItem.getActions().get(GuiItemActionType.MIDDLE);
            case RIGHT -> guiItem.getActions().get(GuiItemActionType.RIGHT);
            case SHIFT_RIGHT -> guiItem.getActions().get(GuiItemActionType.SHIFT_RIGHT);
            case null, default -> null;
        };

        if (action == null || CollectionUtils.isEmpty(action.getCommands())) {
            log.info("There are no commands for this item: {}, action: {}", guiItem.getItem().getDisplayName(), clickType);
            return;
        }

        String command = action.getCommands().getFirst();
        SystemCommand systemCommand = SystemCommand.fromString(command);
        log.info("Command: {} and system command: {}", command, systemCommand);

        // On click input commands
        if (systemCommand != null && systemCommand.getInputType() != null) {
            log.info("Awaiting {} input for player: {}", systemCommand.getInputType(), player.getUniqueId());
            chatListener.addAwaitingInput(player.getUniqueId(), systemCommand.getInputType(), systemGuiHolder);
            player.closeInventory();
            return;
        } else if (systemCommand != null) {
            if (SystemCommand.CHANGE_ITEM_LORES.equals(systemCommand)) {
                editLoreService.openEditLoreGui(player, systemGuiHolder);
                return;
            }
        }

        switch (systemCommand) {
            case EDIT_ITEMS -> {
                plugin.getEditGuiService().openGuiEditItem(player, systemGuiHolder);
            }

            case BACK -> {
                player.closeInventory();
                if (systemGuiHolder.hasPrevSystemGui()) {
                    player.openInventory(systemGuiHolder.getPrevSystemGui().getInventory());
                } else if (systemGuiHolder.getGui() != null) {
                    GUI gui = systemGuiHolder.getGui();
                    GuiHolder guiHolder = new GuiHolder(plugin, player, gui, OpenMode.EDIT_GUI);
                    player.openInventory(guiHolder.getInventory());
                }
            }

            case null, default -> player.sendMessage("§cUnknown system command.");
        }

    }

}
