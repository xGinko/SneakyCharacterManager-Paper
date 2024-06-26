package net.sneakymouse.sneakycharactermanager;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.config.Configuration;
import net.sneakymouse.sneakycharactermanager.util.PaperMessagingUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Character {
    
    private final String uuid;
    private boolean enabled;
    private String name;
    private String skin;
    private boolean slim;
    private List<String> tags = new ArrayList<>();

    public Character(String uuid, boolean enabled, String name, String skin, boolean slim, List<String> tags) {
        this.uuid = uuid;
        this.enabled = enabled;
        this.name = name;
        this.skin = skin;
        this.slim = slim;
        this.tags = tags;
    }

    public Character(String uuid, String name, String skin, boolean slim) {
        this(
            uuid,
        true,
            name,
            skin,
            slim,
            new ArrayList<>()
        );
    }

    public Character(String uuid, Configuration config) {
        this(
            uuid,
            config.getBoolean("enabled"),
            config.getString("name"),
            config.getString("skin"),
            config.getBoolean("slim"),
            config.getStringList("tags")
        );
    }

    public void loadCharacter(ServerInfo serverInfo, String playerUUID, boolean forced) {
        PaperMessagingUtil.sendByteArray(serverInfo, "loadCharacter", playerUUID, this, forced);
        if (this.skin.isEmpty()) {
            PaperMessagingUtil.sendByteArray(serverInfo, "defaultSkin", playerUUID, this.uuid);
        }
    }

    public void loadTempCharacter(ServerInfo serverInfo, String requesterUUID, String sourceUUID) {
        PaperMessagingUtil.sendByteArray(serverInfo, "loadTempCharacter", requesterUUID, this, sourceUUID);
    }

    public String getUUID() {
        return this.uuid;
    }
    
    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getName() {
        return this.name;
    }

    public String getNameUnformatted() {
        Pattern pattern = Pattern.compile("<[^>]*>|&[0-9A-FK-ORa-fk-or]");
        Matcher matcher = pattern.matcher(this.name);
        return matcher.replaceAll("");
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSkin() {
        return this.skin;
    }

    public void setSkin(String skin) {
        this.skin = skin;
    }

    public boolean isSlim() {
        return slim;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public void setSlim(boolean slim) {
        this.slim = slim;
    }
    
}
