package com.gliesestudio.mc.quickgui.service;

import com.gliesestudio.mc.quickgui.QuickGUI;
import com.gliesestudio.mc.quickgui.enums.PlayerHead;
import com.gliesestudio.mc.quickgui.gui.GUI;
import com.gliesestudio.mc.quickgui.gui.OpenMode;
import com.gliesestudio.mc.quickgui.gui.SystemGuiHolder;
import com.gliesestudio.mc.quickgui.gui.item.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditLoreServiceImpl implements EditLoreService {

    private final QuickGUI plugin;

    public EditLoreServiceImpl(QuickGUI plugin) {
        this.plugin = plugin;
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

    private GUI createLoreGui(SystemGuiHolder systemGuiHolder) {
        GuiItem guiItem = systemGuiHolder.getGui().getItem(systemGuiHolder.getEditItemSlot());
        List<String> lores = guiItem.getItem().getLore();
        if (lores == null) lores = new ArrayList<>();
        int rowSize = lores.size() <= 5 ? 1 :
                lores.size() <= 13 ? 2 :
                        lores.size() <= 21 ? 3 :
                                lores.size() <= 29 ? 4 : 5;

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
        leftAction.setCommands(List.of("add lore"));
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
        return guiItem;
    }

}
