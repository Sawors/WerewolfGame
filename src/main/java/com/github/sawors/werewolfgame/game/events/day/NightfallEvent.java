package com.github.sawors.werewolfgame.game.events.day;

import com.github.sawors.werewolfgame.game.GameManager;
import com.github.sawors.werewolfgame.game.GamePhase;
import com.github.sawors.werewolfgame.game.events.GameEvent;
import com.github.sawors.werewolfgame.game.events.PhaseType;

public class NightfallEvent extends GameEvent {

    @Override
    public void start(GameManager manager) {
        manager.buildQueue(PhaseType.NIGHT);
    }
    
    @Override
    public GamePhase getPhase() {
        return GamePhase.NIGHTFALL;
    }
}
