package com.github.sawors.werewolfgame.game.events.day;

import com.github.sawors.werewolfgame.LinkedUser;
import com.github.sawors.werewolfgame.Main;
import com.github.sawors.werewolfgame.database.UserId;
import com.github.sawors.werewolfgame.game.GameManager;
import com.github.sawors.werewolfgame.game.GamePhase;
import com.github.sawors.werewolfgame.game.events.GenericVote;
import com.github.sawors.werewolfgame.game.events.RoleEvent;
import com.github.sawors.werewolfgame.game.roles.PlayerRole;
import com.github.sawors.werewolfgame.game.roles.base.Mayor;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Map;
import java.util.Set;

public class MayorVoteEvent extends GenericVote implements RoleEvent {

    
    public MayorVoteEvent(Set<LinkedUser> votepool, Set<UserId> voters , TextChannel channel) {
        // TODO : user-defined vote time
        super(votepool, voters, channel, "Vote for the best Mayor !",30);
        Main.logAdmin("Voters",voters);
        Main.logAdmin("Votepool",votepool);
    }

    @Override
    public void onWin(UserId winner, Map<UserId, Integer> results) {
        Main.logAdmin(winner);
        manager.nextEvent();
    }

    @Override
    public void onTie(Set<UserId> tieset) {
        Main.logAdmin("Ignoring Tie",tieset);
        manager.nextEvent();
    }

    @Override
    public void onValidationFail() {

    }

    @Override
    public void onValidationSuccess(boolean forced) {

    }

    @Override
    public void start(GameManager manager) {
        votemessage.setTitle("Electing the Mayor");
        votemessage.setDescription(messagebody);
        votemessage.addField("Supplementary Role","This role does not replace your original role",false);
        votemessage.addField("Role","The mayor will decide who should die if a tie happens when the Village decides which player to eliminate. \nWhen the Mayor dies a new Mayor is designated by the old one just before passing away",false);

        start(manager,votemessage);
    }
    
    @Override
    public GamePhase getPhase() {
        return GamePhase.FIRST_DAY;
    }
    
    @Override
    public PlayerRole getRole() {
        return new Mayor();
    }
}
