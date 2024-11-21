package com.gliesestudio.mc.quickgui.commands;

import com.gliesestudio.mc.quickgui.enums.AwaitingInputType;

import javax.annotation.Nullable;

public interface PluginCommands {

    String OPEN_GUI = "opengui";
    String QUICK_GUI = "quickgui";
    String GUI = "gui";

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

}
