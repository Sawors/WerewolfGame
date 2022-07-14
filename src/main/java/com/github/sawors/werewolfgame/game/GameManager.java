package com.github.sawors.werewolfgame.game;

import com.github.sawors.werewolfgame.GameType;
import net.dv8tion.jda.api.entities.User;

import java.util.HashMap;

public class GameManager {

    GameType gametype;
    HashMap<User,Long> players = new HashMap<>();


    GameManager(GameType type){
        this.gametype = type;
    }


    public GameType getGameType() {
        return gametype;
    }

    public void setGameType(GameType gametype) {
        this.gametype = gametype;
    }


}
