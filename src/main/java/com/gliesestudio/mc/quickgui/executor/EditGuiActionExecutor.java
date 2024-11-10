package com.gliesestudio.mc.quickgui.executor;

import com.gliesestudio.mc.quickgui.QuickGUI;
import com.gliesestudio.mc.quickgui.commands.PluginCommands;
import com.gliesestudio.mc.quickgui.manager.GuiManager;
import com.gliesestudio.mc.quickgui.utility.PluginUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class EditGuiActionExecutor implements CommandExecutor {

    private final QuickGUI plugin;
    private final GuiManager guiManager;

    public EditGuiActionExecutor(QuickGUI plugin, GuiManager guiManager) {
        this.plugin = plugin;
        this.guiManager = guiManager;
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
            sender.sendMessage(PluginUtils.translateColorCodes("&cInvalid action: " + actionString));
            return false;
        }

        // Execute the reload action
        if (PluginCommands.Action.RELOAD.equals(action)) {
            reloadPluginConfig(sender);
            return true;
        }

        // Make more actions as needed...

        return false;
    }

    public void reloadPluginConfig(@NotNull CommandSender sender) {
        plugin.reloadConfig();
        sender.sendMessage("§aReloaded plugin configs.");
        int guiSize = guiManager.reloadGuis();
        if (guiSize == 0)
            sender.sendMessage("§6No GUI configured to load! Create your first GUI using /editgui add <name>.");
        else
            sender.sendMessage("§aLoaded " + guiSize + " GUI" + (guiSize == 1 ? "" : "s") + ".");
    }

}
