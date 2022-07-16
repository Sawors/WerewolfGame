package com.github.sawors.werewolfgame.game.teams.classic;

import com.github.sawors.werewolfgame.database.UserId;
import com.github.sawors.werewolfgame.game.WerewolfTeam;

import java.util.ArrayList;
import java.util.List;

public class Wolves implements WerewolfTeam {
    
    List<UserId> members = new ArrayList<>();
    String name = "Wolves";
    int color = 0x913E40;
    WerewolfTeam ally = null;
    
    // this is kind of absurd for an interface to have the same method bodies for all of its implementations (?)
    
    @Override
    public List<UserId> getMembers() {
        return List.copyOf(members);
    }
    
    @Override
    public void addMember(UserId player) {
        members.add(player);
    }
    
    @Override
    public void removeMember(UserId player) {
        members.remove(player);
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public int getColor() {
        return color;
    }
    
    @Override
    public void setAlly(WerewolfTeam ally) {
        this.ally = ally;
    }
    
    @Override
    public WerewolfTeam getAlly() {
        return ally;
    }
}
