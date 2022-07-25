package io.github.sawors.werewolfgame.game.teams.classic;

import io.github.sawors.werewolfgame.game.WerewolfTeam;

public class Village extends WerewolfTeam {
    public Village(){
        this.name = "Village";
        this.color = 0xDDB572;
        setAlly(null);
    }
}
