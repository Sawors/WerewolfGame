package io.github.sawors.werewolfgame.bundledextensions.classic.roles.lover;

import io.github.sawors.werewolfgame.extensionsloader.WerewolfExtension;
import io.github.sawors.werewolfgame.game.roles.DefaultRoleType;
import io.github.sawors.werewolfgame.game.roles.FirstNightRole;
import io.github.sawors.werewolfgame.game.roles.PlayerRole;

public class Lover extends PlayerRole implements FirstNightRole {
    
    public Lover(WerewolfExtension extension) {
        super(extension);
    }
    
    @Override
    public String toString() {
        return DefaultRoleType.LOVER.toString();
    }

    @Override
    public Integer priority() {
        return -20;
    }

    @Override
    public void onDeathAction() {
        //TODO : Lover action (kill)
    }
    
    @Override
    public void doFirstNightAction() {
        //TODO : Let them know each other on the first night
    }
}
