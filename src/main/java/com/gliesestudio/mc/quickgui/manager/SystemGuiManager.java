package com.gliesestudio.mc.quickgui.manager;

import com.gliesestudio.mc.quickgui.QuickGUI;
import com.gliesestudio.mc.quickgui.commands.PluginCommands;
import com.gliesestudio.mc.quickgui.enums.CommandExecutor;
import com.gliesestudio.mc.quickgui.enums.ItemStackType;
import com.gliesestudio.mc.quickgui.inventory.QuickGuiHolder;
import com.gliesestudio.mc.quickgui.inventory.SystemGuiHolder;
import com.gliesestudio.mc.quickgui.model.GuiItem;
import com.gliesestudio.mc.quickgui.placeholder.PlaceholderHelper;
import com.gliesestudio.mc.quickgui.placeholder.SystemPlaceholder;
import com.gliesestudio.mc.quickgui.utility.PluginUtils;
import net.kyori.adventure.text.Component;
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SystemGuiManager {

    private static final Logger log = LoggerFactory.getLogger(SystemGuiManager.class);
    private final NamespacedKey COMMAND_KEY;
    private final NamespacedKey COMMAND_EXECUTOR_KEY;

    private final QuickGUI plugin;

    public SystemGuiManager(@NotNull QuickGUI plugin) {
        this.COMMAND_KEY = new NamespacedKey(plugin, "gui_command");
        this.COMMAND_EXECUTOR_KEY = new NamespacedKey(plugin, "gui_command_executor");
        this.plugin = plugin;
    }

    public @NotNull NamespacedKey COMMAND_KEY() {
        return this.COMMAND_KEY;
    }

    public @NotNull NamespacedKey COMMAND_EXECUTOR_KEY() {
        return this.COMMAND_EXECUTOR_KEY;
    }

    @Nullable
    public SystemGuiHolder createGuiFromSystemResource(@NotNull String name, @NotNull QuickGuiHolder editGuiHolder, PluginCommands.Action action) {
        // Read file from resources
        String filePath = "guis/system/" + name + ".yml";

        // Load the file from resources
        InputStream stream = plugin.getResource(filePath);
        if (stream == null) {
            log.error("Could not find file in resources: {}", filePath);
            return null;
        }

        // Create the GUI from the input stream
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(new InputStreamReader(stream));
        return createGuiFromYml(configuration, editGuiHolder, action);
    }

    public @NotNull SystemGuiHolder createGuiFromYml(@NotNull FileConfiguration guiConfig, @NotNull QuickGuiHolder editGuiHolder,
                                                     PluginCommands.Action action) {
        Map<String, String> placeholders = Map.of(
                SystemPlaceholder.GUI_NAME, editGuiHolder.getName(),
                SystemPlaceholder.GUI_TITLE, editGuiHolder.getTitle().content(),
                SystemPlaceholder.GUI_ROWS, String.valueOf(editGuiHolder.getInventory().getSize() / 9),
                SystemPlaceholder.GUI_OPEN_PERMISSION, editGuiHolder.getPermission() != null ? editGuiHolder.getPermission() : "NONE",
                SystemPlaceholder.GUI_ALIAS, editGuiHolder.getAlias() != null ? editGuiHolder.getAlias() : "NONE"
        );

        // Get the title and rows from the configuration
        String name = guiConfig.getString("name", "unknown");
        String titleText = guiConfig.getString("title", "System GUI");
        Component title = Component.text(PluginUtils.translateColorCodes(
                PlaceholderHelper.parseValues(titleText, placeholders)
        ));
        int rows = guiConfig.getInt("rows", 3);

        // Create an inventory for the GUI
        SystemGuiHolder guiHolder = new SystemGuiHolder(plugin, rows, title, name, editGuiHolder, action);
        Inventory systemGui = guiHolder.getInventory();

        List<GuiItem> guiItems = loadItemsFromConfig(guiConfig);
        log.info("Items list count for the system gui: {}", guiItems.size());
        for (GuiItem guiItem : guiItems) {
            int slot = (guiItem.getRow() - 1) * 9 + (guiItem.getColumn() - 1);
            if (slot < 0 || slot >= systemGui.getSize()) continue;

            // Prepare item stack and add it to the GUI
            ItemStack itemStack = prepareItemStack(guiItem, placeholders);
            systemGui.setItem(slot, itemStack);
        }

        return guiHolder;
    }

    private @NotNull ItemStack prepareItemStack(GuiItem guiItem, Map<String, String> placeholders) {
        ItemStack itemStack = new ItemStack(guiItem.getItem());
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            // Set display name
            if (guiItem.getDisplayName() != null) {
                meta.displayName(Component.text(PluginUtils.translateColorCodes(guiItem.getDisplayName())));

                // Set lore with parsed placeholders
                meta.lore(guiItem.getLore().stream().map(text ->
                        text != null ? Component.text(PluginUtils.translateColorCodes(
                                PlaceholderHelper.parseValues(text, placeholders)
                        )) : Component.empty()
                ).toList());
            } else {
                meta.setHideTooltip(true);
            }

            // Set command into the item meta.
            if (guiItem.getCommand() != null) {
                PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
                String commandExecutor = guiItem.getCommandExecutor() != null ? guiItem.getCommandExecutor().getExecutor() : CommandExecutor.PLAYER.getExecutor();
                dataContainer.set(COMMAND_KEY, PersistentDataType.STRING, guiItem.getCommand());
                dataContainer.set(COMMAND_EXECUTOR_KEY, PersistentDataType.STRING, commandExecutor);
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
            List<String> lore = new ArrayList<>();
            if (itemConfig.containsKey("lore") && itemConfig.get("lore") instanceof List) {
                lore = (List<String>) itemConfig.get("lore");
            }
            String command = (String) itemConfig.get("command");
            String type = (String) itemConfig.get("type");
            ItemStackType itemStackType = ItemStackType.fromName(type);
            String executor = (String) itemConfig.get("executor");
            CommandExecutor commandExecutor = CommandExecutor.fromString(executor);

            Map<?, ?> position = (Map<?, ?>) itemConfig.get("position");
            int row = (int) position.get("row");
            int column = (int) position.get("column");

            GuiItem guiItem = new GuiItem()
                    .item(material)
                    .displayName(displayName)
                    .lore(lore)
                    .command(command)
                    .row(row)
                    .column(column)
                    .itemStackType(itemStackType != null ? itemStackType : ItemStackType.BUTTON)
                    .commandExecutor(commandExecutor != null ? commandExecutor : CommandExecutor.PLAYER);
            items.add(guiItem);
        }
        return items;
    }

}
