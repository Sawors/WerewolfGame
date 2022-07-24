package com.github.sawors.werewolfgame.game.events.day;

import com.github.sawors.werewolfgame.LinkedUser;
import com.github.sawors.werewolfgame.database.UserId;
import com.github.sawors.werewolfgame.game.GameManager;
import com.github.sawors.werewolfgame.game.events.GenericVote;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Set;

public class VoteEliminate extends GenericVote {
    public VoteEliminate(GameManager manager, Set<LinkedUser> votepool, Set<UserId> voters, TextChannel channel) {
        super(manager, votepool, voters, channel,"");
    }

    @Override
    public void start() {
        // TODO vote
    }

    @Override
    public void validate(boolean force, boolean wait) {

    }
}