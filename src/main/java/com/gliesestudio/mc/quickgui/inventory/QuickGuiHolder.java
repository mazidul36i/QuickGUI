package com.gliesestudio.mc.quickgui.inventory;

import com.gliesestudio.mc.quickgui.QuickGUI;
import com.gliesestudio.mc.quickgui.commands.PluginCommands;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class QuickGuiHolder implements InventoryHolder {

    private final Inventory inventory;
    private final PluginCommands.Action action;
    private final String name;
    private final TextComponent title;
    private final String alias;
    private final String permission;

    public QuickGuiHolder(QuickGUI plugin, int rows, TextComponent title, String name, PluginCommands.Action action) {
        // Create an Inventory with 9 slots, `this` here is our InventoryHolder.
        this.inventory = plugin.getServer().createInventory(this, rows * 9, title);
        this.name = name;
        this.title = title;
        this.action = action;
        this.alias = null;
        this.permission = null;
    }

    public QuickGuiHolder(QuickGUI plugin, int rows, TextComponent title, String name, PluginCommands.Action action,
                          String alias, String permission) {
        // Create an Inventory with 9 slots, `this` here is our InventoryHolder.
        this.inventory = plugin.getServer().createInventory(this, rows * 9, title);
        this.name = name;
        this.title = title;
        this.action = action;
        this.alias = alias;
        this.permission = permission;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return this.inventory;
    }

    public PluginCommands.Action getAction() {
        return action;
    }

    public String getName() {
        return name;
    }

    public TextComponent getTitle() {
        return title;
    }

    public String getAlias() {
        return alias;
    }

    public String getPermission() {
        return permission;
    }

}
