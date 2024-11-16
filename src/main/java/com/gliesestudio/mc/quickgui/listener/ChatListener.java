package com.gliesestudio.mc.quickgui.listener;

import com.gliesestudio.mc.quickgui.QuickGUI;
import com.gliesestudio.mc.quickgui.enums.AwaitingInputType;
import com.gliesestudio.mc.quickgui.inventory.SystemGuiHolder;
import com.gliesestudio.mc.quickgui.model.AwaitingInputHolder;
import com.gliesestudio.mc.quickgui.service.EditGuiService;
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

public class ChatListener implements Listener {

    private static final Logger log = LoggerFactory.getLogger(ChatListener.class);
    private final Map<UUID, AwaitingInputHolder> awaitingInputs = new HashMap<>();

    private final QuickGUI plugin;
    private final EditGuiService editGuiService;

    public ChatListener(QuickGUI plugin, EditGuiService editGuiService) {
        this.plugin = plugin;
        this.editGuiService = editGuiService;
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
            String name = awaitingInputHolder.getSystemGuiHolder().getEditGuiHolder().getName();

            switch (awaitingInputHolder.getInputType()) {
                case INPUT_NAME -> {
                    boolean isSaved = editGuiService.editGuiName(name, textInput);
                    if (isSaved) openEditGuiBack(player, name);
                    else player.sendMessage("§cFailed to change GUI name");
                }

                case INPUT_TITLE -> {
                    boolean isSaved = editGuiService.editGuiTitle(name, textInput);
                    if (isSaved) openEditGuiBack(player, name);
                    else player.sendMessage("§cFailed to change GUI title");
                }

                case INPUT_ROW -> {
                    try {
                        int newRows = Integer.parseInt(textInput);
                        boolean isSaved = editGuiService.editGuiRows(name, newRows);
                        if (isSaved) openEditGuiBack(player, name);
                        else player.sendMessage("§cFailed to change GUI title");
                    } catch (NumberFormatException e) {
                        player.sendMessage("§cPlease enter a valid row count from 1 to 6");
                        return;
                    }
                }
            }

            // Remove player from the waiting map
            awaitingInputs.remove(player.getUniqueId());
        }
    }

    private void openEditGuiBack(Player player, String name) {
        // Schedule the inventory opening on the main thread
        Bukkit.getScheduler().runTask(plugin, () -> {
            editGuiService.reloadGui(name);
            editGuiService.editGui(player, name);
        });
    }

    public void addAwaitingInput(UUID playerUuid, AwaitingInputType inputType, SystemGuiHolder systemGuiHolder) {
        AwaitingInputHolder inputHolder = new AwaitingInputHolder()
                .inputType(inputType)
                .systemGuiHolder(systemGuiHolder);
        awaitingInputs.put(playerUuid, inputHolder);
    }

}
