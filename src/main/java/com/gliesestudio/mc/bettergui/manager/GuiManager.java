package com.gliesestudio.mc.bettergui.manager;

import com.gliesestudio.mc.bettergui.BetterGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class GuiManager {

    private static final Logger log = LoggerFactory.getLogger(GuiManager.class);
    private final BetterGUI plugin;
    private final Map<String, Inventory> guis = new HashMap<>();
    private final Map<String, Map<Integer, String>> guiCommands = new HashMap<>();

    public GuiManager(BetterGUI plugin) {
        this.plugin = plugin;
        loadGuis(plugin.getConfig());
    }

    /**
     * Loads all GUI definitions from the configuration file.
     *
     * @param config The plugin configuration file.
     */
    public void loadGuis(FileConfiguration config) {
        log.info("Loading GUIs...");
        guis.clear();
        guiCommands.clear();

        if (!config.contains("guis")) {
            plugin.getLogger().warning("No GUIs defined in the configuration.");
            return;
        }

        // Loop through each GUI in the configuration
        for (String guiName : config.getConfigurationSection("guis").getKeys(false)) {
            String title = config.getString("guis." + guiName + ".title", "Custom GUI");
            int rows = config.getInt("guis." + guiName + ".rows", 3);
            Inventory inventory = Bukkit.createInventory(null, rows * 9, title);

            Map<Integer, String> commandMap = new HashMap<>();

            // Loop through each item slot in the GUI
            for (String slotKey : config.getConfigurationSection("guis." + guiName + ".items").getKeys(false)) {
                int slot = Integer.parseInt(slotKey.replace("slot", ""));
                String materialName = config.getString("guis." + guiName + ".items." + slotKey + ".item", "STONE");
                String displayName = config.getString("guis." + guiName + ".items." + slotKey + ".display-name", "");
                String command = config.getString("guis." + guiName + ".items." + slotKey + ".command");

                Material material = Material.getMaterial(materialName.toUpperCase());
                if (material == null) {
                    plugin.getLogger().warning("Invalid material for item in GUI '" + guiName + "' at slot " + slot + ". Defaulting to STONE.");
                    material = Material.STONE;
                }

                ItemStack item = new ItemStack(material);
                ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    meta.setDisplayName(displayName);
                    item.setItemMeta(meta);
                }

                inventory.setItem(slot, item);

                // Store the command associated with this slot if it exists
                if (command != null) {
                    commandMap.put(slot, command);
                }
            }

            guis.put(guiName, inventory);
            guiCommands.put(guiName, commandMap);
        }
        log.info("Loaded guis: {}", guis.size());
    }

    public void reloadConfig(FileConfiguration config) {
        loadGuis(config);
    }

    public Map<String, Inventory> getGuis() {
        return guis;
    }

    public Inventory getGui(String name) {
        return guis.get(name);
    }

    /**
     * Executes the command associated with a specific GUI slot.
     *
     * @param guiName The name of the GUI.
     * @param slot    The slot number that was clicked.
     * @param player  The player who clicked the item.
     */
    public void executeCommand(String guiName, int slot, Player player) {
        if (guiCommands.containsKey(guiName) && guiCommands.get(guiName).containsKey(slot)) {
            String command = guiCommands.get(guiName).get(slot);
            // Replace placeholder %player% with the actual player's name
            command = command.replace("%player%", player.getName());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        }
    }
}
