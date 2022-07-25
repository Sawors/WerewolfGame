package io.github.sawors.werewolfgame.game.roles;

import io.github.sawors.werewolfgame.extensionsloader.WerewolfExtension;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Objects;

public abstract class PlayerRole {
        
        WerewolfExtension extension;
        String uniqueid = RandomStringUtils.randomAlphabetic(6);
        
        public PlayerRole(WerewolfExtension extension){
                this.extension = extension;
        }
        @Override
        public String toString(){
                return this.getClass().getName();
        }
        /**
         * Everything before wolves is less than 0, everything after is greater
         *
         * When not active during the night set this value to null.
         *
         * By default, "base" roles use an increment of 10 to let some space for other roles to be played
         * between without having to shift the entire role set.
        **/
        public abstract Integer priority();

        public void onDeathAction(){}

        public void nightAction(){}
        
        public WerewolfExtension getExtension(){
                return  extension;
        }
        
        @Override
        public boolean equals(Object obj){
                return obj.getClass() == this.getClass() && Objects.equals(((PlayerRole) obj).getExtension().getId(), this.getExtension().getId());
        }
        
        @Override
        public int hashCode() {
                return this.getClass().getCanonicalName().hashCode();
        }
}
