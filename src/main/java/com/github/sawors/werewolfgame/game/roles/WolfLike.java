package com.github.sawors.werewolfgame.game.roles;

import com.github.sawors.werewolfgame.game.PlayerRole;

public abstract class WolfLike extends PlayerRole {
    @Override
    protected Integer priority() {
        return 0;
    }

    public void wolfAction(){

    }
}
