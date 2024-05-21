package net.sneakymouse.sneakycharactermanager.admincommands;

import net.sneakymouse.sneakycharactermanager.SneakyCharacterManagerPaper;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public abstract class CommandBaseAdmin extends Command {

    public CommandBaseAdmin(String name) {
        super(name);
        this.setPermission(SneakyCharacterManagerPaper.IDENTIFIER + ".admin.command." + this.getName());
    }

    @Override
    public abstract boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args);
    
}