package com.github.sawors.werewolfgame.game.roles.classic;

import com.github.sawors.werewolfgame.game.PlayerRole;
import com.github.sawors.werewolfgame.game.RoleType;

public class LittleGirl extends PlayerRole {
    @Override
    public RoleType getRoleType() {
        return RoleType.LITTLE_GIRL;
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
