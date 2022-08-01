package io.github.sawors.werewolfgame.game.events;

import io.github.sawors.werewolfgame.extensionsloader.WerewolfExtension;
import io.github.sawors.werewolfgame.game.GameManager;

public abstract class GameEvent {
    
    public WerewolfExtension extension;
    public GameEvent(WerewolfExtension extension){
        this.extension = extension;
    }
    
    public abstract void start(GameManager manager);

    public String getStartMessage(){
        return null;
    }

    public String getEndMessage(){
        return null;
    }
    
    public WerewolfExtension getExtension(){
        return extension;
    }
}
