package com.github.sawors.werewolfgame.game.roles.classic;

import com.github.sawors.werewolfgame.game.roles.DefaultRoleType;
import com.github.sawors.werewolfgame.game.roles.VillagerLike;

public class Hunter extends VillagerLike {
    @Override
    public String toString() {
        return DefaultRoleType.HUNTER.toString();
    }

    @Override
    public void onDeathAction() {
        //TODO : Hunter action
    }
}
