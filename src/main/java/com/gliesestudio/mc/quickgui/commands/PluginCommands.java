package com.gliesestudio.mc.quickgui.commands;

public interface PluginCommands {

    String OPEN_GUI = "gui";
    String QUICK_GUI = "quickgui";
    String EDIT_GUI = "editgui";

    enum Action {
        RELOAD("reload"),
        CREATE("create");

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
