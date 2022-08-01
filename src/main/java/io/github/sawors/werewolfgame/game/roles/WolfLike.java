package io.github.sawors.werewolfgame.game.roles;

import io.github.sawors.werewolfgame.extensionsloader.WerewolfExtension;

public abstract class WolfLike extends PrimaryRole {
    public WolfLike(WerewolfExtension extension) {
        super(extension);
    }
    
    @Override
    public Integer priority() {
        return 0;
    }

}
