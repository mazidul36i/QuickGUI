package com.gliesestudio.mc.quickgui.service;

import com.gliesestudio.mc.quickgui.QuickGUI;
import com.gliesestudio.mc.quickgui.enums.AwaitingInputType;
import com.gliesestudio.mc.quickgui.gui.GUI;
import com.gliesestudio.mc.quickgui.gui.GuiHolder;
import com.gliesestudio.mc.quickgui.gui.OpenMode;
import com.gliesestudio.mc.quickgui.gui.SystemGuiHolder;
import com.gliesestudio.mc.quickgui.gui.item.GuiItem;
import com.gliesestudio.mc.quickgui.gui.item.GuiItemInfo;
import com.gliesestudio.mc.quickgui.manager.SystemGuiManager;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class EditItemServiceImpl implements EditItemService {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(EditGuiServiceImpl.class);

    private final Logger logger;
    private final QuickGUI plugin;

    private final File guiFolder;

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

        // Open the GUI
        SystemGuiHolder editItemGuiHolder = new SystemGuiHolder(plugin, player, editItemGui, guiHolder.getGui(), OpenMode.EDIT_ITEMS, itemSlot);
        player.openInventory(editItemGuiHolder.getInventory());
    }

    @Override
    public void changeItem(@NotNull Player player, SystemGuiHolder systemGuiHolder) {
        Material cursorItemType = player.getItemOnCursor().getType();
        if (player.getItemOnCursor().isEmpty() || cursorItemType.isAir() || systemGuiHolder.getGui() == null) return;

        log.info("Change item for item slot: '{}' with material: {}", systemGuiHolder.getEditItemSlot(), cursorItemType.name());
        GUI gui = systemGuiHolder.getGui();
        GuiItem guiItem = gui.getItem(systemGuiHolder.getEditItemSlot());
        GuiItemInfo itemInfo = guiItem.getItem();
        String currentItemName = itemInfo.getName();
        itemInfo.setName(cursorItemType.name());

        // Save the GUI configuration
        try {
            File guiConfigFile = new File(guiFolder, systemGuiHolder.getGui().getName() + ".yml");
            FileConfiguration guiConfig = gui.serialize();
            guiConfig.save(guiConfigFile);
        } catch (IOException ignored) {
        }

        Material material = Material.getMaterial(currentItemName);
        int amount = 1;
        if (material == null) {
            material = Material.AIR;
            amount = 0;
        }
        player.setItemOnCursor(new ItemStack(material, amount));
    }

    @Override
    public boolean updateItemConfig(SystemGuiHolder systemGuiHolder, AwaitingInputType inputType, String newValue) {
        GUI gui = systemGuiHolder.getGui();
        if (gui == null) return false;
        GuiItem guiItem = gui.getItem(systemGuiHolder.getEditItemSlot());
        GuiItemInfo itemInfo = guiItem.getItem();

        if (AwaitingInputType.INPUT_ITEM_NAME.equals(inputType)) {
            itemInfo.setDisplayName(newValue);
        }

        try {
            File guiConfigFile = new File(guiFolder, systemGuiHolder.getGui().getName() + ".yml");
            FileConfiguration guiConfig = gui.serialize();
            guiConfig.save(guiConfigFile);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public void toggleItemGlow(Player player, SystemGuiHolder systemGuiHolder) {
        log.info("Toggle item glow for item slot: {}", systemGuiHolder.getEditItemSlot());
        GUI gui = systemGuiHolder.getGui();
        if (gui == null) return;
        GuiItem guiItem = gui.getItem(systemGuiHolder.getEditItemSlot());
        GuiItemInfo itemInfo = guiItem.getItem();
        itemInfo.setGlow(!itemInfo.isGlow());

        // Save the GUI configuration
        try {
            File guiConfigFile = new File(guiFolder, systemGuiHolder.getGui().getName() + ".yml");
            FileConfiguration guiConfig = gui.serialize();
            guiConfig.save(guiConfigFile);
        } catch (IOException ignored) {
        }
    }
}
