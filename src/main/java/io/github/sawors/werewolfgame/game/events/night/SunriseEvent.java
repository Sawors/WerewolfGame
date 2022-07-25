package io.github.sawors.werewolfgame.game.events.night;

import io.github.sawors.werewolfgame.extensionsloader.WerewolfExtension;
import io.github.sawors.werewolfgame.game.GameManager;
import io.github.sawors.werewolfgame.game.GamePhase;
import io.github.sawors.werewolfgame.game.events.GameEvent;
import io.github.sawors.werewolfgame.game.events.PhaseType;

public class SunriseEvent extends GameEvent {
    
    public SunriseEvent(WerewolfExtension extension) {
        super(extension);
    }
    
    @Override
    public void start(GameManager manager) {
        manager.buildQueue(PhaseType.DAY);
    }
    
    @Override
    public GamePhase getPhase() {
        return GamePhase.SUNRISE;
    }
}
