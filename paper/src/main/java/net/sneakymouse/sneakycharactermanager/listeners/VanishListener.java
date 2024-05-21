package net.sneakymouse.sneakycharactermanager.listeners;

import net.sneakymouse.sneakycharactermanager.SneakyCharacterManagerPaper;
import net.sneakymouse.sneakycharactermanager.handlers.character.Character;
import net.sneakymouse.sneakycharactermanager.struct.Toggleable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerHideEntityEvent;
import org.bukkit.event.player.PlayerShowEntityEvent;

public class VanishListener implements Toggleable, Listener {

    private final SneakyCharacterManagerPaper plugin;

    public VanishListener() {
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

    @SuppressWarnings("UnstableApiUsage")
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerHideEntity(PlayerHideEntityEvent event) {
        if (event.getEntity().getType() == EntityType.PLAYER) {
            plugin.nametagManager.unnicknamePlayer((Player) event.getEntity());
        }
    }

    @SuppressWarnings("UnstableApiUsage")
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerShowEntity(PlayerShowEntityEvent event) {
        if (event.getEntity().getType() != EntityType.PLAYER) return;
        final Player unVanishingPlayer = (Player) event.getEntity();

        plugin.nametagManager.unnicknamePlayer(unVanishingPlayer);

        unVanishingPlayer.getScheduler().runDelayed(plugin, reNick -> {
            Character character = Character.get(unVanishingPlayer);
            if (character != null) plugin.nametagManager.nicknamePlayer(unVanishingPlayer, character.getName());
        }, null,2L);
    }
}
