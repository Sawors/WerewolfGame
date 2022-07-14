package com.github.sawors.werewolfgame;

import com.github.sawors.werewolfgame.game.WerewolfPlayer;

import java.io.File;
import java.util.ArrayList;

public class GameManager {

    private GameType gametype;
    private GamePhase gamephase;
    private ArrayList<WerewolfPlayer> players = new ArrayList<>();
    private String id;


    public GameManager(GameType type){
        this.id = Main.generateGameId();
        this.gametype = type;
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
