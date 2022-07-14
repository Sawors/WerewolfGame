package com.github.sawors.werewolfgame;

import com.github.sawors.werewolfgame.game.RoleType;
import com.github.sawors.werewolfgame.game.WerewolfPlayer;
import net.dv8tion.jda.api.entities.Guild;
import org.bukkit.Server;

import java.io.File;
import java.util.HashMap;

public class GameManager {

    private GameType gametype;
    private GamePhase gamephase;
    private HashMap<WerewolfPlayer, RoleType> playerlist = new HashMap<>();
    private String id;
    private Guild discordserver;
    private Server mcserver;


    public GameManager(GameType type){
        this.id = Main.generateRandomGameId();
        this.gametype = type;
    }

    public void addPlayer(WerewolfPlayer player){
        // instead of using a set
        if(!playerlist.containsKey(player)){
            playerlist.put(player, RoleType.NOT_SET);
        }
    }

    public String getGameID(){
        return id;
    }

    public GameType getGameType() {
        return gametype;
    }

    public void setGameType(GameType gametype) {
        this.gametype = gametype;
    }






    public static GameManager getByID(String id){
        return Main.getGamesList().getOrDefault(id, null);
    }

    public static GameManager restoreFromFile(File backup) {
        //TODO : Restoration process (priority : not important)
        return new GameManager(GameType.MIXED);
    }


}
