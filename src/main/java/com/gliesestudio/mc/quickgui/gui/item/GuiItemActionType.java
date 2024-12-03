package com.gliesestudio.mc.quickgui.gui.item;

import lombok.Getter;
import org.bukkit.event.inventory.ClickType;

@Getter
public enum GuiItemActionType {

    LEFT("left"),
    SHIFT_LEFT("shift-left"),
    MIDDLE("middle"),
    RIGHT("right"),
    SHIFT_RIGHT("shift-right");

    private final String type;

    GuiItemActionType(String type) {
        this.type = type;
    }

    public static GuiItemActionType fromString(String type) {
        for (GuiItemActionType t : GuiItemActionType.values()) {
            if (t.type.equalsIgnoreCase(type)) {
                return t;
            }
        }
        return null;
    }

    public static GuiItemActionType fromClickType(ClickType clickType) {
        return switch (clickType) {
            case LEFT -> LEFT;
            case SHIFT_LEFT -> SHIFT_LEFT;
            case MIDDLE -> MIDDLE;
            case RIGHT -> RIGHT;
            case SHIFT_RIGHT -> SHIFT_RIGHT;
            default -> null;
        };
    }

}
