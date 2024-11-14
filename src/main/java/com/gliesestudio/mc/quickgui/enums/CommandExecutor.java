package com.gliesestudio.mc.quickgui.enums;

public enum CommandExecutor {
    SERVER("server"),
    PLAYER("player");

    private final String executor;

    CommandExecutor(String executor) {
        this.executor = executor;
    }

    // From string
    public static CommandExecutor fromString(String executor) {
        for (CommandExecutor e : CommandExecutor.values()) {
            if (e.executor.equalsIgnoreCase(executor)) {
                return e;
            }
        }
        return null;
    }

    public String getExecutor() {
        return executor;
    }

}
