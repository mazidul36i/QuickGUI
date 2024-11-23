package com.gliesestudio.mc.quickgui;

import com.gliesestudio.mc.quickgui.commands.PluginCommands;
import com.gliesestudio.mc.quickgui.completer.EditActionTabCompleter;
import com.gliesestudio.mc.quickgui.completer.OpenGuiTabCompleter;
import com.gliesestudio.mc.quickgui.executor.EditGuiActionExecutor;
import com.gliesestudio.mc.quickgui.executor.OpenGuiCommand;
import com.gliesestudio.mc.quickgui.gui.GuiManager;
import com.gliesestudio.mc.quickgui.listener.ChatListener;
import com.gliesestudio.mc.quickgui.listener.GuiListener;
import com.gliesestudio.mc.quickgui.listener.SystemGuiListener;
import com.gliesestudio.mc.quickgui.manager.SystemGuiManager;
import com.gliesestudio.mc.quickgui.service.*;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.logging.Logger;

public final class QuickGUI extends JavaPlugin {

    private final Logger logger = getLogger();

    // Listeners
    private ChatListener chatListener;

    // Commands
    private OpenGuiCommand openGuiCommandExecutor;

    // Services
    private EditGuiService editGuiService;
    private EditItemService editItemService;
    private EditLoreService editLoreService;

    @Override
    public void onEnable() {
        displayStartingMessage();
        saveDefaultConfig();

        // Initialize gui managers
        GuiManager.init(this);
        SystemGuiManager.init(this);

        // Initialize services
        editGuiService = new EditGuiServiceImpl(this);
        editItemService = new EditItemServiceImpl(this);
        editLoreService = new EditLoreServiceImpl(this);

        // Initialize commands
        openGuiCommandExecutor = new OpenGuiCommand(this);

        // Initialize listeners
        chatListener = new ChatListener(this);

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
        pluginManager.registerEvents(new GuiListener(this), this);
        pluginManager.registerEvents(new SystemGuiListener(this, chatListener), this);

        // Register the chat listener
        pluginManager.registerEvents(chatListener, this);
    }

    private void registerCommands() {
        logger.info("Registering commands...");

        // Register the OpenGuiCommand
        PluginCommand openGuiCommand = getCommand(PluginCommands.OPEN_GUI);
        if (Objects.nonNull(openGuiCommand)) {
            openGuiCommand.setExecutor(openGuiCommandExecutor);
            openGuiCommand.setTabCompleter(new OpenGuiTabCompleter());
        }

        // Register the EditGuiCommand
        PluginCommand editGuiCommand = getCommand(PluginCommands.GUI);
        if (Objects.nonNull(editGuiCommand)) {
            editGuiCommand.setExecutor(new EditGuiActionExecutor(this, editGuiService));
            editGuiCommand.setTabCompleter(new EditActionTabCompleter());
        }

    }

    public @NotNull EditGuiService getEditGuiService() {
        if (editGuiService == null) editGuiService = new EditGuiServiceImpl(this);
        return editGuiService;
    }

    public @NotNull EditItemService getEditItemService() {
        if (editItemService == null) editItemService = new EditItemServiceImpl(this);
        return editItemService;
    }

    public @NotNull EditLoreService getEditLoreService() {
        if (editLoreService == null) editLoreService = new EditLoreServiceImpl(this);
        return editLoreService;
    }

}
