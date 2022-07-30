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
    public VillageVoteEvent(WerewolfExtension extension, @Nullable TextChannel channel) {
        //TODO : allow user to change vote time during game configuration
        super(extension, channel);
        this.votetime = 5*60;
    }

    @Override
    public void start(GameManager manager) {
        Set<LinkedUser> votepool = manager.defaultVotePool();
        votepool.removeIf(us -> manager.getPlayerRoles().get(us.getId()) == null || !manager.getPlayerRoles().get(us.getId()).isAlive());
        this.votepool = votepool;
        Set<UserId> voters = manager.getRealPlayers();
        voters.removeIf(us -> manager.getPlayerRoles().get(us) == null || !manager.getPlayerRoles().get(us).isAlive());
        this.voters = voters;
        // TODO vote
    }
    
    @Override
    public GamePhase getPhase() {
        return GamePhase.VILLAGE_VOTE;
    }
}
