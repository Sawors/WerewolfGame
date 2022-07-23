package com.github.sawors.werewolfgame.game.phases.night;

import com.github.sawors.werewolfgame.game.GameManager;
import com.github.sawors.werewolfgame.game.phases.GameEvent;
import com.github.sawors.werewolfgame.game.phases.PhaseType;

public class SunRise extends GameEvent {
    public SunRise(GameManager manager) {
        super(manager);
        this.type = PhaseType.NIGHT;
    }

    @Override
    public void start() {
        this.gm.buildQueue(PhaseType.DAY);
    }
}
