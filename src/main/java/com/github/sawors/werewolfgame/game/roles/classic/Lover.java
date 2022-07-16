package com.github.sawors.werewolfgame.game.roles.classic;

import com.github.sawors.werewolfgame.game.FirstNightRole;
import com.github.sawors.werewolfgame.game.RoleType;

public class Lover extends FirstNightRole {

    @Override
    public RoleType getRoleType() {
        return RoleType.LOVER;
    }

    @Override
    public Integer priority() {
        return -20;
    }

    @Override
    public void onDeathAction() {
        //TODO : Lover action (kill)
    }

    @Override
    public void doFirstNightAction() {
        //TODO : Let them know each other on the first night
    }
}
