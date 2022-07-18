package com.github.sawors.werewolfgame.game.roles.classic;

import com.github.sawors.werewolfgame.game.PlayerRole;

public class Seer extends PlayerRole {
    @Override
    public String toString() {
        return RoleType.SEER.toString();
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
