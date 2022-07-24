package com.github.sawors.werewolfgame.game.events.day;

import com.github.sawors.werewolfgame.LinkedUser;
import com.github.sawors.werewolfgame.database.UserId;
import com.github.sawors.werewolfgame.game.GameManager;
import com.github.sawors.werewolfgame.game.GamePhase;
import com.github.sawors.werewolfgame.game.events.GenericVote;
import net.dv8tion.jda.api.entities.TextChannel;

import javax.annotation.Nullable;
import java.util.Set;

public class VillageVoteEvent extends GenericVote {
    public VillageVoteEvent(GameManager manager, Set<LinkedUser> votepool, Set<UserId> voters, @Nullable TextChannel channel) {
        //TODO : allow user to change vote time during game configuration
        super(manager, votepool, voters, channel,"",60);
    }

    @Override
    public void start() {
        // TODO vote
    }
    
    @Override
    public GamePhase getPhase() {
        return GamePhase.VILLAGE_VOTE;
    }
    
    @Override
    public void validate(boolean force, boolean wait) {

    }
}
