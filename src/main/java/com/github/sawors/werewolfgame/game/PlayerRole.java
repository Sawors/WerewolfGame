package com.github.sawors.werewolfgame.game;

public interface PlayerRole {
        RoleType getRoleType();


        /**
         * Everything before wolves is <0, everything after >0.
         *
         * When not active during the night set this value to null.
         *
         * By default, "classic" roles use an increment of 10 to let some space for other roles to be played
         * between without having to shift the entire role set.
        **/
        Integer priority();

        void onDeathAction();

        void nightAction();
}
