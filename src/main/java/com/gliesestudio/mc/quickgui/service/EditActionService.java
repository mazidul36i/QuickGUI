package com.gliesestudio.mc.quickgui.service;

import com.gliesestudio.mc.quickgui.enums.SystemCommand;
import com.gliesestudio.mc.quickgui.gui.SystemGuiHolder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface EditActionService {

    void openEditActionGui(@NotNull Player player, SystemGuiHolder systemGuiHolder, SystemCommand actionCommand);

}
