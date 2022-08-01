package io.github.sawors.werewolfgame.game.roles.base;

import io.github.sawors.werewolfgame.extensionsloader.WerewolfExtension;
import io.github.sawors.werewolfgame.game.roles.PlayerRole;

public class Mayor extends PlayerRole {
    public Mayor(WerewolfExtension extension) {
        super(extension);
    }
    
    @Override
    public Integer priority() {
        return null;
    }
}
