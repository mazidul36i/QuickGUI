package com.gliesestudio.mc.quickgui.service;

import com.gliesestudio.mc.quickgui.enums.AwaitingInputType;
import com.gliesestudio.mc.quickgui.gui.SystemGuiHolder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface EditLoreService {

    void openEditLoreGui(@NotNull Player player, SystemGuiHolder systemGuiHolder);

    boolean editItemLoreConfig(SystemGuiHolder systemGuiHolder, AwaitingInputType inputType, String lore, Integer editLorePosition);

    void deleteItemLoreConfig(Player player, SystemGuiHolder systemGuiHolder, int deleteLorePosition);

}
