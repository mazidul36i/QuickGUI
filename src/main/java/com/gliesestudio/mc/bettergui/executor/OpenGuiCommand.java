package com.gliesestudio.mc.bettergui.executor;

import com.gliesestudio.mc.bettergui.manager.GuiManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public class OpenGuiCommand implements CommandExecutor {
    private final GuiManager guiManager;

    public OpenGuiCommand(GuiManager guiManager) {
        this.guiManager = guiManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        // Check if the sender is a player
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        // Check if the player provided a GUI name
        if (args.length == 0) {
            player.sendMessage("Please specify the name of the GUI to open.");
            return false;
        }

        // Get the GUI name from the command arguments
        String guiName = args[0];

        // Retrieve the GUI from the GuiManager
        Inventory gui = guiManager.getGui(guiName);
        if (gui == null) {
            player.sendMessage("GUI with the name '" + guiName + "' does not exist.");
            return true;
        }

        // Open the GUI for the player
        player.openInventory(gui);
        return true;
    }
}
