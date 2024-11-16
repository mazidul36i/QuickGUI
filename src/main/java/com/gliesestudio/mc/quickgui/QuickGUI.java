package com.gliesestudio.mc.quickgui;

import com.gliesestudio.mc.quickgui.commands.PluginCommands;
import com.gliesestudio.mc.quickgui.completer.EditActionTabCompleter;
import com.gliesestudio.mc.quickgui.completer.OpenGuiTabCompleter;
import com.gliesestudio.mc.quickgui.executor.EditGuiActionExecutor;
import com.gliesestudio.mc.quickgui.executor.OpenGuiCommand;
import com.gliesestudio.mc.quickgui.listener.ChatListener;
import com.gliesestudio.mc.quickgui.listener.GuiListener;
import com.gliesestudio.mc.quickgui.listener.SystemGuiListener;
import com.gliesestudio.mc.quickgui.manager.GuiManager;
import com.gliesestudio.mc.quickgui.manager.SystemGuiManager;
import com.gliesestudio.mc.quickgui.service.EditGuiService;
import com.gliesestudio.mc.quickgui.service.EditGuiServiceImpl;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.logging.Logger;

public final class QuickGUI extends JavaPlugin {

    private final Logger logger = getLogger();

    private GuiManager guiManager;
    private SystemGuiManager systemGuiManager;

    // Listeners
    private ChatListener chatListener;

    // Commands
    private OpenGuiCommand openGuiCommandExecutor;

    // Services
    private EditGuiService editGuiService;

    @Override
    public void onEnable() {
        displayStartingMessage();
        saveDefaultConfig();

        // Initialize gui managers
        guiManager = new GuiManager(this);
        systemGuiManager = new SystemGuiManager(this);

        // Initialize services
        editGuiService = new EditGuiServiceImpl(this, guiManager, systemGuiManager);

        // Initialize commands
        openGuiCommandExecutor = new OpenGuiCommand(guiManager);

        // Initialize listeners
        chatListener = new ChatListener(this, editGuiService);

        registerEvents();
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

    private void registerEvents() {
        // Get the plugin manager
        PluginManager pluginManager = getServer().getPluginManager();

        // Register gui click listener
        pluginManager.registerEvents(new GuiListener(guiManager), this);
        pluginManager.registerEvents(new SystemGuiListener(this, systemGuiManager, chatListener), this);

        // Register the chat listener
        pluginManager.registerEvents(chatListener, this);
    }

    private void registerCommands() {
        logger.info("Registering commands...");

        // Register the OpenGuiCommand
        PluginCommand openGuiCommand = getCommand(PluginCommands.OPEN_GUI);
        if (Objects.nonNull(openGuiCommand)) {
            openGuiCommand.setExecutor(openGuiCommandExecutor);
            openGuiCommand.setTabCompleter(new OpenGuiTabCompleter(guiManager));
        }

        // Register the EditGuiCommand
        PluginCommand editGuiCommand = getCommand(PluginCommands.GUI);
        if (Objects.nonNull(editGuiCommand)) {
            editGuiCommand.setExecutor(new EditGuiActionExecutor(this, guiManager, systemGuiManager));
            editGuiCommand.setTabCompleter(new EditActionTabCompleter(guiManager));
        }

    }

}
