package net.sneakycharactermanager.paper.listeners;

import net.sneakycharactermanager.paper.SneakyCharacterManager;
import net.sneakycharactermanager.paper.commands.CommandChar;
import net.sneakycharactermanager.paper.consolecommands.ConsoleCommandCharDisable;
import net.sneakycharactermanager.paper.consolecommands.ConsoleCommandCharTemp;
import net.sneakycharactermanager.paper.handlers.character.Character;
import net.sneakycharactermanager.paper.handlers.skins.SkinCache;
import net.sneakycharactermanager.paper.struct.Toggleable;
import net.sneakycharactermanager.paper.util.BungeeMessagingUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinLeaveListener implements Toggleable, Listener {

    private final SneakyCharacterManager plugin;

    public JoinLeaveListener() {
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
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // We need to load other players names when a person joins
        plugin.nametagManager.loadNames(player);

        player.getScheduler().runDelayed(plugin, preloadAndInitMap -> {
            plugin.skinPreloader.preload(player);

            if (ConsoleCommandCharDisable.isPlayerCharDisabled(player.getUniqueId().toString())) {
                return;
            }

            player.getScheduler().runAtFixedRate(plugin, mapPlayer -> {
                if (Character.isPlayerMapped(player)) {
                    mapPlayer.cancel();
                    return;
                }

                if (ConsoleCommandCharTemp.isPlayerTempChar(player.getUniqueId().toString())) {
                    ConsoleCommandCharTemp.reApply(player);
                } else {
                    BungeeMessagingUtil.sendByteArray(player, "playerJoin", player.getUniqueId().toString());
                }
            }, null, 1, 20);
        }, null, 5L);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        // Un-Nick player who is disconnecting from the server
        plugin.nametagManager.unnicknamePlayer(player);

        CommandChar.tabCompleteMap.remove(player.getUniqueId().toString());
        SkinCache.remove(player.getUniqueId().toString());
        plugin.skinPreloader.preLoadedPlayers.remove(player);

        Character character = Character.get(player);

        if (character == null) {
            if (!ConsoleCommandCharDisable.isPlayerCharDisabled(player.getUniqueId().toString()))
                SneakyCharacterManager.logger().error("{} quit the game but wasn't a character. This should never happen.", player.getName());
        } else {
            character.save();
        }

        Character.remove(player);
    }
}
