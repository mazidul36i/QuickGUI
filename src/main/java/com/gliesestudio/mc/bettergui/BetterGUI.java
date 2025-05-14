package com.gliesestudio.mc.bettergui;

import com.gliesestudio.mc.bettergui.executor.OpenGuiCommand;
import com.gliesestudio.mc.bettergui.listener.GuiListener;
import com.gliesestudio.mc.bettergui.manager.GuiManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.LoggerFactory;

import java.util.logging.Logger;

public final class BetterGUI extends JavaPlugin {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(BetterGUI.class);
    private final Logger logger = getLogger();

    private GuiManager guiManager;

    @Override
    public void onEnable() {
        displayStartingMessage();

        saveDefaultConfig();
        guiManager = new GuiManager(this);
        getServer().getPluginManager().registerEvents(new GuiListener(guiManager, this), this);
        getCommand("opengui").setExecutor(new OpenGuiCommand(guiManager));
    }

    @Override
    public void onDisable() {
        logger.info("\u001B[33mBetterTPA plugin disabled\u001B[0m");
    }

    private void displayStartingMessage() {
        log.info("Starting BetterGUI plugin...");
        logger.info("\u001B[32m╔═══════════════════════════════╗\u001B[0m");
        logger.info("\u001B[32m║          Better GUI           ║\u001B[0m");
        logger.info("\u001B[32m╚═══════════════════════════════╝\u001B[0m");
    }

    public void reloadPluginConfig() {
        reloadConfig();
        guiManager.reloadConfig(getConfig());
    }

}
