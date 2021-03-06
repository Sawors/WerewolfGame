package com.github.sawors.werewolfgame.game.events.night;

import com.github.sawors.werewolfgame.game.GameManager;
import com.github.sawors.werewolfgame.game.GamePhase;
import com.github.sawors.werewolfgame.game.events.GameEvent;
import com.github.sawors.werewolfgame.game.events.PhaseType;

public class SunriseEvent extends GameEvent {

    @Override
    public void start(GameManager manager) {
        manager.buildQueue(PhaseType.DAY);
    }
    
    @Override
    public GamePhase getPhase() {
        return GamePhase.SUNRISE;
    }
}
