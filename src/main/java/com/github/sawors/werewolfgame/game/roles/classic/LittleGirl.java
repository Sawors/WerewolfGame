package com.github.sawors.werewolfgame.game.roles.classic;

import com.github.sawors.werewolfgame.game.roles.DefaultRoleType;
import com.github.sawors.werewolfgame.game.roles.WolfLike;

public class LittleGirl extends WolfLike {
    @Override
    public String toString() {
        return DefaultRoleType.LITTLE_GIRL.toString();
    }

    @Override
    public void wolfAction() {
        //TODO : Little Girl action
    }
}
