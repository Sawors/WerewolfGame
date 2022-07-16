package com.github.sawors.werewolfgame.game.roles.classic;

import com.github.sawors.werewolfgame.game.FirstNightRole;
import com.github.sawors.werewolfgame.game.RoleType;

public class Cupid extends FirstNightRole {
    @Override
    public RoleType getRoleType() {
        return RoleType.CUPID;
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
