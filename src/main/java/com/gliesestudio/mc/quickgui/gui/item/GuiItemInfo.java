package com.gliesestudio.mc.quickgui.gui.item;

import lombok.Data;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class GuiItemInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 7777009449102718047L;

    private String name;
    private GuiItemType type;
    private String displayName;
    private List<String> lore;
    private boolean hideTooltip;

    public static GuiItemInfo deserialize(ConfigurationSection itemInfoConfig) {
        GuiItemInfo info = new GuiItemInfo();
        GuiItemType itemType = GuiItemType.fromName(itemInfoConfig.getString("type"));
        info.setName(itemInfoConfig.getString("name"));
        info.setType(itemType != null ? itemType : GuiItemType.BUTTON);
        info.setDisplayName(itemInfoConfig.getString("display-name"));
        info.setLore(itemInfoConfig.getStringList("lore"));
        info.setHideTooltip(itemInfoConfig.getBoolean("hide-tooltip", false));
        return info;
    }

    public static GuiItemInfo fromItemStack(@NotNull ItemStack itemStack) {
        GuiItemInfo itemInfo = new GuiItemInfo();
        itemInfo.setName(itemStack.getType().toString());
        itemInfo.setType(GuiItemType.BUTTON);
        return itemInfo;
    }

    public Map<String, Object> serialize() {
        Map<String, Object> itemInfoConfig = new HashMap<>();
        itemInfoConfig.put("name", name);
        itemInfoConfig.put("type", type.getName());
        itemInfoConfig.put("display-name", displayName);
        itemInfoConfig.put("hide-tooltip", hideTooltip);
        if (lore != null && !lore.isEmpty()) itemInfoConfig.put("lore", lore);
        return itemInfoConfig;
    }

}

