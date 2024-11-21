package com.gliesestudio.mc.quickgui.utility;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import org.bukkit.Bukkit;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class CustomHeadUtil {

    private static final Logger log = LoggerFactory.getLogger(CustomHeadUtil.class);

    public static void setCustomHeadMeta(ItemMeta itemMeta, String textureBase64) {
        if (itemMeta instanceof SkullMeta skullMeta && textureBase64 != null && !textureBase64.isEmpty()) {
            // Create a new PlayerProfile and set the texture
            PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
            profile.setProperty(new ProfileProperty("textures", textureBase64));

            // Apply the profile to the SkullMeta
            skullMeta.setPlayerProfile(profile);
        }
    }
}
