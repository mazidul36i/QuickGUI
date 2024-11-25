package com.gliesestudio.mc.quickgui.service;

import com.gliesestudio.mc.quickgui.QuickGUI;
import com.gliesestudio.mc.quickgui.enums.SystemCommand;
import com.gliesestudio.mc.quickgui.gui.GUI;
import com.gliesestudio.mc.quickgui.gui.OpenMode;
import com.gliesestudio.mc.quickgui.gui.SystemGuiHolder;
import com.gliesestudio.mc.quickgui.gui.item.GuiItemActionType;
import com.gliesestudio.mc.quickgui.manager.SystemGuiManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class EditActionServiceImpl implements EditActionService {

    private static final Logger log = LoggerFactory.getLogger(EditLoreServiceImpl.class);
    private final QuickGUI plugin;
    private final File guiFolder;

    public EditActionServiceImpl(QuickGUI plugin) {
        this.plugin = plugin;
        this.guiFolder = new File(plugin.getDataFolder(), "guis");
    }

    @Override
    public void openEditActionGui(@NotNull Player player, SystemGuiHolder systemGuiHolder, SystemCommand actionCommand) {
        log.info("Open edit action gui for item slot: {} and action command: {}", systemGuiHolder.getEditItemSlot(), actionCommand);
        GuiItemActionType itemActionType = switch (actionCommand) {
            case EDIT_ITEM_ACTION_LEFT -> GuiItemActionType.LEFT;
            case EDIT_ITEM_ACTION_SHIFT_LEFT -> GuiItemActionType.SHIFT_LEFT;
            case EDIT_ITEM_ACTION_MIDDLE -> GuiItemActionType.MIDDLE;
            case EDIT_ITEM_ACTION_RIGHT -> GuiItemActionType.RIGHT;
            case EDIT_ITEM_ACTION_SHIFT_RIGHT -> GuiItemActionType.SHIFT_RIGHT;
            default -> null;
        };
        // Verify if the action type is valid.
        if (itemActionType == null) {
            player.sendMessage("§cCouldn't find the action type for the command: " + actionCommand);
            return;
        }

        // Create the GUI from system resources
        GUI editActionGui = SystemGuiManager.getSystemGui("edit-actions");

        // Verify if any GUI exists with the name.
        if (editActionGui == null) {
            player.sendMessage("§cCouldn't load system edit action for '§r" + "edit-item");
            return;
        }

        // Open the GUI
        SystemGuiHolder editItemGuiHolder = new SystemGuiHolder(plugin, player, editActionGui, systemGuiHolder.getGui(),
                OpenMode.EDIT_ACTIONS, systemGuiHolder.getEditItemSlot(), itemActionType, systemGuiHolder);
        player.openInventory(editItemGuiHolder.getInventory());
    }

}
