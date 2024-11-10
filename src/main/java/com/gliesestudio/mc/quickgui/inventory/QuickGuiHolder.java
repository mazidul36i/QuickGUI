package com.gliesestudio.mc.quickgui.inventory;

import com.gliesestudio.mc.quickgui.QuickGUI;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class QuickGuiHolder implements InventoryHolder {

    private final Inventory inventory;

    public QuickGuiHolder(QuickGUI plugin) {
        // Create an Inventory with 9 slots, `this` here is our InventoryHolder.
        this.inventory = plugin.getServer().createInventory(this, 9);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return this.inventory;
    }

}
