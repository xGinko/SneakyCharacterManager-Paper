package net.sneakycharactermanager.bungee;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.bukkit.configuration.ConfigurationSection;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.sneakycharactermanager.bungee.util.PaperMessagingUtil;

public class PlayerData {

    private static final Map<String, PlayerData> playerDataMap = new HashMap<String, PlayerData>();
    private static final ConfigurationProvider provider = ConfigurationProvider.getProvider(YamlConfiguration.class);

    private final String playerUUID;
    private Configuration config;
    private final File playerFile;
    private String lastPlayedCharacter;
    private final Map<String, Character> characterMap = new HashMap<String, Character>();

    public PlayerData(String playerUUID) {
        this.playerUUID = playerUUID;
        playerFile = new File(SneakyCharacterManager.getCharacterDataFolder(), playerUUID + ".yml");


        //Loading YML Configuration
        try {
            if(!playerFile.exists()){
                if(!playerFile.createNewFile()){
                    throw new IOException("Failed to create player data file!");
                }
            }
        } catch(IOException e){
            e.printStackTrace();
        }

        loadConfig();

        this.lastPlayedCharacter = this.config.getString("lastPlayedCharacter");

        if (this.lastPlayedCharacter == null) {
            String firstCharacterUUID = UUID.randomUUID().toString();
            this.config.set("lastPlayedCharacter", firstCharacterUUID);

            Configuration section = this.config.getSection(firstCharacterUUID);

            section.set("enabled", true);
            section.set("name", ProxyServer.getInstance().getPlayer(UUID.fromString(playerUUID)).getName());
            section.set("skin", "");
            saveConfig();
        }

        for(String key : this.config.getKeys()){
            if(key.equalsIgnoreCase("lastPlayedCharacter")) continue;

            Character character = new Character(key, this.config.getSection(key));
            this.characterMap.put(key, character);
        }
    }

    private void loadConfig(){
        try {
            this.config = provider.load(playerFile);
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    private  void saveConfig(){
        try{
            provider.save(this.config, playerFile);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void loadCharacter(ServerInfo serverInfo, String characterUUID) {
        Character character = this.characterMap.get(characterUUID);
        loadConfig();

        if (character == null) {
            SneakyCharacterManager.getInstance().getLogger().severe("An attempt was made to load a character that does not exist! [" + this.playerUUID + ", " + characterUUID + "]");
            return;
        } else {
            character.loadCharacter("loadCharacter", serverInfo, this.playerUUID);
        }

        //Not sure if I misunderstood this, but I'm assuming this is intended to update the last played character if it has changed?
        if (!this.lastPlayedCharacter.equals(characterUUID)) {
            this.lastPlayedCharacter = characterUUID;

            this.config.set("lastPlayedCharacter", character);
            saveConfig();
        }
    }

    public void loadLastPlayedCharacter(ServerInfo serverInfo) {
        this.loadCharacter(serverInfo, this.lastPlayedCharacter);
    }

    public static PlayerData get(String playerUUID) {
        if (playerDataMap.containsKey(playerUUID)) {
            return playerDataMap.get(playerUUID);
        } else {
            PlayerData playerData = new PlayerData(playerUUID);
            playerDataMap.put(playerUUID, playerData);
            return playerData;
        }
    }

    public void createNewCharacter(String name, String skin) {
        loadConfig();
        Character character = new Character(name, skin);

        this.characterMap.put(character.getUUID(), character);

        Map<String, Object> characterData = new LinkedHashMap<>();
        characterData.put("enabled", character.isEnabled());
        characterData.put("name", character.getName());
        characterData.put("skin", character.getSkin());

        Configuration section = this.config.getSection(character.getUUID());
        section.set("enabled", character.isEnabled());
        section.set("name", character.getName());
        section.set("skin", character.getSkin());

        saveConfig();

    }

    private void updateCharacterInYaml(Character character) {
        loadConfig();
        Configuration section = this.config.getSection(character.getUUID());

        section.set("enabled", character.isEnabled());
        section.set("name", character.getName());
        section.set("skin", character.getSkin());

        saveConfig();
    }

    public void setCharacterEnabled(String characterUUID, boolean enabled) {
        Character character = this.characterMap.get(characterUUID);

        if (character == null) {
            SneakyCharacterManager.getInstance().getLogger().severe("An attempt was made to enable/disable a character that does not exist! [" + this.playerUUID + ", " + characterUUID + "]");
        } else {
            character.setEnabled(enabled);
            this.characterMap.put(characterUUID, character);
            this.updateCharacterInYaml(character);
        }
    }

    public void setCharacterName(String characterUUID, String name) {
        Character character = this.characterMap.get(characterUUID);

        if (character == null) {
            SneakyCharacterManager.getInstance().getLogger().severe("An attempt was made to rename a character that does not exist! [" + this.playerUUID + ", " + characterUUID + "]");
        } else {
            character.setName(name);
            this.characterMap.put(characterUUID, character);
            this.updateCharacterInYaml(character);
        }
    }

    public void setCharacterSkin(String characterUUID, String skin) {
        Character character = this.characterMap.get(characterUUID);

        if (character == null) {
            SneakyCharacterManager.getInstance().getLogger().severe("An attempt was made to reskin a character that does not exist! [" + this.playerUUID + ", " + characterUUID + "]");
        } else {
            character.setSkin(skin);
            this.characterMap.put(characterUUID, character);
            this.updateCharacterInYaml(character);
        }
    }

    public void sendCharacterSelectionGui(ServerInfo serverInfo) {
        List<Character> enabledCharacters = new ArrayList<Character>();

        for (Character character: characterMap.values()) {
            if (character.isEnabled()) enabledCharacters.add(character);
        }

        PaperMessagingUtil.sendByteArray(serverInfo, "characterSelectionGUI", this.playerUUID, enabledCharacters);
    }

    public String getLastPlayedCharacter() {
        return lastPlayedCharacter;
    }

    public void rebuildCharacterMap(ServerInfo serverInfo) {
        Character character = this.characterMap.get(this.lastPlayedCharacter);

        if (character == null) {
            SneakyCharacterManager.getInstance().getLogger().severe("An attempt was made to load a character that does not exist! [" + this.playerUUID + ", " + this.lastPlayedCharacter + "]");
        } else {
            character.loadCharacter("rebuildCharacterMap", serverInfo, this.playerUUID);
        }
    }

}
