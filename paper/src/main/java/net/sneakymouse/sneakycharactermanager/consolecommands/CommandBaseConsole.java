package net.sneakymouse.sneakycharactermanager.consolecommands;

import net.sneakymouse.sneakycharactermanager.SneakyCharacterManagerPaper;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public abstract class CommandBaseConsole extends Command {

    public CommandBaseConsole(String name) {
        super(name);
        this.setPermission(SneakyCharacterManagerPaper.IDENTIFIER + "console.command." + this.getName());
    }

    @Override
    public abstract boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args);
    
}