package com.github.sawors.werewolfgame.game;

public interface PlayerRole {
        Role getRoleType();

        int priority();

        void onDeathAction();

        void nightAction();
}
