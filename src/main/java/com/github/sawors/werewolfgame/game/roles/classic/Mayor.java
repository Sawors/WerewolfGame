package com.github.sawors.werewolfgame.game.roles.classic;

import com.github.sawors.werewolfgame.game.PlayerRole;
import com.github.sawors.werewolfgame.game.RoleType;

public class Mayor extends PlayerRole {
    @Override
    public RoleType getRoleType() {
        return RoleType.MAYOR;
    }

    @Override
    public Integer priority() {
        return null;
    }

    @Override
    public void onDeathAction() {
        //TODO : Transmit role
    }
}
