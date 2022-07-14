package com.github.sawors.werewolfgame.game.roles.classic;

import com.github.sawors.werewolfgame.game.PlayerRole;
import com.github.sawors.werewolfgame.game.Role;

public class Mayor implements PlayerRole {
    @Override
    public Role getRoleType() {
        return Role.MAYOR;
    }

    @Override
    public Integer priority() {
        return null;
    }

    @Override
    public void onDeathAction() {
        //TODO : Transmit role
    }

    @Override
    public void nightAction() {
        // none
    }
}
