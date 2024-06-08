package net.sneakymouse.sneakycharactermanager.handlers.character;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import net.sneakymouse.sneakycharactermanager.SneakyCharacterManagerPaper;
import net.sneakymouse.sneakycharactermanager.commands.consolecommands.ConsoleCommandCharTemp;
import net.sneakymouse.sneakycharactermanager.handlers.skins.SkinCache;
import net.sneakymouse.sneakycharactermanager.handlers.skins.SkinData;
import net.sneakymouse.sneakycharactermanager.util.SkinUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.profile.PlayerTextures;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class CharacterLoader {

    public static boolean loadCharacter(Character character) {
        String url = character.getSkin();
        Player player = character.getPlayer();

        LoadCharacterEvent event = new LoadCharacterEvent(
                player,
                character.isFirstLoad(),
                character.getCharacterUUID(),
                character.getName(),
                url,
                character.isSlim(),
                character.getTags()
        );

        if (!event.callEvent()) {
            return false;
        }

        ConsoleCommandCharTemp.playerTempCharRemove(player.getUniqueId().toString());

        ProfileProperty profileProperty = SkinCache.get(player.getUniqueId().toString(), url);

        character.setFirstLoad(false);

        if (profileProperty == null) {
            if (!shouldSkipLoading(character)) {
                SkinData.getOrCreate(url, character.isSlim(), 2, player);
            }
        } else {
            player.setPlayerProfile(SkinUtil.handleCachedSkin(player, profileProperty));
        }

        SneakyCharacterManagerPaper.getInstance().nametagManager.nicknamePlayer(player, character.getName());

        return true;
    }

    
    private static boolean shouldSkipLoading(Character character) {
        String url = character.getSkin();

        if (url != null) {
            if (url.startsWith("http")) {
                return false;
            } else {
                SneakyCharacterManagerPaper.logger().warn("Invalid Skin URL Received. Was this our fault?");
            }
        }

        return true;
    }

    public static void updateSkin(Player player, String url, Boolean slim) {
        PlayerProfile playerProfile = player.getPlayerProfile();

        if (slim == null) {
            Bukkit.getAsyncScheduler().runNow(SneakyCharacterManagerPaper.getInstance(), (s) -> {
                checkSlimThenSetSkin(url, playerProfile.getTextures().getSkinModel().equals(PlayerTextures.SkinModel.SLIM), player);
            });
        } else {
            SkinData.getOrCreate(url, slim, 3, player);
        }
    }

    private static void checkSlimThenSetSkin(String url, boolean slim, Player player) {
        try {
            HttpClient httpClient = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder().uri(new URI(url.replace("imgur", "filmot")))
                    .timeout(Duration.ofSeconds(2))
                    .build();

            HttpResponse<InputStream> response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());

            // Check the HTTP response status
            int statusCode = response.statusCode();

            if (statusCode == 200) {
                try (InputStream inputStream = response.body()) {
                    if (inputStream != null) {
                        BufferedImage image = ImageIO.read(inputStream);

                        if (image != null) {
                            int pixel = image.getRGB(55, 20);
                            int alpha = (pixel >> 24) & 0xFF;
                            slim = alpha == 0;
                        }
                    }
                }
            }
        } catch (IOException | InterruptedException | URISyntaxException e) {
            SneakyCharacterManagerPaper.logger().error("Error while getting skin image.", e);
        }

        final boolean slimFinal = slim;

        player.getScheduler().run(SneakyCharacterManagerPaper.getInstance(),
                cacheSkin -> SkinData.getOrCreate(url, slimFinal, 3, player), null);
    }
}
