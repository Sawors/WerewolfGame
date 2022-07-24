package com.github.sawors.werewolfgame.bundledextensions.classic.roles.witch;

import com.github.sawors.werewolfgame.game.GameManager;
import com.github.sawors.werewolfgame.game.GamePhase;
import com.github.sawors.werewolfgame.game.events.GameEvent;
import com.github.sawors.werewolfgame.game.events.RoleEvent;
import com.github.sawors.werewolfgame.game.roles.PlayerRole;

public class WitchPotionEvent extends GameEvent implements RoleEvent {
    public WitchPotionEvent(GameManager manager) {
        super(manager);
    }
    
    @Override
    public void start() {
    
    }
    
    @Override
    public GamePhase getPhase() {
        return GamePhase.NIGHT_POSTWOLVES;
    }
    
    @Override
    public PlayerRole getRole() {
        return new Witch();
    }
}
