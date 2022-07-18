package com.github.sawors.werewolfgame.game.roles.classic;

import com.github.sawors.werewolfgame.game.PlayerRole;

public class Villager extends PlayerRole {
    @Override
    public String toString() {
        return RoleType.VILLAGER.toString();
    }

    @Override
    public Integer priority() {
        return null;
    }
}
