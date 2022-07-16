package com.github.sawors.werewolfgame.game.teams.classic;

import com.github.sawors.werewolfgame.database.UserId;
import com.github.sawors.werewolfgame.game.WerewolfTeam;

import java.util.List;

public class Village implements WerewolfTeam {
    @Override
    public List<UserId> getMembers() {
        return null;
    }
    
    @Override
    public void addMember(UserId player) {
    
    }
    
    @Override
    public void removeMember(UserId player) {
    
    }
    
    @Override
    public String getName() {
        return null;
    }
    
    @Override
    public int getColor() {
        return 0;
    }
    
    @Override
    public void setAlly(WerewolfTeam ally) {
    
    }
    
    @Override
    public WerewolfTeam getAlly() {
        return null;
    }
}
