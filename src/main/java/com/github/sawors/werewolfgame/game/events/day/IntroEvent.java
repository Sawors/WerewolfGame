package com.github.sawors.werewolfgame.game.events.day;

import com.github.sawors.werewolfgame.game.GameManager;
import com.github.sawors.werewolfgame.game.GamePhase;
import com.github.sawors.werewolfgame.game.events.GameEvent;

public class IntroEvent extends GameEvent {
    
    public IntroEvent(GameManager manager){
        super(manager);
    }
    
    @Override
    public void start() {
    
    }
    
    @Override
    public GamePhase getPhase() {
        return GamePhase.FIRST_DAY;
    }
}
