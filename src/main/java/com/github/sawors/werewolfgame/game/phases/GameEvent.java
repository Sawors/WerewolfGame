package com.github.sawors.werewolfgame.game.phases;

import com.github.sawors.werewolfgame.game.GameManager;

public abstract class GameEvent {
    
    private boolean interrupted = false;
    protected GameManager gm;
    protected PhaseType type;
    
    public GameEvent(GameManager manager){
        this.gm = manager;
    }
    
    public abstract void start();
    public void stop(){
        boolean interrupted = true;
    };

    public PhaseType getType() {
        return type;
    }
}
