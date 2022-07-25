package io.github.sawors.werewolfgame.game.events;

import io.github.sawors.werewolfgame.extensionsloader.WerewolfExtension;
import io.github.sawors.werewolfgame.game.GameManager;
import io.github.sawors.werewolfgame.game.GamePhase;

public abstract class GameEvent {
    
    public WerewolfExtension extension;
    public GameEvent(WerewolfExtension extension){
        this.extension = extension;
    }
    
    public abstract void start(GameManager manager);

    public abstract GamePhase getPhase();
}
