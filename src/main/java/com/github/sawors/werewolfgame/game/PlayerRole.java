package com.github.sawors.werewolfgame.game;

public abstract class PlayerRole {
        @Override
        public abstract String toString();
        /**
         * Everything before wolves is <0, everything after >0.
         *
         * When not active during the night set this value to null.
         *
         * By default, "classic" roles use an increment of 10 to let some space for other roles to be played
         * between without having to shift the entire role set.
        **/
        protected abstract Integer priority();

        protected void onDeathAction(){};

        protected void nightAction(){};
}
