package com.gliesestudio.mc.quickgui.gui;

import com.gliesestudio.mc.quickgui.QuickGUI;
import com.gliesestudio.mc.quickgui.gui.item.GuiItem;
import com.gliesestudio.mc.quickgui.placeholder.SystemPlaceholder;
import com.gliesestudio.mc.quickgui.utility.CollectionUtils;
import com.gliesestudio.mc.quickgui.utility.PluginUtils;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class SystemGuiHolder extends GuiHolder {

    private static final Logger log = LoggerFactory.getLogger(SystemGuiHolder.class);

    private final GUI systemGui;
    @Getter
    private final SystemGuiHolder prevSystemGui;
    @Getter
    private final Integer editItemSlot;

    public SystemGuiHolder(QuickGUI plugin, Player player, GUI systemGui, GUI gui, OpenMode mode) {
        super(plugin, player, gui, mode);
        this.systemGui = systemGui;
        this.prevSystemGui = null;
        this.editItemSlot = null;
    }

    public SystemGuiHolder(QuickGUI plugin, Player player, GUI systemGui, GUI gui, OpenMode mode, Integer editItemSlot) {
        super(plugin, player, gui, mode);
        this.systemGui = systemGui;
        this.prevSystemGui = null;
        this.editItemSlot = editItemSlot;
    }

    public SystemGuiHolder(QuickGUI plugin, Player player, GUI systemGui, GUI gui, OpenMode mode, Integer editItemSlot, SystemGuiHolder prevSystemGui) {
        super(plugin, player, gui, mode);
        this.systemGui = systemGui;
        this.editItemSlot = editItemSlot;
        this.prevSystemGui = prevSystemGui;
    }

    public boolean hasPrevSystemGui() {
        return this.prevSystemGui != null && this.prevSystemGui.getGui() != null;
    }

    @Override
    public @NotNull Inventory getInventory() {
        if (super.inventory != null) return super.inventory;
        return this.createInventory();
    }

    public @NotNull Inventory createInventory() {
        String titleText;
        if (OpenMode.EDIT_ITEMS.equals(super.getMode())) titleText = systemGui.getTitle();
        else titleText = "&5GUI: &r" + super.gui.getName();

        TextComponent title = Component.text(PluginUtils.translateColorCodes(titleText));
        int invSize = systemGui.getRows() * 9;
        super.inventory = super.plugin.getServer().createInventory(this, invSize, title);

        // Placeholders
        Map<String, String> placeholders = Map.of(
                SystemPlaceholder.GUI_NAME, gui.getName(),
                SystemPlaceholder.GUI_TITLE, gui.getTitle(),
                SystemPlaceholder.GUI_ROWS, String.valueOf(gui.getRows()),
                SystemPlaceholder.GUI_OPEN_PERMISSION, gui.hasPermission() ? gui.getPermission() : "NONE",
                SystemPlaceholder.GUI_ALIAS, gui.hasAlias() ? gui.getAlias() : "NONE"
        );

        if (CollectionUtils.isNotEmpty(systemGui.getItems())) {
            systemGui.getItems().forEach((slot, guiItem) -> {
                if (slot >= invSize) return;
                ItemStack itemStack = guiItem.createItemStack(super.player, placeholders);
                if (itemStack != null) super.inventory.setItem(slot, itemStack);
            });
        }

        // Add clicked item into the edit slot while editing an item.
        if ((OpenMode.EDIT_ITEMS.equals(super.getMode()) || OpenMode.EDIT_LORES.equals(super.getMode()))
                && editItemSlot != null && editItemSlot < invSize) {
            GuiItem guiItem = super.gui.getItem(editItemSlot);
            ItemStack itemStack = guiItem.createItemStack(super.player, placeholders);
            if (itemStack != null) {
                if (OpenMode.EDIT_ITEMS.equals(super.getMode())) super.inventory.setItem(4, itemStack);
                if (OpenMode.EDIT_LORES.equals(super.getMode())) super.inventory.setItem(0, itemStack);
            }
        }

        return super.inventory;
    }

    public GuiItem getSystemGuiItem(int slot) {
        return systemGui.getItems().get(slot);
    }

    public @NotNull Inventory getGuiInventory() {
        GuiHolder guiHolder = new GuiHolder(super.plugin, super.player, super.gui, OpenMode.EDIT_GUI);
        return guiHolder.getInventory();
    }

}
