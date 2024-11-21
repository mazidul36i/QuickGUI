package com.gliesestudio.mc.quickgui.service;

import com.gliesestudio.mc.quickgui.gui.GuiHolder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface EditItemService {

    void openEditItemGui(@NotNull Player player, GuiHolder guiHolder, int itemSlot);

}
