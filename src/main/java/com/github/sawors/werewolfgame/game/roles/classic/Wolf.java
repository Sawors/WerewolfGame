package com.github.sawors.werewolfgame.game.roles.classic;

import com.github.sawors.werewolfgame.game.PlayerRole;
import com.github.sawors.werewolfgame.game.Role;

public class Wolf implements PlayerRole {
    @Override
    public Role getRoleType() {
        return Role.WOLF;
    }

    @Override
    public int priority() {
        // THIS IS THE ONLY STANDARD !!!!
        // everything before wolves is <0, everything after is >0
        return 0;
    }

    @Override
    public void onDeathAction() {
        // do nothing since wolves don't have any particular death action
    }

    @Override
    public void nightAction() {

    }
}
