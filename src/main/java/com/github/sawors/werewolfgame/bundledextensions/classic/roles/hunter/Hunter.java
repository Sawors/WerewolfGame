package com.github.sawors.werewolfgame.bundledextensions.classic.roles.hunter;

import com.github.sawors.werewolfgame.game.events.GameEvent;
import com.github.sawors.werewolfgame.game.roles.DefaultRoleType;
import com.github.sawors.werewolfgame.game.roles.PrimaryRole;

import java.util.Set;

public class Hunter extends PrimaryRole  {
    @Override
    public String toString() {
        return DefaultRoleType.HUNTER.toString();
    }
    
    @Override
    public Integer priority() {
        return null;
    }
    
    @Override
    public void onDeathAction() {
        //TODO : Hunter action
    }
    
    @Override
    public void onLoad() {
    
    }
    
    @Override
    public Set<GameEvent> getEvents() {
        return Set.of();
    }
}
