package io.github.sawors.werewolfgame.game.roles;

import io.github.sawors.werewolfgame.extensionsloader.WerewolfExtension;

public abstract class VillagerLike extends PlayerRole {
    public VillagerLike(WerewolfExtension extension) {
        super(extension);
    }
    
    @Override
    public Integer priority() {
        return null;
    }
}
