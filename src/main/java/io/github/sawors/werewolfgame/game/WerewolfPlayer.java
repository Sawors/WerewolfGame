package io.github.sawors.werewolfgame.game;

import io.github.sawors.werewolfgame.database.UserId;
import io.github.sawors.werewolfgame.game.roles.PlayerRole;
import io.github.sawors.werewolfgame.game.roles.PrimaryRole;

import java.util.ArrayList;
import java.util.List;

public class WerewolfPlayer {
    
    
    UserId user;
    List<PlayerRole> roles = new ArrayList<>();
    PrimaryRole mainrole;
    boolean alive = true;
    boolean awake = true;
    GameManager gm;
    List<String> tags = new ArrayList<>();

    
    public WerewolfPlayer(UserId parent, GameManager manager){
        this.user = parent;
        this.gm = manager;
    }

    public WerewolfPlayer(UserId parent, GameManager manager, PrimaryRole role){
        this.user = parent;
        this.gm = manager;
        this.mainrole = role;
        this.roles.add(role);
    }
    
    public List<String> getTags(){
        return List.copyOf(tags);
    }
    
    public void removeTag(String tag){
        tags.remove(tag);
    }
    
    public void addTag(String tag){
        tags.add(tag);
    }

    @Override
    public String toString() {
        return user+":"+roles+"@"+gm.getId();
    }
    
    public UserId getUser() {
        return user;
    }
    public List<PlayerRole> getRoles() {
        return List.copyOf(roles);
    }



    public void addRole(PlayerRole role) {
        if(role instanceof PrimaryRole pr){
            this.roles.removeIf(r -> r instanceof PrimaryRole);
            this.mainrole = pr;
        }
        this.roles.add(role);
    }

    public PrimaryRole getMainRole(){
        return this.mainrole;
    }
    
    public void removeRole(PlayerRole role){
        roles.remove(role);
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
