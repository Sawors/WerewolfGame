package com.github.sawors.werewolfgame.game.roles.classic;

import com.github.sawors.werewolfgame.game.FirstNightRole;
import com.github.sawors.werewolfgame.game.roles.DefaultRoleType;

public class Cupid extends FirstNightRole {
    @Override
    public String toString() {
        return DefaultRoleType.CUPID.toString();
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
