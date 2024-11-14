package com.gliesestudio.mc.quickgui.service;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface EditGuiService {

    boolean createGui(@NotNull CommandSender sender, String name, int rows);

    boolean editGui(@NotNull Player player, String name);

}
