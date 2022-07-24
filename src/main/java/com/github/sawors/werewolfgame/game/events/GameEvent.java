package com.github.sawors.werewolfgame.game.events;

import com.github.sawors.werewolfgame.game.GameManager;

public abstract class GameEvent {

    protected GameManager gm;
    protected PhaseType type;
    
    public GameEvent(GameManager manager){
        this.gm = manager;
    }
    
    public abstract void start();

    public PhaseType getType() {
        return type;
    }
}
