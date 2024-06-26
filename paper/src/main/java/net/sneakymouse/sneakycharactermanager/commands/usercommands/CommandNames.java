package net.sneakymouse.sneakycharactermanager.commands.usercommands;

import net.sneakymouse.sneakycharactermanager.SneakyCharacterManagerPaper;
import net.sneakymouse.sneakycharactermanager.util.ChatUtility;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CommandNames extends CommandBase {

    public CommandNames() {
        super("names");
        this.description = "Change player Nickname settings. ON: Show nametags & player names, OFF: Hide Name Tags, Character: Show nicknames hide real names";
        this.usageMessage = "/names [on/off/character]";
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatUtility.convertToComponent("&4Must be a player to run this command"));
            return false;
        }

        if (args.length != 1) {
            player.sendMessage(ChatUtility.convertToComponent("&4Invalid Usage: " + this.usageMessage));
            return false;
        }

        if (args[0].equalsIgnoreCase("on")) {
            SneakyCharacterManagerPaper.getInstance().nametagManager.hideNames(player, false);
            SneakyCharacterManagerPaper.getInstance().nametagManager.createLocalized(player, true);
            player.sendMessage(ChatUtility.convertToComponent("&eNow showing nicknames & real names!"));
        }
        else if (args[0].equalsIgnoreCase("off")) {
            SneakyCharacterManagerPaper.getInstance().nametagManager.hideNames(player, true);
            player.sendMessage(ChatUtility.convertToComponent("&eNow hiding names!"));
        }
        else if (args[0].equalsIgnoreCase("character")) {
            SneakyCharacterManagerPaper.getInstance().nametagManager.hideNames(player, false);
            SneakyCharacterManagerPaper.getInstance().nametagManager.createLocalized(player, false);
            player.sendMessage(ChatUtility.convertToComponent("&eNow showing nicknames & Hiding real names!"));
        }
        else {
            player.sendMessage(ChatUtility.convertToComponent("&4Unknown argument: " + this.usageMessage));
            return false;
        }

        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        if(args.length == 1){
            return List.of("on", "off", "character");
        } else{
            return List.of();
        }
    }
}
