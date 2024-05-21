package net.sneakycharactermanager.paper.listeners;

import dev.geco.gsit.api.event.PlayerGetUpPoseEvent;
import dev.geco.gsit.api.event.PlayerPoseEvent;
import net.sneakycharactermanager.paper.SneakyCharacterManager;
import net.sneakycharactermanager.paper.handlers.character.Character;
import net.sneakycharactermanager.paper.struct.Toggleable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public class GSitListener implements Toggleable, Listener {

    private final SneakyCharacterManager plugin;

    public GSitListener() {
        this.plugin = SneakyCharacterManager.getInstance();
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
    public void onPlayerPose(PlayerPoseEvent event) {
        Player player = event.getPlayer();
        Character character = Character.get(player);
        if (character != null) {
            plugin.nametagManager.unnicknamePlayer(player);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerGetUpFromPose(PlayerGetUpPoseEvent event){
        Player player = event.getPlayer();
        Character character = Character.get(player);
        if (character != null) {
            plugin.nametagManager.unnicknamePlayer(player);
            plugin.nametagManager.nicknamePlayer(player, character.getName());
        }
    }
}
