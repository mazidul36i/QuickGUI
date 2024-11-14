package com.gliesestudio.mc.quickgui.enums;

public enum ItemStackType {

    SYSTEM_BUTTON("system_button", -1),
    SYSTEM_FILLER("system_filler", -2),
    FILLER("filler", 0),
    BUTTON("button", 1),
    PLACEHOLDER("placeholder", 2);

    private final String name;
    private final int id;

    ItemStackType(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public static ItemStackType fromName(String name) {
        for (ItemStackType type : ItemStackType.values()) {
            if (type.name.equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }

    public static ItemStackType fromId(int id) {
        for (ItemStackType type : ItemStackType.values()) {
            if (type.id == id) {
                return type;
            }
        }
        return null;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
