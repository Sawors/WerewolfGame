package com.github.sawors.werewolfgame.game.roles.base;

import com.github.sawors.werewolfgame.extensionsloader.WerewolfExtension;
import com.github.sawors.werewolfgame.game.roles.DefaultRoleType;
import com.github.sawors.werewolfgame.game.roles.PlayerRole;

public class Spectator extends PlayerRole {
    public Spectator(WerewolfExtension extension) {
        super(extension);
    }
    
    @Override
    public String toString() {
        return DefaultRoleType.SPECTATOR.toString();
    }
    
    @Override
    public Integer priority() {
        return null;
    }
}
