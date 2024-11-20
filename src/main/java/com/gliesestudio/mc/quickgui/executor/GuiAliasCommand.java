package com.gliesestudio.mc.quickgui.executor;

import com.gliesestudio.mc.quickgui.QuickGUI;
import com.gliesestudio.mc.quickgui.gui.GUI;
import com.gliesestudio.mc.quickgui.gui.GuiHolder;
import com.gliesestudio.mc.quickgui.gui.GuiManager;
import com.gliesestudio.mc.quickgui.gui.OpenMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GuiAliasCommand extends Command {

    private final QuickGUI plugin;
    private final String guiName;

    public GuiAliasCommand(QuickGUI plugin, String name, String guiName, String permission) {
        super(name);
        this.plugin = plugin;
        this.guiName = guiName;
        this.setAliases(List.of(name));
        this.setPermission(permission);
        this.setDescription("Opens a GUI with the specified name.");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can open this command.");
            return true;
        }
        if (this.getPermission() != null && !(player.hasPermission(this.getPermission()))) {
            player.sendMessage("§cYou do not have permission to open this GUI.");
            return true;
        }

        // Retrieve the GUI from the GuiManager
        GUI gui = GuiManager.guis.get(guiName);
        if (gui == null) {
            player.sendMessage("GUI with the name '" + guiName + "' does not exist.");
            return true;
        }
        // Open the GUI for the player
        GuiHolder guiHolder = new GuiHolder(plugin, player, gui, OpenMode.VIEW);
        player.openInventory(guiHolder.getInventory());
        return true;
    }
}