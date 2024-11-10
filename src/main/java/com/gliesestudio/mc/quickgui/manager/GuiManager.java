package com.gliesestudio.mc.quickgui.manager;

import com.gliesestudio.mc.quickgui.QuickGUI;
import com.gliesestudio.mc.quickgui.inventory.QuickGuiHolder;
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

import java.io.File;
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
    private void loadGuis() {
        log.info("Loading GUIs...");
        guis.clear();
        guiCommands.clear();

        File[] guiFiles = guiFolder.listFiles();
        if (guiFiles == null || guiFiles.length == 0) {
            log.info("No GUI files found!");
            return;
        }

        // Loop through each file in the guis folder
        for (File file : guiFiles) {
            if (file.isFile() && file.getName().endsWith(".yml")) {
                FileConfiguration config = YamlConfiguration.loadConfiguration(file);

                String guiName = file.getName().replace(".yml", ""); // Use file name as GUI name
                Component titleText = Component.text(PluginUtils.translateColorCodes(config.getString("title", "Custom GUI")));
                int rows = config.getInt("rows", 3);

                // Create an inventory for the GUI
                QuickGuiHolder holder = new QuickGuiHolder(plugin);
                Inventory inventory = Bukkit.createInventory(holder, rows * 9, titleText);
                Map<Integer, String> commandMap = new HashMap<>();

                for (String slotKey : config.getConfigurationSection("items").getKeys(false)) {
                    int slot = Integer.parseInt(slotKey.replace("slot", ""));
                    String materialName = config.getString("items." + slotKey + ".item", "STONE");
                    String displayName = config.getString("items." + slotKey + ".display-name", "");
                    String command = config.getString("items." + slotKey + ".command");

                    Material material = Material.getMaterial(materialName.toUpperCase());
                    if (material == null) {
                        plugin.getLogger().warning("Invalid material in file " + file.getName() + " for slot " + slot + ". Defaulting to STONE.");
                        material = Material.STONE;
                    }

                    ItemStack item = new ItemStack(material);
                    ItemMeta meta = item.getItemMeta();
                    if (meta != null) {
                        meta.displayName(Component.text(PluginUtils.translateColorCodes(displayName)));
                        item.setItemMeta(meta);
                    }

                    inventory.setItem(slot, item);

                    if (command != null) {
                        commandMap.put(slot, command);
                    }
                }

                guis.put(guiName, inventory);
                guiCommands.put(guiName, commandMap);
            }
        }
        log.info("Loaded guis: {}", guis.size());
    }

    public void reloadGuis() {
        loadGuis();
    }

    public Map<String, Inventory> getGuis() {
        return guis;
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
