package net.sneakymouse.sneakycharactermanager.handlers;

import net.sneakymouse.sneakycharactermanager.SneakyCharacterManagerPaper;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.sneakymouse.sneakycharactermanager.handlers.character.Character;

public class Placeholders extends PlaceholderExpansion {

    public Placeholders() {}

    @Override
    public @NotNull String getIdentifier() {
        return SneakyCharacterManagerPaper.IDENTIFIER;
    }

    @Override
    @SuppressWarnings("UnstableApiUsage")
    public @NotNull String getAuthor() {
        return SneakyCharacterManagerPaper.getInstance().getPluginMeta().getAuthors().get(0);
    }

    @Override
    @SuppressWarnings("UnstableApiUsage")
    public @NotNull String getVersion() {
        return SneakyCharacterManagerPaper.getInstance().getPluginMeta().getVersion();
    }
    
    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, String params) {
        Character character = Character.get(player);
        if (character == null) return "";

        String placeholder = params.toLowerCase();

        if (placeholder.equals("character_uuid")) {
            return character.getCharacterUUID();
        } else if (placeholder.equals("character_name")) {
            return character.getName();
        } else if (placeholder.equals("character_name_noformat")) {
            return character.getNameUnformatted();
        } else if (placeholder.equals("character_skin")) {
            return character.getSkin();
        } else if (placeholder.equals("character_slim")) {
            return character.isSlim() + "";
        } else if (placeholder.equals("character_tags")) {
            return character.getTagsJoined();
        } else if (placeholder.startsWith("character_hastag_")) {
            return character.hasTag(placeholder.replace("character_hastag_", "")) + "";
        }

        return null;
    }
    
}
