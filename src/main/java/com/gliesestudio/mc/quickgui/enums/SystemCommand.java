package com.gliesestudio.mc.quickgui.enums;

import com.gliesestudio.mc.quickgui.gui.item.GuiItemActionType;
import lombok.Getter;

import javax.annotation.Nullable;

@Getter
public enum SystemCommand {
    CHANGE_NAME("change name", AwaitingInputType.INPUT_NAME, null),
    CHANGE_TITLE("change title", AwaitingInputType.INPUT_TITLE, null),
    CHANGE_ROWS("change rows", AwaitingInputType.INPUT_ROW, null),
    CHANGE_PERMISSION("change permission", AwaitingInputType.INPUT_PERMISSION, null),
    CHANGE_ALIAS("change alias", AwaitingInputType.INPUT_ALIAS, null),
    EDIT_ITEMS("edit items", null, null),
    BACK("back", null, null),

    TOGGLE_ITEM_GLOW("toggle item glow", null, null),
    CHANGE_ITEM_NAME("change item name", AwaitingInputType.INPUT_ITEM_NAME, null),

    CHANGE_ITEM_LORES("change item lores", null, null),
    ADD_ITEM_LORE("add item lore", AwaitingInputType.INPUT_ADD_ITEM_LORE, null),
    EDIT_ITEM_LORE("edit item lore", AwaitingInputType.INPUT_EDIT_ITEM_LORE, null),
    DELETE_ITEM_LORE("delete item lore", null, null),

    EDIT_ITEM_ACTION_LEFT("edit item action left", null, GuiItemActionType.LEFT),
    EDIT_ITEM_ACTION_SHIFT_LEFT("edit item action shift-left", null, GuiItemActionType.SHIFT_LEFT),
    EDIT_ITEM_ACTION_MIDDLE("edit item action middle", null, GuiItemActionType.MIDDLE),
    EDIT_ITEM_ACTION_RIGHT("edit item action right", null, GuiItemActionType.RIGHT),
    EDIT_ITEM_ACTION_SHIFT_RIGHT("edit item action shift-right", null, GuiItemActionType.SHIFT_RIGHT),

    ADD_ITEM_ACTION_LEFT_COMMAND("add item action left command",
            AwaitingInputType.INPUT_ADD_ITEM_ACTION_LEFT_COMMAND, GuiItemActionType.LEFT),
    ADD_ITEM_ACTION_SHIFT_LEFT_COMMAND("add item action shift-left command",
            AwaitingInputType.INPUT_ADD_ITEM_ACTION_SHIFT_LEFT_COMMAND, GuiItemActionType.SHIFT_LEFT),
    ADD_ITEM_ACTION_MIDDLE_COMMAND("add item action middle command",
            AwaitingInputType.INPUT_ADD_ITEM_ACTION_MIDDLE_COMMAND, GuiItemActionType.MIDDLE),
    ADD_ITEM_ACTION_RIGHT_COMMAND("add item action right command",
            AwaitingInputType.INPUT_ADD_ITEM_ACTION_RIGHT_COMMAND, GuiItemActionType.RIGHT),
    ADD_ITEM_ACTION_SHIFT_RIGHT_COMMAND("add item action shift-right command",
            AwaitingInputType.INPUT_ADD_ITEM_ACTION_SHIFT_RIGHT_COMMAND, GuiItemActionType.SHIFT_RIGHT),

    EDIT_ITEM_ACTION_LEFT_COMMAND("edit item action left command",
            AwaitingInputType.INPUT_EDIT_ITEM_ACTION_LEFT_COMMAND, GuiItemActionType.LEFT),
    EDIT_ITEM_ACTION_SHIFT_LEFT_COMMAND("edit item action shift-left command",
            AwaitingInputType.INPUT_EDIT_ITEM_ACTION_SHIFT_LEFT_COMMAND, GuiItemActionType.SHIFT_LEFT),
    EDIT_ITEM_ACTION_MIDDLE_COMMAND("edit item action middle command",
            AwaitingInputType.INPUT_EDIT_ITEM_ACTION_MIDDLE_COMMAND, GuiItemActionType.MIDDLE),
    EDIT_ITEM_ACTION_RIGHT_COMMAND("edit item action right command",
            AwaitingInputType.INPUT_EDIT_ITEM_ACTION_RIGHT_COMMAND, GuiItemActionType.RIGHT),
    EDIT_ITEM_ACTION_SHIFT_RIGHT_COMMAND("edit item action shift-right command",
            AwaitingInputType.INPUT_EDIT_ITEM_ACTION_SHIFT_RIGHT_COMMAND, GuiItemActionType.SHIFT_RIGHT),

    DELETE_ITEM_ACTION_LEFT_COMMAND("delete item action left command", null, GuiItemActionType.LEFT),
    DELETE_ITEM_ACTION_SHIFT_LEFT_COMMAND("delete item action shift-left command", null, GuiItemActionType.SHIFT_LEFT),
    DELETE_ITEM_ACTION_MIDDLE_COMMAND("delete item action middle command", null, GuiItemActionType.MIDDLE),
    DELETE_ITEM_ACTION_RIGHT_COMMAND("delete item action right command", null, GuiItemActionType.RIGHT),
    DELETE_ITEM_ACTION_SHIFT_RIGHT_COMMAND("delete item action shift-right command", null, GuiItemActionType.SHIFT_RIGHT),

    ;

    private final String command;
    private final AwaitingInputType inputType;
    private final GuiItemActionType itemActionType;

    SystemCommand(String command, AwaitingInputType inputType, GuiItemActionType itemActionType) {
        this.command = command;
        this.inputType = inputType;
        this.itemActionType = itemActionType;
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
