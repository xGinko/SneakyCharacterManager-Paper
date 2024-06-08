package net.sneakymouse.sneakycharactermanager.handlers;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.context.ContextCalculator;
import net.luckperms.api.context.ContextConsumer;
import net.luckperms.api.context.ContextSet;
import net.luckperms.api.context.ImmutableContextSet;
import net.sneakymouse.sneakycharactermanager.handlers.character.Character;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;

public class ContextCalculatorCharacterTag implements ContextCalculator<Player> {

    private static final String CONTEXT_NAME = "charactertag";

    @Override
    public void calculate(@NonNull Player target, @NonNull ContextConsumer consumer) {
        Character character = Character.get(target);

        if (character != null) {
            for (String tag : character.getTags()) {
                consumer.accept(CONTEXT_NAME, tag);
            }
        }
    }

    @Override
    public ContextSet estimatePotentialContexts() {
        ImmutableContextSet.Builder builder = ImmutableContextSet.builder();

        List<String> allTags = new ArrayList<>();

        for (Character character : Character.getAll()) {
            for (String tag: character.getTags()) {
                allTags.add(tag);
            }
        }

        for (String tag : allTags) {
            builder.add(CONTEXT_NAME, tag);
        }

        return builder.build();
    }

    public void register() {
        LuckPerms luckPerms = LuckPermsProvider.get();
        luckPerms.getContextManager().registerCalculator(this);
    }
    
}