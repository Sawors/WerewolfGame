package com.github.sawors.werewolfgame.game.roles.classic;

import com.github.sawors.werewolfgame.game.PlayerRole;
import com.github.sawors.werewolfgame.game.RoleType;

public class Seer implements PlayerRole {
    @Override
    public RoleType getRoleType() {
        return RoleType.SEER;
    }

    @Override
    public Integer priority() {
        return -10;
    }

    @Override
    public void onDeathAction() {
        // none
    }


    //TODO : Seer action
    @Override
    public void nightAction() {

    }
}
