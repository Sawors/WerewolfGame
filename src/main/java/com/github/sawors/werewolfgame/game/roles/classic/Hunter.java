package com.github.sawors.werewolfgame.game.roles.classic;

import com.github.sawors.werewolfgame.game.PlayerRole;
import com.github.sawors.werewolfgame.game.RoleType;

public class Hunter extends PlayerRole {
    @Override
    public String toString() {
        return RoleType.HUNTER.toString();
    }

    @Override
    public Integer priority() {
        return null;
    }

    @Override
    public void onDeathAction() {
        //TODO : Hunter action
    }
}
