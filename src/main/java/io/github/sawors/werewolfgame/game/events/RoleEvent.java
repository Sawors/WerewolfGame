package io.github.sawors.werewolfgame.game.events;

import io.github.sawors.werewolfgame.game.roles.PlayerRole;

public interface RoleEvent {
    PlayerRole getRole();
}
