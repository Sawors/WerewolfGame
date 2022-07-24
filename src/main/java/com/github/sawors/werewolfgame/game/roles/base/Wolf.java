package com.github.sawors.werewolfgame.game.roles.base;

import com.github.sawors.werewolfgame.game.events.GameEvent;
import com.github.sawors.werewolfgame.game.roles.DefaultRoleType;
import com.github.sawors.werewolfgame.game.roles.WolfLike;

import java.util.Set;

public class Wolf extends WolfLike {
    @Override
    public String toString() {
        return DefaultRoleType.WOLF.toString();
    }

    @Override
    public void wolfAction() {

    }
    
    @Override
    public Set<GameEvent> getEvents() {
        return Set.of();
    }
}
