package com.github.sawors.werewolfgame.game.roles.classic;

import com.github.sawors.werewolfgame.game.FirstNightRole;

public class Cupid extends FirstNightRole {
    @Override
    public String toString() {
        return RoleType.CUPID.toString();
    }

    @Override
    public Integer priority() {
        return -30;
    }

    @Override
    public void doFirstNightAction() {
        //TODO : select lovers on first night
    }
}
