package com.github.sawors.werewolfgame.game.roles.classic;

import com.github.sawors.werewolfgame.game.PlayerRole;
import com.github.sawors.werewolfgame.game.Role;

public class Villager implements PlayerRole {
    @Override
    public Role getRoleType() {
        return Role.VILLAGER;
    }

    @Override
    public Integer priority() {
        return null;
    }

    @Override
    public void onDeathAction() {
        // none for simple villager
    }

    @Override
    public void nightAction() {
        // none for simple villager
    }
}
