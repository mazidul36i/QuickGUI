package com.gliesestudio.mc.quickgui.inventory;

import com.gliesestudio.mc.quickgui.QuickGUI;
import com.gliesestudio.mc.quickgui.commands.PluginCommands;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class QuickGuiHolder implements InventoryHolder {

    private final Inventory inventory;
    private final PluginCommands.Action action;

    public QuickGuiHolder(QuickGUI plugin, PluginCommands.Action action) {
        // Create an Inventory with 9 slots, `this` here is our InventoryHolder.
        this.inventory = plugin.getServer().createInventory(this, 9);
        this.action = action;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return this.inventory;
    }

    public PluginCommands.Action getAction() {
        return action;
    }

}
