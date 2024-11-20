package com.gliesestudio.mc.quickgui.gui;

import com.gliesestudio.mc.quickgui.QuickGUI;
import com.gliesestudio.mc.quickgui.commands.PluginCommands;
import com.gliesestudio.mc.quickgui.executor.GuiAliasCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuiManager {

    private static final Logger log = LoggerFactory.getLogger(GuiManager.class);
    public static Map<String, GUI> guis = new HashMap<>();

    public static void init(@NotNull QuickGUI plugin) {
        File guiFolder = getGuisFolder(plugin);
        // Ensure the guis folder exists
        if (!guiFolder.exists() && !guiFolder.mkdirs()) {
            log.error("Failed to create 'guis' folder. Shutting down QuickQUI!");
            return;
        }

        // Load GUIs from individual files
        loadGuis(plugin, guiFolder);
    }

    /**
     * Get 'guis' data folder of the plugin.
     *
     * @param plugin Main plugin class {@link QuickGUI}.
     * @return {@link File} having path to 'guis' folder.
     */
    public static File getGuisFolder(@NotNull QuickGUI plugin) {
        return new File(plugin.getDataFolder(), "guis");
    }

    /**
     * Loads all GUI definitions from the configuration file.
     */
    private static int loadGuis(@NotNull QuickGUI plugin, File guiFolder) {
        log.info("Loading GUIs...");
        guis.clear();

        File[] guiFiles = guiFolder.listFiles();
        if (guiFiles == null || guiFiles.length == 0) {
            log.info("No GUI files found!");
            return 0;
        }

        // Loop through each file in the guis folder
        for (File guiFile : guiFiles) {
            GUI gui = createGuiFromFile(guiFile);
            if (gui == null) continue;
            guis.put(gui.getName(), gui);
        }
        log.info("Loaded guis: {}", guis.size());

        // Register aliases
        registerGuiAliases(plugin);
        return guis.size();
    }

    @Nullable
    public static GUI createGuiFromFile(@NotNull File guiFile) {
        log.info("Loading gui config from file: {}", guiFile.getName());
        if (!(guiFile.exists() && guiFile.isFile() && guiFile.getName().endsWith(".yml"))) {
            log.warn("Invalid file format. Skipping");
            return null;
        }

        // Deserialize YML config to GUI.
        return GUI.deserialize(YamlConfiguration.loadConfiguration(guiFile));
    }

    public static int reloadGuis(@NotNull QuickGUI plugin) {
        log.info("Reloading GUIs...");
        unregisterGuiAliases();
        return loadGuis(plugin, getGuisFolder(plugin));
    }

    public static void reloadGui(@NotNull QuickGUI plugin, String name) {
        GUI gui = createGuiFromFile(new File(getGuisFolder(plugin), name + ".yml"));
        if (gui != null) guis.put(name, gui);
    }

    @Nullable
    public static GUI getGui(String name) {
        return guis.get(name);
    }

    public static List<String> getGuiNames() {
        return guis.keySet().stream().toList();
    }

    // Register each alias as a command dynamically
    private static void registerGuiAliases(@NotNull QuickGUI plugin) {
        log.info("Registering GUI aliases...");
        CommandMap commandMap = getCommandMap();
        if (commandMap != null) {
            guis.forEach((name, gui) -> {
                if (gui.hasAlias()) {
                    GuiAliasCommand aliasCommand = new GuiAliasCommand(plugin, gui.getAlias(), name,
                            gui.getPermission());
                    commandMap.register(PluginCommands.QUICK_GUI, aliasCommand);
                }
            });
        }
    }

    private static void unregisterGuiAliases() {
        log.info("Unregistering GUI aliases...");
        CommandMap commandMap = getCommandMap();
        if (!(commandMap instanceof SimpleCommandMap simpleCommandMap)) return;
        // Unregister each alias commands
        guis.forEach((name, gui) -> {
            if (gui.hasAlias()) {
                simpleCommandMap.getKnownCommands().remove(PluginCommands.QUICK_GUI + ":" + gui.getAlias());
                simpleCommandMap.getKnownCommands().remove(gui.getAlias());
            }
        });
    }

    // Reflect to access Bukkit's command map
    @Nullable
    private static CommandMap getCommandMap() {
        try {
            Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            return (CommandMap) commandMapField.get(Bukkit.getServer());
        } catch (Exception e) {
            log.error("Error getting command map", e);
            return null;
        }
    }

}
