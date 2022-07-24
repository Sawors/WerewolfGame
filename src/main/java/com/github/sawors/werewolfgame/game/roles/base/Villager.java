package com.github.sawors.werewolfgame.game.roles.base;

import com.github.sawors.werewolfgame.game.roles.DefaultRoleType;
import com.github.sawors.werewolfgame.game.roles.VillagerLike;

public class Villager extends VillagerLike {
    @Override
    public String toString() {
        return DefaultRoleType.VILLAGER.toString();
    }
}
