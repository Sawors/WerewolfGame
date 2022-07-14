package com.github.sawors.werewolfgame.game.roles.classic;

import com.github.sawors.werewolfgame.game.PlayerRole;
import com.github.sawors.werewolfgame.game.RoleType;

public class Wolf implements PlayerRole {
    @Override
    public RoleType getRoleType() {
        return RoleType.WOLF;
    }

    @Override
    public Integer priority() {
        // THIS IS THE ONLY STANDARD !!!!
        return 0;
    }

    @Override
    public void onDeathAction() {
        // do nothing since wolves don't have any particular death action
    }


    //TODO : Wolf action
    @Override
    public void nightAction() {

    }
}
