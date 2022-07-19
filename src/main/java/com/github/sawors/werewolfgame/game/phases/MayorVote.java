package com.github.sawors.werewolfgame.game.phases;

import com.github.sawors.werewolfgame.LinkedUser;
import com.github.sawors.werewolfgame.game.GameManager;

import java.util.Set;

public class MayorVote extends GenericVote{
    
    public MayorVote(GameManager manager, Set<LinkedUser> votepool) {
        super(manager, votepool);
    }
    
    @Override
    public void start() {
    
    }
}
