package com.github.sawors.werewolfgame.game.events.night;

import com.github.sawors.werewolfgame.game.GameManager;
import com.github.sawors.werewolfgame.game.events.GameEvent;
import com.github.sawors.werewolfgame.game.events.PhaseType;

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
