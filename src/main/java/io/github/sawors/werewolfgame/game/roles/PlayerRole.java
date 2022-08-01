package io.github.sawors.werewolfgame.game.roles;

import io.github.sawors.werewolfgame.Main;
import io.github.sawors.werewolfgame.extensionsloader.WerewolfExtension;
import io.github.sawors.werewolfgame.game.GamePhase;
import io.github.sawors.werewolfgame.game.events.GameEvent;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public abstract class PlayerRole {
        
        WerewolfExtension extension;
        String rolename = getClass().getSimpleName().toLowerCase(Locale.ROOT);
        Map<GameEvent, GamePhase> events = new HashMap<>();
        Map<Integer,Map<GameEvent, GamePhase>> roundevents = new HashMap<>();
        
        public PlayerRole(WerewolfExtension extension){
                this.extension = extension;
        }
        @Override
        public String toString(){
                return getRoleName();
        }
        /**
         * Everything before wolves is less than 0, everything after is greater
         * When not active during the night set this value to null.
         * By default, "base" roles use an increment of 10 to let some space for other roles to be played
         * between without having to shift the entire role set.
        **/
        public abstract Integer priority();
        
        public WerewolfExtension getExtension(){
                return  extension;
        }
        
        public void setName(String name){
                this.rolename = name.toLowerCase(Locale.ROOT);
        }
        
        public String getRoleName(){
                return this.rolename;
        }
        
        @Override
        public boolean equals(Object obj){
                return obj.getClass() == this.getClass() && Objects.equals(((PlayerRole) obj).getExtension().getId(), this.getExtension().getId());
        }
        
        @Override
        public int hashCode() {
                return this.getClass().getCanonicalName().hashCode();
        }
        
        public Map<GameEvent, GamePhase> getEvents(){
                return events;
        };
        
        public Map<GameEvent, GamePhase> getRoundEvents(int round){
                Map<GameEvent, GamePhase> evs = roundevents.get(round);
                Main.logAdmin("evs",evs);
                Main.logAdmin("roundevents"+getClass().getSimpleName(),roundevents);
                return evs != null ? evs : Map.of();
        }
        
        public void addEvent(GameEvent event, GamePhase phase){
              events.put(event,phase);
        }
        
        public void addRoundEvent(GameEvent event, GamePhase phase, int round){
                if(roundevents.containsKey(round)){
                        roundevents.get(round).put(event,phase);
                } else {
                        roundevents.put(round,Map.of(event,phase));
                }
        }
}
