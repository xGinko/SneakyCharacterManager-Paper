package net.sneakycharactermanager.paper;

import java.io.File;

import net.sneakycharactermanager.paper.commands.CommandTesting;
import net.sneakycharactermanager.paper.handlers.nametags.NametagManager;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.java.JavaPlugin;

import net.sneakycharactermanager.paper.commands.CommandChar;
import net.sneakycharactermanager.paper.commands.CommandSkin;
import net.sneakycharactermanager.paper.listeners.BungeeMessageListener;
import net.sneakycharactermanager.paper.listeners.ConnectionEventListeners;
import net.sneakycharactermanager.paper.util.BungeeMessagingUtil;

public class SneakyCharacterManager extends JavaPlugin implements Listener {

    private static SneakyCharacterManager instance = null;

    public NametagManager nametagManager;

    @Override
    public void onEnable() {
        instance = this;
        nametagManager = new NametagManager();

        saveDefaultConfig();

        if (getConfig().getBoolean("deleteCharacterDataOnServerStart")) {
            deleteFolderContents(getCharacterDataFolder());
        }

        getServer().getCommandMap().register("sneakycharactermanager", new CommandChar());
        getServer().getCommandMap().register("sneakycharactermanager", new CommandSkin());
        getServer().getCommandMap().register("sneakycharactermanager", new CommandTesting());

        getServer().getMessenger().registerIncomingPluginChannel(this, "sneakymouse:sneakycharactermanager", new BungeeMessageListener());
        getServer().getMessenger().registerOutgoingPluginChannel(this, "sneakymouse:sneakycharactermanager");

        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new ConnectionEventListeners(), this);

        BungeeMessagingUtil.sendByteArray("pluginEnabled");
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        if (event.getPlugin() == this) BungeeMessagingUtil.sendByteArray("pluginDisabled");
    }

    public static SneakyCharacterManager getInstance() {
        return instance;
    }

    private static File getCharacterDataFolder() {
        File dir = new File(SneakyCharacterManager.getInstance().getServer().getWorldContainer(), "characterdata");

        if (!dir.exists()) {
            dir.mkdirs();
        }

        return dir;
    }

    private static void deleteFolderContents(File folder) {
        if (folder.isDirectory()) {
            for (File file : folder.listFiles()) {
                if (file.isDirectory()) {
                    deleteFolderContents(file);
                }
                file.delete();
            }
        }
    }

}