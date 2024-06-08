package net.sneakymouse.sneakycharactermanager.util;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class SkinUtil {
    
    public static PlayerProfile handleCachedSkin(OfflinePlayer player, ProfileProperty profileProperty) {
        PlayerProfile playerProfile = player.getPlayerProfile();
        playerProfile.removeProperty("textures");
    
        if (profileProperty == null) {
            if(player.isOnline()){
                ((Player)player).sendMessage(ChatUtility.convertToComponent("&4Failed to load skin! Something went wrong!"));
            }
            return null;
        } else {
            playerProfile.setProperty(profileProperty);
            return playerProfile;
        }
    }

}
