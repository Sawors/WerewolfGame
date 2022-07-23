package com.github.sawors.werewolfgame.game.phases.day;

import com.github.sawors.werewolfgame.game.GameManager;
import com.github.sawors.werewolfgame.game.phases.GameEvent;
import com.github.sawors.werewolfgame.game.phases.PhaseType;

public class NightFall extends GameEvent {

    public NightFall(GameManager manager) {
        super(manager);
        this.type = PhaseType.DAY;
    }

    @Override
    public void start() {
        this.gm.buildQueue(PhaseType.NIGHT);
    }
}
