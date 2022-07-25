package io.github.sawors.werewolfgame.bundledextensions.classic.roles.witch;

import io.github.sawors.werewolfgame.extensionsloader.WerewolfExtension;
import io.github.sawors.werewolfgame.game.GameManager;
import io.github.sawors.werewolfgame.game.GamePhase;
import io.github.sawors.werewolfgame.game.events.GameEvent;
import io.github.sawors.werewolfgame.game.events.RoleEvent;
import io.github.sawors.werewolfgame.game.roles.PlayerRole;

public class WitchPotionEvent extends GameEvent implements RoleEvent {
    
    public WitchPotionEvent(WerewolfExtension extension) {
        super(extension);
    }
    
    @Override
    public void start(GameManager manager) {
    
    }
    
    @Override
    public GamePhase getPhase() {
        return GamePhase.NIGHT_POSTWOLVES;
    }
    
    @Override
    public PlayerRole getRole() {
        return new Witch(extension);
    }
}
