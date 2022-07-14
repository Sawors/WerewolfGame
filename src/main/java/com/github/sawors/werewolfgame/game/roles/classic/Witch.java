package com.github.sawors.werewolfgame.game.roles.classic;

import com.github.sawors.werewolfgame.game.PlayerRole;
import com.github.sawors.werewolfgame.game.Role;

public class Witch implements PlayerRole {
    @Override
    public Role getRoleType() {
        return Role.WITCH;
    }

    @Override
    public Integer priority() {
        return 10;
    }

    @Override
    public void onDeathAction() {
        // none
    }

    //TODO : Witch action
    @Override
    public void nightAction() {

    }
}
