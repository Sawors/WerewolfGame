package com.github.sawors.werewolfgame.game.roles.classic;

import com.github.sawors.werewolfgame.game.PlayerRole;
import com.github.sawors.werewolfgame.game.RoleType;

public class Spectator implements PlayerRole {
    @Override
    public RoleType getRoleType() {
        return RoleType.SPECTATOR;
    }
    
    @Override
    public Integer priority() {
        return null;
    }
    
    @Override
    public void onDeathAction() {}
    
    @Override
    public void nightAction() {}
}
