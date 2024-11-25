package com.gliesestudio.mc.quickgui.gui.item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuiItemInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 7777009449102718047L;

    private String name;
    private GuiItemType type;
    private String displayName;
    private List<String> lore;
    private boolean hideTooltip;
    private String texture;
    private boolean glow;

    public static GuiItemInfo deserialize(ConfigurationSection itemInfoConfig) {
        GuiItemInfo info = new GuiItemInfo();
        GuiItemType itemType = GuiItemType.fromName(itemInfoConfig.getString("type"));
        info.setName(itemInfoConfig.getString("name"));
        info.setType(itemType != null ? itemType : GuiItemType.BUTTON);
        info.setDisplayName(itemInfoConfig.getString("display-name"));
        info.setLore(itemInfoConfig.getStringList("lore"));
        info.setHideTooltip(itemInfoConfig.getBoolean("hide-tooltip", false));
        info.setTexture(itemInfoConfig.getString("texture"));
        info.setGlow(itemInfoConfig.getBoolean("glow"));
        return info;
    }

    public static GuiItemInfo fromItemStack(@NotNull ItemStack itemStack) {
        GuiItemInfo itemInfo = new GuiItemInfo();
        itemInfo.setName(itemStack.getType().toString());
        if (itemStack.hasItemMeta()) {
            itemStack.getItemMeta().displayName(); // TODO: get item meta from item stack like display name and lore
        }
        itemInfo.setType(GuiItemType.BUTTON);
        return itemInfo;
    }

    public static GuiItemInfo fromMaterial(Material material, GuiItemType itemType, boolean hideTooltip) {
        GuiItemInfo itemInfo = new GuiItemInfo();
        itemInfo.setName(material.name());
        itemInfo.setType(itemType);
        itemInfo.setHideTooltip(hideTooltip);
        return itemInfo;
    }

    public Map<String, Object> serialize() {
        Map<String, Object> itemInfoConfig = new HashMap<>();
        itemInfoConfig.put("name", name);
        itemInfoConfig.put("type", type.getName());
        itemInfoConfig.put("display-name", displayName);
        itemInfoConfig.put("hide-tooltip", hideTooltip);
        if (lore != null && !lore.isEmpty()) itemInfoConfig.put("lore", lore);
        itemInfoConfig.put("texture", texture);
        itemInfoConfig.put("glow", glow);
        return itemInfoConfig;
    }

}

