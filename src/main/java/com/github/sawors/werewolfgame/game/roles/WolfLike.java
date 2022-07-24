package com.github.sawors.werewolfgame.game.roles;

public abstract class WolfLike extends PlayerRole {
    @Override
    public Integer priority() {
        return 0;
    }

    public abstract void wolfAction();

}
