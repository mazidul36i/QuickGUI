/*
 * MIT License
 *
 * Copyright (c) 2024 Mazidul Islam
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.gliesestudio.mc.quickgui.completer;

import com.gliesestudio.mc.quickgui.commands.PluginCommands;
import com.gliesestudio.mc.quickgui.manager.GuiManager;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * This class provides tab completion for the custom GUIs.
 *
 * @author Mazidul Islam
 * @version 1.0
 * @see TabCompleter
 * @see GuiManager
 * @see PluginCommands
 * @since 1.0
 */
public class OpenGuiTabCompleter implements TabCompleter {

    private static final Logger log = LoggerFactory.getLogger(OpenGuiTabCompleter.class);
    private final GuiManager guiManager;

    public OpenGuiTabCompleter(GuiManager guiManager) {
        this.guiManager = guiManager;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (!StringUtils.equals(command.getName(), PluginCommands.OPEN_GUI)) {
            log.debug("Registered wrong command for OpenGuiTabCompleter: {}", command);
            return new ArrayList<>();
        }

        List<String> suggestions = new ArrayList<>();
        if (args.length == 1) {
            for (String guiName : guiManager.getGuiNames()) {
                if (guiName.toLowerCase().contains(args[0].toLowerCase())) {
                    suggestions.add(guiName);
                }
            }
        }
        return suggestions;
    }

}
