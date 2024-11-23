package com.gliesestudio.mc.quickgui.manager;

import com.gliesestudio.mc.quickgui.QuickGUI;
import com.gliesestudio.mc.quickgui.gui.GUI;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SystemGuiManager {

    private static final Logger log = LoggerFactory.getLogger(SystemGuiManager.class);
    private static final Map<String, GUI> systemGuis = new HashMap<>();

    public static void init(@NotNull QuickGUI plugin) {
        loadGuis(plugin);
    }

    private static void loadGuis(@NotNull QuickGUI plugin) {
        Set<String> systemGuiNames = Set.of("edit-gui", "edit-item", "edit-lores");

        systemGuiNames.forEach(name -> {
            GUI editGui = createGuiFromSystemResource(plugin, name);
            if (editGui != null) {
                systemGuis.put(editGui.getName(), editGui);
            }
        });
    }

    @Nullable
    private static GUI createGuiFromSystemResource(@NotNull QuickGUI plugin, @NotNull String name) {
        // Read file from resources
        String filePath = "guis/system/" + name + ".yml";

        // Load the file from resources
        InputStream stream = plugin.getResource(filePath);
        if (stream == null) {
            log.error("Could not find file in resources: {}", filePath);
            return null;
        }

        // Create the GUI from the input stream
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(new InputStreamReader(stream));
        return GUI.deserialize(configuration);
    }

    @Nullable
    public static GUI getSystemGui(String name) {
        return systemGuis.get(name);
    }

}
