package com.gliesestudio.mc.quickgui.service;

import com.gliesestudio.mc.quickgui.enums.AwaitingInputType;
import com.gliesestudio.mc.quickgui.gui.GuiHolder;
import com.gliesestudio.mc.quickgui.gui.SystemGuiHolder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface EditItemService {

    void openEditItemGui(@NotNull Player player, GuiHolder guiHolder, int itemSlot);

    void changeItem(@NotNull Player player, SystemGuiHolder systemGuiHolder);

    boolean updateItemConfig(SystemGuiHolder systemGuiHolder, AwaitingInputType inputType, String newValue);

    void toggleItemGlow(Player player, SystemGuiHolder systemGuiHolder);

}
