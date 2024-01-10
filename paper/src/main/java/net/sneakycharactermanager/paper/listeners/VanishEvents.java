package net.sneakycharactermanager.paper.listeners;

import net.sneakycharactermanager.paper.SneakyCharacterManager;
import net.sneakycharactermanager.paper.handlers.character.Character;

import java.util.List;
import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerHideEntityEvent;
import org.bukkit.event.player.PlayerShowEntityEvent;

public class VanishEvents implements Listener {

    public static List<Player> vanishedPlayers = new ArrayList<>();

    @EventHandler
    public void onVanish(PlayerHideEntityEvent event){
        if(!(event.getEntity() instanceof Player hidden)) return;
        SneakyCharacterManager.getInstance().nametagManager.unnicknamePlayer(hidden);
        if (!vanishedPlayers.contains(hidden)) vanishedPlayers.add(hidden);
    }

    @EventHandler
    public void onVanish(PlayerShowEntityEvent event){
        if(!(event.getEntity() instanceof Player hidden)) return;
        Character character = Character.get(hidden);
        if(character == null) return;
        SneakyCharacterManager.getInstance().nametagManager.unnicknamePlayer(hidden);
        SneakyCharacterManager.getInstance().nametagManager.nicknamePlayer(hidden, character.getName());
        vanishedPlayers.remove(hidden);
    }

}
