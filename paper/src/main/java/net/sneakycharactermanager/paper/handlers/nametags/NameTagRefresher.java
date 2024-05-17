package net.sneakycharactermanager.paper.handlers.nametags;

import me.clip.placeholderapi.PlaceholderAPI;
import net.sneakycharactermanager.paper.SneakyCharacterManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class NameTagRefresher extends BukkitRunnable {

    private final BukkitTask task;
    private final ConcurrentMap<Player, List<Player>> trackedByPrev = new ConcurrentHashMap<>();

    public NameTagRefresher() {
        this.task = runTaskTimer(SneakyCharacterManager.getInstance(), 0, 20);
    }

    public void stop() {
        this.task.cancel();
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Nickname name = SneakyCharacterManager.getInstance().nametagManager.getNickname(player);
            if (player.isDead() ||
                player.getGameMode() == GameMode.SPECTATOR ||
                (SneakyCharacterManager.isPapiActive() && !PlaceholderAPI.setPlaceholders(player, "%cmi_user_vanished_symbol%").isEmpty()) ||
                name == null
            ) {
                trackedByPrev.put(player, new ArrayList<>());
                continue;
            }

            trackedByPrev.put(player, SneakyCharacterManager.getInstance().nametagManager.refreshNicknames(player, name, trackedByPrev.getOrDefault(player, new ArrayList<>())));
        }
    }
    
}
