package com.gliesestudio.mc.quickgui.enums;

import lombok.Getter;

import javax.annotation.Nullable;

@Getter
public enum SystemCommand {
    CHANGE_NAME("change name", AwaitingInputType.INPUT_NAME),
    CHANGE_TITLE("change title", AwaitingInputType.INPUT_TITLE),
    CHANGE_ROWS("change rows", AwaitingInputType.INPUT_ROW),
    CHANGE_PERMISSION("change permission", AwaitingInputType.INPUT_PERMISSION),
    CHANGE_ALIAS("change alias", AwaitingInputType.INPUT_ALIAS),
    EDIT_ITEMS("edit items", null),
    BACK("back", null),

    TOGGLE_ITEM_GLOW("toggle item glow", null),
    CHANGE_ITEM_NAME("change item name", AwaitingInputType.INPUT_ITEM_NAME),

    CHANGE_ITEM_LORES("change item lores", null),
    ADD_ITEM_LORE("add item lore", AwaitingInputType.INPUT_ADD_ITEM_LORE),
    EDIT_ITEM_LORE("edit item lore", AwaitingInputType.INPUT_EDIT_ITEM_LORE),
    DELETE_ITEM_LORE("delete item lore", null),

    ;

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

}
