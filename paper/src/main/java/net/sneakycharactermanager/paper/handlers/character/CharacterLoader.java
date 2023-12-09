package net.sneakycharactermanager.paper.handlers.character;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;

import net.sneakycharactermanager.paper.LoadCharacterEvent;
import net.sneakycharactermanager.paper.SneakyCharacterManager;
import net.sneakycharactermanager.paper.handlers.skins.SkinData;
import net.sneakycharactermanager.paper.util.ChatUtility;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.profile.PlayerTextures;

public class CharacterLoader {

    public static void loadCharacter(Character character){
        String url = character.getSkin();

        if(url == null || url.isEmpty()) return;

        //Not using invalid urls:
        if(!url.startsWith("http")){
            SneakyCharacterManager.getInstance().getLogger().warning("Invalid Skin URL Received? Was this our fault?");
            return;
        }

        //This system may need to change at some point
        PlayerProfile playerProfile = character.getPlayer().getPlayerProfile();
        boolean isSlimSkin = playerProfile.getTextures().getSkinModel().equals(PlayerTextures.SkinModel.SLIM);
        //Might need a setting for Is-Slim Skin? For now, defaulting to the characters base model type

        Bukkit.getAsyncScheduler().runNow(SneakyCharacterManager.getInstance(), (s) ->{
            SkinData data = new SkinData(url, isSlimSkin);
            Bukkit.getScheduler().runTaskLater(SneakyCharacterManager.getInstance(), () ->{
                playerProfile.removeProperty("textures");
                ProfileProperty property = data.getTextureProperty();
                if(property == null){
                    character.getPlayer().sendMessage(ChatUtility.convertToComponent("&4Failed to load skin! Something went wrong!"));
                }else{
                    playerProfile.setProperty(property);
                    character.getPlayer().setPlayerProfile(playerProfile);
                    SneakyCharacterManager.getInstance().nametagManager.nicknamePlayer(character.getPlayer(), character.getCharacterName());
                }
            }, 0);
        });

        Bukkit.getServer().getPluginManager().callEvent(new LoadCharacterEvent(character.getPlayer(), character.isFirstLoad(), character.getCharacterUUID(), character.getCharacterName(), url));
        character.setFirstLoad(false);
    }

    public static void updateSkin(Player player, String url){
        //This system may need to change at some point
        PlayerProfile playerProfile = player.getPlayerProfile();
        boolean isSlimSkin = playerProfile.getTextures().getSkinModel().equals(PlayerTextures.SkinModel.SLIM);

        String nickname = SneakyCharacterManager.getInstance().nametagManager.getNickname(player);
        if(nickname.equals(player.getName())) return;

        Bukkit.getAsyncScheduler().runNow(SneakyCharacterManager.getInstance(), (s) ->{
            SkinData data = new SkinData(url, isSlimSkin);
            Bukkit.getScheduler().runTaskLater(SneakyCharacterManager.getInstance(), () ->{
                playerProfile.removeProperty("textures");
                ProfileProperty property = data.getTextureProperty();
                if(property == null){
                    player.sendMessage(ChatUtility.convertToComponent("&4Failed to load skin! Something went wrong!"));
                }else{
                    playerProfile.setProperty(property);
                    player.setPlayerProfile(playerProfile);
                    //SneakyCharacterManager.getInstance().nametagManager.nicknamePlayer(player, nickname);
                }
            }, 0);
        });
    }
}