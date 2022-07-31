package io.github.sawors.werewolfgame.game.events.utility;

import io.github.sawors.werewolfgame.Main;
import io.github.sawors.werewolfgame.extensionsloader.WerewolfExtension;
import io.github.sawors.werewolfgame.game.GameManager;
import io.github.sawors.werewolfgame.game.GamePhase;
import io.github.sawors.werewolfgame.game.events.GameEvent;
import io.github.sawors.werewolfgame.game.events.PhaseType;

public class NightfallEvent extends GameEvent {
    
    public NightfallEvent(WerewolfExtension extension) {
        super(extension);
    }
    
    @Override
    public void start(GameManager manager) {
        Main.logAdmin("The Night Is Falling");
        manager.buildQueue(PhaseType.NIGHT);
        manager.nextEvent();
    }
    
    @Override
    public GamePhase getPhase() {
        return GamePhase.NIGHTFALL;
    }
}
