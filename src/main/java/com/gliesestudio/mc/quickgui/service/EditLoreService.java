package com.gliesestudio.mc.quickgui.service;

import com.gliesestudio.mc.quickgui.gui.SystemGuiHolder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface EditLoreService {

    void openEditLoreGui(@NotNull Player player, SystemGuiHolder systemGuiHolder);

}
