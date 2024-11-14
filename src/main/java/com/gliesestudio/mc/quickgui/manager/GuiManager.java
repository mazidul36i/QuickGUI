package com.gliesestudio.mc.quickgui.manager;

import com.gliesestudio.mc.quickgui.QuickGUI;
import com.gliesestudio.mc.quickgui.commands.PluginCommands;
import com.gliesestudio.mc.quickgui.enums.ItemStackType;
import com.gliesestudio.mc.quickgui.inventory.QuickGuiHolder;
import com.gliesestudio.mc.quickgui.model.GuiItem;
import com.gliesestudio.mc.quickgui.utility.PluginUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
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
    private final NamespacedKey COMMAND_KEY;
    private final NamespacedKey COMMAND_EXECUTOR;

    private final QuickGUI plugin;
    private final Map<String, Inventory> guis = new HashMap<>();
    private final Map<String, Map<Integer, String>> guiCommands = new HashMap<>();
    private final File guiFolder;

    public GuiManager(@NotNull QuickGUI plugin) {
        this.COMMAND_KEY = new NamespacedKey(plugin, "gui_command");
        this.COMMAND_EXECUTOR = new NamespacedKey(plugin, "gui_command_executor");

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

    public @NotNull NamespacedKey COMMAND_KEY() {
        return this.COMMAND_KEY;
    }

    public @NotNull NamespacedKey COMMAND_EXECUTOR() {
        return this.COMMAND_EXECUTOR;
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
            Inventory gui = createGuiFromYml(guiFile, guiName, null);
            guis.put(guiName, gui);
        }

        log.info("Loaded guis: {}", guis.size());
        return guis.size();
    }

    @Nullable
    public Inventory createGuiFromYml(@NotNull File guiFile, String guiName, PluginCommands.Action action) {
        log.info("Loading gui config from file: {}", guiFile.getName());
        if (!(guiFile.exists() && guiFile.isFile() && guiFile.getName().endsWith(".yml"))) {
            log.warn("Invalid file format. Skipping");
            return null;
        }

        // Get the name of the GUI from the file name
        FileConfiguration guiConfig = YamlConfiguration.loadConfiguration(guiFile);

        // Get the title and rows from the configuration
        Component titleText = Component.text(PluginUtils.translateColorCodes(guiConfig.getString("title", "Custom GUI")));
        int rows = guiConfig.getInt("rows", 3);

        List<GuiItem> systemActionItems = null;
        if (PluginCommands.Action.EDIT.equals(action)) {
            rows++;
            // Create action buttons.
            systemActionItems = prepareSystemActionButtons(rows);
        }

        // Create an inventory for the GUI
        QuickGuiHolder holder = new QuickGuiHolder(plugin, action);
        Inventory gui = Bukkit.createInventory(holder, rows * 9, titleText);
        Map<Integer, String> commandMap = new HashMap<>();

        List<GuiItem> guiItems = loadItemsFromConfig(guiConfig);
        if (systemActionItems != null) guiItems.addAll(systemActionItems);

        log.info("Items list count for gui: {}", guiItems.size());
        for (GuiItem guiItem : guiItems) {
            int slot = (guiItem.getRow() - 1) * 9 + (guiItem.getColumn() - 1);
            if (slot < 0 || slot >= gui.getSize()) continue;

            // Prepare item stack and add it to the GUI
            ItemStack itemStack = prepareItemStack(guiItem);
            gui.setItem(slot, itemStack);

            // Register the command for this item slot
            if (guiItem.getCommand() != null) {
                commandMap.put(slot, guiItem.getCommand());
            }
        }

        guiCommands.put(guiName, commandMap);
        return gui;
    }

    private @NotNull ItemStack prepareItemStack(GuiItem guiItem) {
        ItemStack itemStack = new ItemStack(guiItem.getItem());
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            // Set display name
            if (guiItem.getDisplayName() != null) {
                meta.displayName(Component.text(PluginUtils.translateColorCodes(guiItem.getDisplayName())));
            } else {
                meta.setHideTooltip(true);
            }

            // Set command into the item meta.
            if (guiItem.getCommand() != null) {
                PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
                dataContainer.set(COMMAND_KEY, PersistentDataType.STRING, guiItem.getCommand());
                dataContainer.set(COMMAND_EXECUTOR, PersistentDataType.STRING, guiItem.getExecutor().getExecutor()); // TODO: nullable
            }

            meta.setCustomModelData(guiItem.getItemStackType().getId());
            itemStack.setItemMeta(meta);
        }
        return itemStack;
    }

    private @NotNull List<GuiItem> loadItemsFromConfig(@NotNull FileConfiguration config) {
        List<GuiItem> items = new ArrayList<>();

        List<Map<?, ?>> itemList = config.getMapList("items");
        for (Map<?, ?> itemConfig : itemList) {
            String materialName = (String) itemConfig.get("item");
            Material material = Material.matchMaterial(materialName);
            if (material == null) continue;

            String displayName = (String) itemConfig.get("display-name");
            String command = (String) itemConfig.get("command");
            String type = (String) itemConfig.get("type");
            ItemStackType itemStackType = ItemStackType.fromName(type);

            Map<?, ?> position = (Map<?, ?>) itemConfig.get("position");
            int row = (int) position.get("row");
            int column = (int) position.get("column");

            GuiItem guiItem = new GuiItem(material, displayName, command, row, column,
                    itemStackType != null ? itemStackType : ItemStackType.BUTTON);
            items.add(guiItem);
        }
        return items;
    }

    /**
     * Prepare system action buttons for edit GUI.
     *
     * @param row position of the edit GUI row.
     * @return List of system action buttons.
     */
    private @NotNull List<GuiItem> prepareSystemActionButtons(int row) {
        List<GuiItem> items = new ArrayList<>();

        // Filler button
        Material fillerMaterial = Material.YELLOW_STAINED_GLASS_PANE;
        items.add(new GuiItem(fillerMaterial, null, null, row, 1, ItemStackType.SYSTEM_FILLER));
        items.add(new GuiItem(fillerMaterial, null, null, row, 2, ItemStackType.SYSTEM_FILLER));
        items.add(new GuiItem(fillerMaterial, null, null, row, 3, ItemStackType.SYSTEM_FILLER));

        // Cancel button
        String cancelDisplayName = "§cCancel";
        String cancelCommand = PluginCommands.SystemCommand.CANCEL.getCommand();
        GuiItem cancelButton = new GuiItem(Material.RED_DYE, cancelDisplayName, cancelCommand, row, 4, ItemStackType.SYSTEM_BUTTON);
        items.add(cancelButton);

        // Close button
        String closeDisplayName = "§8Close";
        String closeCommand = PluginCommands.SystemCommand.CLOSE.getCommand();
        GuiItem closeButton = new GuiItem(Material.GRAY_DYE, closeDisplayName, closeCommand, row, 5, ItemStackType.SYSTEM_BUTTON);
        items.add(closeButton);

        // Done button
        String doneDisplayName = "§aDone";
        String doneCommand = PluginCommands.SystemCommand.DONE.getCommand();
        GuiItem doneButton = new GuiItem(Material.LIME_DYE, doneDisplayName, doneCommand, row, 6, ItemStackType.SYSTEM_BUTTON);
        items.add(doneButton);

        // Add filler buttons
        items.add(new GuiItem(fillerMaterial, null, null, row, 7, ItemStackType.SYSTEM_FILLER));
        items.add(new GuiItem(fillerMaterial, null, null, row, 8, ItemStackType.SYSTEM_FILLER));
        items.add(new GuiItem(fillerMaterial, null, null, row, 9, ItemStackType.SYSTEM_FILLER));

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

}
