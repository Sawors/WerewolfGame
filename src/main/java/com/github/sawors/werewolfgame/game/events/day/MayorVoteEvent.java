package com.github.sawors.werewolfgame.game.events.day;

import com.github.sawors.werewolfgame.LinkedUser;
import com.github.sawors.werewolfgame.Main;
import com.github.sawors.werewolfgame.database.UserId;
import com.github.sawors.werewolfgame.game.GameManager;
import com.github.sawors.werewolfgame.game.events.GenericVote;
import com.github.sawors.werewolfgame.game.events.PhaseType;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Map;
import java.util.Set;

public class MayorVoteEvent extends GenericVote {

    
    public MayorVoteEvent(GameManager manager, Set<LinkedUser> votepool, Set<UserId> voters , TextChannel channel) {
        // TODO : user-defined vote time
        super(manager, votepool, voters, channel, "Vote for the best Mayor !",30);
        Main.logAdmin("Voters",voters);
        Main.logAdmin("Votepool",votepool);
        this.type = PhaseType.DAY;
    }

    @Override
    public void onWin(UserId winner, Map<UserId, Integer> results) {
        Main.logAdmin(winner);
        gm.nextEvent();
    }

    @Override
    public void onTie(Set<UserId> tieset) {
        Main.logAdmin("Ignoring Tie",tieset);
        gm.nextEvent();
    }

    @Override
    public void onValidationFail() {

    }

    @Override
    public void onValidationSuccess(boolean forced) {

    }

    @Override
    public void start() {
        votemessage.setTitle("Electing the Mayor");
        votemessage.setDescription(messagebody);
        votemessage.addField("Supplementary Role","This role does not replace your original role",false);
        votemessage.addField("Role","The mayor will decide who should die if a tie happens when the Village decides which player to eliminate. \nWhen the Mayor dies a new Mayor is designated by the old one just before passing away",false);

        start(votemessage);
    }
}
