package com.github.sawors.werewolfgame.game.roles;

import com.github.sawors.werewolfgame.game.events.GameEvent;

import java.util.Set;

public abstract class PrimaryRole extends PlayerRole {
    public void onLoad(){};
    public abstract Set<GameEvent> getEvents();
}
