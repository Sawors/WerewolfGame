package com.github.sawors.werewolfgame.game.roles.classic;

import com.github.sawors.werewolfgame.game.PlayerRole;
import com.github.sawors.werewolfgame.game.RoleType;

public class Seer extends PlayerRole {
    @Override
    public RoleType getRoleType() {
        return RoleType.SEER;
    }

    @Override
    public Integer priority() {
        return -10;
    }

    @Override
    public void nightAction() {
        //TODO : Seer action
    }
}
