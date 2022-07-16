package com.github.sawors.werewolfgame.game;

import com.github.sawors.werewolfgame.database.UserId;

import java.util.List;

public interface WerewolfTeam {
    List<UserId> getMembers();
    void addMember(UserId player);
    void removeMember(UserId player);
    String getName();
    int getColor();
    void setAlly(WerewolfTeam ally);
    WerewolfTeam getAlly();
}
