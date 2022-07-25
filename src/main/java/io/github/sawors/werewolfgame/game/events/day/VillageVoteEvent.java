package io.github.sawors.werewolfgame.game.events.day;

import io.github.sawors.werewolfgame.LinkedUser;
import io.github.sawors.werewolfgame.database.UserId;
import io.github.sawors.werewolfgame.extensionsloader.WerewolfExtension;
import io.github.sawors.werewolfgame.game.GameManager;
import io.github.sawors.werewolfgame.game.GamePhase;
import io.github.sawors.werewolfgame.game.events.GenericVote;
import net.dv8tion.jda.api.entities.TextChannel;

import javax.annotation.Nullable;
import java.util.Set;

public class VillageVoteEvent extends GenericVote {
    public VillageVoteEvent(WerewolfExtension extension, Set<LinkedUser> votepool, Set<UserId> voters, @Nullable TextChannel channel) {
        //TODO : allow user to change vote time during game configuration
        super(extension, votepool, voters, channel,"",60);
    }

    @Override
    public void start(GameManager manager) {
        // TODO vote
    }
    
    @Override
    public GamePhase getPhase() {
        return GamePhase.VILLAGE_VOTE;
    }
}
