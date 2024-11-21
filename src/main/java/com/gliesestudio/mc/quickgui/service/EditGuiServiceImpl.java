package com.gliesestudio.mc.quickgui.service;

import com.gliesestudio.mc.quickgui.QuickGUI;
import com.gliesestudio.mc.quickgui.gui.*;
import com.gliesestudio.mc.quickgui.gui.item.GuiItem;
import com.gliesestudio.mc.quickgui.gui.item.GuiItemInfo;
import com.gliesestudio.mc.quickgui.manager.SystemGuiManager;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EditGuiServiceImpl implements EditGuiService {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(EditGuiServiceImpl.class);

    private final Logger logger;
    private final QuickGUI plugin;

    private final File guiFolder;

    public EditGuiServiceImpl(QuickGUI plugin) {
        this.logger = plugin.getLogger();
        this.plugin = plugin;

        this.guiFolder = new File(plugin.getDataFolder(), "guis");
    }

    @Override
    public void createGui(@NotNull CommandSender sender, String name, int rows) {
        // Create the GUI file
        File guiFile = new File(guiFolder, name + ".yml");
        // Verify if any GUI with the same name already exists.
        if (guiFile.exists()) {
            sender.sendMessage("§cA GUI with the name '§r" + name + "§c' already exists.");
            return;
        }

        try {
            // If the file doesn't exist, create it and add initial configuration
            if (guiFile.createNewFile()) {
                FileConfiguration guiConfig = YamlConfiguration.loadConfiguration(guiFile);
                guiConfig.set("name", name);
                guiConfig.set("title", name);
                guiConfig.set("rows", rows);
                guiConfig.set("items", new ArrayList<>());
                guiConfig.setComments("items", List.of("List of items in the GUI."));

                // Save the initial configuration
                guiConfig.save(guiFile);

                sender.sendMessage("§aGUI '§r" + name + "§a' created with §r" + rows + "§a rows.");
                plugin.getLogger().log(Level.INFO, "Created new GUI: " + name + " with " + rows + " rows.");

                // Reload GUIs to include the new GUI in the manager
                GuiManager.reloadGuis(plugin);
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Could not create GUI file for " + name, e);
            sender.sendMessage("§cAn error occurred while creating the GUI.");
        }
    }

    @Override
    public void reloadGui(String name) {
        GuiManager.reloadGui(plugin, name);
    }

    @Override
    public void reloadGuis() {
        GuiManager.reloadGuis(plugin);
    }

    @Override
    public boolean editGui(@NotNull Player player, String name) {
        GUI gui = GuiManager.getGui(name);
        if (gui == null) {
            player.sendMessage("§cNo GUI with the name '§r" + name + "§c' exists.");
            return true;
        }

        // Create the GUI from system resources
        GUI editGui = SystemGuiManager.getSystemGui("edit-gui");

        // Verify if any GUI exists with the name.
        if (editGui == null) {
            player.sendMessage("§cCouldn't load system edit gui for '§r" + name);
            return true;
        }

        // Open the GUI
        SystemGuiHolder guiHolder = new SystemGuiHolder(plugin, player, editGui, gui, OpenMode.EDIT_GUI);
        player.openInventory(guiHolder.getInventory());
        return true;
    }

    @Override
    public boolean editGuiName(String name, String newName) {
        File guiConfigFile = new File(guiFolder, name + ".yml");
        File renameToFile = new File(guiFolder, newName + ".yml");
        FileConfiguration guiConfig = YamlConfiguration.loadConfiguration(guiConfigFile);
        guiConfig.set("name", newName);

        try {
            if (guiConfigFile.renameTo(renameToFile)) {
                guiConfig.save(renameToFile);
                return true;
            }
        } catch (IOException ignored) {
        }
        return false;
    }

    @Override
    public boolean editGuiTitle(String name, String newTitle) {
        return setConfig(name, "title", newTitle);
    }

    @Override
    public boolean editGuiRows(String name, int newRows) {
        return setConfig(name, "rows", newRows);
    }

    @Override
    public boolean editGuiPermission(String name, String newPermission) {
        return setConfig(name, "permission", newPermission);
    }

    @Override
    public boolean editGuiAlias(String name, String newAlias) {
        return setConfig(name, "alias", newAlias);
    }

    private boolean setConfig(String name, String key, Object newValue) {
        File guiConfigFile = new File(guiFolder, name + ".yml");
        FileConfiguration guiConfig = YamlConfiguration.loadConfiguration(guiConfigFile);
        guiConfig.set(key, newValue);

        try {
            guiConfig.save(guiConfigFile);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public void openGuiEditItem(Player player, SystemGuiHolder systemGuiHolder) {
        player.openInventory(systemGuiHolder.getGuiInventory());
    }

    @Override
    public void editGuiItem(Player player, GuiHolder holder, ItemStack itemStack, int slot) {
        GUI gui = holder.getGui();
        if (itemStack != null && !itemStack.getType().isAir() && !itemStack.getType().isEmpty()) {
            GuiItem guiItem = new GuiItem();
            guiItem.setItem(GuiItemInfo.fromItemStack(itemStack));
            gui.updateItem(slot, guiItem);
        } else {
            gui.updateItem(slot, null);
        }

        try {
            YamlConfiguration guiConfig = gui.serialize();
            File guiFile = new File(guiFolder, holder.getGui().getName() + ".yml");
            guiConfig.save(guiFile);
            log.info("Updated item slot: {}", slot);
            reloadGui(holder.getGui().getName());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
