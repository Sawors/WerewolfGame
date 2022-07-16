package com.github.sawors.werewolfgame.game.roles.classic;

import com.github.sawors.werewolfgame.game.PlayerRole;
import com.github.sawors.werewolfgame.game.RoleType;

public class Villager extends PlayerRole {
    @Override
    public RoleType getRoleType() {
        return RoleType.VILLAGER;
    }

    @Override
    public Integer priority() {
        return null;
    }
}
