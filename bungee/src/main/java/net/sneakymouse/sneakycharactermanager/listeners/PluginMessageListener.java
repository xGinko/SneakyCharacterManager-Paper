package net.sneakymouse.sneakycharactermanager.listeners;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.sneakymouse.sneakycharactermanager.Character;
import net.sneakymouse.sneakycharactermanager.PlayerData;
import net.sneakymouse.sneakycharactermanager.SneakyCharacterManagerBungee;
import net.sneakymouse.sneakycharactermanager.util.PaperMessagingUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PluginMessageListener implements Listener {

    private List<String> handledRequests;
    public PluginMessageListener(){
        handledRequests = new ArrayList<>();
    }

    @EventHandler
    public void on(PluginMessageEvent event) {
        if (!event.getTag().equalsIgnoreCase("sneakymouse:" + SneakyCharacterManagerBungee.IDENTIFIER)) {
            return;
        }

        Connection connection = event.getSender();
        ServerInfo serverInfo = null;
        ProxiedPlayer player = null;
        if (connection instanceof ProxiedPlayer proxiedPlayer) {
            serverInfo = proxiedPlayer.getServer().getInfo();
            player = proxiedPlayer;
        } else if (connection instanceof Server server) {
            serverInfo = server.getInfo();
        }

        ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());
        String channelData = in.readUTF();

        String[] _data = channelData.split("_UUID:");

        String subChannel = _data[0];
        String uuid = _data[1];

        if(handledRequests.contains(uuid)){
            SneakyCharacterManagerBungee.getInstance().getLogger().warning("Received duplicate message! Ignoring");
            return;
        }

        handledRequests.add(uuid);

        switch (subChannel) {
            case "playerJoin" :
                String playerUUID = in.readUTF();
                PlayerData playerData = PlayerData.get(playerUUID);
                playerData.loadLastPlayedCharacter(serverInfo);
                break;
            case "playerQuit" :
                playerUUID = in.readUTF();
                PlayerData.remove(playerUUID);
                break;
            case "characterSelectionGUI" :
                playerUUID = in.readUTF();
                playerData = PlayerData.get(playerUUID);
                String requesterUUID = in.readUTF();
                playerData.sendEnabledCharacters(serverInfo, subChannel, requesterUUID);
                break;
            case "preloadSkins" :
                playerUUID = in.readUTF();
                playerData = PlayerData.get(playerUUID);
                playerData.sendEnabledCharacters(serverInfo, subChannel, playerUUID);
                break;
            case "selectCharacter" :
                playerUUID = in.readUTF();
                playerData = PlayerData.get(playerUUID);
                playerData.loadCharacter(serverInfo, in.readUTF(), false);
                break;
            case "tempCharacter" :
                requesterUUID = in.readUTF();
                playerUUID = in.readUTF();
                playerData = PlayerData.get(playerUUID);
                playerData.loadTempCharacter(serverInfo, requesterUUID, in.readUTF());
                break;
            case "selectCharacterByName" :
                playerUUID = in.readUTF();
                playerData = PlayerData.get(playerUUID);
                playerData.loadCharacterByName(serverInfo, in.readUTF());
                break;
            case "updateCharacter" :
                playerUUID = in.readUTF();
                playerData = PlayerData.get(playerUUID);
                String lastPlayed = playerData.getLastPlayedCharacter();
                int type = in.readInt();
                switch (type) {
                    case 1: //Updating Skin
                        playerData.setCharacterSkin(lastPlayed, in.readUTF(), in.readBoolean());
                        break;
                    case 2: //Updating Name
                        playerData.setCharacterName(lastPlayed, in.readUTF());
                        playerData.updateCharacterList(serverInfo);
                        break;
                    case 3: //Updating enabled
                        // This case is here for consistency only. A player should never be able to change the enabled state of their current character, so it does nothing.
                        break;
                    case 4: //Updating Tags
                        playerData.setCharacterTags(lastPlayed, readStringList(in));
                        break;
                }
                break;
            case "defaultSkin":
                playerUUID = in.readUTF();
                String characterUUID = in.readUTF();
                String url = in.readUTF();
                boolean slim = in.readBoolean();
                playerData = PlayerData.get(playerUUID);
                playerData.setCharacterSkin(characterUUID, url, slim);
                break;
            case "createNewCharacter" :
                playerUUID = in.readUTF();
                playerData = PlayerData.get(playerUUID);
                characterUUID = playerData.createNewCharacter(ProxyServer.getInstance().getPlayer(UUID.fromString(playerUUID)).getName());
                if (characterUUID != null) playerData.loadCharacter(serverInfo, characterUUID, true);
                break;
            case "deleteCharacter" :
                playerUUID = in.readUTF();
                characterUUID = in.readUTF();
                playerData = PlayerData.get(playerUUID);

                Character character = playerData.getCharacter(characterUUID);

                playerData.setCharacterEnabled(characterUUID, false);
                PaperMessagingUtil.sendByteArray(serverInfo, "deleteConfirmed", playerUUID, character.getName(), characterUUID);
                playerData.updateCharacterList(serverInfo);
                break;
            case "saveTemplateChar" :
                String characterID = in.readUTF();
                String characterName = in.readUTF();
                String characterSkin = in.readUTF();
                boolean characterSlim = in.readBoolean();

                playerData = PlayerData.get("template");

                playerData.createNewCharacter(characterID, characterName, characterSkin, characterSlim);
                
                break;

            case "getAllCharacters":
                requesterUUID = in.readUTF();
                String filter = in.readUTF();
                try {
                    List<String> data = PlayerData.getAllCharacters(filter);
                    PaperMessagingUtil.sendByteArray(serverInfo, "getAllCharacters", requesterUUID, data);
                } catch(IOException e) { break; }
                break;
            default:
                SneakyCharacterManagerBungee.getInstance().getLogger().severe("SneakyCharacterManagerBungee received a packet but the subchannel was unknown: " + subChannel);
                break;
        }
    }

    public static List<String> readStringList(ByteArrayDataInput in) {
        int size = in.readInt();

        List<String> strings = new ArrayList<>();
        while (strings.size() < size) {
            strings.add(in.readUTF());
        }

        return strings;
    }
    
}