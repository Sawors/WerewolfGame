package com.github.sawors.werewolfgame.bundledextensions.classic.roles.cupid;

import com.github.sawors.werewolfgame.game.events.GameEvent;
import com.github.sawors.werewolfgame.game.roles.DefaultRoleType;
import com.github.sawors.werewolfgame.game.roles.FirstNightRole;
import com.github.sawors.werewolfgame.game.roles.PrimaryRole;

import java.util.Set;

public class Cupid extends PrimaryRole implements FirstNightRole {
    @Override
    public String toString() {
        return DefaultRoleType.CUPID.toString();
    }

    @Override
    public Integer priority() {
        return -30;
    }
    
    @Override
    public void onLoad() {
    
    }
    
    @Override
    public Set<GameEvent> getEvents() {
        return Set.of();
    }
    
    @Override
    public void doFirstNightAction() {
        //TODO : select lovers on first night
    }
}
