package com.github.sawors.werewolfgame.game.events.night;

import com.github.sawors.werewolfgame.game.GameManager;
import com.github.sawors.werewolfgame.game.GamePhase;
import com.github.sawors.werewolfgame.game.events.GameEvent;
import com.github.sawors.werewolfgame.game.events.PhaseType;

public class SunriseEvent extends GameEvent {
    public SunriseEvent(GameManager manager) {
        super(manager);
    }

    @Override
    public void start() {
        this.gm.buildQueue(PhaseType.DAY);
    }
    
    @Override
    public GamePhase getPhase() {
        return GamePhase.SUNRISE;
    }
}
