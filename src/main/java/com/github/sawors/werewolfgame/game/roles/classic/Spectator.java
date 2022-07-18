package com.github.sawors.werewolfgame.game.roles.classic;

import com.github.sawors.werewolfgame.game.PlayerRole;
import com.github.sawors.werewolfgame.game.RoleType;

public class Spectator extends PlayerRole {
    @Override
    public String toString() {
        return RoleType.SPECTATOR.toString();
    }
    
    @Override
    public Integer priority() {
        return null;
    }
}
