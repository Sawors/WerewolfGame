package com.github.sawors.werewolfgame.game.roles.classic;

import com.github.sawors.werewolfgame.game.FirstNightRole;
import com.github.sawors.werewolfgame.game.PlayerRole;
import com.github.sawors.werewolfgame.game.RoleType;

public class Lover implements PlayerRole, FirstNightRole {

    @Override
    public RoleType getRoleType() {
        return RoleType.LOVER;
    }

    @Override
    public Integer priority() {
        return -20;
    }

    //TODO : Lover action (kill)
    @Override
    public void onDeathAction() {
        // none
    }

    @Override
    public void nightAction() {
        // none
    }

    @Override
    public void doFirstNightAction() {
        //TODO : Let them know each other on the first night
    }
}
