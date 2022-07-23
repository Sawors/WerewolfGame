package com.github.sawors.werewolfgame.game.phases.day;

import com.github.sawors.werewolfgame.game.GameManager;
import com.github.sawors.werewolfgame.game.phases.GameEvent;
import com.github.sawors.werewolfgame.game.phases.PhaseType;

public class Intro extends GameEvent {
    
    public Intro(GameManager manager){
        super(manager);
        this.type = PhaseType.DAY;
    }
    
    @Override
    public void start() {
    
    }
}
