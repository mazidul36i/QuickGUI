package com.gliesestudio.mc.quickgui.placeholder;

import java.util.Map;

public class PlaceholderHelper {

    public static String parseValue(String text, String key, String value) {
        return text != null ? text.replace(key, value) : null;
    }

    public static String parseValues(String text, Map<String, String> keyValues) {
        if (text == null) return text;
        for (Map.Entry<String, String> entry : keyValues.entrySet()) {
            text = text.replace(entry.getKey(), entry.getValue());
        }
        return text;
    }

}
