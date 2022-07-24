package com.github.sawors.werewolfgame.game.events.day;

import com.github.sawors.werewolfgame.Main;
import com.github.sawors.werewolfgame.extensionsloader.WerewolfExtension;
import com.github.sawors.werewolfgame.game.GameManager;
import com.github.sawors.werewolfgame.game.GamePhase;
import com.github.sawors.werewolfgame.game.events.GameEvent;
import com.github.sawors.werewolfgame.game.events.PhaseType;

public class NightfallEvent extends GameEvent {
    
    public NightfallEvent(WerewolfExtension extension) {
        super(extension);
    }
    
    @Override
    public void start(GameManager manager) {
        Main.logAdmin("The Night Is Falling");
        manager.buildQueue(PhaseType.NIGHT);
    }
    
    @Override
    public GamePhase getPhase() {
        return GamePhase.NIGHTFALL;
    }
}
