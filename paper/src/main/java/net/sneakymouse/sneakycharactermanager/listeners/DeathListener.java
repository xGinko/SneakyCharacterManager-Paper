package net.sneakymouse.sneakycharactermanager.listeners;

import net.sneakymouse.sneakycharactermanager.struct.Toggleable;
import net.sneakymouse.sneakycharactermanager.SneakyCharacterManagerPaper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;

import net.sneakymouse.sneakycharactermanager.handlers.character.Character;;

public class DeathListener implements Toggleable, Listener {

    private final SneakyCharacterManagerPaper plugin;

    public DeathListener() {
        this.plugin = SneakyCharacterManagerPaper.getInstance();
    }

    @Override
    public void enable() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void disable() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {
        plugin.nametagManager.unnicknamePlayer(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerRespawn(PlayerPostRespawnEvent event) {
        Player player = event.getPlayer();
        Character character = Character.get(player);
        if (character != null) {
            plugin.nametagManager.nicknamePlayer(player, character.getName());
        }
    }
}
