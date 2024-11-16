package com.gliesestudio.mc.quickgui.service;

import com.gliesestudio.mc.quickgui.QuickGUI;
import com.gliesestudio.mc.quickgui.commands.PluginCommands;
import com.gliesestudio.mc.quickgui.inventory.QuickGuiHolder;
import com.gliesestudio.mc.quickgui.inventory.SystemGuiHolder;
import com.gliesestudio.mc.quickgui.manager.GuiManager;
import com.gliesestudio.mc.quickgui.manager.SystemGuiManager;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EditGuiServiceImpl implements EditGuiService {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(GuiManager.class);

    private final Logger logger;
    private final QuickGUI plugin;

    private final GuiManager guiManager;
    private final SystemGuiManager systemGuiManager;

    private final File guiFolder;

    public EditGuiServiceImpl(QuickGUI plugin, GuiManager guiManager, SystemGuiManager systemGuiManager) {
        this.logger = plugin.getLogger();
        this.plugin = plugin;
        this.guiManager = guiManager;
        this.systemGuiManager = systemGuiManager;

        this.guiFolder = new File(plugin.getDataFolder(), "guis");
    }

    @Override
    public boolean createGui(@NotNull CommandSender sender, String name, int rows) {
        // Create the GUI file
        File guiFile = new File(plugin.getDataFolder() + "/guis", name + ".yml");
        // Verify if any GUI with the same name already exists.
        if (guiFile.exists()) {
            sender.sendMessage("§cA GUI with the name '§r" + name + "§c' already exists.");
            return false;
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
                guiManager.reloadGuis();
                return true;
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Could not create GUI file for " + name, e);
            sender.sendMessage("§cAn error occurred while creating the GUI.");
        }

        return false;
    }

    @Override
    public void reloadGui(String name) {
        guiManager.reloadGui(name);
    }

    @Override
    public boolean editGui(@NotNull Player player, String name) {
        QuickGuiHolder editGuiHolder = guiManager.getGui(name);
        if (editGuiHolder == null) {
            player.sendMessage("§cNo GUI with the name '§r" + name + "§c' exists.");
            return true;
        }

        // Create the GUI from system resources
        SystemGuiHolder systemGuiHolder = systemGuiManager.createGuiFromSystemResource("edit-gui", editGuiHolder,
                PluginCommands.Action.EDIT);

        // Verify if any GUI exists with the name.
        if (systemGuiHolder == null) {
            player.sendMessage("§cCouldn't load system edit gui for '§r" + name);
            return true;
        }

        // Open the GUI
        player.openInventory(systemGuiHolder.getInventory());
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
        File guiConfigFile = new File(guiFolder, name + ".yml");
        FileConfiguration guiConfig = YamlConfiguration.loadConfiguration(guiConfigFile);
        guiConfig.set("title", newTitle);

        try {
            guiConfig.save(guiConfigFile);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public boolean editGuiRows(String name, int newRows) {
        File guiConfigFile = new File(guiFolder, name + ".yml");
        FileConfiguration guiConfig = YamlConfiguration.loadConfiguration(guiConfigFile);
        guiConfig.set("rows", newRows);

        try {
            guiConfig.save(guiConfigFile);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

}
