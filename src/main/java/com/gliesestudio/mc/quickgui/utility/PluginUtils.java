package com.gliesestudio.mc.quickgui.utility;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface PluginUtils {

    static String translateColorCodes(@NotNull String textToTranslate) {
        return translateColorCodes('&', textToTranslate);
    }

    static String translateColorCodes(char altColorChar, @NotNull String textToTranslate) {
        char[] b = textToTranslate.toCharArray();
        for (int i = 0; i < b.length - 1; i++) {
            if (b[i] == altColorChar && "0123456789AaBbCcDdEeFfKkLlMmNnOoRrXx".indexOf(b[i + 1]) > -1) {
                b[i] = Constants.COLOR_CHAR;
                b[i + 1] = Character.toLowerCase(b[i + 1]);
            }
        }
        return new String(b);
    }

    static String replacePlaceholders(String placeholderText, Player player) {
        return placeholderText.replace("%player%", player.getName());
    }

    static boolean isEmptyCollection(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

}
