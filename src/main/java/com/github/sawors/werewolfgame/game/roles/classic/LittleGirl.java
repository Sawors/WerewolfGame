package com.github.sawors.werewolfgame.game.roles.classic;

import com.github.sawors.werewolfgame.game.PlayerRole;
import com.github.sawors.werewolfgame.game.RoleType;

public class LittleGirl implements PlayerRole {
    @Override
    public RoleType getRoleType() {
        return RoleType.LITTLE_GIRL;
    }

    @Override
    public Integer priority() {
        return 0;
    }

    @Override
    public void onDeathAction() {
        // none
    }

    //TODO : Little Girl action
    @Override
    public void nightAction() {

    }
}
