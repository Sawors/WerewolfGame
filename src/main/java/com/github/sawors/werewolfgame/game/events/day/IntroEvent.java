package com.github.sawors.werewolfgame.game.events.day;

import com.github.sawors.werewolfgame.game.GameManager;
import com.github.sawors.werewolfgame.game.GamePhase;
import com.github.sawors.werewolfgame.game.events.GameEvent;

public class IntroEvent extends GameEvent {
    
    @Override
    public void start(GameManager manager) {
    
    }
    
    @Override
    public GamePhase getPhase() {
        return GamePhase.FIRST_DAY;
    }
}
