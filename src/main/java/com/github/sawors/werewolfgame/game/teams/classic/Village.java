package com.github.sawors.werewolfgame.game.teams.classic;

import com.github.sawors.werewolfgame.game.WerewolfTeam;

public class Village extends WerewolfTeam {
    Village(){
        this.name = "Village";
        this.color = 0xDDB572;
        setAlly(null);
    }
}
