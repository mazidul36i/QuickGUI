package com.gliesestudio.mc.quickgui.gui;

import com.gliesestudio.mc.quickgui.QuickGUI;
import com.gliesestudio.mc.quickgui.gui.item.GuiItem;
import com.gliesestudio.mc.quickgui.utility.CollectionUtils;
import com.gliesestudio.mc.quickgui.utility.PluginUtils;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class GuiHolder implements InventoryHolder {

    protected final QuickGUI plugin;
    protected Inventory inventory;
    protected final Player player;

    @Getter
    protected final GUI gui;
    @Getter
    private final OpenMode mode;
    @Getter
    private final GuiHolder previousGui;

    public GuiHolder(QuickGUI plugin, Player player, GUI gui, OpenMode mode) {
        this.plugin = plugin;
        this.player = player;
        this.gui = gui;
        this.mode = mode;
        this.previousGui = null;
    }

    public GuiHolder(QuickGUI plugin, Player player, GUI gui, OpenMode mode, GuiHolder previousGui) {
        this.plugin = plugin;
        this.player = player;
        this.gui = gui;
        this.mode = mode;
        this.previousGui = previousGui;
    }

    public boolean hasPreviousGui() {
        return previousGui != null && previousGui.getGui() != null;
    }

    @Override
    public @NotNull Inventory getInventory() {
        if (inventory != null) return inventory;
        return createInventory();
    }

    public @NotNull Inventory createInventory() {
        String titleText = mode == OpenMode.EDIT_GUI ? "&5GUI: &r" + gui.getName() : PluginUtils.replacePlaceholders(gui.getTitle(), player);
        TextComponent title = Component.text(PluginUtils.translateColorCodes(titleText));
        int invSize = gui.getRows() * 9;
        this.inventory = plugin.getServer().createInventory(this, invSize, title);

        if (CollectionUtils.isNotEmpty(gui.getItems())) {
            gui.getItems().forEach((slot, guiItem) -> {
                if (slot >= invSize) return;
                ItemStack itemStack = guiItem.createItemStack(player);
                if (itemStack != null) inventory.setItem(slot, itemStack);
            });
        }
        return this.inventory;
    }

    public GuiItem getGuiItem(int slot) {
        return gui.getItems().get(slot);
    }

}
