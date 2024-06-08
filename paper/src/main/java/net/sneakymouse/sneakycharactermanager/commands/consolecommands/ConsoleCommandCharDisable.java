package net.sneakymouse.sneakycharactermanager.commands.consolecommands;

import net.sneakymouse.sneakycharactermanager.SneakyCharacterManagerPaper;
import net.sneakymouse.sneakycharactermanager.handlers.character.Character;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ConsoleCommandCharDisable extends CommandBaseConsole {

    private static List<String> disabledPlayerUUIDs = new ArrayList<>();

    public ConsoleCommandCharDisable() {
        super("chardisable");
        this.description = "The given player is locked out of their character save file. Use this when a player is temporarily acting as something that isn't their own character. The player's status will be restores on server reboot or when they load a character.";
        this.setUsage("chardisable <Player>)");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (sender instanceof Player) {
            sender.sendMessage("This command can only be run from the console.");
            return false;
        }

        if (args.length != 1) {
            sender.sendMessage("Invalid Usage: " + this.getUsage());
            return false;
        }

        Player player = Bukkit.getPlayer(args[0]);

        if (player == null) {
            sender.sendMessage("Invalid Player: '" + args[0] + "'");
            return false;
        }

        Character character = Character.get(player);
        if (character != null) character.save();

        Character.remove(player);
        SneakyCharacterManagerPaper.getInstance().nametagManager.unnicknamePlayer(player);

        disabledPlayerUUIDs.add(player.getUniqueId().toString());

        sender.sendMessage("The player '" + args[0] + "' is no longer on a character.");

        return true;
    }

    public static boolean isPlayerCharDisabled(String playerUUID) {
        return disabledPlayerUUIDs.contains(playerUUID);
    }

    public static void playerCharEnable(String playerUUID) {
        disabledPlayerUUIDs.remove(playerUUID);
    }
    
}
