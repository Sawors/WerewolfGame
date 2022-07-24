package com.github.sawors.werewolfgame.game.roles.utility;

import com.github.sawors.werewolfgame.game.roles.DefaultRoleType;
import com.github.sawors.werewolfgame.game.roles.PlayerRole;

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
