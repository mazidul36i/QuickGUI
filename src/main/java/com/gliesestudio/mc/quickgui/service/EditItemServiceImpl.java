package com.gliesestudio.mc.quickgui.service;

import com.gliesestudio.mc.quickgui.QuickGUI;
import com.gliesestudio.mc.quickgui.gui.GUI;
import com.gliesestudio.mc.quickgui.gui.GuiHolder;
import com.gliesestudio.mc.quickgui.gui.OpenMode;
import com.gliesestudio.mc.quickgui.gui.SystemGuiHolder;
import com.gliesestudio.mc.quickgui.manager.SystemGuiManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.logging.Logger;

public class EditItemServiceImpl implements EditItemService {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(EditGuiServiceImpl.class);

    private final Logger logger;
    private final QuickGUI plugin;

    private final File guiFolder;

    private final static int EDIT_ITEM_SLOT = 4;

    public EditItemServiceImpl(@NotNull QuickGUI plugin) {
        this.logger = plugin.getLogger();
        this.plugin = plugin;

        this.guiFolder = new File(plugin.getDataFolder(), "guis");
    }

    @Override
    public void openEditItemGui(@NotNull Player player, GuiHolder guiHolder, int itemSlot) {
        log.info("Open edit item gui for item slot: {}", itemSlot);

        // Create the GUI from system resources
        GUI editItemGui = SystemGuiManager.getSystemGui("edit-item");

        // Verify if any GUI exists with the name.
        if (editItemGui == null) {
            player.sendMessage("§cCouldn't load system edit gui for '§r" + "edit-item");
            return;
        }

        // Add clicked item into the edit slot (4)
        editItemGui.updateItem(EDIT_ITEM_SLOT, guiHolder.getGuiItem(itemSlot));

        // Open the GUI
        SystemGuiHolder editItemGuiHolder = new SystemGuiHolder(plugin, player, editItemGui, guiHolder.getGui(), OpenMode.EDIT_ITEMS);
        player.openInventory(editItemGuiHolder.getInventory());
    }

}
