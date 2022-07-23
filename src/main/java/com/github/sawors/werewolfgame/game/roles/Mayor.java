package com.github.sawors.werewolfgame.game.roles;

import com.github.sawors.werewolfgame.game.PlayerRole;

public class Mayor extends PlayerRole {
    @Override
    public String toString() {
        return DefaultRoleType.MAYOR.toString();
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
