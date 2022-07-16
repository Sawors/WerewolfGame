package com.github.sawors.werewolfgame.game;

import com.github.sawors.werewolfgame.database.UserId;

import java.util.ArrayList;
import java.util.List;

public abstract class WerewolfTeam {
    protected List<UserId> members = new ArrayList<>();
    protected String name;
    protected int color;
    private WerewolfTeam ally;

    // this is kind of absurd for an interface to have the same method bodies for all of its implementations (?)

    public List<UserId> getMembers() {
        return List.copyOf(members);
    }

    public void addMember(UserId player) {
        members.add(player);
    }

    public void removeMember(UserId player) {
        members.remove(player);
    }

    public String getName() {
        return name;
    }

    public int getColor() {
        return color;
    }

    public void setAlly(WerewolfTeam ally) {
        this.ally = ally;
    }

    public WerewolfTeam getAlly() {
        return ally;
    }
}
