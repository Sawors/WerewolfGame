package com.github.sawors.werewolfgame.game;

import com.github.sawors.werewolfgame.database.UserId;
import com.github.sawors.werewolfgame.game.roles.classic.Spectator;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class WerewolfPlayer {
    
    
    UserId user;
    List<PlayerRole> roles = new ArrayList<>();
    boolean alive = true;
    GameManager gm;
    
    public WerewolfPlayer(UserId parent, GameManager manager){
        this.user = parent;
        this.gm = manager;
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
        Stack<Integer> stack = new Stack<>();
        alive = true;
    }
    
    public boolean isAlive(){
        return alive;
    }
}
