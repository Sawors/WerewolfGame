package io.github.sawors.werewolfgame.game.events.day;

import io.github.sawors.werewolfgame.extensionsloader.WerewolfExtension;
import io.github.sawors.werewolfgame.game.GameManager;
import io.github.sawors.werewolfgame.game.events.GameEvent;

public class TellDeathEvent extends GameEvent {
    public TellDeathEvent(WerewolfExtension extension) {
        super(extension);
    }
    
    @Override
    public void start(GameManager manager) {
    
    }
}
