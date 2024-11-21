package com.gliesestudio.mc.quickgui.enums;

import lombok.Getter;

@Getter
public enum PlayerHead {

    BACK("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjBjZmI0ZjM3Y2NlZmQwNTg5YzU1NzhiNTQxZTdhZjkyM2UzZTY0MjBhZGE2YmU0NDNkZmFkY2IwNWJhZTE5NCJ9fX0="),

    ARROW_RIGHT("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjkxYWM0MzJhYTQwZDdlN2E2ODdhYTg1MDQxZGU2MzY3MTJkNGYwMjI2MzJkZDUzNTZjODgwNTIxYWYyNzIzYSJ9fX0="),
    ARROW_UP("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzczMzRjZGRmYWI0NWQ3NWFkMjhlMWE0N2JmOGNmNTAxN2QyZjA5ODJmNjczN2RhMjJkNDk3Mjk1MjUxMDY2MSJ9fX0="),
    ARROW_LEFT("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2EyYzEyY2IyMjkxODM4NGUwYTgxYzgyYTFlZDk5YWViZGNlOTRiMmVjMjc1NDgwMDk3MjMxOWI1NzkwMGFmYiJ9fX0="),
    ARROW_DOWN("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTc3NDIwMzRmNTlkYjg5MGM4MDA0MTU2YjcyN2M3N2NhNjk1YzQzOTlkOGUwZGE1Y2U5MjI3Y2Y4MzZiYjhlMiJ9fX0="),

    ARROW_UP_RIGHT("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDU2ZTZkMTRjZjY4ZTI1YzhlYTJlOWIyZjVlYzRmMjY3YWViY2M5YTE5MjZhYmZjNGEwMDZiMDVhMTU0NDJmYyJ9fX0="),
    ARROW_UP_LEFT("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTkyOTc0NjgxNjg3Njg5ZGE3ZGRhM2YxOWI3ZTRhNTNmZTBkZDA5YmVmZDdmYTg4Mzg3NDQzODRjOWQxYWM3MSJ9fX0="),
    ARROW_DOWN_LEFT("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2JlY2YzYWFmZDc4YTg0Mzk0YWQzNzc1ZTZhOTg3M2YwMGViOTZlZGMyNjljZmNiMmVkOWNjNzgxM2FmM2EyYyJ9fX0="),
    ARROW_DOWN_RIGHT("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTY2N2YyYzg4ZTc2YTE1ODg4ZmM3NGVmOTNmNTI1YTMyMWE0MDZmYWQ4Zjc2YWMwYjIyZjcyN2FjMTJiNzFmZSJ9fX0=");

    private final String base64;

    PlayerHead(String base64) {
        this.base64 = base64;
    }

    // From string matching name
    public static PlayerHead fromString(String name) {
        for (PlayerHead playerHead : PlayerHead.values()) {
            if (playerHead.name().equalsIgnoreCase(name)) {
                return playerHead;
            }
        }
        return null;
    }

}
