package com.gliesestudio.mc.quickgui.manager;

import com.gliesestudio.mc.quickgui.QuickGUI;
import com.gliesestudio.mc.quickgui.commands.PluginCommands;
import com.gliesestudio.mc.quickgui.enums.CommandExecutor;
import com.gliesestudio.mc.quickgui.enums.ItemStackType;
import com.gliesestudio.mc.quickgui.executor.GuiAliasCommand;
import com.gliesestudio.mc.quickgui.inventory.QuickGuiHolder;
import com.gliesestudio.mc.quickgui.model.GuiItem;
import com.gliesestudio.mc.quickgui.placeholder.PlaceholderHelper;
import com.gliesestudio.mc.quickgui.placeholder.SystemPlaceholder;
import com.gliesestudio.mc.quickgui.utility.PluginUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandMap;
import org.bukkit.command.SimpleCommandMap;
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
import java.lang.reflect.Field;
import java.util.*;

public class GuiManager {

    private static final Logger log = LoggerFactory.getLogger(GuiManager.class);
    private final NamespacedKey COMMAND_KEY;
    private final NamespacedKey COMMAND_EXECUTOR_KEY;

    private final QuickGUI plugin;
    private final Map<String, QuickGuiHolder> guis = new HashMap<>();
    private final Map<String, QuickGuiHolder> aliases = new HashMap<>();
    private final File guiFolder;

    public GuiManager(@NotNull QuickGUI plugin) {
        this.COMMAND_KEY = new NamespacedKey(plugin, "gui_command");
        this.COMMAND_EXECUTOR_KEY = new NamespacedKey(plugin, "gui_command_executor");

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

    public @NotNull NamespacedKey COMMAND_EXECUTOR_KEY() {
        return this.COMMAND_EXECUTOR_KEY;
    }

    /**
     * Loads all GUI definitions from the configuration file.
     */
    private int loadGuis() {
        log.info("Loading GUIs...");
        guis.clear();
        aliases.clear();

        File[] guiFiles = guiFolder.listFiles();
        if (guiFiles == null || guiFiles.length == 0) {
            log.info("No GUI files found!");
            return 0;
        }

        // Loop through each file in the guis folder
        for (File guiFile : guiFiles) {
            QuickGuiHolder guiHolder = createGuiFromFile(guiFile, null);
            if (guiHolder == null) continue;
            guis.put(guiHolder.getName(), guiHolder);
            if (guiHolder.getAlias() != null) aliases.put(guiHolder.getAlias(), guiHolder);
        }

        // Register aliases
        registerGuiAliases(aliases);

        log.info("Loaded guis: {}", guis.size());
        return guis.size();
    }

    @Nullable
    public QuickGuiHolder createGuiFromFile(@NotNull File guiFile, PluginCommands.Action action) {
        log.info("Loading gui config from file: {}", guiFile.getName());
        if (!(guiFile.exists() && guiFile.isFile() && guiFile.getName().endsWith(".yml"))) {
            log.warn("Invalid file format. Skipping");
            return null;
        }

        // Create the GUI from the file
        FileConfiguration configuration = YamlConfiguration.loadConfiguration(guiFile);
        return createGuiFromYml(configuration, action);
    }

    public @NotNull QuickGuiHolder createGuiFromYml(@NotNull FileConfiguration guiConfig, PluginCommands.Action action) {
        // Get the title and rows from the configuration
        String name = guiConfig.getString("name", "unknown");
        String titleText = guiConfig.getString("title", "Custom GUI");
        String alias = guiConfig.getString("alias");
        String permission = guiConfig.getString("permission");
        int rows = guiConfig.getInt("rows", 3);

        TextComponent title = Component.text(PluginUtils.translateColorCodes(
                PlaceholderHelper.parseValue(titleText, SystemPlaceholder.GUI_NAME, name)
        ));

        // Create an inventory for the GUI
        QuickGuiHolder guiHolder = new QuickGuiHolder(plugin, rows, title, name, action, alias, permission);
        Inventory gui = guiHolder.getInventory();

        List<GuiItem> guiItems = loadItemsFromConfig(guiConfig);

        log.info("Items list count for gui: {}", guiItems.size());
        for (GuiItem guiItem : guiItems) {
            int slot = (guiItem.getRow() - 1) * 9 + (guiItem.getColumn() - 1);
            if (slot < 0 || slot >= gui.getSize()) continue;

            // Prepare item stack and add it to the GUI
            ItemStack itemStack = prepareItemStack(guiItem, name, titleText, rows);
            gui.setItem(slot, itemStack);
        }

        return guiHolder;
    }

    private @NotNull ItemStack prepareItemStack(GuiItem guiItem, String guiName, String guiTitle, int guiRows) {
        ItemStack itemStack = new ItemStack(guiItem.getItem());
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            // Create all required placeholders
            Map<String, String> placeholders = Map.of(
                    SystemPlaceholder.GUI_NAME, guiName,
                    SystemPlaceholder.GUI_TITLE, guiTitle,
                    SystemPlaceholder.GUI_ROWS, String.valueOf(guiRows),
                    SystemPlaceholder.GUI_OPEN_PERMISSION, "NONE", // TODO: not implemented yet
                    SystemPlaceholder.GUI_ALIAS, "NONE" // TODO: not implemented yet
            );

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

    public int reloadGuis() {
        registerGuiAliases(aliases.keySet());
        return loadGuis();
    }

    public void reloadGui(String name) {
        QuickGuiHolder guiHolder = createGuiFromFile(new File(guiFolder, name + ".yml"), null);
        if (guiHolder != null) guis.put(name, guiHolder);
    }

    public QuickGuiHolder getGui(String name) {
        return guis.get(name);
    }

    public List<String> getGuiNames() {
        return guis.keySet().stream().toList();
    }

    // Register each alias as a command dynamically
    private void registerGuiAliases(Map<String, QuickGuiHolder> aliases) {
        CommandMap commandMap = getCommandMap();
        if (commandMap != null) {
            aliases.forEach((alias, guiHolder) -> {
                GuiAliasCommand aliasCommand = new GuiAliasCommand(this, alias, guiHolder.getName(),
                        guiHolder.getPermission());
                commandMap.register(PluginCommands.QUICK_GUI, aliasCommand);
            });
        }
    }

    private void registerGuiAliases(Set<String> aliases) {
        CommandMap commandMap = getCommandMap();
        if (!(commandMap instanceof SimpleCommandMap simpleCommandMap)) return;
        // Unregister each alias commands
        aliases.forEach(alias -> {
            simpleCommandMap.getKnownCommands().remove(PluginCommands.QUICK_GUI + ":" + alias);
            simpleCommandMap.getKnownCommands().remove(alias);
        });

//        Bukkit.getOnlinePlayers().forEach(Player::updateCommands); // TODO: not implemented yet
    }

    // Reflect to access Bukkit's command map
    @Nullable
    private CommandMap getCommandMap() {
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
