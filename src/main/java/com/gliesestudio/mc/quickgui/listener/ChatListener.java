package com.gliesestudio.mc.quickgui.listener;

import com.gliesestudio.mc.quickgui.QuickGUI;
import com.gliesestudio.mc.quickgui.enums.AwaitingInputType;
import com.gliesestudio.mc.quickgui.gui.SystemGuiHolder;
import com.gliesestudio.mc.quickgui.model.AwaitingInputHolder;
import com.gliesestudio.mc.quickgui.service.EditGuiService;
import com.gliesestudio.mc.quickgui.service.EditItemService;
import com.gliesestudio.mc.quickgui.service.EditLoreService;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

public class ChatListener implements Listener {

    private static final Logger log = LoggerFactory.getLogger(ChatListener.class);
    private final Map<UUID, AwaitingInputHolder> awaitingInputs = new HashMap<>();

    private final QuickGUI plugin;
    private final EditGuiService editGuiService;
    private final EditItemService editItemService;
    private final EditLoreService editLoreService;

    public ChatListener(QuickGUI plugin) {
        this.plugin = plugin;
        this.editGuiService = plugin.getEditGuiService();
        this.editItemService = plugin.getEditItemService();
        this.editLoreService = plugin.getEditLoreService();
    }

    @EventHandler
    public void onPlayerChat(AsyncChatEvent event) {
        Player player = event.getPlayer();

        // Check if the player is awaiting any inputs
        if (awaitingInputs.containsKey(player.getUniqueId()) && event.message() instanceof TextComponent message) {
            event.setCancelled(true);  // Prevent the message from appearing in chat
            String textInput = message.content();
            log.info("Message: {}", textInput);

            AwaitingInputHolder awaitingInputHolder = awaitingInputs.get(player.getUniqueId());
            String name = awaitingInputHolder.getSystemGuiHolder().getGui().getName();

            switch (awaitingInputHolder.getInputType()) {
                case INPUT_NAME -> {
                    Pattern pattern = Pattern.compile("^[a-zA-Z0-9_-]+$");
                    if (!(pattern.matcher(textInput).matches())) {
                        player.sendMessage("§eInvalid GUI name. The name must contain only alphabets, numbers, underscores, and hyphens.");
                        return;
                    }

                    boolean isSaved = editGuiService.editGuiName(name, textInput);
                    if (isSaved) {
                        openEditGuiBackReloadAll(player, textInput);
                    } else player.sendMessage("§cFailed to change GUI name");
                }

                case INPUT_TITLE -> {
                    boolean isSaved = editGuiService.editGuiTitle(name, textInput);
                    if (isSaved) openEditGuiBack(player, name, true);
                    else player.sendMessage("§cFailed to change GUI title");
                }

                case INPUT_ROW -> {
                    try {
                        int newRows = Integer.parseInt(textInput);
                        boolean isSaved = editGuiService.editGuiRows(name, newRows);
                        if (isSaved) openEditGuiBack(player, name, true);
                        else player.sendMessage("§cFailed to change GUI title");
                    } catch (NumberFormatException e) {
                        player.sendMessage("§cPlease enter a valid row count from 1 to 6");
                        return;
                    }
                }

                case INPUT_PERMISSION -> {
                    boolean isSaved = editGuiService.editGuiPermission(name, textInput);
                    if (isSaved) openEditGuiBack(player, name, true);
                    else player.sendMessage("§cFailed to change GUI permission");
                }

                case INPUT_ALIAS -> {
                    boolean isSaved = editGuiService.editGuiAlias(name, textInput);
//                    editGuiService.reloadGuis();
                    if (isSaved) openEditGuiBackReloadAll(player, name);
                    else player.sendMessage("§cFailed to change GUI alias");
                }

                case INPUT_ITEM_NAME -> {
                    boolean isSaved = editItemService.updateItemConfig(awaitingInputHolder.getSystemGuiHolder(), awaitingInputHolder.getInputType(), textInput);
                    if (isSaved) openAwaitingGui(player, awaitingInputHolder.getSystemGuiHolder(), true);
                    else player.sendMessage("§cFailed to change GUI name");
                }

                case INPUT_ADD_ITEM_LORE -> {
                    boolean isSaved = editLoreService.editItemLoreConfig(awaitingInputHolder.getSystemGuiHolder(),
                            awaitingInputHolder.getInputType(), textInput, awaitingInputHolder.getEditLorePosition());
                    if (isSaved) openAwaitingLoreGui(player, awaitingInputHolder.getSystemGuiHolder());
                    else player.sendMessage("§cFailed to add GUI item lore");
                }

                case INPUT_EDIT_ITEM_LORE -> {
                    boolean isSaved = editLoreService.editItemLoreConfig(awaitingInputHolder.getSystemGuiHolder(),
                            awaitingInputHolder.getInputType(), textInput, awaitingInputHolder.getEditLorePosition());
                    if (isSaved) openAwaitingLoreGui(player, awaitingInputHolder.getSystemGuiHolder());
                    else player.sendMessage("§cFailed to edit GUI item lore");
                }

                // If no input has matched or not implemented
                case null, default -> player.sendMessage("§eUnknown input");
            }

            // Remove player from the waiting map
            awaitingInputs.remove(player.getUniqueId());
        }
    }

    private void openAwaitingGui(Player player, SystemGuiHolder systemGuiHolder, boolean reload) {
        // Schedule the inventory opening on the main thread
        Bukkit.getScheduler().runTask(plugin, () -> {
            if (reload) editGuiService.reloadGui(systemGuiHolder.getGui().getName());
            player.openInventory(systemGuiHolder.createInventory());
        });
    }

    private void openEditGuiBack(Player player, String name, boolean reload) {
        // Schedule the inventory opening on the main thread
        Bukkit.getScheduler().runTask(plugin, () -> {
            if (reload) editGuiService.reloadGui(name);
            editGuiService.editGui(player, name);
        });
    }

    private void openEditGuiBackReloadAll(Player player, String name) {
        // Schedule the inventory opening on the main thread
        Bukkit.getScheduler().runTask(plugin, () -> {
            editGuiService.reloadGuis();
            editGuiService.editGui(player, name);
        });
    }

    private void openAwaitingLoreGui(Player player, SystemGuiHolder systemGuiHolder) {
        // Schedule the inventory opening on the main thread
        Bukkit.getScheduler().runTask(plugin, () -> {
            editGuiService.reloadGui(systemGuiHolder.getGui().getName());
            systemGuiHolder.getPrevSystemGui().createInventory();
            editLoreService.openEditLoreGui(player, systemGuiHolder.getPrevSystemGui());
        });
    }

    public void addAwaitingInput(UUID playerUuid, AwaitingInputType inputType, SystemGuiHolder systemGuiHolder) {
        AwaitingInputHolder inputHolder = new AwaitingInputHolder()
                .inputType(inputType)
                .systemGuiHolder(systemGuiHolder);
        awaitingInputs.put(playerUuid, inputHolder);
    }

    public void addAwaitingInput(UUID playerUuid, AwaitingInputType inputType, SystemGuiHolder systemGuiHolder, int editLorePosition) {
        AwaitingInputHolder inputHolder = new AwaitingInputHolder()
                .inputType(inputType)
                .systemGuiHolder(systemGuiHolder)
                .editLorePosition(editLorePosition);
        awaitingInputs.put(playerUuid, inputHolder);
    }

}
