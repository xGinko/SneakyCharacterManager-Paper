package net.sneakymouse.sneakycharactermanager;

import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.sneakymouse.sneakycharactermanager.commands.admincommands.CommandCharAdmin;
import net.sneakymouse.sneakycharactermanager.commands.admincommands.CommandCharScan;
import net.sneakymouse.sneakycharactermanager.commands.admincommands.CommandCharTag;
import net.sneakymouse.sneakycharactermanager.commands.admincommands.CommandSaveTemplateChar;
import net.sneakymouse.sneakycharactermanager.commands.admincommands.CommandUniform;
import net.sneakymouse.sneakycharactermanager.commands.admincommands.CommandUserify;
import net.sneakymouse.sneakycharactermanager.commands.consolecommands.ConsoleCommandCharDisable;
import net.sneakymouse.sneakycharactermanager.commands.consolecommands.ConsoleCommandCharEnable;
import net.sneakymouse.sneakycharactermanager.commands.consolecommands.ConsoleCommandCharTemp;
import net.sneakymouse.sneakycharactermanager.commands.usercommands.CommandChar;
import net.sneakymouse.sneakycharactermanager.commands.usercommands.CommandNames;
import net.sneakymouse.sneakycharactermanager.commands.usercommands.CommandNick;
import net.sneakymouse.sneakycharactermanager.commands.usercommands.CommandSkin;
import net.sneakymouse.sneakycharactermanager.handlers.ContextCalculatorCharacterTag;
import net.sneakymouse.sneakycharactermanager.handlers.Placeholders;
import net.sneakymouse.sneakycharactermanager.handlers.character.Character;
import net.sneakymouse.sneakycharactermanager.handlers.character.CharacterSelectionMenu;
import net.sneakymouse.sneakycharactermanager.handlers.nametags.NameTagRefresher;
import net.sneakymouse.sneakycharactermanager.handlers.nametags.NametagManager;
import net.sneakymouse.sneakycharactermanager.handlers.skins.SkinPreloader;
import net.sneakymouse.sneakycharactermanager.handlers.skins.SkinQueue;
import net.sneakymouse.sneakycharactermanager.listeners.BungeeMessageListener;
import net.sneakymouse.sneakycharactermanager.listeners.DeathListener;
import net.sneakymouse.sneakycharactermanager.listeners.GameModeListener;
import net.sneakymouse.sneakycharactermanager.listeners.JoinLeaveListener;
import net.sneakymouse.sneakycharactermanager.listeners.TeleportListener;
import net.sneakymouse.sneakycharactermanager.listeners.VanishListener;
import net.sneakymouse.sneakycharactermanager.util.BungeeMessagingUtil;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class SneakyCharacterManagerPaper extends JavaPlugin {

    public static final String IDENTIFIER = "sneakycharacters";
    private static final Map<Player, Integer> taskIdMap = new HashMap<>();

    public NametagManager nametagManager;
    public CharacterSelectionMenu selectionMenu;
    public SkinQueue skinQueue;
    public SkinPreloader skinPreloader;
    public NameTagRefresher nameTagRefresher;

    private static SneakyCharacterManagerPaper instance;
    private static ComponentLogger logger;
    private static boolean papiActive = false;

    @Override
    public void onEnable() {
        instance = this;
        logger = getComponentLogger();
        nametagManager = new NametagManager();
        selectionMenu = new CharacterSelectionMenu();
        skinQueue = new SkinQueue();
        skinPreloader = new SkinPreloader();
        nameTagRefresher = new NameTagRefresher();

        saveDefaultConfig();

        if (getConfig().getBoolean("deleteCharacterDataOnServerStart", false)) {
            deleteFolderContents(getCharDataFolder());
        }

        getServer().getCommandMap().register(IDENTIFIER, new CommandChar());
        getServer().getCommandMap().register(IDENTIFIER, new CommandSkin());
        getServer().getCommandMap().register(IDENTIFIER, new CommandNames());
        getServer().getCommandMap().register(IDENTIFIER, new CommandNick());

        getServer().getCommandMap().register(IDENTIFIER, new CommandCharAdmin());
        getServer().getCommandMap().register(IDENTIFIER, new CommandCharScan());
        getServer().getCommandMap().register(IDENTIFIER, new CommandCharTag());
        getServer().getCommandMap().register(IDENTIFIER, new CommandUniform());
        getServer().getCommandMap().register(IDENTIFIER, new CommandSaveTemplateChar());
        getServer().getCommandMap().register(IDENTIFIER, new CommandUserify());

        getServer().getCommandMap().register(IDENTIFIER, new ConsoleCommandCharDisable());
        getServer().getCommandMap().register(IDENTIFIER, new ConsoleCommandCharEnable());
        getServer().getCommandMap().register(IDENTIFIER, new ConsoleCommandCharTemp());

        getServer().getMessenger().registerIncomingPluginChannel(this, "sneakymouse:" + IDENTIFIER, new BungeeMessageListener());
        getServer().getMessenger().registerOutgoingPluginChannel(this, "sneakymouse:" + IDENTIFIER);

        getServer().getPluginManager().registerEvents(selectionMenu, this);
        for (Toggleable listener : new Toggleable[] {
                new JoinLeaveListener(),
                new DeathListener(),
                new GameModeListener(),
                new VanishListener(),
                // new GSitListener(),
                new TeleportListener() }
        ) {
            listener.enable();
        }

        getServer().getPluginManager().addPermission(new Permission(IDENTIFIER + ".*"));
        getServer().getPluginManager().addPermission(new Permission(CharacterSelectionMenu.CHARACTER_SLOTS_PERMISSION_NODE + "*"));
        getServer().getPluginManager().addPermission(new Permission(IDENTIFIER + ".character.*"));
        getServer().getPluginManager().addPermission(new Permission(IDENTIFIER + ".command.*"));
        getServer().getPluginManager().addPermission(new Permission(IDENTIFIER + ".admin.*"));
        getServer().getPluginManager().addPermission(new Permission(IDENTIFIER + ".admin.command.*"));
        getServer().getPluginManager().addPermission(new Permission(IDENTIFIER + ".admin.bypass.*"));

        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            papiActive = true;
            new Placeholders().register();
        }

        if (getServer().getPluginManager().getPlugin("LuckPerms") != null) {
            new ContextCalculatorCharacterTag().register();
        }

        for (Player player : getServer().getOnlinePlayers()) {
            int taskId = getServer().getScheduler().scheduleSyncRepeatingTask(this, () -> {
                if (!player.isOnline() || Character.isPlayerMapped(player)) {
                    getServer().getScheduler().cancelTask(taskIdMap.get(player));
                    taskIdMap.remove(player);
                } else {
                    BungeeMessagingUtil.sendByteArray(player, "playerJoin", player.getUniqueId().toString());
                }
            }, 0, 20);
        
            taskIdMap.put(player, taskId);
        }

        if(getConfig().getBoolean("respawnNameTags", false)){
            int respawnTimer = getConfig().getInt("respawnTimerSeconds", 600);
            respawnTimer = respawnTimer*20;

            getServer().getScheduler().scheduleSyncRepeatingTask(this, ()->{
                for(Player player : getServer().getOnlinePlayers()){
                    Character character = Character.get(player);
                    if(character == null) continue;

                    nametagManager.unnicknamePlayer(player);
                    nametagManager.nicknamePlayer(player, character.getName());
                }
            }, respawnTimer, respawnTimer);
        }

        getServer().getScheduler().scheduleSyncRepeatingTask(this, Character::saveAll, 0, 1200);
    }

    @Override
    public void onDisable() {
        Character.saveAll();
        this.nametagManager.unnickAll();

        getServer().getScheduler().cancelTasks(this);
        getServer().getAsyncScheduler().cancelTasks(this);

        this.skinQueue.stop();
        this.nameTagRefresher.stop();
    }

    public static SneakyCharacterManagerPaper getInstance() {
        return instance;
    }

    public static ComponentLogger logger() {
        return logger;
    }

    public static boolean isPapiActive() {
        return papiActive;
    }

    public static File getCharDataFolder() {
        File dir = new File(getInstance().getDataFolder(), "characterdata");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        return dir;
    }

    public static File getUniformFolder() {
        File dir = new File(getInstance().getDataFolder(), "uniforms");

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