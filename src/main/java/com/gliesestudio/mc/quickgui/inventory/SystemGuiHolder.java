package com.gliesestudio.mc.quickgui.inventory;

import com.gliesestudio.mc.quickgui.QuickGUI;
import com.gliesestudio.mc.quickgui.commands.PluginCommands;
import com.gliesestudio.mc.quickgui.gui.GuiHolder;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class SystemGuiHolder implements InventoryHolder {

    private final Inventory systemInventory;
    private final PluginCommands.Action action;
    private final String name;
    private final GuiHolder editGuiHolder;

    public SystemGuiHolder(QuickGUI plugin, int rows, Component title, String name, GuiHolder editGuiHolder,
                           PluginCommands.Action action) {
        // Create an Inventory with 9 slots, `this` here is our InventoryHolder.
        this.systemInventory = plugin.getServer().createInventory(this, rows * 9, title);
        this.name = name;
        this.editGuiHolder = editGuiHolder;
        this.action = action;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return this.systemInventory;
    }

    public PluginCommands.Action getAction() {
        return action;
    }

    public String getName() {
        return name;
    }

    public GuiHolder getEditGuiHolder() {
        return editGuiHolder;
    }

}
