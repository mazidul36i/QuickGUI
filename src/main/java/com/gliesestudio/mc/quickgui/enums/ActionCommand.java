package com.gliesestudio.mc.quickgui.enums;

public enum ActionCommand {

    CLOSE("close"),
    BACK("back");

    private final String command;

    ActionCommand(String command) {
        this.command = command;
    }

    public static ActionCommand fromString(String command) {
        for (ActionCommand c : ActionCommand.values()) {
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
