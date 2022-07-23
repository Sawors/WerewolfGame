package com.github.sawors.werewolfgame.game.roles.classic;

import com.github.sawors.werewolfgame.game.PlayerRole;
import com.github.sawors.werewolfgame.game.roles.DefaultRoleType;

public class Seer extends PlayerRole {
    @Override
    public String toString() {
        return DefaultRoleType.SEER.toString();
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
