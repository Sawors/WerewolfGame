package com.github.sawors.werewolfgame.game.roles;

import com.github.sawors.werewolfgame.extensionsloader.WerewolfExtension;

public abstract class VillagerLike extends PlayerRole {
    public VillagerLike(WerewolfExtension extension) {
        super(extension);
    }
    
    @Override
    public Integer priority() {
        return null;
    }
}
