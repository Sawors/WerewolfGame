package com.github.sawors.werewolfgame.game.phases;

import com.github.sawors.werewolfgame.game.GameManager;

public abstract class GamePhase {
    
    private boolean interrupted = false;
    private GameManager gm;
    
    public GamePhase(GameManager manager){
        this.gm = manager;
    }
    
    public abstract void start();
    public void stop(){
        boolean interrupted = true;
    };
    
}
