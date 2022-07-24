package com.github.sawors.werewolfgame.game.roles;

import com.github.sawors.werewolfgame.extensionsloader.WerewolfExtension;
import com.github.sawors.werewolfgame.game.events.GameEvent;

import java.util.Set;

public abstract class PrimaryRole extends PlayerRole {
    public PrimaryRole(WerewolfExtension extension) {
        super(extension);
    }
    
    public void onLoad(){};
    public abstract Set<GameEvent> getEvents();
    
}
