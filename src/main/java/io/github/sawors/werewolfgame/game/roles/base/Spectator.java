package io.github.sawors.werewolfgame.game.roles.base;

import io.github.sawors.werewolfgame.extensionsloader.WerewolfExtension;
import io.github.sawors.werewolfgame.game.roles.PlayerRole;

public class Spectator extends PlayerRole {
    public Spectator(WerewolfExtension extension) {
        super(extension);
    }
    
    @Override
    public Integer priority() {
        return null;
    }
}
