package io.github.sawors.werewolfgame.game.roles.base;

import io.github.sawors.werewolfgame.extensionsloader.WerewolfExtension;
import io.github.sawors.werewolfgame.game.roles.DefaultRoleType;
import io.github.sawors.werewolfgame.game.roles.PlayerRole;

public class Mayor extends PlayerRole {
    public Mayor(WerewolfExtension extension) {
        super(extension);
    }
    
    @Override
    public String toString() {
        return DefaultRoleType.MAYOR.toString();
    }

    @Override
    public Integer priority() {
        return null;
    }

    @Override
    public void onDeathAction() {
        //TODO : Transmit role
    }
}