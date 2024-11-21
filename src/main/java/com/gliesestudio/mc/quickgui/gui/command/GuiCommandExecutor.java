package com.gliesestudio.mc.quickgui.gui.command;

import lombok.Getter;

@Getter
public enum GuiCommandExecutor {
    SERVER("server"),
    PLAYER("player");

    private final String executor;

    GuiCommandExecutor(String executor) {
        this.executor = executor;
    }

    // From string
    public static GuiCommandExecutor fromString(String executor) {
        for (GuiCommandExecutor e : GuiCommandExecutor.values()) {
            if (e.executor.equalsIgnoreCase(executor)) {
                return e;
            }
        }
        return null;
    }

}
