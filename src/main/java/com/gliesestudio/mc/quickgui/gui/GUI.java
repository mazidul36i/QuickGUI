package com.gliesestudio.mc.quickgui.gui;

import com.gliesestudio.mc.quickgui.gui.item.GuiItem;
import lombok.Data;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Data
public class GUI implements Serializable {

    @Serial
    private static final long serialVersionUID = 9145177080913758390L;

    private String name;
    private String title;
    private String alias;
    private String permission;
    private int rows;
    private Map<Integer, GuiItem> items;

    public String getTitle() {
        return title != null ? title : name;
    }

    public boolean hasAlias() {
        return alias != null && !alias.isEmpty();
    }

    public boolean hasPermission() {
        return permission != null && !permission.isEmpty();
    }

    public void updateItem(int slot, GuiItem guiItem) {
        if (items == null) items = new HashMap<>();
        items.put(slot, guiItem);
    }

    public static @NotNull GUI deserialize(@NotNull FileConfiguration guiConfig) {
        GUI gui = new GUI();
        gui.setName(guiConfig.getString("name"));
        gui.setTitle(guiConfig.getString("title"));
        gui.setAlias(guiConfig.getString("alias"));
        gui.setPermission(guiConfig.getString("permission"));
        gui.setRows(guiConfig.getInt("rows"));

        ConfigurationSection itemsConfig = guiConfig.getConfigurationSection("items");
        if (itemsConfig != null) {
            Map<Integer, GuiItem> items = new HashMap<>();
            itemsConfig.getKeys(false).forEach(slot -> {
                ConfigurationSection itemConfig = itemsConfig.getConfigurationSection(slot);
                int slotInt;
                try {
                    slotInt = Integer.parseInt(slot);
                } catch (NumberFormatException e) {
                    return;
                }
                if (itemConfig != null) {
                    GuiItem guiItem = GuiItem.deserialize(itemConfig);
                    items.put(slotInt, guiItem);
                }
            });
            gui.setItems(items);
        }

        return gui;
    }

    public @NotNull YamlConfiguration serialize() {
        YamlConfiguration guiConfig = new YamlConfiguration();
        guiConfig.set("name", name);
        guiConfig.set("title", title);
        guiConfig.set("alias", alias);
        guiConfig.set("permission", permission);
        guiConfig.set("rows", rows);

        if (items != null) {
            ConfigurationSection itemsConfig = guiConfig.createSection("items");
            items.forEach((slot, item) -> {
                if (item != null && item.hasItemInfo()) {
                    itemsConfig.createSection(String.valueOf(slot), item.serialize());
                }
            });
        }

        return guiConfig;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GUI gui = (GUI) o;
        return Objects.equals(name, gui.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

    public GuiItem getItem(Integer itemSlot) {
        return items.get(itemSlot);
    }
}
