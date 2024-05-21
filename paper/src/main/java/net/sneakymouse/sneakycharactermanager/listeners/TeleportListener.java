package net.sneakymouse.sneakycharactermanager.listeners;

import net.sneakymouse.sneakycharactermanager.SneakyCharacterManagerPaper;
import net.sneakymouse.sneakycharactermanager.handlers.character.Character;
import net.sneakymouse.sneakycharactermanager.struct.Toggleable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class TeleportListener implements Toggleable, Listener {

    private final SneakyCharacterManagerPaper plugin;

    public TeleportListener() {
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
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        final Player player = event.getPlayer();

        plugin.nametagManager.unnicknamePlayer(player);

        player.getScheduler().runDelayed(plugin, reNick -> {
            Character character = Character.get(player);
            if (character != null) plugin.nametagManager.nicknamePlayer(player, character.getName());
        }, null, 2L);
    }
}
