package io.github.sawors.werewolfgame.game.roles;

import io.github.sawors.werewolfgame.extensionsloader.WerewolfExtension;

public abstract class PrimaryRole extends PlayerRole {
    public PrimaryRole(WerewolfExtension extension) {
        super(extension);
    }
    
    public void onLoad(){}
    
}
