package com.github.sawors.werewolfgame.game.events.day;

import com.github.sawors.werewolfgame.game.GameManager;
import com.github.sawors.werewolfgame.game.events.GameEvent;
import com.github.sawors.werewolfgame.game.events.PhaseType;

public class IntroEvent extends GameEvent {
    
    public IntroEvent(GameManager manager){
        super(manager);
        this.type = PhaseType.DAY;
    }
    
    @Override
    public void start() {
    
    }
}
