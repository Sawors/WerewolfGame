package com.github.sawors.werewolfgame.game.events;

import com.github.sawors.werewolfgame.game.GameManager;
import com.github.sawors.werewolfgame.game.GamePhase;

public abstract class GameEvent {
    
    public abstract void start(GameManager manager);

    public abstract GamePhase getPhase();
}
