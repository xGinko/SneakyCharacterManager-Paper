package net.sneakymouse.sneakycharactermanager.handlers.skins;

import java.util.*;

import net.sneakymouse.sneakycharactermanager.SneakyCharacterManagerPaper;
import net.sneakymouse.sneakycharactermanager.util.BungeeMessagingUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class SkinPreloader extends BukkitRunnable {

    public int requestsThisMinute = 0;
    public List<Player> preLoadedPlayers = new ArrayList<>();

    public SkinPreloader() {
        this.runTaskTimer(SneakyCharacterManagerPaper.getInstance(), 0, 1200);
    }

    @Override
    public void run() {
        this.preload();

        this.requestsThisMinute = 0;
    }

    public void preload() {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            if (!this.preLoadedPlayers.contains(player)) {
                preload(player);
                return;
            }
        }
    }

    public void preload(Player player) {
        if (this.requestsThisMinute > 25) return;
        preLoadedPlayers.add(player);

        BungeeMessagingUtil.sendByteArray(player, "preloadSkins", player.getUniqueId().toString());
    }
    
}
