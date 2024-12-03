package com.gliesestudio.mc.quickgui.service;

import com.gliesestudio.mc.quickgui.QuickGUI;
import com.gliesestudio.mc.quickgui.enums.AwaitingInputType;
import com.gliesestudio.mc.quickgui.enums.PlayerHead;
import com.gliesestudio.mc.quickgui.enums.SystemCommand;
import com.gliesestudio.mc.quickgui.gui.GUI;
import com.gliesestudio.mc.quickgui.gui.OpenMode;
import com.gliesestudio.mc.quickgui.gui.SystemGuiHolder;
import com.gliesestudio.mc.quickgui.gui.command.GuiCommandExecutor;
import com.gliesestudio.mc.quickgui.gui.item.*;
import com.gliesestudio.mc.quickgui.utility.CollectionUtils;
import com.gliesestudio.mc.quickgui.utility.Constants;
import com.gliesestudio.mc.quickgui.utility.StringUtils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;

// TODO: Make implementation for changes and saving of item actions...
public class EditActionServiceImpl implements EditActionService {

    private static final Logger log = LoggerFactory.getLogger(EditLoreServiceImpl.class);
    private final QuickGUI plugin;
    private final File guiFolder;

    public EditActionServiceImpl(QuickGUI plugin) {
        this.plugin = plugin;
        this.guiFolder = new File(plugin.getDataFolder(), "guis");
    }

    @Override
    public void openEditActionGui(@NotNull Player player, SystemGuiHolder systemGuiHolder, SystemCommand actionCommand) {
        log.info("Open edit action gui for item slot: {} and action command: {}", systemGuiHolder.getEditItemSlot(), actionCommand);
        GuiItemActionType itemActionType = actionCommand.getItemActionType();
        // Verify if the action type is valid.
        if (itemActionType == null) {
            player.sendMessage("§cCouldn't find the action type for the command: " + actionCommand);
            return;
        }

        // Create the GUI from system resources
        GUI editActionGui = createActionGui(systemGuiHolder.getGui().getItem(systemGuiHolder.getEditItemSlot()), itemActionType);

        // Open the GUI
        SystemGuiHolder editItemGuiHolder = new SystemGuiHolder(plugin, player, editActionGui, systemGuiHolder.getGui(),
                OpenMode.EDIT_ACTIONS, systemGuiHolder.getEditItemSlot(), itemActionType, systemGuiHolder);
        player.openInventory(editItemGuiHolder.getInventory());
    }

    @Override
    public void deleteItemActionCommand(Player player, SystemGuiHolder systemGuiHolder, @NotNull SystemCommand deleteCommand, int deleteCommandPosition) {
        GuiItemActionType itemActionType = deleteCommand.getItemActionType();
        // Verify if the action type is valid.
        if (itemActionType == null) {
            player.sendMessage("§cCouldn't find the action type for the command: " + deleteCommand);
            return;
        }

        log.info("Delete command for action: {}, pos: {}", itemActionType, deleteCommandPosition);
        GUI gui = systemGuiHolder.getGui();
        if (gui == null) return;
        GuiItem guiItem = gui.getItem(systemGuiHolder.getEditItemSlot());
        GuiItemAction guiItemAction = guiItem.getAction(itemActionType);
        if (guiItemAction == null || CollectionUtils.isEmpty(guiItemAction.getCommands())) return;

        // Remove the lore
        if (deleteCommandPosition < 0 || deleteCommandPosition >= guiItemAction.getCommands().size()) return;
        guiItemAction.getCommands().remove(deleteCommandPosition);

        try {
            File guiConfigFile = new File(guiFolder, systemGuiHolder.getGui().getName() + ".yml");
            FileConfiguration guiConfig = gui.serialize();
            guiConfig.save(guiConfigFile);
        } catch (IOException ignored) {
        }

        // Recreate command gui items
        Inventory inventory = systemGuiHolder.getInventory();
        GUI systemGui = systemGuiHolder.getSystemGui();
        Map<Integer, GuiItem> systemGuiItems = systemGui.getItems();
        for (int i = 0; i < 6; i++) {
            systemGuiItems.remove(i + 29);
            inventory.clear(i + 29);
        }
        systemGuiItems.putAll(createCommandsGuiItems(guiItem.getAction(itemActionType), itemActionType));
        for (int i = 0; i < 6; i++) {
            if (systemGuiItems.containsKey(i + 29))
                inventory.setItem(i + 29, systemGuiItems.get(i + 29).createItemStack(player));
        }
    }

    @Override
    public SystemGuiHolder editItemCommandConfig(Player player, SystemGuiHolder systemGuiHolder, @NotNull AwaitingInputType inputType, String command, Integer editCommandPosition) {
        boolean isAddCommand = editCommandPosition == null;
        GuiItemActionType itemActionType = switch (inputType) {
            case INPUT_ADD_ITEM_ACTION_LEFT_COMMAND,
                 INPUT_EDIT_ITEM_ACTION_LEFT_COMMAND -> GuiItemActionType.LEFT;
            case INPUT_ADD_ITEM_ACTION_SHIFT_LEFT_COMMAND,
                 INPUT_EDIT_ITEM_ACTION_SHIFT_LEFT_COMMAND -> GuiItemActionType.SHIFT_LEFT;
            case INPUT_ADD_ITEM_ACTION_MIDDLE_COMMAND,
                 INPUT_EDIT_ITEM_ACTION_MIDDLE_COMMAND -> GuiItemActionType.MIDDLE;
            case INPUT_ADD_ITEM_ACTION_RIGHT_COMMAND,
                 INPUT_EDIT_ITEM_ACTION_RIGHT_COMMAND -> GuiItemActionType.RIGHT;
            case INPUT_ADD_ITEM_ACTION_SHIFT_RIGHT_COMMAND,
                 INPUT_EDIT_ITEM_ACTION_SHIFT_RIGHT_COMMAND -> GuiItemActionType.SHIFT_RIGHT;
            default -> null;
        };
        // Verify if the action type is valid.
        if (itemActionType == null) return null;

        GUI gui = systemGuiHolder.getGui();
        if (gui == null) return null;
        GuiItem guiItem = gui.getItem(systemGuiHolder.getEditItemSlot());
        GuiItemAction guiItemAction = guiItem.getAction(itemActionType);

        // Set actions and commands if it is null
        if (guiItemAction == null) {
            guiItemAction = new GuiItemAction();
            guiItemAction.setExecutor(GuiCommandExecutor.SERVER);
        }
        if (guiItemAction.getCommands() == null) guiItemAction.setCommands(new ArrayList<>());

        // Add or edit command
        if (isAddCommand) {
            guiItemAction.getCommands().add(command);
        } else {
            if (editCommandPosition < 0 || editCommandPosition >= guiItemAction.getCommands().size()) return null;
            guiItemAction.getCommands().set(editCommandPosition, command);
        }

        // Set updated action avoiding null pointer exceptions.
        if (guiItem.getActions() == null) guiItem.setActions(Map.of(itemActionType, guiItemAction));
        else guiItem.getActions().put(itemActionType, guiItemAction);

        // Save the updated action config
        try {
            File guiConfigFile = new File(guiFolder, systemGuiHolder.getGui().getName() + ".yml");
            FileConfiguration guiConfig = gui.serialize();
            guiConfig.save(guiConfigFile);
            // Recreate the system GUI with updated command actions.
            GUI actionGui = createActionGui(guiItem, itemActionType);
            return new SystemGuiHolder(systemGuiHolder, actionGui);
        } catch (IOException e) {
            return null;
        }
    }

    private GUI createActionGui(GuiItem editItem, GuiItemActionType itemActionType) {
        GUI actionGui = new GUI();
        actionGui.setName("edit-actions");
        actionGui.setTitle("&9Edit Actions");
        actionGui.setPermission("quickqui.edit");
        actionGui.setRows(5);

        Map<Integer, GuiItem> guiItemMap = new HashMap<>();
        guiItemMap.put(4, editItem);
        guiItemMap.putAll(createStaticActionGuiItems(editItem.getAction(itemActionType)));
        guiItemMap.putAll(createCommandsGuiItems(editItem.getAction(itemActionType), itemActionType));

        actionGui.setItems(guiItemMap);
        return actionGui;
    }

    // TODO: Implement click price
    // TODO: implement cooldown
    private Map<Integer, GuiItem> createStaticActionGuiItems(GuiItemAction itemAction) {
        Map<Integer, GuiItem> guiItemMap = new HashMap<>();

        if (itemAction == null) itemAction = new GuiItemAction();
        String permission = StringUtils.hasText(itemAction.getPermission()) ? itemAction.getPermission() : Constants.NONE;
        boolean closeInventory = itemAction.isCloseInv();
        String cooldown = Constants.NONE;
        String moneyPrice = Constants.NONE;
        String itemPrice = Constants.NONE;
        String expPrice = Constants.NONE;

        // Create filler items
        Set<Integer> fillerSlots = Set.of(0, 1, 2, 3, 5, 6, 8, 9, 13, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 35, 36, 37, 38, 39, 40, 41, 42, 43);
        GuiItem fillerItem = new GuiItem();
        fillerItem.setItem(GuiItemInfo.fromMaterial(Material.GRAY_STAINED_GLASS_PANE, GuiItemType.SYSTEM_FILLER, true));
        fillerSlots.forEach(slot -> guiItemMap.put(slot, fillerItem));

        // Create row-1 items
        GuiItem changeCurrency = new GuiItem();
        changeCurrency.setItem(GuiItemInfo.builder()
                .name(Material.PLAYER_HEAD.name())
                .displayName("&6Change Currency")
                .texture(PlayerHead.COIN.name())
                .lore(List.of("&aVault", "&7PlayerPoints"))
                .type(GuiItemType.SYSTEM_BUTTON)
                .hideTooltip(false)
                .build());
        guiItemMap.put(7, changeCurrency);

        // Create row-2 items
        GuiItem changePermission = new GuiItem();
        changePermission.setItem(GuiItemInfo.builder()
                .name(Material.GOLDEN_HORSE_ARMOR.name())
                .displayName("&9Change Permission")
                .lore(List.of("&7Change the click permission",
                        "",
                        "&cNONE &7for no permission",
                        "",
                        "&7Current: &a" + permission)
                )
                .type(GuiItemType.SYSTEM_BUTTON)
                .hideTooltip(false)
                .build());
        guiItemMap.put(10, changePermission);

        GuiItem changeCooldown = new GuiItem();
        changeCooldown.setItem(GuiItemInfo.builder()
                .name(Material.CLOCK.name())
                .displayName("&9Change Cooldown")
                .lore(List.of("",
                        "&7Units",
                        "&ams&7, &as&7, &am&7, &ah&7, &ad",
                        "",
                        "&7Current: &a" + cooldown)
                )
                .type(GuiItemType.SYSTEM_BUTTON)
                .hideTooltip(false)
                .build());
        guiItemMap.put(11, changeCooldown);

        GuiItem toggleCloseGui = new GuiItem();
        toggleCloseGui.setItem(GuiItemInfo.builder()
                .name(Material.CHEST.name())
                .displayName("&9Toggle close GUI")
                .lore(List.of("&7Close GUI before executing commands?",
                        "",
                        "&7Current: &a" + closeInventory)
                )
                .type(GuiItemType.SYSTEM_BUTTON)
                .hideTooltip(false)
                .build());
        guiItemMap.put(12, toggleCloseGui);

        GuiItem changeMoneyPrice = new GuiItem();
        changeMoneyPrice.setItem(GuiItemInfo.builder()
                .name(Material.GOLD_NUGGET.name())
                .displayName("&9Change Money Price")
                .lore(List.of("&7Pay money per click",
                        "",
                        "&7Current: &a" + moneyPrice)
                )
                .type(GuiItemType.SYSTEM_BUTTON)
                .hideTooltip(false)
                .build());
        guiItemMap.put(14, changeMoneyPrice);

        GuiItem changeItemPrice = new GuiItem();
        changeItemPrice.setItem(GuiItemInfo.builder()
                .name(Material.DIAMOND.name())
                .displayName("&9Change Item Price")
                .lore(List.of("&7Pay items per click",
                        "",
                        "&7Current: &a" + itemPrice)
                )
                .type(GuiItemType.SYSTEM_BUTTON)
                .hideTooltip(false)
                .build());
        guiItemMap.put(15, changeItemPrice);

        GuiItem changeExpPrice = new GuiItem();
        changeExpPrice.setItem(GuiItemInfo.builder()
                .name(Material.EXPERIENCE_BOTTLE.name())
                .displayName("&9Change Exp Price")
                .lore(List.of("&7Pay experience per click",
                        "",
                        "&7Current: &a" + expPrice)
                )
                .type(GuiItemType.SYSTEM_BUTTON)
                .hideTooltip(false)
                .build());
        guiItemMap.put(16, changeExpPrice);

        // Create row-4 items
        GuiItem commandsInfo = new GuiItem();
        commandsInfo.setItem(GuiItemInfo.builder()
                .name(Material.WRITABLE_BOOK.name())
                .displayName("&6&lCommands Info")
                .lore(List.of("&f<PLAYER> &7will be replaced with the Target/Viewer player",
                        "&fServer Example: &7<SERVER>kill <PLAYER>",
                        "&fMessage Example: &7<MSG>&a[SHOP]&7 You bought an Item",
                        "&fBungee Example: &7<CONNECT> server",
                        "&fPlayer OP Example: &7<OP> heal",
                        "&fSound Example: &7<SOUND>note.pling",
                        "&fPrev. GUI Example: &7<BACK>",
                        "&fPlayer Example: &7opengui welcome",
                        "",
                        "&fLeft click: &7Edit",
                        "&fRight click: &7Delete")
                )
                .type(GuiItemType.SYSTEM_BUTTON)
                .hideTooltip(false)
                .build());
        guiItemMap.put(28, commandsInfo);

        // Create row-5 items
        GuiItem back = new GuiItem();
        back.setItem(GuiItemInfo.builder()
                .name(Material.PLAYER_HEAD.name())
                .displayName("&cBack")
                .texture(PlayerHead.BACK.name())
                .type(GuiItemType.SYSTEM_FILLER)
                .hideTooltip(false)
                .build());
        GuiItemAction backActions = new GuiItemAction();
        backActions.setCommands(List.of("back"));
        back.setActions(Map.of(GuiItemActionType.LEFT, backActions));
        guiItemMap.put(44, back);

        return guiItemMap;
    }

    private Map<Integer, GuiItem> createCommandsGuiItems(GuiItemAction itemAction, GuiItemActionType itemActionType) {
        Map<Integer, GuiItem> commandsItemMap = new HashMap<>();

        // Add command GUI item
        GuiItem addCommandItem = new GuiItem();
        addCommandItem.setItem(GuiItemInfo.builder()
                .name(Material.PLAYER_HEAD.name())
                .displayName("&aAdd Command")
                .texture(PlayerHead.PUMPKIN_PLUS.name())
                .type(GuiItemType.SYSTEM_BUTTON)
                .hideTooltip(false)
                .build());
        // Actions
        GuiItemAction addCommandLeftAction = new GuiItemAction();
        addCommandLeftAction.setCommands(List.of(String.format("add item action %s command", itemActionType.getType())));
        addCommandItem.setActions(Map.of(GuiItemActionType.LEFT, addCommandLeftAction));

        if (itemAction == null || CollectionUtils.isEmpty(itemAction.getCommands())) {
            commandsItemMap.put(29, addCommandItem);
            return commandsItemMap;
        }

        List<String> commands = itemAction.getCommands().stream().filter(StringUtils::hasText).limit(6).toList();
        for (int i = 0; i < commands.size(); i++) {
            commandsItemMap.put(i + 29, createCommandGuiItem(commands.get(i), itemActionType));
        }

        if (commands.size() < 6) commandsItemMap.put(29 + commands.size(), addCommandItem);
        return commandsItemMap;
    }

    private static @NotNull GuiItem createCommandGuiItem(@NotNull String command, GuiItemActionType itemActionType) {
        GuiItem guiItem = new GuiItem();
        guiItem.setItem(GuiItemInfo.builder()
                .name("BOOK")
                .type(GuiItemType.SYSTEM_BUTTON)
                .displayName("&a" + command)
                .build());

        // Gui actions
        GuiItemAction leftAction = new GuiItemAction();
        GuiItemAction rightAction = new GuiItemAction();
        leftAction.setCommands(List.of(String.format("edit item action %s command", itemActionType.getType())));
        rightAction.setCommands(List.of(String.format("delete item action %s command", itemActionType.getType())));

        guiItem.setActions(Map.of(
                GuiItemActionType.LEFT, leftAction,
                GuiItemActionType.RIGHT, rightAction
        ));
        return guiItem;
    }

}
