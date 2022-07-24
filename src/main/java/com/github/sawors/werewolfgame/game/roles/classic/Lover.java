package com.github.sawors.werewolfgame.game.roles.classic;

import com.github.sawors.werewolfgame.game.roles.DefaultRoleType;
import com.github.sawors.werewolfgame.game.roles.FirstNightRole;

public class Lover extends FirstNightRole {
    
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
