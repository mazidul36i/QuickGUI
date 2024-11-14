package com.gliesestudio.mc.quickgui.commands;

import javax.annotation.Nullable;

public interface PluginCommands {

    String OPEN_GUI = "gui";
    String QUICK_GUI = "quickgui";
    String EDIT_GUI = "editgui";

    enum Action {
        RELOAD("reload"),
        CREATE("create"),
        EDIT("edit");

        private final String action;

        Action(String action) {
            this.action = action;
        }

        public String getAction() {
            return action;
        }

        /**
         * This method is used to get the action from the string.
         *
         * @param action The action to get.
         * @return The action.
         */
        public static Action fromString(String action) {
            for (Action a : Action.values()) {
                if (a.action.equalsIgnoreCase(action)) {
                    return a;
                }
            }
            return null;
        }

    }

    enum SystemCommand {
        CANCEL("cancel"),
        CLOSE("close"),
        DONE("done");

        private final String command;

        SystemCommand(String command) {
            this.command = command;
        }

        /**
         * This method is used to get the system command from the string.
         *
         * @param command The command to get.
         * @return The system command.
         */
        @Nullable
        public static SystemCommand fromString(String command) {
            for (SystemCommand c : SystemCommand.values()) {
                if (c.command.equalsIgnoreCase(command)) {
                    return c;
                }
            }
            return null;
        }

        public String getCommand() {
            return command;
        }
    }

}
