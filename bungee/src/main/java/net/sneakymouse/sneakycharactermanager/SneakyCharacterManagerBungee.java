package net.sneakymouse.sneakycharactermanager;

import net.md_5.bungee.api.plugin.Plugin;
import net.sneakymouse.sneakycharactermanager.listeners.ConnectionEventListeners;
import net.sneakymouse.sneakycharactermanager.listeners.PluginMessageListener;

import java.io.File;

public class SneakyCharacterManagerBungee extends Plugin {

    public static final String IDENTIFIER = "sneakycharacters";

    private static SneakyCharacterManagerBungee instance;

    @Override
    public void onEnable() {
        instance = this;

        getProxy().registerChannel("sneakymouse:" + IDENTIFIER);
        getProxy().getPluginManager().registerListener(this, new PluginMessageListener());
        getProxy().getPluginManager().registerListener(this, new ConnectionEventListeners());
    }

    public static SneakyCharacterManagerBungee getInstance() {
        return instance;
    }

    public static File getCharacterDataFolder() {
        File dir = new File(instance.getDataFolder(), "characterdata");

        if (!dir.exists()) {
            dir.mkdirs();
        }

        return dir;
    }
}