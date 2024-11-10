package com.gliesestudio.mc.quickgui.service;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public interface EditGuiService {

    boolean createGui(@NotNull CommandSender sender, String name, int rows);

}
