package com.github.sawors.werewolfgame.game;

import com.github.sawors.werewolfgame.database.UserId;
import com.github.sawors.werewolfgame.game.roles.PlayerRole;
import com.github.sawors.werewolfgame.game.roles.utility.Spectator;
import com.github.sawors.werewolfgame.game.teams.classic.Village;

import java.util.ArrayList;
import java.util.List;

public class WerewolfPlayer {
    
    
    UserId user;
    List<PlayerRole> roles = new ArrayList<>();
    boolean alive = true;
    boolean awake = true;
    GameManager gm;
    WerewolfTeam team = new Village();

    
    public WerewolfPlayer(UserId parent, GameManager manager){
        this.user = parent;
        this.gm = manager;
    }

    public WerewolfPlayer(UserId parent, GameManager manager, PlayerRole role){
        this.user = parent;
        this.gm = manager;
        this.roles.add(role);
    }

    @Override
    public String toString() {
        return user+":"+roles+"@"+gm.getId();
    }

    public void setTeam(WerewolfTeam team){
        this.team = team;
    }

    public WerewolfTeam getTeam(){
        return team;
    }
    
    public UserId getUser() {
        return user;
    }
    public List<PlayerRole> getRoles() {
        return List.copyOf(roles);
    }
    
    public void addRole(PlayerRole role) {
        this.roles.add(role);
    }
    
    public void removeRole(PlayerRole role){
        roles.remove(role);
    }
    
    public void setSpectator(){
        roles.add(new Spectator());
    }
    
    public void kill(){
        alive = false;
    }
    
    public void resurrect(){
        alive = true;
    }

    public void wakeup(){
        awake = true;
    }
    public void sleep(){
        awake = false;
    }
    
    public boolean isAlive(){
        return alive;
    }
}
