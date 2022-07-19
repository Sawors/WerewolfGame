package com.github.sawors.werewolfgame.game.phases;

import com.github.sawors.werewolfgame.LinkedUser;
import com.github.sawors.werewolfgame.game.GameManager;

import java.util.Set;

public abstract class GenericVote extends GamePhase{
    
    private Set<LinkedUser> votepool;
    
    public GenericVote(GameManager manager, Set<LinkedUser> votepool){
        super(manager);
        this.votepool = votepool;
    };
    
    public void setVotePool(Set<LinkedUser> votepool){
        this.votepool = votepool;
    }
}
