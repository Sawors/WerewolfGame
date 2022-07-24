package com.github.sawors.werewolfgame.game.roles.classic;

import com.github.sawors.werewolfgame.game.roles.DefaultRoleType;
import com.github.sawors.werewolfgame.game.roles.PlayerRole;

public class Witch extends PlayerRole {
    @Override
    public String toString() {
        return DefaultRoleType.WITCH.toString();
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
