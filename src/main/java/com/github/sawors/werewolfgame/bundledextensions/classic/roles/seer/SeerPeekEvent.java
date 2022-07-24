package com.github.sawors.werewolfgame.bundledextensions.classic.roles.seer;

import com.github.sawors.werewolfgame.game.GameManager;
import com.github.sawors.werewolfgame.game.GamePhase;
import com.github.sawors.werewolfgame.game.events.GameEvent;
import com.github.sawors.werewolfgame.game.events.RoleEvent;
import com.github.sawors.werewolfgame.game.roles.PlayerRole;

public class SeerPeekEvent extends GameEvent implements RoleEvent {
    public SeerPeekEvent(GameManager manager) {
        super(manager);
    }
    
    @Override
    public void start() {
    
    }
    
    @Override
    public GamePhase getPhase() {
        return GamePhase.NIGHT_PREWOLVES;
    }
    
    @Override
    public PlayerRole getRole() {
        return new Seer();
    }
}
