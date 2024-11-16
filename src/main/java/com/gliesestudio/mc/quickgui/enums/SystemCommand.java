package com.gliesestudio.mc.quickgui.enums;

import javax.annotation.Nullable;

public enum SystemCommand {
    CHANGE_NAME("change name", AwaitingInputType.INPUT_NAME),
    CHANGE_TITLE("change title", AwaitingInputType.INPUT_TITLE),
    CHANGE_ROWS("change rows", AwaitingInputType.INPUT_ROW),
    CHANGE_PERMISSION("change permission", AwaitingInputType.INPUT_PERMISSION),
    CHANGE_ALIAS("change alias", AwaitingInputType.INPUT_ALIAS),
    EDIT_ITEMS("edit items", null);

    private final String command;
    private final AwaitingInputType inputType;

    SystemCommand(String command, AwaitingInputType inputType) {
        this.command = command;
        this.inputType = inputType;
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

    public AwaitingInputType getInputType() {
        return inputType;
    }
}
