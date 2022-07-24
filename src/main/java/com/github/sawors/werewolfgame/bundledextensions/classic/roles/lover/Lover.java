package com.github.sawors.werewolfgame.bundledextensions.classic.roles.lover;

import com.github.sawors.werewolfgame.game.roles.DefaultRoleType;
import com.github.sawors.werewolfgame.game.roles.FirstNightRole;
import com.github.sawors.werewolfgame.game.roles.PlayerRole;

public class Lover extends PlayerRole implements FirstNightRole {
    
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
