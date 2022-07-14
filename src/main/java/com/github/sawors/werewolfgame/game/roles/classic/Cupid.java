package com.github.sawors.werewolfgame.game.roles.classic;

import com.github.sawors.werewolfgame.game.FirstNightRole;
import com.github.sawors.werewolfgame.game.PlayerRole;
import com.github.sawors.werewolfgame.game.RoleType;

public class Cupid implements PlayerRole, FirstNightRole {
    @Override
    public RoleType getRoleType() {
        return RoleType.CUPID;
    }

    @Override
    public Integer priority() {
        return -30;
    }

    @Override
    public void onDeathAction() {
        // none
    }

    @Override
    public void nightAction() {
        // none
    }

    @Override
    public void doFirstNightAction() {
        //TODO : select lovers on first night
    }
}
