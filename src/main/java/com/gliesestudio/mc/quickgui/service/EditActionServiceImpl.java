package com.gliesestudio.mc.quickgui.service;

import com.gliesestudio.mc.quickgui.QuickGUI;
import com.gliesestudio.mc.quickgui.enums.PlayerHead;
import com.gliesestudio.mc.quickgui.enums.SystemCommand;
import com.gliesestudio.mc.quickgui.gui.GUI;
import com.gliesestudio.mc.quickgui.gui.OpenMode;
import com.gliesestudio.mc.quickgui.gui.SystemGuiHolder;
import com.gliesestudio.mc.quickgui.gui.item.*;
import com.gliesestudio.mc.quickgui.utility.Constants;
import com.gliesestudio.mc.quickgui.utility.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
        GuiItemActionType itemActionType = switch (actionCommand) {
            case EDIT_ITEM_ACTION_LEFT -> GuiItemActionType.LEFT;
            case EDIT_ITEM_ACTION_SHIFT_LEFT -> GuiItemActionType.SHIFT_LEFT;
            case EDIT_ITEM_ACTION_MIDDLE -> GuiItemActionType.MIDDLE;
            case EDIT_ITEM_ACTION_RIGHT -> GuiItemActionType.RIGHT;
            case EDIT_ITEM_ACTION_SHIFT_RIGHT -> GuiItemActionType.SHIFT_RIGHT;
            default -> null;
        };
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

    private GUI createActionGui(GuiItem editItem, GuiItemActionType itemActionType) {
        GUI actionGui = new GUI();
        actionGui.setName("edit-actions");
        actionGui.setTitle("&9Edit Actions");
        actionGui.setPermission("quickqui.edit");
        actionGui.setRows(5);
        actionGui.setItems(createStaticActionGuiItems(editItem.getAction(itemActionType)));
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
        Set<Integer> fillerSlots = Set.of(0, 1, 2, 3, 4, 6, 7, 8, 9, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 35, 36, 37, 38, 39, 40, 41, 42, 43);
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
        guiItemMap.put(5, changeCurrency);

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

        GuiItem addCommand = new GuiItem();
        addCommand.setItem(GuiItemInfo.builder()
                .name(Material.PLAYER_HEAD.name())
                .displayName("&aAdd Command")
                .texture(PlayerHead.PUMPKIN_PLUS.name())
                .type(GuiItemType.SYSTEM_BUTTON)
                .hideTooltip(false)
                .build());
        guiItemMap.put(29, addCommand);

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

}
