package com.github.sawors.werewolfgame.game.roles.classic;

import com.github.sawors.werewolfgame.game.PlayerRole;
import com.github.sawors.werewolfgame.game.RoleType;

public class LittleGirl extends PlayerRole {
    @Override
    public String toString() {
        return RoleType.LITTLE_GIRL.toString();
    }

    @Override
    public Integer priority() {
        return 0;
    }

    @Override
    public void nightAction() {
        //TODO : Little Girl action
    }
}
