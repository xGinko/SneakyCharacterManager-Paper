package net.sneakymouse.sneakycharactermanager.listeners;

import net.sneakymouse.sneakycharactermanager.SneakyCharacterManagerPaper;
import net.sneakymouse.sneakycharactermanager.handlers.character.Character;
import net.sneakymouse.sneakycharactermanager.struct.Toggleable;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;

public class GameModeListener implements Toggleable, Listener {

    private final SneakyCharacterManagerPaper plugin;

    public GameModeListener() {
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
    public void onPlayerChangeGameMode(PlayerGameModeChangeEvent event){
        Player player = event.getPlayer();

        if (event.getNewGameMode() == GameMode.SPECTATOR) {
            plugin.nametagManager.unnicknamePlayer(player);
            return;
        }

        plugin.nametagManager.unnicknamePlayer(player);
        Character character = Character.get(player);
        if (character != null) {
            player.getScheduler().runDelayed(plugin, applyNick -> plugin.nametagManager.nicknamePlayer(player, character.getName()), null, 2L);
        }
    }
}
