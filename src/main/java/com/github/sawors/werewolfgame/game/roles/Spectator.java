package com.github.sawors.werewolfgame.game.roles;

import com.github.sawors.werewolfgame.game.PlayerRole;

public class Spectator extends PlayerRole {
    @Override
    public String toString() {
        return DefaultRoleType.SPECTATOR.toString();
    }
    
    @Override
    public Integer priority() {
        return null;
    }
}
