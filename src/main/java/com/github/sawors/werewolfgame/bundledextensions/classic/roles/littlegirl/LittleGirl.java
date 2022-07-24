package com.github.sawors.werewolfgame.bundledextensions.classic.roles.littlegirl;

import com.github.sawors.werewolfgame.game.events.GameEvent;
import com.github.sawors.werewolfgame.game.roles.DefaultRoleType;
import com.github.sawors.werewolfgame.game.roles.WolfLike;

import java.util.Set;

public class LittleGirl extends WolfLike {
    @Override
    public String toString() {
        return DefaultRoleType.LITTLE_GIRL.toString();
    }
    
    @Override
    public void onLoad() {
    
    }
    
    @Override
    public Set<GameEvent> getEvents() {
        return Set.of();
    }
    
    @Override
    public void wolfAction() {
        //TODO : Little Girl action
    }
}
