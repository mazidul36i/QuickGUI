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
import com.gliesestudio.mc.quickgui.gui.GuiManager;
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
import java.util.Set;

/**
 * This class provides tab completion for the actions of the plugin.
 *
 * @author Mazidul Islam
 * @version 1.0
 * @see TabCompleter
 * @see GuiManager
 * @see PluginCommands
 * @since 1.0
 */
public class EditActionTabCompleter implements TabCompleter {

    private static final Logger log = LoggerFactory.getLogger(EditActionTabCompleter.class);
    private static final Set<PluginCommands.Action> actions = Set.of(PluginCommands.Action.values());

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        // Suggestion list.
        List<String> suggestions = new ArrayList<>();

        if (!StringUtils.equals(command.getName(), PluginCommands.GUI)) {
            log.debug("Registered wrong command for EditActionTabCompleter: {}", command);
            return suggestions;
        }

        if (args.length == 1) {
            for (PluginCommands.Action action : actions) {
                if (action.getAction().toLowerCase().contains(args[0].toLowerCase())) {
                    suggestions.add(action.getAction());
                }
            }
        }

        if (args.length == 2) {
            PluginCommands.Action action = PluginCommands.Action.fromString(args[0]);
            if (PluginCommands.Action.EDIT.equals(action)) {
                suggestions.addAll(GuiManager.getGuiNames());
            }
        }

        if (args.length == 3) {
            PluginCommands.Action action = PluginCommands.Action.fromString(args[0]);
            if (PluginCommands.Action.CREATE.equals(action)) {
                suggestions.addAll(List.of("1", "2", "3", "4", "5", "6"));
            }
        }

        return suggestions;
    }

}

