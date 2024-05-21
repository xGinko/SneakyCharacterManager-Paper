package net.sneakymouse.sneakycharactermanager.listeners;

import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.sneakymouse.sneakycharactermanager.PlayerData;

public class ConnectionEventListeners implements Listener {

    @EventHandler
    public void onPlayerDisconnect(PlayerDisconnectEvent event) {
        PlayerData.remove(event.getPlayer().getUniqueId().toString());
    }
    
}
