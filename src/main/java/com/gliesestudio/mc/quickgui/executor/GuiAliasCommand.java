package com.gliesestudio.mc.quickgui.executor;

import com.gliesestudio.mc.quickgui.inventory.QuickGuiHolder;
import com.gliesestudio.mc.quickgui.manager.GuiManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GuiAliasCommand extends Command {

    private final String guiName;
    private final GuiManager guiManager;

    public GuiAliasCommand(GuiManager guiManager, String name, String guiName, String permission) {
        super(name);
        this.guiName = guiName;
        this.setAliases(List.of(name));
        this.guiManager = guiManager;
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
        QuickGuiHolder guiHolder = guiManager.getGui(guiName);
        if (guiHolder == null) {
            player.sendMessage("GUI with the name '" + guiName + "' does not exist.");
            return true;
        }
        // Open the GUI for the player
        player.openInventory(guiHolder.getInventory());
        return true;
    }
}