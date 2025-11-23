package com.gliesestudio.mc.quickgui.listener;

import com.gliesestudio.mc.quickgui.QuickGUI;
import com.gliesestudio.mc.quickgui.enums.AwaitingInputType;
import com.gliesestudio.mc.quickgui.enums.SystemCommand;
import com.gliesestudio.mc.quickgui.gui.GUI;
import com.gliesestudio.mc.quickgui.gui.GuiHolder;
import com.gliesestudio.mc.quickgui.gui.OpenMode;
import com.gliesestudio.mc.quickgui.gui.SystemGuiHolder;
import com.gliesestudio.mc.quickgui.gui.item.GuiItem;
import com.gliesestudio.mc.quickgui.gui.item.GuiItemAction;
import com.gliesestudio.mc.quickgui.gui.item.GuiItemActionType;
import com.gliesestudio.mc.quickgui.service.EditActionService;
import com.gliesestudio.mc.quickgui.service.EditGuiService;
import com.gliesestudio.mc.quickgui.service.EditItemService;
import com.gliesestudio.mc.quickgui.service.EditLoreService;
import com.gliesestudio.mc.quickgui.utility.CollectionUtils;
import com.gliesestudio.mc.quickgui.utility.Constants;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.gliesestudio.mc.quickgui.enums.SystemCommand.*;

public class SystemGuiListener implements Listener {

    private static final Logger log = LoggerFactory.getLogger(SystemGuiListener.class);
    private final QuickGUI plugin;

    private final ChatListener chatListener;

    private final EditGuiService editGuiService;
    private final EditItemService editItemService;
    private final EditLoreService editLoreService;
    private final EditActionService editActionService;

    public SystemGuiListener(QuickGUI plugin, ChatListener chatListener) {
        this.plugin            = plugin;
        this.chatListener      = chatListener;
        this.editGuiService    = plugin.getEditGuiService();
        this.editItemService   = plugin.getEditItemService();
        this.editLoreService   = plugin.getEditLoreService();
        this.editActionService = plugin.getEditActionService();
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

        // Special case: Handle the action to change gui item.
        if (holder.getMode() == OpenMode.EDIT_ITEMS && slot == Constants.EDIT_GUI_ITEM_SLOT && clickType == ClickType.LEFT) {
            editItemService.changeItem(player, holder);
            player.openInventory(holder.createInventory());
            return;
        }

        GuiItem guiItem = holder.getSystemGuiItem(slot);
        if (guiItem == null) return;
        handleGuiClick(holder, player, guiItem, clickType, slot);
    }

    private void handleGuiClick(SystemGuiHolder systemGuiHolder, Player player, GuiItem guiItem, ClickType clickType, int slot) {
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
        log.info("Command: '{}' and system command: '{}'", command, systemCommand);

        // On click input commands
        if (systemCommand != null && systemCommand.getInputType() != null) {
            log.info("Awaiting {} input for player: {}", systemCommand.getInputType(), player.getUniqueId());
            if (AwaitingInputType.INPUT_EDIT_ITEM_LORE.equals(systemCommand.getInputType())) {
                chatListener.addAwaitingInput(player.getUniqueId(), systemCommand.getInputType(), systemGuiHolder, slot - 2);
            } else if (List.of(EDIT_ITEM_ACTION_LEFT_COMMAND, EDIT_ITEM_ACTION_SHIFT_LEFT_COMMAND, EDIT_ITEM_ACTION_MIDDLE_COMMAND,
                               EDIT_ITEM_ACTION_RIGHT_COMMAND, EDIT_ITEM_ACTION_SHIFT_RIGHT_COMMAND).contains(systemCommand)) {
                chatListener.addAwaitingInput(player.getUniqueId(), systemCommand.getInputType(), systemGuiHolder, slot - 29);
            } else chatListener.addAwaitingInput(player.getUniqueId(), systemCommand.getInputType(), systemGuiHolder);
            player.closeInventory();
            return;
        }

        switch (systemCommand) {
            case EDIT_ITEMS -> editGuiService.openGuiEditItem(player, systemGuiHolder);

            case CHANGE_ITEM_LORES -> editLoreService.openEditLoreGui(player, systemGuiHolder);

            case EDIT_ITEM_ACTION_LEFT,
                 EDIT_ITEM_ACTION_SHIFT_LEFT,
                 EDIT_ITEM_ACTION_MIDDLE,
                 EDIT_ITEM_ACTION_RIGHT,
                 EDIT_ITEM_ACTION_SHIFT_RIGHT ->
                    editActionService.openEditActionGui(player, systemGuiHolder, systemCommand);

            case TOGGLE_ITEM_GLOW -> {
                editItemService.toggleItemGlow(player, systemGuiHolder);
                player.openInventory(systemGuiHolder.createInventory());
            }

            case DELETE_ITEM_LORE -> {
                editLoreService.deleteItemLoreConfig(player, systemGuiHolder, slot - 2);
                systemGuiHolder.getPrevSystemGui().createInventory();
                editLoreService.openEditLoreGui(player, systemGuiHolder.getPrevSystemGui());
            }

            case DELETE_ITEM_ACTION_LEFT_COMMAND,
                 DELETE_ITEM_ACTION_SHIFT_LEFT_COMMAND,
                 DELETE_ITEM_ACTION_MIDDLE_COMMAND,
                 DELETE_ITEM_ACTION_RIGHT_COMMAND,
                 DELETE_ITEM_ACTION_SHIFT_RIGHT_COMMAND ->
                    editActionService.deleteItemActionCommand(player, systemGuiHolder, systemCommand, slot - 29);

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
