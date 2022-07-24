package com.github.sawors.werewolfgame.bundledextensions.classic.roles.witch;

import com.github.sawors.werewolfgame.game.events.GameEvent;
import com.github.sawors.werewolfgame.game.roles.DefaultRoleType;
import com.github.sawors.werewolfgame.game.roles.PrimaryRole;

import java.util.Set;

public class Witch extends PrimaryRole {
    @Override
    public String toString() {
        return DefaultRoleType.WITCH.toString();
    }

    @Override
    public Integer priority() {
        return 10;
    }

    @Override
    public void nightAction() {
        //TODO : Witch action
    }
    
    @Override
    public void onLoad() {
    
    }
    
    @Override
    public Set<GameEvent> getEvents() {
        return Set.of();
    }
}
