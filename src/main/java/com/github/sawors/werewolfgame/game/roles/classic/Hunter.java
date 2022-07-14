package com.github.sawors.werewolfgame.game.roles.classic;

import com.github.sawors.werewolfgame.game.PlayerRole;
import com.github.sawors.werewolfgame.game.Role;

public class Hunter implements PlayerRole {
    @Override
    public Role getRoleType() {
        return Role.HUNTER;
        //
    }

    @Override
    public Integer priority() {
        return null;
    }


    //TODO : Hunter action
    @Override
    public void onDeathAction() {

    }

    @Override
    public void nightAction() {
        // none
    }
}
