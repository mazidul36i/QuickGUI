package com.gliesestudio.mc.quickgui.gui.item;

import lombok.Getter;

@Getter
public enum GuiItemType {

    SYSTEM_BUTTON("system_button", -1),
    SYSTEM_FILLER("system_filler", -2),
    FILLER("filler", 0),
    BUTTON("button", 1),
    PLACEHOLDER("placeholder", 2);

    private final String name;
    private final int id;

    GuiItemType(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public static GuiItemType fromName(String name) {
        for (GuiItemType type : GuiItemType.values()) {
            if (type.name.equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }

    public static GuiItemType fromId(int id) {
        for (GuiItemType type : GuiItemType.values()) {
            if (type.id == id) {
                return type;
            }
        }
        return null;
    }

}
