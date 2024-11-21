package com.gliesestudio.mc.quickgui.service;

import com.gliesestudio.mc.quickgui.gui.GuiHolder;
import com.gliesestudio.mc.quickgui.gui.SystemGuiHolder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface EditGuiService {

    void createGui(@NotNull CommandSender sender, String name, int rows);

    void reloadGui(String name);

    void reloadGuis();

    boolean editGui(@NotNull Player player, String name);

    boolean editGuiName(String name, String newName);

    boolean editGuiTitle(String name, String newTitle);

    boolean editGuiRows(String name, int newRows);

    boolean editGuiPermission(String name, String newPermission);

    boolean editGuiAlias(String name, String newAlias);

    void openGuiEditItem(Player player, SystemGuiHolder systemGuiHolder);

    void editGuiItem(Player player, GuiHolder holder, ItemStack itemStack, int slot);
}
