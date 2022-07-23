package com.github.sawors.werewolfgame.game.phases.day;

import com.github.sawors.werewolfgame.LinkedUser;
import com.github.sawors.werewolfgame.game.GameManager;
import com.github.sawors.werewolfgame.game.phases.GenericVote;
import com.github.sawors.werewolfgame.game.phases.PhaseType;

import java.util.Set;

public class MayorVote extends GenericVote {
    
    public MayorVote(GameManager manager, Set<LinkedUser> votepool) {
        super(manager, votepool);
        this.type = PhaseType.DAY;
    }
    
    @Override
    public void start() {
    
    }
}
