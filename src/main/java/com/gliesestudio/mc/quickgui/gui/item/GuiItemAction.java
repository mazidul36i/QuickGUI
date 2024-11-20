package com.gliesestudio.mc.quickgui.gui.item;

import com.gliesestudio.mc.quickgui.gui.command.GuiCommandExecutor;
import com.gliesestudio.mc.quickgui.utility.CollectionUtils;
import lombok.Data;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class GuiItemAction implements Serializable {

    @Serial
    private static final long serialVersionUID = 4104076159673692490L;

    private List<String> commands;
    private String permission;
    private boolean closeInv;
    private GuiCommandExecutor executor;

    public boolean hasCommands() {
        return CollectionUtils.isNotEmpty(commands);
    }

    public boolean hasPermission() {
        return permission != null && !permission.isEmpty();
    }

    public static GuiItemAction deserialize(@NotNull ConfigurationSection actionConfig) {
        GuiItemAction action = new GuiItemAction();
        action.setCommands(actionConfig.getStringList("commands"));
        action.setPermission(actionConfig.getString("permission"));
        action.setCloseInv(actionConfig.getBoolean("close-inv"));
        GuiCommandExecutor executor = GuiCommandExecutor.fromString(actionConfig.getString("executor"));
        action.setExecutor(executor != null ? executor : GuiCommandExecutor.SERVER);
        return action;
    }

    public Map<String, Object> serialize() {
        Map<String, Object> actionConfig = new HashMap<>();
        actionConfig.put("commands", commands);
        actionConfig.put("permission", permission);
        actionConfig.put("close-inv", closeInv);
        actionConfig.put("executor", executor != null ? executor.getExecutor() : GuiCommandExecutor.SERVER);
        return actionConfig;
    }

}
