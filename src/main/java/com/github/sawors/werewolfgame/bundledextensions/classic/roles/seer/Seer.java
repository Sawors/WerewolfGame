package com.github.sawors.werewolfgame.bundledextensions.classic.roles.seer;

import com.github.sawors.werewolfgame.game.events.GameEvent;
import com.github.sawors.werewolfgame.game.roles.DefaultRoleType;
import com.github.sawors.werewolfgame.game.roles.PrimaryRole;

import java.util.HashSet;
import java.util.Set;

public class Seer extends PrimaryRole {
    @Override
    public String toString() {
        return DefaultRoleType.SEER.toString();
    }

    @Override
    public Integer priority() {
        return -10;
    }

    @Override
    public void nightAction() {
        //TODO : Seer action
    }
    
    @Override
    public void onLoad() {
    
    }
    
    @Override
    public Set<GameEvent> getEvents() {
        return new HashSet<>();
    }
}
