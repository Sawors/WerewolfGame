package com.github.sawors.werewolfgame.game.events;

import com.github.sawors.werewolfgame.extensionsloader.WerewolfExtension;
import com.github.sawors.werewolfgame.game.GameManager;
import com.github.sawors.werewolfgame.game.GamePhase;

public abstract class GameEvent {
    
    public WerewolfExtension extension;
    public GameEvent(WerewolfExtension extension){
        this.extension = extension;
    }
    
    public abstract void start(GameManager manager);

    public abstract GamePhase getPhase();
}
