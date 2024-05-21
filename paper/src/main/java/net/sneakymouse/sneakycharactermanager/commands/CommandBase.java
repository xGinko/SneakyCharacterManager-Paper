package net.sneakymouse.sneakycharactermanager.commands;

import net.sneakymouse.sneakycharactermanager.SneakyCharacterManagerPaper;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public abstract class CommandBase extends Command {

    public CommandBase(String name) {
        super(name);
        this.setPermission(SneakyCharacterManagerPaper.IDENTIFIER + ".command." + this.getName());
    }

    @Override
    public abstract boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args);

}