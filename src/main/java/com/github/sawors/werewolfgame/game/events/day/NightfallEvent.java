package com.github.sawors.werewolfgame.game.events.day;

import com.github.sawors.werewolfgame.game.GameManager;
import com.github.sawors.werewolfgame.game.events.GameEvent;
import com.github.sawors.werewolfgame.game.events.PhaseType;

public class NightfallEvent extends GameEvent {

    public NightfallEvent(GameManager manager) {
        super(manager);
        this.type = PhaseType.DAY;
    }

    @Override
    public void start() {
        this.gm.buildQueue(PhaseType.NIGHT);
    }
}
