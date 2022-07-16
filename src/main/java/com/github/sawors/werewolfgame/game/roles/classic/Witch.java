package com.github.sawors.werewolfgame.game.roles.classic;

import com.github.sawors.werewolfgame.game.PlayerRole;
import com.github.sawors.werewolfgame.game.RoleType;

public class Witch extends PlayerRole {
    @Override
    public RoleType getRoleType() {
        return RoleType.WITCH;
    }

    @Override
    public Integer priority() {
        return 10;
    }

    @Override
    public void nightAction() {
        //TODO : Witch action
    }
}
