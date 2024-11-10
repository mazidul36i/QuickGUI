package com.gliesestudio.mc.quickgui.service;

import com.gliesestudio.mc.quickgui.QuickGUI;
import com.gliesestudio.mc.quickgui.manager.GuiManager;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EditGuiServiceImpl implements EditGuiService {

    private final Logger logger;
    private final QuickGUI plugin;

    private final GuiManager guiManager;

    public EditGuiServiceImpl(QuickGUI plugin, GuiManager guiManager) {
        this.logger = plugin.getLogger();
        this.plugin = plugin;
        this.guiManager = guiManager;
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

}