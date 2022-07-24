package com.github.sawors.werewolfgame.bundledextensions.classic.roles.witch;

import com.github.sawors.werewolfgame.game.roles.DefaultRoleType;
import com.github.sawors.werewolfgame.game.roles.PlayerRole;
import com.github.sawors.werewolfgame.game.roles.PrimaryRole;

public class Witch extends PlayerRole implements PrimaryRole {
    @Override
    public String toString() {
        return DefaultRoleType.WITCH.toString();
    }

    @Override
    public Integer priority() {
        return 10;
    }

    @Override
    public void nightAction() {
        //TODO : Witch action
    }
    
    @Override
    public void onLoad() {
    
    }
}
