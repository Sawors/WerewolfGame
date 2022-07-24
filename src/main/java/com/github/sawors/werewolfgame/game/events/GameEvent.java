package com.github.sawors.werewolfgame.game.events;

import com.github.sawors.werewolfgame.game.GameManager;
import com.github.sawors.werewolfgame.game.GamePhase;

public abstract class GameEvent {

    protected GameManager gm;
    
    public GameEvent(GameManager manager){
        this.gm = manager;
    }
    
    public abstract void start();

    public abstract GamePhase getPhase();
}
