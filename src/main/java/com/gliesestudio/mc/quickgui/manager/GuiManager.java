package com.gliesestudio.mc.quickgui.manager;

import com.gliesestudio.mc.quickgui.QuickGUI;
import com.gliesestudio.mc.quickgui.inventory.QuickGuiHolder;
import com.gliesestudio.mc.quickgui.model.GuiItem;
import com.gliesestudio.mc.quickgui.utility.PluginUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuiManager {

    private static final Logger log = LoggerFactory.getLogger(GuiManager.class);

    private final QuickGUI plugin;
    private final Map<String, Inventory> guis = new HashMap<>();
    private final Map<String, Map<Integer, String>> guiCommands = new HashMap<>();
    private final File guiFolder;

    public GuiManager(QuickGUI plugin) {
        this.plugin = plugin;
        this.guiFolder = new File(plugin.getDataFolder(), "guis");

        // Ensure the guis folder exists
        if (!guiFolder.exists() && !guiFolder.mkdirs()) {
            log.error("Failed to create 'guis' folder. Shutting down QuickQUI!");
            return;
        }

        // Load GUIs from individual files
        loadGuis();
    }

    /**
     * Loads all GUI definitions from the configuration file.
     */
    private int loadGuis() {
        log.info("Loading GUIs...");
        guis.clear();
        guiCommands.clear();

        File[] guiFiles = guiFolder.listFiles();
        if (guiFiles == null || guiFiles.length == 0) {
            log.info("No GUI files found!");
            return 0;
        }

        // Loop through each file in the guis folder
        for (File guiFile : guiFiles) {
            String guiName = guiFile.getName().replace(".yml", "");
            Inventory gui = createGui(guiFile, guiName);
            guis.put(guiName, gui);
        }

        log.info("Loaded guis: {}", guis.size());
        return guis.size();
    }

    @Nullable
    private Inventory createGui(File guiFile, String guiName) {
        log.info("Loading gui config from file: {}", guiFile.getName());
        if (!(guiFile.isFile() && guiFile.getName().endsWith(".yml"))) {
            log.warn("Invalid file format. Skipping");
            return null;
        }

        // Get the name of the GUI from the file name
        FileConfiguration guiConfig = YamlConfiguration.loadConfiguration(guiFile);

        // Get the title and rows from the configuration
        Component titleText = Component.text(PluginUtils.translateColorCodes(guiConfig.getString("title", "Custom GUI")));
        int rows = guiConfig.getInt("rows", 3);

        // Create an inventory for the GUI
        QuickGuiHolder holder = new QuickGuiHolder(plugin);
        Inventory gui = Bukkit.createInventory(holder, rows * 9, titleText);
        Map<Integer, String> commandMap = new HashMap<>();

        List<GuiItem> guiItems = loadItemsFromConfig(guiConfig);
        log.info("Items list count for gui: {}", guiItems.size());
        for (GuiItem guiItem : guiItems) {
            int slot = (guiItem.getRow() - 1) * 9 + (guiItem.getColumn() - 1);

            ItemStack itemStack = new ItemStack(guiItem.getItem());
            ItemMeta meta = itemStack.getItemMeta();
            if (meta != null) {
                meta.displayName(Component.text(PluginUtils.translateColorCodes(guiItem.getDisplayName())));
                itemStack.setItemMeta(meta);
            }
            gui.setItem(slot, itemStack);

            // Register the command for this item slot
            if (guiItem.getCommand() != null) {
                commandMap.put(slot, guiItem.getCommand());
            }
        }

        guiCommands.put(guiName, commandMap);
        return gui;
    }

    private List<GuiItem> loadItemsFromConfig(FileConfiguration config) {
        List<GuiItem> items = new ArrayList<>();

        List<Map<?, ?>> itemList = config.getMapList("items");
        for (Map<?, ?> itemConfig : itemList) {
            String materialName = (String) itemConfig.get("item");
            Material material = Material.matchMaterial(materialName);
            if (material == null) continue;

            String displayName = (String) itemConfig.get("display-name");
            String command = (String) itemConfig.get("command");

            Map<?, ?> position = (Map<?, ?>) itemConfig.get("position");
            int row = (int) position.get("row");
            int column = (int) position.get("column");

            GuiItem guiItem = new GuiItem(material, displayName, command, row, column);
            items.add(guiItem);
        }
        return items;
    }

    public int reloadGuis() {
        return loadGuis();
    }

    public Inventory getGui(String name) {
        return guis.get(name);
    }

    public List<String> getGuiNames() {
        return guis.keySet().stream().toList();
    }

    /**
     * Executes the command associated with a specific GUI slot.
     *
     * @param player  The player who clicked the item.
     * @param guiName The name of the GUI.
     * @param slot    The slot number that was clicked.
     */
    public void executeCommand(Player player, String guiName, int slot) {
        if (guiCommands.containsKey(guiName) && guiCommands.get(guiName).containsKey(slot)) {
            String command = guiCommands.get(guiName).get(slot);
            // Replace placeholder %player% with the actual player's name
            command = command.replace("%player%", player.getName());
//            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
            Bukkit.dispatchCommand(player, command);
        }
    }

}
