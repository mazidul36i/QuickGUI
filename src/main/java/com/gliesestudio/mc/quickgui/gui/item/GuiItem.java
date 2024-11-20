package com.gliesestudio.mc.quickgui.gui.item;

import com.gliesestudio.mc.quickgui.placeholder.PlaceholderHelper;
import com.gliesestudio.mc.quickgui.placeholder.SystemPlaceholder;
import com.gliesestudio.mc.quickgui.utility.CollectionUtils;
import com.gliesestudio.mc.quickgui.utility.PluginUtils;
import lombok.Data;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nullable;
import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
public class GuiItem implements Serializable {

    @Serial
    private static final long serialVersionUID = -5861118391400601112L;

    private GuiItemInfo item;
    private Map<GuiItemActionType, GuiItemAction> actions;

    public boolean hasItemInfo() {
        return item != null && item.getName() != null;
    }

    public boolean hasActions() {
        return actions != null && !actions.isEmpty();
    }

    @Nullable
    public ItemStack createItemStack(Player player) {
        if (item == null || item.getName() == null) return null;

        Material itemMaterial = Material.getMaterial(item.getName());
        if (itemMaterial == null) return null;

        ItemStack itemStack = new ItemStack(itemMaterial);
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            if (item.isHideTooltip()) {
                meta.setHideTooltip(true);
            } else {
                // Set display name
                if (item.getDisplayName() != null) {
                    meta.displayName(Component.text(PluginUtils.translateColorCodes(item.getDisplayName())));
                }

                // Set lore with parsed placeholders
                if (CollectionUtils.isNotEmpty(item.getLore())) {
                    meta.lore(item.getLore().stream().map(text ->
                            text != null && !text.isEmpty() ? Component.text(PluginUtils.translateColorCodes(
                                    PlaceholderHelper.parseValue(text, SystemPlaceholder.PLAYER, player.getName())
                            )) : Component.empty()
                    ).toList());
                }
            }
            itemStack.setItemMeta(meta);
        }
        return itemStack;
    }

    public static GuiItem deserialize(ConfigurationSection itemConfig) {
        GuiItem guiItem = new GuiItem();
        ConfigurationSection itemInfoConfig = itemConfig.getConfigurationSection("item");
        if (itemInfoConfig != null) {
            guiItem.setItem(GuiItemInfo.deserialize(itemInfoConfig));
        }

        ConfigurationSection actionsConfig = itemConfig.getConfigurationSection("actions");
        if (actionsConfig != null) {
            Map<GuiItemActionType, GuiItemAction> actions = new HashMap<>();
            actionsConfig.getKeys(false).forEach(action -> {
                ConfigurationSection actionConfig = actionsConfig.getConfigurationSection(action);
                GuiItemActionType actionType = GuiItemActionType.fromString(action);
                if (actionConfig != null && actionType != null) {
                    GuiItemAction guiAction = GuiItemAction.deserialize(actionConfig);
                    actions.put(actionType, guiAction);
                }
            });
            guiItem.setActions(actions);
        }
        return guiItem;
    }

    public Map<String, Object> serialize() {
        if (!hasItemInfo()) return null;

        Map<String, Object> itemConfig = new HashMap<>();
        itemConfig.put("item", item.serialize());

        if (actions != null && !actions.isEmpty()) {
            Map<String, Object> actionsConfig = new HashMap<>();
            actions.forEach((actionType, action) ->
                    actionsConfig.put(actionType.getType(), action.serialize())
            );
            itemConfig.put("actions", actionsConfig);
        }
        return itemConfig;
    }

}
