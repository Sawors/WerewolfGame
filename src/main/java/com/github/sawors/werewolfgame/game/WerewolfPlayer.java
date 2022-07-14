package com.github.sawors.werewolfgame.game;

import java.util.ArrayList;
import java.util.UUID;

public class WerewolfPlayer {
    UUID minecraftid;
    String discordid;
    String name;
    ArrayList<PlayerPreference> preferences;

    public WerewolfPlayer(UUID mcid, String discordid, String name, ArrayList<PlayerPreference> preferences){
        this.minecraftid = mcid;
        this.discordid = discordid;
        this.name = name;
        this.preferences = preferences;
    }

}
