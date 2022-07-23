package com.github.sawors.werewolfgame.game.phases.day;

import com.github.sawors.werewolfgame.LinkedUser;
import com.github.sawors.werewolfgame.game.GameManager;
import com.github.sawors.werewolfgame.game.phases.GenericVote;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Set;

public class VoteEliminate extends GenericVote {
    public VoteEliminate(GameManager manager, Set<LinkedUser> votepool, TextChannel channel) {
        super(manager, votepool, channel,"");
    }

    @Override
    public void start() {
        // TODO vote
    }

    @Override
    public void validate() {

    }
}
