package com.github.sawors.werewolfgame.game.roles;

import com.github.sawors.werewolfgame.extensionsloader.WerewolfExtension;

public abstract class WolfLike extends PrimaryRole {
    public WolfLike(WerewolfExtension extension) {
        super(extension);
    }
    
    @Override
    public Integer priority() {
        return 0;
    }

    public abstract void wolfAction();

}
