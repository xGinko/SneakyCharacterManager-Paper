package net.sneakymouse.sneakycharactermanager.commands.admincommands;

import net.sneakymouse.sneakycharactermanager.SneakyCharacterManagerPaper;
import net.sneakymouse.sneakycharactermanager.util.ChatUtility;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CommandCharAdmin extends CommandBaseAdmin {

    public CommandCharAdmin() {
        super("charadmin");
        this.description = "Switch between your different characters!";
        this.setUsage("/charadmin [playerName]");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!(sender instanceof Player admin)) {
            sender.sendMessage(ChatUtility.convertToComponent("&4Must be a player to run this command"));
            return false;
        }

        if (args.length != 1) {
            admin.sendMessage(ChatUtility.convertToComponent("&4Invalid Usage: " + this.usageMessage));
            return false;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            admin.sendMessage(ChatUtility.convertToComponent("&aUnknown player: &b" + args[0]));
            return true;
        }

        CompletableFuture.supplyAsync(() -> new File(SneakyCharacterManagerPaper.getCharacterDataFolder(), target.getUniqueId().toString()).exists())
                .thenAccept(playerFolderExists -> {
                    if (!playerFolderExists) {
                        admin.sendMessage(ChatUtility.convertToComponent("&aUnknown player: &b" + args[0]));
                        return;
                    }

                    admin.sendMessage(ChatUtility.convertToComponent("&aLoading character admin menu for player: &b" + args[0]));
                    SneakyCharacterManagerPaper plugin = SneakyCharacterManagerPaper.getInstance();
                    target.getScheduler().execute(plugin, () -> plugin.selectionMenu.openAdminMenu(target, admin), null, 1L);
                });
        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, String[] args, Location location) {
        if (args.length != 1) {
            return Collections.emptyList();
        }

        return Arrays.stream(Bukkit.getOfflinePlayers()) // Using getOfflinePlayers is VERY intense. This will need to be cached.
                .map(OfflinePlayer::getName)
                .filter(name -> name != null && name.toLowerCase().startsWith(args[0].toLowerCase()) && !name.contains("CMI-Fake-Operator"))
                .toList();
    }
}
