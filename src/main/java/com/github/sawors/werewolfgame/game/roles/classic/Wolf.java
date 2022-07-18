package com.github.sawors.werewolfgame.game.roles.classic;

import com.github.sawors.werewolfgame.game.PlayerRole;
import com.github.sawors.werewolfgame.game.RoleType;

public class Wolf extends PlayerRole {
    @Override
    public String toString() {
        return RoleType.WOLF.toString();
    }

    @Override
    public Integer priority() {
        // THIS IS THE ONLY STANDARD !!!!
        return 0;
    }

    @Override
    public void nightAction() {
        //TODO : Wolf action
    }
}
