package com.gliesestudio.mc.quickgui.executor;

import com.gliesestudio.mc.quickgui.QuickGUI;
import com.gliesestudio.mc.quickgui.commands.PluginCommands;
import com.gliesestudio.mc.quickgui.manager.GuiManager;
import com.gliesestudio.mc.quickgui.service.EditGuiService;
import com.gliesestudio.mc.quickgui.service.EditGuiServiceImpl;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class EditGuiActionExecutor implements CommandExecutor {

    private final QuickGUI plugin;
    private final GuiManager guiManager;
    private final EditGuiService editGuiService;

    public EditGuiActionExecutor(QuickGUI plugin, GuiManager guiManager) {
        this.plugin = plugin;
        this.guiManager = guiManager;
        editGuiService = new EditGuiServiceImpl(plugin, guiManager);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        // Check if the sender has provided an action or not.
        if (args.length == 0) {
            sender.sendMessage("Please specify the name of the command to execute.");
            return false;
        }

        // Get the GUI name from the command arguments
        String actionString = args[0];
        PluginCommands.Action action = PluginCommands.Action.fromString(actionString);
        if (action == null) {
            sender.sendMessage("§cInvalid action: " + actionString);
            return false;
        }

        // Execute the 'reload' action
        if (PluginCommands.Action.RELOAD.equals(action)) {
            reloadPluginConfig(sender);
            return true;
        }

        // Execute the 'create' action
        if (PluginCommands.Action.CREATE.equals(action)) {
            return createGui(sender, args);
        }

        // Execute the 'edit' action
        if (PluginCommands.Action.EDIT.equals(action)) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("§cOnly players can use this command.");
                return true;
            }
            return editGui(player, args);
        }

        // Make more actions as needed...

        return false;
    }

    private void reloadPluginConfig(@NotNull CommandSender sender) {
        plugin.reloadConfig();
        sender.sendMessage("§aReloaded plugin configs.");
        int guiSize = guiManager.reloadGuis();
        if (guiSize == 0)
            sender.sendMessage("§6No GUI configured to load! Create your first GUI using /editgui add <name>.");
        else
            sender.sendMessage("§aLoaded " + guiSize + " GUI" + (guiSize == 1 ? "" : "s") + ".");
    }

    private boolean createGui(@NotNull CommandSender sender, String @NotNull [] args) {
        if (args.length <= 1) {
            sender.sendMessage("§cPlease specify the name of the GUI to create.");
            return false;
        }
        String name = args[1];
        int rows = 3;

        // Validate rows input
        if (args.length >= 3) {
            try {
                rows = Integer.parseInt(args[2]);
                if (rows < 1 || rows > 6) {
                    sender.sendMessage("§cRow count must be between 1 and 6.");
                    return false;
                }
            } catch (NumberFormatException e) {
                sender.sendMessage("§cInvalid row count. Please provide a number between 1 and 6.");
                return false;
            }
        }

        // Create gui with the name of rows.
        return editGuiService.createGui(sender, name, rows);
    }

    private boolean editGui(@NotNull Player player, String @NotNull [] args) {
        if (args.length <= 1) {
            player.sendMessage("§cPlease specify the name of the GUI to edit.");
            return false;
        }
        String name = args[1];

        return editGuiService.editGui(player, name);
    }

}
