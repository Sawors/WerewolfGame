package com.github.sawors.werewolfgame.bundledextensions.classic.roles.seer;

import com.github.sawors.werewolfgame.extensionsloader.WerewolfExtension;
import com.github.sawors.werewolfgame.game.GameManager;
import com.github.sawors.werewolfgame.game.GamePhase;
import com.github.sawors.werewolfgame.game.events.GameEvent;
import com.github.sawors.werewolfgame.game.events.RoleEvent;
import com.github.sawors.werewolfgame.game.roles.PlayerRole;

public class SeerPeekEvent extends GameEvent implements RoleEvent {
    
    public SeerPeekEvent(WerewolfExtension extension) {
        super(extension);
    }
    
    @Override
    public void start(GameManager manager) {
    
    }
    
    @Override
    public GamePhase getPhase() {
        return GamePhase.NIGHT_PREWOLVES;
    }
    
    @Override
    public PlayerRole getRole() {
        return new Seer(extension);
    }
}
