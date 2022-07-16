package com.github.sawors.werewolfgame.game;

import com.github.sawors.werewolfgame.LinkedUser;
import com.github.sawors.werewolfgame.game.roles.classic.Spectator;

import java.util.ArrayList;
import java.util.List;

public class WerewolfPlayer {
    
    
    LinkedUser user;
    List<PlayerRole> roles = new ArrayList<>();
    
    
    public LinkedUser getUser() {
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
    
}
