package com.github.sawors.werewolfgame.game.roles.classic;

import com.github.sawors.werewolfgame.game.PlayerRole;
import com.github.sawors.werewolfgame.game.RoleType;

public class Hunter implements PlayerRole {
    @Override
    public RoleType getRoleType() {
        return RoleType.HUNTER;
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
