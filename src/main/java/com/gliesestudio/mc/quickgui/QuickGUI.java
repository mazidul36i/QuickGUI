package com.gliesestudio.mc.quickgui;

import com.gliesestudio.mc.quickgui.commands.PluginCommands;
import com.gliesestudio.mc.quickgui.completer.EditActionTabCompleter;
import com.gliesestudio.mc.quickgui.completer.OpenGuiTabCompleter;
import com.gliesestudio.mc.quickgui.executor.EditGuiActionExecutor;
import com.gliesestudio.mc.quickgui.executor.OpenGuiCommand;
import com.gliesestudio.mc.quickgui.listener.GuiListener;
import com.gliesestudio.mc.quickgui.manager.GuiManager;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.logging.Logger;

public final class QuickGUI extends JavaPlugin {

    private final Logger logger = getLogger();

    private GuiManager guiManager;

    @Override
    public void onEnable() {
        displayStartingMessage();

        saveDefaultConfig();
        guiManager = new GuiManager(this);
        getServer().getPluginManager().registerEvents(new GuiListener(guiManager, this), this);

        registerCommands();
    }

    @Override
    public void onDisable() {
        logger.info("\u001B[33mQuickGUI plugin disabled\u001B[0m");
    }

    private void displayStartingMessage() {
        logger.info("\u001B[32m╔═══════════════════════════════╗\u001B[0m");
        logger.info("\u001B[32m║           Quick GUI           ║\u001B[0m");
        logger.info("\u001B[32m╚═══════════════════════════════╝\u001B[0m");
    }

    private void registerCommands() {
        logger.info("Registering commands...");

        // Register the OpenGuiCommand
        PluginCommand openGuiCommand = getCommand(PluginCommands.OPEN_GUI);
        if (Objects.nonNull(openGuiCommand)) {
            openGuiCommand.setExecutor(new OpenGuiCommand(guiManager));
            openGuiCommand.setTabCompleter(new OpenGuiTabCompleter(guiManager));
        }

        // Register the EditGuiCommand
        PluginCommand editGuiCommand = getCommand(PluginCommands.EDIT_GUI);
        if (Objects.nonNull(editGuiCommand)) {
            editGuiCommand.setExecutor(new EditGuiActionExecutor(this, guiManager));
            editGuiCommand.setTabCompleter(new EditActionTabCompleter(guiManager));
        }

    }
}
