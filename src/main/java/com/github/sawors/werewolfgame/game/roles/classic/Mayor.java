package com.github.sawors.werewolfgame.game.roles.classic;

import com.github.sawors.werewolfgame.game.PlayerRole;

public class Mayor extends PlayerRole {
    @Override
    public String toString() {
        return RoleType.MAYOR.toString();
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
