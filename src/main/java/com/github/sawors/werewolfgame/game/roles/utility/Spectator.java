package com.github.sawors.werewolfgame.game.roles.utility;

import com.github.sawors.werewolfgame.game.roles.DefaultRoleType;
import com.github.sawors.werewolfgame.game.roles.PlayerRole;

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
