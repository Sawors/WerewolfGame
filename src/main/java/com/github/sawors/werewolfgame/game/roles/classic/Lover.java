package com.github.sawors.werewolfgame.game.roles.classic;

import com.github.sawors.werewolfgame.game.PlayerRole;
import com.github.sawors.werewolfgame.game.Role;

public class Lover implements PlayerRole {

    @Override
    public Role getRoleType() {
        return Role.LOVER;
    }

    @Override
    public Integer priority() {
        return null;
    }

    //TODO : Lover action
    @Override
    public void onDeathAction() {

    }

    @Override
    public void nightAction() {
        //TODO : Let them know each other on the first night
    }
}
