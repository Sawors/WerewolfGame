package com.github.sawors.werewolfgame.game.roles.classic;

import com.github.sawors.werewolfgame.game.roles.DefaultRoleType;
import com.github.sawors.werewolfgame.game.roles.WolfLike;

public class Wolf extends WolfLike {
    @Override
    public String toString() {
        return DefaultRoleType.WOLF.toString();
    }

    @Override
    public void wolfAction() {

    }
}
