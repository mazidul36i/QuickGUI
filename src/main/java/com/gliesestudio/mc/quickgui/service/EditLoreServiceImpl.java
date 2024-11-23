package com.gliesestudio.mc.quickgui.service;

import com.gliesestudio.mc.quickgui.QuickGUI;
import com.gliesestudio.mc.quickgui.enums.AwaitingInputType;
import com.gliesestudio.mc.quickgui.enums.PlayerHead;
import com.gliesestudio.mc.quickgui.gui.GUI;
import com.gliesestudio.mc.quickgui.gui.OpenMode;
import com.gliesestudio.mc.quickgui.gui.SystemGuiHolder;
import com.gliesestudio.mc.quickgui.gui.item.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditLoreServiceImpl implements EditLoreService {

    private static final Logger log = LoggerFactory.getLogger(EditLoreServiceImpl.class);
    private final QuickGUI plugin;
    private final File guiFolder;

    public EditLoreServiceImpl(QuickGUI plugin) {
        this.plugin = plugin;
        this.guiFolder = new File(plugin.getDataFolder(), "guis");
    }

    // TODO: Make the lores gui dynamic
    @Override
    public void openEditLoreGui(@NotNull Player player, SystemGuiHolder systemGuiHolder) {
        // Create the GUI from system resources
        GUI editLoresGui = createLoreGui(systemGuiHolder);

        // Open the GUI
        SystemGuiHolder editLoresGuiHolder = new SystemGuiHolder(plugin, player, editLoresGui, systemGuiHolder.getGui(),
                OpenMode.EDIT_LORES, systemGuiHolder.getEditItemSlot(), systemGuiHolder);
        player.openInventory(editLoresGuiHolder.getInventory());
    }

    @Override
    public boolean editItemLoreConfig(@NotNull SystemGuiHolder systemGuiHolder, AwaitingInputType inputType, String lore, Integer editLorePosition) {
        GUI gui = systemGuiHolder.getGui();
        if (gui == null) return false;
        GuiItem guiItem = gui.getItem(systemGuiHolder.getEditItemSlot());
        GuiItemInfo itemInfo = guiItem.getItem();
        if (itemInfo.getLore() == null) itemInfo.setLore(new ArrayList<>());

        if (AwaitingInputType.INPUT_ADD_ITEM_LORE.equals(inputType)) {
            itemInfo.getLore().add(lore);
        } else if (AwaitingInputType.INPUT_EDIT_ITEM_LORE.equals(inputType)) {
            if (editLorePosition == null || editLorePosition < 0 || editLorePosition >= itemInfo.getLore().size())
                return false;
            itemInfo.getLore().set(editLorePosition, lore);
        }

        File guiConfigFile = new File(guiFolder, systemGuiHolder.getGui().getName() + ".yml");
        FileConfiguration guiConfig = gui.serialize();

        try {
            guiConfig.save(guiConfigFile);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public void deleteItemLoreConfig(Player player, SystemGuiHolder systemGuiHolder, int deleteLorePosition) {
        GUI gui = systemGuiHolder.getGui();
        if (gui == null) return;
        GuiItem guiItem = gui.getItem(systemGuiHolder.getEditItemSlot());
        GuiItemInfo itemInfo = guiItem.getItem();
        if (itemInfo.getLore() == null) itemInfo.setLore(new ArrayList<>());

        // Remove the lore
        if (deleteLorePosition < 0 || deleteLorePosition >= itemInfo.getLore().size()) return;
        itemInfo.getLore().remove(deleteLorePosition);

        try {
            File guiConfigFile = new File(guiFolder, systemGuiHolder.getGui().getName() + ".yml");
            FileConfiguration guiConfig = gui.serialize();
            guiConfig.save(guiConfigFile);
        } catch (IOException ignored) {
        }
    }

    private @NotNull GUI createLoreGui(@NotNull SystemGuiHolder systemGuiHolder) {
        GuiItem guiItem = systemGuiHolder.getGui().getItem(systemGuiHolder.getEditItemSlot());
        List<String> lores = guiItem.getItem().getLore();
        if (lores == null) lores = new ArrayList<>();
        int rowSize = lores.size() <= 5 ? 1 :
                lores.size() <= 14 ? 2 :
                        lores.size() <= 23 ? 3 :
                                lores.size() <= 31 ? 4 : 5;

        // Create gui object for the edit lore.
        GUI editLoreGui = new GUI();
        editLoreGui.setName("edit-lores");
        editLoreGui.setTitle("&9Edit Lores");
        editLoreGui.setRows(rowSize);

        // System gui buttons
        Map<Integer, GuiItem> guiItems = new HashMap<>();
        guiItems.put(0, guiItem);
        guiItems.put(1, getInfoBookGuiItem());
        guiItems.put(2 + lores.size(), getAddLoreGuiItem());
        guiItems.put(rowSize * 9 - 1, getBackGuiItem());

        // Lore items
        for (int i = 0; i < lores.size(); i++) {
            guiItems.put(2 + i, createLoreGuiItem(lores.get(i)));
        }

        editLoreGui.setItems(guiItems);
        return editLoreGui;
    }

    private static @NotNull GuiItem getInfoBookGuiItem() {
        GuiItem guiItem = new GuiItem();
        GuiItemInfo itemInfo = new GuiItemInfo();
        itemInfo.setName("WRITABLE_BOOK");
        itemInfo.setType(GuiItemType.SYSTEM_BUTTON);
        itemInfo.setDisplayName("&6INFO");
        itemInfo.setLore(List.of("&bLeft click",
                "&3Edit",
                "",
                "&bRight click",
                "&3Delete",
                "",
                "&7Use color codes to customize lore",
                "&f<EMPTY>&7: Add an empty line to the lore"
        ));
        guiItem.setItem(itemInfo);
        return guiItem;
    }

    private static @NotNull GuiItem getAddLoreGuiItem() {
        GuiItem guiItem = new GuiItem();
        GuiItemInfo itemInfo = new GuiItemInfo();
        itemInfo.setName("PLAYER_HEAD");
        itemInfo.setType(GuiItemType.SYSTEM_BUTTON);
        itemInfo.setDisplayName("&aAdd Lore");
        itemInfo.setLore(List.of("&3Click to add a new lore"));
        itemInfo.setTexture(PlayerHead.PUMPKIN_PLUS.name());
        guiItem.setItem(itemInfo);

        // Gui actions
        GuiItemAction leftAction = new GuiItemAction();
        leftAction.setCommands(List.of("add item lore"));
        guiItem.setActions(Map.of(GuiItemActionType.LEFT, leftAction));
        return guiItem;
    }

    private static @NotNull GuiItem getBackGuiItem() {
        GuiItem guiItem = new GuiItem();
        GuiItemInfo itemInfo = new GuiItemInfo();
        itemInfo.setName("PLAYER_HEAD");
        itemInfo.setType(GuiItemType.SYSTEM_BUTTON);
        itemInfo.setDisplayName("&cBack");
        itemInfo.setTexture(PlayerHead.BACK.name());
        guiItem.setItem(itemInfo);

        // Gui actions
        GuiItemAction leftAction = new GuiItemAction();
        leftAction.setCommands(List.of("back"));
        guiItem.setActions(Map.of(GuiItemActionType.LEFT, leftAction));
        return guiItem;
    }

    private static @NotNull GuiItem createLoreGuiItem(String lore) {
        GuiItem guiItem = new GuiItem();
        GuiItemInfo itemInfo = new GuiItemInfo();
        itemInfo.setName("BOOK");
        itemInfo.setType(GuiItemType.SYSTEM_BUTTON);
        itemInfo.setDisplayName(lore == null || lore.isEmpty() ? "<EMPTY>" : lore);
        guiItem.setItem(itemInfo);

        // Gui actions
        GuiItemAction leftAction = new GuiItemAction();
        GuiItemAction rightAction = new GuiItemAction();
        leftAction.setCommands(List.of("edit item lore"));
        rightAction.setCommands(List.of("delete item lore"));

        guiItem.setActions(Map.of(
                GuiItemActionType.LEFT, leftAction,
                GuiItemActionType.RIGHT, rightAction
        ));
        return guiItem;
    }

}
